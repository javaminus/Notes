区分 SQL 语句是在**主库**还是**分库**执行，实际上不是 SQL 语句本身的区别，而是**数据库连接配置**的不同。  
在应用层（如 Java 或 Python）通过不同的数据源连接到主库、分库或备库，然后执行相同的 SQL 语句。

下面分别举例说明，并以 Java Spring（MyBatis/JDBC）为例代码演示：

---

## 一、主库执行 SQL

**场景：**  
主库一般用于写操作（插入、更新、删除），也可以用于读操作。

### 配置数据源（主库）

```yaml name=application.yml
spring:
  datasource:
    url: jdbc:mysql://master.db.example.com:3306/order_db
    username: root
    password: master123
```

### 代码示例

```java name=MainDataSourceDemo.java
@Autowired
private JdbcTemplate jdbcTemplate; // 默认数据源指向主库

public int getOrderCountFromMaster(String date) {
    String sql = "SELECT COUNT(*) FROM orders WHERE order_date = ?";
    return jdbcTemplate.queryForObject(sql, Integer.class, date);
}
```

---

## 二、分库执行 SQL

**场景：**  
分库通常用于大数据量场景，数据分散在多个数据库。需要按分库规则选择数据源或遍历所有分库。

### 多数据源配置（分库）

```yaml name=application.yml
spring:
  datasource:
    order_db_00:
      url: jdbc:mysql://db00.example.com:3306/order_db_00
      username: root
      password: db00pwd
    order_db_01:
      url: jdbc:mysql://db01.example.com:3306/order_db_01
      username: root
      password: db01pwd
    order_db_02:
      url: jdbc:mysql://db02.example.com:3306/order_db_02
      username: root
      password: db02pwd
```

### 代码演示（分库遍历）

```java name=ShardingDataSourceDemo.java
@Autowired
private JdbcTemplate jdbcTemplateDb00; // 指向 order_db_00
@Autowired
private JdbcTemplate jdbcTemplateDb01; // 指向 order_db_01
@Autowired
private JdbcTemplate jdbcTemplateDb02; // 指向 order_db_02

public int getOrderCountFromAllShards(String date) {
    String sql = "SELECT COUNT(*) FROM orders WHERE order_date = ?";
    int count00 = jdbcTemplateDb00.queryForObject(sql, Integer.class, date);
    int count01 = jdbcTemplateDb01.queryForObject(sql, Integer.class, date);
    int count02 = jdbcTemplateDb02.queryForObject(sql, Integer.class, date);
    return count00 + count01 + count02;
}
```

---

## 三、如何区分？

- **主库执行**：连接的是主库的数据源（如 master.db.example.com），通常用于写操作。
- **分库执行**：连接的是分库的数据源（如 db00, db01, db02），通常用于读操作或分布式写。
- **SQL语句本身不变**，变化的是数据源和连接地址。

---

## 四、总结

1. 判断 SQL 在主库还是分库执行，关键看**连接的数据库实例**。
2. 应用层通过配置不同的数据源，决定 SQL 最终在哪个库执行。
3. SQL 语句内容一致，连接不同的数据源即可区分。

如需具体框架（如 Spring、MyBatis、ShardingSphere）代码示例，可以进一步补充说明！