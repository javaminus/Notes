# Redisson学习笔记

> 作者：javaminus  
> 日期：2025-07-11

## 1. Redisson简介

Redisson是基于Redis的Java驱动，提供了分布式和可扩展的Java数据结构，不仅实现了对Redis的基本操作，还实现了诸如分布式锁、分布式集合、分布式服务等高级功能。

**主要特点：**
- 基于Netty的异步框架
- 丰富的分布式对象和服务
- 支持多种部署模式（单机、主从、哨兵、集群）
- 线程安全的编程接口
- 内置分布式锁、延时队列等高级功能

## 2. 环境准备

### 2.1 Maven依赖

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.19.0</version>
</dependency>
```

### 2.2 确保Redis服务已启动

默认端口：6379

## 3. 连接配置

### 3.1 单节点连接

```java
// 配置类
Config config = new Config();
config.useSingleServer()
      .setAddress("redis://localhost:6379")
      .setPassword("yourpassword") // 如果有密码
      .setDatabase(0);

// 创建Redisson客户端
RedissonClient redisson = Redisson.create(config);

// 使用完毕后关闭
redisson.shutdown();
```

### 3.2 集群连接

```java
Config config = new Config();
config.useClusterServers()
      .addNodeAddress("redis://127.0.0.1:7001")
      .addNodeAddress("redis://127.0.0.1:7002")
      .addNodeAddress("redis://127.0.0.1:7003");

RedissonClient redisson = Redisson.create(config);
```

### 3.3 哨兵模式

```java
Config config = new Config();
config.useSentinelServers()
      .setMasterName("mymaster")
      .addSentinelAddress("redis://127.0.0.1:26389")
      .addSentinelAddress("redis://127.0.0.1:26379");

RedissonClient redisson = Redisson.create(config);
```

## 4. 基本对象操作

### 4.1 字符串对象(RBucket)

```java
// 获取字符串对象
RBucket<String> bucket = redisson.getBucket("mykey");

// 设置值
bucket.set("Hello, Redisson!");

// 获取值
String value = bucket.get();

// 设置过期时间
bucket.set("value with TTL", 10, TimeUnit.SECONDS);

// 异步API
RFuture<Void> future = bucket.setAsync("async value");
future.whenComplete((result, exception) -> {
    if (exception == null) {
        System.out.println("设置成功");
    }
});
```

### 4.2 位图对象(RBitSet)

```java
RBitSet bitSet = redisson.getBitSet("mybitset");
bitSet.set(0, true);
bitSet.set(1812, true);
boolean isSet = bitSet.get(1812);
```

## 5. 分布式集合

### 5.1 映射(RMap)

```java
// 获取分布式Map
RMap<String, String> map = redisson.getMap("mymap");

// 常规操作
map.put("key1", "value1");
String value = map.get("key1");
map.fastPut("key2", "value2");  // 异步操作

// 原子操作
map.putIfAbsent("uniqueKey", "value");
```

### 5.2 列表(RList)

```java
RList<String> list = redisson.getList("mylist");

// 添加元素
list.add("item1");
list.add("item2");

// 获取元素
String item = list.get(0);

// 列表长度
int size = list.size();
```

### 5.3 集合(RSet)

```java
RSet<String> set = redisson.getSet("myset");
set.add("item1");
set.add("item2");

// 判断元素是否存在
boolean exists = set.contains("item1");

// 移除元素
set.remove("item2");
```

### 5.4 有序集合(RSortedSet)

```java
RSortedSet<String> sortedSet = redisson.getSortedSet("mysortedset");
sortedSet.add("c");
sortedSet.add("a");
sortedSet.add("b");

// 获取有序结果
Iterator<String> iterator = sortedSet.iterator();
```

### 5.5 计分有序集合(RScoredSortedSet)

```java
RScoredSortedSet<String> scoredSortedSet = redisson.getScoredSortedSet("myscores");
scoredSortedSet.add(95.0, "Alice");
scoredSortedSet.add(87.5, "Bob");
scoredSortedSet.add(99.0, "Carol");

// 获取分数
Double score = scoredSortedSet.getScore("Alice");

// 获取排名
Integer rank = scoredSortedSet.rank("Alice");

// 按分数获取元素
Collection<String> highScores = scoredSortedSet.valueRange(90, true, 100, true);
```

## 6. 分布式锁

### 6.1 可重入锁(RLock)

```java
RLock lock = redisson.getLock("mylock");

// 普通加锁
lock.lock();
try {
    // 保护的代码块
    doSomething();
} finally {
    lock.unlock();
}

// 带超时的锁
lock.lock(10, TimeUnit.SECONDS);
```

### 6.2 公平锁(RFairLock)

```java
RLock fairLock = redisson.getFairLock("myfairlock");
fairLock.lock();
try {
    // 保护的代码块
} finally {
    fairLock.unlock();
}
```

### 6.3 读写锁(RReadWriteLock)

```java
RReadWriteLock rwlock = redisson.getReadWriteLock("myrwlock");

// 读锁（共享）
RLock readLock = rwlock.readLock();
readLock.lock();
try {
    // 读取操作
} finally {
    readLock.unlock();
}

// 写锁（排他）
RLock writeLock = rwlock.writeLock();
writeLock.lock();
try {
    // 写入操作
} finally {
    writeLock.unlock();
}
```

### 6.4 信号量(RSemaphore)

```java
RSemaphore semaphore = redisson.getSemaphore("mysemaphore");
// 初始化为5个许可
semaphore.trySetPermits(5);

