### 问题

**什么是 Spring 的依赖注入（DI）？有哪些常用的依赖注入方式？**

---

#### 详细解释

**依赖注入（Dependency Injection, DI）**是 Spring 框架的核心思想之一。它指的是由 Spring 容器负责创建对象，并自动将对象所依赖的其他 Bean 注入进来，而不是让对象自己去查找或创建依赖对象。DI 实现了**控制反转（IoC）**，提升了代码的解耦性和可测试性。

**常见的依赖注入方式有三种：**

1. **构造器注入（Constructor Injection）**
   - 通过构造方法传递依赖。
   - 推荐用于强依赖、不可变依赖，便于单元测试。
   - Spring 5 推荐优先使用构造器注入。
   ```java
   @Component
   public class UserService {
       private final UserRepository userRepo;
       @Autowired  // 可省略
       public UserService(UserRepository userRepo) {
           this.userRepo = userRepo;
       }
   }
   ```

2. **Setter 方法注入（Setter Injection）**
   - 通过 setXxx() 方法注入依赖。
   - 适合可选依赖或有默认值的依赖。
   ```java
   @Component
   public class UserService {
       private UserRepository userRepo;
       @Autowired
       public void setUserRepository(UserRepository userRepo) {
           this.userRepo = userRepo;
       }
   }
   ```

3. **字段注入（Field Injection）**
   - 直接在成员变量上加 @Autowired 注解（不推荐，见下）。
   - 不利于单元测试、依赖不可见、可能导致循环依赖等问题。
   ```java
   @Component
   public class UserService {
       @Autowired
       private UserRepository userRepo;
   }
   ```

**推荐选择：**
- 构造器注入 > Setter 注入 > 字段注入

**场景举例：**
比如你有一个点餐系统，订单服务（OrderService）依赖于用户服务（UserService）和菜品服务（DishService）。通过依赖注入，Spring 自动帮你把 UserService 和 DishService 注入进 OrderService，你只需声明依赖关系即可。

---

#### 总结性回答（复习提示词）

> 依赖注入（DI）：Spring IoC 核心。构造器注入优先，Setter 适合可选依赖，字段注入不推荐。DI 解耦代码，便于测试和维护。