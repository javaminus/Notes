## 12. Java中的访问修饰符有哪些？分别有什么作用？

### 详细解释

Java 提供了四种访问修饰符，用于控制类、变量、方法等成员的访问范围：

1. **private**：仅在本类内部可访问，最严格的访问权限，外部类和子类都不能访问。
2. **default（包访问权限）**：不写修饰符，只有同一个包中的类可以访问。
3. **protected**：同包内可访问，子类（即使在不同包）也可以访问。
4. **public**：对所有类都可见，访问权限最宽松。

**总结表格：**

| 修饰符    | 同类 | 同包 | 子类 | 其他包 |
| --------- | :--: | :--: | :--: | :----: |
| private   |  √   |      |      |        |
| default   |  √   |  √   |      |        |
| protected |  √   |  √   |  √   |        |
| public    |  √   |  √   |  √   |   √    |

**通俗例子：**
```java
public class Demo {
    private int a;
    int b; // default
    protected int c;
    public int d;
}
```

### 总结性提示词

> 四种访问修饰符：private最严格，public最开放，default包内可见，protected包及子类可见。