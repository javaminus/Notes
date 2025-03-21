## **MySQL 事务的 MVCC 机制原理**

### **1. 什么是 MVCC（多版本并发控制）？**

MVCC（Multi-Version Concurrency Control，多版本并发控制）是一种**无锁并发控制机制**，用于解决数据库事务的**可见性**问题，避免 **脏读、不可重复读、幻读**，同时提高数据库的**并发性能**。  

MySQL 的 **InnoDB** 存储引擎使用 **MVCC** 机制，在 **Read Committed** 和 **Repeatable Read** 隔离级别下**通过版本链和 Read View 机制**来实现高效的事务管理。

---

### **2. MVCC 主要依赖的机制**

#### **（1）隐藏列（事务 ID & 回滚指针）**

在 **InnoDB** 的 MVCC 实现中，每个数据行（记录）都有**隐藏的系统列**：
- **trx_id（事务 ID）**：  
  - 记录最后一次**修改该行**的事务 ID。
- **roll_pointer（回滚指针）**：  
  - 指向该行的**上一个版本**，形成**版本链**，可以通过 undo log（回滚日志）找回历史数据。

#### **（2）Undo Log（回滚日志）**
- **记录旧版本数据**，每次 `UPDATE` 产生一条 Undo Log，形成**版本链**。
- 事务需要读取历史版本时，可以通过 `roll_pointer` 找到**符合可见性规则的数据版本**。

---

### **3. Read View 机制**
在 **MVCC** 机制下，事务读取数据时，会生成一个 **Read View（可见性视图）**，用于决定当前事务**能看到哪些数据版本**。

#### **Read View 主要存储以下信息**
1. **当前活跃事务列表（Active Transaction List）**  
   - 记录**当前未提交的事务 ID**，用于判断哪些数据版本可见。
2. **最小活跃事务 ID（MIN_TRX_ID）**  
   - 事务列表中**最小的未提交事务 ID**。
3. **最大事务 ID（MAX_TRX_ID）**  
   - 事务创建时的最新事务 ID +1，代表**未来的事务 ID**。
4. **当前事务 ID（CURRENT_TRX_ID）**  
   - 该事务自身的事务 ID。

---

### **4. MVCC 的数据可见性规则**
事务读取数据时，判断某一行的 `trx_id` 是否对当前事务可见：

| 规则                                     | 说明                         | 可见性   |
| ---------------------------------------- | ---------------------------- | -------- |
| **trx_id < MIN_TRX_ID**                  | 该数据在当前事务开始前已提交 | ✅ 可见   |
| **trx_id > MAX_TRX_ID**                  | 该数据是未来事务创建的       | ❌ 不可见 |
| **trx_id 在 Active Transaction List 中** | 该数据是未提交事务修改的     | ❌ 不可见 |

---

### **5. MVCC 机制在不同隔离级别下的行为**
| 隔离级别                        | Read View 生成时机                 | 事务期间 Read View 是否变化 | 作用                                                   |
| ------------------------------- | ---------------------------------- | --------------------------- | ------------------------------------------------------ |
| **Read Committed（读已提交）**  | 每次 `SELECT` 时生成新的 Read View | 变化                        | 事务能读取其他事务已提交的数据，可能导致**不可重复读** |
| **Repeatable Read（可重复读）** | 事务开始时生成 Read View           | 不变                        | 事务期间查询的数据一致，解决**不可重复读**             |

---

### **6. MVCC 版本链示例**
假设有一张 `users` 表，初始数据如下：
```sql
SELECT * FROM users;
+----+-------+--------+
| id | name  | age    |
+----+-------+--------+
| 1  | Alice | 25     |
+----+-------+--------+
```

#### **场景 1：事务 A 读取数据**
```sql
BEGIN;
SELECT * FROM users WHERE id = 1;
```
此时事务 A 生成 **Read View**，假设 `trx_id = 100`。

#### **场景 2：事务 B 修改数据**
```sql
BEGIN;
UPDATE users SET age = 26 WHERE id = 1;
-- trx_id = 101
COMMIT;
```
- 事务 B 生成新版本 `(id=1, name='Alice', age=26, trx_id=101)`。
- 旧版本 `(id=1, name='Alice', age=25, trx_id=100)` 仍然保留，并且 `roll_pointer` 指向它。

#### **场景 3：事务 A 读取数据**
```sql
SELECT * FROM users WHERE id = 1;
```
- **Read Committed 隔离级别**：事务 A 生成新的 Read View，看到的是 `age=26`（因为事务 B 已提交）。
- **Repeatable Read 隔离级别**：事务 A **仍然看到 `age=25`**，因为 Read View **不会变**，事务 B 提交后对事务 A **不可见**。

---

### **7. MVCC 解决的并发问题**
| 问题           | Read Committed | Repeatable Read | 解决方式  |
| -------------- | -------------- | --------------- | --------- |
| **脏读**       | ❌ 可能         | ✅ 不可能        | Read View |
| **不可重复读** | ❌ 可能         | ✅ 解决          | Read View |
| **幻读**       | ❌ 可能         | ❌ 可能          | 需间隙锁  |

**注意**：MVCC **不能** 解决**幻读**，需要**间隙锁（Gap Lock）**来解决。

---

### **8. 总结**

- **MVCC 通过版本链（Undo Log）+ Read View 机制实现无锁并发控制**。
- **Read View 由活跃事务列表、事务 ID 组成，用于判断数据是否可见**。
- **Read Committed**：每次查询生成新 Read View，**可能不可重复读**。
- **Repeatable Read**：事务开始时生成 Read View，**可避免不可重复读**。
- **MVCC 提高了并发性能**，但**无法解决幻读，需要间隙锁**。

**MySQL InnoDB 默认使用 MVCC 结合锁机制，在大部分情况下既能保证一致性又能提高性能！** 🚀