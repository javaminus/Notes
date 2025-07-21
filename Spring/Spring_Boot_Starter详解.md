# Spring Boot Starter 详解

## 什么是 Spring Boot Starter?

Spring Boot Starter 是一组依赖描述，旨在为特定类型的应用程序功能提供一站式解决方案。这些 starter 包含了多个相关的依赖项，你只需要引入一个 starter，就能获取所需的所有依赖，从而快速启动项目开发。

简单来说，Spring Boot Starter 就是将特定领域的常用依赖打包在一起，形成一个独立的启动器，开发者可以一次性将相关的依赖引入项目中。

## Spring Boot Starter 的工作原理

Spring Boot Starter 基于以下核心原则工作：

1. **自动配置**：Starter 包含自动配置类，当 Spring Boot 应用启动时，这些配置类会根据应用的依赖关系自动配置 beans。

2. **条件化配置**：利用 `@ConditionalOnClass`, `@ConditionalOnMissingBean` 等注解，实现智能的条件配置。

3. **默认属性**：为配置项提供默认值，减少开发者的配置工作量。

4. **可覆盖性**：所有自动配置都可以被开发者自定义配置覆盖。

## Spring Boot Starter 的命名规范

- 官方 Starter：`spring-boot-starter-*`，如 `spring-boot-starter-web`
- 非官方 Starter：`*-spring-boot-starter`，如 `mybatis-spring-boot-starter`

## 常用的 Spring Boot Starters

以下是一些常用的 Spring Boot Starters：

| Starter 名称                   | 描述                                                         |
| ------------------------------ | ------------------------------------------------------------ |
| spring-boot-starter            | 核心 starter，包含自动配置、日志和YAML支持                   |
| spring-boot-starter-web        | 构建 Web 应用，包括 RESTful 应用程序的 starter               |
| spring-boot-starter-data-jpa   | 使用 Hibernate 实现 JPA 的 starter                           |
| spring-boot-starter-security   | 添加 Spring Security 支持                                    |
| spring-boot-starter-test       | 测试 Spring Boot 应用的 starter，包含 JUnit, Hamcrest 和 Mockito |
| spring-boot-starter-jdbc       | 使用 JDBC 连接数据库的 starter                               |
| spring-boot-starter-actuator   | 提供生产就绪特性，帮助监控和管理应用                         |
| spring-boot-starter-data-redis | 使用 Redis 的 starter                                        |
| spring-boot-starter-thymeleaf  | 使用 Thymeleaf 视图构建 MVC web 应用的 starter               |

## 如何使用 Spring Boot Starter

在 Maven 项目中添加 starter 依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

## 创建自定义 Starter

如果你需要为团队或组织开发一个通用功能的 starter，可以按以下步骤创建：

1. **创建两个模块**：
   - `xxx-spring-boot-autoconfigure`：包含自动配置代码
   - `xxx-spring-boot-starter`：包含对自动配置模块和其他依赖的引用

2. **添加依赖**：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure</artifactId>
</dependency>
```

3. **创建配置类**：
```java
@Configuration
@ConditionalOnClass(YourService.class)
@EnableConfigurationProperties(YourProperties.class)
public class YourAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public YourService yourService(YourProperties properties) {
        YourService service = new YourService();
        service.setProperty(properties.getProperty());
        return service;
    }
}
```

4. **创建配置属性类**：
```java
@ConfigurationProperties(prefix = "your.prefix")
public class YourProperties {
    private String property;
    // getters and setters
}
```

5. **创建 META-INF/spring.factories 文件**：
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.YourAutoConfiguration
```

## Spring Boot Starter 的优势

1. **简化依赖管理**：一个 starter 可以替代多个依赖配置，减少 POM 或 Gradle 文件的复杂度。

2. **版本兼容性**：Spring Boot 的依赖管理确保 starter 中的所有库都是互相兼容的版本。

3. **自动配置**：无需手动配置，减少样板代码。

4. **可扩展性**：所有自动配置都可以被覆盖，提供灵活的定制能力。

5. **一致的开发体验**：不同功能的 starter 提供一致的使用方式。

## 总结

Spring Boot Starter 是 Spring Boot 简化开发的关键机制之一，通过提供一站式的依赖管理和自动配置，大大减少了项目初始化和配置的工作量，使开发者可以更专注于业务逻辑的实现。

# Spring Boot Starter 高频面试题及答案

## 基础概念类问题

