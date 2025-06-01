## 9. 什么是Java中的多态？多态的实现方式和实际应用场景有哪些？

### 详细解释

**多态（Polymorphism）**是面向对象编程的三大特性之一。它指的是同一个接口或方法，针对不同的对象类型可以有不同的实现和表现。Java 的多态分为**编译时多态**（方法重载）和**运行时多态**（方法重写）。

#### 实现方式
- **方法重载（Overload）**：同一个类中方法名相同但参数不同。编译阶段确定调用哪个方法。
- **方法重写（Override）**：子类重写父类的方法。运行时根据对象的实际类型决定调用哪个方法（动态绑定）。

#### 实际应用场景
- **接口编程**：如面向接口开发，List 可以指向 ArrayList、LinkedList 等实现。
- **父类引用指向子类对象**：提高代码扩展性和灵活性。

**通俗例子：**
```java
class Animal {
    void speak() { System.out.println("Animal speaks"); }
}
class Dog extends Animal {
    void speak() { System.out.println("Dog barks"); }
}
Animal a = new Dog();
a.speak(); // 输出：Dog barks
```
此处 `a` 的编译类型是 `Animal`，运行时类型是 `Dog`，表现出多态性。

### 总结性提示词

> 多态：同一接口多种实现，分为重载和重写，父类引用指向子类对象，提升代码扩展性和灵活性。