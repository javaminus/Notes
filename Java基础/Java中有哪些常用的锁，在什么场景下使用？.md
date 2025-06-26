# Java 常用锁类型详解（含面试追问与答案）

本文详细介绍 Java 中常用的锁类型，包括原理、适用场景、代码示例，及面试官常见追问与详细答案。

---

## 1. synchronized（内置锁/监视器锁）

- **特点：**
  - Java 关键字，隐式锁对象（this 或 class）。
  - 可重入，自动加解锁。
  - 作用范围：代码块、方法。
- **适用场景：**
  - 保护临界资源，简单同步需求。
- **原理补充：**
  - 底层依赖对象头的 Mark Word 与 Monitor。
  - 支持偏向锁、轻量级锁、重量级锁的锁升级机制。
- **代码示例：**
  ```java
  public class SynchronizedDemo {
      private int count = 0;

      public synchronized void inc() {
          count++;
      }

      public void dec() {
          synchronized (this) {
              count--;
          }
      }
  }
  ```
- **面试追问与答案：**
  - **synchronized 的底层实现机制？**  
    synchronized 是基于 JVM 内置的监视器锁（Monitor）和对象头的 Mark Word 实现的。底层通过字节码指令（monitorenter/monitorexit）实现互斥同步。对象头记录锁状态及持有线程等信息，支持锁的多种状态（偏向锁、轻量级锁、重量级锁）。
  - **偏向锁、轻量级锁、重量级锁的区别？**  
    偏向锁：适合无竞争场景，线程获得锁后，再次进入无需 CAS 操作。  
    轻量级锁：适合短时间少量竞争，使用 CAS 操作完成加锁解锁。  
    重量级锁：高竞争场景，线程阻塞挂起，依赖操作系统的互斥量。
  - **synchronized 和 Lock 的区别？**  
    synchronized 是 JVM 层面的，自动加解锁，不支持尝试锁、公平锁等；Lock（如 ReentrantLock）是 Java 层面，功能更丰富，如可重入、公平锁、可中断、定时锁等，但需要手动释放锁。
  - **为什么 synchronized 是可重入的？**  
    同一个线程获得锁后，可以再次获得该锁（计数递增），不会被自己阻塞。这样可以避免递归或嵌套调用时死锁。
  - **synchronized 锁住的是对象还是代码块？**  
    可以锁对象（this、任意对象），也可以锁 class 对象（类锁），取决于 synchronized 用在实例方法、静态方法还是同步代码块。

---

## 2. ReentrantLock（重入锁）

- **特点：**
  - 明确加锁/解锁，灵活性强。
  - 支持重入、公平锁、可中断、可定时、tryLock 等高级功能。
  - 需手动释放锁，否则易死锁。
- **适用场景：**
  - 需要高级锁控制，复杂并发场景。
- **原理补充：**
  - 基于 AQS（AbstractQueuedSynchronizer）。
  - 可指定为公平锁（先到先得）或非公平锁（默认）。
- **代码示例：**
  ```java
  import java.util.concurrent.locks.ReentrantLock;

  public class ReentrantLockDemo {
      private final ReentrantLock lock = new ReentrantLock();
      private int count = 0;

      public void inc() {
          lock.lock();
          try {
              count++;
          } finally {
              lock.unlock();
          }
      }
  }
  ```
- **面试追问与答案：**
  - **ReentrantLock 的底层原理是什么？**  
    基于 AQS（AbstractQueuedSynchronizer）实现。AQS 维护一个同步队列，线程通过 CAS 争抢锁，获取失败则进入队列等待。
  - **公平锁和非公平锁的实现区别与场景？**  
    公平锁：按线程请求顺序获得锁，避免饿死，但吞吐量低；非公平锁：允许插队，吞吐更高，可能导致部分线程长期得不到锁。ReentrantLock 默认非公平。
  - **ReentrantLock 和 synchronized 的优缺点？**  
    ReentrantLock 功能更强，例如支持可重入、公平性、可中断、定时锁；但需要手动 unlock，易出错。synchronized 语法简单，自动释放锁，JVM 优化后性能差距不大。
  - **什么是可重入？为什么要可重入？**  
    可重入指同一线程可以重复获得同一把锁。这样递归调用或方法嵌套时不会死锁，提高编程灵活性。
  - **lock.lock() 忘记 unlock 会发生什么？**  
    该线程持有锁不释放，导致其他线程永久阻塞，严重时引发死锁或线程池耗尽。

