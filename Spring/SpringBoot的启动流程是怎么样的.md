Spring Boot 的启动流程主要包括以下几个核心步骤：

---

### 1. 执行 main 方法
通常入口类会用 `@SpringBootApplication` 注解，并包含 main 方法：

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

---

### 2. 创建 SpringApplication 实例
- `SpringApplication.run()` 首先会创建一个 `SpringApplication` 对象。
- 推断当前应用类型（普通应用、Servlet Web、Reactive Web）。

---

### 3. 加载配置和监听器
- 加载环境变量（如 application.properties/yml）。
- 加载并注册一系列 ApplicationContextInitializer、ApplicationListener。

---

### 4. 启动 Spring 容器
- 创建合适的 ApplicationContext（比如 Web 项目为 `AnnotationConfigServletWebServerApplicationContext`）。
- 扫描、注册、实例化所有的 Bean。

---

### 5. 启动内嵌 Web 容器（如 Tomcat）
- 如果是 Web 项目，会自动装配嵌入式 Web 服务器（Tomcat/Jetty/Undertow）。
- 创建并启动 WebServer，注册 DispatcherServlet。

---

### 6. 完成启动，监听请求
- 发布 ApplicationReadyEvent。
- 应用启动完成，开始对外提供服务。

---

## 图示流程

1. main 方法入口
2. 创建 SpringApplication 实例
3. 推断应用类型
4. 加载配置和监听器
5. 创建 ApplicationContext
6. 启动内嵌服务器
7. 初始化 Bean
8. 应用启动完成

---

## 总结

Spring Boot 通过一行 `SpringApplication.run()`，自动完成了环境准备、Bean 注册、内嵌服务器启动等一系列工作，实现了“开箱即用”的体验。

如果需要更详细的源码分析，也可以告诉我！