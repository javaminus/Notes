不能。**lambda表达式在Java中只能用于函数式接口**（Functional Interface）的简化，而函数式接口的定义是**只包含一个抽象方法的接口**（即Single Abstract Method，简称SAM接口）。

### 详细解释

- **多方法接口**：指接口中有两个及以上的抽象方法。
- **lambda表达式**：只能实现一个方法，因此只能用于只有一个抽象方法的接口，也就是函数式接口。

#### 示例

假设接口如下：

```java
@FunctionalInterface
interface MyFunc {
    void doSomething();
}
```

这种接口就可以用lambda简化：

```java
MyFunc func = () -> System.out.println("Hello");
```

但如果是：

```java
interface MultiFunc {
    void doA();
    void doB();
}
```

**不能用lambda表达式直接实现MultiFunc接口**，必须用匿名类：

```java
MultiFunc func = new MultiFunc() {
    @Override
    public void doA() {
        // 实现A
    }
    @Override
    public void doB() {
        // 实现B
    }
};
```

### 总结

- **lambda表达式只能用于单一抽象方法的接口**（函数式接口）。
- 多方法接口不能用lambda表达式简化，只能用匿名内部类或显式类实现。

如果你有多方法接口，建议思考是否可以拆分成多个函数式接口，如果真的需要用lambda。