## **Spring 的 IOC（Inversion of Control，控制反转）**

### **1. 什么是 IOC？**

**IOC（控制反转）** 是一种**设计思想**，用于管理对象的依赖关系。Spring 通过 **IOC 容器** 负责创建、管理和注入对象，而不是由代码手动创建对象。

**核心思想：**
 👉 **传统方式**（手动创建对象）：

```
class A {
    private B b;
    
    public A() {
        this.b = new B();  // 手动创建对象
    }
}
```

👉 **IOC 方式**（容器管理对象）：

```
@Component
class A {
    @Autowired
    private B b;  // 依赖由 Spring 容器自动注入
}
```

**好处：**

- **解耦合**（对象的创建与使用分离）
- **更容易维护和测试**
- **便于扩展**

------

### **2. IOC 容器**

Spring 提供了 **BeanFactory** 和 **ApplicationContext** 作为 IOC 容器：

| **IOC 容器**           | **特点**                                          |
| ---------------------- | ------------------------------------------------- |
| **BeanFactory**        | 轻量级，延迟加载（Lazy Load），适用于资源受限环境 |
| **ApplicationContext** | 强大，支持事件监听、国际化、AOP、事务管理等       |

**常见 ApplicationContext 实现类：**

- `ClassPathXmlApplicationContext`（基于 XML）
- `AnnotationConfigApplicationContext`（基于 JavaConfig）
- `FileSystemXmlApplicationContext`（从文件系统加载 XML）

**示例（基于 Java 配置）：**

```
@Configuration
@ComponentScan("com.example")
public class AppConfig {}

public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    A a = context.getBean(A.class);
}
```

------

### **3. Spring IOC 的 Bean 管理**

Spring IOC 主要负责：

1. **Bean 的创建**（实例化）
2. **Bean 的依赖注入**
3. **Bean 的生命周期管理**
4. **Bean 的作用域**

------

### **4. 依赖注入（DI, Dependency Injection）**

依赖注入是 IOC 的核心，Spring 通过 **XML 配置、注解、JavaConfig** 来注入 Bean。

#### **1️⃣ 构造函数注入**

```
@Component
class A {
    private B b;

    @Autowired  // 自动注入 B
    public A(B b) {
        this.b = b;
    }
}
```

🔹 **适用于：** 依赖是**必须**的，保证不可变性。

------

#### **2️⃣ Setter 方法注入**

```
@Component
class A {
    private B b;

    @Autowired
    public void setB(B b) {
        this.b = b;
    }
}
```

🔹 **适用于：** 依赖是**可选的**，或者对象需要后续修改。

------

#### **3️⃣ 字段注入**

```
@Component
class A {
    @Autowired
    private B b;
}
```

🔹 **优点：** 代码简洁，避免冗余的 setter 方法
 🔹 **缺点：** **不利于单元测试**，无法手动注入

------

### **5. Bean 作用域**

Spring 中 Bean 的作用域决定了 Bean 的生命周期：

| 作用域              | 说明                                           |
| ------------------- | ---------------------------------------------- |
| `singleton`（默认） | **全局单例**，IOC 容器中**只创建一次**         |
| `prototype`         | **每次获取 Bean 都创建新对象**                 |
| `request`           | **每个 HTTP 请求创建一个 Bean**（仅 Web 应用） |
| `session`           | **每个 HTTP 会话（Session）创建一个 Bean**     |
| `application`       | **整个 Web 应用共享同一个 Bean**               |

**示例：**

```
@Component
@Scope("prototype")  // 每次获取都会创建新对象
class A {}
```

------

### **6. Bean 生命周期**

Spring 管理 Bean 生命周期的 5 个阶段：

1. **实例化**（调用构造方法）
2. **依赖注入**（执行 `@Autowired` 或 XML 配置）
3. **初始化**（调用 `@PostConstruct` 或 `InitializingBean`）
4. **使用**
5. **销毁**（调用 `@PreDestroy` 或 `DisposableBean`）

**示例：**

```
@Component
class A {
    @PostConstruct
    public void init() {
        System.out.println("Bean 初始化");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean 销毁");
    }
}
```

------

### **7. IOC 运行机制**

1. **Spring 启动**时，创建 IOC 容器（`ApplicationContext`）。
2. **扫描组件**（`@ComponentScan`），注册 Bean 定义（`BeanDefinition`）。
3. **实例化 Bean**（默认单例模式）。
4. **执行依赖注入**（`@Autowired`）。
5. **调用初始化方法**（`@PostConstruct`）。
6. **Bean 进入可用状态**。
7. **应用关闭时销毁 Bean**（`@PreDestroy`）。

------

### **8. 典型面试题**

✅ **IOC 和 DI 的区别？**
 ➡ **IOC 是思想，DI 是实现方式**（IOC 依赖 DI 实现）。

✅ **Spring 如何实现 IOC？**
 ➡ 通过 **ApplicationContext** 作为 IOC 容器，解析 **BeanDefinition**，利用 **反射创建 Bean** 并执行依赖注入。

✅ **@Autowired 的底层原理？**
 ➡ 依赖 **BeanPostProcessor**（如 `AutowiredAnnotationBeanPostProcessor`），解析 `@Autowired` 并注入 Bean。

✅ **Spring Bean 默认是什么作用域？**
 ➡ `singleton`，即 **全局唯一实例**。

