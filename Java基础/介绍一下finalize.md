`finalize` 是 Java 中定义在 `java.lang.Object` 类中的一个方法，其作用是让对象在被垃圾回收（GC）前有机会做一些资源释放或清理动作。

### 主要特点
- **定义**：`protected void finalize() throws Throwable { }`
- **调用时机**：当垃圾回收器准备回收对象时，自动调用该对象的 `finalize()` 方法。每个对象最多只会调用一次。
- **常见用途**：用于释放非内存资源（如关闭文件、网络连接等），但实际推荐使用 `try-with-resources` 或显式关闭。
- **注意事项**：
  - `finalize()` 不能保证一定会被执行，也不能确定具体的执行时间。
  - 如果对象在 `finalize()` 中再次变成可达状态（即“复活”），本次不会被回收。
  - `finalize()` 执行速度慢且不确定，可能带来性能问题。
  - 从 Java 9 开始，`finalize()` 方法已被标记为“过时”（deprecated）。

### 示例代码
```java
@Override
protected void finalize() throws Throwable {
    try {
        // 资源清理代码
        System.out.println("对象被回收，释放资源");
    } finally {
        super.finalize();
    }
}
```

### 更好的替代方案
- 推荐使用 `AutoCloseable` 接口配合 `try-with-resources` 语句自动释放资源，避免依赖 `finalize()`。

```java
public class AutoCloseableDemo implements AutoCloseable {
    public void doSomething() {
        System.out.println("正在处理资源...");
    }

    @Override
    public void close() {
        System.out.println("资源已自动关闭！");
    }

    public static void main(String[] args) {
        try (AutoCloseableDemo demo = new AutoCloseableDemo()) {
            demo.doSomething();
            // 这里可能抛出异常，资源依然能被自动关闭
        }
    }
}
```



### 总结
`finalize()` 是一种对象被回收前的“临终钩子”，但由于不可靠和效率低下，实际开发中应避免使用，优先采用更安全的资源管理方式。