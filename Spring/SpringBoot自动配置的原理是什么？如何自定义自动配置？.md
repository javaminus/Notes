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



# Spring Boot 自动配置原理 面试常见追问及参考答案

---

## 1. @EnableAutoConfiguration 与 @SpringBootApplication 有什么关系？

**答**：  
- @SpringBootApplication 是一个组合注解，包含了 @EnableAutoConfiguration、@SpringBootConfiguration 和 @ComponentScan。
- 其中 @EnableAutoConfiguration 负责自动配置的核心功能。

---

## 2. Spring Boot 自动配置类是如何被加载的？

**答**：  
- Spring Boot 在启动时通过 SpringFactoriesLoader 机制，扫描所有依赖 jar 包下的 `META-INF/spring.factories` 文件；
- 读取其中 `org.springframework.boot.autoconfigure.EnableAutoConfiguration` 对应的自动配置类全限定名，并将这些类加载到 IOC 容器中。

---

## 3. 自动配置类如何实现“按需生效”？

**答**：  
- 自动配置类通常配合条件注解如 `@ConditionalOnClass`、`@ConditionalOnBean`、`@ConditionalOnMissingBean` 等；
- 只有当相关条件成立时（如依赖存在、用户未自定义 Bean 等），相关 Bean 才会被自动注入，保证不会与用户自定义 Bean 冲突。

---

## 4. 如何禁用某个自动配置？

**答**：  
- 可以在启动类上使用 `@SpringBootApplication(exclude = XXXAutoConfiguration.class)` 排除指定自动配置类；
- 也可以在配置文件（application.properties）中使用 `spring.autoconfigure.exclude=...` 属性排除。

---

## 5. 自定义自动配置和普通 @Configuration 有什么区别？

**答**：  
- 自定义自动配置需用条件注解控制 Bean 是否创建，避免和用户配置冲突；
- 需注册到 `spring.factories`，才能被外部项目引入并自动生效；
- 普通 @Configuration 只在当前项目生效，不具备 starter 自动装配能力。

---

## 6. 为什么自动配置类要使用 @ConditionalOnMissingBean 等条件注解？

**答**：  
- 避免“重复注入”：只有用户没有自定义相关 Bean 时，自动配置才生效；
- 保证用户自定义配置优先于自动配置，增强扩展性和灵活性。

---

## 7. 如何调试/排查自动配置是否生效？

**答**：  
- 可加上 `--debug` 启动参数，Spring Boot 启动日志会输出自动配置报告，显示哪些自动配置生效、哪些未生效及原因；
- 也可以通过 Actuator 的 `/actuator/conditions` 或 `/actuator/beans` 端点查看 Bean 装配情况。

---

## 8. @Conditional 系列注解常见有哪些？各自作用是什么？

**答**：  
- `@ConditionalOnClass`：类路径存在指定类时生效；
- `@ConditionalOnMissingBean`：容器中不存在指定 Bean 时生效；
- `@ConditionalOnProperty`：配置了指定属性时生效；
- `@ConditionalOnBean`：容器中存在指定 Bean 时生效；
- `@ConditionalOnResource`：classpath 下有指定资源时生效。

---

## 9. 自动配置与 starter 的关系是什么？

**答**：  
- starter 本质是一个依赖管理模块（只做依赖聚合），通常会依赖一个带自动配置的模块；
- 只有自动配置模块写了自动配置类并注册到 spring.factories，starter 引入后才能自动装配。

---

## 10. 自定义自动配置被引入后，如何控制配置顺序？

**答**：  
- 可用 `@AutoConfigureBefore` 或 `@AutoConfigureAfter` 注解控制自动配置类的加载顺序；
- 避免 Bean 装配冲突或顺序依赖问题。

---

## 面试总结tips

- 理解 EnableAutoConfiguration + spring.factories + 条件注解的联动机制。
- 掌握自定义 starter 自动配置的注册和控制方法。
- 熟悉常见排查、扩展和禁用自动配置的操作。

---