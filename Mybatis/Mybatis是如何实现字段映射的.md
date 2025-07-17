# MyBatis 字段映射机制详解

MyBatis 实现 Java 对象与数据库表字段映射的机制非常灵活，主要通过以下几种方式完成：

## 1. 基础映射原理

MyBatis 的字段映射核心是**结果映射机制**，在SQL执行后，MyBatis 将结果集的每一行数据转换为 Java 对象的过程中，需要确定数据库列与对象属性之间的对应关系。

### 1.1 自动映射

默认情况下，MyBatis 会尝试自动映射结果集中的列到 JavaBean 的属性：

- **原理**：通过**反射机制**获取对象的属性，然后按照属性名和列名匹配
- **匹配规则**：默认使用大小写不敏感的匹配，例如 `user_name` 会映射到 `userName`

```java
// 自动映射示例
@Select("SELECT id, user_name, email FROM users")
List<User> findAllUsers();
```

## 2. 映射配置方式

### 2.1 XML 配置映射

使用 `resultMap` 元素详细定义映射关系：

```xml
<resultMap id="userResultMap" type="User">
    <id property="userId" column="id" />
    <result property="userName" column="user_name"/>
    <result property="emailAddress" column="email"/>
</resultMap>

<select id="selectUsers" resultMap="userResultMap">
    SELECT id, user_name, email FROM users
</select>
```

### 2.2 注解配置映射

使用 `@Results` 注解和 `@Result` 子注解定义映射关系：

```java
@Select("SELECT id, user_name, email FROM users")
@Results(id = "userMap", value = {
    @Result(property = "userId", column = "id", id = true),
    @Result(property = "userName", column = "user_name"),
    @Result(property = "emailAddress", column = "email")
})
List<User> selectUsers();
```

## 3. 字段映射内部实现机制

### 3.1 映射流程

MyBatis 的字段映射主要由 `ResultSetHandler` 接口的默认实现 `DefaultResultSetHandler` 负责处理：

1. **结果集解析**：获取 `ResultSet` 对象，遍历每一行数据
2. **创建目标对象**：通过反射创建目标类的实例
3. **属性映射**：根据映射规则，将结果集的列值设置到对象属性
4. **类型转换**：使用 `TypeHandler` 处理 JDBC 类型与 Java 类型的转换

### 3.2 自动映射实现

`autoMappingBehavior` 配置控制自动映射行为：

```java
// DefaultResultSetHandler 中的自动映射逻辑（伪代码）
private boolean applyAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, 
                                      MetaObject metaObject, String columnPrefix) {
    // 获取未明确映射的列
    List<String> unmappedColumnNames = rsw.getUnmappedColumnNames(resultMap, columnPrefix);
    
    for (String columnName : unmappedColumnNames) {
        String propertyName = columnName;
        
        // 处理下划线转驼峰
        if (configuration.isMapUnderscoreToCamelCase()) {
            propertyName = camelCaseConversion(columnName);
        }
        
        // 检查是否有匹配的属性
        if (metaObject.hasSetter(propertyName)) {
            // 获取值并设置到对象属性中
            TypeHandler<?> typeHandler = rsw.getTypeHandler(propertyName, columnName);
            Object value = typeHandler.getResult(rsw.getResultSet(), columnName);
            metaObject.setValue(propertyName, value);
        }
    }
}
```

## 4. 命名规则与转换

### 4.1 下划线转驼峰

MyBatis 提供了自动将数据库下划线命名法转换为 Java 驼峰命名法的功能：

```xml
<!-- 在MyBatis配置文件中启用 -->
<settings>
    <setting name="mapUnderscoreToCamelCase" value="true"/>
</settings>
```

### 4.2 转换原理

`CamelCaseMap` 类实现了下划线到驼峰的转换规则：

```java
// CamelCaseMap 中的转换逻辑（伪代码）
private String convertToCamelCase(String input) {
    StringBuilder result = new StringBuilder();
    boolean nextUpperCase = false;
    
    for (int i = 0; i < input.length(); i++) {
        char currentChar = input.charAt(i);
        if (currentChar == '_') {
            nextUpperCase = true;
        } else {
            if (nextUpperCase) {
                result.append(Character.toUpperCase(currentChar));
                nextUpperCase = false;
            } else {
                result.append(Character.toLowerCase(currentChar));
            }
        }
    }
    return result.toString();
}
```

## 5. TypeHandler 类型处理器

字段映射不仅涉及名称匹配，还包括类型转换，MyBatis 通过 TypeHandler 处理：

```java
// TypeHandler接口
public interface TypeHandler<T> {
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;
    T getResult(ResultSet rs, String columnName) throws SQLException;
    T getResult(ResultSet rs, int columnIndex) throws SQLException;
    T getResult(CallableStatement cs, int columnIndex) throws SQLException;
}
```

### 5.1 内置类型处理器

MyBatis 提供了大量内置类型处理器，如 `IntegerTypeHandler`, `StringTypeHandler` 等。

### 5.2 自定义类型处理器

可以实现自己的类型处理器来支持特定数据类型：

```java
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {
    private Class<T> clazz;
    
    public JsonTypeHandler(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) 
        throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }
    
    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return JSON.parseObject(json, clazz);
    }
    
    // 其他方法实现...
}
```

## 6. 复杂映射处理

### 6.1 嵌套结果映射

处理一对一、一对多关系：

```xml
<resultMap id="userWithOrdersMap" type="User">
    <id property="id" column="user_id"/>
    <result property="name" column="user_name"/>
    <collection property="orders" ofType="Order">
        <id property="id" column="order_id"/>
        <result property="amount" column="order_amount"/>
    </collection>
</resultMap>
```

### 6.2 懒加载实现

MyBatis 通过动态代理实现懒加载，在需要时才执行额外的SQL：

```xml
<resultMap id="userMap" type="User">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <collection property="orders" select="selectOrdersByUserId" column="id" fetchType="lazy"/>
</resultMap>
```

## 7. 总结

MyBatis 的字段映射机制综合运用了反射、类型转换、动态代理等多种技术手段：

1. **配置灵活**：支持XML和注解两种配置方式
2. **自动映射**：默认支持同名映射和下划线转驼峰
3. **类型转换**：通过TypeHandler体系实现JDBC类型和Java类型的互转
4. **复杂映射**：支持嵌套查询、嵌套结果、延迟加载等高级特性
5. **可扩展性强**：支持自定义TypeHandler、ResultHandler等

这种灵活而强大的映射机制，使MyBatis能够应对各种复杂的ORM场景，是其作为持久层框架受欢迎的重要原因。