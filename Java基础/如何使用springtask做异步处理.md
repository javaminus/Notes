# 使用Spring Task实现异步处理

Spring Task提供了强大的异步处理能力，可以帮助您提升应用性能和响应速度。下面我将详细介绍如何在Spring Boot应用中配置和使用Spring Task进行异步处理。

```java
@Configuration
@EnableAsync
public class MultiThreadPoolConfig { // 配置多个线程池，同时开启异步，异步指定具体线程池

    @Bean(name = "interfaceTaskExecutor")
    public ThreadPoolTaskExecutor interfaceTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("InterfaceAsync-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "scheduleTaskExecutor")
    public ThreadPoolTaskExecutor scheduleTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ScheduleAsync-");
        executor.initialize();
        return executor;
    }
}

@Service
public class AsyncService {

    @Async("interfaceTaskExecutor") // 线程池名称
    public void handleApiTask() {
        // 接口异步处理逻辑
    }

    @Async("scheduleTaskExecutor") // 线程池名称
    public void handleScheduleTask() {
        // 定时任务逻辑
    }
}
```



## 1. 基础配置

### 添加依赖
Spring Boot已内置Spring Task支持，无需额外依赖：
```xml
<!-- Spring Boot 项目已默认包含，无需额外添加 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
```

### 启用异步处理
```java
@Configuration
@EnableAsync  // 启用异步处理
public class AsyncConfig {
    
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(500);
        // 线程名称前缀
        executor.setThreadNamePrefix("Async-");
        // 拒绝策略：调用者运行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化
        executor.initialize();
        return executor;
    }
}
```

## 2. 异步方法实现

### 基础异步方法
```java
@Service
@Slf4j
public class NotificationService {

    @Async("taskExecutor")
    public void sendNotificationAsync(String message) {
        log.info("开始发送通知: {}", message);
        try {
            // 模拟耗时操作
            Thread.sleep(2000);
            log.info("通知发送完成: {}", message);
        } catch (InterruptedException e) {
            log.error("通知发送中断", e);
            Thread.currentThread().interrupt();
        }
    }
}
```

### 带返回值的异步方法
```java
@Async("taskExecutor")
public CompletableFuture<String> processDataAsync(String input) {
    log.info("异步处理数据: {}", input);
    try {
        // 模拟耗时处理
        Thread.sleep(3000);
        String result = "处理结果: " + input;
        log.info("数据处理完成");
        return CompletableFuture.completedFuture(result);
    } catch (InterruptedException e) {
        log.error("数据处理中断", e);
        Thread.currentThread().interrupt();
        return CompletableFuture.failedFuture(e);
    }
}
```

## 3. 高级配置

### 自定义异常处理器
```java
@Configuration
public class AsyncExceptionConfig implements AsyncConfigurer {
    
    @Override
    public Executor getAsyncExecutor() {
        // 返回上面定义的executor
        return taskExecutor();
    }
    
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("异步方法执行异常 - 方法:[{}], 参数:{}, 异常:{}", 
                method.getName(), Arrays.toString(params), ex.getMessage());
            // 可以添加告警通知等逻辑
        };
    }
}
```

### 定时任务结合异步处理
```java
@Configuration
@EnableScheduling  // 启用定时任务
public class ScheduleConfig {
    // 配置可留空，使用默认配置
}

@Service
public class DataProcessingService {
    
    @Autowired
    private NotificationService notificationService;

    // 每天凌晨1点执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduledDataProcessing() {
        log.info("开始定时数据处理任务");
        List<String> dataItems = fetchDataItems();
        
        // 并行处理多条数据
        dataItems.forEach(item -> {
            notificationService.sendNotificationAsync("处理数据项: " + item);
        });
        
        log.info("定时任务触发完成，已提交{}个异步处理任务", dataItems.size());
    }
    
    private List<String> fetchDataItems() {
        // 模拟获取数据
        return Arrays.asList("数据1", "数据2", "数据3");
    }
}
```

## 4. 实际应用场景

### 异步发送钉钉通知
```java
@Service
@Slf4j
public class DingDingService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${dingding.webhook}")
    private String webhook;
    
    @Async("taskExecutor") // taskExecutor是线程池的名称
    public CompletableFuture<Boolean> sendAlertAsync(String title, String content) {
        log.info("开始异步发送钉钉告警: {}", title);
        try {
            // 构建钉钉消息
            Map<String, Object> message = buildDingDingMessage(title, content);
            
            // 发送HTTP请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                webhook, message, String.class);
                
            boolean success = response.getStatusCode().is2xxSuccessful();
            log.info("钉钉告警发送{}: {}", success ? "成功" : "失败", title);
            
            return CompletableFuture.completedFuture(success);
        } catch (Exception e) {
            log.error("钉钉告警发送异常", e);
            return CompletableFuture.completedFuture(false);
        }
    }
    
    private Map<String, Object> buildDingDingMessage(String title, String content) {
        // 构建钉钉消息格式
        Map<String, Object> message = new HashMap<>();
        // 填充消息内容...
        return message;
    }
}
```

