## 问题：什么是JVM内存溢出（OOM）和内存泄漏？如何定位和解决？

### 详细解释

**JVM内存溢出（OutOfMemoryError, OOM）**指的是Java应用程序在运行期间，JVM无法为对象分配足够的内存，导致抛出`OutOfMemoryError`异常。常见类型包括堆内存溢出、方法区溢出、栈溢出等。

**内存泄漏（Memory Leak）**指程序中不再使用的对象仍然被引用，无法被GC回收，导致可用内存逐渐减少，最终可能引发OOM。

#### 1. OOM常见类型和场景

- **Java堆溢出**
  - 错误信息：`java.lang.OutOfMemoryError: Java heap space`
  - 场景：内存中对象数量过多，GC后仍无法回收足够空间。例如：大集合不断增长、缓存未清理、死循环创建对象等。
- **方法区/元空间溢出**
  - 错误信息：`java.lang.OutOfMemoryError: Metaspace`
  - 场景：频繁动态生成类（如反射、动态代理、热部署），导致类元数据空间耗尽。
- **本地线程栈溢出**
  - 错误信息：`java.lang.StackOverflowError`或`java.lang.OutOfMemoryError: unable to create new native thread`
  - 场景：递归过深、线程创建过多。

#### 2. 内存泄漏典型案例

- 静态集合类持有大量对象引用，如`StaticMap.put(key, value)`
- 监听器/回调未注销，导致对象无法被回收
- ThreadLocal未及时清理
- 缓存未淘汰或定时清理

#### 3. 定位和排查方法

- **分析OOM日志**：查看异常堆栈、错误类型。
- **生成和分析Heap Dump**：使用`-XX:+HeapDumpOnOutOfMemoryError`参数生成堆转储文件，再用MAT、VisualVM、jprofiler等工具分析。
- **监控内存使用曲线**：通过JConsole、VisualVM、Prometheus等工具实时监控堆内存、元空间、线程等指标。
- **代码审查**：关注大对象、集合、静态变量的生命周期。

#### 4. 解决思路

- 优化代码，及时释放无用对象引用
- 合理设置JVM内存参数（如`-Xmx`, `-XX:MaxMetaspaceSize`等）
- 采用合适的数据结构和缓存策略
- 定期清理Listener、ThreadLocal等资源
- 引入内存泄漏检测工具，如`LeakCanary`（Android）、`MAT`等

#### 5. 例子

```java
// 内存泄漏示例：静态集合未清理
public class MemoryLeakDemo {
    private static List<Object> cache = new ArrayList<>();
    public static void main(String[] args) {
        while (true) {
            cache.add(new Object());
        }
    }
}
```
> 逐步填满堆，最终导致OOM。

### 总结性回答/提示词

- OOM：JVM分配内存失败，常见于堆、元空间、栈
- 内存泄漏：无用对象仍被引用，无法回收
- 排查思路：分析日志、heap dump、监控曲线、代码审查
- 解决方法：优化代码、合理配置参数、用工具分析
- 复习提示：**“OOM看异常类型，heap dump查根因，注意静态变量和大对象引用”**