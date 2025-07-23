# 扫表任务如何写SQL避免跳页问题？

在定时任务批量扫描表数据（如状态为 INIT 的任务）时，常规分页（OFFSET/LIMIT）方式会导致**跳页**问题：  
- 因为每处理一批数据就会更新其状态（如从 INIT → SUCCESS），下次分页时数据总数发生变化，OFFSET 就会错位，导致部分数据被遗漏（跳页）。

---

## 跳页的错误分页SQL

```sql
-- 第一页
SELECT * FROM table WHERE state = 'INIT' ORDER BY id LIMIT 0, 100;

-- 第二页
SELECT * FROM table WHERE state = 'INIT' ORDER BY id LIMIT 100, 100;
```
- 如果第一页的某些数据被处理后状态变更，第二页的 OFFSET 就会跳过未处理的数据。

---

## 正确写法：基于游标（ID）方式分页

**核心思想**：每次记录上一批的最大ID，下次从该ID之后继续查，保证数据不重复不遗漏。  
适合自增ID或有唯一递增字段的场景。

### 示例SQL

```sql
-- 第一次查询（假设last_max_id=0）
SELECT * FROM table 
WHERE state = 'INIT' AND id > 0 
ORDER BY id ASC 
LIMIT 100;

-- 假设查到的最大ID为 12345，下一次查询
SELECT * FROM table 
WHERE state = 'INIT' AND id > 12345 
ORDER BY id ASC 
LIMIT 100;
```
- 每次处理前一批数据后，记录本批次最大ID。
- 下次以 `id > last_max_id` 为条件查新数据。

---

## 总结

- **不用 OFFSET/LIMIT**，避免因数据变动导致的“跳页”或“漏查”。
- 用**游标分页**（如自增ID、时间戳等唯一递增字段）+ LIMIT，保证数据不重不漏、处理高效。

> 💡 **一句话总结：扫表任务分页时用“游标分页”而不是OFFSET/LIMIT，条件形如 `id > last_max_id order by id limit N`，可彻底避免跳页问题。**