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