### 批量数据处理
```java
@Service
public class BatchProcessService {

    @Autowired
    private ApplicationContext context;
    
    public void processBatchData(List<Data> dataList) {
        int batchSize = 100;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        // 分批处理
        for (int i = 0; i < dataList.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, dataList.size());
            List<Data> batch = dataList.subList(i, endIndex);
            
            // 自我注入获取代理对象，确保@Async生效
            BatchProcessService proxy = context.getBean(BatchProcessService.class);
            futures.add(proxy.processDataBatchAsync(batch));
        }
        
        // 等待所有异步任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("所有批次处理完成，共{}条数据", dataList.size());
    }
    
    @Async("taskExecutor")
    public CompletableFuture<Void> processDataBatchAsync(List<Data> batch) {
        log.info("开始处理批次数据，数量: {}", batch.size());
        // 处理逻辑...
        return CompletableFuture.completedFuture(null);
    }
}
```

## 5. 最佳实践

### 线程池隔离
为不同业务场景配置专用线程池：
```java
@Configuration
@EnableAsync
public class ThreadPoolConfig {
    
    @Bean("notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Notify-");
        executor.initialize();
        return executor;
    }
    
    @Bean("dataProcessExecutor") 
    public Executor dataProcessExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("DataProc-");
        executor.initialize();
        return executor;
    }
}
```

### 超时控制
```java
public String processWithTimeout(String input) {
    try {
        CompletableFuture<String> future = processDataAsync(input);
        // 设置超时时间为5秒
        return future.get(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return "处理被中断";
    } catch (TimeoutException e) {
        return "处理超时";
    } catch (ExecutionException e) {
        return "处理发生错误: " + e.getCause().getMessage();
    }
}
```

## 6. 注意事项

1. **同类内调用问题**：直接调用同类中的@Async方法不会生效，需通过自我注入获取代理对象
2. **事务传播**：异步方法会在新线程中执行，不会继承原有事务
3. **异常处理**：异步方法中的异常不会传播到调用方，需要适当的异常处理机制
4. **线程池监控**：在生产环境中应监控线程池状态，防止资源耗尽
5. **参数传递**：避免传递大对象，可能导致序列化问题和内存压力

Spring Task是实现异步处理的强大工具，合理配置和使用可以显著提升应用性能和用户体验。

# 追问

### 1. CompletableFuture和Future的具体区别

Future是Java 5引入的异步编程接口，而CompletableFuture是Java 8引入的增强版本，具体区别有：

- **手动完成能力**：Future只能等待计算完成或取消，而CompletableFuture可以通过`complete()`、`completeExceptionally()`手动完成。
- **回调机制**：Future只能通过阻塞的`get()`获取结果，而CompletableFuture提供了非阻塞回调如`thenApply()`、`thenAccept()`等。
- **组合能力**：Future无法组合多个异步操作，CompletableFuture可通过`thenCompose()`、`thenCombine()`、`allOf()`等实现复杂组合。
- **异常处理**：Future的异常处理有限，CompletableFuture提供`exceptionally()`、`handle()`等专门处理异常的方法。
- **默认线程池**：CompletableFuture默认使用ForkJoinPool.commonPool()，而Future需要显式提供Executor。

### 2. 如何处理CompletableFuture中的异常传播问题

CompletableFuture提供了多种异常处理机制：

- **exceptionally(Function)**：当上游发生异常时执行，可返回替代结果。
```java
CompletableFuture.supplyAsync(() -> { throw new RuntimeException("error"); })
    .exceptionally(ex -> "默认值")
```

- **handle(BiFunction)**：无论是否发生异常都会执行，可处理正常结果或异常。
```java
future.handle((result, ex) -> ex != null ? "出错了: " + ex.getMessage() : result)
```

- **whenComplete(BiConsumer)**：用于观察结果或异常，但不改变结果或异常状态。

- **异常传播机制**：异常会沿着CompletableFuture链向下传播，直到被处理或到达链尾。

- **捕获位置**：建议在靠近CompletableFuture链终点处理异常，集中处理多个阶段可能产生的异常。

### 3. thenApply和thenCompose有什么区别

这两个方法代表了函数式编程中的两种重要操作：

- **thenApply (map操作)**：
  - 将一个值转换为另一个值 (T -> U)
  - 返回嵌套的CompletableFuture: CompletableFuture<CompletableFuture<U>>
  - 适用于结果转换，不涉及新的异步操作
  ```java
  CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "100")
      .thenApply(s -> Integer.parseInt(s))
      .thenApply(i -> i * 2)
      .thenApply(i -> "结果: " + i);
  ```

