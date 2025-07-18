Spring Boot 通过其自动配置和嵌入式服务器机制，使得可以直接通过 main 方法启动 Web 项目。其核心原理如下：

### 1. main 方法入口

Spring Boot 项目的入口通常是一个带有 `@SpringBootApplication` 注解的类：

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 2. SpringApplication.run 的核心流程

- **创建 SpringApplication 实例**
  - 根据项目类型（普通项目、Web 项目、响应式 Web 项目等）推断应用类型。
  - 加载配置（如 application.properties/yml）。
- **推断并启动 Web 容器**
  - Spring Boot Starter Web 会自动引入嵌入式 Servlet 容器（如 Tomcat、Jetty、Undertow）。
  - 通过 SPI 机制自动发现并加载 `ServletWebServerFactory`（比如 TomcatServletWebServerFactory）。
- **初始化 Spring 容器**
  - 创建 `ApplicationContext`（Web 项目为 `AnnotationConfigServletWebServerApplicationContext`）。
  - 扫描、实例化、装配 Bean。
- **启动 WebServer**
  - 调用 `ServletWebServerFactory` 创建并启动 WebServer（如 Tomcat）。
  - 将 DispatcherServlet 注册到 Servlet 容器。
- **应用启动完成，监听请求**

### 3. 关键机制

- **自动装配**：依赖于 Spring Boot 的自动配置（`@EnableAutoConfiguration`），根据类路径和配置自动装配 Web 环境。
- **嵌入式容器启动**：不用外部部署 war 包，main 方法直接启动内嵌 Tomcat。
- **统一入口**：所有配置和启动都可以通过一行 `SpringApplication.run()` 完成。

### 总结

Spring Boot 通过在 main 方法中调用 `SpringApplication.run`，自动完成了 Spring 容器初始化、嵌入式 Web 容器启动和应用准备工作，无需传统的 web.xml 或外部容器部署，实现了“一键启动”Web 项目。

如果你想深入源码，可以进一步查看 `SpringApplication#run` 以及 `ServletWebServerApplicationContext` 的启动流程。