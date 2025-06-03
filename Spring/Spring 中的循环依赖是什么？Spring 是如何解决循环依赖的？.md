### 问题

**Spring 中的循环依赖是什么？Spring 是如何解决循环依赖的？**

---

#### 详细解释

**循环依赖**指的是多个 Bean 之间存在依赖闭环：A 依赖 B，B 依赖 C，C 又依赖回 A（或 A、B 互相依赖）。如果没有特殊处理，容易导致 Bean 初始化时死锁或报错。

**Spring 如何解决循环依赖？**  
Spring 主要通过“三级缓存”机制解决单例 Bean 的构造方法（构造器注入除外）循环依赖问题：

1. **singletonObjects**：一级缓存，存放完全初始化好的单例 Bean。
2. **earlySingletonObjects**：二级缓存，存放“半成品” Bean（已实例化但未完成依赖注入），用于暴露给依赖其它 Bean 的地方提前引用。
3. **singletonFactories**：三级缓存，存放 Bean 工厂（ObjectFactory），可以暴露一个代理对象（比如 AOP 代理）。

**工作流程：**
- 当 Spring 创建 Bean A 时，发现需要依赖 Bean B。
- 此时 Spring 会先把 Bean A 的“引用”放进二级或三级缓存，然后去创建 Bean B。
- 如果 Bean B 又依赖 Bean A，就会从缓存中提前拿到 Bean A 的引用，避免死锁。
- 等所有依赖注入完成后，再将 Bean A 放入一级缓存，整个依赖链路完成。
- **注意：构造器注入的循环依赖无法解决，只能解决“setter 注入/属性注入”类型的循环依赖。**

**通俗场景举例：**

- 小明要做蛋糕（A），需要用到奶油（B）；奶油厂（B）又需要蛋糕模型（A）来定制奶油形状。此时，A 和 B 互相依赖。
- Spring 会先给奶油厂一个“蛋糕的半成品模型”（提前暴露引用），等双方都准备好后再合成完整蛋糕。

---

#### 总结性回答（复习提示词）

> Spring 循环依赖：A 依赖 B，B 又依赖 A。Spring 通过三级缓存（singletonObjects、earlySingletonObjects、singletonFactories）机制，提前暴露 Bean 引用，解决 setter/属性注入的循环依赖。构造器注入无法解决。