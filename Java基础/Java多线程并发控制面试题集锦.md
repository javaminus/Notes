# Java 多线程并发控制面试题集锦

## 1. synchronized - 线程安全的计数器

**问题描述**: 
实现一个线程安全的计数器，多个线程可以同时对计数器进行递增操作，确保最终结果正确。

**Java代码实现**:
```java
public class SynchronizedCounter {
    private int count = 0; 
    
    // 使用synchronized方法确保线程安全
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() { // 为了保证可见性，不过更好的写法是去掉这里的synchronized，在count变量添加volatile
        return count;
    }
    
    public static void main(String[] args) throws InterruptedException {
        final SynchronizedCounter counter = new SynchronizedCounter();
        int threadCount = 10;
        int incrementsPerThread = 1000;
        
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("Expected count: " + (threadCount * incrementsPerThread));
        System.out.println("Actual count: " + counter.getCount());
    }
}
```



---

### 1. `getCount()` 是否需要加 `synchronized`？

**分析：**

- `getCount()` 方法只是读取 `count` 变量的值，没有修改它。
- 但在多线程环境中，如果没有 `synchronized`，`count` 变量的变化对其他线程来说**可能不可见**（JMM内存模型的可见性问题）。
- 如果 `increment()` 是同步的，`getCount()` 不同步，主线程在 `join()` 之后调用 `getCount()`，**理论上**由于所有线程都已结束，`count` 的最终值应该是准确的。因为此时主线程和其他线程之间已经有了“happens-before”关系。
- 但**如果在多线程运行时随时调用 `getCount()`**，没有同步修饰可能会看到旧值（因为JVM可能对`count`做了缓存）。

**结论：**

- **安全起见**，如果你希望`getCount()`在任何时候都能拿到最新值，建议加`synchronized`或者把`count`声明为`volatile`。
- 你这个程序里，`main`线程是在所有子线程`join()`之后才调用`getCount()`，所以即使不加`synchronized`，最后一次输出也不会有问题（但这属于特例）。

---

### 2. `synchronized` 锁的是什么？

`synchronized` 有两种用法：

#### 1. 修饰实例方法（如你的`increment`、`getCount`）

- 锁的是**当前对象实例**（即`this`）。
- 同一个对象的`synchronized`实例方法，同一时间只能有一个线程进入。

#### 2. 修饰静态方法

- 锁的是**当前类的Class对象**（即`SynchronizedCounter.class`）。

#### 3. 修饰代码块

- 锁的是括号里的对象（如`synchronized(obj)`）。

**你的代码里：**

```java
public synchronized void increment() { ... }
public synchronized int getCount() { ... }
```

这两个方法锁的都是**同一个SynchronizedCounter实例**（即`synchronizedCounter`这个对象）。  
同一时刻，只有一个线程能执行这两个方法中的任何一个。

---

### 总结

- getCount() 是否加 synchronized？  
  - 如果只是主线程在所有子线程结束后读取，可以不加。
  - 如果在多线程环境下随时读取，建议加 synchronized 或 volatile。
- synchronized 锁的是谁？  
  - 实例方法锁当前对象（this），静态方法锁类的Class对象，代码块锁括号里的对象。

## 2. ReentrantLock - 可中断的锁等待

**问题描述**:
实现一个银行账户转账系统，使用ReentrantLock防止死锁，并支持锁等待超时。

