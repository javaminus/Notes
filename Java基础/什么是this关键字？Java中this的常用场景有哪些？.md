## 9. 什么是this关键字？Java中this的常用场景有哪些？

### 详细解释

- **this** 是 Java 中的一个引用变量，表示当前对象的引用（即方法或构造方法正在操作的那个对象）。
- 主要用于区分成员变量和局部变量同名的情况，也可用于在构造方法中调用其他构造方法。

**常用场景：**
1. **区分成员变量和参数同名：**
   ```java
   class Person {
       String name;
       Person(String name) {
           this.name = name; // this.name表示成员变量，name表示参数
       }
   }
   ```
2. **在构造方法中调用本类的其他构造方法：**
   ```java
   class Person {
       String name;
       int age;
       Person(String name) { this(name, 0); }
       Person(String name, int age) {
           this.name = name;
           this.age = age;
       }
   }
   ```
3. **在方法中返回当前对象：**
   ```java
   class Person {
       Person getSelf() { return this; }
   }
   ```

### 总结性提示词

> this代表当前对象，常用于区分同名变量、调用本类其他构造及返回自身实例。