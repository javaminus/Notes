区分 SQL 语句是在**主库**还是**备库**执行，关键在于**数据源配置和连接方式**，SQL语句本身通常没有区别。以下是详细演示：

---

## 1. 配置主库与备库数据源（以 Spring Boot 为例）

```yaml name=application.yml
spring:
  datasource:
    master:
      url: jdbc:mysql://master-db.example.com:3306/demo
      username: root
      password: master_pwd
    slave:
      url: jdbc:mysql://slave-db.example.com:3306/demo
      username: root
      password: slave_pwd
```

---

## 2. Java 代码演示（使用不同数据源执行 SQL）

```java name=DataSourceDemo.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DataSourceDemo {

    // 分别注入主库和备库的数据源
    @Autowired
    private JdbcTemplate masterJdbcTemplate; // 连接主库

    @Autowired
    private JdbcTemplate slaveJdbcTemplate;  // 连接备库

    // 在主库执行SQL（通常用于写操作或强一致性读）
    public int getCountFromMaster(String date) {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_date = ?";
        return masterJdbcTemplate.queryForObject(sql, Integer.class, date);
    }

    // 在备库执行SQL（通常用于读操作或统计分析）
    public int getCountFromSlave(String date) {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_date = ?";
        return slaveJdbcTemplate.queryForObject(sql, Integer.class, date);
    }
}
```

---

## 3. 使用说明

- **主库执行**：`masterJdbcTemplate` 连接的是主库，所有通过它运行的 SQL 都在主库上执行。
- **备库执行**：`slaveJdbcTemplate` 连接的是备库，所有通过它运行的 SQL 都在备库上执行。

你可以根据业务需求，决定某个查询或写操作应该使用哪个数据源。

---

## 4. 总结

- **SQL语句本身无区别**，区分主库/备库在于连接的数据源不同。
- 应用层（如Spring）配置不同的数据源，代码中选择用哪个数据源执行SQL，就决定了是在主库还是备库执行。

如需 MyBatis、ShardingSphere 或其他框架的代码示例，也可继续补充！