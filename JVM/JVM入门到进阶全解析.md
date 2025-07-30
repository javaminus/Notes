# JVM 入门到进阶全解析

本文将系统梳理 Java 虚拟机（JVM）的核心知识点，从基础原理到进阶实践，并结合常见面试题举例说明，帮助你从入门到进阶全面掌握 JVM。



> 方法区是一种规范，在jdk1.7和之前，永久代就是实现方法区的一种方式；
>
> jdk1.7之后，元空间是实现方法区的一种方式；  元空间不在堆上，永久代在堆上。

---

## 目录

1. [JVM 概述](#jvm-概述)
2. [JVM 体系结构](#jvm-体系结构)
3. [类加载机制](#类加载机制)
4. [运行时数据区](#运行时数据区)
5. [垃圾回收（GC）](#垃圾回收gc)
6. [JVM 性能调优](#jvm-性能调优)
7. [常见面试题与解析](#常见面试题与解析)
8. [实用命令与工具](#实用命令与工具)

---

## JVM 概述

**Java Virtual Machine**（Java虚拟机，简称JVM），是一种能够运行 Java 字节码的虚拟计算机。它屏蔽了底层操作系统的差异，使得 Java 能够“一次编写，处处运行”。

### JVM 的作用

- 加载类文件并执行
- 内存管理和垃圾回收
- 安全性和跨平台支持
- 运行时异常处理

**举例：**
> 你在 Windows 上写的 Java 程序，同样可以在 Mac、Linux 上运行，因为 JVM 负责与底层系统对接。

---

## JVM 体系结构

JVM 的主要组成部分有：

- **类加载器（Class Loader）**
- **运行时数据区（Runtime Data Area）**
- **执行引擎（Execution Engine）**
- **本地方法接口（Native Interface）**
- **垃圾回收器（Garbage Collector）**

![img](assets/u=2405320439,3649150098&fm=3074&app=3074&f=JPEG.jfif)  

---

## 类加载机制

### 类加载的过程

1. **加载（Loading）**  
   通过类的全限定名查找并加载类的二进制数据到内存。
2. **链接（Linking）**  
   - 验证（Verify）：校验字节码合法性
   - 准备（Prepare）：为静态变量分配内存并初始化默认值
   - 解析（Resolve）：将常量池中的符号引用替换为直接引用
3. **初始化（Initialization）**  
   执行类构造器 `<clinit>()` 方法，初始化静态变量

### 类加载器的层次结构

- **启动类加载器（Bootstrap ClassLoader）**：加载 JDK 核心类库（如rt.jar）
- **扩展类加载器（Extension ClassLoader）**：加载扩展目录（ext）下的类库
- **应用程序类加载器（App ClassLoader）**：加载应用classpath下的类

**举例：**
```java
Class<?> clazz = Class.forName("java.util.ArrayList");
System.out.println(clazz.getClassLoader()); // null，说明由Bootstrap加载
```

---

## 运行时数据区

JVM 在运行期间会把内存划分为几个区域：

### 1. 方法区（MetaSpace）

- 存储类的元信息、静态变量、常量池等
- JDK8之前叫永久代（PermGen），JDK8之后变为MetaSpace，存放在本地内存

### 2. 堆（Heap）

- 存储对象实例和数组
- 垃圾回收的主要区域

### 3. 虚拟机栈（Java Stack）

- 每个线程独立
- 存储方法调用过程中的局部变量、操作数栈、帧数据

### 4. 本地方法栈（Native Stack）

- 为 JVM 调用本地（C/C++）方法服务

### 5. 程序计数器（PC Register）

- 记录当前线程执行的字节码行号指示器

**举例：**

```java
public void foo() {
    int a = 1; // 存在栈
    String s = new String("hello"); // s 引用在栈，"hello" 对象在堆
}
```

---

## 垃圾回收（GC）

### 为什么需要垃圾回收？

JVM 负责对象的生命周期管理，自动释放无用对象占用的内存，避免内存泄露。

### 常见垃圾回收器

- **Serial GC**：单线程，适合小内存场景
- **Parallel GC**：多线程，吞吐量优先
- **CMS GC**：低延迟，适合响应时间敏感场景
- **G1 GC**：区域化管理，适合大堆内存

### 垃圾回收的分代

- **新生代（Young Generation）**：Eden、Survivor
- **老年代（Old Generation）**
- **永久代/元空间（PermGen/MetaSpace）**

### GC 过程举例

1. 对象创建时，分配在 Eden 区
2. 当 Eden 满时，Minor GC 发生，将存活对象移到 Survivor 区
3. Survivor 区存活多次后，进入老年代
4. 老年代满时，触发 Major GC（Full GC）

---

## JVM 性能调优

### 1. 常用参数

- `-Xms`：初始堆大小
- `-Xmx`：最大堆大小
- `-Xss`：每个线程栈大小
- `-XX:MetaspaceSize`：元空间初始大小
- `-XX:MaxMetaspaceSize`：元空间最大大小
- `-XX:+PrintGCDetails`：打印详细GC日志

### 2. 性能调优思路

- 根据应用特点合理设置堆大小和垃圾回收器
- 通过 GC 日志分析内存分配和回收情况
- 监控 Full GC 频率，优化对象创建与回收

**举例：**

```shell
java -Xms512m -Xmx2g -XX:+UseG1GC -XX:+PrintGCDetails -jar my-app.jar
```

---

## 常见面试题与解析

### 1. JVM 内存结构有哪些？各自作用是什么？

**答：**  
主要包括方法区（存储类信息、常量）、堆（存储对象）、虚拟机栈（存储局部变量）、本地方法栈（调用本地方法）、程序计数器（指示当前执行字节码行）。

### 2. 对象的创建过程？

**答：**
1. 类加载检查
2. 分配内存
3. 初始化零值
4. 设置对象头
5. 执行构造方法

### 3. 什么是双亲委派机制？

**答：**
类加载器先将加载请求委托给父类加载器，只有父类加载器找不到类时，子类加载器才会尝试加载。这保证了 Java 核心类的安全性。

### 4. 什么情况下对象会被回收？

**答：**
当对象不再被任何变量引用时，JVM 会将其视为“可回收”，如局部变量作用域结束或引用被置为 null。

**举例代码：**
```java
public void testGC() {
    Object obj = new Object();
    obj = null; // 此时 obj 原对象可被回收
}
```

### 5. 如何判断对象已经死亡？

**答：**
- 引用计数法（已废弃，容易出现循环引用）
- 可达性分析算法（主流）：对象不可通过 GC Roots 可达即为“死亡对象”。

### 6. 如何分析内存泄漏？

**答：**
- 使用 `jmap` 导出堆快照，借助 `MAT` 工具分析
- 监控 Full GC 频率和堆内存是否持续增长

---

## 实用命令与工具

### 1. jps - 查询 Java 进程列表

```shell
jps
```

### 2. jstack - 导出线程堆栈

```shell
jstack <pid>
```

### 3. jmap - 导出堆内存快照

```shell
jmap -dump:format=b,file=heap.hprof <pid>
```

### 4. jstat - 查看GC情况

```shell
jstat -gc <pid> 1000
```

### 5. VisualVM/Arthas - 图形化监控与诊断工具

---

## 结语

JVM 是 Java 面试中的高频考点，掌握其原理和实战技巧不仅有助于面试，更能提升开发和运维能力。建议结合代码和工具多实践，深入理解每一个知识点。

---

**建议阅读：**

- 《深入理解Java虚拟机》
- [阿里巴巴Java诊断手册](https://tech.antfin.com/community/articles/705233)
- [fastthread.io 线程分析](https://fastthread.io/)