- **thenCompose (flatMap操作)**：
  - 将一个CompletableFuture转换为另一个CompletableFuture (T -> CompletableFuture<U>)
  - 返回平铺的结果: CompletableFuture<U>
  - 适用于需要基于上一步结果再次异步操作的场景，避免嵌套
  ```java
  CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "用户ID:100")
      .thenCompose(id -> getUserDetail(id)); // getUserDetail返回CompletableFuture<String>
  ```

### 4. 高并发场景下使用CompletableFuture注意事项

- **自定义线程池**：避免使用默认ForkJoinPool，它在高负载下可能导致线程饥饿，影响整个应用性能。
```java
ExecutorService executor = new ThreadPoolExecutor(10, 20, 60L, TimeUnit.SECONDS, 
    new LinkedBlockingQueue<>(500), new ThreadFactoryBuilder().setNameFormat("async-pool-%d").build());
CompletableFuture.supplyAsync(task, executor);
```

- **线程池隔离**：不同业务场景使用独立线程池，避免互相影响。

- **资源限制**：合理设置线程池参数和队列容量，添加拒绝策略。

- **超时控制**：总是设置超时机制，防止任务无限等待。

- **异常处理**：确保捕获所有异常，防止未处理异常导致线程终止。

- **避免太多小任务**：将小任务合并，减少线程上下文切换开销。

- **结果聚合性能**：大量CompletableFuture.allOf()可能带来性能问题，考虑分批处理。

### 5. CompletableFuture的内部实现机制

CompletableFuture实现相当精巧：

- **状态管理**：内部使用volatile变量存储结果和完成状态。

- **观察者模式**：通过Completion内部类实现回调链，每个thenXxx操作会创建一个Completion节点。

- **栈帧安全**：使用栈帧友好的方式触发回调，避免长链时可能的栈溢出。

- **双重队列**：维护等待线程队列和回调操作队列。

- **CAS操作**：使用无锁化的CAS(Compare-And-Swap)保证线程安全。

- **延迟执行**：回调在源CompletableFuture完成时被触发。

- **异常传播**：记录并传播异常到依赖的CompletableFuture。

### 6. 如何实现CompletableFuture的超时控制

- **Java 9+ 内置方法**：
```java
// 超时抛异常
future.orTimeout(5, TimeUnit.SECONDS)
// 超时返回默认值
future.completeOnTimeout("默认值", 5, TimeUnit.SECONDS)
```

- **Java 8 自定义实现**：
```java
public static <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
    CompletableFuture<T> result = new CompletableFuture<>();
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    scheduler.schedule(() -> {
        result.completeExceptionally(new TimeoutException());
        scheduler.shutdown();
    }, timeout, unit);
    
    return result;
}

// 使用
CompletableFuture<String> future = CompletableFuture.supplyAsync(this::longRunningTask);
CompletableFuture<String> futureWithTimeout = 
    CompletableFuture.anyOf(future, timeoutAfter(5, TimeUnit.SECONDS))
        .thenApply(result -> (String)result);
```

- **与外部库结合**：Resilience4j或Guava的TimeLimiter提供更丰富的超时功能。

### 7. CompletableFuture和Java 9的Flow API关系

- **不同定位**：
  - CompletableFuture处理单次异步计算
  - Flow API实现响应式流处理（发布-订阅模式）

- **数据处理差异**：
  - CompletableFuture: 一次性计算单个结果
  - Flow API: 处理无限数据流，支持背压(backpressure)

- **集成方式**：可以在Flow.Processor实现中使用CompletableFuture处理单个元素。

- **技术演进**：
  - CompletableFuture是Java对Promise/Future模式的实现
  - Flow API是响应式编程规范(Reactive Streams)的Java标准化

- **实际使用**：CompletableFuture适合并行API调用，Flow API适合事件流和数据流处理。

### 8. 实际项目中管理和监控CompletableFuture

- **线程池命名与监控**：
```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(/*参数*/);
executor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat("async-task-%d").build());
// 定期收集线程池指标
```

- **MDC上下文传递**：确保日志上下文在异步操作中保留。

- **跟踪ID传递**：通过自定义包装器传递跟踪ID。

- **指标收集**：
  - 执行时间分布
  - 成功/失败率
  - 拒绝率
  - 线程池饱和度

- **分布式追踪**：与Sleuth/Zipkin等集成，追踪异步调用链路。

- **健康检查**：定期检查线程池健康状态。

- **优雅关闭**：应用关闭时等待进行中的Future完成或超时。

- **取消机制**：实现超时或用户取消后的资源释放。

- **基于AOP的统一监控**：通过切面收集所有异步操作指标。