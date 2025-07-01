## MySQL EXPLAIN 分析SQL详解

### 一、什么是 EXPLAIN？

EXPLAIN 是 MySQL 提供的 SQL 执行计划分析工具。  
它可以展示一条 SQL 语句（SELECT、DELETE、INSERT、UPDATE）的执行计划，帮助开发者理解优化器如何执行语句，判断是否有效利用索引、是否存在全表扫描、连接顺序等问题，是SQL调优的必备工具。

---

### 二、EXPLAIN 的基本用法

```sql
EXPLAIN SELECT * FROM user WHERE id = 1;
```
或
```sql
EXPLAIN FORMAT=JSON SELECT * FROM user WHERE id = 1;
```
常见用法还包括 `DESC`、`DESCRIBE`。

---

### 三、EXPLAIN 结果字段详解（以常见表格输出为例）

| 字段名        | 含义与解读                                                   |
| ------------- | ------------------------------------------------------------ |
| id            | 查询的序列号，表示执行顺序，越大越优先执行；多表关联时可辅助判断驱动表和被驱动表。 |
| select_type   | 查询类型，如 SIMPLE（简单查询），PRIMARY（主查询），SUBQUERY（子查询），DERIVED（派生表/子查询）。 |
| table         | 当前访问的表名或别名。                                       |
| partitions    | 匹配的分区信息（分区表时有用）。                             |
| type          | 访问类型，性能从好到差：system > const > eq_ref > ref > range > index > all。 |
| possible_keys | 可能用到的索引（优化器分析的结果）。                         |
| key           | 实际使用到的索引。                                           |
| key_len       | 索引长度（字节），越短越好，反映了索引利用的字段数和类型。   |
| ref           | 哪些列或常量与 key 一起被用来查找索引值（通常是主键或外键的值）。 |
| rows          | 预计扫描的行数，越少越好。                                   |
| filtered      | 经过条件过滤后剩余的行数百分比（MySQL 5.7+）。               |
| Extra         | 额外信息，如Using index（覆盖索引）、Using where、Using temporary（临时表）、Using filesort（文件排序）等。 |

---

### 四、核心字段详解与优化建议

#### 1. type（访问类型，最重要）

- **system**：表只有一行（系统表），性能最佳
- **const**：通过主键或唯一索引查找单行（等值查询）
- **eq_ref**：唯一索引等值连接（多表JOIN时）
- **ref**：普通索引等值查找
- **range**：索引范围查找（BETWEEN、>、<、IN等）
- **index**：全索引扫描（不查表数据）
- **ALL**：全表扫描，性能最差，需重点优化

**优化目标：尽量让type不出现ALL，优先const、eq_ref、ref、range。**

---

#### 2. key / possible_keys

- **possible_keys**：所有可能用到的索引
- **key**：实际被用到的索引
- 若key为空，说明没用到索引，要检查SQL和表结构

---

#### 3. rows

- 估算需要扫描的行数，越小越好
- 对大表，rows很大时要考虑加索引或优化SQL

---

#### 4. Extra

常见值说明：

- **Using where**：利用了WHERE条件过滤
- **Using index**：覆盖索引，无需回表
- **Using temporary**：用到了临时表，常见于GROUP BY、ORDER BY
- **Using filesort**：用到了外部排序，ORDER BY时未用到索引，需优化
- **Using join buffer**：关联时未走索引，可能有性能隐患

---

### 五、EXPLAIN分析SQL的常见步骤

1. **看 type 是否为 ALL**（全表扫描，需重点优化）
2. **看 key 是否命中预期索引**
3. **分析 rows 是否过大**
4. **关注 Extra 字段的 Using filesort、Using temporary**
5. **多表JOIN时，注意 id 和 select_type，谁是驱动表**

---

### 六、JSON格式的EXPLAIN

```sql
EXPLAIN FORMAT=JSON SELECT * FROM user WHERE id=1;
```
- 更详细的执行计划，适合自动化分析、复杂SQL。

---

### 七、典型案例

1. **type=ALL，rows很大**  
   → 需要加索引或优化SQL

2. **Extra=Using filesort/temporary**  
   → 优化ORDER BY、GROUP BY，考虑建合适的索引

3. **key为空**  
   → 没有命中索引，检查SQL写法或索引设计

---

### 八、总结口诀

