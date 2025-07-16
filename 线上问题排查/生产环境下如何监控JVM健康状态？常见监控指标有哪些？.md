## 问题6：生产环境下如何监控JVM健康状态？常见监控指标有哪些？

在生产环境中持续、准确地监控 JVM 的关键指标，能帮助我们及时发现性能瓶颈、内存泄漏、GC 瓶颈等问题，并快速定位和响应。

#### 1. 主要监控指标

1. 堆内存使用  
   - Eden、Survivor、Old（Tenured）三代空间的已用和剩余容量  
   - 堆使用率（Used／Max）  
2. 垃圾回收（GC）  
   - Minor GC 与 Full/Mixed GC 的次数和耗时  
   - GC 暂停（STW）总时长与单次最大停顿  
   - GC 吞吐比（GC 时间 vs 应用运行时间）  
3. 元空间（Metaspace）  
   - 已用 vs 最大 Metaspace 大小  
   - 类加载和卸载次数  
4. 线程状态  
   - 活跃线程总数／峰值  
   - 各状态线程数（RUNNABLE、BLOCKED、WAITING、TIMED_WAITING）  
   - 线程池（Executor）使用率、队列长度、拒绝次数  
5. 类加载  
   - 已加载类总数  
   - 类卸载次数  
6. 本地/直接内存  
   - NIO DirectBuffer 内存使用  
   - 本地线程栈占用  
7. CPU 和系统资源  
   - JVM 进程 CPU 利用率  
   - 系统负载、磁盘 I/O、网络 I/O  
8. 应用级指标（业务埋点）  
   - 响应时延（P50/P95/P99）  
   - 吞吐量（TPS/QPS）  
   - 错误率  

#### 2. 常用监控工具和方案

1. JMX + 可视化  
   - JConsole、VisualVM、Java Mission Control（JMC）  
2. APM / Metrics 系统  
   - Prometheus + Micrometer + Grafana  
   - Elastic APM、SkyWalking、Pinpoint  
3. 命令行诊断  
   - jstat (内存／GC 统计)  
   - jcmd、jmap、jstack（快照采集）  
4. 日志采集与聚合  
   - GC 日志（`-Xlog:gc*,gc+heap+stats:file=gc.log`）  
   - 结合 ELK/EFK 或 Splunk 分析  

#### 3. 监控部署与告警

- **指标采集**：在启动参数中开启 JMX 或 Metrics Reporter  
- **可视化大盘**：搭建 Grafana 面板，展示堆内存、GC 停顿、线程数等趋势图  
- **告警策略**：  
  - 堆使用率 > 80% 持续 N 分钟  
  - 单次 GC 暂停 > 200 ms  
  - 活跃线程数接近上限  
  - 错误率或响应时延异常上升  
- **故障演练**：定期模拟 OOM、GC 突增等场景，验证告警触发和响应流程  

#### 4. 实践建议

- 在应用启动时，就把各项监控参数和日志路径配置好  
- 定期回顾监控大盘，结合历史数据发现趋势和异常  
- 将业务指标与 JVM 指标关联，定位性能问题时更有上下文  

### 小结/提示词

- 关键监控：堆内存、GC（次数/耗时/停顿）、线程、类加载、Metaspace、CPU  
- 工具链：JMX（JConsole/JMC）、Prometheus+Grafana、APM、ELK  
- 告警：堆使用率＞80%、GC 停顿过长、线程饱和、业务指标异常  
- 复习口诀：**“堆／GC／线程／Metaspace／CPU＋业务埋点，视图+告警+演练”**  