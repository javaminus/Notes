# 使用Spring Task实现异步处理

Spring Task提供了强大的异步处理能力，可以帮助您提升应用性能和响应速度。下面我将详细介绍如何在Spring Boot应用中配置和使用Spring Task进行异步处理。

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