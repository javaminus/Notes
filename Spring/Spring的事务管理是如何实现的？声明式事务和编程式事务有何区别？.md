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

# Spring 事务管理 面试常见追问及参考答案

---

## 1. @Transactional 的实现原理是什么？

**答：**
- @Transactional 本质是通过 Spring AOP 实现的。Spring 会为被 @Transactional 标注的方法创建代理对象，方法调用时自动开启、提交或回滚事务。
- 底层主要用动态代理（JDK Proxy 或 CGLIB），在方法执行前后通过 PlatformTransactionManager 控制事务。

---

## 2. @Transactional 注解可以加在哪些地方？类和方法哪个优先级高？

**答：**
- 可以加在类或方法上。加在类上表示所有 public 方法都开启事务，加在方法上只对该方法生效。
- 方法上的 @Transactional 优先级高于类上的设置。

---

## 3. @Transactional 默认只对哪些异常回滚？如何自定义？

**答：**
- 默认情况下，只对运行时异常（RuntimeException 及其子类）和 Error 回滚，受检异常（checked exception）不会回滚。
- 可通过 @Transactional 的 rollbackFor、noRollbackFor 属性自定义哪些异常回滚或不回滚，例如：  
  `@Transactional(rollbackFor = Exception.class)`

---

## 4. 在什么情况下声明式事务会失效？

**答：**
- 非 public 方法不会被代理（事务失效）。

- 同类内部方法调用（自调用）不会经过代理（事务失效）。

- > # Spring 事务中的“同类内部方法调用不会经过代理（事务失效）”是什么意思？
  >
  > ## 现象描述
  >
  > 在 Spring 的声明式事务管理（如 `@Transactional` 注解）下，**如果一个类中的方法A调用了同一个类中的方法B（而B方法上有事务注解）**，这种“同类内部方法调用”**不会触发事务**，即B方法上的事务不会生效。
  >
  > ---
  >
  > ## 为什么会这样？
  >
  > Spring 的声明式事务是基于AOP代理实现的，只有**通过代理对象调用**的方法才能被AOP拦截，进而实现事务控制。
  >
  > - **外部调用**（如通过Spring容器获取的Bean对象调用）：会被代理拦截，事务生效。
  > - **内部自调用**（this.xxx()）：直接走本类方法，不经过Spring代理，事务注解失效。
  >
  > ---
  >
  > ## 代码举例
  >
  > ```java
  > @Service
  > public class UserService {
  > 
  >     // 方法A没有事务
  >     public void methodA() {
  >         // 直接调用本类带事务的方法
  >         methodB(); // 事务不会生效！
  >     }
  > 
  >     @Transactional
  >     public void methodB() {
  >         // 期望加事务，实际不会生效
  >     }
  > }
  > ```
  > **在上面例子中，methodA调用methodB时，methodB的@Transactional不会生效。**
  >
  > ---
  >
  > ## 正确用法
  >
  > - 通过Spring容器获取的Bean对象调用带有事务的方法，事务才会生效。
  > - 若确实需要自调用，请通过AOP暴露代理对象（如 `AopContext.currentProxy()`），或者重构代码让事务方法由外部类调用。
  >
  > ```java
  > // 通过AopContext获取代理对象调用
  > ((UserService)AopContext.currentProxy()).methodB();
  > ```
  >
  > ---
  >
  > ## 面试总结
  >
  > - Spring声明式事务只能拦截“代理对象”方法调用，**同类内部方法自调用不会被拦截，事务失效**。
  > - 这是Spring AOP/事务机制的常见“坑”，实际开发和面试都需重点注意。
  >
  > ---

- 异常被捕获未抛出，事务不会回滚。(受检异常)

