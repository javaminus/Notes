# @Async 注解简介

## 1. 作用

`@Async` 是 Spring 提供的异步方法执行注解。  
它允许你将某个方法变成**异步方法**，即调用该方法时不会阻塞当前线程，而是交由 Spring 的线程池异步执行，提升应用的并发能力和响应速度。

---

## 2. 基本用法

- 在配置类加上 `@EnableAsync` 开启异步功能。
- 在需要异步的方法上添加 `@Async` 注解（通常是 `@Service`、`@Component` 类中的 `public` 方法）。

**示例：**

```java
// 1. 启用异步
@Configuration
@EnableAsync
public class AsyncConfig {}

// 2. 使用 @Async
@Service
public class MailService {
    @Async
    public void sendMail(String address) {
        // 这里是异步发送邮件逻辑
        System.out.println("开始发送邮件..." + Thread.currentThread().getName());
    }
}

// 3. 调用
mailService.sendMail("test@example.com"); // 立即返回，邮件在后台线程异步发送
```

---

## 3. 工作原理

- Spring AOP 会为带有 `@Async` 注解的方法生成代理对象，方法调用时会将任务提交到线程池执行。
- 可以自定义线程池（实现 `AsyncConfigurer` 或定义 `@Bean` 的 `Executor`），否则使用默认线程池。

---

## 4. 方法返回值

- 可以返回 `void`，表示不关心结果。
- 可以返回 `Future<T>` 或 `CompletableFuture<T>`，获取异步结果或异常。

---

## 5. 注意事项

- `@Async` 只能用于 Spring 管理的 Bean 的 `public` 方法，且**自调用不会异步**。
- 适用于耗时、非核心业务（如发送邮件、短信、通知、日志等）。

---

## 6. 常见应用场景

- 发送邮件、短信、消息推送
- 文件上传、图片处理
- 第三方接口调用
- 报表导出等后台任务

---

## 7. 面试总结

> `@Async` 用于让方法异步执行，提升并发和响应能力，常用于耗时操作，需与 `@EnableAsync` 一起使用。



# @Async 面试常见追问及参考答案

---

## 1. @Async 的底层原理是什么？

**答：**  
- @Async 基于 Spring AOP 实现。Spring 会为带有 @Async 注解的方法生成代理对象，方法调用时会将任务提交到线程池中异步执行。
- 如果目标类实现了接口，使用 JDK 动态代理；否则用 CGLIB 代理。
- 代理对象负责将 @Async 方法的调用交由线程池，并立即返回主线程。

---

## 2. @Async 方法返回值有哪些支持？怎么获取异步结果？

**答：**  
- @Async 方法可以返回 void、Future<T>、CompletableFuture<T> 或 ListenableFuture<T>。
- 如果返回 Future/CompletableFuture，可以在主线程通过 get() 方法获取异步结果或异常（注意 get() 会阻塞）。

---

## 3. @Async 用于同类内部方法调用时会异步执行吗？

**答：**  
- 不会。和事务类似，**同类内部自调用不会经过 Spring 代理**，@Async 注解不会生效，方法会同步执行。

---

## 4. 如何自定义 @Async 使用的线程池？

**答：**  
- 可以通过实现 AsyncConfigurer 接口或在配置类中声明 @Bean public Executor taskExecutor()，并通过 @Async("beanName") 指定使用哪个线程池。
- 若未指定，默认使用 SimpleAsyncTaskExecutor（不推荐生产环境使用）。

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        return new ThreadPoolTaskExecutor();
    }
}
```

---

## 5. @Async 与 @Scheduled 有什么区别？

**答：**  
- @Async 用于让方法异步执行，由调用方触发，适合并发/解耦耗时操作；
- @Scheduled 用于定时任务，无需调用方触发，按设定时间自动执行。

---

## 6. @Async 如何处理异常？异常会抛给主线程吗？

**答：**  
- @Async 方法中异常默认不会抛到主线程，如果返回 Future/CompletableFuture，可以通过 get() 捕获；
- 可以实现 AsyncUncaughtExceptionHandler 统一处理未捕获的异步异常。

```java
@Override
public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setDefaultTimeout(5000);
    configurer.setTaskExecutor(taskExecutor());
}
```

---

## 7. @Async 是否支持事务？异步方法能用 @Transactional 吗？

**答：**  
- @Async 方法执行在新线程，与主线程事务隔离，**不能共享主线程事务**。
- 如果异步方法需要事务，需在异步方法上单独加 @Transactional，事务范围只覆盖异步逻辑。

---

## 8. 如何保证 @Async 方法的线程安全？

**答：**  
- 方法内部如有共享状态或可变成员变量，需考虑线程安全（如加锁、使用线程安全集合等）。
- 推荐无状态/局部变量，避免多线程数据冲突。

---

## 9. @Async 能否用于静态方法？

**答：**  
- 不能。Spring 无法为静态方法生成代理，因此 @Async 注解在静态方法上无效。

---

## 10. @Async 常见应用场景有哪些？

**答：**  
- 发送邮件、短信、消息推送
- 文件上传、图片处理
- 第三方接口调用
- 审计、日志、报表导出等异步任务

---

## 面试总结提示

- 明确 @Async 的适用条件和常见“失效”场景
- 熟悉线程池配置、异常处理、事务和线程安全等细节
- 能结合实际业务场景举例说明

---

---