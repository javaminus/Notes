1、**实例化**：Spring 创建 Bean 实例。

2、**依赖注入**：将 Bean 的依赖注入（通过构造函数、Setter 或字段注入）。

3、实现 Aware 接口

- `BeanNameAware`: 传递 Bean 的名称。
- `BeanFactoryAware`: 传递 `BeanFactory`。

4、**BeanPostProcessor 的前置处理**：在初始化之前进行处理。

5、初始化回调

- `InitializingBean` 的 `afterPropertiesSet()`。
- `@PostConstruct` 注解的方法。

6、**BeanPostProcessor 的后置处理**：初始化后进行处理。

7、**使用**：此时 Bean 已经可以被应用。

8、销毁回调

- `DisposableBean` 的 `destroy()`。
- `@PreDestroy` 注解的方法。

# Spring Bean 生命周期面试常见追问及参考答案

---

## 1. 实例化/依赖注入

- **Q：Spring 是如何创建 Bean 实例的？有哪些方式？**
  - **A**：通过反射机制创建 Bean 实例，常见有无参构造方法（默认）、有参构造方法（构造器注入）、工厂方法等。
- **Q：依赖注入有哪几种？**
  - **A**：构造器注入、Setter 方法注入、字段（@Autowired/@Resource）注入。

---

## 2. 实现 Aware 接口

- **Q：BeanNameAware、BeanFactoryAware 有什么用？**
  - **A**：BeanNameAware 可获取容器分配的 Bean 名称，BeanFactoryAware 可获得当前 BeanFactory 实例，便于获取其他 Bean 或资源。
- **Q：实际开发中什么时候会用到这些 Aware 接口？**
  - **A**：如需在 Bean 内部获取容器自身资源或进行编程式 Bean 管理时使用，如访问 ApplicationContext、动态获取其他 Bean。

---

## 3. BeanPostProcessor

- **Q：BeanPostProcessor 主要做什么？常见应用有哪些？**
  - **A**：允许在 Bean 初始化前后进行自定义处理。常见用法有：自动代理（AOP）、自定义注解处理、属性修改、监控等。
- **Q：如何自定义一个 BeanPostProcessor？**
  - **A**：实现 BeanPostProcessor 接口并注册到容器中。可重写 postProcessBeforeInitialization 和 postProcessAfterInitialization 方法。

---

## 4. 初始化回调

- **Q：@PostConstruct 和 InitializingBean 的区别？实际开发中推荐用哪个？**
  - **A**：@PostConstruct 是 JSR-250 标准注解，推荐使用，兼容性好；InitializingBean 是 Spring 接口，紧耦合于 Spring。一般推荐用 @PostConstruct。
- **Q：如果两者都定义了，执行顺序如何？**
  - **A**：@PostConstruct 方法先执行，afterPropertiesSet() 后执行。

---

## 5. 销毁回调

- **Q：@PreDestroy 和 DisposableBean 的区别？**
  - **A**：@PreDestroy 是标准注解，推荐使用，兼容性好；DisposableBean 是 Spring 专用接口。推荐用 @PreDestroy。
- **Q：Bean 是什么时候被销毁的？**
  - **A**：单例 Bean 在容器关闭时销毁，原型 Bean 不会自动销毁（需手动）。

---

## 6. Bean 生命周期顺序

- **Q：Bean 生命周期各阶段的执行顺序？**
  - **A**：实例化 → 依赖注入 → Aware 接口 → BeanPostProcessor 前置 → 初始化（@PostConstruct/afterPropertiesSet）→ BeanPostProcessor 后置 → 使用 → 销毁（@PreDestroy/destroy）。

---

## 7. 其他常见追问

- **Q：如何管理原型作用域（prototype）的 Bean 销毁？**
  - **A**：原型 Bean 由容器创建，但不负责销毁，需开发者手动调用销毁方法。
- **Q：Spring 容器是如何实现 Bean 生命周期管理的？**
  - **A**：通过 BeanDefinition、BeanFactory、ApplicationContext 以及生命周期回调接口和注解实现全流程管理。

---

## 面试总结小结

- 回答时关注“创建-注入-初始化-使用-销毁”主流程，说明回调接口与注解的区别与推荐用法。
- 结合实际项目举例说明如 AOP、动态代理、资源释放等场景。
- 熟悉各阶段顺序、扩展点（如 BeanPostProcessor/Aware 接口）的实现与作用。

---