## 问题：JVM 中的 Finalizer（finalize 方法）和清理机制是什么？为什么不推荐使用？如何安全地管理对象资源？

---

### 详细解释（结合场景 & 通俗例子）

#### 1. 什么是 Finalizer？
- **Finalizer** 指 Java 对象的 `protected void finalize()` 方法。
- 当对象即将被 GC 回收前，JVM 会自动调用该方法，允许开发者执行资源清理（如关闭文件、释放本地资源）。

#### 2. Finalizer 的工作流程
1. 对象变为“不可达”时，如果重写了 `finalize()`，GC 会将其放入 Finalizer 队列。
2. 专门的 Finalizer 线程会异步调用该对象的 `finalize()` 方法。
3. finalize() 执行完后，对象才会正式进入“可回收”状态。

#### 3. 问题与风险
- **不可预测的执行时机**：GC 何时触发、finalize 何时执行都无法保证，可能资源长期未释放。
- **性能隐患**：Finalizer 线程执行慢会导致大量待清理对象堆积，严重影响 GC 性能。
- **复活漏洞**：在 finalize() 中如果重新让对象变为可达（如赋值给静态变量），对象会“复活”，再也不会被 GC 回收，可能导致内存泄漏。
- **异常处理**：finalize() 抛出的异常不会被捕获，可能导致资源未释放。

#### 4. 替代方案与最佳实践
- **推荐使用 try-with-resources / AutoCloseable**
  - 如 `InputStream`, `FileChannel` 等都实现了 `AutoCloseable`，使用 try-with-resources 语法块能确保资源及时关闭。
- **显式资源释放**
  - 明确调用 `close()` 或 `dispose()` 方法，代码简洁、可控。
- **Cleaner/PhantomReference（JDK9+）**
  - 替代 Finalizer 的安全机制，Cleaner 机制不会导致对象复活，执行更高效。

#### 5. 通俗例子
```java
class MyResource {
    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalize called!");
        close(); // 假如这里关闭文件
    }
    public void close() { /* 关闭资源逻辑 */ }
}

// 推荐做法
try (FileInputStream in = new FileInputStream("file.txt")) {
    // 使用 in
} // 自动关闭，无需依赖 finalize
```

---

### 总结性的回答（复习提示词）

- **finalize()**：资源清理，但不安全，易泄漏
- **不推荐**：不可控、不可预测、性能差
- **推荐**：AutoCloseable + try-with-resources，JDK9+ 用 Cleaner
- **口诀**：`“资源要手关，finalize 不可靠，try-with-resources 最安全”`