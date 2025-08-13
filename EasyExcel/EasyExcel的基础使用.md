# EasyExcel导入导出全面指南（面试专用）

## 1. EasyExcel基础概念

**EasyExcel** 是阿里巴巴开源的一款基于Java的Excel处理工具，相比Apache POI，它使用了更少的内存并提供了更简单的API。

### 核心优势（面试亮点）：
- **低内存消耗**：采用SAX模式逐行读取，避免将整个文件加载到内存

> - **DOM模式**：一次性将整个文件加载到内存，构建成一个树结构，适合小文件、频繁操作场景。
> - **SAX模式**：边读边处理，适合大文件、只做遍历和简单处理。

- **简洁API**：大幅简化了Excel操作的代码量
- **高性能**：读写速度快，适合大数据量场景
- **注解驱动**：通过Java注解轻松配置Excel映射关系

## 2. 导入Excel（读取数据）

### 2.1 数据结构与核心组件

#### ① 实体类定义（映射Excel列）
```java
public class UserImportDto {
    @ExcelProperty("用户名")  // Excel中的列名
    private String username;
    
    @ExcelProperty("年龄")
    private Integer age;
    
    @ExcelProperty("出生日期")
    @DateTimeFormat("yyyy-MM-dd")  // 日期格式化
    private Date birthDate;
    
    @ExcelIgnore  // 不从Excel读取的字段
    private String ignoreField;
    
    // getter/setter省略
}
```

#### ② 读取监听器（核心组件）

>  AnalysisContext context，其实这只是一个接口

```java
public interface AnalysisContext {

    /**
     * 获取当前 sheet 的信息
     */
    ReadSheet currentReadSheet();

    /**
     * 获取当前行号（从0开始）
     */
    Integer readRowHolder().getRowIndex();

    /**
     * 获取表头数据。key为列index，value为表头对象
     */
    Map<Integer, CellDataList> readRowHolder().getCellMap();

    /**
     * 获取自定义参数
     */
    Map<String, Object> getCustom();

    /**
     * 获取当前表头（如果有）
     */
    List<String> readSheetHolder().getExcelHeadProperty().getHeadMap();

    // 还有很多其它方法，具体可查看源码
}
```



```java
public class UserImportListener extends AnalysisEventListener<UserImportDto> {
    // 存储读取结果
    private List<UserImportDto> list = new ArrayList<>();
    // 存储错误信息
    private List<String> errorMsgs = new ArrayList<>();
    
    // 每解析一行数据都会调用此方法
    @Override
    public void invoke(UserImportDto data, AnalysisContext context) {
        // 1. 数据校验
        if (StringUtils.isEmpty(data.getUsername())) {
            int rowIndex = context.readRowHolder().getRowIndex() + 1;
            errorMsgs.add("第" + rowIndex + "行：用户名不能为空");
            return;
        }
        
        // 2. 数据处理（可转换、加工数据）
        
        // 3. 添加到结果集
        list.add(data);
        
        // 4. 达到BATCH_COUNT时，进行批量插入数据库，防止OOM
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }
    }
    
    // 所有数据解析完成后调用
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 确保最后遗留的数据也被保存
        saveData();
    }
    
    // 保存数据的方法
    private void saveData() {
        if (!list.isEmpty()) {
            // 调用Service层保存数据
            // userService.batchSave(list);
        }
    }
    
    // 获取结果和错误信息的方法
    public List<UserImportDto> getList() {
        return list;
    }
    
    public List<String> getErrorMsgs() {
        return errorMsgs;
    }
}
```

### 2.2 读取Excel的代码实现

```java
public void importExcel(MultipartFile file) throws IOException {
    // 创建自定义监听器
    UserImportListener listener = new UserImportListener();
    
    try {
        // 调用EasyExcel读取API
        EasyExcel.read(file.getInputStream(), UserImportDto.class, listener)
                .sheet()  // 默认读取第一个sheet
                .headRowNumber(1)  // 表头行数，默认为1
                .doRead();
                
        // 读取完成后处理结果
        List<UserImportDto> successList = listener.getList();
        List<String> errorMsgs = listener.getErrorMsgs();
        
        if (!errorMsgs.isEmpty()) {
            // 处理错误信息
        }
        
        // 其他业务处理...
    } catch (Exception e) {
        // 异常处理
        throw new RuntimeException("Excel导入失败", e);
    }
}
```

## 3. 导出Excel（写入数据）

### 3.1 数据结构与配置

#### ① 导出实体类定义
```java
public class UserExportDto {
    @ExcelProperty(value = "用户ID", index = 0)
    private Long id;
    
    @ExcelProperty(value = "用户名", index = 1)
    private String username;
    
    @ExcelProperty(value = "年龄", index = 2)
    private Integer age;
    
    @ExcelProperty(value = "状态", index = 3)
    // 使用转换器处理枚举类型
    @ExcelEnumFormat(UserStatusEnum.class)
    private Integer status;
    
    @ExcelProperty(value = "注册时间", index = 4)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date registerTime;
    
    // getter/setter省略
}
```

#### ② 自定义样式（可选）
```java
public class CustomCellStyleStrategy extends AbstractCellStyleStrategy {
    @Override
    public void initCellStyle(Workbook workbook, IndexedColors indexedColors) {
        // 初始化样式
    }
    
    @Override
    public void setHeadCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
        // 设置表头样式
        CellStyle headStyle = cell.getSheet().getWorkbook().createCellStyle();
        headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle.setBorderBottom(BorderStyle.THIN);
        // 其他样式设置...
        cell.setCellStyle(headStyle);
    }
    
    @Override
    public void setContentCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
        // 设置内容样式
    }
}
```

