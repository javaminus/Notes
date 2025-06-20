## 问题：Redis 的慢查询如何监控与分析？常见慢查询场景如何优化？

### 详细解释（结合场景，含通俗例子）

Redis 在高并发场景下，少数慢查询命令可能导致服务卡顿或阻塞，影响整体性能。为此，Redis 提供了 `SLOWLOG` 功能用于监控和分析慢查询。

#### 1. 慢查询监控与分析

- **SLOWLOG 机制**
  - Redis 自动记录执行时间超过阈值（默认为10000微秒=10ms）的命令。
  - 可通过命令 `SLOWLOG get N` 查询最近 N 条慢查询记录，包含执行时间、命令内容、时间戳等信息。
  - 配置参数：`slowlog-log-slower-than` 设置慢查询阈值，`slowlog-max-len` 设置记录最大条数。

- **分析常见慢查询场景**
  - **大 Key 操作**：如对超长 List、Hash、Set、ZSet 进行批量操作，会阻塞主线程。
  - **批量命令**：如 `KEYS`、`SMEMBERS`、`LRANGE`（大区间）等全量扫描命令。
  - **阻塞命令**：如 `BLPOP`，若业务处理不及时会造成阻塞。
  - **AOF 重写和 RDB 快照冲突**：大量写操作时碰到持久化操作，影响响应。

#### 2. 慢查询优化建议

- **避免大 Key**：合理拆分数据结构，限制单个 Key 内元素数量。
- **优化命令使用**：避免使用全量遍历命令，推荐用 `SCAN`、`SSCAN` 等渐进式遍历。
- **合理设置慢查询阈值**：根据业务实际情况调整 `slowlog-log-slower-than`。
- **监控与报警**：结合监控平台实时告警，及时发现并处理慢查询。
- **异步处理重任务**：如大数据量写入、清理，用分批、异步任务处理。

#### 通俗例子

就像餐厅后厨有监控摄像头（SLOWLOG），专门记录厨师做菜时间过长的情况（慢查询）。管理者定期查看监控，发现哪些菜耗时太久，就优化菜谱、调整流程，保证整体出餐效率。

---

### 总结性回答（复习提示）

> Redis 用 SLOWLOG 监控慢查询，重点关注大 Key 和全量操作，建议用 SCAN 代替 KEYS，定期分析慢查询日志，优化数据结构和命令使用，保障高性能。