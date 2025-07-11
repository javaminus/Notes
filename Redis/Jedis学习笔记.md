# Jedis学习笔记

 作者：javaminus  
 日期：2025-07-11

## 1. Jedis简介

Jedis是Java语言中用于连接Redis的客户端库，提供了简单易用的API来操作Redis数据库。

主要特点：
- 轻量级客户端
- API设计简单直观
- 支持Redis的所有常用功能
- 支持连接池管理

## 2. 环境准备

### 2.1 Maven依赖

```xml
dependency
    groupIdredis.clientsgroupId
    artifactIdjedisartifactId
    version4.3.1version
dependency
```

### 2.2 确保Redis服务已启动

默认端口：6379

## 3. 基本连接

### 3.1 直接连接

```java
 创建Jedis对象
Jedis jedis = new Jedis(localhost, 6379);

 测试连接
String pong = jedis.ping();
System.out.println(pong);  输出：PONG

 关闭连接
jedis.close();
```

### 3.2 带密码连接

```java
Jedis jedis = new Jedis(localhost, 6379);
jedis.auth(yourpassword);
```

## 4. Jedis连接池

### 4.1 为什么需要连接池？

- 避免频繁创建和销毁连接
- 提高系统性能
- 控制资源使用

### 4.2 连接池基本使用

```java
 配置连接池
JedisPoolConfig poolConfig = new JedisPoolConfig();
poolConfig.setMaxTotal(10);     最大连接数
poolConfig.setMaxIdle(5);       最大空闲连接
poolConfig.setMinIdle(1);       最小空闲连接

 创建连接池
JedisPool jedisPool = new JedisPool(poolConfig, localhost, 6379);

 获取连接
try (Jedis jedis = jedisPool.getResource()) {
     使用jedis操作Redis
    jedis.set(key, value);
} 

 应用程序结束前关闭连接池
jedisPool.close();
```

## 5. 基本数据操作

### 5.1 字符串(String)操作

```java
 设置值
jedis.set(name, Alice);

 获取值
String name = jedis.get(name);

 设置过期时间(秒)
jedis.setex(sessionId, 30, abc123);

 递增
jedis.incr(counter);

 批量操作
jedis.mset(k1, v1, k2, v2);
ListString values = jedis.mget(k1, k2);
```

### 5.2 列表(List)操作

```java
 从列表左侧添加元素
jedis.lpush(users, Tom, Jack, Steve);

 获取列表范围
ListString users = jedis.lrange(users, 0, -1);

 从列表右侧弹出元素
String user = jedis.rpop(users);

 获取列表长度
long size = jedis.llen(users);
```

### 5.3 哈希(Hash)操作

```java
 设置哈希表字段值
jedis.hset(user1, name, Alice);
jedis.hset(user1, email, alice@example.com);

 获取字段值
String name = jedis.hget(user1, name);

 获取所有字段和值
MapString, String userInfo = jedis.hgetAll(user1);

 判断字段是否存在
boolean exists = jedis.hexists(user1, age);
```

### 5.4 集合(Set)操作

```java
 添加集合成员
jedis.sadd(tags, java, redis, spring);

 获取集合所有成员
SetString tags = jedis.smembers(tags);

 判断元素是否在集合中
boolean isMember = jedis.sismember(tags, java);

 集合交集
SetString intersection = jedis.sinter(tags1, tags2);
```

### 5.5 有序集合(ZSet)操作

```java
 添加有序集合成员
jedis.zadd(ranking, 100, player1);
jedis.zadd(ranking, 85, player2);
jedis.zadd(ranking, 95, player3);

 获取分数范围的成员
SetString topPlayers = jedis.zrevrangeByScore(ranking, 100, 90);

 获取成员排名(从0开始)
Long rank = jedis.zrevrank(ranking, player2);

 获取成员分数
Double score = jedis.zscore(ranking, player1);
```

## 6. 事务操作

Redis事务允许一次执行多个命令，中途不会被其他命令打断。

```java
 开始事务
Transaction tx = jedis.multi();

try {
     在事务中执行命令
    tx.set(account1, 900);
    tx.set(account2, 100);
    
     提交事务
    tx.exec();
} catch (Exception e) {
     回滚事务
    tx.discard();
}
```

## 7. 管道(Pipeline)操作

管道可以一次性发送多条命令，提高吞吐量。

```java
Pipeline pipeline = jedis.pipelined();

 添加命令到管道
pipeline.set(key1, value1);
pipeline.set(key2, value2);
pipeline.incr(counter);

 执行管道中的所有命令
ListObject results = pipeline.syncAndReturnAll();
```

## 8. 发布订阅模式

### 8.1 发布消息

```java
jedis.publish(channel1, Hello, Redis!);
```

### 8.2 订阅消息

```java
 创建一个订阅者实例
Jedis subscriber = new Jedis(localhost, 6379);

 实现订阅监听器
JedisPubSub jedisPubSub = new JedisPubSub() {
    @Override
    public void onMessage(String channel, String message) {
        System.out.println(收到消息： + message +  来自频道： + channel);
    }
};

 订阅频道(会阻塞线程)
subscriber.subscribe(jedisPubSub, channel1);
```

## 9. Lua脚本

在Redis中执行Lua脚本可以保证原子性。

```java
 定义Lua脚本
String luaScript = return redis.call('set',KEYS[1],ARGV[1]);

 执行脚本
Object result = jedis.eval(luaScript, 1, mykey, myvalue);
```

## 10. 实用场景示例

### 10.1 实现分布式锁

```java
 尝试获取锁
String lockKey = resource_lock;
String lockValue = UUID.randomUUID().toString();
boolean lockAcquired = jedis.setnx(lockKey, lockValue) == 1;
if (lockAcquired) {
    try {
         设置锁超时时间，防止死锁
        jedis.expire(lockKey, 30);
        
         执行需要加锁的操作
         ...
        
    } finally {
         释放锁 (确保是自己的锁)
        if (lockValue.equals(jedis.get(lockKey))) {
            jedis.del(lockKey);
        }
    }
}
```

### 10.2 简单缓存实现

```java
 查询数据，先查缓存，缓存没有再查数据库
String productKey = product + productId;
String productInfo = jedis.get(productKey);

if (productInfo == null) {
     缓存未命中，查询数据库
    productInfo = queryProductFromDB(productId);
    
     将查询结果存入缓存，设置过期时间
    jedis.setex(productKey, 3600, productInfo);
}

return productInfo;
```

## 11. 常见问题与最佳实践

1. 使用连接池而非直连
   - 使用JedisPool管理连接，避免频繁创建连接

2. 正确关闭资源
   - 使用try-with-resources或finally确保关闭连接

3. 设置合理的超时时间
   - 连接超时、读取超时设置合理的值

4. 合理设置键的过期时间
   - 避免Redis内存持续增长

5. 使用Pipeline批量操作
   - 大量数据读写时使用Pipeline提升性能

---

这份学习笔记涵盖了Jedis的核心用法，希望对你有所帮助！如果有任何问题，欢迎随时交流。