- **EXPLAIN在手，SQL无忧。锁定type、key、rows、Extra，优化有的放矢。**

---

如需具体SQL的EXPLAIN分析案例，欢迎补充SQL语句！



## 常见EXPLAIN面试追问及参考答案

---

### 1. EXPLAIN中的type为index和ALL有何区别？

**答：**
- `type=index` 表示全索引扫描，遍历整个索引（无需访问表数据，通常在覆盖索引时出现）。
- `type=ALL` 表示全表扫描，需要访问所有表数据，性能最差。
- `index`比`ALL`略优，但大表都应避免。

---

### 2. 为什么key_len比实际字段长度大？

**答：**
- `key_len`是索引字段长度之和，加上了各字段类型的存储开销（如varchar加2字节长度，NULLable加1字节NULL标志）。
- 若是联合索引，实际使用几个字段，key_len就等于这些字段的总长度。

---

### 3. explain的rows字段和实际扫描行数一样吗？

**答：**
- 不完全一样。`rows`是优化器根据统计信息估算的行数，实际执行时可能多也可能少，尤其在统计信息不准确时。

---

### 4. Extra字段中Using where和Using index区别是什么？

**答：**
- `Using where`表示用WHERE条件做了过滤。
- `Using index`表示覆盖索引（只用到索引，无需回表）。
- 两者可同时出现，表示通过索引过滤数据且只用索引。

---

### 5. 为什么我的SQL明明加了索引，但EXPLAIN还是显示ALL/没有用上索引？

**答：**
- 索引未命中原因可能有：
  - WHERE条件没有用到索引字段或写法导致索引失效（如对索引字段做函数、运算、隐式类型转换等）。
  - 表数据量太小，优化器认为全表扫描更快。
  - 联合索引未遵循最左前缀原则。
  - 索引统计信息未更新或SQL写法不合理。

---

### 6. possible_keys有多个索引时，key只选了一个，为什么？

**答：**
- 优化器会根据成本估算选择最优索引，只能用一个索引进行查找（除非用Index Merge）。
- 其他索引没被选中，可能是因为选择的索引区分度更高、字段更匹配等原因。

---

### 7. 什么是Index Merge？explain如何体现？

**答：**
- Index Merge是MySQL一种索引合并优化，允许在一个查询中同时用多个单列索引，最后合并结果。
- 在EXPLAIN的key字段会显示如`index_merge`，possible_keys显示所有用到的索引，Extra中出现`Using union`/`Using intersect`等字样。

---

### 8. explain能分析视图、子查询、派生表吗？

**答：**
- 能。每个子查询、派生表、视图都会在EXPLAIN中以不同id、select_type展示执行计划。

---

### 9. 多表连接（JOIN）时，如何判断连接顺序及驱动表？

**答：**
- EXPLAIN中的id小的表是驱动表，执行顺序按id从大到小。select_type、ref等字段辅助判断表之间的连接关系。

---

### 10. EXPLAIN能看到存储过程或触发器里的SQL吗？

**答：**
- 不能。EXPLAIN只能分析单条SQL的执行计划，不能直接分析存储过程、触发器内部的SQL。

---

### 11. explain能分析insert/update/delete吗？

**答：**
- 可以。`EXPLAIN UPDATE ...`、`EXPLAIN DELETE ...`等可以分析这些语句的执行计划（MySQL 5.6+）。

---

### 12. explain输出里没有order by，但extra有Using filesort，为什么？

**答：**
- 说明SQL语句里有ORDER BY，但没用到索引排序，MySQL只能用外部排序（filesort），性能较差。

---

### 13. explain能发现死锁、锁等待等问题吗？

**答：**
- 不能。EXPLAIN只分析执行计划，不显示锁等待、死锁等运行时信息。需用`SHOW ENGINE INNODB STATUS`等命令排查。

---

### 14. explain能看到并行执行计划吗？

**答：**
- MySQL目前（8.0）本身不支持SQL并行执行计划，EXPLAIN也不会展示并行相关信息。

---

### 15. explain FORMAT=JSON比普通格式多什么信息？

**答：**
- JSON格式更详细，能显示cost、attached_condition、used_columns、table_access_type等细粒度信息，便于自动化分析复杂SQL。

---

如需某一问题的更深入解释或实战案例，欢迎补充提问！