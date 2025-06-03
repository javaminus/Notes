### 问题

**Spring 的 AOP（面向切面编程）是什么？有哪些常用的应用场景？**

---

#### 详细解释

AOP（Aspect Oriented Programming，面向切面编程）是 Spring 的一大核心特性。它可以让我们在不修改源代码的情况下，将一些通用功能（如日志、安全、事务等）“切入”到业务逻辑的指定点位上，实现横向复用和关注点分离。  

**Spring AOP 的主要概念：**
- **切面（Aspect）**：横切关注点的模块，如日志、事务。
- **连接点（JoinPoint）**：程序执行的特定点（如方法调用）。
- **通知（Advice）**：切面在连接点上的具体动作，如前置、后置、异常通知。
- **切点（Pointcut）**：定义 Advice 应该应用到哪些 JoinPoint。
- **目标对象（Target）**：被代理的对象。
- **织入（Weaving）**：将切面应用到目标对象的过程。

**Spring 常用的 AOP 实现方式：**
- 基于代理（JDK 动态代理、CGLIB）
- 通过注解（@Aspect, @Before, @After, @Around 等）

**经典应用场景：**
- 日志记录（如接口调用日志、异常日志）
- 事务管理（如 @Transactional）
- 权限控制（如 @PreAuthorize）
- 性能监控、缓存、审计等

**通俗例子：**
- 比如你开发一个商城系统，所有下单方法都要记录操作日志。用 AOP，只需定义一个日志切面，对所有下单方法自动织入，无需每个业务方法里都手写记录日志的代码。

**简单代码示例：**
```java
@Aspect
@Component
public class LogAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        System.out.println("调用方法：" + joinPoint.getSignature().getName());
    }
}
```

---

#### 总结性回答（复习提示词）

> Spring AOP：面向切面编程，横切关注点（如日志、事务、安全）自动织入方法执行，提升复用和解耦。常见注解 @Aspect、@Before、@After、@Around。