## 问题：JVM 的方法区、永久代（PermGen）与元空间（Metaspace）有什么区别？各自的作用与常见问题是什么？

---

### 详细解释（结合场景 & 通俗例子）

#### 1. 方法区、PermGen、Metaspace 的概念和区别

- **方法区（Method Area）**
  - JVM 规范定义的内存区域，用于存放类结构信息（类元数据）、常量池、静态变量、JIT 编译后的代码等。
  - 由 JVM 实现决定其实现方式。

- **永久代（PermGen）**
  - HotSpot JVM 对“方法区”的具体实现，JDK 8 及之前版本采用。
  - 分配在虚拟机进程的内存空间中，容量有限（默认几十 MB，可通过 `-XX:PermSize` 和 `-XX:MaxPermSize` 调整）。
  - 存储内容：类的元数据（类名、方法/字段、常量池）、静态变量、类加载器、JSP/反射生成的类等。
  - **常见问题**：频繁动态生成类（如大量 JSP、CGLib 动态代理、反射）会导致 PermGen OOM（`java.lang.OutOfMemoryError: PermGen space`）。

- **元空间（Metaspace）**
  - JDK 8 及以后，HotSpot JVM 用元空间取代了永久代，依然实现“方法区”。
  - 分配在本地内存（Native Memory），理论上只受系统物理内存限制。
  - 可通过 `-XX:MetaspaceSize` 和 `-XX:MaxMetaspaceSize` 设置初始/最大空间。
  - **优势**：减少类元数据 OOM，适应动态类加载多的场景。  
  - **常见问题**：极端情况下（如恶意/大量动态代理）可能导致本地内存耗尽，抛出 `java.lang.OutOfMemoryError: Metaspace`。

#### 2. 典型应用场景与调优

- **Web 容器/动态代理**
  - 大量 JSP 动态编译、或 Spring/CGLib 频繁生成代理类，极易撑爆 PermGen/Metaspace。
- **调优参数**
  - JDK8- 及以前：
    - `-XX:PermSize=64m -XX:MaxPermSize=256m`
  - JDK8+：
    - `-XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m`
- **诊断工具**
  - 使用 `jmap -clstats`、`jcmd`、`VisualVM` 等分析类加载和元空间占用。

#### 3. 通俗例子

```java
// 动态生成大量类（如代理、JSP），易爆 PermGen/Metaspace
for (int i = 0; i < 100000; i++) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(SomeClass.class);
    enhancer.setCallback(new MethodInterceptor() { ... });
    enhancer.create(); // 每轮生成一个新代理类
}
```
- 在 JDK7，会报 PermGen OOM；JDK8+ 报 Metaspace OOM。

---

### 总结性的回答（复习提示词）

- **方法区**：存类元数据/常量池/静态变量
- **PermGen**：JDK8-，JVM进程内，易OOM
- **Metaspace**：JDK8+，本地内存，物理内存限制
- **口诀**：`“方法区元数据信息，PermGen易爆，Metaspace更大，一样可OOM”`