## 15. 常见的Java异常类型有哪些？异常和错误的区别是什么？

### 详细解释

- **异常（Exception）**：程序运行过程中发生的不正常情况，属于 `java.lang.Exception` 类及其子类。
- **常见异常类型：**
  - **运行时异常（RuntimeException及其子类）**：如 `NullPointerException`、`ArrayIndexOutOfBoundsException`、`ClassCastException`、`ArithmeticException` 等。编译器不会强制检查，通常是程序员的代码错误引起。
  - **受检异常（非运行时异常）**：如 `IOException`、`SQLException`、`FileNotFoundException` 等。编译器强制要求处理（try-catch或throws）。
  - **自定义异常**：继承自Exception或其子类，可根据需要自定义。

- **错误（Error）**：`java.lang.Error` 及其子类，表示JVM无法恢复的严重问题，如 `OutOfMemoryError`、`StackOverflowError`。通常不建议捕获或处理。

**异常和错误的区别：**
- 异常是可以被程序处理和恢复的；错误是无法处理的严重问题。
- 异常用于程序本身的逻辑问题，错误多为系统级故障。

**通俗例子：**
```java
try {
    int[] arr = new int[3];
    arr[5] = 10; // 会抛出ArrayIndexOutOfBoundsException
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("数组越界异常");
}
```

### 总结性提示词

> 异常可分为运行时和受检异常，可被程序处理；错误是严重问题，通常无法恢复。