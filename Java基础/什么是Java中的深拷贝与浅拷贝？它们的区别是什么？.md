## 8. 什么是Java中的深拷贝与浅拷贝？它们的区别是什么？

### 详细解释

- **浅拷贝（Shallow Copy）**：复制对象时，只复制对象本身和其中的基本数据类型字段，对于引用类型字段，仅复制引用（地址），不会复制引用对象本身。这样，原对象和拷贝对象中的引用类型字段指向同一个对象。
- **深拷贝（Deep Copy）**：不仅复制对象本身，还会递归复制其所有引用类型的字段（复制引用对象本身）。这样，原对象和新对象完全独立，互不影响。

**常见实现方式：**
- 浅拷贝：实现 `Cloneable` 接口并重写 `clone()` 方法，调用 `super.clone()`。
- 深拷贝：在 `clone()` 方法中，对所有引用类型字段也进行 clone，或通过序列化/反序列化实现。

**通俗例子：**
```java
class Address implements Cloneable {
    String city;
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
class Person implements Cloneable {
    String name;
    Address address;
    public Object clone() throws CloneNotSupportedException {
        Person p = (Person) super.clone();
        p.address = (Address) address.clone(); // 深拷贝
        return p;
    }
}
```
如果只用 `super.clone()`，address 字段就是浅拷贝；如果也调用 address 的 clone 方法，则是深拷贝。

### 总结性提示词

> 深拷贝复制对象及其引用对象，浅拷贝只复制引用地址。深拷贝两对象完全独立，浅拷贝引用类型字段会相互影响。