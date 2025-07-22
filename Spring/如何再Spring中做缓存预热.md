# 如何在Spring启动过程中做缓存预热

这个问题其实是考察如何在Spring启动过程中做额外操作，常见做法如下：

---

## 1. 监听 ApplicationReadyEvent

`ApplicationReadyEvent` 是 Spring Boot 框架中的一个事件类，表示应用程序已经完全启动并准备好接收请求。可以通过监听这个事件，在应用启动后执行缓存预热逻辑。

**示例：**
```java
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CachePreloader {

    @EventListener(ApplicationReadyEvent.class)
    public void preloadCache() {
        // 执行缓存预热逻辑
    }
}
```

---

## 2. 实现 Runner 接口

Spring Boot 提供了 `CommandLineRunner` 和 `ApplicationRunner` 接口，应用启动后会自动调用其实现类的方法。

**示例：**
```java
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CachePreloader implements CommandLineRunner {
    @Override
    public void run(String... args) {
        // 执行缓存预热逻辑
    }
}
```
或者
```java
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationArguments;

@Component
public class CachePreloader implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        // 执行缓存预热逻辑
    }
}
```

---

## 3. 实现 InitializingBean 接口

实现 `InitializingBean` 接口，并在 `afterPropertiesSet` 方法中执行缓存预热逻辑。Spring 初始化该 Bean 时会调用此方法。

**示例：**
```java
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class CachePreloader implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        // 执行缓存预热逻辑
    }
}
```

---

## 4. 使用 @PostConstruct 注解

在 Bean 的方法上使用 `@PostConstruct` 注解，该方法会在 Bean 的构造方法执行完毕后被调用，可以在此处进行缓存预热。

**示例：**
```java
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CachePreloader {
    @PostConstruct
    public void preloadCache() {
        // 执行缓存预热逻辑
    }
}
```

---

## 执行顺序补充

- 构造函数
- `@PostConstruct`
- `afterPropertiesSet`（InitializingBean）
- 自定义 `init-method`

---

## 总结

Spring启动时做缓存预热，可以选择监听 ApplicationReadyEvent、实现 Runner 接口、实现 InitializingBean 或使用 @PostConstruct。实际选型可根据业务需要和预热时机灵活选用。