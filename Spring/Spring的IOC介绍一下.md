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