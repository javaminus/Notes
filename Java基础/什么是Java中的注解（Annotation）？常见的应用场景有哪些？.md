## 16. 什么是Java中的注解（Annotation）？常见的应用场景有哪些？

### 详细解释

**注解（Annotation）** 是 Java 5 引入的一种特殊语法，用于在代码中添加元数据。注解本身不会直接影响代码逻辑，但可被编译器、工具或运行时通过反射读取，用于实现配置和自动化等功能。

#### 常见应用场景
- **编译时检查**：如 `@Override`、`@SuppressWarnings`，帮助编译器检查代码正确性。
- **框架配置**：如 Spring 的 `@Autowired`、`@Controller`、JUnit 的 `@Test` 等，简化配置和依赖注入。
- **生成文档/代码**：如 `@Deprecated`、自定义注解配合注解处理器自动生成代码或文档。
- **运行时反射**：如 ORM 框架通过注解映射数据库表字段。

**通俗例子：**
```java
@Override
public String toString() { return "Hello"; }

@SuppressWarnings("unchecked")
List rawList = new ArrayList();

@Entity
public class User { @Id private Long id; }
```

#### 自定义注解
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotation {
    String value();
}
```

### 总结性提示词

> 注解用于为代码添加元数据，常见于编译检查、框架配置、自动化文档和运行时反射。支持自定义，便于自动化和解耦。