## 可能遇到的问题：

- 内存问题：百万级数据量的Excel文件会非常大，都加载到内存中可能会导致**内存溢出**。 
- 性能问题：百万级数据从Excel读取并插入到数据，可能会很**慢**，需要考虑性能问题。 
- 在文件的读取及导入过程中，可能会遇到各种各样的问题（**读入失败**），我们需要妥善的处理好这些问题 。



### 内存溢出问题

百万级数据量，一次性都读取到内存中，肯定是不现实的，那么好的办法就是基于流式读取的方式进行分批处理。  在技术选型上，我们选择使用EasyExcel，他特别针对大数据量和复杂Excel文件的处理进行了优化。在解析Excel时EasyExcel不会将Excel一次性全部加载到内存中，而是从磁盘上一行行读取数据，逐个解析。 

### 性能问题

百万级数据的处理，如果用单线程的话肯定是很慢的，想要提升性能，那么就需要使用**多线程**。  多线程的使用上涉及到两个场景，一个是用多线程进行文件的**读取**，另一个是用多线程实现数据的**插入**。这里就涉及到一个**生产者-消费者**的模式了，多个线程读取，然后多个线程插入，这样可以最大限度的提升整体的性能。  而数据的插入，我们除了借助多线程之外，还可以同时使用数据库的**批量插入**的功能，这样就能更加的提升插入速度。 

###错误处理  

在文件的读取和数据库写入过程中，会需要解决各种各样的问题，比如数据格式错误、数据不一致、有重复数据等。  

所以我们需要分两步来：第一步就是先进行数据的检查，在开始插入之前就把数据的格式等问题提前检查好，然后在插入过程中，对异常进行处理。  处理方式有很多种，可以进行事务回滚、可以进行日志记录。这个根据实际情况，一般来说不建议做回滚，直接做自动重试，重试几次之后还是不行的话，再记录日志然后后续在重新插入即可。  并且在这个过程中，需要考虑一下数据重复的问题，需要在excel中某几个字段设置成数据库唯一性约束，然后在遇到数据冲突的时候，进行处理，处理方式可以是覆盖、跳过以及报错。这个根据实际业务情况来，一般来说**跳过+打印日志**是相对合理的。 

### 所以，整体方案就是：

借助EasyExcel来实现Excel的读取，因为他并不会一次性把整个Excel都加载到内存中，而是逐行读取的。为了提升并发性能，我们再进一步将百万级数据分散到不同的sheet中，然后借助线程池，**多线程同时读取不同的sheet**，在读取过程中，借助EasyExcel的ReadListener做数据处理。  

在处理过程中，我们并不会每一条数据都操作数据库，这样对数据库来说压力太大了，我们会设定一个批次，比如1000条，我们会把从Excel中读取到的数据暂存在内存中，这里可以使用List实现，当读取了1000条之后，就执行一次数据的批量插入，批量插入可以借助mybatis就能简单的实现了。  

而这个过程中，还需要考虑一些并发的问题，所以我们在处理过程中会使用线程安全的队列来保存暂存在内存中的数据，如ConcurrentLinkedQueue  经过验证，如此实现之后，读取一个100万数据的Excel并插入数据，耗时在100秒左右，不超过2分钟。 

### 具体实现

为了提升并发处理的能力，我们把百万级数据放到同一个excel的不同的sheet中，然后通过使用EasyExcel并发的读取这些sheet。  EasyExcel提供了ReadListener接口，允许在读取每一批数据后进行自定义处理。我们可以基于他的这个功能来实现文件的分批读取。 

#### 并发读取多个sheet

```java
@Service
public class ExcelImporterService {

    @Autowired
    private MyDataService myDataService;
    
    public void doImport() {
        // Excel文件的路径
        String filePath = "/excel/test.xlsx";

        // 需要读取的sheet数量
        int numberOfSheets = 20;

        // 创建一个固定大小的线程池，大小与sheet数量相同 (禁止用下面的方式创建线程池，一定要自定义线程池)
        ExecutorService executor = Executors.newFixedThreadPool(numberOfSheets);

        // 遍历所有sheets
        for (int sheetNo = 0; sheetNo < numberOfSheets; sheetNo++) {
            // 在Java lambda表达式中使用的变量需要是final
            int finalSheetNo = sheetNo;

            // 向线程池提交一个任务
            executor.submit(() -> {
                // 使用EasyExcel读取指定的sheet
                EasyExcel.read(filePath, MyDataModel.class, new MyDataModelListener(myDataService))
                         .sheet(finalSheetNo) // 指定sheet号
                         .doRead(); // 开始读取操作
            });
        }

        // 启动线程池的关闭序列
		executor.shutdown();

        // 等待所有任务完成，或者在等待超时前被中断
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // 如果等待过程中线程被中断，打印异常信息
            e.printStackTrace();
        }
    }
}
```



#### ReadListener

真实项目代码：

```java
package com.changcheng.listener.easyExcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.changcheng.domain.CPKValue;
import com.changcheng.service.ICPKValueService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CPKValueExcelListener extends AnalysisEventListener<Map<Integer, String>> {
    private ICPKValueService icpkValueService;
    private static final int BATCH_COUNT = 1000;
    private List<CPKValue> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    private Map<Integer, String> headers = new HashMap<>(); // 存储表头

    public CPKValueExcelListener(ICPKValueService icpkValueService) {
        this.icpkValueService = icpkValueService;
    }
    // 处理表头
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("解析到一条头数据: {}", JSON.toJSONString(headMap));
        // 将表头数据转换为字符串并存储到 headers
        for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
            headers.put(entry.getKey(), entry.getValue().getStringValue());
        }
        log.info("存储的表头: {}", JSON.toJSONString(headers));
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        // log.info("解析到一条数据: {}", JSON.toJSONString(data));
        for (int i = 3; i < headers.size(); i++) {
            if (data.get(i) == null) {
                continue;
            }
            try {
                String workshop = data.get(0);
                if (!workshop.contains("车间")) {
                    workshop = data.get(0) + "车间";
                }
                cachedDataList.add(new CPKValue(null,workshop, data.get(1), null, data.get(2), headers.get(i), Double.parseDouble(data.get(i)), LocalDateTime.now()));
            }catch (NumberFormatException e) { // 遇到不能解析的字段直接跳过
            }
        }
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("所有数据解析完成！");
    }

    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        icpkValueService.saveBatch(cachedDataList);
        // 这里可以将 cachedDataList 中的数据保存到数据库
        log.info("存储数据库成功！");
    }
}
```

