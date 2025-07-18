排查 Java 应用中**CPU**或**内存占用率过高**的问题，通常分为两个阶段：  

1. 先用操作系统命令定位是哪个进程/线程占用资源高；  
2. 再结合 Java 工具具体定位到是**哪段代码**导致的高消耗。

---

## 一、定位高 CPU/内存进程

### 1. 查看高 CPU/内存进程
```bash
top
# 或
htop
```
- 找到 CPU 或内存占用高的 Java 进程，记下 PID。

### 2. 查看进程内线程的 CPU 消耗（如上一步已得 PID=12345）
```bash
top -H -p 12345
```
- 可看到每个线程（LWP/TID）的 CPU 占用率。

### 3. 将线程ID转换为十六进制（便于与 Java 堆栈匹配）
```bash
printf "%x\n" 线程ID
# 例如线程ID为 12367，输出为 305f
```

---

## 二、Java 层面定位具体代码

### 1. 用 jstack 打印线程堆栈
```bash
jstack 12345 > thread.txt
```
- 12345 为 Java 进程 PID。

### 2. 在 thread.txt 中搜索高 CPU/内存线程的十六进制 TID
- 搜索 `nid=0x305f`（0x305f 为上一步得到的十六进制线程ID）
- 找到该线程的堆栈，即可看到**具体是哪个代码、方法、类**导致 CPU 高。

---

## 三、排查思路流程总结

1. **top/htop** 查看高占用进程（记下 Java 进程 PID）。
2. **top -H -p PID** 定位高占用线程ID。
3. **printf "%x\n" 线程ID** 转为16进制。
4. **jstack PID** 导出线程堆栈。
5. **搜索 nid=0x线程ID**，分析代码。
6. 排查是否死循环、热点代码、锁竞争等。

---

## 四、内存占用高的排查方法

1. **jmap -heap PID** 查看堆配置与使用。
2. **jmap -histo:live PID** 查看对象分布。
3. **jhat**、**MAT**、**VisualVM** 分析 heap dump 文件：
   ```bash
   jmap -dump:live,format=b,file=heap.bin PID
   ```
   用可视化工具打开分析，找大对象或内存泄漏。

---

## 五、常见 Java 排查工具

- `jstack` —— 线程堆栈
- `jmap` —— 堆快照、内存对象分布
- `jstat` —— GC、内存统计
- `jconsole`/`visualvm` —— 图形化监控
- `arthas` —— 一站式诊断神器（推荐！）

---

## 六、Arthas 快速定位
Arthas 是阿里开源的 Java 诊断工具，几乎是线上排查必备：
```bash
# 下载并启动
java -jar arthas-boot.jar

# 连接目标 Java 进程后，查看线程
thread -n 5  # 显示CPU占用最高的5个线程
# 查看具体线程堆栈
thread 线程ID
```
可以直接定位到高 CPU 线程的代码。

---

### **总结**

- Linux 层定位进程和线程，转换线程ID为16进制。
- 用 jstack、arthas 等工具定位具体代码位置。
- 内存问题用 jmap、MAT、VisualVM。
- 线上强烈推荐使用 Arthas，简单高效。

如需具体工具命令或分析某段堆栈，可进一步提问！