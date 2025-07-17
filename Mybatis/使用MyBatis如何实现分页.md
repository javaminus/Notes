# MyBatis分页实现方案详解

> 当然可以！其实分页的本质，就是让数据库每次只返回你需要的那一页的数据，而不是全部都查出来。最简单的分页SQL语句就是你说的：  
>
> ```sql
> select * from user limit 1, 10
> ```
> 这条语句的意思是：跳过前1条数据，取接下来的10条。比如你想看第2页，每页10条，就可以用 `limit 10, 10`（跳过前10条，取10条）。
>
> 但实际开发中，**仅仅依靠这一条SQL还不够**，原因主要有以下几点：
>
> ---
>
> ### 1. **分页信息不全，页面无法正常展示**
> 前端分页一般需要：当前页数据、总数据条数、总页数、是否有下一页等。如果只执行这条SQL，只有当前页的数据，所以还需要一条统计总数的SQL：
> ```sql
> select count(*) from user
> ```
> 这样才能让前端显示“共多少页”“共多少条”等信息。
>
> ---
>
> ### 2. **多条件、复杂查询分页难处理**
> 实际业务往往不是单表查询，可能有各种筛选条件（如用户名、状态、时间区间等），SQL变得很复杂，拼接容易出错。分页插件可以自动帮你拼接和处理这些条件，省去了很多麻烦。
>
> ---
>
> ### 3. **性能问题：大数据量深度分页很慢**
> 如果你的表有几十万甚至几百万条数据，`limit 100000, 10` 这种写法会让数据库先跳过前面十万条，效率很低。分页插件可以优化，比如用主键游标、子查询等方式，大大提升性能。
>
> ---
>
> ### 4. **代码复用和维护**
> 如果你每个地方都自己写分页SQL，代码重复多、维护难。分页插件（如PageHelper、MyBatis-Plus）可以自动处理分页参数、总数统计、结果封装，让开发者只关心业务逻辑，维护起来也更方便。
>
> ---
>
> ### 5. **兼容多数据库和复杂SQL场景**
> 不同数据库分页语法不同（MySQL用limit，Oracle用rownum，SQL Server用top），插件可以自动适配。复杂的SQL（联表、排序）也能自动加上分页逻辑。
>
> ---
>
> #### **举个例子：前端分页需要的数据**
> ```json
> {
>   "pageNum": 2,
>   "pageSize": 10,
>   "total": 120,
>   "pages": 12,
>   "list": [ ...当前页的数据... ]
> }
> ```
> 如果只用`select * from user limit 10, 10`，你拿不到`total`和`pages`这些信息，所以还要多查一次`count(*)`。
>
> ---
>
> #### **面试简答：**
> > 直接用limit能实现最基础的分页，但实际开发中还需要统计总数、处理复杂条件、优化性能和统一封装分页结果，所以通常会用分页插件或框架来简化开发和提升系统效率。
>
> ---
>
> 如果你需要分页的完整代码实现或者想了解插件怎么用，也可以告诉我你的具体需求！

MyBatis提供了多种实现分页的方式，从原生支持到插件扩展，下面详细介绍各种实现方法：

## 1. 使用RowBounds实现物理分页

MyBatis内置的RowBounds类提供了基本的分页功能，但这是内存分页（先查询所有数据，再在内存中截取），不适合大数据量场景：

```java
// Mapper接口定义
List<User> getUserList(RowBounds rowBounds);

// 调用方式
SqlSession sqlSession = sqlSessionFactory.openSession();
UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
int offset = (pageNum - 1) * pageSize; // 起始位置
RowBounds rowBounds = new RowBounds(offset, pageSize); // offset是偏移量，pageSize是每页条数
List<User> users = userMapper.getUserList(rowBounds);
```

## 2. 使用PageHelper插件（推荐方式）

PageHelper是MyBatis最流行的分页插件，能够自动识别数据库类型并生成对应的分页SQL：

