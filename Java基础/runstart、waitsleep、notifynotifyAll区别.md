下面是对 Java 线程中 `run`/`start`、`wait`/`sleep`、`notify`/`notifyAll` 的区别简要总结：

---

### 1. `run()` 与 `start()`  
- `run()`：普通方法调用，代码在当前线程执行，不会启动新线程。  
- `start()`：启动新线程，线程进入就绪状态，由 JVM 调度并在新线程中执行 `run()` 方法。

```java
// 这是实现Runnable接口，重写里面的run()方法创建线程；
public class MyRunnable implements Runnable {
    @Override
    public void run() {
        try {
            // 睡眠3秒
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("实现Runnable接口的线程");
    }
}
// 在使用thread.run()和thread.start()是不一样的
public class MyRunnableTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("--------开始调用线程--------");
        Thread thread= new Thread(new MyRunnable());
        thread.run();
        System.out.println("--------调用线程结束--------");
    }
}
```



---

### 2. `wait()` 与 `sleep()`  
- `wait()`：是 `Object` 类的方法，只能在同步块中调用，当前线程释放锁并进入等待队列，等待被其它线程唤醒（`notify`/`notifyAll`）。  
- `sleep()`：是 `Thread` 类的静态方法，当前线程暂停指定时间，不释放锁，时间到后自动恢复运行。

---

### 3. `notify()` 与 `notifyAll()`  
- `notify()`：唤醒等待队列中**一个**正在等待该对象锁的线程。  
- `notifyAll()`：唤醒等待队列中**所有**正在等待该对象锁的线程。

---

**总结表：**

| 方法        | 作用         | 是否释放锁 | 唤醒机制     | 备注           |
| ----------- | ------------ | ---------- | ------------ | -------------- |
| run()       | 普通方法调用 | 无         | 无           | 当前线程执行   |
| start()     | 启动新线程   | 无         | 无           | 实现并发       |
| wait()      | 等待并释放锁 | 是         | notify/All   | 只能同步块中用 |
| sleep()     | 休眠不释放锁 | 否         | 时间自动唤醒 | 任意位置可用   |
| notify()    | 唤醒一个线程 | 无         | 唤醒一个     | 同步块中用     |
| notifyAll() | 唤醒所有线程 | 无         | 全部唤醒     | 同步块中用     |