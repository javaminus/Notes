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

**事务传播机制**（Propagation Mechanism）是指在 Spring 或其他支持事务的框架中，**一个事务方法被另一个事务方法调用时，事务在方法间是如何传播和管理的规则**。

### 通俗解释

当你在调用一个带有事务的方法时（比如用 `@Transactional` 注解的方法），这个方法可能会被另一个也带有事务的方法调用。事务传播机制就定义了：  
- 当前方法是否要加入调用者的事务，  
- 还是自己新建一个事务，  
- 或者挂起当前事务，  
- 甚至不允许有事务等。

### 主要意义

它保证了**业务方法之间事务的边界和行为一致性**，避免在复杂调用链下事务失控或意外行为。

记忆口诀：**三有三无，一嵌套**

### Spring 常见传播行为举例

- **必需（REQUIRED）**：有就用，没有就新建

- **新建（REQUIRES_NEW）**：总是新建自己的事务

- **强制要（MANDATORY）**：必须有事务，否则报错

- **撑支持（SUPPORTS）**：有就用，没有就不用

- **挂起不用（NOT_SUPPORTED）**：有就挂起，不用事务

- **永远不能有（NEVER）**：有事务报错

- **嵌套保存点（NESTED）**：有事务嵌套保存点，没有就新建

- > ## 1. **什么是嵌套保存点（NESTED）？**
  >
  > - **保存点（Savepoint）**：数据库事务里可以设置“中间点”，如果后面出错，可以回滚到这个保存点，而不是整个事务都回滚。
  > - **嵌套保存点（NESTED）**：在一个事务执行过程中，又进入了另一个方法（比如 Service 调用 Service），此时可以在当前事务下再建一个保存点，形成“嵌套事务”。

### 总结

**事务传播机制**就是用来规定方法间调用时事务的承接和管理方式，是保证数据一致性和业务隔离的重要手段。

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



# 事务传播举例

当然可以！下面举一个**Spring事务传播行为**的实际代码例子，帮助你理解它的作用。

---

## 例子：REQUIRED 与 REQUIRES_NEW 传播行为

假设你有两个业务方法：  
- `orderService.createOrder()`：下订单
- `paymentService.pay()`：支付

我们希望：下订单和支付是两个独立的事务，即使支付失败也不影响订单创建。

### 代码示例

```java
@Service
public class OrderService {

    @Autowired
    private PaymentService paymentService;

    @Transactional(propagation = Propagation.REQUIRED) // 默认传播行为
    public void createOrder() {
        // 1. 创建订单
        // ...保存订单代码...

        // 2. 调用支付
        paymentService.pay();
    }
}

@Service
public class PaymentService {

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 新建事务
    public void pay() {
        // ...支付逻辑...

        // 支付失败，抛异常
        throw new RuntimeException("支付失败！");
    }
}
```

### 结果分析

- `createOrder()` 启动一个事务（REQUIRED）。
- `pay()` 被调用时，`REQUIRES_NEW` 会**挂起外部事务，单独开启一个新的事务**。
- 如果 `pay()` 失败抛异常，**只回滚支付事务，订单事务不会回滚**，订单依然创建成功。

---

## 实际应用场景

- 订单与日志分开管理：下单成功后写操作日志，即使日志写入失败也不影响订单。
- 主业务和补偿业务分离：主流程失败时，补偿操作要独立事务回滚或提交。

---

如需其他传播行为（如 NESTED 或 MANDATORY）例子，也可以随时告诉我！