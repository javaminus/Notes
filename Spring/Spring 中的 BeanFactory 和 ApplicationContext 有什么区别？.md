### 问题

**Spring 中的 BeanFactory 和 ApplicationContext 有什么区别？**

---

#### 详细解释

在 Spring 框架中，BeanFactory 和 ApplicationContext 都是 IoC 容器的实现，但它们在实际开发中的用途和功能存在明显差别。

- **BeanFactory**  
  是 Spring 最基本的 IoC 容器，主要负责实例化、配置和管理 Bean。它采用延迟加载（懒加载）的方式，只有在需要时才创建 Bean 实例，适合资源受限的场合或底层框架开发。

- **ApplicationContext**  
  是 BeanFactory 的子接口，除了具备 BeanFactory 的全部能力之外，还提供了更多企业级功能，比如：
  - 国际化（MessageSource）
  - 事件发布（ApplicationEventPublisher）
  - 支持 AOP
  - 自动载入配置文件
  - 可以加载多个配置文件（如 XML、注解、Java 配置类）

**常见的 ApplicationContext 实现类有：**
- ClassPathXmlApplicationContext
- FileSystemXmlApplicationContext
- AnnotationConfigApplicationContext

**场景对比：**  
- BeanFactory 适合内存敏感、轻量级场景（如 IoC 容器底层实现或资源紧张的移动端）。
- ApplicationContext 适合绝大多数企业级应用开发，几乎是实际开发的默认选择。

---

#### 通俗例子

假设你在工厂工作：
- **BeanFactory** 就像一个仓库管理员，只在你来要某个零件时才临时为你组装出来。
- **ApplicationContext** 就像一个自动化工厂，提前把所有常用零件都准备好，还能支持工厂广播通知、国际化标签、工厂级事件等高级服务。

---

#### 总结性回答（复习提示词）

> ApplicationContext = BeanFactory + 企业级特性（如国际化、事件等），开发中优先用 ApplicationContext，BeanFactory 适合底层或特殊场景。