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

# Spring AOP 面试常见追问与参考答案

---

## 1. Spring AOP 和 OOP（面向对象）有什么区别与联系？

**答：**  
- OOP（面向对象）关注“纵向”地封装数据和行为，把关注点分布在不同的类中；
- AOP（面向切面）关注“横向”地抽取公用逻辑，把跨越多个类的关注点（如日志、事务、安全等）抽出来，减少重复，提升解耦性；
- 二者结合，让业务代码更专注于核心逻辑，通用逻辑通过切面统一管理。

---

## 2. Spring AOP 的底层原理是什么？

**答：**  
- Spring AOP 主要通过动态代理实现：
  - 如果目标实现了接口，默认使用 JDK 动态代理（代理接口）。
  - 如果没有接口，则用 CGLIB 生成目标类的子类代理。
- AOP 通过 ProxyFactoryBean、Advisor、Advice、Proxy 等组合实现切面织入。
- 注解方式（@Aspect）底层是通过 AspectJ 的切面表达式和 Spring AOP 代理结合实现。

---

## 3. Spring AOP 和 AspectJ 有什么区别？

**答：**  
- Spring AOP 是 Spring 自带的基于代理的 AOP 框架，只能拦截 Spring 容器管理的 Bean 的方法。
- AspectJ 是一个功能更强大的 AOP 框架，支持编译期、类加载期、运行期织入，支持更多类型的连接点（如构造器、字段等）。
- Spring AOP 通常用在业务开发，AspectJ 适合需要更强大切点表达能力的场景。

---

## 4. 常用的 Advice 类型有哪些？各自适用场景？

**答：**  
- @Before：方法执行前执行，适合做权限、日志预处理等。
- @After：方法执行后（无论是否异常）执行，适合资源释放等。
- @AfterReturning：方法正常返回后执行，适合结果处理。
- @AfterThrowing：方法抛出异常后执行，适合异常日志、报警等。
- @Around：方法执行前后都可操作，最灵活，常用于统计耗时、事务、权限等。

---

## 5. AOP 会拦截 private 方法吗？

**答：**  
- Spring AOP 只能拦截 public/protected 方法，且只能代理 Spring 容器管理的 Bean 的方法，private 方法无法被 AOP 切面拦截。

---

## 6. AOP 会不会影响性能？怎么优化？

**答：**  
- AOP 通过动态代理增加了一定的性能开销，但对于大多数业务场景影响很小；
- 如果切面逻辑复杂或织入面广，应注意切点表达式的范围，避免不必要的代理，减少切面的复杂度。

---

## 7. 如果想要统计接口耗时，AOP 切点如何设计？

**答：**  
- 可以用 @Around 通知，切点表达式选择需要统计的方法范围，如所有 Controller 层接口。

**举例：统计所有 Controller 下的方法耗时**

```java
@Aspect
@Component
public class TimeLogAspect {
    @Around("execution(* com.example.controller..*(..))")
    public Object logTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;
        System.out.println("执行方法：" + pjp.getSignature() + "，耗时：" + duration + "ms");
        return result;
    }
}
```
- `execution(* com.example.controller..*(..))` 表示切所有 controller 包及子包下的方法。
- `@Around` 可在方法前后执行逻辑，非常适合统计耗时、监控等场景。

如果你只想统计某一个接口（如某个 Controller 的某个方法）的耗时，可以将切点表达式设计为只匹配该方法。常用方式包括：

---

## 1. execution 表达式指定方法

**假设：**  
- 你的 Controller 类是 `com.example.controller.OrderController`
- 要统计的方法为 `createOrder`

**切点写法：**
```java
@Aspect
@Component
public class TimeLogAspect {
    @Around("execution(* com.example.controller.OrderController.createOrder(..))")
    public Object logTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;
        System.out.println("执行方法：" + pjp.getSignature() + "，耗时：" + duration + "ms");
        return result;
    }
}
```
- `execution(* 包名.类名.方法名(..))` 精确指定了要拦截的方法。

---

## 2. 使用自定义注解，只拦截带注解的方法

如果多个方法需要灵活控制，可自定义注解如 `@TimeLog`，只统计打了注解的方法：

**定义注解：**
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeLog {}
```

**方法打注解：**
```java
@TimeLog
public void createOrder(...) { ... }
```

**切点写法：**
```java
@Aspect
@Component
public class TimeLogAspect {
    @Around("@annotation(com.example.annotation.TimeLog)")
    public Object logTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;
        System.out.println("执行方法：" + pjp.getSignature() + "，耗时：" + duration + "ms");
        return result;
    }
}
```

---

## 面试总结

- 精准统计单个接口耗时，推荐用 `execution` 表达式指定方法。
- 若需灵活配置，推荐自定义注解方式，便于后续扩展。

---

---

## 8. 如何避免 AOP 切面“失效”？

**答：**
- 切面只对 Spring 管理的 Bean 有效，直接 new 的对象不会被代理；
- 同类内部方法调用（自调用）不会被 AOP 拦截，因为代理对象不会介入自身内部调用；
- 切面表达式必须正确匹配目标方法（包括包名、参数等）。

---

## 9. 实际开发中常见 AOP 应用场景还有哪些？

**答：**
- 日志记录（接口调用、异常、操作日志）
- 事务管理（如 @Transactional）
- 权限校验（如注解式权限控制）
- 缓存处理（如自动缓存结果）
- 接口限流/幂等性
- 审计追踪

---

## 面试小结提示

- 牢记核心术语（Aspect、Advice、Pointcut、JoinPoint、Weaving）
- 能区分代理方式（JDK/CGLIB）、注解用法；
- 结合实际业务举例，如日志、事务、性能监控等。

---