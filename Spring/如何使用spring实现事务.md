### **Spring 事务实现方式**
Spring 提供了多种方式来实现事务管理，主要包括 **编程式事务管理** 和 **声明式事务管理**。

---

## **1. 编程式事务管理**
编程式事务管理通过 `TransactionTemplate` 或 `PlatformTransactionManager` 手动控制事务。

### **使用 `TransactionTemplate`**
```java
@Service
public class AccountService {
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private AccountDao accountDao;

    public void transfer(String from, String to, double amount) {
        transactionTemplate.execute(status -> {
            accountDao.debit(from, amount);  // 扣钱
            accountDao.credit(to, amount);   // 加钱
            return null;
        });
    }
}
```
- `execute` 方法中执行数据库操作，**Spring 自动管理提交或回滚事务**。

---

## **2. 声明式事务管理**
### **（1）基于 `@Transactional` 注解（推荐）**
使用 `@Transactional` 让 Spring 自动管理事务，减少代码侵入性。

#### **配置事务管理器**
```java
@Configuration
@EnableTransactionManagement
public class TxConfig {
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

#### **使用 `@Transactional`**
```java
@Service
public class AccountService {
    @Autowired
    private AccountDao accountDao;

    @Transactional
    public void transfer(String from, String to, double amount) {
        accountDao.debit(from, amount);
        accountDao.credit(to, amount);
    }
}
```
- **Spring 会自动开启事务，并在方法执行完成后提交事务；如果出现异常，则回滚事务**。

### **（2）基于 XML 配置**
如果使用 XML 配置，可以如下启用事务管理：
```xml
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>

<tx:annotation-driven transaction-manager="transactionManager"/>
```
然后在 Service 方法上使用 `@Transactional`。

---

## **3. 事务的传播行为**

Spring 提供了 7 种事务传播机制（`Propagation`），用于控制方法嵌套调用时的事务行为：

| 传播类型 | 作用 |
|---------|------|
| `REQUIRED` | 默认值，加入当前事务，若无则创建新事务 |
| `REQUIRES_NEW` | 挂起当前事务，创建新事务 |
| `NESTED` | 当前事务中创建嵌套事务，可部分回滚 |
| `SUPPORTS` | 有事务则加入，无事务则以非事务方式执行 |
| `NOT_SUPPORTED` | 以非事务方式运行，挂起当前事务 |
| `NEVER` | 强制非事务模式，若有事务则抛异常 |
| `MANDATORY` | 必须在已有事务中运行，否则抛异常 |

示例：
```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void method() { ... }
```

---

## **4. 事务的回滚策略**
默认情况下，**`@Transactional` 只会回滚 `RuntimeException`（`Unchecked Exception`）和 `Error`**，不会回滚 `Checked Exception`（如 `IOException`）。
- **回滚所有异常**：
  ```java
  @Transactional(rollbackFor = Exception.class)
  ```
- **指定不回滚某个异常**：
  ```java
  @Transactional(noRollbackFor = ArithmeticException.class)
  ```

---

## **总结**
| 方式 | 适用场景 | 代码侵入性 |
|------|---------|----------|
| 编程式事务 | 需要细粒度控制事务的情况 | 高 |
| `@Transactional` 注解 | 业务逻辑较清晰的情况下 | 低（推荐） |
| XML 事务 | 适用于 XML 配置项目 | 低 |

最推荐的方式是 **`@Transactional` + `DataSourceTransactionManager`**，结合 `rollbackFor` 控制回滚策略，满足大多数业务需求。