**Java代码实现**:
```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccountTransfer {
    static class Account {
        private final Lock lock = new ReentrantLock();
        private int balance;
        private final String id;
        
        public Account(String id, int initialBalance) {
            this.id = id;
            this.balance = initialBalance;
        }
        
        public boolean tryLock(long timeout) throws InterruptedException {
            return lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        }
        
        public void unlock() {
            lock.unlock();
        }
        
        public int getBalance() {
            return balance;
        }
        
        public void withdraw(int amount) {
            balance -= amount;
        }
        
        public void deposit(int amount) {
            balance += amount;
        }
        
        @Override
        public String toString() {
            return id;
        }
    }
    
    public static boolean transfer(Account from, Account to, int amount, long timeout) throws InterruptedException {
        if (from.tryLock(timeout)) {
            try {
                if (to.tryLock(timeout)) {
                    try {
                        if (from.getBalance() < amount) {
                            return false;
                        }
                        from.withdraw(amount);
                        to.deposit(amount);
                        return true;
                    } finally {
                        to.unlock();
                    }
                }
            } finally {
                from.unlock();
            }
        }
        return false;
    }
    
    public static void main(String[] args) {
        final Account account1 = new Account("A", 1000);
        final Account account2 = new Account("B", 1000);
        
        new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    if (transfer(account1, account2, 10, 100)) {
                        System.out.println("Transfer success: A -> B");
                    } else {
                        System.out.println("Transfer failed: A -> B");
                    }
                    Thread.sleep(100); // 让当前正在执行的线程休眠500毫秒。
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    if (transfer(account2, account1, 5, 100)) {
                        System.out.println("Transfer success: B -> A");
                    } else {
                        System.out.println("Transfer failed: B -> A");
                    }
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
```



> 你的问题非常有代表性，涉及到Java异常处理机制和lambda表达式（尤其是`Runnable`的实现）。我们详细解释一下：
>
> ---
>
> ## 你的问题重述
>
> > 为什么第68行的异常只能用catch捕获，不能直接在main方法上写`throws InterruptedException`？
>
> 具体来说，你想问的是：
> - 在`main`方法里，启动了一个线程：
>   ```java
>   new Thread(() -> {
>       for (int i = 0; i < 100; i++) {
>           transfer(acount1, acount1, 1000, 100);
>       }
>   }).start();
>   ```
>   其中`transfer`方法声明了`throws InterruptedException`。
> - 你想在lambda里直接写`throws InterruptedException`，或者在`main`方法上写`throws InterruptedException`，为什么不行？
>
> ---
>
> ## 详细解释
>
> ### 1. main方法加throws没用的原因
>
> - `main`方法加`throws InterruptedException`，只对`main`线程有效。
> - 你在`main`里启动了一个**新线程**（`new Thread(...)`），新线程的异常不会“冒泡”到`main`线程。
> - 新线程的异常必须在**新线程内部**自己处理。
>
> ### 2. lambda表达式和Runnable的限制
>
> - `Thread`的构造方法参数是`Runnable`，接口的`run()`方法签名是：
>   ```java
>   void run();
>   ```
>   **没有声明抛出任何受检异常（checked exception）！**
> - Java lambda表达式会自动实现`Runnable.run()`方法。
> - 既然`run()`方法不能抛出受检异常，所以你不能直接在lambda表达式里写`throws InterruptedException`。
>
> ### 3. 正确的做法
>
> - 只能**在lambda内部用try-catch捕获**这个异常。例如：
>   ```java
>   new Thread(() -> {
>       for (int i = 0; i < 50; i++) {
>           try {
>               transfer(acount1, acount1, 1000, 100);
>           } catch (InterruptedException e) {
>               e.printStackTrace(); // 或其他处理
>           }
>       }
>   }).start();
>   ```
> - 如果你直接写成：
>   ```java
>   new Thread(() -> {
>       for (int i = 0; i < 50; i++) {
>           transfer(acount1, acount1, 1000, 100);  // 这里会报错
>       }
>   }).start();
>   ```
>   编译器会报错：**必须捕获或声明该异常**，但是`Runnable.run()`不能声明抛出异常，所以只能捕获。
>
> ---
>
> ## 总结
>
> - 因为`Runnable.run()`没有throws，lambda实现也不能有throws，所以你只能用try-catch。
> - `main`方法加throws，只对主线程有效，对新线程无效。
> - 这就是Java受检异常和线程API设计的必然结果。
>
> 如需更详细的异常和lambda机制讲解，欢迎追问！

## 3. Semaphore - 限制资源并发访问

**问题描述**:
实现一个连接池，限制同时活动的连接数量。

