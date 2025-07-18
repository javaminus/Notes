# MySQL中哪些命令会加上间隙锁（Gap Lock）？

## 1. 哪些命令会加间隙锁？

在**InnoDB存储引擎**下，满足以下条件的SQL语句会加间隙锁（Gap Lock）：

- **需要加锁的SQL语句**，如：
  - `SELECT ... FOR UPDATE`
  - `SELECT ... LOCK IN SHARE MODE`
  - `UPDATE ...`
  - `DELETE ...`
- **WHERE条件使用了索引列**（尤其是范围查询，如`>`、`<`、`BETWEEN`等）
- **当前事务隔离级别为“可重复读（REPEATABLE READ）”或以上**（在“读已提交”下，MySQL 5.7及以后版本只对外键约束和唯一索引冲突加Gap Lock）

### 常见示例

```sql
-- 会加间隙锁（Gap Lock）
SELECT * FROM user WHERE age > 20 FOR UPDATE;

UPDATE user SET name='Tom' WHERE age BETWEEN 30 AND 40;
DELETE FROM user WHERE age < 18;

-- 只锁定单行（主键/唯一索引等值查询），不会加间隙锁
SELECT * FROM user WHERE id = 10 FOR UPDATE;
```

---

## 2. 面试官可能追问的问题及答案

### 1. 间隙锁（Gap Lock）与行锁（Record Lock）有什么区别？

**答：**
- 行锁只锁住已存在的记录（数据行），防止其他事务修改或删除这些行。
- 间隙锁锁住的是索引区间的“空隙”，防止其他事务在该范围内**插入**新记录，但不影响已存在记录的更新。

---

### 2. 哪些情况下不会加间隙锁？

**答：**
- 主键或唯一索引的等值查询（如`WHERE id=10`）通常只加行锁，不加间隙锁。
- 在“读已提交（Read Committed）”隔离级别下，普通查询不会加间隙锁（但外键/唯一索引冲突时例外）。
- 没有用到索引时，InnoDB无法加间隙锁。

---

### 3. 为什么需要间隙锁？它的作用是什么？

**答：**
- 间隙锁的主要作用是防止“幻读”问题，保证事务隔离。
- 可以阻止其他事务在某个区间插入新行，确保同一事务多次范围查询结果一致。

---

### 4. 如何查看或验证间隙锁的存在？

**答：**
- 可以用两会话结合事务和范围查询+插入操作进行实验，观察是否出现阻塞。
- 或通过`SHOW ENGINE INNODB STATUS`命令查看当前锁信息。

---

### 5. 在哪些隔离级别下会加间隙锁？

**答：**
- 在“可重复读（REPEATABLE READ）”隔离级别下，InnoDB会主动加间隙锁以防止幻读。
- 在“读已提交（Read Committed）”下，只有外键约束或唯一索引冲突时才加间隙锁。

---

### 6. 间隙锁加得过多，会带来什么问题？

**答：**
- 会导致并发插入性能下降，甚至出现“插入阻塞”或死锁等问题。
- 设计索引和SQL时应合理避免不必要的大范围范围锁。

---

### 7. 如果表没有索引，间隙锁还能起作用吗？

**答：**
- 不能。没有索引时，InnoDB只能加表锁或行锁，无法加精确的间隙锁。

---

### 8. 间隙锁和Next-Key Lock的关系是什么？

**答：**
- Next-Key Lock是“行锁+间隙锁”的组合，既锁住已存在的行，也锁住相邻区间，彻底防止幻读。



## mysql的什么命令会加上间隙锁？答：在可重复读隔离级别下。 使用非唯一索引进行带where语句的查询、删除、更新 这样说对吗？

你的说法**基本正确，但需要补充和细化**，下面详细说明：

---

## 1. 间隙锁的加锁条件

- 在**可重复读（REPEATABLE READ）隔离级别下**，MySQL InnoDB才会主动使用间隙锁（Gap Lock），目的是防止幻读。
- 间隙锁通常出现在**范围查询**（如 `>`, `<`, `BETWEEN`，而不是等值查询）或**非唯一索引**的情况。
- 常见SQL操作：`SELECT ... FOR UPDATE`、`SELECT ... LOCK IN SHARE MODE`、`UPDATE`、`DELETE`。

---

## 2. 你说的“使用非唯一索引进行带where语句的查询、删除、更新”对吗？

- **对，但不全。**  
  - 不只是“非唯一索引”，**唯一索引的范围查询**（不是等值）同样可能加间隙锁。
  - 如果是主键或唯一索引的**等值查询**（如`WHERE id = 100`），通常只加行锁，不加间隙锁。

- **示例：**
  ```sql
  -- 会加间隙锁（非唯一索引或唯一索引的范围查询）
  SELECT * FROM user WHERE age > 20 FOR UPDATE;
  UPDATE user SET name='Tom' WHERE age BETWEEN 30 AND 40;
  DELETE FROM user WHERE age < 18;
  
  -- 不加间隙锁（唯一索引/主键的等值查询）
  SELECT * FROM user WHERE id = 10 FOR UPDATE;
  ```

