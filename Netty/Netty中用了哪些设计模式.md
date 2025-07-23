# 🏗️ Netty 中用到的设计模式

Netty 作为知名的高性能网络通信框架，代码中大量使用了经典的设计模式，极大提升了框架的可扩展性、灵活性和可维护性。常见的设计模式有：

---

## 1. 🟦 单例模式（Singleton Pattern）
- **应用**：如 `DefaultEventExecutorChooserFactory.INSTANCE`、各种策略实例、部分异常类等，保证全局唯一实例。
- **优点**：节省资源、全局唯一、易于管理。

---

## 2. 🏭 工厂模式（Factory Pattern）
- **应用**：如各种 `*Factory`、`ChannelFactory`、`ByteBufAllocator` 等，用于创建不同的对象实例。
- **优点**：解耦对象创建与使用，便于扩展和替换。

---

## 3. 🪢 责任链模式（Chain of Responsibility Pattern）
- **应用**：Netty 的核心 `ChannelPipeline`，所有的 `ChannelHandler` 链式处理请求。
- **优点**：请求可以在处理链上传递，灵活组合、扩展处理逻辑。

---

## 4. 👀 观察者模式（Observer Pattern）
- **应用**：如 `ChannelFuture` 的回调监听机制（addListener），异步事件通知。
- **优点**：事件驱动，松耦合，便于异步编程。

---

## 5. 🧩 策略模式（Strategy Pattern）
- **应用**：如 `EventExecutorChooser`、`SelectStrategy`，动态选择不同的任务分配或选择策略。
- **优点**：运行时灵活切换算法或策略，提高代码灵活性。

---

## 6. 🎁 装饰者模式（Decorator Pattern）
- **应用**：如 `WrappedByteBuf`、`SslHandler` 等，通过包装原有对象增强其功能。
- **优点**：无需修改对象结构即可增强功能，符合开闭原则。

---

## 7. 🧰 其他设计模式
- **适配器模式（Adapter Pattern）**：Netty 的多种协议适配和转换。
- **模板方法模式（Template Method Pattern）**：如 `ChannelHandlerAdapter`，定义算法骨架，具体实现由子类完成。

---

## 📝 总结

Netty 通过巧妙运用多种设计模式，实现了高性能、高扩展、高可维护的网络通信框架。理解这些设计模式的应用，对深入学习 Netty 源码和高质量系统设计大有裨益。

> **设计模式是 Netty 优雅架构、高性能实现的基石！**