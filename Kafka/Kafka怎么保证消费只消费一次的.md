Kafka 本身默认语义是**“至少消费一次”**（at-least-once），不是“仅消费一次”（exactly-once）。不过，Kafka 通过一系列机制可以实现**“精准一次”消费**（**Exactly Once**），具体方式如下：

---

## 1. Kafka 原生支持的 Exactly Once 语义（EOS）

从 Kafka 0.11 开始，Kafka 引入了事务机制和幂等（idempotence）特性，配合使用可以实现“精准一次”语义：

### 1.1 幂等生产者（Idempotent Producer）
- 开启 `enable.idempotence=true` 后，生产者可以保证即使发生重试，消息不会被重复写入分区。
- Kafka 通过 Producer ID 和 Sequence Number 实现去重。

### 1.2 事务消息（Transactional Producer）
- 生产者可使用事务 API，将一组消息作为一个原子操作写入多个分区和主题。
- 未提交的事务消息不会被消费者读取，保证“要么都写入、要么都不写入”。

### 1.3 消费者端控制 offset 提交
- **Kafka offset 是由消费者维护的**，消费者通常在成功处理完消息后，才提交 offset。
- 如果消费失败，不提交 offset，下次还能重新消费，避免丢失。
- 要实现“仅消费一次”，必须**确保消息处理与 offset 提交的原子性**（即处理成功才提交 offset，且不会因为系统故障重复提交）。

### 1.4 Kafka Streams / Kafka Connect
- Kafka Streams 和 Connect 原生支持 Exactly Once 语义（EOS），自动管理事务和 offset。

---

## 2. 典型消费端 Exactly Once 实现方式

### 2.1 结合业务去重
- 消费端处理时，将每条消息的唯一标识（如业务ID）做幂等操作或唯一性约束。
- 即使重复消费，也不会影响业务结果。

### 2.2 事务消费+offset提交
- 处理消息和 offset 提交要放在同一个事务中（比如消费消息写入数据库后再提交 offset）。
- 如果两者不能原子完成，可能会出现重复消费或消息丢失。

### 2.3 外部存储 offset
- 有些场景下，消费端会将 offset 与业务数据一起存储到数据库，保证处理和 offset一致性。

---

## 3. Kafka Exactly Once 语义适用条件

- 生产端需开启幂等和事务机制。
- Broker 和消费者需升级到支持 EOS 的版本（≥0.11）。
- 处理逻辑需具备幂等性或原子提交 offset 与业务结果。

---

## 总结

Kafka 能通过**幂等生产者**、**事务API**和**原子性 offset 提交**，配合业务幂等机制，实现“仅消费一次”效果，但实际落地一般还需结合业务系统的设计。  
- **生产端：开启幂等和事务。**
- **消费端：确保处理与 offset 提交原子性或业务幂等。**
- 原生 API（Kafka Streams/Connect）可自动支持 EOS。

如果需要具体代码实现或配置建议，可以继续提问！