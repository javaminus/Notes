## 问题3：JVM有哪些常见的类加载器？它们的加载顺序和作用是什么？

Java 虚拟机启动和运行过程中，会使用多个内置加载器来定位、加载和验证类。理解它们的层次和职责，有助于解决类冲突、热部署、隔离加载等问题。

### 1. 启动类加载器（Bootstrap ClassLoader）
- **职责**：加载 JVM 核心类库（`<JAVA_HOME>/jre/lib/rt.jar`、`resources.jar` 等）。  
- **实现**：用 C++/本地代码实现，不是 Java 对象。  
- **特性**：最顶层，没有父加载器。

### 2. 扩展类加载器（Extension ClassLoader）
- **职责**：加载 JRE 扩展目录下的类（`<JAVA_HOME>/jre/lib/ext/*.jar`）或由 `-Djava.ext.dirs` 指定的目录。  
- **实现**：Java 类 `sun.misc.Launcher$ExtClassLoader`。  
- **父加载器**：Bootstrap。

### 3. 应用类加载器（Application／System ClassLoader）
- **职责**：加载用户类路径（`-classpath` 或 `-Djava.class.path`）下的应用程序类。  
- **实现**：Java 类 `sun.misc.Launcher$AppClassLoader`。  
- **父加载器**：Extension。

### 4. 自定义类加载器（User-Defined ClassLoader）
- **职责**：根据特定需求（如插件隔离、热部署、加解密、网络加载）加载字节码。  
- **实现**：继承 `java.lang.ClassLoader`，重写 `findClass()` 或 `loadClass()`。  
- **父加载器**：通常指定为应用类加载器或更上层加载器，也可完全隔离。

### 5. 加载顺序与双亲委派
- **双亲委派模型**  
  每个类加载请求先委托给父加载器，父加载器无法加载时才由当前加载器尝试加载。  
  启动→扩展→应用→自定义  
- **好处**  
  - 保证核心类由可信加载器加载，防止核心 API 被篡改  
  - 减少重复加载，类全限定名在 JVM 中唯一

### 6. 如何破坏委派／子加载优先
在某些场景（如 Tomcat 热部署、SPI 插件）需要父加载器无法加载时也不委托上层，可：  
- 重写 `loadClass()`，先调用 `findClass()` 再委派 `super.loadClass()`  
- 控制 `parent` 引用，或使用“反委派”策略

### 7. 典型面试追问
- 为什么要双亲委派？  
- 双亲委派的安全性和性能影响  
- 如何在容器中实现热部署／卸载？  

### 小结/提示词
- **三大内置加载器**：Bootstrap、Extension、Application  
- **双亲委派**：先父后子，防篡改  
- **自定义加载器**：插件隔离、热部署、加密加载  
- 复习提示：**“启动扩展系统三层委派，自定义破委派灵活拓展”**  