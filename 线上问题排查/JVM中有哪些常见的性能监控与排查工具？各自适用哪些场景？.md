# JVM 常见性能监控与排查工具

JVM 提供了丰富的性能监控与排查工具，下面介绍几个常用的工具、适用场景及案例分析。

## 1. jps (Java Process Status)

**适用场景**：查看系统中所有 Java 进程的 PID。

**案例**：
```bash
$ jps -lvm
23456 com.example.Application -Xms512m -Xmx1024m
23457 sun.tools.jps.Jps -lvm -Dapplication.home=/usr/lib/jvm/java-8-openjdk
```

**分析**：
- PID 23456 是一个正在运行的应用，启动参数设置了最小堆 512MB，最大堆 1GB
- PID 23457 是 jps 命令本身的进程

**jps**
Java Virtual Machine Process Status Tool，显示本地（或远程）JVM 进程信息。

- **-l**
  输出主类（Main Class）或 jar 文件的完整包名（全限定名），比默认的短类名更详细。
- **-v**
  显示传递给 JVM 的参数（如 -Xms、-Xmx 等 JVM 启动参数）。
- **-m**
  显示传递给 main() 方法的参数。

## 2. jstat (JVM Statistics Monitoring Tool)

**适用场景**：实时监控 JVM 的内存、GC 等数据。

**案例**：

> `jstat -gcutil 23456 1000 5` 的意思是：每隔1000毫秒（1秒）采集一次进程号为23456的Java进程的垃圾回收相关统计信息（包括各内存区使用率和GC次数），共采集5次，并以表格形式输出结果。这样可以用来实时观察JVM内存和GC的变化情况。 

```bash
$ jstat -gcutil 23456 1000 5
  S0     S1     E      O      M     CCS    YGC     YGCT    FGC    FGCT     GCT   
  0.00  75.29  67.63  45.33  94.73  89.92     36    0.546     2    0.144    0.690
  0.00  75.29  77.34  45.33  94.73  89.92     36    0.546     2    0.144    0.690
  0.00  75.29  87.21  45.33  94.73  89.92     36    0.546     2    0.144    0.690
  52.95  0.00   8.26  47.11  94.73  89.92     37    0.565     2    0.144    0.709
  52.95  0.00  17.73  47.11  94.73  89.92     37    0.565     2    0.144    0.709
```

**分析**：
- 输出显示 5 个时间点的 GC 信息，间隔 1000 毫秒

- S0/S1: Survivor 区使用率，在第 4 行发生了 Young GC，S0 和 S1 区对象交换

- E: Eden 区使用率，从 67.63% 增长到 87.21% 后触发 GC

- O: Old 区使用率，Young GC 后从 45.33% 增加到 47.11%

- M：Metaspace 元空间的已使用百分比（0-100%，JDK8及以后替代Perm区） 

- CCS：Compressed Class Space 的已使用百分比（类元数据的压缩空间，0-100%） 

- YGC/YGCT: Young GC 次数和耗时，发生了 37 次 Young GC，总耗时 0.565 秒

- FGC/FGCT: Full GC 次数和耗时，发生了 2 次 Full GC，总耗时 0.144 秒

- GCT：所有GC（YGC+FGC）的累计总耗时（秒）

## 3. jstack (Java Stack Trace)

**适用场景**：分析线程死锁、阻塞、CPU 使用率高等问题。

**案例**：
```bash
$ jstack -l 23456
2025-07-16 02:20:46
Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.202-b08 mixed mode):

"http-nio-8080-exec-10" #43 daemon prio=5 tid=0x00007f8cbc173000 nid=0x5f02 waiting on condition [0x00007f8cb8fd6000]
   java.lang.Thread.State: TIMED_WAITING (parking)
        at sun.misc.Unsafe.park(Native Method)
        - parking to wait for  <0x00000000f1772e10> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
        at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)
        at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.awaitNanos(AbstractQueuedSynchronizer.java:2078)
        at java.util.concurrent.ThreadPoolExecutor.awaitTermination(ThreadPoolExecutor.java:1465)
        at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$SharedConnection.resetConnection(LettuceConnectionFactory.java:825)
        at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$SharedConnection.getConnection(LettuceConnectionFactory.java:798)
        at com.example.service.CacheService.getData(CacheService.java:52)
        at com.example.controller.ApiController.handleRequest(ApiController.java:37)
        
"GC task thread#0 (ParallelGC)" os_prio=0 tid=0x00007f8d0006f000 nid=0x5e0b runnable

"VM Periodic Task Thread" os_prio=0 tid=0x00007f8d00094800 nid=0x5e25 waiting on condition

JNI global references: 1020

Found one Java-level deadlock:
=============================
"Thread-1":
  waiting to lock monitor 0x00007f8d00071ef8 (object 0x00000000f1a3db68, a java.lang.Object),
  which is held by "Thread-2"
"Thread-2":
  waiting to lock monitor 0x00007f8d00072aa8 (object 0x00000000f1a3db78, a java.lang.Object),
  which is held by "Thread-1"

Java stack information for the threads listed above:
===================================================
"Thread-1":
        at com.example.DeadlockExample.method1(DeadlockExample.java:15)
        - waiting to lock <0x00000000f1a3db68> (a java.lang.Object)
        - locked <0x00000000f1a3db78> (a java.lang.Object)
        at com.example.DeadlockExample$1.run(DeadlockExample.java:28)
"Thread-2":
        at com.example.DeadlockExample.method2(DeadlockExample.java:21)
        - waiting to lock <0x00000000f1a3db78> (a java.lang.Object)
        - locked <0x00000000f1a3db68> (a java.lang.Object)
        at com.example.DeadlockExample$2.run(DeadlockExample.java:34)
```

