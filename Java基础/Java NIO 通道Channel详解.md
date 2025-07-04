# Java NIO 通道（Channel）详解

## 1. Channel 与传统 IO 流的对比

- **流（Stream）**
  - 只能单向操作（只能读或只能写）。
  - 阻塞式操作（BIO），读取或写入时线程会阻塞。
  - 直接操作数据源（如文件、网络等）。

- **通道（Channel）**
  - 可双向操作（同时支持读写）。
  - 支持异步、非阻塞操作（NIO）。
  - 通过缓冲区（Buffer）读写数据。
  - 面向块（Block）而非字节（Stream 面向字节）。

## 2. Channel 的主要特性

- 通道可以同时进行读和写，而流只能单向（只能读或只能写）。
- 通道支持异步（非阻塞）读写数据，提高了并发性能。
- 通道通过缓冲区（Buffer）来实现数据的读写，数据总是先写入缓冲区，再通过通道输出，或者先从通道读入缓冲区，再由程序处理。

## 3. Channel 结构与接口

- Channel 是一个接口：
  ```java
  public interface Channel extends Closeable {}
  ```
- 主要常用实现类有：
  - `FileChannel`：用于文件的数据读写。
  - `DatagramChannel`：用于 UDP 数据读写。
  - `ServerSocketChannel`：用于服务端的 TCP 连接监听。
  - `SocketChannel`：用于客户端的 TCP 数据读写。

| 类名                | 用途说明                    | 类似于 BIO 中的类                |
| ------------------- | --------------------------- | -------------------------------- |
| FileChannel         | 文件的读写                  | FileInputStream/FileOutputStream |
| DatagramChannel     | UDP 协议数据的读写          | DatagramSocket                   |
| ServerSocketChannel | TCP 服务端监听连接          | ServerSocket                     |
| SocketChannel       | TCP 客户端/已连接套接字读写 | Socket                           |

## 4. FileChannel 常用方法

FileChannel 主要用于对本地文件进行高效的 IO 操作，常用方法如下：

- `public int read(ByteBuffer dst)`  
  从通道读取数据到缓冲区（dst），返回读取的字节数。

- `public int write(ByteBuffer src)`  
  把缓冲区（src）中的数据写入到通道，返回写入的字节数。

- `public long transferFrom(ReadableByteChannel src, long position, long count)`  
  从目标通道（src）复制数据到当前 FileChannel 的指定位置，适合文件间高效传输。

- `public long transferTo(long position, long count, WritableByteChannel target)`  
  将当前 FileChannel 的数据从指定位置复制到目标通道（target），常用于零拷贝优化。

### FileChannel 使用示例

```java
// 读取文件内容到缓冲区
try (FileChannel fileChannel = new FileInputStream("input.txt").getChannel()) {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    int bytesRead = fileChannel.read(buffer);
    // 处理 buffer 中的数据
}

// 写数据到文件
try (FileChannel fileChannel = new FileOutputStream("output.txt").getChannel()) {
    ByteBuffer buffer = ByteBuffer.wrap("Hello NIO".getBytes());
    fileChannel.write(buffer);
}

// 零拷贝文件传输
try (
    FileChannel sourceChannel = new FileInputStream("source.txt").getChannel();
    FileChannel targetChannel = new FileOutputStream("target.txt").getChannel()
) {
    sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
}
```

---

## 5. 小结

- NIO 的 Channel 设计使得数据读写更加高效和灵活，适用于高并发、高性能场景。
- 通过结合 Buffer 及 Selector，可以极大提高 IO 的吞吐量和可扩展性。
- `FileChannel` 支持文件的直接传输（transferTo/transferFrom），可实现零拷贝优化。

---