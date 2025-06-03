### 问题

**Spring 中的事件机制（ApplicationEvent）是什么？有哪些常见应用场景？**

---

#### 详细解释

Spring 提供了内置的事件发布/订阅（观察者模式）机制，允许 Bean 之间松耦合地进行消息传递和业务解耦。

**核心组成：**
- **事件（Event）**：任何继承自 `ApplicationEvent` 的对象。
- **事件发布者（Publisher）**：通常为 ApplicationContext，可通过 `publishEvent()` 方法发送事件。
- **事件监听器（Listener）**：实现 `ApplicationListener` 接口或者用 `@EventListener` 注解的方法，处理特定类型的事件。

**工作流程：**
1. 你定义一个事件类（继承自 `ApplicationEvent`，或直接为普通 POJO）。
2. 在业务流程中，通过 `applicationContext.publishEvent(event)` 发布事件。
3. 有关心此事件的监听器会自动收到并处理它。

**代码示例：**
```java
// 1. 事件定义
public class UserRegisterEvent extends ApplicationEvent {
    private final String username;
    public UserRegisterEvent(Object source, String username) {
        super(source);
        this.username = username;
    }
    public String getUsername() { return username; }
}

// 2. 事件发布
@Autowired
private ApplicationContext applicationContext;
applicationContext.publishEvent(new UserRegisterEvent(this, "javaminus"));

// 3. 事件监听
@Component
public class UserRegisterListener implements ApplicationListener<UserRegisterEvent> {
    @Override
    public void onApplicationEvent(UserRegisterEvent event) {
        System.out.println("新用户注册：" + event.getUsername());
    }
}
// 或用注解
@EventListener
public void handleUserRegister(UserRegisterEvent event) { ... }
```

**典型应用场景：**
- 解耦业务流程：比如用户注册后，自动发送邮件、推送消息等处理逻辑分离成独立监听器。
- 框架扩展：如 Spring 自身会发布 ContextRefreshedEvent、ContextClosedEvent 等生命周期事件。
- 埋点、日志、异步任务等。

---

#### 总结性回答（复习提示词）

> Spring 事件机制（ApplicationEvent）：应用内异步/同步解耦通信，事件发布者 publish，监听器监听处理，常用于业务解耦、扩展、异步任务等场景。