// 获取1个许可
semaphore.acquire();
try {
    // 使用受限资源
} finally {
    // 释放1个许可
    semaphore.release();
}
```

## 7. 分布式服务

### 7.1 延迟队列(RDelayedQueue)

```java
// 创建普通队列
RQueue<String> destinationQueue = redisson.getQueue("myQueue");

// 创建延迟队列，关联到目标队列
RDelayedQueue<String> delayedQueue = redisson.getDelayedQueue(destinationQueue);

// 添加延迟消息
delayedQueue.offer("delayed message", 10, TimeUnit.SECONDS);

// 从目标队列消费消息
String msg = destinationQueue.poll();

// 关闭延迟队列
delayedQueue.destroy();
```

### 7.2 优先队列(RPriorityQueue)

```java
RPriorityQueue<Integer> priorityQueue = redisson.getPriorityQueue("mypriorityqueue");
priorityQueue.add(3);
priorityQueue.add(1);
priorityQueue.add(2);

Integer lowestValue = priorityQueue.poll(); // 返回1
```

### 7.3 发布订阅

```java
// 获取主题
RTopic topic = redisson.getTopic("mytopic");

// 订阅消息
topic.addListener(String.class, (channel, message) -> {
    System.out.println("Received: " + message);
});

// 发布消息
long receiverCount = topic.publish("Hello listeners!");
```

## 8. 分布式执行服务

### 8.1 远程执行

```java
// 远程执行服务
RExecutorService executorService = redisson.getExecutorService("myExecutor");

// 提交任务
Future<String> future = executorService.submit(() -> {
    // 这段代码将在任一Redisson节点执行
    return "任务执行结果";
});

// 获取结果
String result = future.get();
```

### 8.2 调度任务

```java
RScheduledExecutorService scheduledExecutorService = 
    redisson.getExecutorService("myScheduledExecutor");

// 延迟执行
scheduledExecutorService.schedule(
    () -> System.out.println("Task executed!"),
    10, TimeUnit.SECONDS);

// 固定频率执行
scheduledExecutorService.scheduleAtFixedRate(
    () -> System.out.println("Repeated task"),
    5, 10, TimeUnit.SECONDS);
```

## 9. 实用场景示例

### 9.1 分布式锁实现

```java
RLock lock = redisson.getLock("resourceLock");

// 尝试获取锁，最多等待100秒，锁自动释放时间为10秒
boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
if (isLocked) {
    try {
        // 临界区代码
        performCriticalOperation();
    } finally {
        // 释放锁
        lock.unlock();
    }
}
```

### 9.2 限流器实现

```java
// 创建限流器，每1秒钟产生10个许可
RRateLimiter rateLimiter = redisson.getRateLimiter("myRateLimiter");
rateLimiter.trySetRate(RateType.OVERALL, 10, 1, RateIntervalUnit.SECONDS);

// 尝试获取许可
boolean acquired = rateLimiter.tryAcquire(1);
if (acquired) {
    // 执行被限流保护的代码
    handleRequest();
} else {
    // 限流了，返回错误或稍后重试
    returnTooManyRequestsError();
}
```

### 9.3 分布式延迟队列实现

```java
// 创建普通队列
RBlockingQueue<String> queue = redisson.getBlockingQueue("jobQueue");

// 创建延迟队列
RDelayedQueue<String> delayedQueue = redisson.getDelayedQueue(queue);

// 生产者：添加10秒后执行的任务
delayedQueue.offer("job1", 10, TimeUnit.SECONDS);

// 消费者：阻塞等待任务
new Thread(() -> {
    try {
        while (true) {
            // 阻塞直到有元素可用
            String job = queue.take();
            System.out.println("Processing: " + job);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();
```

## 10. 高级功能

### 10.1 分布式对象桶(RBuckets)

```java
// 批量操作多个对象
RBuckets buckets = redisson.getBuckets();

Map<String, String> map = new HashMap<>();
map.put("key1", "value1");
map.put("key2", "value2");
map.put("key3", "value3");

// 批量设置
buckets.set(map);

// 批量获取
Map<String, String> loadedMap = buckets.get("key1", "key2");
```

### 10.2 原子操作

```java
RAtomicLong counter = redisson.getAtomicLong("mycounter");
counter.set(0);
long currentValue = counter.incrementAndGet();

RAtomicDouble atomicDouble = redisson.getAtomicDouble("mydouble");
double value = atomicDouble.addAndGet(1.5);
```

### 10.3 布隆过滤器

```java
RBloomFilter<String> bloomFilter = redisson.getBloomFilter("mybloomfilter");
// 初始化布隆过滤器，预计元素数量为10000，误判率为0.03
bloomFilter.tryInit(10000, 0.03);

// 添加元素
bloomFilter.add("item1");
bloomFilter.add("item2");

// 检查元素
boolean exists = bloomFilter.contains("item1");
boolean notExists = bloomFilter.contains("item3");
```

## 11. 常见问题与最佳实践

1. **妥善管理客户端生命周期**
   - 应用程序中只保持一个RedissonClient实例
   - 应用关闭前调用shutdown()方法

2. **正确处理锁释放**
   - 使用try-finally确保锁释放
   - 考虑使用看门狗机制自动续期的锁

3. **合理设置连接池参数**
   - 根据应用负载设置合适的连接数

4. **选择合适的数据结构**
   - 使用RedissonMap而非多个键值对，以便原子操作
   - 使用RLocalCachedMap提高读取性能

5. **异步API提升性能**
   - 使用异步API处理高并发操作

6. **避免大对象序列化**
   - Redisson默认使用Jackson序列化，大对象可能影响性能

---

这份学习笔记涵盖了Redisson的核心功能和使用方法，适合入门学习！实际使用中可以根据具体业务需求深入研究相关API。如有任何问题，欢迎交流讨论。