---

## 3. ReadWriteLock / ReentrantReadWriteLock（读写锁）

- **特点：**
  - 读写分离，提高并发读性能。
  - 支持多个线程同时读，只允许一个线程写，读写互斥。
- **适用场景：**
  - 读多写少的缓存、配置类数据结构。
- **原理补充：**
  - 读锁和写锁互相依赖 AQS 队列实现。
  - 写锁可重入，读锁重入需同一线程。
- **代码示例：**
  ```java
  import java.util.concurrent.locks.ReentrantReadWriteLock;

  public class ReadWriteLockDemo {
      private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
      private int value = 0;

      public int read() {
          rwLock.readLock().lock();
          try {
              return value;
          } finally {
              rwLock.readLock().unlock();
          }
      }

      public void write(int v) {
          rwLock.writeLock().lock();
          try {
              value = v;
          } finally {
              rwLock.writeLock().unlock();
          }
      }
  }
  ```
- **面试追问与答案：**
  - **读写锁的实现原理是什么？**  
    通过 AQS 实现，读锁允许多线程并发获取，写锁互斥。写锁优先时，写线程会阻塞新进的读线程。
  - **写锁是否可降级为读锁？如何实现？**  
    可以。先持有写锁，再获取读锁，最后释放写锁（即写锁->读锁->释放写锁）。
  - **读锁能否升级为写锁？**  
    不能。直接升级会导致死锁，因为升级操作不是原子的，可能破坏锁的语义。
  - **读写锁适合什么场景？会不会有饥饿问题？**  
    适合读多写少场景。写锁可能因读锁频繁而“饿死”，可通过写优先策略缓解。

---

## 4. StampedLock

- **特点：**
  - Java 8 新增，支持乐观读、悲观读写。
  - 性能优于传统读写锁，但**不可重入**。
- **适用场景：**
  - 极高并发、复杂数据结构读多写少场景。
- **原理补充：**
  - 基于时间戳（stamp），每次锁操作返回一个 stamp 标记。
  - 乐观读允许并发写，但需校验数据一致性。
- **代码示例：**
  ```java
  import java.util.concurrent.locks.StampedLock;

  public class StampedLockDemo {
      private final StampedLock stampedLock = new StampedLock();
      private int value = 0;

      public int read() {
          long stamp = stampedLock.tryOptimisticRead();
          int result = value;
          if (!stampedLock.validate(stamp)) {
              stamp = stampedLock.readLock();
              try {
                  result = value;
              } finally {
                  stampedLock.unlockRead(stamp);
              }
          }
          return result;
      }

      public void write(int v) {
          long stamp = stampedLock.writeLock();
          try {
              value = v;
          } finally {
                  stampedLock.unlockWrite(stamp);
          }
      }
  }
  ```
- **面试追问与答案：**
  - **StampedLock 与 ReentrantReadWriteLock 有什么不同？**  
    StampedLock 支持乐观读（无需加锁，提升性能），不可重入。ReentrantReadWriteLock 支持重入和锁降级。StampedLock 适合高并发但读写操作简单的场景。
  - **什么是乐观读？它的优势和劣势？**  
    乐观读指不加锁直接读取数据，结束后校验期间有无写操作。优势是无锁高性能；劣势是数据可能不一致，需要校验，且不可重入。
  - **为什么 StampedLock 不可重入？会带来什么问题？**  
    因为每次加锁返回的 stamp 是唯一的，重入时 stamp 不同，解锁时会抛异常。递归调用或方法嵌套时需注意，容易出错。
  - **StampedLock 适合哪些高性能场景？**  
    读多写少、对数据一致性要求不极高、数据结构复杂、写操作相对分散的场景。

