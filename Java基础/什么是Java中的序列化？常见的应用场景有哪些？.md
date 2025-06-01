## 7. 什么是Java中的序列化？常见的应用场景有哪些？

### 详细解释

**序列化**（Serialization）是指将对象的状态信息转换为可存储或传输的格式（通常是字节流），以便后续可以恢复（反序列化）成原始对象。

在Java中，序列化通常通过实现 `java.io.Serializable` 接口实现。只需实现该接口，无需编写额外方法。可以通过 `ObjectOutputStream` 和 `ObjectInputStream` 进行序列化和反序列化。

**常见应用场景：**
- 网络传输：如通过Socket发送对象，实现远程通信（RPC）。
- 持久化存储：将对象存到文件或数据库中，后续恢复使用。
- 分布式系统：如Session共享、缓存、消息中间件等场景。

**通俗例子：**
```java
public class Person implements Serializable {
    private String name;
    private int age;
    // ...构造、getter、setter
}

// 序列化
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("person.obj"));
oos.writeObject(new Person("Tom", 20));

// 反序列化
ObjectInputStream ois = new ObjectInputStream(new FileInputStream("person.obj"));
Person p = (Person) ois.readObject();
```

### 总结性提示词

> 序列化：对象转字节流用于存储或传输，常用于网络通信、持久化、分布式系统。实现Serializable，配合ObjectOutputStream/ObjectInputStream使用。