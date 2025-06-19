在 Java 中，**BIO、NIO 和 AIO** 是三种不同的 I/O 模型，它们适用于不同的应用场景。

------

## **1. BIO（Blocking I/O）——同步阻塞 I/O**

### **特点**

- 传统的 **“一请求一线程”** 模型，每个请求都会占用一个独立线程。
- 线程被 **阻塞**，直到 I/O 操作（如 `read()` 或 `write()`）完成。
- 适用于 **小规模并发**，但在高并发情况下会导致**线程资源浪费**。

### **示例**

```java
ServerSocket serverSocket = new ServerSocket(8080);
while (true) {
    Socket socket = serverSocket.accept(); // 阻塞等待连接
    new Thread(() -> handle(socket)).start(); // 每个连接创建一个线程
}
```

### **缺点**

- 线程开销大，连接数多时**系统资源消耗严重**。
- 适用于**小并发**应用，如传统 Web 服务器。

------

## **2. NIO（Non-blocking I/O）——同步非阻塞 I/O**

### **特点**

- **基于多路复用（Selector）**，一个线程可同时管理多个连接，提高效率。
- 采用 **非阻塞模式**，即使没有数据可读，`read()` 也不会阻塞，而是立即返回 `0`。
- 适用于 **高并发服务器**，如 Netty 及 Tomcat 采用的 Reactor 模型。

### **关键组件**

- **Channel（通道）**：类似 `Socket`，但支持**非阻塞**。
- **Buffer（缓冲区）**：数据读写依赖 `Buffer` 而非 `Stream`。
- **Selector（选择器）**：监听多个 `Channel` 事件，实现**多路复用**。

### **示例**

```java
Selector selector = Selector.open();
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.bind(new InetSocketAddress(8080));
serverChannel.configureBlocking(false);
serverChannel.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
    selector.select(); // 阻塞等待事件发生
    Set<SelectionKey> keys = selector.selectedKeys();
    for (SelectionKey key : keys) {
        if (key.isAcceptable()) { /* 处理连接事件 */ }
        else if (key.isReadable()) { /* 处理读事件 */ }
    }
}
```

### **优点**

- **减少线程创建**，一个线程管理多个连接，节省资源。
- **适用于高并发**（如 10w+ 连接）。

### **缺点**

- **API 复杂**，开发难度较大。
- **数据读写仍然是同步的**，业务处理仍然在主线程执行。

------

## **3. AIO（Asynchronous I/O）——异步非阻塞 I/O**

### **特点**

- **真正的异步 I/O**，基于 **操作系统的异步事件通知机制**（如 Linux `epoll`）。
- **无需轮询 Selector**，使用 **回调函数** 处理事件，减少 CPU 资源浪费。
- 适用于 **超高并发、长连接场景**（如 IM、聊天、推送）。

### **关键组件**

- **AsynchronousChannel**（异步通道）
- **CompletionHandler**（完成回调处理）

### **示例**

```java
AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()
    .bind(new InetSocketAddress(8080));

server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
    @Override
    public void completed(AsynchronousSocketChannel channel, Object attachment) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                attachment.flip();
                System.out.println(new String(attachment.array()));
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {}
        });
    }

    @Override
    public void failed(Throwable exc, Object attachment) {}
});
```

### **优点**

- **完全异步**，提高系统吞吐量，适用于**长连接、大量 I/O 任务**。
- 适用于 **高并发业务场景**，如 **聊天应用、推送系统**。

### **缺点**

- **仅支持 Linux、Windows（从 JDK 7 开始支持）**。
- **编程复杂度较高**，依赖回调机制，可能导致 **回调地狱**。

------

## **4. BIO、NIO 和 AIO 对比**

| 方式    | 阻塞性         | 线程模型     | 适用场景         | 适用技术               |
| ------- | -------------- | ------------ | ---------------- | ---------------------- |
| **BIO** | **同步阻塞**   | 一线程一连接 | 小并发，低吞吐量 | 传统 `Socket`          |
| **NIO** | **同步非阻塞** | 多路复用     | 高并发           | `Selector` + `Channel` |
| **AIO** | **异步非阻塞** | 事件驱动回调 | 超高并发、长连接 | `AsynchronousChannel`  |

### **应用场景**

- **BIO** 适用于 **小并发应用**（如简单 Web 服务）。
- **NIO** 适用于 **高并发服务器**（如 Netty、Tomcat）。
- **AIO** 适用于 **超高并发、长连接**（如消息推送、IM）。

------

## **总结**

- **BIO（同步阻塞）**：传统 I/O 模式，适用于 **小规模连接**。
- **NIO（同步非阻塞）**：通过 `Selector` 实现 **多路复用**，适用于 **高并发**。
- **AIO（异步非阻塞）**：基于 **回调机制**，适用于 **超高并发、长连接**。

在现代开发中，**NIO（Netty）** 已成为主流，而 **AIO** 适用于超高并发场景。