## 2. 什么是Java中的自动装箱与拆箱（Autoboxing & Unboxing）？

### 详细解释

**自动装箱（Autoboxing）：**  
Java 会自动将基本类型（如 `int`, `double` 等）转换为对应的包装类型（如 `Integer`, `Double`）。  
**自动拆箱（Unboxing）：**  
Java 会自动将包装类型转换为对应的基本类型。

**常见场景：**
- 将基本类型放入集合（如 `List<Integer>`），编译器自动装箱。
- 对包装类型做运算时，编译器自动拆箱。

**例子：**
```java
List<Integer> list = new ArrayList<>();
list.add(5); // int自动装箱为Integer
int a = list.get(0); // Integer自动拆箱为int

Integer b = 100;
int c = b + 1; // b自动拆箱为int，c结果为101
```
注意：自动装箱/拆箱可能引发性能问题和空指针异常（如对null包装类型拆箱）。

### 总结性提示词

> 自动装箱/拆箱：基本类型与包装类型自动转换，常见于集合和运算，注意性能和空指针风险。