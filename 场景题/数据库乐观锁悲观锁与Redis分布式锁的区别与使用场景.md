# 数据库乐观锁、悲观锁与 Redis 分布式锁的区别与使用场景 🔒🚦

锁机制用于管理多个请求对同一数据的并发操作，保证数据一致性。常见有三类：**乐观锁**、**悲观锁**、**分布式锁（如 Redis 实现）**。下面从定义、区别和使用场景角度总结：  

---

## 1️⃣ 悲观锁（Pessimistic Lock）

- **核心思想**：假设并发冲突总会发生，操作前必须先加锁，防止其他人修改。
- **常见实现**：数据库层面的 `SELECT ... FOR UPDATE`。
- **使用场景**：
  - 🔄 数据竞争激烈、冲突频繁
  - ✏️ 更新/删除操作频繁、写多于读的业务

---

## 2️⃣ 乐观锁（Optimistic Lock）

- **核心思想**：假设并发冲突很少，操作时不加锁，最终提交时通过**版本号**或**时间戳**校验数据是否被修改。
- **常见实现**：数据表加 `version` 字段，每次更新时校验 `version` 是否等于读取时的值。
- **使用场景**：
  - 📖 读多写少、冲突概率低
  - 🏃‍♂️ 追求高性能、减少锁开销的业务

---

## 3️⃣ Redis 分布式锁

- **核心思想**：用于**分布式系统**中多个进程/服务竞争资源时加锁，保证跨系统的操作顺序和一致性。
- **常见实现**：基于 Redis 的 `SETNX`、`EXPIRE` 或 Redisson、Lua 脚本等。
- **使用场景**：
  - 🌐 多个应用/服务需要共同访问共享资源
  - 🕸️ 微服务、分布式部署下的并发控制
  - 🚦 跨节点的业务流程一致性保障

---

## 🔍 乐观锁 vs. 悲观锁

| 对比项     | 乐观锁               | 悲观锁                        |
| ---------- | -------------------- | ----------------------------- |
| 🛠️ 实现方式 | 版本号/时间戳/CAS    | 数据库锁机制（如 for update） |
| 🔄 并发场景 | 读多写少、冲突概率低 | 写多于读、冲突概率高          |
| 💡 操作时机 | 先业务操作，后校验   | 先加锁，后业务操作            |
| ⏳ 性能开销 | 锁开销小，重试成本高 | 锁开销大，吞吐量受限          |

---

## 🎯 Redis 分布式锁 VS 数据库悲观锁

- 单体应用直接用数据库悲观锁即可；
- 分布式场景建议用 Redis 分布式锁，原因如下：
  - 🚀 Redis 性能更好、响应更快
  - 🔁 支持更多高级特性（如可重入、锁续期等）
  - 🗃️ 数据库锁资源宝贵，锁表影响大
  - 💰 Redis 抗并发更经济，数据库主要用于业务相关操作

---

## 💡 总结建议

- 单体项目/数据库并发控制：优先用数据库悲观锁
- 分布式/跨服务并发控制：优先选用 Redis 分布式锁
- 读多写少场景可用乐观锁，写多冲突多场景优先悲观锁或分布式锁

---

> **一句话总结：**  
> 乐观锁、悲观锁适合单体数据库并发控制，Redis 分布式锁适合分布式环境保护共享资源，根据业务并发场景合理选择锁机制。