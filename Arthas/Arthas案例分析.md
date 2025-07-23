# Arthas 实战案例：排查线上服务 CPU 飙升问题

## 问题背景

我们的电商订单服务在双十一活动期间突然出现 CPU 使用率飙升至 95%，API 响应时间从正常的 200ms 增加到 2000ms+，影响了用户下单体验。监控系统发出大量告警，但日志中没有明显错误信息。

## 环境信息

- 应用: Java 微服务 (OrderService)
- JDK版本: 1.8.0_252
- 服务器: 8核16G AWS EC2实例
- 框架: Spring Boot 2.3.4

## 问题排查步骤

### 步骤1：安装并启动 Arthas

```bash
# 下载Arthas
wget https://arthas.aliyun.com/arthas-boot.jar

# 启动Arthas并连接目标Java进程
java -jar arthas-boot.jar

# 系统列出所有Java进程，选择OrderService对应的进程号(例如选择3号进程)
[INFO] arthas-boot version: 3.5.5
[INFO] Found existing java process, please choose one and input the serial number of the process, eg : 1. Then hit ENTER.
* [1]: 1234 org.elasticsearch.bootstrap.Elasticsearch
  [2]: 2341 org.apache.zookeeper.server.quorum.QuorumPeerMain
  [3]: 3456 com.example.orderservice.OrderServiceApplication
  [4]: 4567 org.apache.catalina.startup.Bootstrap
3
```

### 步骤2：使用 dashboard 查看系统整体情况

```bash
$ dashboard
ID     NAME                          GROUP           PRIORITY   STATE     %CPU      DELTA_TIME TIME      INTERRUPTED DAEMON
39     AsyncTaskExecutor-3           main            5          RUNNABLE  28.00     0.140      0:12.978  false       false
23     scheduling-1                  main            5          TIMED_WAI 0.94      0.004      0:9.281   false       true
11     Attach Listener               system          9          RUNNABLE  0.00      0.000      0:0.000   false       true
12     GC Thread#0                   system          9          RUNNABLE  0.00      0.000      0:0.000   false       true
...
Memory             used      total     max       usage     GC
heap               1265M     1843M     4096M     30.88%    gc.ps_scavenge.count          130
ps_eden_space      364M      668M      1365M     26.69%    gc.ps_scavenge.time(ms)       2125
ps_survivor_space  35M       78M       78M       45.33%    gc.ps_marksweep.count         6
ps_old_gen         866M      1097M     2731M     31.70%    gc.ps_marksweep.time(ms)      1799
```

观察到 `AsyncTaskExecutor-3` 线程 CPU 占用异常高，达到 28%。

### 步骤3：使用 thread 命令查看高 CPU 线程详情

```bash
$ thread -n 3
"AsyncTaskExecutor-3" Id=39 RUNNABLE at 28% CPU
    at java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:174)
    at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382)
    at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
    at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
    at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
    at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
    at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
    at com.example.orderservice.service.PromotionService.calculateDiscount(PromotionService.java:67)
    at com.example.orderservice.service.OrderService.createOrder(OrderService.java:128)
    at com.example.orderservice.controller.OrderController.submitOrder(OrderController.java:54)
```

发现问题主要出现在 `PromotionService.calculateDiscount` 方法，使用了 Java Stream API。

### 步骤4：使用 jad 反编译查看有问题的代码

```bash
$ jad com.example.orderservice.service.PromotionService calculateDiscount

ClassLoader:
+-sun.misc.Launcher$AppClassLoader@18b4aac2
  +-sun.misc.Launcher$ExtClassLoader@5451c3a8

Location:
/app/orderservice.jar

public BigDecimal calculateDiscount(Order order) {
    // 双十一活动特殊计算逻辑
    if (isPromotionActive()) {
        List<OrderItem> items = order.getItems();
        
        // 问题代码！每次调用都查询所有商品的促销规则
        List<PromotionRule> allRules = promotionRuleRepository.findAllActiveRules();
        
        return items.stream()
            .map(item -> {
                // 对每个商品项，筛选所有可用的促销规则
                List<PromotionRule> applicableRules = allRules.stream()
                    .filter(rule -> isApplicable(rule, item))
                    .collect(Collectors.toList());
                
                // 对每个适用规则计算折扣，取最大值
                BigDecimal maxDiscount = BigDecimal.ZERO;
                for (PromotionRule rule : applicableRules) {
                    BigDecimal discount = calculateItemDiscount(item, rule);
                    if (discount.compareTo(maxDiscount) > 0) {
                        maxDiscount = discount;
                    }
                }
                return maxDiscount;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    return BigDecimal.ZERO;
}
```

发现问题：每次调用 `calculateDiscount` 都查询数据库获取所有促销规则，且没有缓存。

### 步骤5：使用 monitor 命令监控方法执行情况

```bash
$ monitor -c 5 com.example.orderservice.service.PromotionService calculateDiscount
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 26 ms, listenerId: 1
 timestamp            class                                           method         total  success  fail  avg-rt(ms)  fail-rate
 2023-11-11 15:41:58  com.example.orderservice.service.PromotionService  calculateDiscount  329     329      0     612.35      0.00%
 2023-11-11 15:42:03  com.example.orderservice.service.PromotionService  calculateDiscount  342     342      0     637.42      0.00%
 2023-11-11 15:42:08  com.example.orderservice.service.PromotionService  calculateDiscount  298     298      0     593.17      0.00%
```

