## 2. 解释Java的跨平台原理（“一次编写，到处运行”）是如何实现的？

### 详细解释

Java 的跨平台性是通过 **JVM（Java虚拟机）** 实现的。Java 程序编译后生成的是平台无关的字节码（.class 文件），而不是直接生成特定操作系统的机器码。不同平台上有不同的 JVM 实现，JVM 负责把字节码翻译成当前平台的机器码并运行。

- **“一次编写，到处运行”** 的含义是：同一个 Java 程序，不管在 Windows、Linux 还是 Mac，只要有对应平台的 JVM，都可以运行，无需修改源代码。
- JVM 屏蔽了底层操作系统和硬件差异。

### 总结性提示词

> Java 跨平台靠 JVM，不同平台有不同 JVM，只需编译一次字节码，就能在多种系统运行。