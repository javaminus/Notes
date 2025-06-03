### 问题

**Spring 中的条件注解（@Conditional）有什么作用？常见的条件注解有哪些？**

---

#### 详细解释

Spring 的条件注解（`@Conditional` 及其扩展注解）能让 Spring 根据“条件”决定某个 Bean 是否装配到容器中，使配置更灵活、可扩展。

**核心原理：**
- `@Conditional` 注解可用在 `@Configuration`、`@Bean`、`@Component` 等声明上。
- Spring 在解析 Bean 时，会判断条件（由实现 `Condition` 接口的类给出）是否成立，成立才创建 Bean。
- 常与自动配置、环境切换等场景结合使用。

**Spring Boot 提供了许多常用的条件注解：**

| 注解                            | 作用                                             |
| ------------------------------- | ------------------------------------------------ |
| @ConditionalOnClass             | 类路径下有指定类时才生效（如依赖存在时自动配置） |
| @ConditionalOnMissingClass      | 类路径下没有指定类时才生效                       |
| @ConditionalOnBean              | 容器中存在指定 Bean 时才生效                     |
| @ConditionalOnMissingBean       | 容器中没有指定 Bean 时才生效                     |
| @ConditionalOnProperty          | 配置文件中有指定属性时才生效                     |
| @ConditionalOnExpression        | SpEL 表达式成立时才生效                          |
| @ConditionalOnResource          | 类路径中存在指定资源时才生效                     |
| @ConditionalOnWebApplication    | 当前是 Web 应用时生效                            |
| @ConditionalOnNotWebApplication | 当前不是 Web 应用时生效                          |

**举例说明：**
- 只有当项目引入了 Redis 依赖，`@ConditionalOnClass(RedisTemplate.class)` 的 Bean 才会被自动配置。
- 你可以写一个 Bean，只有 application.properties 里设置了 `feature.enabled=true`，用 `@ConditionalOnProperty("feature.enabled")` 控制它是否加载。

**自定义条件：**
- 可以实现 `org.springframework.context.annotation.Condition` 接口，注入自己的判断逻辑，然后配合 `@Conditional(MyCondition.class)` 使用。

---

#### 总结性回答（复习提示词）

> 条件注解：控制 Bean 是否装配（如 @ConditionalOnClass、@ConditionalOnMissingBean、@ConditionalOnProperty），常用于自动配置和环境切换，可自定义条件。