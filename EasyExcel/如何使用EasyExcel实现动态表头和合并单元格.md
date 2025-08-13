# 使用EasyExcel实现动态表头和合并单元格：结合Spring Boot + MyBatis Plus

## 1. 环境准备

首先在Spring Boot项目中添加相关依赖：

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.3.2</version>
</dependency>

<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3</version>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 2. 动态表头实现

### 2.1 基本原理

EasyExcel实现动态表头的核心是使用`List<List<String>>`数据结构，每个内部List代表一行表头，外部List包含所有行。源码中，`com.alibaba.excel.write.metadata.holder.WriteSheetHolder`类负责管理这些表头信息。

### 2.2 实现示例

| 学生信息 | 成绩信息 | 成绩信息 | 学期信息 |
| -------- | -------- | -------- | -------- |
| 姓名     | 科目     | 分数     | 学期     |
| 张三     | 数学     | 90       | 2023春季 |
| 张三     | 英语     | 85       | 2023春季 |
| 李四     | 数学     | 88       | 2023春季 |
| ...      | ...      | ...      | ...      |

先创建实体类和VO类：

```java
// 数据库实体
@Data
@TableName("student_score")
public class StudentScore {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentName;
    private String subject;
    private Integer score;
    private String semester;
}

// 动态导出VO
@Data
public class ScoreExportVO {
    private String studentName;
    private String subject;
    private Integer score;
    private String semester;
}
```

创建Service和导出逻辑：

```java
@Service
public class ExcelExportService {
    @Autowired
    private StudentScoreMapper studentScoreMapper;

    /**
     * 导出成绩数据
     */
    public void exportScoreData(HttpServletResponse response, String semester) throws IOException {
        // 1. 查询数据
        LambdaQueryWrapper<StudentScore> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(semester)) {
            queryWrapper.eq(StudentScore::getSemester, semester);
        }
        List<StudentScore> scoreList = studentScoreMapper.selectList(queryWrapper);
        List<ScoreExportVO> exportData = convertToExportVO(scoreList);

        // 2. 构建动态表头
        List<List<String>> headList = buildDynamicHead(semester);

        // 3. 转换为写入格式
        List<List<Object>> dataList = new ArrayList<>();
        for (ScoreExportVO vo : exportData) {
            List<Object> rowData = new ArrayList<>();
            rowData.add(vo.getStudentName());
            rowData.add(vo.getSubject());
            rowData.add(vo.getScore());
            rowData.add(vo.getSemester());
            dataList.add(rowData);
        }

        // 4. 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生成绩表", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 5. 注册合并单元格处理器
        MergeCellHandler mergeCellHandler = new MergeCellHandler(exportData);

        // 6. 写入Excel
        EasyExcel.write(response.getOutputStream())
                .head(headList)
                .registerWriteHandler(mergeCellHandler)
                .sheet("学生成绩")
                .doWrite(dataList);
    }

    /**
     * 构建动态表头
     */
    private List<List<String>> buildDynamicHead(String semester) {
        List<List<String>> headList = new ArrayList<>();
        
        // 第一列：学生信息 - 姓名
        headList.add(Arrays.asList("学生信息", "姓名"));
        
        // 第二列：成绩信息 - 科目
        headList.add(Arrays.asList("成绩信息", "科目"));
        
        // 第三列：成绩信息 - 分数
        headList.add(Arrays.asList("成绩信息", "分数"));
        
        // 第四列：学期信息 - 学期
        headList.add(Arrays.asList("学期信息", "学期"));
        
        return headList;
    }

    // 数据转换方法
    private List<ScoreExportVO> convertToExportVO(List<StudentScore> scoreList) {
        return scoreList.stream()
                .map(score -> {
                    ScoreExportVO vo = new ScoreExportVO();
                    BeanUtils.copyProperties(score, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }
}
```

## 3. 合并单元格实现

### 3.1 源码分析

EasyExcel合并单元格的原理是通过实现`CellWriteHandler`接口自定义单元格处理逻辑。在源码中，`com.alibaba.excel.write.handler.AbstractCellWriteHandler`是一个抽象类，它实现了`CellWriteHandler`接口，我们只需要继承这个抽象类并重写相关方法即可。

关键方法是`afterSheetCreate`，它在Sheet创建后被调用，我们可以在这里使用POI的API来合并单元格：

