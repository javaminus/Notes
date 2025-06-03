# 🔥 高频面试题：什么是CAS（Compare-And-Swap）？它在Java中的实现原理、优缺点以及应用场景是什么？

---

## 1️⃣ 概念详解

**CAS（Compare-And-Swap，比较并交换）** 是一种无锁（lock-free）并发技术，用于实现多线程环境下的原子操作。

- **原理**：操作时先比较内存中某个位置的值与预期值是否相等，只有相等时才将其更新为新值，否则什么都不做。
- **核心思想**：乐观并发控制，“先比对，再交换”，不阻塞其他线程。

---

## 2️⃣ Java中的实现

- Java中CAS底层依赖于**Unsafe类的native方法**，JDK的`AtomicInteger`、`AtomicReference`等原子类都是基于CAS实现的。
- 典型方法：`compareAndSet(expectedValue, newValue)`

```java
AtomicInteger counter = new AtomicInteger(0);
boolean success = counter.compareAndSet(0, 1); // 如果当前值是0，就更新为1
```
> 实质上，CAS会不断重试，直到某个线程成功为止。

---

## 3️⃣ CAS的优缺点

### 👍 优点
- 无需加锁，性能高，适合高并发。
- 不会导致线程阻塞或上下文切换。

### 👎 缺点
| 问题             | 说明                                               | 解决方法                               |
| ---------------- | -------------------------------------------------- | -------------------------------------- |
| ABA问题          | 某个值从A变为B，又变回A，CAS无法感知，可能导致误判 | 引入版本号（如AtomicStampedReference） |
| 自旋消耗CPU      | 多线程高并发时可能导致长时间自旋，浪费CPU资源      | 限制重试次数，或退避策略               |
| 只能保证一个变量 | 不能对多个变量同时进行原子操作（非组合原子性）     | 用锁或原子引用封装多个变量             |

---

## 4️⃣ 应用场景举例

- `java.util.concurrent` 包下的原子类（如 `AtomicInteger`、`AtomicBoolean`）
- 高并发计数器、乐观锁实现（如数据库行乐观锁的版本号）
- 非阻塞队列（如 ConcurrentLinkedQueue）

---

## 📝 总结性复习提示

- CAS 是无锁并发的核心，compare-and-swap原理+自旋重试机制。
- 优点：高性能，无阻塞。缺点：ABA、自旋、单变量。
- 面试常考：CAS原理、ABA问题、CAS与synchronized对比及适用场景。

---

> 💡 **口诀记忆**：CAS，先比较再交换，无锁高性能，ABA需防范！