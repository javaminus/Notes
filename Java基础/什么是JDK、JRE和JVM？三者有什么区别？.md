## 1. 什么是JDK、JRE和JVM？三者有什么区别？

### 详细解释

- **JDK（Java Development Kit）**：Java开发工具包，包含JRE和开发者用的编译器（javac）、调试工具等。是Java开发者必备的环境。
- **JRE（Java Runtime Environment）**：Java运行时环境，包含JVM和Java类库，只能运行Java程序，不能编译。
- **JVM（Java Virtual Machine）**：Java虚拟机，负责加载字节码文件并将其翻译为特定平台的机器指令，是Java实现跨平台的核心。

**关系：JDK > JRE > JVM**

JDK包含JRE，JRE包含JVM。

### 总结性提示词

> JDK用于开发，JRE用于运行，JVM用于跨平台。JDK包含JRE，JRE包含JVM。