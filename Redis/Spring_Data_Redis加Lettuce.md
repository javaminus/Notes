# Spring Data Redis + Lettuce 高级学习文档

---

## 1. 简介

Spring Data Redis 是 Spring 官方推出的 Redis 操作整合库，支持多种客户端（Jedis, Lettuce）。  

Lettuce 的主要作用

- **连接和操作 Redis**：Lettuce 让 Java 应用可以很方便地连接 Redis 服务器，并进行各种数据结构的读写操作。
- **多线程安全**：Lettuce 的连接可以在多个线程间安全共享，适合高并发场景。
- **支持异步和响应式 API**：除了同步操作，还可以用异步方式、响应式方式访问 Redis，满足高性能和现代微服务需求。
- **支持 Redis 单机、哨兵、集群、TLS 等模式**：几乎支持所有 Redis 部署方式。
- **Spring Boot 默认集成**：Spring Boot 2.x 及以上，spring-boot-starter-data-redis 默认用 Lettuce 作为 Redis 客户端。

---

## 2. 依赖配置

在 Maven 项目中添加如下依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

---

## 3. Redis 连接配置

### 3.1 单机模式

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: 1000ms
```

### 3.2 哨兵模式

```yaml
spring:
  redis:
    sentinel:
      master: mymaster         # 指定哨兵监控的主节点名称（与 Redis Sentinel 配置一致）
      nodes:                   # 哨兵节点列表，格式为 IP:端口
        - 192.168.1.1:26379
        - 192.168.1.2:26379
        - 192.168.1.3:26379
    password:                  # Redis 访问密码（如果有密码则填写，无则留空）
    database: 0                # 使用的数据库编号，默认是 0
    lettuce:
      pool:
        max-active: 8          # 连接池最大连接数
        max-idle: 8            # 连接池最大空闲连接数
        min-idle: 0            # 连接池最小空闲连接数
        max-wait: 1000ms       # 连接池获取连接最大等待时间（毫秒）
```

### 3.3 集群模式

```yaml
spring:
  redis:
    cluster:
      nodes:
        - 192.168.1.1:6379
        - 192.168.1.2:6379
        - 192.168.1.3:6379
      max-redirects: 3
    password:
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: 1000ms
```

---

## 4. RedisTemplate 配置

自定义序列化器（Jackson的序列化器），支持存储复杂对象：

```java
@Configuration  // 标记为Spring的配置类
public class RedisConfig {

