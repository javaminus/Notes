> ## 循环依赖案例展示：
>
> 下面通过一个简单的代码示例，说明Spring中**Bean的循环依赖**问题。
>
> ---
>
> ## 1. 什么是循环依赖？
>
> **循环依赖**指的是：A依赖B，B又依赖A，导致Spring在创建Bean时出现依赖注入的死循环。
>
> ---
>
> ## 2. 示例代码
>
> 假设我们有两个类：`A` 和 `B`，它们互相依赖：
>
> ```java
> @Component
> public class A {
>     @Autowired
>     private B b;
> }
> 
> @Component
> public class B {
>     @Autowired
>     private A a;
> }
> ```
>
> 或者使用构造器注入时：
>
> ```java
> @Component
> public class A {
>     private final B b;
>     @Autowired
>     public A(B b) {
>         this.b = b;
>     }
> }
> 
> @Component
> public class B {
>     private final A a;
>     @Autowired
>     public B(A a) {
>         this.a = a;
>     }
> }
> ```
>
> ---
>
> ## 3. 结果分析
>
> - **属性注入（setter、字段注入）**：Spring 默认可以解决单例Bean的循环依赖。因为Spring会先实例化bean（无参构造），然后填充属性。
> - **构造器注入**：Spring无法解决循环依赖，会报`BeanCurrentlyInCreationException`异常，因为A和B都要等对方构造完才能继续。
>
> ---
>
> ## 4. 结论
>
> - **循环依赖**会导致Spring容器在注入Bean时出错，尤其是构造器注入时。
> - 推荐避免设计上出现循环依赖，如拆分服务、引入中间层等。
>

Spring 通过 **三级缓存** 解决循环依赖问题，核心思想是 **提前暴露对象的引用**，允许依赖对象在完整初始化前先获取到一个早期的对象引用。具体来说，Spring 的三级缓存包括：

1. **singletonObjects（一缓存）**：  
   - 存放已经完全初始化的单例 Bean，直接可用。
  
2. **earlySingletonObjects（二缓存）**：  
   - 存放**提前暴露**的 Bean 实例（半成品），用于解决循环依赖。
  
3. **singletonFactories（三缓存）**：  
   - 存放**ObjectFactory**，用于创建 Bean 的早期引用，主要用于 AOP 代理对象的生成。

### **三级缓存的核心流程**

1. **创建 Bean 实例，但未初始化**，将其实例工厂（`ObjectFactory`）放入 **三级缓存** (`singletonFactories`)。
2. **检测依赖**，如果遇到循环依赖，先尝试从**一级缓存**获取完整 Bean，否则从**二级缓存**获取早期实例。
3. **如果二级缓存未命中**，则从**三级缓存**中获取 `ObjectFactory`，调用 `getObject()` 生成 Bean 的早期引用，并存入**二级缓存**，然后删除三级缓存中的 `ObjectFactory`。
4. **最终 Bean 初始化完成**，存入**一级缓存**，并从**二、三级缓存中移除**。

### **关键作用**
- **防止 AOP 代理丢失**：AOP 代理对象需要在 Bean 完成后增强，但如果直接暴露原始对象，代理可能无法生效。因此，Spring 让 `ObjectFactory` 提供代理后的对象。
- **降低空间占用**：只有在确实遇到循环依赖时才会使用二级缓存，从而减少不必要的实例存储。

### **局限性**
- **仅支持“构造方法外”的循环依赖**（即字段、Setter 注入）。
- **`@Scope("prototype")` 无法使用**，因为原型 Bean 不会放入单例池。

总结来说，**Spring 三级缓存机制通过提前暴露 Bean 的引用，使得循环依赖得以解决，同时保证 AOP 代理不丢失**。



# Spring 三级缓存解决循环依赖机制 面试追问及参考答案

---

## 1. 为什么需要三级缓存？两级缓存不可以吗？

**答：**  
两级缓存只能支持简单的循环依赖（直接暴露原始对象），但无法支持 AOP 代理等场景。三级缓存中的 ObjectFactory 能动态创建 Bean 的早期引用（可以是代理对象），这样即使依赖对象被代理，也不会丢失代理增强，保证功能完整。  
简言之，三级缓存是为了解决“需要被代理的 Bean”在循环依赖下还能正常被增强。

---

## 2. Spring 的三级缓存如何保证 AOP 代理不会丢失？

**答：**  
当 Bean 需要被 AOP 增强时，Spring 会在 ObjectFactory 的 getObject() 方法里生成代理对象，而不是直接暴露原始对象。这样即使在循环依赖时暴露出去的是代理对象，依赖方获得的也是增强后的 Bean，从而保证代理不丢失。

---

## 3. prototype 作用域的 Bean 为什么不能解决循环依赖？

**答：**  
prototype Bean 每次获取都会新建实例，Spring 只负责创建，不会进行完整生命周期管理，也不会将其实例加入单例池（缓存），因此无法通过提前暴露引用来解决依赖闭环。一旦遇到循环依赖会直接抛异常。

---

## 4. 构造器注入能否解决循环依赖？Spring 为什么不支持？

**答：**  
不能。构造器注入时 Bean 必须在构造阶段就完成所有依赖注入，无法提前暴露半成品引用，无法打破依赖闭环。Spring 只支持 setter/属性注入的循环依赖解决。

---

## 5. Spring 是如何检测并处理循环依赖的？

**答：**  
Spring 会维护一个“正在创建中的 Bean”集合（singletonsCurrentlyInCreation）。如果发现正在创建的 Bean 又依赖自己，会尝试从二级/三级缓存获取早期引用，否则抛出异常。

---

## 6. Spring 三级缓存的核心数据结构是什么？

**答：**  
- singletonObjects（一级缓存）：Map<String, Object>  
- earlySingletonObjects（二级缓存）：Map<String, Object>  
- singletonFactories（三级缓存）：Map<String, ObjectFactory<?>>

---

## 7. 三级缓存会不会引发并发安全问题？

**答：**  
Spring 通过 synchronized、ConcurrentHashMap 等保证缓存操作的线程安全，防止并发条件下 Bean 状态错乱。

---

## 8. 如果一个 Bean 同时被多个线程依赖，三级缓存如何管理？

**答：**  
缓存操作都是线程安全的，且同一个 Bean 只会被实例化一次，所有线程获取到的都是同一个对象引用。

---

## 9. 三级缓存机制对 Bean 的生命周期有哪些影响？

**答：**  
提前暴露引用可能导致依赖方拿到的 Bean 还没完全初始化（如属性为 null），开发时要避免在构造阶段调用未初始化完成的 Bean 方法。

---

## 10. 如何规避循环依赖带来的设计风险？

**答：**  
- 避免设计上产生 Bean 间强耦合
- 通过接口、事件、懒加载（@Lazy）、重构业务逻辑等方式打破依赖闭环
- 多用组合少用互相依赖

---

## 面试总结提示

- 三级缓存：保证循环依赖下 Bean 可用且不丢失 AOP 代理
- 仅支持 setter/属性注入和 singleton 范围
- prototype、构造器注入、复杂依赖链无法解决
- 熟悉缓存结构和流程图，有助于深入理解 Spring 容器底层原理

---