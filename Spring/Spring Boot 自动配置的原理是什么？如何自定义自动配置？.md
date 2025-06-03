### 问题

**Spring Boot 自动配置的原理是什么？如何自定义自动配置？**

---

#### 详细解释

Spring Boot 之所以能“开箱即用”，很大程度上得益于其**自动配置（Auto-Configuration）机制**。它能根据你的项目依赖和配置自动为你装配合适的 Bean，无需手动配置繁琐的 XML 或 Java 配置类。

**核心原理：**
- **@SpringBootApplication** 注解实际上包含了 `@EnableAutoConfiguration`，该注解会触发自动配置机制。
- **SpringFactoriesLoader** 会扫描所有依赖包下的 `META-INF/spring.factories` 文件，读取其中配置的 `org.springframework.boot.autoconfigure.EnableAutoConfiguration` 条目。
- 这些条目实际上是自动配置类（如 `DataSourceAutoConfiguration`、`WebMvcAutoConfiguration` 等）的全限定名。
- Spring Boot 会根据类路径下的依赖（比如是否有 H2、MySQL 驱动），以及你是否在 `application.properties` 配置了相关属性，来决定哪些自动配置类生效（通常配合 `@ConditionalOnClass`、`@ConditionalOnMissingBean` 等条件注解）。

**自定义自动配置步骤：**
1. 新建一个自动配置类，使用 `@Configuration` 和 `@Conditional...` 系列注解，定义要装配的 Bean。
2. 用 `@AutoConfigureAfter` 或 `@AutoConfigureBefore` 控制加载顺序（如有需要）。
3. 在 `resources/META-INF/spring.factories` 文件中，添加一行指向你的自动配置类：
   ```
   org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
   com.example.demo.config.MyAutoConfiguration
   ```
4. 将自定义 starter 打成 jar 包，供其他项目引入即可实现自动装配。

**通俗例子：**
- 比如你引入 spring-boot-starter-web，Spring Boot 检查到有 Web 相关依赖，会自动装配 Tomcat、DispatcherServlet、Jackson 等 Bean。
- 你可以写一个自己的自动配置，让公司所有项目只要引入你写的 starter，自动具备统一的日志、监控等能力。

---

#### 总结性回答（复习提示词）

> Spring Boot 自动配置原理：@EnableAutoConfiguration + spring.factories + 条件注解。自定义自动配置需实现配置类并注册到 spring.factories。