---

## 3. 精确表述

**通常在“可重复读”隔离级别下，使用范围条件（如>、<、BETWEEN等）对有索引字段进行查询、更新、删除操作时，InnoDB会加间隙锁，以防止幻读。主键或唯一索引的等值查询只加行锁，不加间隙锁。**

---

## 4. 面试官可能追问

### Q1. 没有索引会加间隙锁吗？
**A1.** 没有索引时，InnoDB无法精确加间隙锁，可能退化为表锁或全表行锁，失去高并发优势。

### Q2. 读已提交隔离级别下会加间隙锁吗？
**A2.** 一般不会，只有在唯一索引冲突或外键检查等特殊情况下才会加间隙锁。

### Q3. 为什么等值查询（唯一索引/主键）不加间隙锁？
**A3.** 因为已精确定位到唯一一行，不存在“区间”可插入新数据的风险，无需防幻读。

---

如果需要更详细的SQL例子或锁机制对比，欢迎继续提问！



## 非唯一索引什么意思？

**非唯一索引**（Non-Unique Index）是指**索引列上的值可以重复**，即**索引并不要求列的值唯一**，可以有多个记录拥有相同的索引值。

---

## 详细解释

- **唯一索引**（Unique Index）：强制要求索引列上的值必须唯一，不能有重复值（除了NULL）。
- **非唯一索引**：对索引列的值不做唯一性限制，同一个值可以在索引列中出现多次。

---

## 举例说明

假设有如下表：

| id   | name | age  |
| ---- | ---- | ---- |
| 1    | Tom  | 18   |
| 2    | Jack | 20   |
| 3    | Mary | 18   |

- 如果对`age`列创建**非唯一索引**，则`age=18`可以出现在多行（如id=1和id=3）。
- 如果创建唯一索引，则`age=18`只能出现一次。

---

## 场景与作用

- 非唯一索引**常用于频繁查询但不要求唯一性的字段**，如性别、分类、状态等。
- 能加速查询，但**不保证唯一性**。

---

## 总结

> **非唯一索引就是允许索引列的值重复，不做唯一性限制的索引。**

如需了解唯一索引、主键索引与非唯一索引的区别，欢迎继续提问！



## 为什么非唯一索引查询会加间隙锁（可重复读级别）

你的问题非常好，也是数据库面试和实际开发中经常被问到的知识点。下面详细为你解答：

---

## 1. 间隙锁的作用是什么？

间隙锁（Gap Lock）是InnoDB用来**防止幻读**的一种锁机制。它不是锁定具体的某一行数据，而是锁住索引区间中的“空隙”，**阻止其它事务在这个区间内插入新数据**。

---

## 2. 为什么非唯一索引查询会加间隙锁？

### 原因一：非唯一索引（或范围查询）对应的可能是多个行或区间

- 非唯一索引的查询（如`WHERE age > 20`），可能查到多行，也可能查到一个区间，**并不是唯一确定一行**。
- 如果只对已有行加行锁（Record Lock），那么别的事务可以在这些区间内插入新记录，导致当前事务的多次查询结果不同，这就是“幻读”。

### 原因二：防止并发下的幻读

- 比如：  
  1. 事务A执行`SELECT * FROM user WHERE age > 20 FOR UPDATE;`，InnoDB会对`age > 20`的区间加间隙锁。
  2. 这时事务B试图插入一条`age=22`的数据，会被阻塞，直到事务A提交。
  3. 这样保证了事务A在整个事务期间多次查询`WHERE age > 20`的结果都一致，不会看到“幻影”一样新增的数据。

### 原因三：唯一索引等值查询不需要间隙锁

- 唯一索引（或主键）等值查询，如`WHERE id = 1`，**只会锁定那一行，不会锁定区间**，因为不会出现“插入新行导致结果变化”的问题。

---

## 3. 总结

> **非唯一索引查询会加间隙锁，是为了防止并发插入带来的幻读问题，保证事务隔离级别下的读取一致性。**  
> 间隙锁锁住的是区间，阻止其它事务在该范围内插入新数据。

---

## 4. 面试官可能追问

**Q1:** 为什么唯一索引的等值查询不加间隙锁？  
**A1:** 因为唯一索引等值查询只会命中唯一一行，不存在区间可插入新行的情况，不会发生幻读，所以只需加行锁。

**Q2:** 间隙锁会带来哪些副作用？  
**A2:** 会影响并发插入性能，可能导致插入阻塞或死锁，因此应合理设计索引和SQL语句，避免大范围间隙锁。

**Q3:** 没有索引时还能加间隙锁吗？  
**A3:** 没有索引时，InnoDB无法精确加间隙锁，可能会退化为表锁或全表行锁。

**Q4:** 哪些隔离级别下会加间隙锁？  
**A4:** 主要是在可重复读（REPEATABLE READ）隔离级别下，InnoDB会加间隙锁防止幻读。

---

如需进一步举例、实验SQL或原理图解，欢迎继续追问！