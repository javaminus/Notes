## 问题：JVM是如何实现线程安全的？有哪些内存模型和关键字保障并发正确性？

### 详细解释

Java应用的并发安全高度依赖于JVM的线程调度、内存模型和关键字的支持。理解这些机制对于编写高效、正确的多线程程序至关重要。

#### 1. JVM内存模型（Java Memory Model, JMM）

JMM定义了**多线程间变量的可见性、原子性和有序性**。它抽象出主内存（heap中的共享变量）与每个线程私有的工作内存（寄存器、缓存）。

- **可见性**：一个线程修改变量，其他线程何时能看到。
- **原子性**：操作不可分割，线程中断不会导致数据不一致。
- **有序性**：代码执行顺序和预期一致，防止指令重排序。

#### 2. 关键字及机制

- **volatile**
  - 保证变量的可见性和禁止指令重排序，但不保证原子性。
  - 适用场景：状态标志、单例双重检查等。
  - 例子：
    ```java
    private volatile boolean running = true;
    ```
- **synchronized**
  - 保证代码块的互斥访问和可见性，基于对象监视器（Monitor）。
  - 适用场景：临界区保护、对象锁。
  - 例子：
    ```java
    synchronized(this) {
      // 临界区
    }
    ```
- **final**
  - 保证被修饰变量的初始化安全，防止对象发布时被其他线程看到未初始化的状态。

- **原子类（java.util.concurrent.atomic）**
  - 提供CAS（Compare And Swap）原子操作，适合高并发下无锁计数、累加等。
  - 例子：
    ```java
    AtomicInteger counter = new AtomicInteger(0);
    counter.incrementAndGet();
    ```

#### 3. 典型场景与例子

**场景1：双重检查锁定的单例模式**

```java
public class Singleton {
  private static volatile Singleton instance;
  private Singleton() {}
  public static Singleton getInstance() {
    if (instance == null) {
      synchronized(Singleton.class) {
        if (instance == null) {
          instance = new Singleton();
        }
      }
    }
    return instance;
  }
}
```
> 使用volatile保证instance的可见性和禁止指令重排序，synchronized保证互斥。

**场景2：高性能计数器**

```java
AtomicInteger counter = new AtomicInteger(0);
public void add() {
  counter.incrementAndGet(); // 原子操作
}
```

#### 4. 面试常考点

- volatile与synchronized的区别和适用场景
- JMM的三大特性（原子性、可见性、有序性）
- CAS原理和ABA问题
- happens-before原则

### 总结性回答/提示词

- JVM线程安全依靠JMM+关键字（volatile、synchronized、final、原子类）
- 关注可见性、原子性、有序性
- 典型场景：单例模式、原子计数、高并发下的锁和无锁
- 复习提示：**“JMM三性，volatile可见性/synchronized互斥，原子类无锁并发”**