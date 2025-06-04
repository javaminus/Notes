下面为你详细介绍 Java 中常用的锁类型，包括它们的特点、适用场景，并配以简洁的代码示例，便于理解和实际应用。

---

## 1. synchronized（内置锁/监视器锁）

### 特点
- Java 关键字，隐式锁对象（this 或 class）。
- 可重入、自动释放。
- 适合简单并发控制。

### 适用场景
- 保护临界区资源，简单同步需求。

### 示例

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

---

## 2. ReentrantLock（重入锁）

### 特点
- 在 `java.util.concurrent.locks` 包下。
- 显式加锁/释放，支持公平锁、可中断、可重入、可尝试获取锁。

### 适用场景
- 需要高级锁控制，如 tryLock、响应中断等。

### 示例

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

---

## 3. ReadWriteLock / ReentrantReadWriteLock（读写锁）

### 特点
- 读写分离：多个线程可同时读，写操作互斥。
- 适合读多写少的场景。

### 适用场景
- 缓存、配置、字典等高并发读，低并发写的数据结构。

### 示例

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

---

## 4. StampedLock

### 特点
- Java 8 引入。支持乐观读锁、悲观读写锁，性能高。
- 不可重入。

### 适用场景
- 极高并发、复杂数据结构的读多写少场景。

### 示例

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

---

## 5. Semaphore（信号量）

### 特点
- 控制同时访问资源的线程数量。

### 适用场景
- 限流、连接池、资源池等。

### 示例

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

---

## 6. CountDownLatch

### 特点
- 一个或多个线程等待，直到其他线程执行完毕。

### 适用场景
- 多线程启动同步、主线程等待子线程结果。

### 示例

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

---

## 7. CyclicBarrier

### 特点
- 一组线程互相等待，直到全部到达某一同步点。

### 适用场景
- 多线程阶段性协作。

### 示例

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

---

## 总结表

| 锁类型                 | 特点                        | 典型场景           |
| ---------------------- | --------------------------- | ------------------ |
| synchronized           | 语法简单，自动加解锁        | 保护临界区资源     |
| ReentrantLock          | 灵活，支持 tryLock/公平锁等 | 高级并发控制       |
| ReentrantReadWriteLock | 读写分离，提升并发读性能    | 读多写少的数据结构 |
| StampedLock            | 乐观/悲观读写，性能高       | 高并发复杂场景     |
| Semaphore              | 控制并发线程数              | 限流、连接池       |
| CountDownLatch         | 多线程等待                  | 任务同步、批量启动 |
| CyclicBarrier          | 分阶段同步                  | 分阶段批处理、协作 |

---

如需某种锁的更复杂用法或原理详解，欢迎随时追问！