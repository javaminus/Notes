### **JVM 内存区域划分**
Java 虚拟机（JVM）的内存结构主要分为 **线程私有** 和 **线程共享** 两大类，具体如下：

---

## **1. 内存区域总览**
| 内存区域 | 线程共享 | 线程私有 | 作用 |
|---------|---------|---------|------|
| **方法区（Method Area）** | ✅ | ❌ | 存储类元信息（类的结构、静态变量、常量池等） |
| **堆（Heap）** | ✅ | ❌ | 存储对象实例，GC 主要管理区域 |
| **虚拟机栈（JVM Stack）** | ❌ | ✅ | 方法执行时的栈帧（局部变量、方法调用信息） |
| **本地方法栈（Native Stack）** | ❌ | ✅ | 执行 Native 方法时使用 |
| **程序计数器（PC Register）** | ❌ | ✅ | 记录当前线程执行的字节码指令地址 |

---

## **2. 各内存区域详细解析**
### **(1) 方法区（Method Area）** ✅ 线程共享
- 存储 **类的元信息**（类的结构、方法、常量池、静态变量等）。
- **JDK 8 之前** 方法区存储在 **永久代（PermGen）**。
- **JDK 8 及以后** 使用 **元空间（Metaspace）** 替代永久代，存储在 **本地内存** 而非 JVM 堆中。

**示例（静态变量 & 常量池）：**
```java
public class Test {
    static int a = 10; // 存在方法区
    final static String CONSTANT = "Hello"; // 存在运行时常量池
}
```
🔥 **GC 触及方法区的时机：**
- **常量池回收**（如 `intern()` 后的字符串）。
- **无用类回收**（ClassLoader 被回收、无实例、不可反射访问）。

---

### **(2) 堆（Heap）** ✅ 线程共享
- **JVM 内存最大区域**，用于存放 **对象实例**。
- **垃圾回收（GC）** 主要管理此区域。
- 结构：
  - **新生代（Young Generation）**
    - **Eden 区**：新创建对象最初进入此区。
    - **Survivor From / To**：对象经过 Minor GC 幸存后转移到此区。
  - **老年代（Old Generation）**：长期存活的对象。

**示例：**
```java
class Person {
    int age;  // age 存在堆中
}
```
📌 **大对象（如长字符串、数组）** 直接进入老年代，避免大量 GC。

---

### **(3) 虚拟机栈（JVM Stack）** ❌ 线程私有
- 方法执行时的**栈帧（Stack Frame）** 存储：
  - **局部变量表**（基本类型、对象引用）
  - **操作数栈**（计算临时变量）
  - **方法返回地址**
- **栈溢出（StackOverflowError）** 发生在：
  - **递归调用过深**
  - **方法嵌套调用过多**

**示例（局部变量表 & 操作数栈）：**
```java
void test() {
    int x = 1; // x 存在局部变量表
}
```

---

### **(4) 本地方法栈（Native Method Stack）** ❌ 线程私有
- 为 **JNI（Java Native Interface）** 方法服务，调用 C/C++ 代码时使用。
- 抛出 **StackOverflowError** 或 **OutOfMemoryError**。

**示例（调用 C 代码）：**
```java
public class NativeTest {
    static {
        System.loadLibrary("native"); // 加载 C/C++ 库
    }
    public native void callNative(); // 调用本地方法
}
```

---

### **(5) 程序计数器（PC Register）** ❌ 线程私有
- 记录 **当前线程执行的字节码指令地址**。
- **Java 方法执行时**，记录字节码地址；**Native 方法执行时**，PC 为空。

**示例（多线程切换）：**
```java
new Thread(() -> {
    System.out.println("Thread A"); // PC 记录当前执行指令
}).start();
```

---

## **3. JVM 内存溢出 & 解决方案**
| 错误 | 发生原因 | 解决方案 |
|------|---------|---------|
| `OutOfMemoryError: Java heap space` | 堆内存不足 | 调整 `-Xmx`、优化 GC、减少大对象 |
| `OutOfMemoryError: Metaspace` | 方法区（元空间）不足 | 增加 `-XX:MetaspaceSize` |
| `StackOverflowError` | 递归过深，栈空间溢出 | 增加 `-Xss`，优化递归 |
| `OutOfMemoryError: unable to create new native thread` | 线程过多 | 限制线程池大小，优化并发 |

---

## **4. JVM 参数优化（常见 JVM 调优参数）**
```shell
# 设置堆大小
-Xms512m -Xmx1024m  # 最小 512MB，最大 1GB

# 设置年轻代大小
-XX:NewRatio=2  # 年轻代 = 1/3 堆大小

# 设置元空间大小
-XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m

# 设置 GC 机制（G1 垃圾回收）
-XX:+UseG1GC
```

---

## **5. 总结**
- **方法区**：存储类元数据，JDK 8 之后使用 **元空间（Metaspace）**。
- **堆**：存储对象，GC 主要管理区域，分 **新生代 & 老年代**。
- **虚拟机栈**：存储局部变量表、方法调用信息，递归深会导致 **StackOverflowError**。
- **本地方法栈**：服务于 JNI 调用，溢出也会抛出 **StackOverflowError**。
- **程序计数器**：记录当前线程执行的 **字节码指令地址**。

💡 **优化 JVM 运行时性能** 需合理分配 **堆大小（`-Xms -Xmx`）、元空间（`-XX:MetaspaceSize`）、GC 算法（`-XX:+UseG1GC`）** 等参数。