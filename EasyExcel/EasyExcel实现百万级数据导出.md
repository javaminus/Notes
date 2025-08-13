# 百万级数据的Excel导出实现方案

两种方案：

- 都是异步导出，让用户等待数据导出，使用多线程生成多个sheet；

- 使用长城云服务器，数据导出生成url地址（设置url过期时间1小时），用户直接去下载；

- > # 两种百万级Excel导出方案对比
  >
  > | 对比维度     | 应用服务器流式导出方案            | 云存储对象服务导出方案                    |
  > | ------------ | --------------------------------- | ----------------------------------------- |
  > | **基本原理** | 通过应用服务器生成Excel并提供下载 | 生成Excel后上传至云存储，提供临时下载链接 |
  >
  > ## 一、服务器资源使用
  >
  > ### 应用服务器方案
  > ✅ **优点**：无需第三方存储服务，架构简单  
  > ❌ **缺点**：占用应用服务器CPU、内存、磁盘和网络带宽，影响其他业务
  >
  > ### 云存储方案
  > ✅ **优点**：导出完成后资源立即释放，不占用应用服务器带宽  
  > ✅ **优点**：可以利用云服务商的CDN加速下载  
  > ❌ **缺点**：临时文件需上传至云端，增加额外网络传输
  >
  > ## 二、扩展性和并发处理
  >
  > ### 应用服务器方案
  > ✅ **优点**：流程可控，易于实现和调试  
  > ❌ **缺点**：并发导出任务会竞争服务器资源，难以同时处理多个大文件导出
  >
  > ### 云存储方案
  > ✅ **优点**：文件下载由云存储服务承担，支持更高并发  
  > ✅ **优点**：可以同时处理多个大文件导出任务  
  > ❌ **缺点**：依赖云服务提供商的可用性和稳定性
  >
  > ## 三、用户体验
  >
  > ### 应用服务器方案
  > ✅ **优点**：流程简单，用户一次操作完成下载  
  > ❌ **缺点**：大文件下载慢，网络波动可能导致下载中断  
  > ❌ **缺点**：长时间连接可能超时，导致下载失败
  >
  > ### 云存储方案
  > ✅ **优点**：下载速度更快、更稳定（云存储优化）  
  > ✅ **优点**：支持断点续传（取决于云服务商）  
  > ❌ **缺点**：用户需要点击额外链接进行下载
  >
  > ## 四、成本因素
  >
  > ### 应用服务器方案
  > ✅ **优点**：无需额外的云存储费用  
  > ❌ **缺点**：可能需要更高配置的服务器支撑大文件导出
  >
  > ### 云存储方案
  > ✅ **优点**：可以使用低配服务器，降低服务器成本  
  > ✅ **优点**：按实际使用量付费，资源使用更高效  
  > ❌ **缺点**：需支付云存储和数据传输费用
  >
  > ## 五、安全性考量
  >
  > ### 应用服务器方案
  > ✅ **优点**：数据全程在自有系统内处理  
  > ❌ **缺点**：长时间占用连接，可能被中间人攻击
  >
  > ### 云存储方案
  > ✅ **优点**：预签名URL带时效性，过期自动失效  
  > ✅ **优点**：可设置IP限制、访问权限等安全策略  
  > ❌ **缺点**：数据存储在第三方，需评估云服务商安全合规性
  >
  > ## 六、实施难度
  >
  > ### 应用服务器方案
  > ✅ **优点**：开发难度较低，无需集成第三方服务  
  > ❌ **缺点**：需自行处理文件存储和清理、超时处理等问题
  >
  > ### 云存储方案
  > ✅ **优点**：文件管理更规范，可利用云服务的生命周期管理  
  > ❌ **缺点**：需要额外的集成开发和异常处理机制
  >
  > ## 七、适用场景
  >
  > ### 应用服务器方案适合：
  > - 数据量不太大（<50万行）
  > - 并发导出需求少
  > - 简单应用或内部系统
  > - 不希望依赖第三方服务
  >
  > ### 云存储方案适合：
  > - 数据量非常大（>100万行）
  > - 高并发导出需求
  > - 面向大量外部用户的系统
  > - 对下载速度和稳定性要求高
  >
  > ## 总结建议
  >
  > 1. **小型应用**：选择应用服务器方案，实现简单、成本低
  > 2. **大型系统**：选择云存储方案，性能更好、扩展性强
  > 3. **折中方案**：可结合两者优点，如：
  >    - 小数据量时直接通过应用服务器导出
  >    - 大数据量时自动切换到云存储导出模式
  >    - 利用云存储作为备份，但主要通过应用服务器提供下载
  >
  > 根据您的具体业务场景、用户规模和预算情况进行选择，两种方案可以结合使用以获得最佳效果。
  >
  > # 云存储方案主要减轻了"文件下载"步骤的压力
  >
  > 在百万级数据导出过程中，整个流程包括：
  >
  > 1. **数据查询**：从数据库检索百万行数据
  > 2. **文件生成**：将数据处理并写入Excel文件
  > 3. **文件存储**：将生成的文件临时保存
  > 4. **文件下载**：将文件传输给最终用户
  >
  > ## 云存储方案最主要解决的是第4步：文件下载环节
  >
  > ### 为什么下载环节是性能瓶颈？
  >
  > 在传统应用服务器方案中：
  > - 应用服务器需要**维持HTTP连接**直到整个文件传输完成
  > - 百万级Excel文件通常很大(50MB~200MB)，下载可能需要数分钟
  > - 每个下载连接会占用一个服务器线程/连接池资源
  > - 并发下载会显著消耗服务器带宽
  >
  > ### 云存储如何减轻这一压力：
  >
  > 1. **连接转移**：将长时间的下载连接从应用服务器转移到专业的云存储服务
  > 2. **资源释放**：一旦文件上传到云存储并生成URL，应用服务器立即释放资源
  > 3. **带宽优化**：云存储服务通常有更优化的带宽和全球CDN网络
  > 4. **并发处理**：云存储服务专为高并发文件下载设计，可同时服务更多用户
  >
  > ### 具体效益对比：
  >
  > | 应用服务器直接下载                          | 云存储下载                                       |
  > | ------------------------------------------- | ------------------------------------------------ |
  > | 一个50MB文件下载需持续占用服务器连接2-3分钟 | 服务器只需几秒上传文件到云存储，然后立即释放资源 |
  > | 10个并发下载可能占用服务器500MB带宽         | 10个并发下载对服务器带宽零影响                   |
  > | 下载中断需重新开始，消耗更多资源            | 支持断点续传，减少资源浪费                       |
  > | 服务器处理业务逻辑能力下降                  | 服务器可专注于核心业务逻辑                       |
  >
  > 通过云存储方案，您的应用服务器可以专注于业务处理，而将大文件传输这种资源密集型工作交给专门的云存储服务处理，从而显著提升系统整体性能和用户体验。

