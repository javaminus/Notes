# JVM 的方法区、永久代（PermGen）、元空间（Metaspace）区别与常见问题

## 1. 概念与区别

### 方法区（Method Area）

- JVM 规范定义的内存区域，用于存储**类结构信息**（类元数据）、常量池、静态变量、JIT 编译后的代码等。
- 并未规定具体实现方式，由各 JVM 实现自行决定。

### 永久代（PermGen）

- HotSpot JVM 对“方法区”的实现，**JDK8 及以前版本**采用。
- 分配在**JVM 进程内的固定内存区域**，容量有限，默认几十 MB，可配置 `-XX:PermSize` 和 `-XX:MaxPermSize`。
- 存储内容：类元数据、常量池、静态变量、类加载器、JSP/反射生成的类等。
- **常见问题**：频繁动态生成类（如大量 JSP、CGLib 代理、反射）会导致 PermGen OOM（`java.lang.OutOfMemoryError: PermGen space`）。

### 元空间（Metaspace）

- **JDK8 及以后 HotSpot JVM** 用 Metaspace 取代了 PermGen，依然实现“方法区”功能。
- 分配在**本地内存（Native Memory）**，理论上只受系统物理内存限制。
- 可通过 `-XX:MetaspaceSize` 和 `-XX:MaxMetaspaceSize` 设置初始/最大空间。
- **优势**：不再易因类过多而 OOM，适应动态类加载多的场景。
- **常见问题**：极端情况下（如恶意/大量动态代理）可能导致本地内存耗尽，抛出 `java.lang.OutOfMemoryError: Metaspace`。

---

## 2. 典型应用场景与调优

- **Web 容器/动态代理**：如 Tomcat 频繁编译 JSP、Spring/CGLib 频繁生成代理类，易撑爆 PermGen/Metaspace。
- **调优参数**：
  - JDK8 以前：
    - `-XX:PermSize=64m -XX:MaxPermSize=256m`
  - JDK8 及以后：
    - `-XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m`
- **诊断工具**：
  - `jmap -clstats`、`jcmd`、VisualVM、JProfiler 等，分析类加载与元空间占用。

---

## 3. 通俗例子

假设：通过 CGLib 动态代理频繁生成大量类：

```java
for (int i = 0; i < 100000; i++) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(SomeClass.class);
    enhancer.setCallback(new MethodInterceptor() { ... });
    enhancer.create(); // 每次生成一个新代理类
}
```

- 在 JDK7 下，运行到一定数量后会报 PermGen OOM。
- 在 JDK8+ 下，报 Metaspace OOM（但理论上空间更大，仍可被耗尽）。

---

## 4. 总结性复习提示

- **方法区**：存类元数据/常量池/静态变量
- **PermGen**：JDK8-，JVM进程内，易OOM
- **Metaspace**：JDK8+，本地内存，物理内存限制
- **口诀**：  
  **“方法区元数据信息，PermGen易爆，Metaspace更大，一样可OOM”**

---

## 5. 面试官常见追问及参考答案

### Q1. 为什么要废弃 PermGen，改用 Metaspace？
**A1:**  
PermGen 空间固定且默认较小，容易 OOM，扩展性差；Metaspace 使用本地内存，空间更大，适合动态类多的现代应用。

---

### Q2. Metaspace 还会 OOM 吗？如何避免？
**A2:**  
会，极端情况下（如大量动态代理、JSP编译等）本地内存耗尽仍会 OOM。可通过 `-XX:MaxMetaspaceSize` 设限，并监控类加载和内存。

---

### Q3. 静态变量存在 PermGen/Metaspace 吗？为何？
**A3:**  
是的。静态变量属于类级别内容，随类元数据一同加载到方法区（PermGen/Metaspace），而非堆内存。

---

### Q4. 如何排查 PermGen/Metaspace OOM 问题？
**A4:**  
- 观察 OOM 日志报错信息
- 用 `jmap -clstats`、`jcmd GC.class_histogram`、VisualVM 等工具分析类加载数量和元空间占用
- 检查应用是否有动态类加载泄漏或重复加载问题

---

### Q5. JDK8+ 堆内存调大能否解决 Metaspace OOM？
**A5:**  
不能。Metaspace 使用本地内存，与堆（-Xms/-Xmx）无关，需单独设置 `-XX:MaxMetaspaceSize`。

---