## 问题9：如何解读GC日志？常见GC日志参数有哪些？

GC（垃圾收集）日志记录了 JVM 在各个垃圾回收阶段的内存使用、停顿时间、频率等关键数据。掌握日志格式和常见参数，能快速定位 GC 性能瓶颈。

### 1. 开启GC日志的常用参数

- JDK8 及以前  
  - `-XX:+PrintGCDetails`：输出 GC 详细信息（各代空间使用情况）。  
  - `-XX:+PrintGCDateStamps`：每条日志加时间戳。  
  - `-XX:+PrintGCTimeStamps`：每条日志加相对启动的时间（秒）。  
  - `-Xloggc:/path/to/gc.log`：将 GC 日志写入文件。  
  - `-XX:+UseGCLogFileRotation` 与相关参数：按文件大小或个数轮转日志。

- JDK9 及以后（统一日志系统）  
  - `-Xlog:gc*:file=gc.log:tags,uptime,time,level`  
  - 可细化分类：`-Xlog:gc+heap+detail` 记录堆变化，`gc+age` 记录对象年龄分布。

### 2. 日志格式解析（以 G1GC 为例）

示例：  
```
2025-06-01T12:00:00.123+0000: 15.456: [GC pause (G1 Evacuation Pause) (young) 
   1024M->128M(2048M), 0.0456789 secs]
```

- 时间戳与 uptime  
  - `2025-06-01T12:00:00.123+0000`：真实时间  
  - `15.456`：JVM 启动 15.456 秒后的事件  

- GC 类型  
  - `(G1 Evacuation Pause) (young)`：G1 的一次 Young GC（Eden+Survivor 回收）  

- 空间变化与堆大小  
  - `1024M->128M(2048M)`：回收前堆使用 1024 MB，回收后 128 MB，最大堆 2048 MB  

- 停顿时长  
  - `0.0456789 secs`：此次 GC 停顿 45.7 ms  

### 3. 常见GC算法输出差异

- Parallel GC（吞吐优先）  
  - 日志中标签 `(DefNew)`、`(Tenured)` 分别表示新生代、老年代回收。  

- CMS GC（低停顿）  
  - 标记：`(CMS-initial-mark)`、`(CMS-concurrent-mark)`、`(CMS-concurrent-preclean)`、`(CMS-concurrent-abortable-preclean)`、`(CMS-remark)`、`(CMS-concurrent-sweep)`  
  - 分析标记/清理阶段耗时，定位并发与 STW 时间。  

- G1 GC（分区回收）  
  - 常见标签：`[GC pause (G1 Evacuation Pause) (young)]`、`[GC pause (G1 Evacuation Pause) (mixed)]`  
  - 关注 Mixed GC 中 Old 区回收比例与停顿目标（MaxGCPauseMillis）。  

### 4. 关键指标与调优思路

- Minor GC 频率与停顿  
  - 频繁 Minor GC 说明 Eden 小或对象创建过快，可增大新生代（`-Xmn` 或 `-XX:G1NewSizePercent`）。  

- Full/Mixed GC 停顿  
  - 停顿过长需调整老年代大小、切换低停顿 GC（G1、ZGC、Shenandoah）。  
  - G1 可通过 `-XX:MaxGCPauseMillis`、`-XX:InitiatingHeapOccupancyPercent` 调节触发时机与停顿目标。  

- GC 吞吐比（Throughput）  
  - 吞吐比 = 1 - (GC总耗时 / 总运行时间)。低吞吐可增大堆、并行线程数（`-XX:ParallelGCThreads`、`-XX:ConcGCThreads`）。  

- 对象年龄分布  
  - `-XX:+PrintTenuringDistribution` 输出对象在各年龄段存活状况，调整 `-XX:MaxTenuringThreshold`。  

### 5. 常见面试追问

- GC 日志中各阶段（标记、清理、压缩/复制）的含义？  
- 如何根据日志调整 G1 各项参数？  
- JDK8 CMS 与 G1 区别与选型依据？  
- ZGC/Shenandoah 有哪些日志参数？  

### 小结/提示词

- **开启日志**：`-XX:+PrintGCDetails`／`-Xlog:gc*`  
- **解析要点**：时间戳、GC类型、空间变化、停顿时长  
- **算法差异**：Parallel、CMS、G1 日志标签与阶段  
- **调优指标**：频率、停顿、吞吐、年龄分布  
- 复习口诀：  
  “看日志先识类型 → 空间前后对比 → 停顿时长 → 调参（代大小+算法+线程）”  