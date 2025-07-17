在Spring中，**根据配置动态生成Bean**常见的做法有以下几种：

---

### 1. 使用 `@Configuration` 和 `@Bean` 注解

你可以在配置类里根据配置条件动态创建Bean。例如：

```java
@Configuration
public class MyConfig {

    @Value("${my.bean.type}")
    private String type;

    @Bean
    public MyService myService() {
        if ("A".equals(type)) {
            return new MyServiceA();
        } else {
            return new MyServiceB();
        }
    }
}
```

这里 `myService()` 方法的返回类型是 `MyService`，所以 `MyServiceA` 和 `MyServiceB` 必须是 `MyService` 的子类或者实现类（如果是接口）。 

这种方式适合根据配置返回不同的Bean实例。

---

### 2. 使用 `@Conditional` 注解

Spring 4及以上支持 `@Conditional`，可以根据条件动态注册Bean：

```java
@Bean
@Conditional(MyCondition.class)
public MyService myService() {
    return new MyService();
}
```

你需要实现自己的 `Condition` 类，例如：

```java
public class MyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String type = context.getEnvironment().getProperty("my.bean.type");
        return "A".equals(type);
    }
}
```

---

### 3. 使用 `BeanDefinitionRegistryPostProcessor` 或 `ImportBeanDefinitionRegistrar`

这是最灵活的方式，可以在Spring启动时动态注册Bean：

```java
public class DynamicBeanRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String type = ... // 读取配置
        if ("A".equals(type)) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(MyServiceA.class);
            registry.registerBeanDefinition("myService", beanDefinition);
        } else {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(MyServiceB.class);
            registry.registerBeanDefinition("myService", beanDefinition);
        }
    }
}
```
并在配置类上加上 `@Import(DynamicBeanRegistrar.class)`。

---

### 4. 使用 `@Profile`

如果配置可以用Profile区分，可以这样：

```java
@Bean
@Profile("A")
public MyService myServiceA() { ... }

@Bean
@Profile("B")
public MyService myServiceB() { ... }
```
然后通过激活不同的Profile动态加载不同Bean。

---

### 5. 利用工厂Bean

自定义一个FactoryBean，根据配置返回不同实例：

```java
public class MyServiceFactoryBean implements FactoryBean<MyService> {
    @Override
    public MyService getObject() {
        String type = ... // 读取配置
        if ("A".equals(type)) {
            return new MyServiceA();
        } else {
            return new MyServiceB();
        }
    }
    @Override
    public Class<?> getObjectType() { return MyService.class; }
}
```
然后在配置类里注册这个FactoryBean。

---

**总结：**
- 推荐使用 `@Configuration` 配合 `@Bean`，简单直接；
- 复杂需求可以用 `ImportBeanDefinitionRegistrar` 或 `BeanDefinitionRegistryPostProcessor`；
- 配置方式可以用 yml/properties 文件、环境变量等。

如需具体代码示例或针对你的业务场景定制，欢迎补充需求！