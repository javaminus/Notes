# MyBatis-Plus 分页原理详解

MyBatis-Plus 的分页功能通过拦截器实现，与 PageHelper 有类似之处，但具有其独特的实现机制和优化策略。

## 1. 核心拦截器结构

MyBatis-Plus 3.4+ 版本的分页主要依赖两个关键组件：
- **MybatisPlusInterceptor**：核心拦截器容器
- **PaginationInnerInterceptor**：分页内部拦截器，实现具体分页功能

```java
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 添加分页内部拦截器，并指定数据库类型
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
}
```

## 2. 分页执行原理

### 2.1 拦截 SQL 执行过程

MyBatis-Plus 拦截器实现了 MyBatis 的 `Interceptor` 接口，拦截点是 `Executor` 的 `query` 方法。当执行查询时：

1. 用户通过 `Page<T> page = new Page<>(current, size)` 创建分页对象
2. 调用 Mapper 方法时传入 Page 对象 `userMapper.selectPage(page, queryWrapper)`
3. 拦截器检测到参数中有 Page 对象，触发分页逻辑

### 2.2 SQL 改写过程

PaginationInnerInterceptor 会：

1. **解析原始 SQL**：获取当前执行的 SQL 语句
2. **构建 COUNT 查询**：生成 `SELECT COUNT(*) FROM (原SQL)` 形式的计数 SQL
3. **执行 COUNT 查询**：获取满足条件的总记录数
4. **改写原始 SQL**：根据数据库类型添加分页语法
   - MySQL: `LIMIT offset, size`
   - Oracle: 使用 ROWNUM
   - PostgreSQL: `LIMIT size OFFSET offset`
   - 其他数据库各自特定语法

### 2.3 分页结果封装

1. **总数注入**：将 COUNT 查询结果设置到 Page 对象的 total 属性
2. **当前页数据**：执行改写后的 SQL 得到当前页数据集合
3. **自动计算**：根据总数和每页条数，自动计算总页数等信息
4. **结果封装**：将数据集合设置到 Page 对象的 records 属性

## 3. 高级特性与优化

### 3.1 COUNT 查询优化

- **自动优化 COUNT SQL**：移除不必要的 ORDER BY，提高性能
- **支持自定义 COUNT SQL**：可通过 `Page.setCountSql()` 设置自定义 COUNT SQL
- **合理的 COUNT 触发机制**：
  - 首次分页会执行 COUNT 查询
  - 可通过 `Page.setSearchCount(false)` 禁用总数查询
  - 支持设置最大限制数避免全表扫描

### 3.2 多数据源和动态表名支持

- 根据配置的数据库类型自动适配分页语法
- 支持动态表名替换，实现分库分表场景下的分页

### 3.3 溢出总页数处理

- 提供 `overflow` 配置，控制请求页大于总页数时的处理策略
  - true: 返回首页数据
  - false: 返回最后一页数据

## 4. 与其他分页插件的区别

**MyBatis-Plus 分页 vs PageHelper**：
- 调用方式：MyBatis-Plus 通过 Page 对象传参，PageHelper 通过静态方法设置线程变量
- 集成度：MyBatis-Plus 分页是其生态的一部分，与其他功能无缝衔接
- 扩展性：MyBatis-Plus 分页支持更多定制化选项和场景优化

## 5. 内部核心源码分析（简化版）

```java
// PaginationInnerInterceptor 核心逻辑伪代码
public Object intercept(Invocation invocation) {
    // 1. 检查是否有分页参数
    Page<?> page = findPage(invocation.getArgs());
    if (page == null) {
        return invocation.proceed(); // 无分页参数，正常执行
    }
    
    // 2. 获取原始SQL
    String originalSql = boundSql.getSql();
    
    // 3. 执行COUNT查询（如果需要）
    if (page.isSearchCount()) {
        String countSql = getCountSql(originalSql); // 构建COUNT SQL
        Long total = executeCountQuery(countSql);   // 执行COUNT查询
        page.setTotal(total);                      // 设置总记录数
    }
    
    // 4. 构建分页SQL
    String pageSql = concatPageSql(originalSql, page); // 根据数据库类型构建分页SQL
    
    // 5. 替换为分页SQL并执行
    metaObject.setValue("delegate.boundSql.sql", pageSql);
    Object result = invocation.proceed();
    
    // 6. 返回结果（结果会自动设置到page对象的records）
    return result;
}
```

## 总结

MyBatis-Plus 分页原理是通过拦截 SQL 执行过程，自动添加分页语句和执行 COUNT 查询，将分页信息封装到 Page 对象中返回。它对 SQL 进行了智能优化，支持多种数据库，并提供了丰富的定制选项。这种拦截器方式实现的分页，使开发者可以专注于业务逻辑而无需编写繁琐的分页代码，大大提高了开发效率。