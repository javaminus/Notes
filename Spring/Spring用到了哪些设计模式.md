Spring 框架广泛应用了多种经典设计模式，常见的有以下几种：

---

### 1. **工厂模式（Factory Pattern）**
- Spring IoC 容器本身就是一个超级工厂（BeanFactory、ApplicationContext），用来创建和管理各种 Bean 实例。

### 2. **单例模式（Singleton Pattern）**
- Spring 默认的 Bean 是单例的（Singleton Scope），整个容器中只有一个实例。

### 3. **代理模式（Proxy Pattern）**
- Spring AOP（面向切面编程）大量使用动态代理（JDK 动态代理、CGLIB 字节码生成代理）。
- 用于事务管理、权限控制、日志等横切逻辑。

### 4. **模板方法模式（Template Method Pattern）**
- Spring 中的 JdbcTemplate、HibernateTemplate 等“模板”类，把通用流程写在父类，具体细节由子类或回调实现。

### 5. **观察者模式（Observer Pattern）**
- Spring 的事件机制（ApplicationEventPublisher 和 ApplicationListener），Bean 可以监听和响应容器中的事件。

### 6. **策略模式（Strategy Pattern）**
- Spring 中的 ResourceLoader、BeanFactoryPostProcessor、HandlerMapping 等，允许根据不同策略选择不同实现。

### 7. **适配器模式（Adapter Pattern）**
- Spring MVC 的 HandlerAdapter、Spring 的各种适配器类（如 MethodAdapter）用于兼容不同类型的处理器或接口。

### 8. **装饰者模式（Decorator Pattern）**
- BeanWrapper、PropertyEditor 等在属性赋值过程中为 Bean 添加功能，AOP 也有装饰器思想。

### 9. **责任链模式（Chain of Responsibility Pattern）**
- Spring Security、Spring MVC 的拦截器（HandlerInterceptor），Filter、BeanPostProcessor链式调用。

### 10. **建造者模式（Builder Pattern）**
- 用于创建复杂对象，比如 BeanDefinitionBuilder、UriComponentsBuilder 等。

---

**总结**：Spring 是一个大量运用面向对象设计模式的优秀框架，常见有工厂、单例、代理、模板方法、观察者、策略、适配器、装饰者、责任链和建造者等设计模式。

如需某个模式在Spring中的具体源码实现和用法，可以随时继续提问！