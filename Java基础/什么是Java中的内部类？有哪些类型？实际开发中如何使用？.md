## 13. 什么是Java中的内部类？有哪些类型？实际开发中如何使用？

### 详细解释

**内部类（Inner Class）** 是在一个类的内部定义的类。Java 支持多种内部类，主要包括：

1. **成员内部类**：定义在类的成员位置上，可以访问外部类的所有成员，包括 private 字段和方法。
2. **静态内部类**：用 static 修饰，类似于外部类，可以访问外部类的静态成员。实例化时不需要外部类对象。
3. **局部内部类**：定义在方法内部，只能在方法内部使用，作用范围仅限于方法内。
4. **匿名内部类**：没有名字的内部类，通常用于简化代码，常见于回调或事件处理（如 new Runnable() { ... }）。

**实际开发场景：**
- 封装辅助逻辑（如集合的迭代器）。
- 实现回调、监听器，简化代码。
- 辅助工具类，隐藏不必要的实现细节。

**通俗例子：**
```java
class Outer {
    private int data = 10;
    class Inner {
        void display() { System.out.println(data); }
    }
    static class StaticInner {
        void show() { System.out.println("Static inner"); }
    }
}
Outer.Inner inner = new Outer().new Inner();
inner.display();

Outer.StaticInner staticInner = new Outer.StaticInner();
staticInner.show();
```

### 总结性提示词

> 内部类有成员、静态、局部、匿名四种，常用于封装辅助逻辑、事件回调和隐藏实现细节。可访问外部类成员，简化开发。