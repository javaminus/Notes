# 🧩 Netty 的线程模型详解

Netty 采用了基于 **Reactor 模型** 的多路复用（NIO）技术来高效处理海量并发网络请求。根据实际业务场景和启动参数，Netty 支持三种主流线程模型：

---

## 1️⃣ 单Reactor单线程模型

- **原理**：一个线程（Reactor）负责所有事件（连接、读写）处理和分发。
- **特点**：模型简单，适用于连接数少、业务处理快的场景。
- **缺点**：单线程处理所有请求，无法充分利用多核 CPU，在高并发/重业务场景下容易成为瓶颈。

![img](assets/15100432-8d55f7719d1ad6e3) 

**流程图：**
```
[客户端1] \
[客户端2]  >---[Reactor线程A]---[Handler]
[客户端3] /
```

---

## 2️⃣ 单Reactor多线程模型

- **原理**：一个 Reactor 线程负责事件监听和分发，业务处理交由工作线程池（Worker Pool）完成。
- **优点**：可以并发处理业务逻辑，提升吞吐量。
- **缺点**：Reactor 线程既要负责连接，也要负责 IO 操作；若 IO 处理阻塞，连接请求可能被延迟或拒绝。

![img](assets/15100432-a66dfaf29be6a116) 

**流程图：**
```
[客户端]---[Reactor线程]---[Handler线程池]
```

---

## 3️⃣ 主从Reactor多线程模型（Netty 默认）

- **原理**：主 Reactor 只负责处理连接请求，子 Reactor（WorkerGroup）负责处理读写事件和业务逻辑。
- **优点**：充分利用多核 CPU，连接和读写分离，支持高并发、高负载场景，扩展性强。
- **在 Netty 中**，通常 BossGroup 负责 accept 连接，WorkerGroup 负责读写和处理业务。

![img](assets/15100432-1fdba5d7554cc9cc) 

**流程图：**
```
[客户端]---[主Reactor(BossGroup)]---[子Reactor(WorkerGroup)]---[Handler线程池]
```

---

## 🛠️ Netty 的主从多线程实现

```java
// 创建 BossGroup（主Reactor）和 WorkerGroup（子Reactor）
EventLoopGroup bossGroup = new NioEventLoopGroup(1);
EventLoopGroup workerGroup = new NioEventLoopGroup();

ServerBootstrap b = new ServerBootstrap();
b.group(bossGroup, workerGroup)
 .channel(NioServerSocketChannel.class)
 .childHandler(new ChannelInitializer<SocketChannel>() {
     @Override
     protected void initChannel(SocketChannel ch) throws Exception {
         ch.pipeline().addLast(new YourHandler());
     }
 });
```
- bossGroup 只管理连接请求
- workerGroup 负责数据读写与业务处理

---

## 🚩 总结

- **单Reactor单线程**：适合低负载、简单场景
- **单Reactor多线程**：业务处理可并发，IO仍有瓶颈
- **主从Reactor多线程（生产推荐）**：高并发、高性能、扩展性强

> Netty 通过灵活的线程模型配置，能够高效应对各种网络通信场景，充分发挥多核服务器的性能优势。