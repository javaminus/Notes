# 线程Dump（jstack）详解

- **线程dump**是一个现象（输出内容），**jstack**是获取线程dump的常用工具之一。
- 除了jstack外，还有其它方法（如kill信号、JVisualVM等）也可获取线程dump。
- 通常建议优先用jstack，功能更丰富；而在生产环境没法用jstack时，用kill -3信号也能得到线程dump。

---

## 1. 什么是线程Dump（Thread Dump）？

线程Dump，又叫**线程快照**，是指将JVM进程中所有线程的当前运行状态、调用栈信息一次性导出。通过分析线程Dump，可以排查死锁、线程阻塞、线程数暴涨、CPU占用高等问题，是定位Java线上问题的利器。

---

## 2. jstack的使用方法

### 基本用法

```bash
jstack <pid>
```
- `<pid>` 是Java进程ID。可以用 `jps` 或 `ps -ef | grep java` 查找。

### 常用参数

- `jstack -l <pid>`  
  输出更详细信息（包括锁的拥有者、等待者等）。
- `jstack -F <pid>`  
  强制输出线程信息（当普通模式失败时使用）。
- `jstack -m <pid>`  
  同时显示Java和本地（C/C++）栈信息。

### 导出到文件（推荐）

```bash
假设 Java 进程 PID 为 12345，可以使用如下命令导出 dump 日志：

jstack -l 12345 > jstack_dump.log
```

### 线上诊断建议

- 连续多次（如间隔10秒）采集3~5次Dump，有助于动态分析线程状态。
- 采集前尽量避免重启JVM，否则问题状态丢失。

---

## 3. 线程Dump中主要内容解析

- **每个线程的名字、ID、优先级、状态（如RUNNABLE、WAITING、BLOCKED、TIMED_WAITING等）**
- **线程调用栈**：显示当前线程正在执行的方法调用链
- **锁信息**：
  - 哪些线程持有锁（Owns lock）
  - 哪些线程在等待锁（Waiting to lock）
  - 死锁（Found one Java-level deadlock）

---

## 4. 常见问题场景举例

- **死锁排查**：Dump中会标记出死锁线程及死锁资源
- **线程阻塞/等待**：可看到线程因锁竞争或资源等待而停留在synchronized、wait、IO等方法
- **CPU飙高**：查找RUNNABLE且堆栈反复出现在同一方法的线程
- **线程数量异常**：Dump中线程数过多，排查是否有线程泄漏

------

**假设 jstack dump 片段如下：**

Code

```
"Thread-1" #12 prio=5 os_prio=31 tid=0x00007f9e1000b800 nid=0x4e07 waiting for monitor entry [0x0000700000a8d000]
   java.lang.Thread.State: BLOCKED (on object monitor)
        at com.example.DemoClass.method(DemoClass.java:10)
        - waiting to lock <0x00000000cafe1234> (a java.lang.Object)
        - locked <0x00000000cafe5678> (a java.lang.Object)
```

分析：

- Thread-1 处于 BLOCKED 状态，正在等待获取 `0x00000000cafe1234` 的锁。
- 查看哪个线程持有该锁，找到阻塞源头。

---

## 5. 面试官常见追问与参考答案

### Q1: jstack得到的线程状态有哪些？含义是什么？
**答**：主要有：
- `RUNNABLE`：正在运行或准备运行

- `WAITING`/`TIMED_WAITING`：在等待某条件或超时等待

  > **WAITING**和**TIMED_WAITING**都是Java线程的阻塞状态，区别在于是否有时间限制：
  >
  > - **WAITING**：线程无限期等待被唤醒，比如`Object.wait()`、`Thread.join()`没有超时参数。线程只能靠其他线程唤醒。
  > - **TIMED_WAITING**：线程等待有时间限制，比如`Thread.sleep(ms)`、`Object.wait(ms)`、`Thread.join(ms)`。超时后自动唤醒，也可以被其他线程提前唤醒。
  >
  > 一句话记忆：  
  > > WAITING是无限等待，TIMED_WAITING是带时间的等待。

- `BLOCKED`：等待获取锁

- `NEW`：新建未启动

- `TERMINATED`：已结束

---

### Q2: 如何用jstack排查死锁？
**答**：jstack Dump文件会标明`Found one Java-level deadlock`，并详细列出涉及死锁的线程及锁资源。可通过分析堆栈和锁持有/等待关系定位死锁源头。

---

### Q3: jstack采集线程Dump时会影响线上应用吗？
**答**：jstack会短暂暂停JVM所有线程（Safepoint），但一般影响极小（毫秒级），可安全在线上使用。但极端高并发或卡顿时，采集可能稍慢。

---

### Q4: 线程Dump中如何定位CPU占用高的线程？
**答**：先用top、ps等工具找到CPU占用高的线程（tid），转换为16进制（如Linux下 `printf "%x\n" <tid>`），再在jstack输出中查找对应线程ID，分析该线程的调用栈。

---

### Q5: 为什么建议多次采集线程Dump？
**答**：多次采集可以观察线程状态是否持续不变（如死锁/阻塞），便于确认问题性质和趋势。

---

### Q6: jstack和jmap、jcmd的区别？
**答**：
- jstack用于获取线程信息和栈快照；
- jmap主要用于堆内存、对象分布分析；
- jcmd功能综合，可执行jstack、jmap、诊断命令等。

---

## 6. 总结复习提示

- 线程Dump：定位死锁、阻塞、线程泄漏、CPU高等问题的利器
- jstack用法：`jstack -l <pid>`
- 重点关注：线程状态、调用堆栈、锁关系、死锁提示
- 口诀：“多次采集比对、堆栈锁全关注，死锁阻塞一清楚”