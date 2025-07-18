### 问题

**如何在 Spring 中实现自定义注解？自定义注解一般有哪些使用场景？**

---

#### 详细解释

Spring 支持开发者自定义注解，并结合 AOP、Bean 后处理器等实现很多高级功能。自定义注解常用于解耦、统一处理、代码增强等场景。

#### 自定义注解的实现步骤

1. **定义注解**
   - 使用 `@interface` 关键字，配合元注解（如 `@Target`、`@Retention`）。
   - 常见元注解：
     - `@Target`：指定注解可用于类、方法或字段等。
     - `@Retention`：指定注解的保留策略（如 RUNTIME）。

   ```java
   @Target(ElementType.METHOD)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface LogOperation {
       String value() default "";
   }
   ```

2. **实现注解处理逻辑**
   - 结合 Spring AOP 编写切面（@Aspect），拦截使用了自定义注解的方法或类，实现统一处理逻辑。

   ```java
   @Aspect
   @Component
   public class LogAspect {
       @Around("@annotation(logOperation)")
       public Object log(ProceedingJoinPoint pjp, LogOperation logOperation) throws Throwable {
           // 前置逻辑
           System.out.println("操作：" + logOperation.value());
           Object result = pjp.proceed();
           // 后置逻辑
           return result;
       }
   }
   ```

3. **使用自定义注解**
   - 在目标方法或类上添加自定义注解。

   ```java
   @LogOperation("下单")
   public void createOrder() { ... }
   ```

#### 常见使用场景

- **统一日志记录、权限校验、参数校验等横切关注点**（AOP 典型场景）
- **自定义注解+配置元数据**（如自定义配置属性映射）
- **简化重复性代码**（如声明式事务、缓存、API 接口标记等）
- **实现领域特定语言（DSL）**（如领域模型注解、数据脱敏等）

**举例说明**  
如 @Transactional、@RestController 都是 Spring 的内置注解。你也可以定义 @SensitiveData 注解，配合切面对敏感字段脱敏处理。

---

#### 总结性回答（复习提示词）

> 自定义注解：@interface 定义 + @Target/@Retention + AOP/后处理配合使用。常用于统一日志、权限、参数校验、标记元数据等场景。

# Spring 自定义注解 面试追问及参考答案

---

## 1. @Retention 有哪些取值？分别有什么用？

**答：**  
- SOURCE：注解只保留在源码，不会编译到 class 文件。
- CLASS（默认）：注解保留在 class 文件，但运行时不可读取。
- RUNTIME：注解不仅保留在 class 文件，运行时也可通过反射获取。  
  Spring AOP、反射等运行时处理场景必须用 RUNTIME。

---

## 2. @Target 有哪些常用取值？举例说明

**答：**  
- TYPE：类、接口、枚举
- METHOD：方法
- FIELD：字段
- PARAMETER：方法参数
- CONSTRUCTOR：构造方法  
  常用如：限制注解只能标记在方法（METHOD），或类（TYPE）上。

---

## 3. Spring AOP 如何拦截自定义注解？原理是什么？

**答：**  
- 在切面（@Aspect）中用 @Around 或 @Before、@After 配合 @annotation(注解名) 表达式拦截带指定注解的方法。
- Spring AOP 底层通过代理（JDK 动态代理或 CGLIB）实现横切逻辑，运行时通过反射判断方法是否有注解。

---

## 4. 自定义注解能否继承？如何实现注解的组合？

**答：**  
- 注解不能继承，但可实现“组合注解”——一个注解上再加其他注解。如 @RestController = @Controller + @ResponseBody。

---

## 5. 如何获取方法或类上的自定义注解参数？

**答：**  
- 通过反射（Method.getAnnotation(...)），或在切面通知方法参数中直接写注解对象，Spring 会自动注入。

---

## 6. 自定义注解只能用于 AOP 吗？还有哪些典型用法？

**答：**  
- 不只限于 AOP。还可结合 BeanPostProcessor、参数解析器、校验框架等实现自定义校验、元数据标识、自动配置等。

---

## 7. 如何实现带属性的自定义注解？默认值如何定义？

**答：**  
- 在 @interface 内定义属性（方法），可用 default 指定默认值。例如：
  ```java
  String value() default "";
  int level() default 1;
  ```

---

## 8. 注解能否作用于运行时动态生成的 Bean？

**答：**  
- 只要注解的 @Retention 是 RUNTIME，且 Bean 被注册到 Spring 容器，AOP/后处理器等都可以生效。

---

## 9. 自定义注解和元注解的区别？如何理解元注解？

**答：**  
- 自定义注解是业务功能标识，元注解（如 @Target、@Retention、@Documented、@Inherited）是用于修饰注解本身的注解。

---

## 10. 自定义注解有哪些注意事项？

**答：**  
- 必须用 @Retention(RUNTIME) 才能被 Spring AOP/反射处理。
- 不要滥用注解，避免增加理解和维护复杂度。
- 组合注解时注意元注解的继承和合并。

---

## 面试总结提示

- 熟悉注解定义、元注解作用和 Spring AOP 拦截原理
- 能举例自定义注解实际开发场景（日志、权限、校验、配置等）
- 了解组合注解、反射获取注解属性等进阶用法

---