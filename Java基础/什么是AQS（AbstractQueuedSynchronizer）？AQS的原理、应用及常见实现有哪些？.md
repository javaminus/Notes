# ⚡ 什么是AQS（AbstractQueuedSynchronizer）？AQS的原理、应用及常见实现有哪些？

---

## 1️⃣ AQS概念

**AQS（抽象队列同步器，AbstractQueuedSynchronizer）** 是 Java 并发包（`java.util.concurrent.locks`）的基础同步框架。它为各种锁（如 ReentrantLock）、同步器（如 CountDownLatch、Semaphore）等提供了统一的底层实现。

---

## 2️⃣ AQS的核心原理

- **同步状态（state）**  
  用一个 volatile int 表示资源状态（如锁是否被占用、可用资源数量等），通过CAS原子操作修改。

- **双向等待队列（CLH队列）**  
  获取资源失败的线程会被包装成`Node`节点，加入到一个FIFO的双向链表队列，排队等待。

- **独占与共享两种模式**  
  - **独占模式**：同一时刻只允许一个线程持有（如独占锁ReentrantLock）
  - **共享模式**：允许多个线程同时获取（如信号量Semaphore、闭锁CountDownLatch）

- **模板方法设计**  
  子类通过重写`tryAcquire`、`tryRelease`、`tryAcquireShared`、`tryReleaseShared`等方法，定制自己的同步逻辑。AQS负责队列管理和线程的挂起/唤醒。

---

## 3️⃣ 工作流程简述

1. 线程尝试获取资源（比如加锁），通过CAS修改state；
2. 如果获取失败，线程被包装为Node节点，加入到等待队列，并被阻塞（挂起）；
3. 当前驱节点释放资源时，会唤醒后继节点重新尝试获取资源；
4. 获取到资源的线程可以继续执行，释放资源时通过AQS方法唤醒下一个等待线程。

---

## 4️⃣ 典型应用场景与常见实现

| 同步器                 | 模式         | 说明                   |
| ---------------------- | ------------ | ---------------------- |
| ReentrantLock          | 独占         | 可重入独占锁           |
| Semaphore              | 共享         | 信号量，限流控制并发   |
| CountDownLatch         | 共享         | 闭锁，等待指定线程完成 |
| CyclicBarrier          | 共享         | 循环栅栏，分批线程同步 |
| ReentrantReadWriteLock | 读共享写独占 | 读写锁，读多写少场景   |
| FutureTask             | 独占         | 异步任务同步           |

---

## 5️⃣ 简单自定义独占锁示例

```java
class MyLock extends AbstractQueuedSynchronizer {
    @Override
    protected boolean tryAcquire(int arg) {
        return compareAndSetState(0, 1); // CAS抢占锁
    }
    @Override
    protected boolean tryRelease(int arg) {
        setState(0); // 释放锁
        return true;
    }
    public void lock() { acquire(1); }
    public void unlock() { release(1); }
}
```

---

## 📝 总结

- **AQS是Java并发的锁/同步器的核心基础**，核心思想是state变量+等待队列。
- 能实现独占锁、共享锁等多种并发控制工具。
- 面试高频：原理、队列、常见实现类、会举自定义AQS子类。

---

> 💡 口诀：AQS，状态+队列，独占共享都能管，面试必讲同步原理！