该方法平均执行时间超过600ms，且每5秒执行超过300次。

### 步骤6：使用 trace 命令分析方法内部调用耗时

```bash
$ trace com.example.orderservice.service.PromotionService calculateDiscount -n 3
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 45 ms, listenerId: 2
`---ts=2023-11-11 15:43:01;thread_name=AsyncTaskExecutor-3;id=39;is_daemon=false;priority=5;TCCL=org.springframework.boot.loader.LaunchedURLClassLoader@66350f69
    `---[628.06ms] com.example.orderservice.service.PromotionService:calculateDiscount()
        +---[0.01ms] com.example.orderservice.service.PromotionService:isPromotionActive()
        +---[0.00ms] com.example.orderservice.domain.Order:getItems()
        +---[423.71ms] com.example.orderservice.repository.PromotionRuleRepository:findAllActiveRules() #主要耗时点
        +---[204.22ms] java.util.stream.Stream:map() #Stream处理耗时
        `---[0.09ms] java.math.BigDecimal:add()
```

发现 `promotionRuleRepository.findAllActiveRules()` 是主要耗时点，占据了总时间的67%。

### 步骤7：查看PromotionRuleRepository的实现

```bash
$ jad com.example.orderservice.repository.PromotionRuleRepository findAllActiveRules

public List<PromotionRule> findAllActiveRules() {
    String sql = "SELECT * FROM promotion_rules WHERE status = 'ACTIVE' AND start_time <= NOW() AND end_time >= NOW()";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PromotionRule.class));
}
```

### 步骤8：使用 watch 命令查看方法返回值

```bash
$ watch com.example.orderservice.repository.PromotionRuleRepository findAllActiveRules "{params, returnObj.size()}" -n 1
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 67 ms, listenerId: 3
ts=2023-11-11 15:44:32; [cost=424.11ms] result=@ArrayList[
    [],
    @Integer[5463],
]
```

发现该方法每次返回超过5000条促销规则记录！

### 步骤9：使用 profiler 分析热点

```bash
$ profiler start
Started [cpu] profiling
```

等待一段时间后停止并生成报告：

```bash
$ profiler stop --format html
profiler output file: /tmp/arthas-output/20231111154532_profiler.html
```

分析报告进一步确认了 `calculateDiscount` 和 `findAllActiveRules` 的性能瓶颈。

## 问题根因与解决方案

### 根因：

1. 每次调用 `calculateDiscount` 都会查询数据库获取所有促销规则(5000+条)
2. 对大量订单项应用Stream操作，重复计算
3. 没有任何缓存机制

### 解决方案：

1. **添加缓存**：将促销规则缓存在本地或使用Redis
   ```java
   @Cacheable(value = "promotionRules", key = "'activeRules'")
   public List<PromotionRule> findAllActiveRules() {
       String sql = "SELECT * FROM promotion_rules WHERE status = 'ACTIVE' AND start_time <= NOW() AND end_time >= NOW()";
       return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PromotionRule.class));
   }
   ```

2. **优化算法**：使用索引结构加速规则匹配
   ```java
   // 预处理规则，按商品ID或分类建立索引
   private Map<Long, List<PromotionRule>> rulesByProductId = new HashMap<>();
   
   public void initRuleIndex() {
       List<PromotionRule> rules = findAllActiveRules();
       // 构建索引
       for (PromotionRule rule : rules) {
           if (rule.getProductId() != null) {
               rulesByProductId.computeIfAbsent(rule.getProductId(), k -> new ArrayList<>()).add(rule);
           }
       }
   }
   
   // 查找商品适用规则时直接通过索引获取
   private List<PromotionRule> findApplicableRules(OrderItem item) {
       return rulesByProductId.getOrDefault(item.getProductId(), Collections.emptyList());
   }
   ```

3. **优化数据库查询**：只获取需要的字段，添加合适的索引

## 解决后的验证

应用修复后，使用Arthas再次检查性能：

```bash
$ monitor -c 5 com.example.orderservice.service.PromotionService calculateDiscount
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 23 ms, listenerId: 4
 timestamp            class                                           method         total  success  fail  avg-rt(ms)  fail-rate
 2023-11-11 17:21:32  com.example.orderservice.service.PromotionService  calculateDiscount  351     351      0     12.35       0.00%
 2023-11-11 17:21:37  com.example.orderservice.service.PromotionService  calculateDiscount  329     329      0     11.42       0.00%
```

方法平均执行时间从600ms降至12ms，性能提升约50倍。CPU使用率也从95%降至正常的25%左右。

## 总结与学到的经验

1. **Arthas命令组合使用**：
   - `dashboard` 发现高CPU线程
   - `thread` 查看问题线程栈
   - `jad` 反编译查看源码
   - `monitor` 监控方法执行情况
   - `trace` 分析方法内部调用耗时
   - `watch` 查看方法入参和返回值
   - `profiler` 生成火焰图分析热点

2. **性能优化关键点**：
   - 数据库查询优化
   - 合理使用缓存
   - 避免重复计算
   - 优化算法和数据结构

3. **最佳实践**：
   - 在开发阶段就进行性能测试
   - 提前考虑大数据量下的表现
   - 设计合理的缓存策略
   - 监控关键业务指标

希望这个案例对你学习Arthas有所帮助！Arthas非常强大，通过这类实战案例可以更好地掌握如何在生产环境中排查各种Java应用问题。