**Java代码实现**:
```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ConnectionPool {
    private final List<Connection> connectionPool;
    private final Semaphore semaphore;
    
    public ConnectionPool(int maxConnections) {
        connectionPool = new ArrayList<>(maxConnections);
        for (int i = 0; i < maxConnections; i++) {
            connectionPool.add(new Connection("Connection-" + i));
        }
        // 初始化信号量，许可数等于连接池大小
        semaphore = new Semaphore(maxConnections);
    }
    
    public Connection getConnection() throws InterruptedException {
        // 获取许可
        semaphore.acquire();
        return getNextAvailableConnection();
    }
    
    public void releaseConnection(Connection connection) {
        if (returnConnection(connection)) {
            // 释放许可
            semaphore.release();
        }
    }
    
    private synchronized Connection getNextAvailableConnection() {
        for (Connection connection : connectionPool) {
            if (!connection.isInUse()) {
                connection.setInUse(true);
                return connection;
            }
        }
        return null; // 不应该发生，因为semaphore保证了可用连接
    }
    
    private synchronized boolean returnConnection(Connection connection) {
        for (Connection con : connectionPool) {
            if (con.equals(connection)) {
                con.setInUse(false);
                return true;
            }
        }
        return false;
    }
    
    static class Connection {
        private final String name;
        private boolean inUse;
        
        public Connection(String name) {
            this.name = name;
        }
        
        public boolean isInUse() {
            return inUse;
        }
        
        public void setInUse(boolean inUse) {
            this.inUse = inUse;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    public static void main(String[] args) {
        final ConnectionPool pool = new ConnectionPool(5);
        
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    Connection connection = pool.getConnection();
                    System.out.println(Thread.currentThread().getName() + " acquired " + connection.getName());
                    // 模拟使用连接
                    Thread.sleep((long) (Math.random() * 1000));
                    System.out.println(Thread.currentThread().getName() + " releasing " + connection.getName());
                    pool.releaseConnection(connection);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Thread-" + i).start();
        }
    }
}
```

## 4. CountDownLatch - 等待多个任务完成

**问题描述**:
实现一个并行任务处理器，主线程需要等待所有工作线程完成初始化后才能继续执行。

**Java代码实现**:
```java
import java.util.concurrent.CountDownLatch;

public class ParallelInitializer {
    public static void main(String[] args) throws InterruptedException {
        int serviceCount = 5;
        CountDownLatch latch = new CountDownLatch(serviceCount);
        
        System.out.println("系统启动中，正在初始化各个服务...");
        
        for (int i = 0; i < serviceCount; i++) {
            final int serviceId = i;
            new Thread(() -> {
                try {
                    // 模拟服务初始化时间不同
                    long initTime = (long) (Math.random() * 3000);
                    Thread.sleep(initTime);
                    System.out.println("服务 " + serviceId + " 初始化完成，耗时：" + initTime + " ms");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 计数减一
                    latch.countDown();
                }
            }, "Service-" + i).start();
        }
        
        // 主线程等待所有服务初始化完成
        latch.await();
        System.out.println("所有服务已初始化完成，系统准备就绪！");
        
        // 继续执行其他逻辑
        System.out.println("系统开始正常工作...");
    }
}
```

## 5. CyclicBarrier - 多线程同步等待

**问题描述**:
实现一个多阶段并行计算任务，每个阶段都需要等待所有线程完成当前阶段后再一起进入下一阶段。

**Java代码实现**:
```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MultiPhaseComputation {
    private static final int THREAD_COUNT = 5;
    private static final int PHASE_COUNT = 3;
    
    public static void main(String[] args) {
        // 创建CyclicBarrier，所有线程到达屏障时执行的动作是打印阶段完成信息
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT, () -> 
            System.out.println("====== 所有线程完成当前阶段，准备进入下一阶段 ======")
        );
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    for (int phase = 0; phase < PHASE_COUNT; phase++) {
                        // 执行当前阶段的计算
                        int processingTime = (int) (Math.random() * 1000);
                        Thread.sleep(processingTime);
                        System.out.println("线程 " + threadId + " 完成第 " + phase + " 阶段计算，耗时：" + processingTime + " ms");
                        
                        // 等待所有线程完成当前阶段
                        barrier.await();
                        System.out.println("线程 " + threadId + " 开始第 " + (phase + 1) + " 阶段计算");
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }, "ComputeThread-" + i).start();
        }
    }
}
```

