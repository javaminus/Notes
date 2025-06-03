## 问题5：JVM中常见的OOM错误类型有哪些？如何定位和解决？

Java 应用运行过程中，如果某个内存区域无法再分配足够空间，就会抛出不同类型的 `OutOfMemoryError`（OOM）。下面列举高频面试会考的几种 OOM，以及定位与解决思路。

### 1. Java Heap Space  
- **表现**：`java.lang.OutOfMemoryError: Java heap space`  
- **原因**：堆中对象过多、垃圾未及时回收或堆设置过小。  
- **定位**：  
  1. 打开 GC 日志（`-XX:+PrintGCDetails -Xloggc:gc.log`），查看堆使用趋势。  
  2. 导出 Heap Dump：`jmap -dump:live,format=b,file=heap.hprof <pid>`。  
  3. 用 MAT、VisualVM 等工具分析：  
     - 查找最大对象（Dominators）。  
     - 分析对象保持链，定位内存泄漏（如静态集合、ThreadLocal、缓存）。  
- **解决**：  
  - 清理无用缓存、弱引用或使用 LRU、Guava Cache 限流。  
  - 调整堆大小：`-Xms… -Xmx…`。  
  - 优化业务代码，减少临时大对象或批量分配。

### 2. PermGen / Metaspace  
- **表现**：  
  - JDK7 及以前：`java.lang.OutOfMemoryError: PermGen space`  
  - JDK8+：`java.lang.OutOfMemoryError: Metaspace`  
- **原因**：  
  - 热部署／反复加载类未卸载导致元区泄漏。  
  - 静态类或大量动态生成的代理类、cglib、JSP 编译等。  
- **定位**：  
  1. 打开 Metaspace 日志：`-XX:+PrintClassHistogram -XX:+PrintTenuringDistribution`。  
  2. 使用 `jcmd <pid> GC.class_histogram` 查看类实例及大小。  
- **解决**：  
  - 限制 Metaspace：`-XX:MaxMetaspaceSize=`。  
  - 检查自定义类加载器、动态代理，确保类可卸载。  
  - 对于应用服务器，做好热部署隔离或重启。

### 3. GC Overhead Limit Exceeded  
- **表现**：`java.lang.OutOfMemoryError: GC overhead limit exceeded`  
- **原因**：JVM 在回收后只回收了很少内存，但 GC 却花费了大量时间（默认 98% 时间做 GC，回收不到 2% 堆），导致呆循环。  
- **定位 & 解决**：  
  - 分析 GC 日志，确认频繁 Full GC。  
  - 增大堆内存，或优化对象生命周期，减少回收压力。  
  - 如果业务允许，可关闭此检查：`-XX:-UseGCOverheadLimit`（不推荐）。

### 4. Direct Buffer Memory  
- **表现**：`java.lang.OutOfMemoryError: Direct buffer memory`  
- **原因**：NIO、Netty 或其他本地库申请了过多直接内存，超出默认 `-XX:MaxDirectMemorySize`。  
- **定位**：  
  - 观察 native 内存占用（`pmap`、`top`）。  
  - 用 `NIO` 工具或 jcmd 查看 Buffer 分配统计：`jcmd <pid> GC.class_stats`（部分实现）。  
- **解决**：  
  - 设置或增大 `-XX:MaxDirectMemorySize=`。  
  - 优化 Buffer 使用、及时调用 `ByteBuffer.clear()`/`recycle`。

### 5. Unable to Create New Native Thread  
- **表现**：`java.lang.OutOfMemoryError: unable to create new native thread`  
- **原因**：进程尝试创建第 N+1 个线程时超出系统或 JVM 限制（如 ulimit、虚拟内存、栈大小）。  
- **定位**：  
  - 检查系统线程数限制：`ulimit -u`。  
  - `jstack <pid>` 查看线程数量。  
- **解决**：  
  - 降低 `-Xss`（线程栈），或减少最大线程数。  
  - 优化线程池配置，避免无限制新建。

---

### 小结/面试提示词  
- 常见 OOM：Heap Space、PermGen/Metaspace、GC Overhead、Direct Buffer、Native Thread  
- 定位思路：GC 日志 → Heap Dump/Class Histogram → 分析工具（MAT/VisualVM）  
- 解决策略：调参（堆、元空间、Direct Memory、线程栈）、代码优化（缓存、代理、线程池）  
- 复习口诀：  
  “看日志、导 dump、用 MAT，定位泄漏／大对象；参数扩／收；优化代码防 OOM”  