### 1. 什么是Spring Boot Starter?
**答案:** Spring Boot Starter是一组依赖描述，用于快速集成特定功能到Spring Boot应用中。它包含了一组特定功能相关的依赖和自动配置代码，让开发者只需引入一个依赖就能获得所有相关功能，而无需手动管理多个依赖及其版本兼容性问题。

### 2. Spring Boot Starter的命名规范是什么?
**答案:**
- 官方提供的Starter：使用`spring-boot-starter-*`命名格式，如`spring-boot-starter-web`
- 第三方提供的Starter：使用`*-spring-boot-starter`命名格式，如`mybatis-spring-boot-starter`

这种命名规范可以清晰区分官方和非官方starter。

### 3. 列举一些常用的Spring Boot Starter及其功能
**答案:**
- `spring-boot-starter`: 核心starter，包含自动配置、日志和YAML支持
- `spring-boot-starter-web`: 构建web应用，包含内嵌Tomcat和Spring MVC
- `spring-boot-starter-data-jpa`: 集成Spring Data JPA与Hibernate
- `spring-boot-starter-security`: 添加Spring Security支持
- `spring-boot-starter-test`: 提供测试支持，包含JUnit、Mockito等
- `spring-boot-starter-actuator`: 提供生产级监控和管理功能
- `spring-boot-starter-data-redis`: Redis集成
- `spring-boot-starter-thymeleaf`: Thymeleaf模板引擎集成

## 原理类问题

### 4. Spring Boot Starter的工作原理是什么?
**答案:** Spring Boot Starter基于以下机制工作：
1. **自动配置**: 通过`@EnableAutoConfiguration`和`@Configuration`注解，在符合条件时自动创建和配置beans
2. **条件化配置**: 利用`@ConditionalOn*`注解（如`@ConditionalOnClass`, `@ConditionalOnMissingBean`）根据应用环境决定是否应用某个配置
3. **属性绑定**: 通过`@ConfigurationProperties`绑定配置文件中的属性
4. **依赖管理**: 集成相关依赖，并通过spring-boot-dependencies管理版本兼容性
5. **META-INF/spring.factories**: 注册自动配置类，Spring Boot启动时会加载这些配置

### 5. Spring Boot自动配置的原理是什么?
**答案:**
1. Spring Boot启动时，`@SpringBootApplication`注解会启用`@EnableAutoConfiguration`
2. `@EnableAutoConfiguration`会导入`AutoConfigurationImportSelector`
3. `AutoConfigurationImportSelector`会读取`META-INF/spring.factories`文件中`org.springframework.boot.autoconfigure.EnableAutoConfiguration`键下的配置类
4. 对这些配置类应用条件判断（`@ConditionalOn*`注解）
5. 符合条件的配置类会被注册到Spring容器中
6. 这些配置类负责创建和配置特定功能所需的beans

### 6. 什么是条件注解？在Spring Boot Starter中有什么作用?
**答案:** 条件注解是Spring Boot中用于控制bean是否应该被创建的机制，主要包括：
- `@ConditionalOnClass`: 当类路径下存在指定类时
- `@ConditionalOnMissingClass`: 当类路径下不存在指定类时
- `@ConditionalOnBean`: 当容器中存在指定bean时
- `@ConditionalOnMissingBean`: 当容器中不存在指定bean时
- `@ConditionalOnProperty`: 当配置文件中存在指定属性值时
- `@ConditionalOnWebApplication`: 当应用是web应用时
- `@ConditionalOnExpression`: 当SpEL表达式为true时

在Starter中，这些注解确保只有在适当的环境下才会应用配置，避免不必要的bean创建，使Starter能够智能适应不同的应用场景。

