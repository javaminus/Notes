Spring 默认支持**部分类型的循环依赖**，具体如下：

---

## 1. Spring默认支持什么循环依赖？

- **单例作用域（singleton）Bean**：Spring 默认支持构造器之外的循环依赖（即通过 setter 或字段注入）。
- **多例作用域（prototype）Bean**：Spring 默认**不支持**循环依赖，创建时如果有循环依赖会直接抛出异常。
- **构造器循环依赖**：即两个 Bean 互相在构造方法中依赖对方，这种情况 Spring **不支持**，会抛出 BeanCurrentlyInCreation 异常。

---

## 2. 原理简述

- Spring 在实例化 Bean 时，会将“正在创建”的 Bean 放入一个缓存（earlySingletonObjects）。
- 如果另一个 Bean 依赖它，Spring 会提前暴露一个“未完全初始化”的引用（即三级缓存），用于 setter 注入等场景。
- 这样可以解决大部分 setter 或字段注入导致的循环依赖问题。

---

## 3. 发生循环依赖如何解决？

- **建议优先使用 setter 或字段注入**，而非构造器注入。
- 如果是构造器注入的循环依赖，建议重构代码，打破依赖环。
- 可以通过拆分接口、事件监听、ApplicationContextAware 或延迟注入（如 `@Lazy`）等方式解决复杂循环依赖。
- 如果必须使用 prototype Bean，建议手动管理依赖，避免循环引用。

---

## 4. 面试简洁回答

> Spring 默认支持单例 Bean 的循环依赖，主要通过三级缓存提前暴露 Bean 对象，实现 setter 或字段注入的依赖环。但如果是构造器注入或 prototype 作用域，Spring 不支持循环依赖，会抛出异常。实际开发中建议使用 setter 注入并合理设计 Bean 之间的关系，避免循环依赖带来维护难题。

如需源码分析或更详细场景举例可继续提问！