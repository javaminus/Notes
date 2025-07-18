在 MySQL 中，可以使用 `INSERT ... ON DUPLICATE KEY UPDATE` 语法实现**如果不存在就插入，如果存在就更新**的效果（即“upsert”操作）。

## 语法示例

假设你的表结构如下：

```sql
CREATE TABLE user (
  id INT PRIMARY KEY,
  name VARCHAR(100)
);
```

### 插入或更新语句

```sql
INSERT INTO user (id, name)
VALUES (1, 'Alice')
ON DUPLICATE KEY UPDATE name = VALUES(name);
```

- 如果 `id=1` 不存在，则插入一条新记录。
- 如果 `id=1` 已存在，则把 `name` 字段更新为 `'Alice'`。

---

## 适用条件

- 表必须有唯一索引（如主键或 UNIQUE KEY），否则不会触发“更新”操作。

---

## 多字段写法

```sql
INSERT INTO user (id, name)
VALUES (2, 'Bob')
ON DUPLICATE KEY UPDATE
  name = VALUES(name);
```

---

## 其它场景

- 如果你只想插入不存在的数据（忽略已存在的），可以用 `INSERT IGNORE`。
- 如果你用的是 MySQL 8.0+，也可以用 `REPLACE INTO`，但它会先删除再插入，触发 DELETE/INSERT，而不是 UPDATE。

---

## 参考

- [MySQL官方文档：INSERT ... ON DUPLICATE KEY UPDATE](https://dev.mysql.com/doc/refman/8.0/en/insert-on-duplicate.html)

如需复杂条件、批量插入或其他用法可以继续追问！