> # Spring Boot 条件注解代码示例
>
> Spring Boot 条件注解是实现自动配置和灵活功能开关的重要机制，下面是每种条件注解的详细代码示例。
>
> ## 1. @ConditionalOnClass
>
> 当类路径下存在指定的类时，配置生效。
>
> ```java
> import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> import com.fasterxml.jackson.databind.ObjectMapper;
> 
> @Configuration
> public class JsonConfiguration {
>     
>     @Bean
>     @ConditionalOnClass(ObjectMapper.class)
>     public JsonService jsonService() {
>         return new JacksonJsonService();  // 只有当类路径中存在ObjectMapper类时才创建此Bean
>     }
> }
> ```
>
> ## 2. @ConditionalOnMissingClass
>
> 当类路径下不存在指定的类时，配置生效。
>
> ```java
> import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> 
> @Configuration
> public class LegacyDatabaseConfig {
>     
>     @Bean
>     @ConditionalOnMissingClass("com.mysql.cj.jdbc.Driver")
>     public DataSource oldDriverDataSource() {
>         // 当新版MySQL驱动不存在时，使用旧版驱动
>         return new OldMySqlDataSource();
>     }
> }
> ```
>
> ## 3. @ConditionalOnBean
>
> 当容器中存在指定Bean时，配置生效。
>
> ```java
> import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> 
> @Configuration
> public class CacheConfiguration {
>     
>     @Bean
>     @ConditionalOnBean(name = "dataSource")
>     public CacheManager cacheManager() {
>         // 只有当存在名为"dataSource"的Bean时才创建缓存管理器
>         return new DatabaseBackedCacheManager();
>     }
> }
> ```
>
> ## 4. @ConditionalOnMissingBean
>
> 当容器中不存在指定Bean时，配置生效。
>
> ```java
> import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> 
> @Configuration
> public class MessageConverterConfig {
>     
>     @Bean
>     @ConditionalOnMissingBean(MessageConverter.class)
>     public MessageConverter defaultMessageConverter() {
>         // 当没有自定义的MessageConverter时，提供一个默认实现
>         return new DefaultMessageConverter();
>     }
> }
> ```
>
> ## 5. @ConditionalOnProperty
>
> 当配置文件中存在指定属性且值满足条件时，配置生效。
>
> ```java
> import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> 
> @Configuration
> public class SecurityConfiguration {
>     
>     @Bean
>     @ConditionalOnProperty(
>         prefix = "app.security", 
>         name = "enabled", 
>         havingValue = "true", 
>         matchIfMissing = false
>     )
>     public SecurityManager securityManager() {
>         // 只有当app.security.enabled=true时才创建此Bean
>         return new DefaultSecurityManager();
>     }
>     
>     @Bean
>     @ConditionalOnProperty(
>         prefix = "app.security",
>         name = "mode",
>         havingValue = "oauth2"
>     )
>     public AuthenticationManager oauth2AuthenticationManager() {
>         // 只有当app.security.mode=oauth2时才创建此Bean
>         return new OAuth2AuthenticationManager();
>     }
> }
> ```
>
> ## 6. @ConditionalOnWebApplication
>
> 当应用是Web应用时，配置生效。
>
> ```java
> import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
> import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> 
> @Configuration
> public class WebConfig {
>     
>     @Bean
>     @ConditionalOnWebApplication
>     public FilterRegistrationBean corsFilter() {
>         // 只有在Web应用中才创建此过滤器
>         FilterRegistrationBean registration = new FilterRegistrationBean();
>         registration.setFilter(new CorsFilter());
>         return registration;
>     }
>     
>     @Bean
>     @ConditionalOnWebApplication(type = Type.SERVLET)
>     public ServletContextListener contextListener() {
>         // 只有在Servlet Web应用中才创建此监听器
>         return new AppServletContextListener();
>     }
>     
>     @Bean
>     @ConditionalOnWebApplication(type = Type.REACTIVE)
>     public WebFilter loggingFilter() {
>         // 只有在Reactive Web应用中才创建此过滤器
>         return new ReactiveLoggingFilter();
>     }
> }
> ```
>
> ## 7. @ConditionalOnExpression
>
> 当SpEL表达式计算结果为true时，配置生效。
>
> ```java
> import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> 
> @Configuration
> public class FeatureConfig {
>     
>     @Bean
>     @ConditionalOnExpression("${app.feature.advanced:false} and '${app.mode}' == 'professional'")
>     public AdvancedFeatureService advancedFeatureService() {
>         // 只有当app.feature.advanced=true且app.mode=professional时才创建此Bean
>         return new AdvancedFeatureService();
>     }
>     
>     @Bean
>     @ConditionalOnExpression("#{environment['spring.profiles.active'] == 'dev'}")
>     public DevToolsService devToolsService() {
>         // 只有当激活的profile是dev时才创建此Bean
>         return new DevToolsService();
>     }
>     
>     @Bean
>     @ConditionalOnExpression("T(java.lang.System).getProperty('os.name').toLowerCase().contains('linux')")
>     public UnixToolsService unixToolsService() {
>         // 只有在Linux操作系统上才创建此Bean
>         return new UnixToolsService();
>     }
> }
> ```
>
> ## 组合使用条件注解
>
> 条件注解可以组合使用，实现更复杂的条件判断：
>
> ```java
> import org.springframework.boot.autoconfigure.condition.*;
> import org.springframework.context.annotation.Bean;
> import org.springframework.context.annotation.Configuration;
> 
> @Configuration
> public class ComplexConditionalConfig {
>     
>     @Bean
>     @ConditionalOnClass(name = "org.apache.kafka.clients.producer.KafkaProducer")
>     @ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
>     @ConditionalOnMissingBean(KafkaTemplate.class)
>     public KafkaTemplate<String, String> kafkaTemplate() {
>         // 满足所有条件才创建此Bean:
>         // 1. 类路径中有KafkaProducer
>         // 2. kafka.enabled=true
>         // 3. 容器中没有KafkaTemplate类型的Bean
>         return new KafkaTemplate<>(producerFactory());
>     }
>     
>     @Bean
>     @ConditionalOnWebApplication
>     @ConditionalOnBean(SecurityManager.class)
>     @ConditionalOnProperty(prefix = "app.security.web", name = "enabled", havingValue = "true", matchIfMissing = true)
>     public WebSecurityManager webSecurityManager(SecurityManager securityManager) {
>         // 满足所有条件才创建此Bean:
>         // 1. 应用是Web应用
>         // 2. 容器中存在SecurityManager Bean
>         // 3. app.security.web.enabled=true或未配置
>         return new DefaultWebSecurityManager(securityManager);
>     }
> }
> ```
>
> ## 自定义条件注解
>
> 你还可以创建自己的条件注解，以满足特定的业务需求：
>
> ```java
> // 自定义条件注解
> @Target({ElementType.TYPE, ElementType.METHOD})
> @Retention(RetentionPolicy.RUNTIME)
> @Documented
> @Conditional(OnProductionEnvironmentCondition.class)
> public @interface ConditionalOnProductionEnvironment {
> }
> 
> // 条件实现类
> public class OnProductionEnvironmentCondition implements Condition {
>     @Override
>     public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
>         String[] activeProfiles = context.getEnvironment().getActiveProfiles();
>         for (String profile : activeProfiles) {
>             if ("production".equals(profile)) {
>                 return true;
>             }
>         }
>         return false;
>     }
> }
> 
> // 使用自定义条件注解
> @Configuration
> public class MonitoringConfig {
>     
>     @Bean
>     @ConditionalOnProductionEnvironment
>     public PerformanceMonitor productionMonitor() {
>         // 只有在生产环境中才创建此监控Bean
>         return new EnhancedPerformanceMonitor();
>     }
> }
> ```
>
> 这些条件注解是Spring Boot自动配置的核心机制，掌握它们可以帮助你构建更加灵活和智能的应用程序配置。