### 2.1 添加依赖

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.4.6</version>
</dependency>
```

### 2.2 配置插件

在application.properties中配置：

```properties
# 分页插件配置
pagehelper.helper-dialect=mysql
pagehelper.reasonable=true
pagehelper.support-methods-arguments=true
pagehelper.params=count=countSql
```

### 2.3 使用方式

```java
// 方式一：直接使用
PageHelper.startPage(pageNum, pageSize);
List<User> users = userMapper.getAllUsers();
PageInfo<User> pageInfo = new PageInfo<>(users);

// 方式二：Lambda表达式
PageInfo<User> pageInfo = PageHelper.startPage(pageNum, pageSize)
    .doSelectPageInfo(() -> userMapper.getAllUsers());

// 返回结果
return pageInfo; // 包含总记录数、总页数、当前页数据等
```

### 2.4 PageInfo对象包含的信息

```java
PageInfo<User> pageInfo = new PageInfo<>(users);
pageInfo.getTotal(); // 总记录数
pageInfo.getPages(); // 总页数
pageInfo.getPageNum(); // 当前页码
pageInfo.getPageSize(); // 每页条数
pageInfo.getList(); // 当前页数据
pageInfo.isHasNextPage(); // 是否有下一页
pageInfo.isHasPreviousPage(); // 是否有上一页
```

## 3. 手动编写SQL实现分页

在XML映射文件中直接编写带有LIMIT子句的SQL：

```xml
<select id="getUserByPage" resultType="User">
    SELECT * FROM user
    <where>
        <if test="name != null and name != ''">
            AND name LIKE CONCAT('%', #{name}, '%')
        </if>
    </where>
    ORDER BY id DESC
    LIMIT #{offset}, #{pageSize}
</select>
```

```java
// Mapper接口
List<User> getUserByPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("name") String name);

// 调用方式
int offset = (pageNum - 1) * pageSize;
List<User> users = userMapper.getUserByPage(offset, pageSize, name);
```

## 4. 使用MyBatis-Plus实现分页

如果项目使用MyBatis-Plus，它内置了强大的分页功能：

### 4.1 添加依赖

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3.1</version>
</dependency>
```

### 4.2 配置分页插件

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### 4.3 使用方式

```java
// 创建Page对象
Page<User> page = new Page<>(pageNum, pageSize);
// 调用分页查询方法
Page<User> resultPage = userMapper.selectPage(page, new QueryWrapper<User>()
    .like(StringUtils.isNotBlank(name), "name", name)
    .orderByDesc("id"));

// 获取分页结果
List<User> users = resultPage.getRecords();
long total = resultPage.getTotal();
```

## 5. 自定义拦截器实现分页

如果需要完全自定义分页逻辑，可以编写MyBatis拦截器：

```java
@Intercepts({
    @Signature(type = Executor.class, method = "query", 
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class CustomPageInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取参数
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        
        // 判断是否需要分页
        if (rowBounds != null && rowBounds != RowBounds.DEFAULT) {
            // 修改SQL，添加分页语句...
            // 执行分页查询...
        }
        
        return invocation.proceed();
    }
    // 其他必要方法...
}
```

## 6. 分页最佳实践

1. **选择正确的分页方式**：
   - 小数据量：可以使用RowBounds
   - 大数据量：使用PageHelper或手写SQL
   - 复杂项目：考虑MyBatis-Plus

2. **优化查询性能**：
   - 在分页字段上建立索引
   - 避免使用SELECT *
   - 大偏移量查询优化（使用子查询或ID查询）

3. **统一封装分页结果**：
```java
public class PageResult<T> {
    private List<T> list;    // 当前页数据
    private long total;      // 总记录数
    private int pageNum;     // 当前页码
    private int pageSize;    // 每页条数
    private int totalPages;  // 总页数
    // getter/setter方法
}
```

以上就是MyBatis实现分页的几种主要方法，根据实际项目需求选择最适合的方案。在企业级应用中，PageHelper和MyBatis-Plus是最常用且推荐的解决方案。