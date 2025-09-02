# 方案一：`wait`/`notify` 

```java
public class Main{
    
    private static final Object lock = new Object();
    private static boolean printA = true; // 如果一个变量总是由锁保护，则不需要声明为volatile。所以这里不需要volatile
    public static void main(String[] args) {
        Thread threadA = new Thread(()->{
            for (int i = 0; i < 10; i++) {
                synchronized (lock){
                    while (!printA) {
                        try{
                            lock.wait();
                        }catch (InterruptedException e){
                            Thread.currentThread().interrupt();
                        }
                    }
                    System.out.print("A");
                    printA = false;
                    lock.notifyAll();
                }
            }
        });

        Thread threadB = new Thread(()->{
            for (int i = 0; i < 10; i++) {
                synchronized (lock){
                    while (printA) {
                        try{
                            lock.wait();
                        }catch (InterruptedException e){
                            Thread.currentThread().interrupt();
                        }
                    }
                    System.out.print("B");
                    printA = true;
                    lock.notifyAll();
                }
            }
        });

        threadA.start();
        threadB.start();
    }
    
}
```



# 方案二：使用lock+Condition

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final Lock lock = new ReentrantLock();
    private static final Condition conditionA = lock.newCondition();
    private static final Condition conditionB = lock.newCondition();
    private static boolean printA = true;

    public static void main(String[] args) {
        Thread threadA = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                try {
                    while (!printA) {
                        conditionA.await();
                    }
                    System.out.print("A");
                    printA = false;
                    conditionB.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread threadB = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                try {
                    while (printA) {
                        conditionB.await();
                    }
                    System.out.print("B");
                    printA = true;
                    conditionA.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        });

        threadA.start();
        threadB.start();
    }
}
```



# 方案三：使用信号量 `Semaphore`

```java
import java.util.concurrent.Semaphore;

public class Main {
    private static final Semaphore semaphoreA = new Semaphore(1);
    private static final Semaphore semaphoreB = new Semaphore(0); // 理解为一个池，acquire()获取池中的锁，锁的数量减一；release()释放锁到池里面，锁的数量加一；只有池的锁的数量大于0，acquire()才不会被阻塞。

    public static void main(String[] args) {
        Thread threadA = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    semaphoreA.acquire();
                    System.out.print("A");
                    semaphoreB.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread threadB = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    semaphoreB.acquire();
                    System.out.print("B");
                    semaphoreA.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        threadA.start();
        threadB.start();
    }
}
```

