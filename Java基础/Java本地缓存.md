Java 的本地缓存是指在应用程序本地（通常是内存中）保存部分数据，以减少对外部资源（如数据库、远程服务等）的访问频率，提高性能和响应速度。常见的本地缓存实现方式和框架如下：

## 1. 原生实现
可以直接使用 Java 的集合类（如 `HashMap`、`ConcurrentHashMap`）来实现简单的本地缓存。例如：

```java
import java.util.concurrent.ConcurrentHashMap;

public class LocalCache {
    private static final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    public static void put(String key, Object value) {
        cache.put(key, value);
    }

    public static Object get(String key) {
        return cache.get(key);
    }

    public static void remove(String key) {
        cache.remove(key);
    }
}
```
这种方式简单易用，但缺点是没有过期管理、容量限制等高级功能。

## 2. Guava Cache
[Guava](https://github.com/google/guava) 是 Google 提供的一个 Java 工具库，其中的 `Cache` 模块支持本地缓存，具备过期、容量限制等功能。

```java
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

Cache<String, Object> cache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();

cache.put("key", "value");
Object value = cache.getIfPresent("key");
```

## 3. Caffeine
[Caffeine](https://github.com/ben-manes/caffeine) 是一个高性能的 Java 本地缓存库，支持多种淘汰策略、统计、异步加载等。

```java
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

Cache<String, Object> cache = Caffeine.newBuilder()
    .maximumSize(1000)
    .expireAfterWrite(10, TimeUnit.MINUTES)
    .build();

cache.put("key", "value");
Object value = cache.getIfPresent("key");
```

## 4. Ehcache
[Ehcache](https://www.ehcache.org/) 是一个功能强大的本地缓存解决方案，支持磁盘持久化等特性，适合复杂场景。

```xml
<!-- Maven 依赖 -->
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <version>3.10.8</version>
</dependency>
```

```java
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
Cache<String, String> cache = cacheManager.createCache("myCache",
    CacheConfigurationBuilder.newCacheConfigurationBuilder(
        String.class, String.class, ResourcePoolsBuilder.heap(100))
);

cache.put("key", "value");
String value = cache.get("key");
```

## 5. Spring Cache
Spring 框架也集成了本地缓存的支持，可以用注解实现本地缓存（底层可以选用上面提到的 Guava、Caffeine、Ehcache）。

```java
@Cacheable(value = "myCache", key = "#id")
public User getUserById(Long id) {
    // 查询数据库
}
```

---

### 总结
- 简单缓存：用 `HashMap` 或 `ConcurrentHashMap`。
- 高性能/丰富功能：推荐用 Caffeine 或 Guava。
- 企业级/持久化/复杂需求：推荐用 Ehcache。
- Spring 项目：可用 Spring Cache 集成。

如需详细代码或某个具体场景说明，请补充说明！