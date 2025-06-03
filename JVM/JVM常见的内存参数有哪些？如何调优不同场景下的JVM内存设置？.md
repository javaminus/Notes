## 问题：JVM常见的内存参数有哪些？如何调优不同场景下的JVM内存设置？

### 详细解释

JVM的内存参数直接影响Java应用的性能、稳定性和可扩展性。合理配置和调优这些参数，是生产环境中Java性能优化的关键步骤。

#### 1. 常用内存参数

- **堆内存相关**
  - `-Xms<size>`：堆内存初始值（如`-Xms2g`），建议与最大堆一致，减少动态扩容带来的GC压力。
  - `-Xmx<size>`：堆内存最大值，防止应用耗尽物理内存。
  - `-Xmn<size>`：新生代大小（JDK8及前），优化Minor GC频率。
- **元空间/方法区**
  - `-XX:MetaspaceSize=<size>`：元空间初始大小（JDK8+）。
  - `-XX:MaxMetaspaceSize=<size>`：元空间最大大小。
- **栈空间**
  - `-Xss<size>`：每个线程的栈大小，适当调整可支持更多线程或更深递归。
- **直接内存**
  - `-XX:MaxDirectMemorySize=<size>`：NIO等直接内存分配上限。

#### 2. GC相关参数

- `-XX:+UseG1GC`、`-XX:+UseConcMarkSweepGC`、`-XX:+UseParallelGC`：选择不同的垃圾回收器。
- `-XX:SurvivorRatio=<n>`：Eden与Survivor区比例。
- `-XX:NewRatio=<n>`：新生代与老年代比例。
- `-XX:MaxTenuringThreshold=<n>`：对象晋升老年代前在新生代存活的GC次数。

#### 3. 典型场景的调优建议

- **高并发Web服务**
  - 堆内存设置足够大以容纳并发请求，但不超过物理内存总量的70~80%。
  - `-Xms`与`-Xmx`设为相同，减少堆扩缩容。
  - 选择G1 GC或ZGC，减少STW时间。
- **大数据/批处理应用**
  - 增大堆内存与新生代，减少GC频率。
  - 可以用Parallel GC，追求高吞吐量。
- **内存敏感型应用**
  - 精准设置各代区大小，避免频繁Full GC或内存溢出。
- **线程数较多的应用**
  - 减小`-Xss`线程栈大小，防止线程数受限于栈空间。

#### 4. 参数调优排查步骤

1. 通过监控工具（如VisualVM、JMC、Prometheus）观察内存、GC指标。
2. 检查GC日志，分析GC频率与停顿时间。
3. 结合业务流量，逐步调整内存参数，关注堆外内存与线程栈空间。
4. 留意OOM、频繁Full GC等异常，及时调整相关参数。

#### 5. 常用参数示例

```shell
# 适合高并发Web应用的JVM参数
java -Xms4g -Xmx4g -Xss512k -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m \
     -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 \
     -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError
```

### 总结性回答/提示词

- JVM常用参数：-Xms、-Xmx、-Xss、-XX:MetaspaceSize
- 调优思路：结合业务类型、内存监控、GC日志，合理设置堆/栈/元空间
- 典型组合：高并发服务=大堆+G1，高吞吐=Parallel GC，线程多=小-Xss
- 复习提示：**“根据应用特性设内存参数，堆/栈/元空间分清楚，监控+调优”**