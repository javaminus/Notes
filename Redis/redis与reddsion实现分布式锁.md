# 分布式锁详解：Redis 与 Redisson 实现指南

## 目录
- [1. 分布式锁基础概念](#1-分布式锁基础概念)
- [2. 使用 Redis 实现分布式锁](#2-使用-redis-实现分布式锁)
- [3. 使用 Redisson 实现分布式锁](#3-使用-redisson-实现分布式锁)
- [4. 两种方式的对比](#4-两种方式的对比)
- [5. 常见问题与解决方案](#5-常见问题与解决方案)
- [6. 实战示例](#6-实战示例)

## 1. 分布式锁基础概念

### 什么是分布式锁？

在单机应用中，我们可以使用 Java 的 `synchronized` 或 `Lock` 接口来实现线程间的互斥。但在分布式系统中，应用部署在多台服务器上，普通的锁无法跨进程工作，这时我们需要分布式锁。

分布式锁的核心功能：
- **互斥性**：在任意时刻，只有一个客户端能持有锁
- **防死锁**：即使持有锁的客户端崩溃，锁也能被释放
- **可重入**：同一个客户端可以多次获取同一把锁
- **高性能和可用性**：加锁解锁要快，且要能应对部分节点故障

### 为什么选择 Redis 实现分布式锁？

- **性能高**：Redis 操作是内存级别的，响应速度极快
- **原子性操作**：Redis 提供了许多原子性操作命令
- **可靠性**：可以通过过期时间防止死锁
- **易用性**：相比 Zookeeper 等方案，实现相对简单

## 2. 使用 Redis 实现分布式锁

### 基本原理

Redis 实现分布式锁主要利用了它的原子性命令。最基本的思路是：
1. 使用 `SETNX` (SET if Not eXists) 命令设置一个键值对
2. 如果键不存在，则设置成功并获取锁
3. 如果键已存在，则设置失败，获取锁失败
4. 操作完成后，删除键以释放锁

### 详细步骤

#### 2.1 获取锁

早期版本使用：
```
SETNX lock_key unique_value  # 尝试获取锁
EXPIRE lock_key 30  # 设置过期时间
```

但这不是原子操作，可能在 SETNX 成功后、EXPIRE 前服务崩溃导致死锁。

Redis 2.6.12 版本后推荐使用：
```
SET lock_key unique_value NX PX 30000
```

参数说明：
- `lock_key`：锁的名称
- `unique_value`：锁的唯一标识符，通常使用UUID生成
- `NX`：只有当key不存在时才执行SET操作
- `PX 30000`：设置30秒过期时间（毫秒单位）

#### 2.2 释放锁

释放锁需要确保只有获取锁的客户端才能释放锁，用Lua脚本确保操作原子性：

```lua
if redis.call("get", KEYS[1]) == ARGV[1] then
    return redis.call("del", KEYS[1])
else
    return 0
end
```

### 2.4 使用原生Redis的缺点

- 需要自己处理各种边界情况
- 锁续期困难（如业务逻辑执行时间超过锁过期时间）
- 可重入实现复杂
- 无法实现读写锁等高级特性
- 分布式环境下单点Redis不够可靠

## 3. 使用 Redisson 实现分布式锁

### 3.1 Redisson 简介

Redisson 是一个在 Redis 基础上实现的 Java 驻内存数据网格（In-Memory Data Grid），它不仅提供了一系列分布式的 Java 常用对象，还提供了许多分布式服务，其中就包含了分布式锁的实现。

相比原生Redis，Redisson提供了更加易用和强大的分布式锁实现。

### 3.2 引入Redisson依赖

Maven:
```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.17.0</version>
</dependency>
```

### 3.3 Redisson 实现分布式锁的基本用法

```java
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class RedissonLockExample {
    public static void main(String[] args) {
        // 1. 创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        
        // 2. 创建Redisson客户端
        RedissonClient redisson = Redisson.create(config);
        
        // 3. 获取锁对象
        RLock lock = redisson.getLock("myLock");
        
        try {
            // 4. 尝试获取锁
            // 参数：等待获取锁的最大时间，锁自动释放时间，时间单位
            boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            
            if (isLocked) {
                try {
                    // 5. 执行业务逻辑
                    System.out.println("获取锁成功，执行业务逻辑");
                    Thread.sleep(5000);
                } finally {
                    // 6. 释放锁
                    lock.unlock();
                    System.out.println("业务执行完毕，释放锁");
                }
            } else {
                System.out.println("获取锁失败");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 7. 关闭Redisson客户端
            redisson.shutdown();
        }
    }
}
```

### 3.4 Redisson 分布式锁的高级特性

#### 1. 可重入锁

同一个线程可以多次获取同一把锁：

```java
RLock lock = redisson.getLock("myLock");

lock.lock();  // 第一次获取锁
lock.lock();  // 第二次获取锁（可重入）

try {
    // 业务逻辑
} finally {
    lock.unlock();  // 第一次释放
    lock.unlock();  // 第二次释放
}
```

#### 2. 读写锁

```java
RReadWriteLock rwlock = redisson.getReadWriteLock("myReadWriteLock");

// 获取读锁
RLock readLock = rwlock.readLock();
readLock.lock();
try {
    // 读取操作
} finally {
    readLock.unlock();
}

// 获取写锁
RLock writeLock = rwlock.writeLock();
writeLock.lock();
try {
    // 写入操作
} finally {
    writeLock.unlock();
}
```

#### 3. 联锁（MultiLock）

同时获取多个锁：

```java
RLock lock1 = redisson1.getLock("lock1");
RLock lock2 = redisson2.getLock("lock2");
RLock lock3 = redisson3.getLock("lock3");

// 联锁
RLock multiLock = redisson.getMultiLock(lock1, lock2, lock3);
multiLock.lock();
try {
    // 所有锁都获取后才执行
} finally {
    multiLock.unlock();
}
```

#### 4. 红锁（RedLock）

基于多个独立的Redis节点，提高可靠性：

```java
RLock lock1 = redisson1.getLock("lock");
RLock lock2 = redisson2.getLock("lock");
RLock lock3 = redisson3.getLock("lock");

// 红锁
RLock redLock = redisson.getRedLock(lock1, lock2, lock3);
redLock.lock();
try {
    // 业务逻辑
} finally {
    redLock.unlock();
}
```

#### 5. 公平锁

按照请求顺序获取锁：

```java
RLock fairLock = redisson.getFairLock("fairLock");
fairLock.lock();
try {
    // 业务逻辑
} finally {
    fairLock.unlock();
}
```

#### 6. 自动续期机制

Redisson的看门狗（Watchdog）机制会在锁快要过期时自动续期，防止业务未执行完锁就过期的问题。

## 4. 两种方式的对比

| 特性         | 原生Redis                      | Redisson                              |
| ------------ | ------------------------------ | ------------------------------------- |
| 实现难度     | 较高，需要自己处理各种边界情况 | 较低，API简单易用                     |
| 锁类型       | 基础锁                         | 可重入锁、公平锁、读写锁等多种类型    |
| 自动续期     | 需要自行实现                   | 内置看门狗机制自动续期                |
| 可重入性     | 需要自行实现                   | 原生支持                              |
| 分布式可靠性 | 单点Redis不可靠                | 支持主从、哨兵、集群模式，支持RedLock |
| 监控与调试   | 困难                           | 有完善的监控指标                      |
| 适用场景     | 简单场景或对Redis精通          | 企业级应用、复杂分布式环境            |

## 5. 常见问题与解决方案

### 5.1 锁过期问题

**问题**：如果锁设置了过期时间，业务执行时间超过过期时间，锁会被自动释放，其他客户端可能获取到锁。

**解决方案**：
- 使用Redisson的自动续期机制
- 手动实现定时任务检查和续期

### 5.2 解锁安全问题

**问题**：如果客户端A获取了锁，但执行时间过长，锁过期了，客户端B获取了同一把锁，这时客户端A执行完成去释放锁，就会错误地释放客户端B的锁。

**解决方案**：
- 锁值使用唯一标识（如UUID）
- 释放锁时检查当前锁是否是自己设置的（使用Lua脚本保证原子性）

### 5.3 单点故障问题

**问题**：如果Redis单点部署，Redis宕机会导致锁服务不可用。

**解决方案**：
- 使用Redis主从/哨兵/集群
- 使用Redisson的RedLock算法

### 5.4 锁超时与业务执行冲突

**问题**：业务执行时间不确定，难以设置合适的锁超时时间。

**解决方案**：
- Redisson的自动续期机制
- 手动实现锁续期逻辑

### 5.5 锁竞争激烈

**问题**：高并发场景下，大量线程竞争同一把锁导致性能下降。

**解决方案**：
- 使用分段锁，减小锁粒度
- 使用公平锁或信号量控制并发量

## 6. 实战示例

### 6.1 使用Redisson实现库存扣减

```java
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

public class InventoryService {
    private final RedissonClient redisson;
    private final String INVENTORY_LOCK_PREFIX = "inventory_lock:";
    
    // 模拟的商品库存
    private int stock = 10;
    
    public InventoryService() {
        // 创建Redisson客户端
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        this.redisson = Redisson.create(config);
    }
    
    /**
     * 扣减库存
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 是否成功
     */
    public boolean deductStock(String productId, int quantity) {
        String lockKey = INVENTORY_LOCK_PREFIX + productId;
        RLock lock = redisson.getLock(lockKey);
        
        try {
            // 尝试获取锁，最多等待5秒，锁自动释放时间30秒
            boolean isLocked = lock.tryLock(5, 30, TimeUnit.SECONDS);
            
            if (isLocked) {
                try {
                    System.out.println("获得商品[" + productId + "]的锁，准备扣减库存");
                    
                    // 模拟从数据库读取最新库存
                    System.out.println("当前库存: " + stock);
                    
                    // 判断库存是否足够
                    if (stock >= quantity) {
                        // 模拟扣减库存的操作耗时
                        Thread.sleep(1000);
                        
                        // 扣减库存
                        stock -= quantity;
                        System.out.println("扣减成功，剩余库存: " + stock);
                        return true;
                    } else {
                        System.out.println("库存不足，扣减失败");
                        return false;
                    }
                } finally {
                    // 释放锁
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        System.out.println("库存锁释放完成");
                    }
                }
            } else {
                System.out.println("获取锁失败，请稍后重试");
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void shutdown() {
        redisson.shutdown();
    }
    
    public static void main(String[] args) {
        final InventoryService service = new InventoryService();
        
        // 模拟5个线程同时扣减库存
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            new Thread(() -> {
                service.deductStock("iPhone13", 2);
            }, "Thread-" + threadId).start();
        }
        
        // 等待所有线程执行完
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 关闭Redisson客户端
        service.shutdown();
    }
}
```

### 6.2 基于Spring Boot的完整实现

**1. 添加依赖**

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Redisson -->
    <dependency>
        <groupId>org.redisson</groupId>
        <artifactId>redisson-spring-boot-starter</artifactId>
        <version>3.17.0</version>
    </dependency>
</dependencies>
```

**2. 配置 Redisson**

```java
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
              .setAddress("redis://127.0.0.1:6379")
              .setDatabase(0);
        return Redisson.create(config);
    }
}
```

**3. 分布式锁服务**

```java
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class DistributedLockService {
    
    private final RedissonClient redissonClient;
    
    @Autowired
    public DistributedLockService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
    
    /**
     * 使用分布式锁执行业务逻辑
     * @param lockKey 锁的Key
     * @param waitTime 等待获取锁的最大时间
     * @param leaseTime 锁自动释放时间，为-1时使用watchdog机制
     * @param timeUnit 时间单位
     * @param supplier 业务逻辑
     * @param <T> 返回结果类型
     * @return 业务执行结果
     * @throws Exception 业务执行异常
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, 
                               TimeUnit timeUnit, Supplier<T> supplier) throws Exception {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (locked) {
                return supplier.get();
            } else {
                throw new RuntimeException("获取锁失败");
            }
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    /**
     * 使用分布式锁执行无返回值的业务逻辑
     */
    public void executeWithLock(String lockKey, long waitTime, long leaseTime, 
                             TimeUnit timeUnit, Runnable runnable) throws Exception {
        executeWithLock(lockKey, waitTime, leaseTime, timeUnit, () -> {
            runnable.run();
            return null;
        });
    }
}
```

**4. 业务服务**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OrderService {
    
    private final DistributedLockService lockService;
    
    // 模拟库存
    private int stock = 100;
    
    @Autowired
    public OrderService(DistributedLockService lockService) {
        this.lockService = lockService;
    }
    
    /**
     * 创建订单并扣减库存
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 数量
     * @return 订单ID
     */
    public String createOrder(String userId, String productId, int quantity) {
        String lockKey = "product_lock:" + productId;
        
        try {
            return lockService.executeWithLock(lockKey, 5, -1, TimeUnit.SECONDS, () -> {
                // 检查库存
                if (stock < quantity) {
                    throw new RuntimeException("库存不足");
                }
                
                // 模拟订单创建
                String orderId = "ORD" + System.currentTimeMillis();
                
                // 扣减库存
                stock -= quantity;
                System.out.printf("用户%s购买商品%s，数量%d，剩余库存%d，订单号%s\n", 
                                 userId, productId, quantity, stock, orderId);
                
                return orderId;
            });
        } catch (Exception e) {
            System.err.println("创建订单失败: " + e.getMessage());
            return null;
        }
    }
    
    public int getStock() {
        return stock;
    }
}
```

**5. Controller层**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping("/create")
    public Map<String, Object> createOrder(@RequestParam String userId,
                                          @RequestParam String productId,
                                          @RequestParam int quantity) {
        Map<String, Object> result = new HashMap<>();
        
        String orderId = orderService.createOrder(userId, productId, quantity);
        if (orderId != null) {
            result.put("success", true);
            result.put("orderId", orderId);
            result.put("remainingStock", orderService.getStock());
        } else {
            result.put("success", false);
            result.put("message", "创建订单失败");
        }
        
        return result;
    }
}
```

**6. 启动类**

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DistributedLockApplication {
    public static void main(String[] args) {
        SpringApplication.run(DistributedLockApplication.class, args);
    }
}
```

## 总结

1. **Redis原生分布式锁**：
   - 基于SETNX + 过期时间实现
   - 需要处理各种边界情况
   - 释放锁时要确保只有持有者能释放
   - 适合简单场景或对Redis非常熟悉的开发者

2. **Redisson分布式锁**：
   - 封装了分布式锁的复杂实现
   - 提供了可重入锁、公平锁、读写锁等多种锁类型
   - 内置看门狗机制自动续期，解决锁过期问题
   - 支持Redis多种部署模式，可靠性高
   - 企业级应用的首选方案

无论选择哪种方式，都需要理解分布式锁的基本原理和潜在问题，以便在实际应用中做出合理选择并正确使用。

对于初学者，建议先从Redisson入手，它封装了大部分复杂细节，使用简单且功能强大。掌握了Redisson后，再深入学习Redis原生实现的原理，有助于更全面地理解分布式锁。