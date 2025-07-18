# Java 中 try-with-resources 与 try-catch-finally 详细讲解

---

## 1. try-catch-finally 结构

**传统资源管理方式：**

```java
FileInputStream in = null;
try {
    in = new FileInputStream("file.txt");
    // ... 使用 in
} catch (IOException e) {
    // 处理异常
} finally {
    if (in != null) {
        try { in.close(); } catch (IOException e) { /* 处理关闭异常 */ }
    }
}
```

- **流程说明：**
  - try 块：资源申请和业务逻辑。
  - catch 块：异常处理。
  - finally 块：无论是否异常，都会执行，适合释放资源（如关闭文件流、数据库连接等）。
  - 通常需在 finally 中手动判断对象是否为 null 并关闭资源。

- **缺点：**
  - 代码冗长，易出错（忘记关闭、关闭异常未处理等）。
  - 多资源嵌套时，finally 块会非常复杂。

---

## 2. try-with-resources 机制

**JDK7 引入的简洁资源管理方式：**

```java
try (FileInputStream in = new FileInputStream("file.txt")) {
    // ... 使用 in
} catch (IOException e) {
    // 处理异常
}
// 这里 in 会自动关闭，无需 finally
```

- **流程说明：**
  - 在 try 括号内声明的资源（必须实现 AutoCloseable 接口），在 try 块结束后会自动调用 close() 方法，无论是否发生异常。
  - 可声明多个资源，用分号 `;` 分隔。

- **优点：**
  - 代码简洁，自动关闭资源，降低出错概率。
  - 多资源管理更方便，关闭顺序为声明的逆序。
  - 异常屏蔽链（Suppressed Exception）机制，辅助追踪关闭异常。

---

## 3. 典型对比场景

### 传统方式（多资源嵌套）

```java
BufferedReader br = null;
FileInputStream in = null;
try {
    in = new FileInputStream("file.txt");
    br = new BufferedReader(new InputStreamReader(in));
    // 使用 br
} finally {
    try { if (br != null) br.close(); } catch (IOException e) {}
    try { if (in != null) in.close(); } catch (IOException e) {}
}
```

### try-with-resources

```java
try (
    FileInputStream in = new FileInputStream("file.txt");
    BufferedReader br = new BufferedReader(new InputStreamReader(in))
) {
    // 使用 br
}
// 自动关闭 br 和 in，顺序为先关闭 br，后关闭 in
```

---

## 4. 使用限制与注意点

- 资源类型必须实现 `AutoCloseable` 接口（如各种流、数据库连接等）。
- try-with-resources 声明的变量作用域只在 try 块内。
- catch 和 finally 块可选，可省略。
- JDK9+ 支持 try-with-resources 变量在外部声明（更灵活）。

---

## 5. 面试官可能追问 & 答案

### Q1：try-with-resources 适用于哪些类型的对象？
**答：** 适用于实现了 `AutoCloseable` 接口的对象，如各种 IO 流、JDBC 连接、Channel、Lock、ZipFile 等。自定义资源类也可实现该接口。

---

### Q2：try-with-resources 如何处理关闭资源时抛出的异常？
**答：** 主异常作为主异常返回，close() 抛出的异常被作为 Suppressed Exception 附加到主异常上（可通过 `Throwable.getSuppressed()` 获取）。这样不会丢失异常信息。

---

### Q3：多个资源时，关闭顺序如何？
**答：** 关闭顺序与声明顺序相反（后声明的先关闭），确保嵌套资源依赖能正确释放。

---

### Q4：try-with-resources 可否和 catch/finally 块一起用？
**答：** 可以。try-with-resources 后可接 catch/finally 块，catch 用于处理异常，finally 用于处理 try 块外的其他逻辑，但无需再手动关闭资源。

---

### Q5：JDK7 以前如何实现类似自动关闭效果？
**答：** 只能用 try-catch-finally 手动关闭资源，或者用第三方工具类（如 Apache Commons IO 的 IOUtils.closeQuietly）。

---

### Q6：自定义类如何支持 try-with-resources？
**答：** 只需让类实现 `AutoCloseable` 接口，并实现 `close()` 方法即可。

```java
class MyResource implements AutoCloseable {
    @Override
    public void close() {
        // 资源释放逻辑
    }
}
```

---

## 6. 总结复习口诀

- try-catch-finally：手动关闭，繁琐易漏
- try-with-resources：自动关闭，简洁安全
- 口诀：“资源要善终，自动最轻松，try-with-resources 少烦恼”

---