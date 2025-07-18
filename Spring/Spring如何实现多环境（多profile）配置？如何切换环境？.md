### 问题

**Spring 如何实现多环境（多 profile）配置？如何切换环境？**

---

#### 详细解释

在实际开发中，应用往往需要针对开发、测试、生产等不同环境配置不同的参数（如数据库地址、日志级别等）。Spring 提供了“Profile”机制用于支持多环境配置和灵活切换。

**实现多环境配置的方式：**

1. **多份配置文件**
   - 按规范命名，如：
     - `application-dev.properties`（开发环境）
     - `application-test.properties`（测试环境）
     - `application-prod.properties`（生产环境）
   - 或 YAML 文件如 `application-dev.yml` 等。

2. **主配置文件激活 profile**
   - 在主配置文件（如 `application.properties` 或 `application.yml`）中指定激活的环境：
     ```properties
     spring.profiles.active=dev
     ```
     或
     ```yaml
     spring:
       profiles:
         active: dev
     ```

3. **命令行参数/环境变量指定**
   - 启动时添加参数：
     ```
     java -jar app.jar --spring.profiles.active=prod
     ```
   - 或设置操作系统环境变量 `SPRING_PROFILES_ACTIVE=prod`。

4. **Profile 注解**
   - 有时 Bean 只在某些环境生效，可用 `@Profile("dev")` 注解在类或方法上限制 Bean 的加载。

     ```java
     @Profile("dev")
     @Bean
     public DataSource devDataSource() { ... }
     ```

5. **YAML 文件多 profile 块**
   - 也可以在 `application.yml` 中用 `---` 分隔不同 profile 配置块。

     ```yaml
     spring:
       profiles: dev
     server:
       port: 8080
     ---
     spring:
       profiles: prod
     server:
       port: 80
     ```

**常见场景：**
- 开发环境使用内存数据库，生产环境用 MySQL。
- 不同环境日志级别、外部服务地址不同。

---

#### 总结性回答（复习提示词）

> 多环境配置：多 profile 文件（如 application-dev.properties），用 spring.profiles.active 指定激活环境，也可用 @Profile 控制 Bean 加载。支持命令行、环境变量、YAML 多块等切换方式。