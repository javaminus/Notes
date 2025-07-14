## 14. 什么是Java中的Lambda表达式？常见的使用场景有哪些？

### 详细解释

**Lambda表达式** 是 Java 8 引入的一种新特性，**用于简化匿名内部类的写法**，使代码更加简洁。它本质上是对函数式接口（只包含一个抽象方法的接口，如 Runnable、Comparator）的实现。

**基本语法：**
```java
(参数列表) -> { 方法体 }
```
参数类型可以省略，只有一个参数时括号也可省略，方法体只有一句时花括号和 return 也可省略。

**常见使用场景：**
- 集合的遍历和操作（如 forEach、map、filter、reduce）。
- 线程的简化写法（如 Runnable）。
- 事件监听、回调等只需实现单一方法的场景。

**通俗例子：**
```java
// 传统匿名类写法
new Thread(new Runnable() {
    public void run() {
        System.out.println("Hello");
    }
}).start();

// Lambda写法
new Thread(() -> System.out.println("Hello")).start();

List<String> list = Arrays.asList("a", "b", "c");
list.forEach(item -> System.out.println(item));
```

### 总结性提示词

> Lambda表达式用于简化单方法接口实现，常用于集合操作、线程、回调等场景，使代码更简洁明了。