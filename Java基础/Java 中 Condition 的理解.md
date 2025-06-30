# Java 中 Condition 的理解

## 问题

- 什么是 Java 中的 Condition？它在多线程并发编程中扮演什么角色？
- Condition 和 Object 的 wait/notify 有什么区别？为什么需要 Condition？
- 如何使用 Condition？它适合哪些场景？

## 详细解释

### 1. 什么是 Condition？

**Condition** 是 `java.util.concurrent.locks` 包中的一个接口，配合 `Lock`（比如 `ReentrantLock`）使用，用于实现比 `Object.wait()`/`notify()` 更加灵活的线程协作机制。

> 通俗理解：Condition 就像是一个“等待队列”，线程可以在这里等待某个条件成立，被合适的时机唤醒。

### 2. Condition 的典型场景

- **生产者-消费者模型**  
  比如一个缓冲区，有“满了”和“空了”两种情况。生产者要等缓冲区有空间才能放入产品，消费者要等缓冲区有产品才能消费。  
  使用两个 Condition（notFull、notEmpty）分别管理“满”和“空”两种条件，互不干扰，比传统的 `wait/notify` 更清晰。

  **代码示例：**

  ```java
  class BoundedBuffer<T> {
      private final Lock lock = new ReentrantLock();
      private final Condition notFull = lock.newCondition();
      private final Condition notEmpty = lock.newCondition();
      private final Queue<T> queue = new LinkedList<>();
      private final int capacity;
  
      public BoundedBuffer(int capacity) {
          this.capacity = capacity;
      }
  
      public void put(T item) throws InterruptedException {
          lock.lock();
          try {
              while (queue.size() == capacity) {
                  notFull.await();
              }
              queue.add(item);
              notEmpty.signal();
          } finally {
              lock.unlock();
          }
      }
  
      public T take() throws InterruptedException {
          lock.lock();
          try {
              while (queue.isEmpty()) {
                  notEmpty.await();
              }
              T item = queue.remove();
              notFull.signal();
              return item;
          } finally {
              lock.unlock();
          }
      }
  }
  ```

### 3. Condition VS Object 的 wait/notify

| 特性             | Object.wait/notify         | Lock + Condition         |
| ---------------- | -------------------------- | ------------------------ |
| 依赖机制         | 内置锁（synchronized）     | 显式锁（Lock）           |
| 支持多个条件变量 | 不支持，只能全体唤醒       | 支持多个 Condition       |
| 灵活性           | 较低                       | 较高                     |
| 必须在锁内调用   | 是                         | 是                       |
| 唤醒粒度         | notify/notifyAll（无区分） | signal/signalAll（区分） |

- Condition 可以创建多个条件队列，可以细粒度地唤醒需要的线程。
- 使用 ReentrantLock 可以替代 synchronized，配合 Condition 实现更复杂的并发控制。

### 4. 总结性回答（复习提示词）

- Condition = “条件队列”+“线程等待/唤醒”
- 配合 Lock 使用，适合多个条件的并发场景
- 支持精准唤醒，避免“伪唤醒”或“惊群”
- 典型场景：生产者-消费者问题（notFull、notEmpty）
- 相比 Object.wait/notify，更灵活、更易管理

## 面试官可能的拓展问题及参考答案

### Q1. Condition 和 synchronized/wait/notify 有什么区别？  
A1. Condition 必须配合 Lock 使用，而 wait/notify 只能配合 synchronized。Condition 支持多个条件队列，wait/notify 只有一个等待队列。Condition 唤醒更精确，代码可读性和可维护性更高。

### Q2. signal() 和 signalAll() 的区别？  
A2. signal() 只唤醒等待队列中的一个线程（不保证是哪个），signalAll() 唤醒所有等待本 Condition 的线程。通常优先使用 signal()，提高效率，但要确保不会发生死锁或饥饿。

### Q3. Condition 为什么必须在锁内调用？  
A3. 因为 Condition 的底层实现依赖于 Lock 的同步机制，只有持有锁的线程才能安全地对等待队列进行操作，防止竞态条件。

### Q4. Condition 适合什么场景？  
A4. 适合多个条件需要独立等待/唤醒的复杂场景，例如有“满/空/半满”等多种状态的缓冲区。

### Q5. Condition 会产生虚假唤醒吗？如何处理？  
A5. 会。虚假唤醒（spurious wakeup）是指线程被唤醒后条件并未满足。所以使用 Condition（以及 wait/notify）时，必须始终用 while 循环判断条件，而不是 if。

---

## 复习小结

- Condition = 多个等待队列 + 精准唤醒
- 必须 with Lock
- 常用方法：await/signal/signalAll
- 使用 while 检查条件，防止虚假唤醒
- 用于复杂并发控制，典型如生产者-消费者