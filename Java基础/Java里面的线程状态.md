在 Java 中，线程的状态由 `java.lang.Thread.State` 枚举定义，主要包括以下 **6 种状态**：

---

### **1. NEW（新建）**
- 线程对象刚刚被创建，但还 **未启动**（未调用 `start()`）。
- 代码示例：
  ```java
  Thread thread = new Thread(() -> System.out.println("Running"));
  System.out.println(thread.getState()); // NEW
  ```

---

### **2. RUNNABLE（可运行）**
- 线程已启动，正在 **运行** 或 **等待 CPU 资源**（处于就绪队列）。
- 可能因为 **时间片** 或 **CPU 调度** 暂时没在运行。
- 代码示例：
  ```java
  Thread thread = new Thread(() -> {
      System.out.println(Thread.currentThread().getState()); // RUNNABLE
  });
  thread.start();
  ```

---

### **3. BLOCKED（阻塞）**
- 线程 **等待进入同步代码块**，但 **锁** 被其他线程占用。
- 发生场景：
  - `synchronized` 方法或代码块
  - 线程 **试图获取锁但失败**
- 代码示例：
  ```java
  class Example {
      synchronized void method() {
          try { Thread.sleep(2000); } catch (InterruptedException e) {}
      }
  }

  Example example = new Example();

  Thread t1 = new Thread(() -> example.method());
  Thread t2 = new Thread(() -> example.method());

  t1.start();
  Thread.sleep(100); // 确保 t1 先获取锁
  t2.start();

  System.out.println(t2.getState()); // BLOCKED
  ```

---

### **4. WAITING（无限等待）**
- 线程 **主动等待**，需要其他线程显式唤醒（`notify()` 或 `notifyAll()`）。
- 发生场景：
  - `Object.wait()`
  - `LockSupport.park()`
  - `Thread.join()`（无超时）
- 代码示例：
  ```java
  class Example {
      synchronized void method() {
          try { wait(); } catch (InterruptedException e) {}
      }
  }

  Example example = new Example();
  Thread t = new Thread(() -> example.method());

  t.start();
  Thread.sleep(100);
  System.out.println(t.getState()); // WAITING
  ```

---

### **5. TIMED_WAITING（超时等待）**
- 线程 **等待** 一段时间后自动唤醒。
- 发生场景：
  - `Thread.sleep(time)`
  - `Object.wait(time)`
  - `Thread.join(time)`
  - `LockSupport.parkNanos()`
  - `LockSupport.parkUntil()`
- 代码示例：
  ```java
  Thread t = new Thread(() -> {
      try { Thread.sleep(5000); } catch (InterruptedException e) {}
  });

  t.start();
  Thread.sleep(100);
  System.out.println(t.getState()); // TIMED_WAITING
  ```

---

### **6. TERMINATED（终止）**
- 线程执行完 `run()` 方法或被 **异常终止**。
- 代码示例：
  ```java
  Thread t = new Thread(() -> {});
  t.start();
  t.join(); // 等待线程执行完毕
  System.out.println(t.getState()); // TERMINATED
  ```

---

## **线程状态转换关系**
```
           +------------- NEW -------------+
           |                               |
           v                               v
      RUNNABLE  <------>  BLOCKED       WAITING
           |            /      \          |  
           |         TIMED_WAITING -------+
           v
      TERMINATED
```

---

## **总结**
| 状态 | 触发方式 | 退出方式 |
|------|---------|---------|
| NEW | `new Thread()` | `start()` |
| RUNNABLE | `start()` | 线程结束、阻塞、等待 |
| BLOCKED | `synchronized` 锁被占用 | 获取锁后变 RUNNABLE |
| WAITING | `wait()`, `join()` | `notify()`, `interrupt()` |
| TIMED_WAITING | `sleep(time)`, `wait(time)` | 时间到、被唤醒 |
| TERMINATED | 线程运行结束 | 无 |

记住这 6 种状态，有助于你更好地理解多线程调度和线程同步问题！🚀