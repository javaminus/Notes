# Redis保存库存时，如何避免被Redis清理掉？

在高并发场景，Redis常用来做库存扣减。但如何避免库存数据被Redis自动清理（淘汰）呢？关键在于**正确设置Redis的内存淘汰策略和Key属性**。

---

## 1️⃣ 了解Redis的内存淘汰策略

Redis有多种淘汰策略，通过`maxmemory-policy`参数配置：

- **volatile-xxx**：只对“有过期时间”的key进行淘汰
- **allkeys-xxx**：所有key都可能被淘汰
- **noeviction**：内存满后不再淘汰，直接返回错误（OOM）

常见策略如：
- `volatile-lru`：最近最少使用的、设置了过期时间的key被淘汰
- `allkeys-lru`：最近最少使用的key都可能被淘汰
- `volatile-ttl`：快过期的key被优先淘汰
- `allkeys-random`：随机淘汰任意key

---

## 2️⃣ 库存业务推荐设置

### **A. 不设置过期时间（expire）**

- 库存key不要设置过期时间，这样在`volatile-xxx`策略下不会被淘汰
- 只设置业务相关的缓存key（如商品详情、活动信息）过期，库存key始终常驻

### **B. 选择合适的淘汰策略**

- 推荐用`volatile-lru`等volatile策略，**库存key无过期时间不会被淘汰**
- 如果用`allkeys-xxx`，库存key也可能被清理，风险较大，仅适用于纯缓存场景

### **C. 业务兜底方案**

- Redis只是缓存，库存真实数据仍需在数据库持久化
- 当Redis库存key丢失时，自动从数据库同步/重建库存到Redis，避免业务影响
- 关键库存数据可引入双写机制（写数据库+写Redis）

---

## 3️⃣ 生产配置建议

- **maxmemory-policy: volatile-lru**
- **库存key绝不设置过期时间**
- **监控Redis内存和key状态，防止意外丢失**
- **极端情况下可用noeviction策略，但需评估OOM风险**

---

> ⚡ 总结：**库存key不要设置过期时间，并使用volatile-lru等淘汰策略，既能保证Redis内存可控，又避免库存数据被淘汰，确保库存准确和高可用。**