```java
public class MergeCellHandler extends AbstractCellWriteHandler {
    private List<ScoreExportVO> data;
    
    public MergeCellHandler(List<ScoreExportVO> data) {
        this.data = data;
    }
    
    @Override
    public void afterSheetCreate(WriteSheetHolder writeSheetHolder, WriteWorkbookHolder writeWorkbookHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        
        // 开始行(含表头，从0开始)
        int firstRowIndex = 0;
        // 数据起始行(从表头下一行开始，表头占2行，因此数据从第2行开始，索引为2)
        int dataStartRow = 2;
        
        // 学生姓名列 - 相同学生姓名合并
        mergeByColumn(sheet, data, dataStartRow, 0);
        
        // 合并表头
        // 学生信息跨1行2列
        sheet.addMergedRegion(new CellRangeAddress(firstRowIndex, firstRowIndex, 0, 0));
        // 成绩信息跨1行2列
        sheet.addMergedRegion(new CellRangeAddress(firstRowIndex, firstRowIndex, 1, 2));
        // 学期信息跨1行1列
        sheet.addMergedRegion(new CellRangeAddress(firstRowIndex, firstRowIndex, 3, 3));
    }
    
    /**
     * 根据指定列合并单元格
     */
    private void mergeByColumn(Sheet sheet, List<ScoreExportVO> data, int startRow, int columnIndex) {
        if (data == null || data.isEmpty()) {
            return;
        }
        
        int rowCount = data.size();
        String lastValue = null;
        int lastRowIndex = startRow;
        
        for (int i = 0; i < rowCount; i++) {
            ScoreExportVO current = data.get(i);
            String currentValue = current.getStudentName();
            
            if (lastValue == null) {
                lastValue = currentValue;
            } else {
                if (!lastValue.equals(currentValue)) {
                    // 不相等时，合并上一组数据
                    if (i - 1 > lastRowIndex) {
                        sheet.addMergedRegion(new CellRangeAddress(lastRowIndex, i - 1, columnIndex, columnIndex));
                    }
                    lastRowIndex = i + startRow;
                    lastValue = currentValue;
                }
            }
            
            // 处理最后一组数据
            if (i == rowCount - 1 && i > lastRowIndex - startRow) {
                sheet.addMergedRegion(new CellRangeAddress(lastRowIndex, i + startRow, columnIndex, columnIndex));
            }
        }
    }
}
```

## 4. 读取带有合并单元格的Excel

EasyExcel读取合并单元格时，默认情况下只会读取到合并区域的第一个单元格数据，**其他被合并的单元格会为空**。要处理这种情况，我们需要自定义监听器。

### 4.1 合并单元格读取处理

```java
@Slf4j
public class MergeDataListener extends AnalysisEventListener<Map<Integer, String>> {
    // 记录合并单元格信息
    private Map<Integer, CellRangeAddress> mergeMap = new HashMap<>();
    // 暂存上一行的数据
    private Map<Integer, String> lastRowData = new HashMap<>();
    // 最终处理后的数据
    private List<Map<Integer, String>> resultList = new ArrayList<>();
    
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到表头数据: {}", headMap);
        
        // 获取合并单元格信息
        Sheet sheet = context.readWorkbookHolder().getReadSheet().getPoiSheet();
        int numMergedRegions = sheet.getNumMergedRegions();
        for (int i = 0; i < numMergedRegions; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstRow = range.getFirstRow();
            int firstColumn = range.getFirstColumn();
            // 将合并单元格信息保存到map中
            mergeMap.put(firstColumn, range);
        }
    }
    
    @Override
    public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
        log.info("解析到数据: {}", rowData);
        
        // 处理合并单元格的数据
        Map<Integer, String> processedData = new HashMap<>(rowData);
        int rowIndex = context.readRowHolder().getRowIndex();
        
        // 检查每一列是否位于合并区域内
        for (Map.Entry<Integer, CellRangeAddress> entry : mergeMap.entrySet()) {
            int columnIndex = entry.getKey();
            CellRangeAddress range = entry.getValue();
            
            // 如果当前行在合并区域内，但不是第一行，则取值自第一行
            if (rowIndex > range.getFirstRow() && rowIndex <= range.getLastRow() 
                    && processedData.get(columnIndex) == null) {
                // 从上一行或缓存中获取值
                processedData.put(columnIndex, lastRowData.get(columnIndex));
            }
        }
        
        // 保存处理后的数据
        resultList.add(processedData);
        // 更新上一行数据
        lastRowData = processedData;
    }
    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成，共 {} 条", resultList.size());
    }
    
    public List<Map<Integer, String>> getResultList() {
        return resultList;
    }
}
```

