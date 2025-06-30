### 问题

**Spring 中的 Bean 作用域（Scope）有哪些？它们的应用场景是什么？**

---

#### 详细解释

Spring Bean 的作用域决定了 Spring 容器在创建和管理 Bean 时，Bean 实例的生命周期和可见范围。Spring 支持多种作用域，适应不同应用场景：

1. **singleton**（单例，默认作用域）
   - 整个 Spring 容器只创建一个 Bean 实例，所有请求共享同一个对象。
   - 适用场景：无状态、线程安全的 Bean（如服务、DAO 等）。

2. **prototype**（原型）
   - 每次获取 Bean 时都会创建新的实例。
   - 适用场景：有状态、需要独立实例的 Bean（如表单数据对象、线程相关对象）。

3. **request**（仅 Web 环境）
   - 每个 HTTP 请求创建一个 Bean，仅在当前请求内有效。
   - 适用场景：存储与单次请求相关的临时数据。

4. **session**（仅 Web 环境）
   - 每个 HTTP Session 创建一个 Bean，整个会话期间保持唯一。
   - 适用场景：与用户会话相关的 Bean，如购物车对象。

5. **application**（仅 Web 环境）
   - 每个 ServletContext 创建一个 Bean，整个 Web 应用共享。
   - 适用场景：全局共享的 Bean。

6. **websocket**（仅 Web 环境）
   - 每个 WebSocket 会话创建一个 Bean。
   - 适用场景：WebSocket 连接期间需要保持状态的 Bean。

**用法示例：**

```java
@Component
@Scope("prototype")
public class OrderForm {
    // 每次获取都会创建新实例
}
```

**常用场景举例：**
- 订单处理服务用 `singleton`
- Web 表单数据对象用 `prototype`
- 用户购物车用 `session`
- 临时请求参数用 `request`

---

#### 总结性回答（复习提示词）

> Spring Bean 作用域：singleton（单例，默认），prototype（多例），request/session/application/websocket（Web 环境）。常用 singleton，原型适合有状态对象，Web 场景用 request、session。

# Spring Bean 作用域 面试常见追问及参考答案

---

## 1. singleton 和 prototype 有什么区别？线程安全吗？

**答：**
- singleton 是整个 Spring 容器中只创建一个实例，所有请求共享，默认作用域，适合无状态、线程安全的 Bean。
- prototype 每次获取都是新实例，适合有状态 Bean（如用户表单），Spring 只负责创建，后续生命周期由用户管理。
- singleton Bean **不是线程安全的**，如果内部有状态要自己保证线程安全；prototype Bean 每次新建，通常线程安全压力小。

---

## 2. Web 环境下 request、session、application 作用域 Bean 如何管理生命周期？

**答：**
- request 作用域 Bean 随 HTTP 请求创建和销毁。
- session 作用域 Bean 随 HTTP Session 创建和销毁。
- application 作用域 Bean 随 ServletContext 创建和销毁。
- 这些作用域依赖 Web 容器，不能在普通非 Web 应用中使用。

---

## 3. @Scope 注解怎么用？可以用在方法/字段/类上吗？

**答：**
- @Scope 只能用在类级别（@Component、@Service 等 Bean 上），不能用在方法或字段上。
- 也可以在 @Bean 方法上声明作用域，如 `@Bean @Scope("prototype")`。

---

## 4. prototype 作用域的 Bean 生命周期有什么不同？

**答：**
- Spring 容器只负责实例化和依赖注入，不会管理 prototype Bean 的完整生命周期（如销毁）。
- 需要用户自己管理销毁（如显式调用 destroy 方法）。

---

## 5. 如果 singleton Bean 依赖 prototype Bean，会发生什么？

**答：**
- 默认情况下，prototype Bean 只会在 singleton Bean 初始化时注入一次，后续都是同一个实例，不会每次新建。
- 如需每次都获取新实例，需结合 `ObjectFactory`、`Provider` 或 `@Lookup` 注解动态获取。

---

## 6. WebSocket 作用域有什么特点？

**答：**
- websocket 作用域用于每个 WebSocket 会话创建独立 Bean，适合需要在 WebSocket 连接期间保存状态的场景。
- 仅在 Spring WebSocket 支持下有效。

---

## 7. 作用域能自定义吗？如何扩展？

**答：**
- Spring 支持自定义作用域，实现 org.springframework.beans.factory.config.Scope 接口，并注册到容器中。

---

## 8. Spring Boot 下如何使用 request/session 作用域 Bean？

**答：**
- 需在 Web 环境下，Bean 上加 @Scope("request") 或 @Scope("session")；
- 控制器或服务可通过注入或 `ObjectFactory<>` 获取。

---

## 9. @Scope 和 @Profile 有什么区别？

**答：**
- @Scope 控制 Bean 的生命周期和可见性；
- @Profile 控制 Bean 是否根据环境（开发、测试、生产等）激活。

---

## 10. 如何在非 Web 环境下使用 request/session/application 作用域？

**答：**
- 不支持。request/session/application/websocket 作用域仅限于 Servlet/WebSocket 环境，普通 Java 应用无法使用。

---

## 面试总结提示

- 熟悉 singleton/prototype 和 Web 作用域的区别与典型场景；
- 理解 prototype 生命周期和依赖注入陷阱；
- 会举例说明如何动态获取 prototype Bean 实例。

---

