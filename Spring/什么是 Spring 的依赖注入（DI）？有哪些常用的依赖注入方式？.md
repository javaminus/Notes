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

# Spring 依赖注入（DI）面试追问及参考答案

---

## 1. 构造器注入、Setter 注入和字段注入的优缺点？

**答：**
- 构造器注入
  - 优点：依赖不可变，强依赖显式声明，有助于单元测试和防止循环依赖。
  - 缺点：依赖多时构造方法参数多，可读性下降。
- Setter 注入
  - 优点：依赖可选，灵活性高；便于后期修改依赖。
  - 缺点：依赖可能未初始化，存在“部分注入”风险。
- 字段注入
  - 优点：代码简洁，无需 setter。
  - 缺点：不利于测试，依赖不可见，易导致循环依赖；不推荐生产使用。

---

## 2. @Autowired 注解可以用在哪些地方？支持哪些依赖类型？

**答：**
- 可以用于构造方法、setter 方法、字段、普通方法、参数。
- 支持按类型自动注入（byType），可结合 @Qualifier 指定 Bean 名称实现按名注入。

---

## 3. 如何处理多个同类型 Bean 的注入冲突？

**答：**
- 可用 @Qualifier("beanName") 指定具体 Bean。
- 或用 @Primary 标记优先注入的 Bean。

---

## 4. @Autowired 和 @Resource 有什么区别？

**答：**
- @Autowired（Spring 提供）：按类型优先，支持 @Qualifier 细粒度指定。
- @Resource（JDK 标准）：默认按名称注入，找不到再按类型。

---

## 5. Spring 如何解决依赖注入时的循环依赖？

**答：**
- 对于 singleton Bean 的 setter/字段注入，Spring 通过三级缓存提前暴露“半成品”对象解决循环依赖。
- 构造器注入和 prototype Bean 不支持循环依赖。

---

## 6. 注入的 Bean 如果找不到会怎么样？如何处理可选依赖？

**答：**
- @Autowired 默认 required=true，找不到 Bean 会报错。
- 可设置 @Autowired(required=false) 或用 Optional<T> 类型，处理可选依赖。

---

## 7. 依赖注入的底层实现原理是什么？

**答：**
- 容器解析 BeanDefinition，根据依赖关系通过反射创建对象并注入依赖。
- 使用 BeanPostProcessor 处理注解（如 AutowiredAnnotationBeanPostProcessor）。

---

## 8. Spring 支持哪些依赖注入配置方式？

**答：**
- 注解方式（@Autowired、@Resource、@Inject）
- XML 配置（<property>、<constructor-arg>）
- JavaConfig（@Bean 方法参数注入）

---

## 9. @Autowired 注解可以省略吗？有哪些前提？

**答：**
- 如果类只有一个有参构造器，@Autowired 可以省略，Spring 自动注入参数。

---

## 10. 如何在单元测试中注入依赖的 Bean？

**答：**
- 结合 @RunWith(SpringRunner.class) + @SpringBootTest/@ContextConfiguration
- 直接用 @Autowired 注入 Bean 到测试类字段

---

## 面试总结提示

- 熟悉三种依赖注入方式优缺点和适用场景
- 能区分 @Autowired、@Resource、@Qualifier、@Primary 等注解作用
- 理解依赖注入底层原理及循环依赖解决机制

---