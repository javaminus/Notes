# MyBatis-Plus 操作数据库详细学习手册

---

## 目录

1. MyBatis-Plus 简介  
2. 快速开始  
3. 常用注解与配置  
4. 基本 CRUD 操作  
5. 条件构造器与分页  
6. 批量操作  
7. 复杂查询（Wrapper）  
8. 自定义 SQL  
9. 乐观锁、自动填充、逻辑删除  
10. 代码生成器  
11. 常见问题与参考资料

---

## 1. MyBatis-Plus 简介

MyBatis-Plus 是 MyBatis 的增强工具包，简化了 CRUD 代码，支持丰富的查询构造器和自动代码生成，兼容原生 MyBatis 用法。

- 官网：[https://baomidou.com/](https://baomidou.com/)
- 主要优点：零侵入、强大的 CRUD 封装、丰富的功能扩展

---

## 2. 快速开始

### 2.1 添加依赖（Maven）

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.5</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### 2.2 数据库配置（application.yml）

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/testdb?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 2.3 编写实体类

```java
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String email;
}
```

### 2.4 编写 Mapper 接口

```java
public interface UserMapper extends BaseMapper<User> {}
```

### 2.5 启动类添加扫描注解

```java
@SpringBootApplication
@MapperScan("com.example.mapper")
public class Application {}
```

---

## 3. 常用注解与配置

- `@TableName("table_name")`：实体类与表名映射
- `@TableId`：主键字段
- `@TableField`：普通字段、填充字段
- `@TableLogic`：逻辑删除字段
- `@Version`：乐观锁字段

---

## 4. 基本 CRUD 操作

### 4.1 新增

```java
User user = new User();
user.setName("Tom");
user.setAge(25);
user.setEmail("tom@example.com");
userMapper.insert(user);
```

### 4.2 查询

**按主键查找：**
```java
User user = userMapper.selectById(1L);
```
**查全部：**
```java
List<User> list = userMapper.selectList(null);
```

### 4.3 修改

```java
User user = userMapper.selectById(1L);
user.setEmail("new@email.com");
userMapper.updateById(user);
```

### 4.4 删除

```java
userMapper.deleteById(1L);
```

---

## 5. 条件构造器与分页

### 5.1 条件查询（QueryWrapper）

# 3. 常用方法

| 方法        | 说明          | 示例                                     |
| ----------- | ------------- | ---------------------------------------- |
| eq          | 等于          | .eq("name", "Tom")                       |
| ne          | 不等于        | .ne("status", 0)                         |
| gt / ge     | 大于/大于等于 | .gt("age", 20)                           |
| lt / le     | 小于/小于等于 | .le("age", 30)                           |
| like        | 模糊匹配      | .like("name", "Jack")                    |
| likeLeft    | 左模糊        | .likeLeft("email", "@qq")                |
| likeRight   | 右模糊        | .likeRight("name", "J")                  |
| isNull      | 是否为NULL    | .isNull("email")                         |
| isNotNull   | 是否不为NULL  | .isNotNull("email")                      |
| in          | in查询        | .in("id", Arrays.asList(1,2,3))          |
| notIn       | not in        | .notIn("status", 0, 1)                   |
| between     | 范围查询      | .between("age", 18, 30)                  |
| orderByAsc  | 升序排序      | .orderByAsc("id")                        |
| orderByDesc | 降序排序      | .orderByDesc("age")                      |
| groupBy     | 分组          | .groupBy("status")                       |
| or          | 或条件        | .eq(...).or().eq(...)                    |
| and         | 并条件        | .and(wrapper -> wrapper.eq(...).ne(...)) |
| last        | 追加SQL片段   | .last("limit 1")                         |

```java
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("age", 20).like("name", "Tom");
List<User> users = userMapper.selectList(wrapper);
```

### 5.2 分页查询

```java
Page<User> page = new Page<>(1, 10); // 第1页，每页10条
IPage<User> userPage = userMapper.selectPage(page, null);
List<User> users = userPage.getRecords();
long total = userPage.getTotal();
```

---

## 6. 批量操作

### 6.1 批量插入

```java
List<User> users = Arrays.asList(
    new User(null, "A", 20, "a@test.com"),
    new User(null, "B", 21, "b@test.com")
);
users.forEach(userMapper::insert);
```

### 6.2 批量删除

```java
userMapper.deleteBatchIds(Arrays.asList(1L, 2L, 3L));
```

---

## 7. 复杂查询（Wrapper）

### 7.1 LambdaQueryWrapper

```java
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.between(User::getAge, 18, 30)
       .like(User::getName, "Tom");
List<User> users = userMapper.selectList(wrapper);
```

### 7.2 排序、分组

```java
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.orderByDesc("age").groupBy("name");
List<User> users = userMapper.selectList(wrapper);
```

### 7.3 只查部分字段

```java
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.select("id", "name").eq("age", 20);
List<User> users = userMapper.selectList(wrapper);
```

---

## 8. 自定义 SQL

### 8.1 Mapper XML

1. 在 resources/mapper 目录下创建 UserMapper.xml

```xml
<mapper namespace="com.example.mapper.UserMapper">
    <select id="selectByName" resultType="com.example.entity.User">
        SELECT * FROM user WHERE name = #{name}
    </select>
</mapper>
```

2. Mapper 接口添加方法

```java
List<User> selectByName(@Param("name") String name);
```

---

## 9. 乐观锁、自动填充、逻辑删除

### 9.1 乐观锁

> **面试就用sql实现就行了，毕竟sql是底层**
>
> ---
>
> ## 一、准备数据表（加入version字段）
>
> ```sql
> CREATE TABLE product (
>     id INT PRIMARY KEY AUTO_INCREMENT,
>     name VARCHAR(50),
>     stock INT,
>     version INT DEFAULT 0    -- 乐观锁版本号
> );
> ```
>
> ---
>
> ## 二、查询当前数据和版本号
>
> ```sql
> SELECT id, stock, version FROM product WHERE id = 1;
> ```
>
> ---
>
> ## 三、带版本号条件的更新（核心乐观锁实现）
>
> 假设你查到 version=3，现在要扣减库存：
>
> ```sql
> UPDATE product
> SET stock = stock - 1,
>     version = version + 1
> WHERE id = 1
>   AND version = 3;
> ```
>
> > **说明**：  
> > - 只有当数据库中version=3时，才会更新成功，否则更新失败（影响行数=0）。
> > - 这样就防止了并发修改导致数据被覆盖。
>
> ---
>
> ## 四、判断是否更新成功
>
> - 如果 `UPDATE` 返回**影响行数为1**，说明更新成功。
> - 如果 `UPDATE` 返回**影响行数为0**，说明有其他线程已经修改过（version已经被改），需要**重新查询、重试**操作。
>
> ---
>
> ## 五、伪代码流程举例
>
> ```sql
> -- 1. 查询
> SELECT stock, version FROM product WHERE id=1;
> 
> -- 2. 执行业务逻辑（如判断stock>0）
> 
> -- 3. 乐观锁更新
> UPDATE product
> SET stock = stock - 1, version = version + 1
> WHERE id = 1 AND version = 查询出来的version;
> 
> -- 4. 检查影响行数
> --    1行：成功
> --    0行：失败，重试
> ```
>
> ---
>
> ## 六、注意事项
>
> - **version字段**可以用int、bigint等整数类型，业务也可用时间戳字段。
> - 乐观锁适合**并发不是极高**、冲突概率较低的场景。
>
> ---
>
> 如需Java/MyBatis/其他具体实现代码可以继续追问！

### 9.2 自动填充

实体类字段：

```java
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createTime;
@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updateTime;
```

实现 MetaObjectHandler：

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
    }
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
```

### 9.3 逻辑删除

实体类字段：

```java
@TableLogic
private Integer deleted;
```

application.yml 配置：

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

---

## 10. 代码生成器

```java
AutoGenerator generator = new AutoGenerator();
generator.setDataSource(...); // 数据源配置
generator.setPackageInfo(...); // 包信息配置
generator.setStrategy(...); // 策略配置
generator.setTemplate(...); // 模板配置
generator.execute();
```

详细配置参考官网：[代码生成器](https://baomidou.com/pages/981406/)

---

## 11. 常见问题与参考资料

- 官网文档：[https://baomidou.com/](https://baomidou.com/)
- [GitHub 示例项目](https://github.com/baomidou/mybatis-plus-samples)
- 常见问题：[FAQ](https://baomidou.com/pages/24112f/)

---

## 12. 实用工具和扩展

- **MyBatisX 插件**（IDEA插件）：提升开发效率
- **MyBatis-Plus Extension**：支持更多高级功能，如多租户、数据权限等

---

## 13. 总结

MyBatis-Plus 能大幅简化数据库操作代码，提升开发效率，推荐在 Spring Boot 项目中使用。  
建议结合 Lambda 表达式、Wrapper 条件构造器以及插件功能，灵活应对各种业务场景。

---