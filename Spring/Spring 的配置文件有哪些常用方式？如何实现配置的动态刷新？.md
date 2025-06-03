### 问题

**Spring 的配置文件有哪些常用方式？如何实现配置的动态刷新？**

---

#### 详细解释

Spring 支持多种方式进行应用配置，方便管理环境变量、数据库连接、第三方服务等参数。主流方式有：

1. **properties 文件**  
   - 最常用方式，文件名通常是 `application.properties`。
   - 支持通过 `@Value("${key}")` 或 `@ConfigurationProperties` 注解读取。
   - 可通过 `spring.profiles.active` 激活不同环境的配置（如 `application-dev.properties`）。

2. **YAML 文件**  
   - 文件名通常为 `application.yml` 或 `application.yaml`，语法更简洁，支持层级结构。
   - 适合复杂、多层级的配置。

3. **环境变量/命令行参数**  
   - Spring Boot 支持通过环境变量、命令行参数覆盖配置文件中的属性，利于容器化部署和运维。

4. **配置中心（分布式配置）**  
   - Spring Cloud Config、Nacos、Apollo 等配置中心可实现分布式配置的集中管理和动态刷新。

**动态刷新配置的方式：**

- **Spring Cloud Config + @RefreshScope**  
  1. 配置中心（如 Spring Cloud Config Server）维护配置。
  2. 客户端引入依赖，并在需要动态刷新的 Bean 上添加 `@RefreshScope` 注解。
  3. 当配置变更后，通过 `/actuator/refresh` 端点或消息总线（如 Spring Cloud Bus）触发刷新，相关 Bean 会重新加载最新配置。

  ```java
  @RefreshScope
  @Component
  public class MyConfigBean {
      @Value("${my.dynamic.value}")
      private String value;
  }
  ```

- **Nacos/Apollo**  
  - 也支持 `@RefreshScope` 或自有的注解（如 Nacos 的 `@NacosValue`) 实现配置自动刷新。

**典型场景举例：**
- 线上环境需要变更数据库连接信息或黑名单名单时，运维可在配置中心修改，无需重启服务。

---

#### 总结性回答（复习提示词）

> Spring 配置方式：properties、YAML、环境变量、配置中心。动态刷新常用 @RefreshScope + Spring Cloud Config/Nacos 等，支持不重启服务实时生效。