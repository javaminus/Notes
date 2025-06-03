## 问题：JVM中有哪些常见的性能监控与排查工具？各自适用哪些场景？

### 详细解释

在开发和运维Java应用时，合理利用JVM性能监控与排查工具，能够快速定位性能瓶颈、内存泄漏、线程死锁等问题。常见的工具既有JDK自带的，也有第三方可视化工具。

#### 1. JDK自带工具

- **jps**  
  类似于Unix的ps命令，查看当前所有Java进程ID（PID）  
  ```shell
  jps -l
  ```
- **jstack**  
  输出指定Java进程的线程堆栈信息，常用于排查死锁、线程阻塞等问题  
  ```shell
  jstack <pid>
  ```
- **jmap**  
  用于分析堆内存使用，生成heap dump文件，或查看对象统计信息  
  ```shell
  jmap -dump:live,format=b,file=heap.hprof <pid>
  ```
- **jhat**  
  分析heap dump文件的早期工具，适合小型heap dump分析（已逐步被更强大的工具替代）
- **jinfo**  
  查看和修改运行中JVM的参数配置信息  
  ```shell
  jinfo -flags <pid>
  ```
- **jstat**  
  实时监控JVM内存、GC等统计信息  
  ```shell
  jstat -gc <pid> 1000
  ```

#### 2. 可视化监控/分析工具

- **VisualVM**  
  JDK自带，图形化展示内存、CPU、线程、GC等，支持heap dump分析、内存泄漏检测插件等。适合开发、测试、简单生产排查。
- **JConsole**  
  JDK自带，监控内存、线程、MBean等，轻量级监控工具。
- **Java Mission Control (JMC)**  
  与Flight Recorder配合，低开销地采集生产环境性能数据，适合性能基线分析和线上排查。
- **MAT（Memory Analyzer Tool）**  
  Eclipse出品，专业分析heap dump，定位内存泄漏、查找大对象、引用链。

#### 3. 典型应用场景举例

- **定位线程死锁**  
  先用`jps`查PID，再用`jstack`查看线程堆栈，快速定位死锁线程。
- **分析内存泄漏**  
  用`jmap`导出heap dump，使用MAT/VisualVM分析占用内存最多的对象，查找GC Roots引用链。
- **GC性能监控**  
  用`jstat`/VisualVM实时监控GC情况，观察Full GC频率、内存回收效果。
- **线上性能采集**  
  用JMC/Flight Recorder在生产环境低开销采集全方位性能数据。

#### 4. 其他第三方工具

- **Arthas**：强大的在线诊断工具，支持方法追踪、实时监控、内存分析、热更新等，适合线上问题排查。
- **YourKit/ JProfiler**：商用性能分析工具，提供丰富的CPU、内存、线程分析功能，适合深度性能调优。

### 总结性回答/提示词

- JDK自带工具：jps、jstack、jmap、jstat、jinfo、VisualVM、JConsole
- 生产/复杂场景：JMC、MAT、Arthas、YourKit/JProfiler
- 典型用途：查线程死锁（jstack）、查内存泄漏（jmap+MAT）、实时GC监控（jstat/VisualVM）、线上低开销采集（JMC）
- 复习提示：**“jps找进程、jstack查线程、jmap导内存、VisualVM/JMC图形化分析”**