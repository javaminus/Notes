# 可重复读与读已提交隔离级别下MVCC机制的核心区别

## 引言
在数据库事务隔离级别中，Repeatable Read(RR)和Read Committed(读已提交)是两种常用的隔离级别，它们都依赖MVCC(多版本并发控制)机制来实现并发控制。理解它们的差异对数据库设计和性能优化至关重要。

## 核心机制对比

### 1. ReadView创建时机
- **RR隔离级别**：事务开始时创建**唯一ReadView**，整个事务期间复用该视图
- **读已提交隔离级别**：**每次SELECT语句**执行时创建新的ReadView

### 2. 数据可见性规则
- **RR**：事务看到的是**第一次SELECT时的数据快照**，保证多次读取结果一致
- **读已提交**：每次查询看到**最新已提交的数据**，可能出现不可重复读

### 3. 幻读处理方式
- **RR**：通过MVCC+**间隙锁(Gap Lock)**组合避免幻读
- **读已提交**：**不防止幻读**，可能出现同一事务内查询结果集变化

## 实现细节差异

### 版本链管理
- **可重复读**：需要维护**整个事务期间**所有相关undo log，undo空间占用较高
- **读已提交**：每条SELECT语句创建独立快照，语句结束后可释放旧版本，undo空间占用更低

### 性能影响
- **RR**：长期事务可能导致undo表空间膨胀，需定期回收
- **读已提交**：资源回收更快，高并发场景下undo表空间增长更平缓

## 典型场景示例

```sql
-- RR隔离级别示例
START TRANSACTION;
-- 第一次SELECT(创建ReadView1,看到数据版本V1)
SELECT * FROM users WHERE id=1;
-- 事务B此时更新并提交
UPDATE users SET name='B' WHERE id=1; COMMIT;
-- 事务A第二次SELECT(复用ReadView1,仍看到V1)
SELECT * FROM users WHERE id=1; -- 结果不变!
COMMIT;

-- 读已提交隔离级别示例
START TRANSACTION;
-- 第一次SELECT(创建ReadView1,看到数据版本V1)
SELECT * FROM users WHERE id=1;
-- 事务B此时更新并提交
UPDATE users SET name='B' WHERE id=1; COMMIT;
-- 事务A第二次SELECT(创建新的ReadView2,看到已提交的V2)
SELECT * FROM users WHERE id=1; -- 结果变化!
COMMIT;
```

## 总结

RR和读已提交隔离级别在MVCC实现上的核心差异在于**ReadView的创建时机和生命周期**。RR通过单一ReadView保证事务内读取一致性，而读已提交通过动态ReadView提供更高的并发性但牺牲了一致性保证。选择哪种隔离级别应根据应用对数据一致性和并发性能的具体需求来决定。
