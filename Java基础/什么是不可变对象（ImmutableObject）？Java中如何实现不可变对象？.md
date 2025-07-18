## 1. 什么是不可变对象（Immutable Object）？Java中如何实现不可变对象？

### 详细解释

不可变对象指的是一旦创建之后，其状态（数据）就不能被修改的对象。Java标准库中最典型的不可变对象就是 `String` 类。不可变对象有如下优势：

- 线程安全：多个线程可以安全共享同一个不可变对象，无需加锁。
- 可作为哈希表的键：因为状态不会变，哈希值也不会变。
- 设计简单，易于维护。

**实现不可变对象的常用做法：**
1. 类用 `final` 修饰，防止被继承。
2. 所有字段用 `private final` 修饰，防止外部修改。
3. 不提供“setter”方法，只提供“getter”。
4. 如果类中有引用类型字段，返回副本而不是原始对象（防止外部修改）。
5. 在构造方法中，深拷贝可变对象。

**通俗例子：**
```java
public final class Person {
    private final String name;
    private final int age;
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public String getName() { return name; }
    public int getAge() { return age; }
}
```
上面这个 `Person` 类创建后，name 和 age 就无法再被改变。

### 总结性提示词

> 不可变对象：final类 + final字段 + 无setter + 深拷贝引用类型字段，线程安全、可作哈希键、设计简单。