✅ **@PostConstruct 和 @PreDestroy 作用？**
 ➡ **@PostConstruct：Bean 初始化时执行**
 ➡ **@PreDestroy：Bean 销毁前执行**

------

### **9. 总结**

1. **IOC 是 Spring 的核心**，让对象的创建和管理交给 Spring 容器，降低耦合。
2. **ApplicationContext 是主要的 IOC 容器**，可以管理 Bean 的生命周期和作用域。
3. **依赖注入（DI）有 3 种方式**：构造器注入、Setter 注入、字段注入。
4. **Bean 作用域决定了对象的生命周期**（`singleton`、`prototype` 等）。
5. **Spring 通过 @PostConstruct 和 @PreDestroy 管理 Bean 生命周期**。

🔥 **理解 IOC，才能更好地掌握 Spring AOP、事务管理、Spring Boot！**



# Spring IOC（控制反转）相关面试常见追问及参考答案

---

## 1. IOC 和 DI 的区别与联系？

**答：**  
- IOC（Inversion of Control，控制反转）是一种设计思想，指对象的创建与依赖由容器负责。
- DI（Dependency Injection，依赖注入）是实现 IOC 的主要方式，即将依赖“注入”而不是“查找”。
- 关系：IOC 是目标，DI 是实现手段。

---

## 2. Spring 支持哪些依赖注入方式？各自优缺点？

**答：**  
- 构造函数注入：依赖不可变，适合“必须依赖”，利于单元测试。
- Setter 注入：依赖可选，灵活性高，适合后续可变更依赖。
- 字段注入：代码简洁但不利于测试和扩展（不推荐于严谨项目）。

---

## 3. BeanFactory 和 ApplicationContext 有哪些核心区别？

**答：**  
- BeanFactory 只提供基础 IOC 容器功能，延迟加载，适合底层或资源受限场景。
- ApplicationContext 功能更全，支持事件、AOP、国际化等，实际开发中几乎都用 ApplicationContext。

---

## 4. Spring IOC 容器的启动流程是什么？

**答：**  
1. 读取配置（XML/注解/JavaConfig）
2. 解析为 BeanDefinition
3. 实例化 Bean（根据作用域）
4. 依赖注入
5. 初始化回调
6. Bean 可用
7. 容器关闭时执行销毁回调

---

## 5. @Autowired、@Resource、@Inject 有什么区别？

**答：**  
- @Autowired（Spring）：按类型注入，支持 required=false，可与 @Qualifier 配合按名称。
- @Resource（JDK标准）：默认按名称，找不到再按类型。
- @Inject（JSR-330）：功能类似 @Autowired，但无 required 属性。

---

## 6. Bean 作用域有哪些？如何自定义作用域？

**答：**  
- 常见作用域：singleton、prototype、request、session、application、websocket。
- 自定义作用域：实现 org.springframework.beans.factory.config.Scope 接口，并注册到容器。

---

## 7. Spring 如何管理 Bean 的生命周期？有哪些扩展点？

**答：**  
- 生命周期包括：实例化、依赖注入、初始化、销毁。
- 扩展点：@PostConstruct/@PreDestroy、InitializingBean/DisposableBean、BeanPostProcessor、BeanFactoryPostProcessor。

---

## 8. 依赖注入时如何处理循环依赖？

**答：**  
- 单例 Bean 的 setter/属性注入循环依赖，Spring 通过三级缓存机制解决。
- 构造器注入和 prototype Bean 的循环依赖无法自动解决。

---

## 9. 如何在 IOC 容器中获取 Bean？有哪些方式？

**答：**  
- 推荐依赖注入（@Autowired、@Resource）。
- 也可通过 ApplicationContext#getBean(Class/Name) 获取，但耦合度较高。

---

## 10. Spring IOC 容器能否管理非 Spring 创建的对象？如何处理？

**答：**  
- 不能自动管理。若需管理，需要用 @Bean/@Component 或手动注册到容器。
- 可通过 ApplicationContextAware 或 BeanFactoryAware 获取容器引用，动态注册 Bean。

---

## 11. @ComponentScan 的原理和作用是什么？

**答：**  
- 用于扫描包下的 @Component、@Service、@Repository、@Controller 等注解 Bean，自动注册到容器。
- 底层通过类路径扫描机制实现。

---

## 12. 如何在单元测试中注入 IOC 容器的 Bean？

**答：**  
- 可用 @RunWith(SpringRunner.class) + @SpringBootTest/@ContextConfiguration
- 也可用 @Autowired 注入测试类成员变量。

---

## 13. Bean 的懒加载（@Lazy）有什么作用？适用场景？

**答：**  
- @Lazy 延迟 Bean 的实例化，首次使用时才创建。
- 适用于重量级资源、启动优化、解决部分循环依赖等。

---

## 14. Spring Bean 的默认作用域是什么？如何修改？

**答：**  
- 默认是 singleton。
- 可用 @Scope("prototype") 等注解或 XML 配置修改。

---

## 15. Spring IOC 容器如何支持配置文件（如 application.properties）中的属性注入？

**答：**  
- 通过 @Value、@ConfigurationProperties 等注解，将配置文件属性注入 Bean 字段或方法。

---

## 面试总结提示

- 理解 IOC/DI 的思想与实现方式
- 熟悉容器类型、Bean 管理流程、生命周期、作用域
- 掌握依赖注入、配置、Bean 扩展点和常见注解用法
- 能回答实际开发中 IOC 使用与原理细节

---