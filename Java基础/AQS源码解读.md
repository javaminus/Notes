# AQS（AbstractQueuedSynchronizer）源码解读

AQS（AbstractQueuedSynchronizer）是 Java 并发包（`java.util.concurrent.locks`）的核心同步器框架。ReentrantLock、Semaphore、CountDownLatch、ReentrantReadWriteLock 等常用同步工具类都是基于 AQS 实现的。

本文将从**原理、关键成员变量、核心方法、源码片段与流程示意、常见面试点**等方面对 AQS 进行解读。

---

## 一、AQS 设计思想

AQS 通过一个**int类型的state变量**和一个**FIFO同步队列**（CLH队列）来实现锁的获取与释放。

- **state**：标识同步状态，如锁的持有次数、信号量许可数等。
- **队列**：多线程争抢锁失败后会被加入到等待队列，按顺序唤醒。

### 适用场景
- **独占锁**（如ReentrantLock）
- **共享锁**（如Semaphore/CountDownLatch）
- **可扩展自定义同步器**

---

## 二、关键成员变量

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer implements java.io.Serializable {

    // 同步状态
    private volatile int state;

    // 队列头节点
    private transient volatile Node head;

    // 队列尾节点
    private transient volatile Node tail;

    // 等待队列的节点类型
    static final class Node { ... }
    // ...
}
```

- **state**: 通过`getState/setState/compareAndSetState`进行读写，保存同步资源状态。
- **head, tail**: 实现CLH队列，管理等待线程。

---

## 三、核心方法与执行流程

### 1. 独占模式（以ReentrantLock为例）

#### 加锁流程（acquire）

```java
public final void acquire(int arg) {
    if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}
```
- 首先尝试`tryAcquire`（子类实现，如ReentrantLock判断CAS设置state）。
- 未成功则将当前线程封装为Node加入队列（addWaiter），并进入acquireQueued阻塞自旋。

#### 释放锁流程（release）

```java
public final boolean release(int arg) {
    if (tryRelease(arg)) {
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```
- 先通过`tryRelease`（子类实现）释放资源。
- 如果完全释放，唤醒队列中的下一个等待节点的线程。

### 2. 共享模式（以Semaphore为例）

```java
public final void acquireShared(int arg) {
    if (tryAcquireShared(arg) < 0)
        doAcquireShared(arg);
}
```
- `tryAcquireShared`尝试获取，返回负数表示失败。
- 失败则加入队列，共享模式允许多个线程同时持有。

---

## 四、典型源码片段分析

### 1. Node 节点结构

```java
static final class Node {
    static final Node EXCLUSIVE = null;
    static final Node SHARED = new Node();
    volatile Node prev;
    volatile Node next;
    volatile Thread thread;
    volatile int waitStatus;
    // ...
}
```
- 每个等待线程都封装为Node，prev/next双向链表结构，thread存储具体线程。

### 2. 自旋获取锁（acquireQueued）

```java
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null;
                failed = false;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```
- 当前节点前驱是head且能成功获取锁，则成为新head。
- 否则自旋或阻塞等待唤醒。

### 3. 唤醒操作（unparkSuccessor）

```java
private void unparkSuccessor(Node node) {
    int ws = node.waitStatus;
    if (ws < 0)
        compareAndSetWaitStatus(node, ws, 0);
    Node s = node.next;
    if (s == null || s.waitStatus > 0) {
        s = null;
        for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
                s = t;
    }
    if (s != null)
        LockSupport.unpark(s.thread);
}
```
- 唤醒队列中下一个有效等待线程。

---

## 五、流程图（简述）

1. **加锁（acquire）**
   1. 尝试CAS抢state，成功即获得锁
   2. 失败则入队，自旋/阻塞等待
   3. 被唤醒后重新抢锁，成功则出队

2. **解锁（release）**
   1. 释放state，完全释放则唤醒下一个节点

---

## 六、AQS的扩展与自定义

- 要自定义同步器，只需继承AQS并实现`tryAcquire/tryRelease`等模板方法。
- 比如自定义的不可重入锁：

```java
class NonReentrantLock extends AbstractQueuedSynchronizer {
    protected boolean isHeldExclusively() { return getState() == 1; }
    public boolean tryAcquire(int acquires) {
        return compareAndSetState(0, 1);
    }
    protected boolean tryRelease(int releases) {
        if (getState() == 0) throw new IllegalMonitorStateException();
        setState(0);
        return true;
    }
    public void lock() { acquire(1); }
    public boolean unlock() { return release(1); }
}
```

---

## 七、常见面试问题

1. **AQS 的核心设计是什么？**
   - 利用state变量和等待队列，实现线程安全的同步器框架。
2. **AQS 如何保证公平/非公平性？**
   - 入队顺序和获取锁的条件可由子类控制（如ReentrantLock可设公平锁）。
3. **AQS 自定义同步器流程？**
   - 继承AQS，重写tryAcquire/tryRelease等方法，组合调用acquire/release即可。
4. **AQS 为什么要用自旋+阻塞结合？**
   - 提高性能，减少线程挂起/唤醒的系统开销。
5. **AQS 的队列结构是什么？**
   - CLH变种的FIFO双向链表队列，维护所有等待线程的顺序。

---

## 八、小结

AQS 以**模板方法+队列排队**的方式，将复杂的线程同步与排队逻辑抽象出来，极大简化了同步器的开发。理解AQS源码是掌握Java高性能并发编程的核心。

如需具体某一方法的详细源码分析及注释，可继续追问！