### 3.2 导出Excel的代码实现

```java
public void exportUser(HttpServletResponse response) throws IOException {
    // 1. 设置响应头信息
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setCharacterEncoding("utf-8");
    String fileName = URLEncoder.encode("用户数据", "UTF-8").replaceAll("\\+", "%20");
    response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    
    try {
        // 2. 查询数据（实际项目中从数据库获取）
        List<UserExportDto> dataList = userService.findExportData();
        
        // 3. 使用EasyExcel写入数据
        EasyExcel.write(response.getOutputStream(), UserExportDto.class)
                .registerWriteHandler(new CustomCellStyleStrategy())  // 注册样式处理器
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())  // 自适应列宽
                .sheet("用户数据")
                .doWrite(dataList);
    } catch (Exception e) {
        // 导出异常处理
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println(JSON.toJSONString(new Result<>(false, "导出Excel失败")));
    }
}
```

## 4. 重要注解与配置详解

### 常用注解（面试重点）

| 注解                | 用途                   | 示例                                          |
| ------------------- | ---------------------- | --------------------------------------------- |
| `@ExcelProperty`    | 指定Excel列名和索引    | `@ExcelProperty(value = "用户名", index = 1)` |
| `@ExcelIgnore`      | 忽略该字段，不导入导出 | `@ExcelIgnore`                                |
| `@DateTimeFormat`   | 日期格式化             | `@DateTimeFormat("yyyy-MM-dd")`               |
| `@NumberFormat`     | 数值格式化             | `@NumberFormat("#.##%")`                      |
| `@ContentFontStyle` | 内容字体样式           | `@ContentFontStyle(fontHeightInPoints = 11)`  |
| `@HeadFontStyle`    | 表头字体样式           | `@HeadFontStyle(bold = true)`                 |
| `@ColumnWidth`      | 设置列宽               | `@ColumnWidth(20)`                            |

### 重要配置项

```java
// 读取配置示例
EasyExcel.read(inputStream, UserDto.class, listener)
        .sheet()
        .headRowNumber(1)  // 表头行数
        .doRead();

// 写入配置示例
EasyExcel.write(outputStream, UserDto.class)
        .sheet("sheet名称")
        .excludeColumnFiledNames(Arrays.asList("不导出的字段名"))  // 排除字段
        .includeColumnFiledNames(Arrays.asList("仅导出的字段名"))  // 包含字段
        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())  // 自适应列宽
        .doWrite(dataList);
```

## 5. 实际应用场景与扩展功能

### 5.1 批量导入优化
处理大数据量时，采用批量提交方式避免OOM：
```java
@Override
public void invoke(UserDto data, AnalysisContext context) {
    list.add(data);
    // 达到BATCH_COUNT，进行批量插入
    if (list.size() >= BATCH_COUNT) {
        saveData();
        list.clear();
    }
}
```

### 5.2 动态列导出
```java
// 动态头信息
List<List<String>> headList = new ArrayList<>();
// 动态数据
List<List<Object>> dataList = new ArrayList<>();
// 填充数据...

// 使用自定义头和数据导出
EasyExcel.write(outputStream)
        .head(headList)
        .sheet("动态列")
        .doWrite(dataList);
```

### 5.3 模板导出
```java
// 读取模板文件
InputStream templateInputStream = this.getClass().getClassLoader().getResourceAsStream("template.xlsx");
// 基于模板写出
EasyExcel.write(outputStream, UserDto.class)
        .withTemplate(templateInputStream)
        .sheet()
        .doFill(dataList);
```

## 6. 面试答题技巧

### 6.1 常见问题与回答

**Q: EasyExcel与Apache POI的区别和优势？**  
A: EasyExcel是基于POI开发的，主要优势有：
1. 采用SAX方式逐行读取解析，大幅降低内存消耗，支持百万级数据处理
2. API更加简洁，开发效率高
3. 提供丰富注解，支持复杂表头、样式等配置
4. 读取时支持监听器模式，更灵活处理数据

**Q: EasyExcel如何处理大数据量导入？**  
A: 主要通过以下方式优化：
1. 使用SAX模式读取，避免一次加载全部数据
2. 在监听器中实现批量处理逻辑，达到一定数量才批量入库
3. 并发处理多个Sheet的数据
4. 可配合线程池实现异步处理

**Q: 如何实现Excel导入数据校验？**  
A: 主要在监听器的invoke方法中实现：
1. 基础校验：非空、长度、格式等
2. 业务规则校验：唯一性、关联性等
3. 收集校验错误，可返回Excel错误报告
4. 可结合JSR-303注解实现自动校验

### 6.2 项目实战亮点

在回答时可强调自己项目中的创新点，例如：
1. 实现了完善的Excel异常处理机制
2. 开发了通用导入导出框架，支持动态列和复杂表头
3. 实现了大数据量分页导出功能
4. 集成了数据校验和错误反馈机制

## 总结

EasyExcel作为Java生态中的Excel处理利器，通过简洁API和高效实现，极大提升了开发效率和性能。熟练掌握其导入导出流程、数据结构和核心配置，能够在面试中展示自己对Java数据处理领域的专业能力。

在实际面试中，建议结合自己的项目经验，讲述使用EasyExcel解决的实际业务问题，以及优化的思路和方法，这样会给面试官留下更深刻的印象。