## 使用类问题

### 7. 如何在项目中引入和使用Spring Boot Starter?
**答案:**
Maven项目中：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Gradle项目中：
```groovy
implementation 'org.springframework.boot:spring-boot-starter-web'
```

引入后，Spring Boot会自动配置相关功能，无需额外配置。如需自定义配置，可在`application.properties`或`application.yml`中进行。

### 8. 如何排除Starter中的某些依赖?
**答案:**
Maven项目中：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

Gradle项目中：
```groovy
implementation('org.springframework.boot:spring-boot-starter-web') {
    exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'
}
```

### 9. 如何禁用某个自动配置?
**答案:** 可以通过以下方式禁用特定的自动配置：

1. 使用`@SpringBootApplication`注解的exclude属性：
```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MyApplication {
    // ...
}
```

2. 在配置文件中使用`spring.autoconfigure.exclude`属性：
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

## 高级问题

### 10. 如何创建自定义Spring Boot Starter?
**答案:** 创建自定义Starter的步骤：

1. 创建Maven项目，命名为`xxx-spring-boot-starter`
2. 添加依赖：
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-autoconfigure</artifactId>
    </dependency>
</dependencies>
```

3. 创建配置属性类：
```java
@ConfigurationProperties(prefix = "xxx")
public class XxxProperties {
    private boolean enabled = true;
    // 属性和getter/setter
}
```

4. 创建自动配置类：
```java
@Configuration
@EnableConfigurationProperties(XxxProperties.class)
@ConditionalOnClass(YourService.class)
public class XxxAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "xxx", name = "enabled", havingValue = "true", matchIfMissing = true)
    public YourService yourService(XxxProperties properties) {
        return new YourServiceImpl(properties);
    }
}
```

5. 在`META-INF/spring.factories`文件中注册自动配置类：
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.XxxAutoConfiguration
```

### 11. @EnableAutoConfiguration注解的作用是什么?
**答案:** `@EnableAutoConfiguration`是Spring Boot自动配置的核心注解，它启用Spring Boot的自动配置机制。具体作用：

