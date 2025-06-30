### 问题

**Spring 中的 @Component、@Service、@Repository、@Controller 注解有什么区别？**

---

#### 详细解释

这四个注解都是 Spring 用于自动扫描和实例化 Bean 的“组件注解”，属于`“类级别注解”`，可以让被标记的类被 Spring 容器自动识别并注册为 Bean。它们的本质作用相同，都是将类交给 Spring 管理，但**语义和使用场景不同**。

- **@Component**  
  最基础的组件注解，适用于任何通用 Bean，只代表“这个类是个组件”，没有额外语义。  
  例如：工具类、辅助类。

- **@Service**  
  用于标记“服务层”组件，语义化更强，表示业务逻辑处理的 Service 类。便于团队协作和代码阅读，也方便 AOP、事务等框架做切面增强。

- **@Repository**  
  用于标记“持久层”组件，主要作用是 DAO 类。Spring 还会对其做特殊处理，比如将数据库相关异常转换为 Spring 的统一异常（DataAccessException）。

- **@Controller**  
  用于标记“控制层”组件，通常用于 Web 层，接收和处理前端的请求，是 Spring MVC 的核心注解。会被 DispatcherServlet 自动识别。

**总结区别：**

| 注解        | 作用层次 | 主要用途                   | 额外特性/语义      |
| ----------- | -------- | -------------------------- | ------------------ |
| @Component  | 通用组件 | 所有 Bean                  | 无                 |
| @Service    | 服务层   | 业务服务类                 | 语义更强，便于 AOP |
| @Repository | 持久层   | DAO 数据访问类             | 异常转换           |
| @Controller | 控制层   | Web 控制器，处理 HTTP 请求 | Spring MVC 识别    |

**场景举例：**

- `@Repository` 用于实现数据库访问的 UserRepository。
- `@Service` 用于实现业务逻辑的 UserService。
- `@Controller` 用于处理用户请求的 UserController。
- `@Component` 用于通用的工具类，如加密工具 EncryptUtil。

---

#### 总结性回答（复习提示词）

> @Component 通用组件，@Service 业务服务，@Repository DAO 持久层（异常转换），@Controller Web 控制器（Spring MVC）。本质一样，主要是语义和层次区分。