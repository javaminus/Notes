### **Spring MVC 工作流程（记忆版）**

------

### **完整流程（7 步）**

1️⃣ **请求进入（Front Controller）**

- 用户请求被 `DispatcherServlet`（前端控制器）拦截。

2️⃣ **寻找控制器（Controller）**

- `DispatcherServlet` 通过 `HandlerMapping` 找到对应的 `Controller` 方法。

3️⃣ **执行控制器（Dispatcher）**

- `DispatcherServlet` 调用 `Controller` 的 `@RequestMapping` 方法，处理请求。

4️⃣ **返回模型数据（HandlerMapping）**

- `Controller` 返回 `ModelAndView`（数据 + 视图名）。

5️⃣ **解析视图（View Resolver）**

- `ViewResolver` 解析视图名，确定页面（如 `JSP`、`Thymeleaf`）。

6️⃣ **渲染视图（Model）**

- 视图模板引擎（如 `Thymeleaf`）渲染数据，生成 HTML 页面。

7️⃣ **响应返回（Servlet Response）**

- HTML 页面返回给用户，浏览器显示内容。



> 不过现在前后端分离，渲染视图交给vue/react
>
> 前后端分离下的流程变化
请求进入：流程一样，DispatcherServlet 拦截请求。
寻找控制器：依然通过 HandlerMapping 找到 Controller。
执行控制器：Controller 方法处理业务逻辑。
返回数据：Controller 通常返回的是对象（如 List、Map），或者直接返回 JSON 字符串，而不是 ModelAndView。
用 @RestController 或 @ResponseBody 注解，直接将对象序列化为 JSON。
（不再解析视图）：不使用 ViewResolver，因为没有视图名和页面需要解析。
（不再渲染视图）：后端不负责页面渲染，前端（如 React/Vue/Angular）负责将 JSON 数据渲染为页面。
响应返回：Servlet 直接将 JSON 数据返回给前端，前端应用解析 JSON，生成 UI。

# Spring MVC 工作流程 面试常见追问及参考答案

---

## 1. DispatcherServlet 的作用是什么？为什么称为“前端控制器”？

**答：**  
- DispatcherServlet 是整个 Spring MVC 的核心入口，负责请求的统一分发、调度和响应。  
- 称为“前端控制器”是因为它拦截所有请求，由它统一分配给具体的 Handler（Controller），实现了前置处理（如安全、日志等）和后置处理（如渲染视图），符合“前端控制器”设计模式。

---

## 2. HandlerMapping 的工作原理？

**答：**  
- HandlerMapping 负责根据请求的 URL、方法等信息，查找并确定应该由哪个 Controller 的哪个方法来处理当前请求。  
- Spring MVC 支持多种 HandlerMapping，如基于注解的 RequestMappingHandlerMapping、基于 URL 的 SimpleUrlHandlerMapping 等。

---

## 3. 请求参数如何传递到 Controller 方法？

**答：**  
- Spring MVC 会根据方法参数类型自动进行参数绑定。常见方式有：  
  - 通过@RequestParam获取请求参数
  - 通过@PathVariable获取路径变量
  - 通过@RequestBody获取请求体（如JSON）
  - 直接将参数封装为Java对象（POJO）

---

## 4. ModelAndView 和 Model 有什么区别？

**答：**  
- ModelAndView 同时包含视图名和模型数据，常用于返回视图型响应；
- Model 只包含数据，通常和 @ResponseBody、RestController 等结合用于返回 JSON 或其他数据格式。

---

## 5. ViewResolver 如何确定视图？

**答：**  
- ViewResolver 通过解析 Controller 返回的视图名（如"home"），结合配置的前缀/后缀规则（如 /WEB-INF/jsp/ + .jsp），找到物理视图模板（如 JSP、Thymeleaf 页面）。

---

## 6. 如果 Controller 返回字符串，怎么区分是视图名还是响应内容？

**答：**  
- 若方法上有 @ResponseBody 或类上有 @RestController，返回字符串作为响应内容（如 JSON 文本）；否则视为视图名，要由 ViewResolver 解析。

---

## 7. 如何自定义异常处理？

**答：**  
- 可以实现 @ControllerAdvice + @ExceptionHandler 注解进行全局异常处理，或实现 HandlerExceptionResolver 接口自定义异常处理逻辑。

---

## 8. Spring MVC 支持哪些视图技术？

**答：**  
- 支持JSP、Thymeleaf、Freemarker、Velocity、PDF、Excel等多种视图（可通过配置ViewResolver实现）。

---

## 9. Spring MVC 与 Servlet 有什么关系？

**答：**  
- Spring MVC 基于 Servlet 架构，DispatcherServlet 继承自 HttpServlet，本质上是一个 Servlet，负责对请求的拦截和分发。

---

## 10. 请求到响应过程中，哪些环节可以进行自定义扩展？

**答：**  
- 可以自定义 HandlerInterceptor（拦截器）、HandlerMethodArgumentResolver（参数绑定）、HandlerMethodReturnValueHandler（返回值处理）、ViewResolver（视图解析）、ExceptionResolver（异常处理）等扩展点。

---

## 面试总结小结

- 牢记流程七步，能简要描述各环节作用；
- 针对 DispatcherServlet、HandlerMapping、ViewResolver、参数绑定、异常处理等细节，能举例说明扩展和原理；
- 熟悉各种注解和扩展点的实际用法。

---