    /**
     * 配置 RedisTemplate Bean
     * 用于操作 Redis，各种数据结构都可用，支持自定义序列化方式
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置连接工厂
        template.setConnectionFactory(factory);

        // 设置 key 的序列化方式为字符串
        template.setKeySerializer(new StringRedisSerializer());
        // 设置 hash key 的序列化方式为字符串
        template.setHashKeySerializer(new StringRedisSerializer());

        // 创建 Jackson 序列化器，用于将对象序列化为 JSON 存入 Redis
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
            new Jackson2JsonRedisSerializer<>(Object.class);
        // 设置 value 的序列化方式为 JSON
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置 hash value 的序列化方式为 JSON
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 初始化配置
        template.afterPropertiesSet();
        // 返回配置好的 RedisTemplate
        return template;
    }
}
```

---

## 5. Redis 9 种数据结构操作示例

### 5.1 String（字符串）

```java
redisTemplate.opsForValue().set("strKey", "hello redis");
String value = (String) redisTemplate.opsForValue().get("strKey");
```

### 5.2 List（列表）

```java
redisTemplate.opsForList().rightPush("listKey", "A");
redisTemplate.opsForList().rightPushAll("listKey", "B", "C");
List<Object> list = redisTemplate.opsForList().range("listKey", 0, -1); // 左闭右闭
```

### 5.3 Hash（哈希）

```java
redisTemplate.opsForHash().put("hashKey", "name", "Alice");
redisTemplate.opsForHash().put("hashKey", "age", 20);
Object name = redisTemplate.opsForHash().get("hashKey", "name");
Map<Object, Object> map = redisTemplate.opsForHash().entries("hashKey");
```

### 5.4 Set（集合）

```java
redisTemplate.opsForSet().add("setKey", "A", "B", "C");
Set<Object> members = redisTemplate.opsForSet().members("setKey");
```

### 5.5 ZSet（有序集合）

```java
redisTemplate.opsForZSet().add("zsetKey", "A", 1);
redisTemplate.opsForZSet().add("zsetKey", "B", 2);
Set<Object> zset = redisTemplate.opsForZSet().range("zsetKey", 0, -1);
```

### 5.6 Bitmap（位图）

> Bitmap 本质是通过字符串的 setbit/getbit 操作实现

```java
// 设置第10位为1
redisTemplate.execute((RedisCallback<Boolean>) conn -> {
    return conn.setBit("bitmapKey".getBytes(), 10, true);
});
// 获取第10位
Boolean bit = redisTemplate.execute((RedisCallback<Boolean>) conn -> {
    return conn.getBit("bitmapKey".getBytes(), 10);
});
```

### 5.7 HyperLogLog（基数统计）

```java
redisTemplate.opsForHyperLogLog().add("hllKey", "A", "B", "C");
Long count = redisTemplate.opsForHyperLogLog().size("hllKey");
```

### 5.8 Geo（地理信息）

```java
redisTemplate.opsForGeo().add("geoKey", new Point(116.405285, 39.904989), "Beijing");
redisTemplate.opsForGeo().add("geoKey", new Point(121.472644, 31.231706), "Shanghai");
Distance distance = redisTemplate.opsForGeo().distance("geoKey", "Beijing", "Shanghai", Metrics.KILOMETERS);
```

### 5.9 Stream（消息队列）

```java
// 添加消息
Map<String, String> msg = new HashMap<>();
msg.put("field1", "value1");
msg.put("field2", "value2");
RecordId recordId = redisTemplate.opsForStream().add("streamKey", msg);

// 从 Redis 的 streamKey 流中，读取最多 10 条消息（范围为全部记录）
List<MapRecord<String, Object, Object>> messages =
    redisTemplate.opsForStream().range("streamKey", Range.unbounded(), 10);
```

---

## 6. 事务操作

```java
// 使用 Spring Data Redis 的事务功能，批量执行多条操作
List<Object> txResults = redisTemplate.execute(new SessionCallback<List<Object>>() {
    @Override
    public List<Object> execute(RedisOperations operations) throws DataAccessException {
        // 开启事务
        operations.multi();
        // 事务中设置 key1 的值
        operations.opsForValue().set("key1", "value1");
        // 事务中设置 key2 的值
        operations.opsForValue().set("key2", "value2");
        // 提交事务，返回所有操作结果
        return operations.exec();
    }
});
```

---

## 7. 设置过期与删除

```java
redisTemplate.opsForValue().set("token", "abc123", 10, TimeUnit.MINUTES); // 10分钟过期
redisTemplate.delete("token");
```

---

## 8. Spring Cache 注解使用（自动缓存）

```java
@Service
public class UserService {

    @Cacheable(value = "userCache", key = "#id")
    public User getUserById(Long id) {
        // 查询数据库
    }
}
```

```yaml
spring:
  cache:
    type: redis
```

---

## 9. Lettuce 高级特性

### 9.1 哨兵与集群支持

- 配置见第3节，Spring Data Redis 自动支持，无需手动编码。
- 可通过 `RedisConnectionFactory` 获取底层连接，做更底层操作。

### 9.2 异步/响应式 API

需要直接使用 Lettuce 原生 API：【我才不用，背都背不完了】

```java
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.async.RedisAsyncCommands;

RedisClient client = RedisClient.create("redis://localhost:6379");
RedisAsyncCommands<String, String> asyncCommands = client.connect().async();
asyncCommands.set("asyncKey", "value");
```

---

## 10. 常见问题

- **序列化异常**：建议自定义 RedisTemplate 的序列化器，推荐用 Jackson2JsonRedisSerializer。
- **连接池耗尽**：可适当调高连接池参数。
- **多线程安全**：Lettuce 默认线程安全。
- **数据结构兼容性**：部分高级数据结构仅在高版本 Redis/客户端支持，注意兼容性。



# Spring Data Redis 实现分布式锁教程

---

## 1. 分布式锁简介

分布式锁用于多实例、多线程环境下，保证同一资源同一时间只被一个消费者操作。Redis 常见分布式锁实现方式是 SETNX（set if not exists）+ EXPIRE（过期时间）。

---

## 2. 基本思路

- 使用 Redis 的 `set key value NX EX 秒` 命令实现原子加锁。
- 使用唯一 value（如 UUID）标识锁的持有者。
- 释放锁时需校验 value，防止误删他人锁。

---

## 3. Spring Data Redis 实现示例

### 3.1 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 3.2 分布式锁实现类

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisDistributedLock {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 尝试获取分布式锁
     * @param key 锁的key
     * @param expireSec 过期时间（秒）
     * @return 唯一标识（用于解锁），获取失败返回null
     */
    public String tryLock(String key, long expireSec) {
        String value = UUID.randomUUID().toString();
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Boolean success = ops.setIfAbsent(key, value, expireSec, TimeUnit.SECONDS);
        return (success != null && success) ? value : null;
    }

    /**
     * 释放锁
     * @param key 锁的key
     * @param value 唯一标识（加锁时返回的value）
     */
    public boolean unlock(String key, String value) {
        // 使用 Lua 保证原子性
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "   return redis.call('del', KEYS[1]) " +
                "else " +
                "   return 0 " +
                "end";
        Long result = redisTemplate.execute(
            (connection, keySerializer, valueSerializer) ->
                connection.eval(script.getBytes(), ReturnType.INTEGER, 1,
                    keySerializer.serialize(key),
                    valueSerializer.serialize(value)
                ),
            redisTemplate.getKeySerializer(),
            redisTemplate.getValueSerializer(),
            key, value
        );
        return result != null && result > 0;
    }
}
```

### 3.3 使用方法

```java
@Autowired
private RedisDistributedLock redisLock;

public void businessMethod() {
    String lockValue = redisLock.tryLock("my:lock:key", 10); // 10秒自动过期
    if (lockValue != null) {
        try {
            // 业务逻辑
        } finally {
            redisLock.unlock("my:lock:key", lockValue);
        }
    } else {
        // 获取锁失败
    }
}
```

---

## 4. 注意事项

- **必须设置过期时间，防止死锁。**
- **解锁必须用 Lua 脚本保证原子性，避免误删他人锁。**
- 可根据需要增加重试获取锁机制。
- 生产环境推荐使用 Redisson 框架实现分布式锁，功能更丰富。
