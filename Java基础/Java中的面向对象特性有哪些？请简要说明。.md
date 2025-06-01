## 5. Java中的面向对象特性有哪些？请简要说明。

### 详细解释

Java 是一种面向对象编程语言，主要有四大面向对象特性：

1. **封装（Encapsulation）**：把数据和操作数据的方法封装在一个类中，对外只暴露必要的接口，隐藏实现细节，提高安全性和代码维护性。
2. **继承（Inheritance）**：子类可以继承父类的属性和方法，实现代码复用和扩展。
3. **多态（Polymorphism）**：同一个方法调用，在不同的对象上有不同的实现（方法重写、接口实现），提高程序灵活性和可扩展性。
4. **抽象（Abstraction）**：通过抽象类或接口只暴露必要的功能接口，隐藏具体实现细节，让开发者只关注需要实现的功能。

**通俗例子：**
```java
class Animal { void speak() { System.out.println("animal"); } }
class Dog extends Animal { void speak() { System.out.println("dog"); } }
Animal a = new Dog();
a.speak(); // 输出 dog，多态
```

### 总结性提示词

> Java面向对象特性有封装、继承、多态和抽象，提升代码复用性、安全性和扩展性。