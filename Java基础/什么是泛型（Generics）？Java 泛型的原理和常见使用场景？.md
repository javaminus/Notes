## 3. 什么是泛型（Generics）？Java 泛型的原理和常见使用场景？

### 详细解释

**泛型**允许在类、接口、方法中使用类型参数，实现代码的类型安全和重用。  
Java 泛型本质上是“类型擦除”（Type Erasure）实现的：编译后泛型信息会被移除，所有泛型参数被替换为 Object 或边界类型。这样保证了向下兼容，但也有局限（如无法直接 new T()、数组无法泛型化等）。

**常用场景：**
- 集合框架中广泛使用泛型，如 `List<String>`、`Map<Integer, String>`。
- 自定义泛型类、泛型方法，增强代码通用性和安全性。

**例子：**
```java
public class Box<T> {
    private T value;
    public void set(T value) { this.value = value; }
    public T get() { return value; }
}
Box<Integer> intBox = new Box<>();
intBox.set(123); // 只能存放Integer
```
编译期间检查类型，避免类型转换异常。

### 总结性提示词

> 泛型：类型参数化，类型检查安全，底层类型擦除，常用于集合、自定义通用类和方法。

---

---

