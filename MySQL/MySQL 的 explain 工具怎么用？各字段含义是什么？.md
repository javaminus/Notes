当然，继续下一个高频但容易被忽略的MySQL面试题：

---

**问题：MySQL 的 explain 工具怎么用？各字段含义是什么？**

**详细解释（结合场景，通俗例子）：**  
`EXPLAIN` 是MySQL自带的SQL执行计划分析工具，用于分析SQL语句在执行时的处理过程和效率。通过在SELECT语句前加上`EXPLAIN`，可以看到MySQL优化器选择了哪些表、走了哪些索引、预计扫描多少行数据等信息。常见字段包括：

- **id**：查询中每个SELECT子句的标识符，数字越大优先级越高。
- **select_type**：查询类型（如SIMPLE、PRIMARY、SUBQUERY等）。
- **table**：当前访问的表名。
- **type**：连接类型，常见的有ALL（全表扫描）、index、range、ref、eq_ref、const、system、NULL等，性能依次提升。
- **possible_keys**：可能使用到的索引。
- **key**：实际使用到的索引。
- **key_len**：使用的索引长度。
- **ref**：与索引进行比较的列。
- **rows**：预估扫描的行数。
- **Extra**：其他额外信息（如Using where、Using index、Using temporary等）。

**通俗例子：**  
就像你去医院体检，EXPLAIN 就是医生给你一份体检报告，告诉你每一项检查的流程、用什么仪器、花多长时间，有哪些地方需要重点注意。你据此可以有针对性地“优化身体”（优化SQL）。

**总结性回答/提示词：**  
EXPLAIN分析SQL执行计划，重点关注type、key、rows、extra字段，定位是否走索引、是否全表扫描，优化性能必备。