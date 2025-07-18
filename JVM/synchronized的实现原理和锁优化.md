# synchronized 的实现原理与锁优化

深入理解 `synchronized` 底层实现，能帮助我们在高并发场景下优化锁竞争，提升吞吐和响应性能。

## 1. 对象头与 Monitor 简介  
- **对象头（Object Header）**  
  - Mark Word：存储锁状态、线程 ID、hashCode、GC 分代年龄等。  
  - Klass Pointer：指向对象的元类型信息。  
- **Monitor**  
  - HotSpot 为每个对象维护一个 Monitor（操作系统互斥体 + 等待队列）。  
  - `monitorenter`／`monitorexit` 字节码指令在进入/退出 `synchronized` 区块时调用 JNI 原语进入或释放 Monitor。

## 2. 锁状态与升级流程  
HotSpot 使用三种（四种）锁状态，并根据竞争情况动态切换：

1. **无锁（Unlocked）**  
   - 对象头 Mark Word 中存储默认值，无 Monitor 关联。

2. **偏向锁（Biased Locking）**  
   - 适用于 “绝大多数情况下只有一个线程访问” 的场景。  
   - 首次获取锁时，CAS 将线程 ID 写入对象头，标记为偏向模式。  
   - 同一线程再次进入无需任何 CAS 或同步开销，直接跳过 Monitor。  
   - 如果有其他线程竞争，偏向锁撤销（Revoke）并升级为轻量级锁。

3. **轻量级锁（Lightweight Locking）**  
   - 竞争发生（另一个线程请求锁）时，线程在自己的栈帧中创建一个 Lock Record，  
     CAS 将对象头指向此 Record，并自旋等待锁释放。  
   - 自旋几次后仍未成功，可选择升级为重量级锁。  

4. **重量级锁（Heavyweight Locking / Monitor Lock）**  
   - 自旋失败或显式阻塞升级到操作系统互斥量（Mutex），线程挂起并加入等待队列。  
   - 唤醒开销大，适用于高争用时防止 CPU 空转。

状态升级顺序：  
Unlocked → 偏向锁 → 轻量级锁 → 重量级锁  
（偏向锁撤销后不再回到偏向状态；轻量级锁可在无竞争后回退到无锁状态）

## 3. JVM 锁优化技巧

1. **开启/关闭偏向锁**  
   - 默认开启：`-XX:+UseBiasedLocking`  
   - 延迟启动：`-XX:BiasedLockingStartupDelay=秒数`  
   - 可关闭：`-XX:-UseBiasedLocking`（适用于短命对象多线程竞争场景）

2. **自旋次数调优**  
   - 自旋等待能减少线程挂起/唤醒开销，但过多则浪费 CPU。  
   - 参数：`-XX:PreBlockSpin`、`-XX:SpinXXX`（JDK 9+ 参数可能变化）  

3. **锁消除（Lock Elimination）**  
   - JIT 编译阶段通过逃逸分析，发现对象仅被单线程使用，可优化掉 `monitorenter/exit`。  
   - 典型场景：方法内部创建、只在本地使用的临时对象。

4. **锁粗化（Lock Coarsening）**  
   - JIT 将多次连续的加解锁合并为一次，加大锁粒度，减少 Monitor 请求。  
   - 典型场景：循环体内多次对同一个对象加锁时。

5. **Bias Revocation 与 Bulk Rebias**  
   - 偏向锁撤销有全局和批量模式，避免单次竞争导致大量撤销带来的性能抖动。  
   - JVM 会在多次撤销同一个类型实例偏向后，进入批量撤销模式。

6. **推荐实践**  
   - 对于低争用场景，可优先使用 `synchronized`（简洁且有偏向/轻量级优化）。  
   - 对于高并发、可预测高争用场景，考虑使用 `ReentrantLock`：  
     - 支持公平锁／非公平锁切换  
     - 支持中断响应、超时获取、Condition 等高级特性  
   - 尽量缩小锁范围，避免在循环或 I/O 操作中持有锁。  
   - 使用无锁或读写分离（`ReadWriteLock`）替代重量级锁。

---

**复习要点：**  
“对象头 Mark Word + Monitor → 偏向／轻量级／重量级三态 → JIT 锁消除／锁粗化 → 参数调优（偏向、自旋）”