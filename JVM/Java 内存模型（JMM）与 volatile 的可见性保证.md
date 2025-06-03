## 问题11：Java 内存模型（JMM）与 volatile 的可见性保证

深入理解 JMM、happens-before 规则和 `volatile` 关键字，是掌握 Java 并发安全的基础。

#### 1. Java 内存模型（JMM）概念
- **主内存（Main Memory）**  
  所有线程共享的内存区域，存放对象实例和变量的最终值。  
- **工作内存（Working Memory）**  
  每个线程独享的缓存区域，用于存放共享变量的副本和操作时的中间值。  
- **拷贝与回写**  
  线程对共享变量的每次读、写，都是先从主内存拷贝到工作内存，然后在工作内存操作，最后再回写到主内存。

#### 2. Happens-Before 原则
JMM 提供一组“先行发生”（happens-before）规则，用来约束操作执行顺序，确保可见性与有序性：
1. **程序顺序规则**  
   同一线程内，代码按先后顺序执行，前面的操作先行发生于后面的操作。  
2. **监视器锁规则**  
   对同一锁的 `unlock()` 先行发生于后续任何对同一锁的 `lock()`。  
3. **volatile 变量规则**  
   对一个 `volatile` 变量的写，先行发生于后面对该变量的读。  
4. **传递性**  
   如果 A happens-before B，且 B happens-before C，则 A happens-before C。  
5. **线程启动/终止规则**  
   `Thread.start()` 先行发生于新线程中的所有操作；线程中所有操作先行发生于其 `Thread.join()` 返回。

#### 3. volatile 的实现机制
- **可见性**  
  每次写入 `volatile` 变量，JVM 会立即刷新到主内存；每次读取，都会从主内存重新加载，避免缓存不一致。  
- **有序性**  
  `volatile` 写操作禁止与它前后的读/写重排序；内存屏障（memory barrier）在写后插入 StoreStore+StoreLoad 屏障，在读前插入 LoadLoad+LoadStore 屏障。  
- **不保证原子性**  
  自增（`i++`）等复合操作仍需同步或 CAS 实现原子。

#### 4. synchronized 与其他并发原语
- **synchronized**  
  基于监视器锁 (monitor)，进入/退出锁前后也会做内存屏障，保证同一锁保护下的可见性与互斥。  
- **CAS（Compare-And-Swap）**  
  硬件级别原子操作，JUC 类（如 `AtomicInteger`）广泛使用。  
- **Lock / AQS**  
  `ReentrantLock`、`ReadWriteLock` 等实现更丰富的公平/非公平策略与条件变量，底层基于 AQS 队列。

#### 5. 典型面试追问
- 为什么 `volatile` 不能替代锁？  
- 什么场景下要用 `volatile`？（状态标志、双重检查）  
- JMM 中的内存屏障有哪些？作用是什么？  
- `happens-before` 与指令重排序（编译器/CPU）的关系。

### 小结/提示词
- JMM：主内存 vs 工作内存  
- happens-before：顺序、锁、volatile、线程启动/终止  
- volatile：可见+有序，不原子  
- synchronized/CAS/AQS：互斥与高并发原语  
- 复习口诀：“先行发生规则定序，volatile 可见有序锁互斥”  