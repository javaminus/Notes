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

