### 问题

**Spring 中如何实现事件发布与监听机制？**

---

#### 详细解释

Spring 框架内置了完整的**事件发布-监听机制（Application Event）**，可用于实现松耦合的模块通信。其核心思想是：一个模块发布事件，其他模块通过监听器自动响应，无需直接调用。

**工作流程：**
1. **定义事件类**  
   事件需要继承 `ApplicationEvent`，可以携带自定义数据。
2. **发布事件**  
   通过 `ApplicationEventPublisher`（通常直接注入，也可以通过 `ApplicationContext`）发布事件。
3. **监听事件**  
   实现 `ApplicationListener` 接口，或使用 `@EventListener` 注解监听指定类型的事件。

**常见场景举例：**
- 用户注册后自动发送欢迎邮件（注册模块发布事件，邮件模块监听处理）
- 操作日志记录、异步任务通知、缓存刷新等

**代码示例：**

1. 定义事件
   ```java
   public class UserRegisterEvent extends ApplicationEvent {
       private final String username;
       public UserRegisterEvent(Object source, String username) {
           super(source);
           this.username = username;
       }
       public String getUsername() { return username; }
   }
   ```
2. 发布事件
   ```java
   @Service
   public class UserService {
       @Autowired
       private ApplicationEventPublisher publisher;
       public void register(String username) {
           // 业务逻辑...
           publisher.publishEvent(new UserRegisterEvent(this, username));
       }
   }
   ```
3. 监听事件
   ```java
   @Component
   public class WelcomeEmailListener {
       @EventListener
       public void onUserRegister(UserRegisterEvent event) {
           System.out.println("发送欢迎邮件给：" + event.getUsername());
       }
   }
   ```

---

#### 总结性回答（复习提示词）

> Spring 事件机制：发布-监听模式，解耦模块通信。发布用 ApplicationEventPublisher，监听用 @EventListener 或 ApplicationListener。常用于通知、日志、异步等场景。

