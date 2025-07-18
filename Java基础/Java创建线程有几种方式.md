Java 创建线程主要有以下几种方式：

---

## 1. 继承 Thread 类

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Hello from Thread!");
    }
}

// 使用
new MyThread().start();
```

---

## 2. 实现 Runnable 接口

```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello from Runnable!");
    }
}

// 使用
new Thread(new MyRunnable()).start();
```

---

## 3. 实现 Callable 接口 + FutureTask

```java
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

class MyCallable implements Callable<String> {
    @Override
    public String call() {
        return "Hello from Callable!";
    }
}

// 使用
FutureTask<String> task = new FutureTask<>(new MyCallable());
new Thread(task).start();
```

---

## 4. 使用线程池（ExecutorService）

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

ExecutorService executor = Executors.newFixedThreadPool(2);
executor.submit(() -> System.out.println("Hello from thread pool!"));
```

---

## 拓展方式（Java 8+ 常用）

- 使用 Lambda 表达式
```java
new Thread(() -> System.out.println("Hello from lambda!")).start();
```

---

### 总结

1. 继承 Thread
2. 实现 Runnable
3. 实现 Callable + FutureTask
4. 线程池（ExecutorService）
5. Lambda（属于语法糖，本质还是 Runnable）

如需代码演示或源码解析可继续提问！