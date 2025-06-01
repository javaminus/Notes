## 10. 什么是Java中的反射？反射的常见用途是什么？

### 详细解释

**反射（Reflection）** 是 Java 提供的一种强大机制，可以在运行时动态地获取类的信息、构造对象、访问属性和方法。通过反射，程序可以不通过编译时类型检查，动态操作任意对象和类，非常灵活。

#### 常见用途
- **框架/容器**：如Spring、Hibernate等框架通过反射自动注入依赖、管理Bean生命周期。
- **通用工具库**：如序列化、对象拷贝、通用toString、equals等。
- **JDBC**：数据库驱动和ORM框架根据类名动态加载数据库驱动。
- **插件机制**：运行时加载和调用插件类。

#### 通俗例子
```java
// 获取类对象
Class<?> clazz = Class.forName("java.lang.String");

// 创建对象
Object str = clazz.getConstructor(String.class).newInstance("hello");

// 调用方法
Method method = clazz.getMethod("length");
int len = (int) method.invoke(str); // 5
```
可见，反射让你无需提前知道类的具体类型和方法名，就能实现灵活的运行时操作。

### 总结性提示词

> 反射：运行时获取类信息、动态创建对象和调用方法，常用于框架、工具库、JDBC、插件机制等场景。