与导入类似，百万级数据的Excel导出同样面临内存溢出、性能和错误处理等挑战。以下是解决方案：

## 一、面临的主要问题

- **内存溢出**：传统方式一次性生成大Excel会占用大量内存
- **性能问题**：百万级数据导出耗时长，影响用户体验
- **浏览器超时**：普通Web请求可能超时
- **数据一致性**：长时间导出过程中数据可能发生变化

## 二、整体解决方案

### 1. 流式处理方案（数据库分页查询）

借助EasyExcel的流式写入功能，避免一次性将所有数据加载到内存：

```java
@Service
public class ExcelExportService {
    
    @Autowired
    private MyDataRepository myDataRepository;
    
    public void exportData(OutputStream outputStream) {
        // 创建ExcelWriter对象
        try (ExcelWriter excelWriter = EasyExcel.write(outputStream).build()) {
            // 创建sheet
            WriteSheet writeSheet = EasyExcel.writerSheet("数据表").build();
            
            // 每次查询的数据量
            final int BATCH_SIZE = 5000;
            int pageIndex = 0;
            
            // 分批查询数据并写入Excel
            while (true) {
                // 分页查询数据
                List<MyDataModel> dataList = myDataRepository.findByPage(pageIndex, BATCH_SIZE);
                if (dataList.isEmpty()) {
                    break;
                }
                
                // 写入当前批次数据
                excelWriter.write(dataList, writeSheet);
                
                // 清理资源
                dataList.clear();
                pageIndex++;
            }
            // ExcelWriter会在try-with-resources中自动关闭
        }
    }
}
```

### 2. 多Sheet分割方案

对于百万级数据，可以按照业务分类或数据批次拆分到多个Sheet：

```java
public void exportMultiSheetData(OutputStream outputStream) {
    try (ExcelWriter excelWriter = EasyExcel.write(outputStream).build()) {
        // 假设按照车间对数据进行分组
        List<String> departments = myDataRepository.getAllDepartments();
        
        int sheetNo = 0;
        for (String department : departments) {
            // 为每个部门创建一个sheet
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetNo, department + "数据").build();
            sheetNo++;
            
            // 分页查询该部门数据
            int pageIndex = 0;
            final int BATCH_SIZE = 5000;
            
            while (true) {
                List<MyDataModel> dataList = myDataRepository.findByDepartmentAndPage(department, pageIndex, BATCH_SIZE);
                if (dataList.isEmpty()) {
                    break;
                }
                
                // 写入数据
                excelWriter.write(dataList, writeSheet);
                dataList.clear();
                pageIndex++;
            }
        }
    }
}
```

### 3. 异步导出 + 文件下载方案

对于Web应用，可以采用异步处理 + 文件下载的方式：

