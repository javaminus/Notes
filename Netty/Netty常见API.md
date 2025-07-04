# Netty 常用 API、核心类与方法总结

---

## 1. 常用 API 总览

| API/类名                    | 说明                                |
| --------------------------- | ----------------------------------- |
| EventLoopGroup              | 事件循环线程组，管理EventLoop       |
| NioEventLoopGroup           | 基于NIO的EventLoopGroup实现         |
| Channel                     | 网络连接抽象，所有数据操作的载体    |
| NioSocketChannel            | 客户端TCP通道实现                   |
| NioServerSocketChannel      | 服务端TCP通道实现                   |
| Bootstrap                   | 客户端启动辅助类                    |
| ServerBootstrap             | 服务端启动辅助类                    |
| ChannelHandler              | 业务处理器接口                      |
| ChannelInboundHandler       | 入站事件处理接口                    |
| ChannelOutboundHandler      | 出站事件处理接口                    |
| ChannelHandlerAdapter       | Handler适配器，便于只重写需要的方法 |
| ChannelPipeline             | Handler链（管道），事件传播通道     |
| ChannelFuture               | 异步操作结果对象                    |
| ChannelInitializer          | Channel初始化工具类                 |
| ChannelOption               | 通道参数设置                        |
| ByteBuf                     | Netty数据缓冲区                     |
| SimpleChannelInboundHandler | 简化的入站数据处理器                |
| AttributeKey                | 自定义属性绑定到Channel             |
| IdleStateHandler            | 空闲状态检测                        |

---

## 2. 关键类与接口

### EventLoopGroup
- 用于管理I/O线程，负责事件轮询、任务调度等。
- 常用：`NioEventLoopGroup`

### Channel
- 所有网络I/O操作的载体。
- 主要实现：`NioSocketChannel`、`NioServerSocketChannel`、`NioDatagramChannel`

### Bootstrap / ServerBootstrap
- 启动客户端/服务端的主要入口。
- `Bootstrap` 用于客户端，`ServerBootstrap` 用于服务端。

### ChannelHandler & 其 Adapter
- 用于处理I/O事件和数据。
- `ChannelInboundHandlerAdapter`、`ChannelOutboundHandlerAdapter`、`SimpleChannelInboundHandler`

### ChannelPipeline
- 事件处理链，包含多个ChannelHandler。

### ByteBuf
- Netty的数据缓冲区，比Java NIO的ByteBuffer更高效灵活。

---

## 3. 常用方法

### EventLoopGroup
- `shutdownGracefully()`：优雅关闭线程组

### Bootstrap/ServerBootstrap
- `group()`：设置EventLoopGroup
- `channel()`：设置通道类型
- `handler()` / `childHandler()`：设置初始化器
- `option()` / `childOption()`：设置通道参数
- `bind()`：绑定端口启动服务（服务端）
- `connect()`：连接服务器（客户端）

### ChannelPipeline
- `addLast(ChannelHandler...)`：添加Handler到管道末尾
- `remove(ChannelHandler)`：移除Handler

### ByteBuf
- `writeBytes(byte[])`：写数据
- `readBytes(byte[])`：读数据
- `release()`：回收缓冲区

### Channel
- `writeAndFlush(Object msg)`：写并发送消息
- `close()`：关闭通道

### ChannelFuture
- `addListener(GenericFutureListener)`：添加异步操作监听器
- `sync()`：阻塞直到操作完成

### ChannelHandlerContext
- `fireChannelRead(Object msg)`：向下传播消息
- `writeAndFlush(Object msg)`：发送消息

### IdleStateHandler
- 构造方法设置读/写/总空闲时间
- 触发userEventTriggered

---

## 4. 典型代码片段

### 服务端启动
```java
ServerBootstrap b = new ServerBootstrap();
b.group(bossGroup, workerGroup)
 .channel(NioServerSocketChannel.class)
 .childHandler(new ChannelInitializer<SocketChannel>() {
     @Override
     public void initChannel(SocketChannel ch) {
         ch.pipeline().addLast(new MyServerHandler());
     }
 });
b.bind(8080).sync();
```

### 客户端启动
```java
Bootstrap b = new Bootstrap();
b.group(group)
 .channel(NioSocketChannel.class)
 .handler(new ChannelInitializer<SocketChannel>() {
     @Override
     public void initChannel(SocketChannel ch) {
         ch.pipeline().addLast(new MyClientHandler());
     }
 });
b.connect("localhost", 8080).sync();
```

### Handler 示例
```java
public class MyHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 处理收到的数据
        ctx.writeAndFlush("response");
    }
}
```

---

## 5. 编解码相关

- `StringEncoder` / `StringDecoder`
- `LengthFieldBasedFrameDecoder`
- `DelimiterBasedFrameDecoder`
- 自定义：继承 `MessageToByteEncoder`、`ByteToMessageDecoder`

---

# Netty 线程模型方案说明

## 1. 线程池分组

- **BossGroup**：专门负责接收客户端连接请求。
- **WorkerGroup**：专门负责处理网络的读写操作。

## 2. NioEventLoop 介绍

- **NioEventLoop**：表示一个不断循环执行处理任务的线程。
  - 每个 NioEventLoop 都有一个 **Selector**，用于监听绑定在其上的 socket 网络通道。
  - 内部采用串行化设计：从消息的读取 → 解码 → 处理 → 编码 → 发送，始终由 IO 线程 NioEventLoop 负责。
- **NioEventLoopGroup**：下包含多个 NioEventLoop。

## 3. NioEventLoop 细节

- 每个 NioEventLoop 包含：
  - 一个 **Selector**
  - 一个 **taskQueue**
- 每个 NioEventLoop 的 Selector 上可以注册监听多个 NioChannel。
- 每个 NioChannel 只会绑定在唯一的 NioEventLoop 上。
- 每个 NioChannel 都绑定有一个自己的 **ChannelPipeline**。

## 4. 结构关系图示（文本版）

```
NioEventLoopGroup
    ├── NioEventLoop1
    │     ├── Selector1
    │     ├── taskQueue1
    │     └── NioChannelA (有自己的 ChannelPipeline)
    ├── NioEventLoop2
    │     ├── Selector2
    │     ├── taskQueue2
    │     └── NioChannelB (有自己的 ChannelPipeline)
    └── ...
```

---

## 5. 总结要点

- BossGroup 负责连接接收，WorkerGroup 负责读写处理。
- NioEventLoop 是一个事件循环线程，管理 Selector 和任务队列。
- 每个 NioEventLoop 可以管理多个 NioChannel，但每个 NioChannel 只属于一个 NioEventLoop。
- 每个 NioChannel 绑定有独立的 ChannelPipeline，负责处理其所有 I/O 事件和数据流转。