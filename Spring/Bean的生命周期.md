**实例化**：Spring 创建 Bean 实例。

**依赖注入**：将 Bean 的依赖注入（通过构造函数、Setter 或字段注入）。

实现 Aware 接口

- `BeanNameAware`: 传递 Bean 的名称。
- `BeanFactoryAware`: 传递 `BeanFactory`。

**BeanPostProcessor 的前置处理**：在初始化之前进行处理。

初始化回调

- `InitializingBean` 的 `afterPropertiesSet()`。
- `@PostConstruct` 注解的方法。

**BeanPostProcessor 的后置处理**：初始化后进行处理。

**使用**：此时 Bean 已经可以被应用。

销毁回调

- `DisposableBean` 的 `destroy()`。
- `@PreDestroy` 注解的方法。