### 4.2 读取实现

```java
@Service
public class ExcelImportService {
    @Autowired
    private StudentScoreMapper studentScoreMapper;
    
    /**
     * 导入带有合并单元格的Excel
     */
    public List<StudentScore> importScoreData(MultipartFile file) throws IOException {
        // 1. 读取Excel
        MergeDataListener listener = new MergeDataListener();
        EasyExcel.read(file.getInputStream())
                .registerReadListener(listener)
                .sheet()
                .doRead();
        
        // 2. 获取处理后的数据
        List<Map<Integer, String>> resultList = listener.getResultList();
        
        // 3. 转换为实体对象
        List<StudentScore> scoreList = new ArrayList<>();
        for (Map<Integer, String> rowData : resultList) {
            StudentScore score = new StudentScore();
            score.setStudentName(rowData.get(0));
            score.setSubject(rowData.get(1));
            score.setScore(Integer.parseInt(rowData.getOrDefault(2, "0")));
            score.setSemester(rowData.get(3));
            scoreList.add(score);
        }
        
        // 4. 批量保存到数据库
        if (!scoreList.isEmpty()) {
            studentScoreMapper.insertBatchSomeColumn(scoreList);
        }
        
        return scoreList;
    }
}
```

## 5. Controller实现

```java
@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    @Autowired
    private ExcelExportService excelExportService;
    
    @Autowired
    private ExcelImportService excelImportService;
    
    /**
     * 导出Excel
     */
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response, 
                           @RequestParam(required = false) String semester) throws IOException {
        excelExportService.exportScoreData(response, semester);
    }
    
    /**
     * 导入Excel
     */
    @PostMapping("/import")
    public R<List<StudentScore>> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return R.fail("请选择要上传的文件");
        }
        List<StudentScore> importData = excelImportService.importScoreData(file);
        return R.ok(importData);
    }
}
```

## 6. 源码深度分析

### 6.1 动态表头源码解析

EasyExcel 在 `com.alibaba.excel.write.metadata.holder.WriteSheetHolder` 类中管理表头信息，核心逻辑位于 `com.alibaba.excel.write.executor.ExcelWriteAddHeaderExecutor` 类：

```java
// ExcelWriteAddHeaderExecutor.java (简化版)
public void addHeaderData(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder) {
    // 获取表头信息
    List<List<String>> head = writeSheetHolder.excelWriteHeadProperty().getHead();
    
    // 计算表头行数
    int relativeRowIndex = writeSheetHolder.getRelativeRowIndex();
    int headRowNumber = relativeRowIndex;
    
    // 生成每行表头
    for (int i = 0; i < head.size(); i++) {
        int columnIndex = i;
        List<String> headColumnList = head.get(i);
        
        // 写入表头数据
        for (int j = 0; j < headColumnList.size(); j++) {
            int rowIndex = headRowNumber + j;
            Row row = writeSheetHolder.getSheet().getRow(rowIndex);
            if (row == null) {
                row = writeSheetHolder.getSheet().createRow(rowIndex);
            }
            Cell cell = row.createCell(columnIndex);
            cell.setCellValue(headColumnList.get(j));
        }
    }
}
```

### 6.2 合并单元格源码解析

合并单元格的核心在于 `com.alibaba.excel.write.handler.CellWriteHandler` 接口和 `AbstractCellWriteHandler` 抽象类。

EasyExcel在写入过程中会调用注册的所有处理器，关键源码在 `com.alibaba.excel.write.executor.AbstractExcelWriteExecutor`：

```java
// AbstractExcelWriteExecutor.java (简化版)
protected void afterSheetCreate() {
    if (writeContext.writeWorkbookHolder().getWriteHandlerMap().containsKey(CellWriteHandler.class)) {
        for (CellWriteHandler cellWriteHandler : writeContext.writeWorkbookHolder().getWriteHandlerMap()
                .get(CellWriteHandler.class)) {
            // 触发afterSheetCreate方法
            cellWriteHandler.afterSheetCreate(writeContext.writeSheetHolder(), writeContext.writeWorkbookHolder());
        }
    }
}
```

当我们自定义 `MergeCellHandler` 并重写 `afterSheetCreate` 方法后，EasyExcel会在创建Sheet后调用我们的合并单元格逻辑。

## 7. 小结与最佳实践

