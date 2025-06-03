## 问题：JVM 中线程栈（Stack）溢出（StackOverflowError、OutOfMemoryError: unable to create new native thread）是怎么发生的？如何排查与优化？

---

### 详细解释（结合场景 & 通俗例子）

#### 1. 线程栈的定义与作用
- **线程栈**（Java 虚拟机栈 / 本地方法栈）为每个线程分配，存储方法调用信息、局部变量、操作数栈等。
- 每个线程启动时，JVM 会为其分配一块独立的栈空间。

#### 2. 两种典型的线程栈溢出错误
- **StackOverflowError**
  - 单个线程栈空间用尽（如无限递归/深度递归），抛出 `java.lang.StackOverflowError`。
  - 例子：
    ```java
    public void endless() { endless(); }
    endless(); // 很快报 StackOverflowError
    ```
- **OutOfMemoryError: unable to create new native thread**
  - 系统总线程数达到上限（OS 或 JVM 限制），再创建新线程时抛出。
  - 常见于线程池配置过大、应用频繁创建新线程，或服务器资源不足。
  - 例子：误把线程池 coreSize/maxSize 设置得极大时，持续请求下会耗尽资源。

#### 3. 排查与优化思路
- **StackOverflowError**
  - 检查递归调用逻辑，避免无限递归或过深递归。
  - 优化算法，改递归为迭代。
  - 可通过 `-Xss` 参数调整单线程栈大小（如 `-Xss256k`），但不建议随意调大。
- **unable to create new native thread**
  - 通过 `jstack`、`ps`、`top` 等工具查看线程数量和状态。
  - 检查线程池配置，合理设置 corePoolSize、maxPoolSize。
  - 评估 OS 级线程数限制（如 Linux 的 `ulimit -u`）。
  - 检查是否有线程泄漏（线程未关闭或异常退出未回收）。

#### 4. 实际生产场景举例
- **无限递归**：递归算法未设置出口，或出口条件判断错误导致爆栈。
- **线程泄漏**：Web 容器或微服务中，业务代码反复 new Thread 执行任务，未用线程池，系统很快无法分配新线程。

---

### 总结性的回答（复习提示词）

- **线程栈溢出**：单线程栈满 = StackOverflowError；系统线程数满 = unable to create new native thread
- **排查方法**：递归/线程池/线程数量
- **优化手段**：递归转迭代、合理分配线程池、控制线程数
- **口诀**：`“单栈爆栈是递归，线程数爆是池管，jstack定位，参数调优”`