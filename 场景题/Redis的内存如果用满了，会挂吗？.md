# Redis 的内存如果用满了，会挂吗？🧠💥

答案是：**一般不会立刻挂掉，但服务会受到影响**。下面详细说明👇

---

## 1️⃣ 内存淘汰策略（maxmemory-policy）🔄

Redis 支持多种内存淘汰策略，当内存用满时，会根据配置的策略决定如何处理：

- **noeviction**：不淘汰任何 key，内存满后所有写操作会被拒绝，返回错误（不会崩溃）。
- **allkeys-lru**：从所有 key 中按最近最少使用（LRU）策略淘汰。
- **volatile-lru**：从设置了过期时间的 key 中按 LRU 淘汰。
- **allkeys-random**：从所有 key 中随机淘汰。
- **volatile-random**：从设置了过期时间的 key 中随机淘汰。
- **volatile-ttl**：淘汰快要过期的 key。
- **allkeys-lfu** / **volatile-lfu**：淘汰访问频率最低的 key。

---

## 2️⃣ 内存满了会发生什么？❓

- Redis **不会因为内存满直接挂掉或崩溃**。
- 如果配置了淘汰策略，Redis 会按照策略清理数据，腾出空间。
- 如果是 noeviction 策略，则**写操作会被拒绝**，返回 `OOM command not allowed when used memory > 'maxmemory'` 错误，但 Redis 服务本身不会挂。

---

## 3️⃣ 需要注意什么？⚠️

- 长期 OOM 或频繁淘汰会影响服务性能，甚至影响整个业务体验。
- 合理配置内存和淘汰策略，监控内存使用，避免频繁触发 OOM。
- 生产环境建议启用 LRU/LFU 等淘汰策略，防止写满后业务写入异常。

---

## ✅ 总结

> Redis 内存用满不会立刻挂掉，
>
> - 会根据淘汰策略清理旧数据，
> - 或者拒绝写入，报错但不崩溃。
>
> 推荐合理配置内存和淘汰策略，保障服务稳定性！🛡️