```java
@RestController
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ExportService exportService;
    
    @GetMapping("/start")
    public ResponseEntity<Map<String, Object>> startExport() {
        // 生成任务ID
        String taskId = UUID.randomUUID().toString();
        
        // 启动异步导出任务
        CompletableFuture.runAsync(() -> {
            exportService.exportToFile(taskId);
        });
        
        // 返回任务ID给前端
        Map<String, Object> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("status", "processing");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{taskId}")
    public ResponseEntity<Map<String, Object>> checkStatus(@PathVariable String taskId) {
        // 查询任务状态
        ExportStatus status = exportService.getExportStatus(taskId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("status", status.getStatus());
        response.put("progress", status.getProgress());
        
        if ("completed".equals(status.getStatus())) {
            response.put("downloadUrl", "/export/download/" + taskId);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/download/{taskId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String taskId) {
        try {
            // 获取导出的文件
            File file = exportService.getExportFile(taskId);
            
            // 转换为Resource
            Resource resource = new FileSystemResource(file);
            
            // 设置文件下载响应头
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"export_" + taskId + ".xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(resource);
                
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
```

### 4. 使用多线程并行导出

```java
@Service
public class MultiThreadExportService {

    @Autowired
    private DataRepository dataRepository;
    
    public void exportWithMultiThread(String exportPath) throws Exception {
        // 获取总记录数
        long totalCount = dataRepository.count();
        // 每个线程处理的数据量
        long perThreadCount = 50000;
        // 计算所需线程数
        int threadCount = (int) Math.ceil((double) totalCount / perThreadCount);
        
        // 创建自定义线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            Math.min(threadCount, 10),  // 核心线程数
            Math.min(threadCount, 20),  // 最大线程数
            60L, TimeUnit.SECONDS,      // 空闲线程存活时间
            new LinkedBlockingQueue<>(),  // 工作队列
            new ThreadFactoryBuilder().setNameFormat("excel-export-%d").build()  // 线程工厂
        );
        
        // 最终合并的文件
        File finalFile = new File(exportPath);
        
        // 用于存储每个线程的临时文件
        List<File> tempFiles = new ArrayList<>();
        List<Future<?>> futures = new ArrayList<>();
        
        // 启动多线程导出
        for (int i = 0; i < threadCount; i++) {
            final int threadIdx = i;
            final long startPos = threadIdx * perThreadCount;
            final long endPos = Math.min((threadIdx + 1) * perThreadCount, totalCount);
            
            // 每个线程的临时文件
            File tempFile = File.createTempFile("export_temp_" + threadIdx + "_", ".xlsx");
            tempFiles.add(tempFile);
            
            // 提交任务
            Future<?> future = executor.submit(() -> {
                try (OutputStream os = new FileOutputStream(tempFile)) {
                    // 分页查询数据
                    List<MyData> dataList = dataRepository.findByRange(startPos, endPos);
                    // 写入临时文件
                    EasyExcel.write(os, MyData.class).sheet("数据_" + threadIdx).doWrite(dataList);
                } catch (Exception e) {
                    throw new RuntimeException("导出线程" + threadIdx + "异常", e);
                }
            });
            
            futures.add(future);
        }
        
        // 等待所有任务完成
        for (Future<?> future : futures) {
            future.get();
        }
        
        // 关闭线程池
        executor.shutdown();
        
        // 合并所有临时文件
        mergeExcelFiles(tempFiles, finalFile);
        
        // 清理临时文件
        for (File tempFile : tempFiles) {
            tempFile.delete();
        }
    }
    
    // 合并Excel文件的方法
    private void mergeExcelFiles(List<File> sourceFiles, File targetFile) {
        // 实现合并逻辑...
    }
}
```

## 三、前端实现配合

对于大数据量导出，前端实现也需要做相应调整：

```javascript
// 前端请求示例
function exportExcel() {
    // 显示进度提示
    showLoadingMessage('正在准备导出数据...');
    
    // 步骤1：启动导出任务
    fetch('/export/start')
        .then(response => response.json())
        .then(data => {
            const taskId = data.taskId;
            
            // 步骤2：定时检查任务状态
            const checkStatusInterval = setInterval(() => {
                fetch(`/export/status/${taskId}`)
                    .then(response => response.json())
                    .then(statusData => {
                        // 更新进度
                        updateProgressBar(statusData.progress);
                        
                        if (statusData.status === 'completed') {
                            // 停止轮询
                            clearInterval(checkStatusInterval);
                            
                            // 步骤3：下载文件
                            window.location.href = statusData.downloadUrl;
                            
                            showSuccessMessage('导出完成，正在下载...');
                        } else if (statusData.status === 'failed') {
                            clearInterval(checkStatusInterval);
                            showErrorMessage('导出失败，请重试');
                        }
                    });
            }, 2000); // 每2秒检查一次状态
        })
        .catch(error => {
            showErrorMessage('启动导出任务失败：' + error.message);
        });
}
```

## 四、优化建议

1. **数据预处理**：导出前进行必要的聚合计算，减少导出数据量
2. **分区导出**：按业务维度（时间、部门等）拆分数据，允许用户选择性导出
3. **压缩文件**：对最终的Excel文件进行压缩，减少网络传输量
4. **权限控制**：对大数据量导出功能设置权限，避免滥用
5. **监控与限流**：对导出任务进行监控，设置合理的并发限制
6. **缓存策略**：导出结果缓存一定时间，相同条件的导出直接返回已有结果

通过以上方案，可以高效、稳定地实现百万级数据的Excel导出功能，同时保证系统的稳定性和用户体验。