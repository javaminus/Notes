当然可以！下面详细讲解Spring Bean初始化过程中常用的三种方式：`@PostConstruct`、`afterPropertiesSet`、`init-method`。

---

## 一、@PostConstruct

### 1. 定义
- `@PostConstruct`是Java标准注解（JSR-250），方法级别的注解。
- 用于**Bean实例化并依赖注入完成后自动执行**的方法。
- 方法签名：无参数，返回值为void。

### 2. 特点
- 适合初始化一些资源、数据等，业务代码与Spring高度解耦。
- 推荐使用，优先级最高。

### 3. 示例

```java
@Component
public class DemoBean {
    @PostConstruct
    public void init() {
        System.out.println("初始化 - @PostConstruct 方法被调用");
    }
}
```

---

## 二、afterPropertiesSet（实现InitializingBean接口）

### 1. 定义
- `InitializingBean`是Spring提供的接口，包含一个`afterPropertiesSet()`方法。
- 如果Bean实现了该接口，Spring会在依赖注入后自动调用此方法。

### 2. 特点
- 只能有一个实现（即只能有一个afterPropertiesSet方法）。
- 与Spring强耦合，不推荐优先使用，但有些老项目会用。

### 3. 示例

```java
@Component
public class DemoBean implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        System.out.println("初始化 - afterPropertiesSet() 被调用");
    }
}
```

---

## 三、init-method（XML/注解中指定）

### 1. 定义
- 在XML配置Bean时可以指定`init-method`，或在`@Bean`注解中用`initMethod`属性**指定初始化方法名**。
- Spring在Bean初始化阶段会自动调用此方法。

### 2. 特点

- 方法名可以自定义。
- 不与Spring或Java标准强耦合，适合第三方类无法添加注解或实现接口时使用。

### 3. 示例

#### XML方式

```xml
<bean id="demoBean" class="com.example.DemoBean" init-method="customInit"/>
```

#### 注解方式

```java
@Bean(initMethod = "customInit")
public DemoBean demoBean() {
    return new DemoBean();
}
```

```java
public class DemoBean {
    public void customInit() {
        System.out.println("初始化 - init-method 被调用");
    }
}
```

---

## 四、三者执行顺序

- 三者可以同时存在，执行顺序为：  
  **@PostConstruct → afterPropertiesSet → init-method**
- Spring框架会自动依次调用这三种方法。

---

## 五、适用场景对比

| 方式               | 解耦性       | 适用场景                                       |
| ------------------ | ------------ | ---------------------------------------------- |
| @PostConstruct     | 强（推荐）   | 普遍适用，尤其是自定义Bean                     |
| afterPropertiesSet | 与Spring耦合 | 仅用于Spring Bean且需要统一初始化入口          |
| init-method        | 最灵活       | 适合无法修改源码（如第三方类）时指定初始化方法 |

---

## 六、完整代码例子

```java
@Component
public class DemoBean implements InitializingBean {
    @PostConstruct
    public void postConstructInit() {
        System.out.println("@PostConstruct 方法执行");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("afterPropertiesSet 方法执行");
    }

    public void customInit() {
        System.out.println("init-method 方法执行");
    }
}

// 配置
@Bean(initMethod = "customInit")
public DemoBean demoBean() {
    return new DemoBean();
}
```
**输出顺序：**
```
@PostConstruct 方法执行
afterPropertiesSet 方法执行
init-method 方法执行
```

---

如需源码层面的详细流程或更深入的场景说明，欢迎继续追问！