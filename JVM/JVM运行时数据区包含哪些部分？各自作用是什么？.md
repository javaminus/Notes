## 问题2：JVM运行时数据区包含哪些部分？各自作用是什么？

Java 虚拟机在运行期间，会划分多个不同的内存区域，每个区域负责不同类型的数据存储与管理。理解这些区域及其交互，是定位性能问题和故障排查的基础。

#### 1. 程序计数器（PC Register）
- **作用**：记录当前线程所执行的字节码指令地址。  
- **特点**：  
  - 每个线程独立，一个线程对应一个 PC 寄存器。  
  - 如果线程正在执行 Java 方法，PC 寄存器存储当前执行指令的地址；如果执行的是本地方法，则值为 undefined。

#### 2. Java 虚拟机栈（JVM Stack）
- **作用**：每个线程创建时分配，用于存储方法调用帧（Frame）。  
- **帧结构**：  
  - 局部变量表（Local Variables）  
  - 操作数栈（Operand Stack）  
  - 动态连接（指向运行时常量池中的符号引用解析结果）  
  - 方法返回地址  
- **异常**：  
  - 栈深度超限 → `StackOverflowError`  
  - 栈帧分配失败 → `OutOfMemoryError: unable to create new native thread`（极少见）

#### 3. 本地方法栈（Native Method Stack）
- **作用**：为 JVM 中调用的本地（Native）方法服务，结构和 JVM 栈类似。  
- **注意**：HotSpot 在调用 C/C++ 实现的本地方法时，会使用本地方法栈或直接使用操作系统线程栈。

#### 4. 堆（Heap）
- **作用**：JVM 最大的一块内存区域，所有对象实例和数组都在堆中分配。  
- **特点**：  
  - 分代管理：新生代（Young）、老年代（Old）、元空间（Metaspace／方法区）之外。  
  - 垃圾回收主要针对堆进行：新生代常做 Minor GC，老年代做 Full GC。  
- **异常**：  
  - `OutOfMemoryError: Java heap space`  

#### 5. 方法区（Method Area，又称元空间 Metaspace）
- **作用**：存储类的元信息（Class 元数据）、静态变量、常量池等。  
- **演进**：  
  - JDK 7 及以前称为永久代（PermGen），在堆中；  
  - JDK 8 开始改为本地内存的 Metaspace，不再受 `-Xmx` 限制，通过 `-XX:MaxMetaspaceSize` 控制。  
- **异常**：  
  - PermGen 溢出 → `OutOfMemoryError: PermGen space`  
  - Metaspace 溢出 → `OutOfMemoryError: Metaspace`

#### 6. 运行时常量池（Runtime Constant Pool）
- **作用**：方法区的一部分，承载编译期生成的各种字面量和符号引用。  
- **特点**：  
  - 方法区里动态生成，也可在运行时通过 `String.intern()` 等方式加入。

#### 7. 直接内存（Direct Memory）
- **作用**：不属于 JVM 管理的堆内存，由 NIO 等框架申请，直接向操作系统申请。  
- **配置**：`-XX:MaxDirectMemorySize`  
- **异常**：  
  - `OutOfMemoryError: Direct buffer memory`

### 各区交互示意

```
┌───────────────┐
│   程序计数器  │ ← 当前执行指令地址
└───────────────┘
        ▲
        │
┌───────────────┐    ┌───────────────┐
│   JVM 栈      │    │ 本地方法栈    │
│(方法调用帧)   │    │ (Native 调用) │
└───────────────┘    └───────────────┘
        ▲                    ▲
        └─────┐      ┌───────┘
              ▼      ▼
           ┌─────────────────────────┐
           │         堆              │ ← 对象实例、数组
           └─────────────────────────┘
                    ▲
                    │
           ┌────────┴─────────┐
           │    方法区/元空间  │ ← 类元信息、静态变量
           └──────────────────┘
                    ▲
                    │
           ┌────────┴─────────┐
           │  运行时常量池     │ ← 字面量、符号引用
           └──────────────────┘
```

### 小结/提示词

- **五大区域**：PC寄存器、JVM栈、本地方法栈、堆、方法区（元空间）  
- **常量池**：方法区的一部分，存字面量和符号引用  
- **直接内存**：堆外，由 NIO 等框架使用  
- **OOM类型**：StackOverflowError, Java heap space, PermGen／Metaspace, Direct buffer memory  
- 复习提示：**“PC懂指令；栈存帧；堆存对象；区分PermGen与Metaspace”**  

