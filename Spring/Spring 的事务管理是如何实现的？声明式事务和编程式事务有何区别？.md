### 问题

**Spring 的事务管理是如何实现的？声明式事务和编程式事务有何区别？**

---

#### 详细解释

Spring 提供了强大的事务管理支持，既可以对本地事务进行管理，也可以集成分布式事务。Spring 的事务管理主要通过 AOP（面向切面编程）实现，拦截方法调用，在方法前后自动开启、提交或回滚事务。

**常见的事务管理方式有两种：**

1. **声明式事务（推荐）**
   - 通过注解（如 `@Transactional`）或 XML 配置为方法或类声明事务属性。
   - Spring 自动在方法调用前开启事务，方法正常返回时提交事务，抛异常时回滚事务。
   - 适合大部分业务场景，简单易用，代码与事务控制解耦。

   ```java
   @Service
   public class OrderService {
       @Transactional
       public void createOrder() {
           // 业务逻辑，自动开启/提交/回滚事务
       }
   }
   ```

2. **编程式事务**
   - 通过 `TransactionTemplate` 或 `PlatformTransactionManager` 在代码中手动控制事务的开启、提交和回滚。
   - 适合需要更细粒度控制的复杂场景（如嵌套、多个事务交错）。
   ```java
   @Autowired
   private TransactionTemplate transactionTemplate;
   
   public void doInTransaction() {
       transactionTemplate.execute(status -> {
           // 业务逻辑
           return null;
       });
   }
   ```

**事务传播行为（Transaction Propagation）**
- Spring 支持多种事务传播机制，如 `REQUIRED`, `REQUIRES_NEW`, `NESTED`，决定当前有事务时新方法如何加入或新建事务。
- 例如：`REQUIRED`（默认）表示如果当前有事务就加入，没有就新建一个；`REQUIRES_NEW` 总是新建新事务，原事务挂起。

**典型场景举例：**
- 声明式事务：下单时涉及多个表（订单、库存、支付），用 `@Transactional` 保证数据一致性。
- 编程式事务：同一业务中部分代码需要在不同事务下运行时，手动管理。

---

#### 总结性回答（复习提示词）

> Spring 事务管理：声明式（@Transactional，推荐，自动控制）和编程式（TransactionTemplate，手动控制）。事务传播行为很重要。大多数业务用声明式，复杂场景用编程式。