---

## 5. Semaphore（信号量）

- **特点：**
  - 控制同时访问特定资源的线程数量。
  - 可用于实现限流、连接池等。
- **适用场景：**
  - 并发限流、资源池控制。
- **原理补充：**
  - 内部维护一个计数器，acquire() 请求资源，release() 释放资源。
- **代码示例：**
  ```java
  import java.util.concurrent.Semaphore;

  public class SemaphoreDemo {
      private final Semaphore semaphore = new Semaphore(2);

      public void doWork() {
          try {
              semaphore.acquire();
              System.out.println(Thread.currentThread().getName() + " acquired");
              Thread.sleep(500);
          } catch (InterruptedException ignored) {
          } finally {
              semaphore.release();
              System.out.println(Thread.currentThread().getName() + " released");
          }
      }
  }
  ```
- **面试追问与答案：**
  - **Semaphore 的实现原理？**  
    基于 AQS 实现。内部维护一个计数器，线程请求资源时计数器减一，释放资源计数器加一，计数为零时新线程阻塞。
  - **Semaphore 和 CountDownLatch、CyclicBarrier 的区别？**  
    Semaphore 控制并发资源数；CountDownLatch 用于等待计数归零；CyclicBarrier 让一组线程互相等待执行。用途、用法和可复用性不同。
  - **Semaphore 支持公平性吗？**  
    支持。构造方法可传入 boolean fair 参数，true 时为公平信号量，线程按顺序获取许可。
  - **Semaphore 是否可重入？**  
    不可重入。单线程多次 acquire 需要相同次数 release，否则可能造成死锁。

---

## 6. CountDownLatch

- **特点：**
  - 允许一个或多个线程等待，直到其他线程完成操作。
  - 一次性使用，不能重置。
- **适用场景：**
  - 主线程等待子线程结果，分布式任务协作。
- **原理补充：**
  - 内部维护一个计数器，await() 阻塞，countDown() 计数减一。
- **代码示例：**
  ```java
  import java.util.concurrent.CountDownLatch;

  public class CountDownLatchDemo {
      public void example() throws InterruptedException {
          CountDownLatch latch = new CountDownLatch(2);

          Runnable worker = () -> {
              System.out.println(Thread.currentThread().getName() + " working");
              latch.countDown();
          };

          new Thread(worker).start();
          new Thread(worker).start();

          latch.await();
          System.out.println("All workers finished.");
      }
  }
  ```
- **面试追问与答案：**
  - **CountDownLatch 的实现原理？一次性还是可复用？**  
    基于 AQS 实现。内部计数器为零时唤醒 await 线程。一次性用，用完不能重置。
  - **线程数大于/小于 count 时会怎样？**  
    大于：多余线程 countDown 不影响 latch，await 线程正常释放；小于：latch 永远不会归零，await 线程一直阻塞。
  - **CountDownLatch 和 CyclicBarrier 的区别？**  
    CountDownLatch 一次性，只能倒计时一次；CyclicBarrier 可复用，可做阶段性同步。用途不同。
  - **CountDownLatch 是否可重用？**  
    不可重用。计数器归零后无法重置。

---

## 7. CyclicBarrier

- **特点：**
  - 多线程互相等待，直到全部到达某个同步点再继续。
  - 可重复使用（循环屏障）。
- **适用场景：**
  - 多线程分阶段协作、批处理。
- **原理补充：**
  - 内部基于 ReentrantLock、Condition 实现计数和唤醒。
