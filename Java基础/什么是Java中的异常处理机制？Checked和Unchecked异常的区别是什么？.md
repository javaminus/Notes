## 12. 什么是Java中的异常处理机制？Checked和Unchecked异常的区别是什么？

### 详细解释

Java 通过异常处理机制来管理程序中运行时出现的各种异常情况，保障程序的健壮性。异常分为 **受检异常（Checked Exception）** 和 **非受检异常（Unchecked Exception）**：

- **Checked Exception（受检异常）**：编译器强制要求处理的异常，通常是程序外部原因导致的异常，如 `IOException`、`SQLException` 等。必须用 `try-catch` 捕获或在方法签名上用 `throws` 声明，否则编译不通过。
- **Unchecked Exception（非受检异常）**：运行时异常，继承自 `RuntimeException`，如 `NullPointerException`、`ArrayIndexOutOfBoundsException` 等。编译器不强制要求处理，常因程序逻辑错误引起。

**常见用法：**
```java
try {
    FileInputStream fis = new FileInputStream("a.txt"); // 可能抛出FileNotFoundException（Checked）
} catch (FileNotFoundException e) {
    e.printStackTrace();
}

int[] arr = new int[2];
System.out.println(arr[3]); // 抛出ArrayIndexOutOfBoundsException（Unchecked）
```

**异常处理建议：**
- Checked异常适合处理外部不可控情况（IO、数据库等）。
- Unchecked异常多为代码bug，应让其暴露出来及时修正。

### 总结性提示词

> Java异常分Checked（编译器强制处理）、Unchecked（运行时异常），用try-catch/throws处理，提升程序健壮性和容错性。