## 6. AtomicReference - 原子引用更新

**问题描述**:
实现一个无锁的栈数据结构，支持多线程并发操作。

**Java代码实现**:
```java
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack<T> {
    private final AtomicReference<Node<T>> top = new AtomicReference<>();
    
    // 内部节点类
    private static class Node<T> {
        private final T value;
        private Node<T> next;
        
        public Node(T value) {
            this.value = value;
        }
    }
    
    // 入栈操作
    public void push(T value) {
        Node<T> newHead = new Node<>(value);
        Node<T> oldHead;
        do {
            oldHead = top.get();
            newHead.next = oldHead;
        } while (!top.compareAndSet(oldHead, newHead));
    }
    
    // 出栈操作
    public T pop() {
        Node<T> oldHead;
        Node<T> newHead;
        do {
            oldHead = top.get();
            if (oldHead == null) {
                return null;
            }
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead, newHead));
        return oldHead.value;
    }
    
    // 判断栈是否为空
    public boolean isEmpty() {
        return top.get() == null;
    }
    
    public static void main(String[] args) throws InterruptedException {
        final LockFreeStack<Integer> stack = new LockFreeStack<>();
        final int pushThreads = 5;
        final int popThreads = 5;
        final int pushesPerThread = 100;
        
        // 创建并启动入栈线程
        Thread[] pushers = new Thread[pushThreads];
        for (int i = 0; i < pushThreads; i++) {
            final int threadId = i;
            pushers[i] = new Thread(() -> {
                for (int j = 0; j < pushesPerThread; j++) {
                    int value = threadId * 1000 + j;
                    stack.push(value);
                    System.out.println("Pushed: " + value);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            pushers[i].start();
        }
        
        // 创建并启动出栈线程
        Thread[] poppers = new Thread[popThreads];
        for (int i = 0; i < popThreads; i++) {
            poppers[i] = new Thread(() -> {
                while (true) {
                    Integer value = stack.pop();
                    if (value != null) {
                        System.out.println("Popped: " + value);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            poppers[i].start();
        }
        
        // 等待所有入栈线程完成
        for (Thread pusher : pushers) {
            pusher.join();
        }
        
        // 运行一段时间后中断出栈线程
        Thread.sleep(2000);
        for (Thread popper : poppers) {
            popper.interrupt();
        }
        
        // 等待所有出栈线程完成
        for (Thread popper : poppers) {
            popper.join();
        }
        
        System.out.println("Stack is empty: " + stack.isEmpty());
    }
}
```

## 7. 综合场景 - 生产者消费者模式

**问题描述**:
实现一个生产者-消费者模式，生产者线程生产数据放入缓冲区，消费者线程从缓冲区取出数据进行消费。

**Java代码实现**:
```java
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerExample {
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    
    public ProducerConsumerExample(int capacity) {
        this.capacity = capacity;
    }
    
    public void produce(int item) throws InterruptedException {
        lock.lock();
        try {
            // 如果缓冲区满了，等待消费者消费
            while (buffer.size() == capacity) {
                notFull.await();
            }
            buffer.offer(item);
            System.out.println("生产: " + item + ", 缓冲区大小: " + buffer.size());
            // 通知消费者有数据可以消费了
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public int consume() throws InterruptedException {
        lock.lock();
        try {
            // 如果缓冲区为空，等待生产者生产
            while (buffer.isEmpty()) {
                notEmpty.await();
            }
            int item = buffer.poll();
            System.out.println("消费: " + item + ", 缓冲区大小: " + buffer.size());
            // 通知生产者有空间可以生产了
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) {
        ProducerConsumerExample example = new ProducerConsumerExample(5);
        
        // 创建生产者线程
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    example.produce(i);
                    Thread.sleep((long) (Math.random() * 500));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // 创建消费者线程
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    example.consume();
                    Thread.sleep((long) (Math.random() * 1000));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // 启动线程
        producer.start();
        consumer.start();
        
        // 等待线程结束
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```