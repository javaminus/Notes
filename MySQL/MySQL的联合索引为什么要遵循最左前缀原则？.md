**问题：MySQL的联合索引为什么要遵循最左前缀原则？**

**详细解释（结合场景，通俗例子）：**  
联合索引是指在多个字段上建立的复合索引，比如 (a, b, c)。MySQL 的 B+ 树结构会将索引按照最左边的字段优先排序。  
“最左前缀原则”指的是：只有当查询条件里包含了索引的最左边的字段，或者是这些字段的连续前缀时，才能命中并使用这个联合索引。

例如，建立了联合索引 `(a, b, c)`，以下查询能用到索引：
- WHERE a = 1
- WHERE a = 1 AND b = 2
- WHERE a = 1 AND b = 2 AND c = 3

但如果只写 WHERE b = 2 或 WHERE c = 3（没有包含最左的a字段），则无法用到该联合索引。

**通俗例子：**  
就像查字典时，必须先确定第一个字母，才能有效地用拼音表定位单词；如果只知道第二个或第三个字母，查找效率会大大降低。

**总结性回答/提示词：**  
联合索引按最左字段排列，查询必须包含最左字段，才能用上索引（最左前缀原则）。