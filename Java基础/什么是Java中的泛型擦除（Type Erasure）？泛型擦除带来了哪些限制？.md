## 15. 什么是Java中的泛型擦除（Type Erasure）？泛型擦除带来了哪些限制？

### 详细解释

**泛型擦除（Type Erasure）** 是 Java 泛型实现的机制。Java 泛型只存在于编译阶段，编译器在编译时会将泛型类型擦除，替换为原始类型（如 Object 或限定的上界），运行时不会保留任何泛型类型信息。

#### 主要影响和限制
- **运行时获取不到泛型类型信息**，即 `List<String>` 和 `List<Integer>` 在运行时其实是同一个类型。
- **不能直接创建泛型数组**，如 `new T[]` 或 `new List<String>[]` 会报错。
- **不能用 instanceof 判断具体泛型类型**，如 `obj instanceof List<String>` 是非法的。
- **静态变量不能用泛型类型参数**，因为类型参数属于实例，不属于类。

**通俗例子：**
```java
List<String> list1 = new ArrayList<>();
List<Integer> list2 = new ArrayList<>();
System.out.println(list1.getClass() == list2.getClass()); // true

// new T[]; // 编译报错
```

#### 应对办法
- 通过传递 Class<T> 参数或使用反射配合类型令牌（TypeToken）等方式规避部分泛型擦除带来的问题。

### 总结性提示词

> Java泛型编译后类型被擦除，运行时无泛型信息，限制了泛型数组、类型判断等操作，常需用Class参数或反射辅助。