1. 通过`AutoConfigurationImportSelector`类导入自动配置类
2. 扫描classpath下所有JAR包中的`META-INF/spring.factories`文件
3. 加载`EnableAutoConfiguration`键下的配置类列表
4. 应用条件注解判断，将符合条件的配置类加入Spring容器
5. 这些配置类会创建和配置应用所需的beans

`@SpringBootApplication`注解包含了`@EnableAutoConfiguration`，所以使用`@SpringBootApplication`会自动启用自动配置。

### 12. spring.factories文件的作用是什么?
**答案:** `META-INF/spring.factories`是Spring Boot的SPI(Service Provider Interface)机制的关键文件，主要作用：

1. 作为自动配置类的注册表，声明哪些类应该被Spring Boot自动加载
2. 定义了多种类型的组件，最常见的是`EnableAutoConfiguration`下的自动配置类
3. 允许Starter在不被显式导入的情况下自动配置功能
4. Spring Boot启动时会扫描所有jar包中的该文件，并处理其中的配置

典型内容示例：
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.XxxAutoConfiguration,\
com.example.YyyAutoConfiguration
```

### 13. Spring Boot Starter如何解决依赖冲突?
**答案:** Spring Boot通过以下机制解决依赖冲突：

1. **依赖管理**: 使用`spring-boot-dependencies`作为父POM，统一管理依赖版本
2. **版本兼容性测试**: Spring Boot团队确保所有官方starter中的依赖版本互相兼容
3. **版本覆盖机制**: 允许开发者在项目中覆盖特定依赖的版本
4. **依赖排除**: 提供机制排除不需要的传递依赖
5. **依赖顺序**: Maven的依赖解析顺序(最短路径和第一声明原则)帮助解决版本冲突

如需覆盖版本，可使用：
```xml
<properties>
    <jackson.version>2.13.0</jackson.version>
</properties>
```

## 实际应用问题

### 14. Spring Boot Starter与传统Spring应用有什么区别?
**答案:**
1. **配置简化**: 传统Spring应用需要大量XML配置或Java配置，Starter通过自动配置大幅减少配置代码
2. **依赖管理**: 传统Spring应用需要手动管理每个依赖及其版本，Starter整合相关依赖并管理版本兼容性
3. **约定优于配置**: Spring Boot遵循"约定优于配置"原则，提供合理默认值，减少决策点
4. **内嵌服务器**: Spring Boot可内嵌Tomcat等服务器，无需外部容器部署
5. **一体化解决方案**: Starter提供特定功能的一站式解决方案，减少集成难度

### 15. 在微服务架构中，Spring Boot Starter有什么优势?
**答案:**
1. **快速启动**: Starter使微服务应用能够快速启动和开发，缩短上线时间
2. **标准化**: 团队可以创建自定义Starter封装公共功能，确保所有微服务遵循相同标准
3. **配置简化**: 减少每个微服务的配置工作，提高开发效率
4. **功能模块化**: 每个微服务可以根据需要选择合适的Starter，实现功能模块化
5. **监控与管理**: 如actuator-starter提供统一的健康检查、指标收集和监控端点
6. **服务发现集成**: 可通过特定Starter轻松集成服务发现(Eureka、Consul等)
7. **一致的开发体验**: 团队成员在不同微服务间切换时，拥有一致的开发体验

### 16. 如何调试Spring Boot的自动配置过程?
**答案:** 可以通过以下方法调试自动配置：

1. **启用debug日志**: 在`application.properties`中设置：
```properties
debug=true
```
这会输出一份自动配置报告，显示哪些配置被应用，哪些未被应用及原因

2. **查看条件评估报告**: 添加依赖：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
并在配置中启用：
```properties
management.endpoints.web.exposure.include=conditions
```
然后访问`/actuator/conditions`端点查看详细的条件评估结果

3. **使用IDE调试**: 在`ConditionEvaluationReportLoggingListener`类上设置断点

### 17. Spring Boot中的@SpringBootApplication注解包含哪些功能?
**答案:** `@SpringBootApplication`是一个组合注解，包含了以下三个注解的功能：

1. **@EnableAutoConfiguration**: 启用Spring Boot的自动配置机制
2. **@ComponentScan**: 启用组件扫描，扫描同包及子包中的组件
3. **@Configuration**: 标识该类为配置类，允许使用@Bean注解定义beans

这个组合注解是Spring Boot简化配置的典型例子，用一个注解替代了多个注解，简化了启动类的代码。