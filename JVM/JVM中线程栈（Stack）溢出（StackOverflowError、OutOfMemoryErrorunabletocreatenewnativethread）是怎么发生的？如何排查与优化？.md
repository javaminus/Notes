## 问题：JVM 中线程栈（Stack）溢出（StackOverflowError、OutOfMemoryError: unable to create new native thread）是怎么发生的？如何排查与优化？

---

### 详细解释（结合场景 & 通俗例子）

#### 1. 线程栈的定义与作用
- **线程栈**（Java 虚拟机栈 / 本地方法栈）为每个线程分配，存储方法调用信息、局部变量、操作数栈等。
- 每个线程启动时，JVM 会为其分配一块独立的栈空间。

#### 2. 两种典型的线程栈溢出错误
- **StackOverflowError**
  - 单个线程栈空间用尽（如无限递归/深度递归），抛出 `java.lang.StackOverflowError`。
  - 例子：
    ```java
    public void endless() { endless(); }
    endless(); // 很快报 StackOverflowError
    ```
- **OutOfMemoryError: unable to create new native thread**
  - 系统总线程数达到上限（OS 或 JVM 限制），再创建新线程时抛出。
  - 常见于线程池配置过大、应用频繁创建新线程，或服务器资源不足。
  - 例子：误把线程池 coreSize/maxSize 设置得极大时，持续请求下会耗尽资源。

#### 3. 排查与优化思路
- **StackOverflowError**
  - 检查递归调用逻辑，避免无限递归或过深递归。
  - 优化算法，改递归为迭代。
  - 可通过 `-Xss` 参数调整单线程栈大小（如 `-Xss256k`），但不建议随意调大。
- **unable to create new native thread**
  - 通过 `jstack`、`ps`、`top` 等工具查看线程数量和状态。
  - 检查线程池配置，合理设置 corePoolSize、maxPoolSize。
  - 评估 OS 级线程数限制（如 Linux 的 `ulimit -u`）。
  - 检查是否有线程泄漏（线程未关闭或异常退出未回收）。

#### 4. 实际生产场景举例
- **无限递归**：递归算法未设置出口，或出口条件判断错误导致爆栈。
- **线程泄漏**：Web 容器或微服务中，业务代码反复 new Thread 执行任务，未用线程池，系统很快无法分配新线程。

---

### 总结性的回答（复习提示词）

- **线程栈溢出**：单线程栈满 = StackOverflowError；系统线程数满 = unable to create new native thread
- **排查方法**：递归/线程池/线程数量
- **优化手段**：递归转迭代、合理分配线程池、控制线程数
- **口诀**：`“单栈爆栈是递归，线程数爆是池管，jstack定位，参数调优”`

## 5. 面试官可能追问 & 答案

### Q1：`StackOverflowError` 和 `OutOfMemoryError: unable to create new native thread` 有什么本质区别？
- **答**：前者是“单个线程的栈空间耗尽”，后者是“系统无法再为新线程分配栈空间”。前者通常由递归/深度调用导致，后者是线程数太多超过操作系统/虚拟机限制。

### Q2：如何定位是线程泄漏？
- **答**：用 `jstack` 查看线程Dump，关注没有终止的业务线程（状态为RUNNABLE、BLOCKED、WAITING），配合 `ps -eLf | grep java | wc -l` 统计线程总数。若线程数持续增长且未回收，基本可判断为线程泄漏。

### Q3：`-Xss` 和线程数之间的关系？
- **答**：`-Xss` 指定每个线程的栈内存大小。单线程栈大，则同等物理内存下，能创建的线程数就少。调整时需权衡单线程深度和系统总线程数。

### Q4：如何避免线程池爆炸导致 OOM？
- **答**：合理设置线程池的最大线程数和队列长度，避免使用无界队列或过大 maxPoolSize。优先用 `ThreadPoolExecutor` 明确参数，不建议用默认的 `Executors.newFixedThreadPool` 等快捷工厂方法。

---