- **代码示例：**
  ```java
  import java.util.concurrent.CyclicBarrier;

  public class CyclicBarrierDemo {
      public void example() {
          CyclicBarrier barrier = new CyclicBarrier(2, () -> System.out.println("Both reached barrier!"));
          Runnable worker = () -> {
              System.out.println(Thread.currentThread().getName() + " ready");
              try {
                  barrier.await();
              } catch (Exception ignored) {}
          };
          new Thread(worker).start();
          new Thread(worker).start();
      }
  }
  ```
- **面试追问与答案：**
  - **CyclicBarrier 的实现原理？**  
    基于 ReentrantLock 和 Condition 实现。每次线程调用 await()，计数器减一，归零后唤醒所有等待线程并可重用。
  - **CyclicBarrier 和 CountDownLatch 的区别？**  
    CyclicBarrier 可循环使用，适合分阶段同步；CountDownLatch 一次性，适合等待所有线程结束。
  - **CyclicBarrier 的回调机制（barrierAction）怎么用？**  
    构造方法可传 barrierAction 参数，计数归零时自动执行一次，常用于阶段性处理。
  - **CyclicBarrier 是怎么实现可复用的？**  
    计数归零后重置计数，可继续下一个阶段同步。

---

## 总结对比表

| 锁类型                  | 特点                         | 适用场景             |
|------------------------|------------------------------|----------------------|
| synchronized           | 语法简单，自动加解锁          | 保护临界区资源       |
| ReentrantLock          | 灵活，支持 tryLock/公平锁等   | 高级并发控制         |
| ReentrantReadWriteLock | 读写分离，提升并发读性能      | 读多写少的数据结构   |
| StampedLock            | 乐观/悲观读写，性能高         | 高并发复杂场景       |
| Semaphore              | 控制并发线程数                | 限流、连接池         |
| CountDownLatch         | 多线程等待，一次性            | 任务同步、批量启动   |
| CyclicBarrier          | 分阶段同步，可重用            | 分阶段批处理、协作   |

---

## 面试官可能追问的共性问题及答案

1. **锁的可重入性是什么？各类锁是否可重入？**  
   可重入性指同一线程可重复获得同一锁。synchronized、ReentrantLock、ReentrantReadWriteLock 支持可重入；StampedLock、Semaphore 不支持。
2. **公平锁和非公平锁区别，应用场景？**  
   公平锁按请求顺序获取，防止饿死但性能低；非公平锁允许插队，性能高但可能部分线程长时间得不到锁。高实时性要求用公平锁，普通业务场景用非公平锁。
3. **死锁产生的条件？如何避免？**  
   四个条件：互斥、不可剥夺、请求与保持、循环等待。避免方法：破坏循环等待、加锁顺序统一、加锁时 tryLock 超时等。
4. **锁优化手段有哪些？自旋锁、偏向锁、轻量级锁的涵义？**  
   优化手段：减少锁粒度、锁分段、锁消除、锁粗化等。  
   自旋锁：线程短暂自旋等待，不立刻挂起；  
   偏向锁：无竞争时锁归属同一线程，减少同步开销；  
   轻量级锁：低竞争场景下用 CAS 实现加锁，减少阻塞。
5. **锁的粒度如何影响性能？**  
   粒度大（锁范围广）安全但并发低；粒度小并发高但易死锁。需权衡选择。
6. **如何选择合适的锁？说说你项目中用过哪些锁以及为什么选择它？**  
   根据并发读写比、性能需求、代码复杂度等因素选择。常用 synchronized 保护简单资源，ReentrantLock 控制复杂场景，读多写少用读写锁。
7. **并发包（java.util.concurrent）中常见锁的底层实现？AQS 作用？**  
   ReentrantLock、Semaphore、CountDownLatch 等都基于 AQS 实现。AQS 通过队列同步器管理锁的获取与释放，实现线程安全。

如需某种锁的源码分析、性能对比、或特殊使用场景讲解，欢迎补充提问！