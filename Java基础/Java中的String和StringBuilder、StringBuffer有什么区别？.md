## 4. Java中的String和StringBuilder、StringBuffer有什么区别？

### 详细解释

- **String**：字符串常量，不可变。每次修改字符串其实都是新建一个字符串对象，原对象不会改变。适合少量字符串拼接和不可变数据。
- **StringBuilder**：字符串变量，不支持多线程（线程不安全），但效率高。适合在单线程下频繁拼接字符串的场景。
- **StringBuffer**：字符串变量，线程安全，方法有synchronized修饰，效率比StringBuilder低。适合多线程环境下拼接字符串。

**通俗例子：**
```java
String s = "hello";
s = s + " world"; // 实际是新建了一个字符串对象

StringBuilder sb = new StringBuilder("hello");
sb.append(" world"); // 原对象被改变

StringBuffer sf = new StringBuffer("hello");
sf.append(" world"); // 线程安全，原对象被改变
```

### 总结性提示词

> String不可变，适合少量拼接；StringBuilder高效适合单线程拼接；StringBuffer线程安全适合多线程。