下面详细介绍**MySQL 乐观锁和悲观锁**的使用方式和场景。

---

## 一、悲观锁（Pessimistic Lock）

**悲观锁**假设数据会被频繁修改，因此在操作数据时会先加锁，防止其他事务修改。

### 1. 行级锁（`SELECT ... FOR UPDATE`）

- 适用于InnoDB、事务下使用。
- 示例：在事务中锁定一行，其他事务对该行写操作会被阻塞。

```sql
START TRANSACTION;
SELECT * FROM user WHERE id = 1 FOR UPDATE;
-- 对数据进行修改
UPDATE user SET balance = balance - 100 WHERE id = 1;
COMMIT;
```
- 上面语句会对id=1这一行加排他锁，直到事务提交或回滚。

### 2. 其它悲观锁方式

- 鉴于MySQL表锁/行锁，通常用在高并发场景下，保证数据一致性。
- 表锁：`LOCK TABLES tablename WRITE;` 适用于MyISAM，但InnoDB推荐行锁。

---

## 二、乐观锁（Optimistic Lock）

**乐观锁**假设数据不会被并发修改，所以不加锁，只在更新时做版本校验。

### 1. 版本号字段法（version）

- 通常在表中增加一个 `version` 字段，每次更新时判断版本号是否变化。

**建表示例：**
```sql
CREATE TABLE product (
  id INT PRIMARY KEY,
  stock INT,
  version INT
);
```

**更新业务逻辑：**
```sql
-- 先查当前版本号和库存
SELECT stock, version FROM product WHERE id = 1;

-- 假设查到 version = 2
-- 更新时带上版本号
UPDATE product SET stock = stock - 1, version = version + 1
WHERE id = 1 AND version = 2;
```
- 如果返回行数为0，说明版本号已被其他事务修改，更新失败（需重试）。

### 2. 时间戳法

- 用 `update_time` 字段充当乐观锁，每次更新时判断时间是否变化。

```sql
SELECT stock, update_time FROM product WHERE id = 1; // 先获取对应id的更新时间

UPDATE product SET stock = stock - 1, update_time = NOW()
WHERE id = 1 AND update_time = '2025-07-21 00:00:00';
```
- 如果有其他事务更新过该行，`update_time` 已变化，当前操作会失败。

---

## 三、应用场景对比

- **悲观锁**：并发极高，且对数据一致性要求极高时使用。适合库存扣减、银行账户等场景。
- **乐观锁**：并发不是特别高，冲突概率较低，且不希望数据库锁表/锁行影响性能。适合订单处理、后台业务审核等场景。

---

## 四、补充：悲观锁和乐观锁在Java/MyBatis中的应用

**悲观锁**：直接用 `SELECT ... FOR UPDATE` 语句。

**乐观锁**：MyBatis可用 `version` 字段配合 `WHERE` 条件实现，失败后重试。

---

## 总结

- **悲观锁**：`SELECT ... FOR UPDATE`，加锁防止并发写入。
- **乐观锁**：`version` 或 `timestamp` 字段，更新时校验并重试，性能更高但有冲突风险。

如还需具体Java代码示例，可继续追问！