1. **动态表头**：通过 `List<List<String>>` 构建多级表头，每个内部List代表一列的表头层级。

2. **合并单元格**：
   - 写入：继承 `AbstractCellWriteHandler` 并重写 `afterSheetCreate` 方法
   - 读取：自定义 `AnalysisEventListener` 记录和处理合并单元格数据

3. **最佳实践**：
   - 批量处理：使用批量导入导出减轻数据库压力
   - 缓存利用：对于大数据量，考虑使用临时表或缓存
   - 异步处理：对于耗时长的导入导出操作，考虑使用异步任务

4. **性能优化**：
   - 使用 EasyExcel 的分页读写功能处理大文件
   - 避免一次性加载全部数据到内存
   - 合并单元格操作是CPU密集型的，大量合并会影响性能

在Spring Boot与MyBatis Plus的组合中，EasyExcel提供了高效、灵活的Excel处理能力，能够很好地满足各种复杂的业务需求。

# EasyExcel实现动态表头和合并单元格的原理与实现思路

在面试中，如果被问到如何使用EasyExcel实现动态表头和合并单元格，可以从以下几个方面进行阐述：

## 动态表头实现原理

EasyExcel实现动态表头的核心是基于表头的数据结构设计。与固定表头使用注解的方式不同，动态表头需要手动构建表头数据模型。具体来说，EasyExcel使用`List<List<String>>`的嵌套集合结构来表示动态表头，其中：

- 外层List代表所有的列
- 内层List代表每一列的多级表头内容

这种数据结构设计非常灵活，可以实现任意级别的表头嵌套。当我们需要根据不同条件动态生成表头时（比如根据用户选择的字段、权限控制显示的列、或者根据业务规则动态决定表头内容），只需要动态构建这个嵌套List结构即可。

在底层实现上，EasyExcel通过`WriteSheetHolder`类管理表头信息，并在`ExcelWriteAddHeaderExecutor`类中处理表头写入逻辑。系统会遍历表头集合，并根据内层List的大小确定表头的行数，然后依次写入单元格。	

## 合并单元格实现原理

EasyExcel实现合并单元格主要依靠自定义处理器机制。核心思路是实现`CellWriteHandler`接口（通常通过继承`AbstractCellWriteHandler`抽象类），并重写`afterSheetCreate`方法。在这个方法中，我们可以使用POI原生的API来进行单元格合并操作。

具体步骤是通过`CellRangeAddress`类指定需要合并的单元格范围（起始行、结束行、起始列、结束列），然后调用`sheet.addMergedRegion()`方法将这些单元格合并。EasyExcel提供了注册处理器的机制，通过`registerWriteHandler`方法将自定义的合并单元格处理器注册到写入流程中。

在源码层面，EasyExcel会在Sheet创建后自动调用所有注册的`CellWriteHandler`的`afterSheetCreate`方法，此时我们的自定义合并逻辑就会被执行。这种设计遵循了"钩子方法"的设计模式，使得用户可以在特定时机插入自定义处理逻辑。

## 动态表头与合并单元格结合使用

在实际应用中，动态表头和合并单元格通常需要结合使用，特别是在复杂报表场景下。例如，我们可能需要根据数据内容动态决定哪些单元格需要合并：

1. 首先构建动态表头数据结构
2. 分析数据内容，确定需要合并的单元格范围
3. 在处理器中实现合并逻辑
4. 通过EasyExcel的链式API将两者结合起来

这种组合使用在大型报表、数据分析结果展示、或者带有复杂统计信息的Excel导出场景中特别有用。

## 实现中的性能考量

在处理大数据量Excel时，合并单元格操作是CPU密集型的，可能会影响性能。因此在实际应用中，需要考虑以下几点：

1. 避免过度合并单元格，特别是在数据量大的情况下
2. 考虑使用批量处理，先处理一批数据，再处理下一批
3. 对于特别复杂的表头和合并需求，可以考虑使用模板方式，预先设计好表头结构
4. 利用EasyExcel的内存优化特性，避免一次性加载过多数据

通过这种方式，EasyExcel可以灵活地满足各种复杂的Excel导出需求，同时保持良好的性能和内存占用。

## 总结

EasyExcel实现动态表头和合并单元格的核心在于理解其数据模型设计和处理器机制。动态表头通过嵌套List结构实现，合并单元格通过自定义处理器实现。两者结合使用可以构建出复杂的、具有良好视觉呈现效果的Excel报表，满足企业中各种复杂的数据展示需求。