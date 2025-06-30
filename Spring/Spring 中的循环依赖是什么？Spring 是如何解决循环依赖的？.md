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

# Spring 循环依赖（Circular Dependency）面试追问及参考答案

---

## 1. 哪些情况下 Spring 能解决循环依赖？哪些情况下不能？

**答：**  
- Spring 只支持解决单例（singleton scope）Bean 的**setter 注入/属性注入**类型的循环依赖。
- 对于 prototype 作用域的 Bean、**构造器注入**（constructor injection）、多级代理/FactoryBean、@DependsOn 等复杂场景，Spring 无法自动解决，启动时会抛出异常。

---

## 2. Spring 三级缓存的每一级分别起什么作用？

**答：**
- singletonObjects（一缓存）：存储完全初始化好的单例 Bean。
- earlySingletonObjects（二缓存）：存储已实例化但未完成依赖注入的“半成品” Bean，可提前暴露给其它 Bean 使用。
- singletonFactories（三缓存）：存储 ObjectFactory，可创建早期 Bean 引用（如 AOP 代理），并在需要时转移到 earlySingletonObjects。

---

## 3. 为什么构造器注入无法解决循环依赖？

**答：**  
- 构造器注入要求依赖 Bean 在实例化时就必须全部准备好，不能提前暴露“半成品”对象，导致无法打破依赖闭环，最终创建失败。

---

## 4. 三级缓存机制如何支持 AOP 代理？

**答：**  
- 三级缓存中的 ObjectFactory 可以在实例化早期就生成 Bean 的代理对象，从而提前将代理暴露给依赖者，保证 AOP 功能和循环依赖兼容。

---

## 5. 如果我的 Bean 都是 prototype 作用域，Spring 还能解决循环依赖吗？

**答：**  
- 不能。prototype Bean 每次请求新实例，生命周期无法被 Spring 管控，无法提前暴露半成品引用，Spring 会抛出异常。

---

## 6. @Autowired/@Resource 注入和循环依赖有什么关系？

**答：**  
- 这类注解属于 setter/属性注入，Spring 支持自动解决单例 Bean 的循环依赖。
- 如果用 @Autowired 构造器注入，则属于构造方法注入，无法解决循环依赖。

---

## 7. 如果出现循环依赖，Spring 是如何检测并处理的？

**答：**  
- Spring 在创建 Bean 时记录正在创建的 Bean 名称，若发现依赖链路中 Bean 已在创建中，则尝试提前暴露引用。若无法解决则抛出 BeanCurrentlyInCreationException 等异常。

---

## 8. 如何手动解除循环依赖？

**答：**  
- 可通过将依赖拆分（如事件发布/监听）、懒加载（@Lazy）、抽象接口、重构业务逻辑等手段打破闭环。

---

## 9. Spring Boot 环境下循环依赖默认是允许的吗？

**答：**  
- Spring Boot 2.6+ 默认禁用循环依赖（spring.main.allow-circular-references=false），如需兼容需显式开启。

---

## 10. Spring 解决循环依赖会不会引发其他问题？

**答：**  
- 如果提前暴露的 Bean 被修改或未完成初始化，可能出现“空字段”或“属性未赋值”问题。建议尽量避免循环依赖，减少耦合风险。

---

## 面试总结提示

- 熟悉三级缓存原理和适用范围（单例、setter注入）
- 知道构造器注入、prototype Bean、@DependsOn 等场景的限制
- 能举例说明循环依赖的业务场景及手动规避方法

---