**分析**：
1. **线程状态分析**：
   - `http-nio-8080-exec-10` 线程处于 `TIMED_WAITING` 状态，正在执行 Redis 缓存操作
   - GC 线程和周期性任务线程正常工作

2. **死锁分析**：
   - 发现一个典型的死锁：`Thread-1` 持有锁 `<0x00000000f1a3db78>` 等待锁 `<0x00000000f1a3db68>`
   - `Thread-2` 持有锁 `<0x00000000f1a3db68>` 等待锁 `<0x00000000f1a3db78>`
   - 死锁位置在 `DeadlockExample.java` 的第 15 行和第 21 行
   - 需要修改代码，确保获取锁的顺序一致

3. **问题修复建议**：
   - 修改 `DeadlockExample` 类，确保所有线程按相同顺序获取锁
   - 考虑使用 `java.util.concurrent` 包中的锁机制代替 synchronized

## 4. jmap (Java Memory Map)

**适用场景**：分析内存泄漏、内存溢出、对象占用等问题。

**案例**：
```bash
$ jmap -heap 23456
Attaching to process ID 23456, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.202-b08

using thread-local object allocation.
Parallel GC with 8 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 40
   MaxHeapFreeRatio         = 70
   MaxHeapSize              = 1073741824 (1024.0MB)
   NewSize                  = 22020096 (21.0MB)
   MaxNewSize               = 357564416 (341.0MB)
   OldSize                  = 44564480 (42.5MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 268435456 (256.0MB)
   used     = 202178456 (192.8135147094727MB)
   free     = 66257000 (63.18648529052734MB)
   75.31777918730965% used
From Space:
   capacity = 44564480 (42.5MB)
   used     = 0 (0.0MB)
   free     = 44564480 (42.5MB)
   0.0% used
To Space:
   capacity = 44564480 (42.5MB)
   used     = 0 (0.0MB)
   free     = 44564480 (42.5MB)
   0.0% used
PS Old Generation
   capacity = 715849728 (682.6953125MB)
   used     = 326589416 (311.45679473876953MB)
   free     = 389260312 (371.23851776123047MB)
   45.622276764593204% used
```

**分析**：
- 应用配置的最大堆内存为 1GB
- Eden 区已使用 75.3%，达到较高水平，可能很快触发 Young GC
- Old 区使用了 45.6%，尚有较多空间
- 整体内存使用正常，但 Eden 区的快速填充可能导致频繁 Young GC

## 5. JVisualVM

**适用场景**：实时监控内存、CPU、线程，分析内存泄漏、性能瓶颈。

**案例**：使用 JVisualVM 分析内存泄漏

![JVisualVM 内存泄漏分析]
内存监控面板显示堆内存持续上涨且 GC 后不降低，通过堆转储找到 `HashMap` 持续增长，定位到 `CacheManager` 类没有设置缓存过期策略导致对象无法回收。

## 6. Arthas

**适用场景**：在线诊断生产问题，无需重启应用。

**案例**：
```bash
$ watch com.example.service.SlowService doProcess '{params, returnObj, throwExp, cost}' '#cost>200'
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 36 ms, listenerId: 1
method=com.example.service.SlowService.doProcess location=AtExit
ts=2025-07-16 02:22:33; [cost=328.11ms] result=@ArrayList[
    @Object[][
        @String[12345],
        @HashMap[isEmpty=false;size=2],
    ],
    @Integer[1],
    null,
    @Double[328.11],
]
```

**分析**：
- 监控 `SlowService.doProcess` 方法的执行情况，筛选耗时超过 200ms 的调用
- 输出显示一次调用耗时 328.11ms，输入参数包含订单号"12345"和一个包含2个元素的 HashMap
- 返回值为整数 1，没有异常抛出
- 通过进一步跟踪这个慢调用可以定位性能瓶颈

## 7. jinfo

**适用场景**：查看和动态修改 JVM 参数。

**案例**：
```bash
$ jinfo -flags 23456
Attaching to process ID 23456, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.202-b08
Non-default VM flags: -XX:CICompilerCount=4 -XX:InitialHeapSize=536870912 -XX:MaxHeapSize=1073741824 -XX:MaxNewSize=357564416 -XX:MinHeapDeltaBytes=524288 -XX:NewSize=178257920 -XX:OldSize=358612992 -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseFastUnorderedTimeStamps -XX:+UseParallelGC
Command line:  -Xms512m -Xmx1024m
```

**分析**：
- 应用运行时配置的初始堆大小为 512MB，最大堆为 1GB
- 使用并行垃圾收集器 `-XX:+UseParallelGC`
- 使用压缩对象指针和类指针，有利于节省内存
- 可以考虑根据服务器资源和应用特点调整参数以优化性能

## 总结

JVM 提供了丰富的性能监控与排查工具，组合使用这些工具可以全面分析 Java 应用的性能状况：
- **jps**: 获取 Java 进程信息
- **jstat**: 实时监控 GC 和内存状态
- **jstack**: 分析线程和死锁问题
- **jmap**: 分析内存使用和泄漏
- **JVisualVM/VisualVM**: 可视化性能监控
- **Arthas**: 在线诊断工具
- **jinfo**: 查看和修改 JVM 参数

选择合适的工具和分析方法，有助于快速定位和解决 JVM 相关的性能问题。