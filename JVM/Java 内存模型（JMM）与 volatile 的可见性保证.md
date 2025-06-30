# Java 内存模型（JMM）与 volatile 的可见性保证

## 1. Java 内存模型（JMM）概念

- **主内存（Main Memory）**  
  所有线程共享的内存区域，存放对象实例和变量的最终值。
- **工作内存（Working Memory）**  
  每个线程独享的缓存区域，存放共享变量的副本和操作时的中间值。
- **拷贝与回写**  
  线程对共享变量的读写，都是**先拷贝到工作内存**，在本地操作，**最后回写主内存**。

## 2. Happens-Before 原则

- 就像规定了“先做什么，再做什么”的顺序。
- 保证前面做的事（比如变量写入）对后面的操作能看得见，且不会被重排序到后面。

JMM 通过“先行发生”（happens-before）规则，保证操作的**有序性与可见性**：

1. **程序顺序规则**  
   单线程内，前面的操作先行发生于后面的操作。
2. **监视器锁规则**  
   一个锁的 unlock 先行发生于后续对同一锁的 lock。
3. **volatile 变量规则**  
   对 volatile 变量的写操作先行发生于后续对该变量的读操作。
4. **传递性**  
   如果 A happens-before B，且 B happens-before C，则 A happens-before C。
5. **线程启动/终止规则**  
   Thread.start() 先行发生于新线程中所有操作；线程中所有操作先行发生于其 Thread.join() 返回。

## 3. volatile 的实现机制

- **可见性**  
  对 volatile 变量的写，JVM **立刻回写主内存**；对 volatile 变量的读，**直接从主内存读取**，保证所有线程看到的总是最新值。
- **有序性**  
  volatile 写操作 **禁止与前后读/写重排序**。JVM 会插入内存屏障（memory barrier）：
    - 写后插入 StoreStore、StoreLoad 屏障
    - 读前插入 LoadLoad、LoadStore 屏障
- **不保证原子性**  
  复合操作（如 i++）仍需用 synchronized 或 CAS 保证原子性。

## 4. synchronized 与其他并发原语

- **synchronized**
  - 基于监视器锁
  - 进入/退出锁前后会插入内存屏障，保证可见性和互斥
- **CAS（Compare-And-Swap）**
  - JVM 和硬件支持的原子操作，广泛用于 JUC 原子类
- **Lock/AQS**
  - ReentrantLock、ReadWriteLock 等，底层基于 AQS（AbstractQueuedSynchronizer），支持更灵活的同步策略

## 5. 典型面试追问

### 1. 为什么 volatile 不能替代锁？
**答：**
- volatile 只能保证变量的“可见性”和“有序性”，
- 不能保证**复合操作的原子性**（如 i++、check-then-act 等），
- 而锁（synchronized/Lock）可以保证互斥和原子性。

### 2. volatile 适合什么场景？
**答：**
- 适用于**状态标志**（如中断标志、停止标志）等简单读写变量，无需复合操作
- 实现**双重检查锁定**（DCL）模式中的变量可见性

### 3. JMM 中的内存屏障有哪些？作用是什么？
**答：**
- 常见的内存屏障有：LoadLoad、LoadStore、StoreStore、StoreLoad
- 作用：**禁止特定类型的指令重排序**，保证内存操作的可见性与有序性

### 4. happens-before 与指令重排序的关系？
**答：**
- happens-before 规则**约束了可见性和有序性**
- 编译器和 CPU 允许指令重排序，但不会违反 happens-before 规则
- 只要程序行为符合 happens-before 语义，多线程就是安全的

### 5. volatile 的底层实现机制？
**答：**
- 通过 JVM 插入内存屏障（memory barrier），并利用 CPU 的缓存一致性协议（MESI），确保写入主内存和读取主内存的顺序和可见性

---

## 小结/提示词

- JMM：主内存 vs 工作内存
- happens-before：顺序、锁、volatile、线程启动/终止
- volatile：可见+有序，不原子
- synchronized/CAS/AQS：互斥与高并发原语
- 复习口诀：**“先行发生规则定序，volatile 可见有序锁互斥”**