下面是一个演示有无 volatile 关键字区别的对比代码。这个例子用两个线程，一个线程不断检查标志位，另一个线程在主线程中修改标志位。你可以分两组代码运行：一组加 volatile，一组不加 volatile。

---

```java name=VolatileFlagDemo.java
public class VolatileFlagDemo {

    // ① 这里切换是否加 volatile 关键字观察区别
    // private /*volatile*/ boolean running = true; // 不加volatile
    private volatile boolean running = true;        // 加volatile

    public void start() {
        Thread t = new Thread(() -> {
            System.out.println("Worker started.");
            while (running) {
                // 忙等，不加sleep，增加可见性问题暴露概率
            }
            System.out.println("Worker stopped.");
        });
        t.start();

        try {
            Thread.sleep(2000); // 主线程等待2秒
        } catch (InterruptedException ignored) {}

        System.out.println("Main thread will set running = false.");
        running = false; // 试图停止工作线程
    }

    public static void main(String[] args) {
        new VolatileFlagDemo().start();
    }
}
```

---

## 使用方法

1. **先用不加 volatile 的那一行（注释掉加volatile的那行，去掉上面那行注释）运行。**
2. 再用加 volatile 的那一行（去掉 volatile 注释，注释掉不加volatile的那行）运行。

### 预期现象

- **加 volatile**：2秒后，线程能及时结束，输出"Worker stopped."。
- **不加 volatile**：有较大概率线程不会停止（死循环），"Worker stopped." 不会输出。

> 注意：不加 volatile 时，在某些 JVM 或操作系统环境下，可能偶尔会结束，但多试几次或放到服务器上可见性问题更易暴露。

---

如需详细解释原理或需要多线程可见性、内存模型资料可继续提问！