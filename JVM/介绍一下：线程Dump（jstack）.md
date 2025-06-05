线程Dump（Thread Dump）是一种用于分析Java应用程序在某一时刻所有线程运行状态的工具。它可以帮助开发人员和运维人员诊断诸如死锁、线程阻塞、CPU飙升等并发相关问题。

## 1. 什么是线程Dump？

线程Dump是指将Java进程中的所有线程的当前状态、调用栈（Stack Trace）和锁的信息导出为文本。通过分析这些信息，可以了解每个线程在做什么、在哪些地方等待、是否发生了死锁等。

## 2. jstack是什么？

jstack 是JDK自带的命令行工具，用于生成Java进程的线程Dump信息。它可以在应用运行时无侵入地获取所有线程的详细堆栈信息。

## 3. jstack的常用用法

假设Java进程的PID为12345，可以用如下命令导出线程Dump：

```bash
jstack 12345 > threaddump.txt
```

常用参数说明：

- `jstack -l <pid>`：输出线程堆栈及锁的详细信息（推荐用于分析死锁）。
- `jstack -F <pid>`：强制输出线程堆栈信息（用于进程无响应时）。
- `jstack -m <pid>`：输出本地方法栈信息（C/C++等Native代码）。

## 4. 线程Dump（jstack）输出内容

jstack输出内容主要包括以下部分：

- 每个线程的名称、优先级、线程ID、状态（如RUNNABLE、WAITING、BLOCKED等）
- 线程当前持有的锁和正在等待的锁
- 线程的Java调用栈
- 如果有死锁，jstack会自动检测并标记出来

**示例片段：**
```
"main" #1 prio=5 os_prio=0 tid=0x00007fbdc8009000 nid=0x1b03 waiting on condition [0x00007fbdcc7f7000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        at com.example.Demo.main(Demo.java:10)
```

## 5. 典型应用场景

- 分析线程死锁（Deadlock）
- 诊断线程阻塞、卡死、活锁等问题
- 调查CPU占用高的线程
- 性能瓶颈定位

## 6. 注意事项

- 生成线程Dump时，建议多次采样（间隔几秒），方便对比线程状态变化。
- 在线上环境使用jstack一般不会影响服务，但极端情况下（进程严重卡死）可能需要用 `-F` 强制模式。
- 线程Dump文件可用 [Thread Dump 分析工具](https://fastthread.io/) 或IDE进行可视化分析。

---

**总结**：  
线程Dump（jstack）是Java并发问题排查的重要工具，可以帮助快速定位线程相关的各种异常，是Java开发和运维必备技能之一。