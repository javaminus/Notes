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