- > # Spring 事务：异常被捕获未抛出，事务不会回滚，是什么意思？
  >
  > ## 现象描述
  >
  > 在 Spring 声明式事务（@Transactional）中，**只有当方法抛出未被捕获的异常时，事务才会自动回滚**（默认只对 RuntimeException 回滚）。  
  > **如果你在方法内部捕获了异常，但没有再抛出，Spring 认为方法执行“正常”，事务会被提交，而不会回滚。**
  >
  > ---
  >
  > ## 代码举例
  >
  > ```java
  > @Transactional
  > public void updateData() {
  >     try {
  >         // 这里发生异常
  >         int x = 1 / 0;
  >     } catch (Exception e) {
  >         // 异常被捕获，没有再抛出
  >         System.out.println("异常被捕获：" + e.getMessage());
  >     }
  >     // 此时方法正常结束，事务会被提交，不会回滚
  > }
  > ```
  > **结果：** 即使发生了异常，只要你 catch 住没有抛出，事务不会回滚。
  >
  > ---
  >
  > ## 正确做法
  >
  > - 如果希望事务回滚，**捕获异常后需要手动抛出**（throw），或者不要捕获，让异常自动抛出。
  > - 或者在 catch 块里手动调用 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly() 标记事务回滚。
  >
  > ```java
  > @Transactional
  > public void updateData() {
  >     try {
  >         int x = 1 / 0;
  >     } catch (Exception e) {
  >         // 需要手动抛出，事务才会回滚
  >         throw e;
  >     }
  > }
  > ```
  > 或者：
  >
  > ```java
  > import org.springframework.transaction.interceptor.TransactionAspectSupport;
  > 
  > @Transactional
  > public void updateData() {
  >     try {
  >         int x = 1 / 0;
  >     } catch (Exception e) {
  >         TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
  >     }
  > }
  > ```
  >
  > ---
  >
  > ## 面试总结
  >
  > - Spring 事务默认只对未捕获的运行时异常自动回滚。
  > - **异常被捕获后未抛出，Spring 认为业务正常，事务会提交。**
  > - 需要回滚时，注意异常处理方式。
  >
  > ---

---

## 5. 多线程/异步方法下事务还能生效吗？

**答：**
- 事务是和当前线程绑定的，多线程/异步（如 @Async）方法不会自动传递事务，需特殊处理。
- 如需多线程事务，需手动传递或新建事务。

---

## 6. 编程式事务有哪些应用场景？和声明式事务能混用吗？

**答：**
- 复杂业务需要多事务嵌套、手动控制提交/回滚时，使用编程式事务（如 TransactionTemplate）。
- 可以和声明式事务混用，但需注意事务传播属性和嵌套事务的管理。

---

## 7. 事务传播行为有哪些？常见用法？

**答：**
- 常见传播行为有：
  - REQUIRED（默认）：当前有事务就加入，没有就新建。
  - REQUIRES_NEW：总是新建事务，原事务挂起。
  - NESTED：嵌套事务，支持部分回滚。
- 用法：如一个主流程用 REQUIRED，子流程需隔离时用 REQUIRES_NEW。

---

## 8. 分布式事务与本地事务有何区别？Spring 如何支持分布式事务？

**答：**
- 本地事务仅涉及单数据源，分布式事务涉及多个服务/数据库。
- Spring 可通过 JTA（如 Atomikos）、Seata、消息队列等方式支持分布式事务，但实现和性能更复杂。

---

## 9. 如何避免事务中的“脏读、不可重复读、幻读”？如何设置隔离级别？

**答：**
- 可通过 @Transactional(isolation = Isolation.READ_COMMITTED) 设置事务隔离级别。
- Spring 支持四种标准隔离级别（DEFAULT/READ_UNCOMMITTED/READ_COMMITTED/REPEATABLE_READ/SERIALIZABLE），可根据业务场景选择。

---

## 10. 数据库连接池和事务管理有关系吗？

**答：**
- 有关系。事务需基于数据库连接实现，连接池要支持事务相关属性（如自动提交、回滚），Spring 会通过 DataSource 获取连接并管理事务。

---

## 面试总结提示

- 理解 @Transactional 的底层原理与失效场景；
- 熟悉声明式与编程式事务的对比和适用场景；
- 能举例说明事务传播、异常回滚、隔离级别等核心点。

---