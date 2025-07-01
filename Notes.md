## 【MySQL】

| Problems                                                     | Hints                                                        | Solution                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| SQL 学习指南：从入门到精通                                   |                                                              | [Editorial](./MySQL/SQL 学习指南：从入门到精通.md)           |
| 事务隔离级别有哪些？                                         | 四种隔离级别：读未提交、读已提交、可重复读、串行化           | [Editorial](./MySQL/事务隔离级别有哪些？.md)                 |
| 脏读和幻读的区别？                                           | **脏读**：一个事务读到了「未提交事务修改过的数据」**幻读**：在一个事务内多次查询某个符合查询条件的「记录数量」，如果前后两次查询到的记录数量不一样。 |                                                              |
| 如何防止幻读？                                               | **针对快照读**（普通 select 语句），是通过 MVCC 方式解决了幻读；  **针对当前读**（select ... for update等语句），是通过 `next-key lock`（记录锁+间隙锁） | [Editorial](./MySQL/如何防止幻读.md)                         |
| 可重复读有没有幻读的问题？（举了例子）                       | **可能存在幻读**问题，但不会有脏读和不可重复读               | [Editorial](./MySQL/可重复读有没有幻读的问题？（举了例子）.md) |
| **MySQL的MVCC是什么？它是如何实现高并发读写的？**            | MVCC（Multi-Version Concurrency Control，多版本并发控制）是一种**无锁并发控制机制**，用于解决数据库事务的**可见性**问题，避免 **脏读、不可重复读、幻读**，同时提高数据库的**并发性能**。 主要依赖机制： （1）隐藏列（事务 ID & 回滚指针） 、 （2）Undo Log（回滚日志） | [Editorial](./MySQL/MySQL的MVCC是什么？它是如何实现高并发读写的？.md) |
| mysql的什么命令会加上间隙锁？                                | 在可重复读隔离级别下。 使用非唯一索引进行带`where`语句的查询、删除、更新 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/aliyun.html#mysql%E7%9A%84%E4%BB%80%E4%B9%88%E5%91%BD%E4%BB%A4%E4%BC%9A%E5%8A%A0%E4%B8%8A%E9%97%B4%E9%9A%99%E9%94%81) |
| MySQL 的存储引擎有哪些？为什么常用InnoDB？                   | InnoDB【支持事务、最小锁的粒度是行锁】、MyISAM、Memory       | [Editorial](./MySQL/MySQL 的存储引擎有哪些？为什么常用InnoDB？.md) |
| B+ 树和 B 树的比较                                           | 叶子节点存储数据不同、B+树支持范围查询（叶子节点通过双向链表连接）、B+树修改树的效率更高（矮胖） | [Editorial](./MySQL/B+ 树和 B 树的比较.md)                   |
| 索引失效的情况                                               | 使用左或者左右模糊匹配 、 对索引列使用函数 、 对索引列进行表达式计算 、 联合索引没有正确使用需要遵循最左匹配原则 | [Editorial](./MySQL/索引失效的情况.md)                       |
| **MySQL的联合索引为什么要遵循最左前缀原则？**                | 联合索引按最左字段排列，查询必须包含最左字段，才能用上索引（最左前缀原则）。 | [Editorial](./MySQL/MySQL的联合索引为什么要遵循最左前缀原则？.md ) |
| 什么是覆盖索引？它的优点是什么？                             | 覆盖索引是指一个查询的**所有字段**都能从索引中获取到，而不需要回表到数据表中查找。优点包括：减少磁盘IO，提升查询性能，减少锁的范围。 |                                                              |
| MySQL 的**自增主键**在高并发下会出现什么问题？如何解决？     | 在高并发场景下，自增主键可能会导致**主键冲突**、**插入性能瓶颈**，甚至在主从复制时因主键重复导致数据不一致。解决方案包括：使用**分布式唯一ID**（如雪花算法、UUID等）、主键预分配、或者采用数据库自带的分布式ID生成器。 |                                                              |
| 二级索引存放的有哪些数据？                                   | 主键索引（聚簇索引）叶子节点存放**完整数据**，二级索引存放**主键**。 |                                                              |
| 事务的特性是什么？如何实现的？                               | 原子性（   undo log（回滚日志） ）、隔离性（  MVCC（多版本并发控制） 或锁机制 ）、持久性（ redo log （重做日志） ）、一致性（ 持久性+原子性+隔离性 ）； | [Editorial](https://www.xiaolincoding.com/interview/mysql.html#%E4%BA%8B%E5%8A%A1%E7%9A%84%E7%89%B9%E6%80%A7%E6%98%AF%E4%BB%80%E4%B9%88-%E5%A6%82%E4%BD%95%E5%AE%9E%E7%8E%B0%E7%9A%84) |
| 间隙锁的原理                                                 | 只存在于可重复读隔离级别，目的是为了解决可重复读隔离级别下幻读的现象。 | [Editorial](./MySQL/间隙锁的原理.md)                         |
| 滥用事务，或者一个事务里有特别多sql的弊端？                  | 容易造成死锁和锁超时、数据回滚时间变长、容易造成主从延迟     | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#%E6%BB%A5%E7%94%A8%E4%BA%8B%E5%8A%A1-%E6%88%96%E8%80%85%E4%B8%80%E4%B8%AA%E4%BA%8B%E5%8A%A1%E9%87%8C%E6%9C%89%E7%89%B9%E5%88%AB%E5%A4%9Asql%E7%9A%84%E5%BC%8A%E7%AB%AF) |
| 两条update语句处理一张表的不同的主键范围的记录，一个<10，一个>15，会不会遇到阻塞？底层是为什么的？ | **不会**，因为锁住的范围不一样，不会形成冲突。 第一条 update sql 的话（ id<10），锁住的范围是（-♾️，10） 第二条 update sql 的话（id >15），锁住的范围是（15，+♾️） |                                                              |
| 如果上面2个范围不是主键或索引？还会阻塞吗？                  | 触发全表扫描，会**阻塞**                                     | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#%E5%A6%82%E6%9E%9C2%E4%B8%AA%E8%8C%83%E5%9B%B4%E4%B8%8D%E6%98%AF%E4%B8%BB%E9%94%AE%E6%88%96%E7%B4%A2%E5%BC%95-%E8%BF%98%E4%BC%9A%E9%98%BB%E5%A1%9E%E5%90%97) |
| 表中十个字段，你主键用自增ID还是UUID，为什么？               | **自增ID**。使用 InnoDB 应该尽可能的按主键的自增顺序插入，并且尽可能使用单调的增加的聚簇键的值来插入新行 。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mayi.html#%E8%A1%A8%E4%B8%AD%E5%8D%81%E4%B8%AA%E5%AD%97%E6%AE%B5-%E4%BD%A0%E4%B8%BB%E9%94%AE%E7%94%A8%E8%87%AA%E5%A2%9Eid%E8%BF%98%E6%98%AFuuid-%E4%B8%BA%E4%BB%80%E4%B9%88-%E6%88%91%E5%9B%9E%E7%AD%94%E4%BA%86%E8%87%AA%E5%A2%9E%E5%92%8Cuuid%E7%9A%84%E4%BC%98%E7%BC%BA%E7%82%B9) |
| MySQL的锁讲一下                                              | 全局锁、表级锁、行级锁                                       | [Editorial](./MySQL/MySQL的锁讲一下（按锁的粒度讲一遍）.md)  |
| 设计一个行级锁的死锁，举一个实际的例子                       | **死锁发生条件**：两个事务**交叉加锁**，形成**循环等待**。  **解决方案**：  1、 **统一加锁顺序**（最有效）。 2、 **使用 `NOWAIT` 或 `SKIP LOCKED`** 避免长时间等待。 3、 **使用短事务**，避免锁占用过长。 | [Editorial](./MySQL/行级锁死锁例子.md)                       |
| mysql 如何避免全表扫描？                                     | 建立索引                                                     | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/elme.html#mysql-%E5%A6%82%E4%BD%95%E9%81%BF%E5%85%8D%E5%85%A8%E8%A1%A8%E6%89%AB%E6%8F%8F) |
| mysql如何实现如果不存在就插入如果存在就更新？                | 可以使用 `INSERT ... ON DUPLICATE KEY UPDATE` 语句来实现“如果不存在就插入，如果存在就更新”的功能。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/elme.html#mysql%E5%A6%82%E4%BD%95%E5%AE%9E%E7%8E%B0%E5%A6%82%E6%9E%9C%E4%B8%8D%E5%AD%98%E5%9C%A8%E5%B0%B1%E6%8F%92%E5%85%A5%E5%A6%82%E6%9E%9C%E5%AD%98%E5%9C%A8%E5%B0%B1%E6%9B%B4%E6%96%B0) |
| 数据库访问量过大怎么办？                                     | **创建或优化索引** 、 **查询优化** 、 **避免索引失效** 、 **读写分离**、 **优化数据库表**、 **使用缓存技术** | [Editorial](./MySQL/数据库访问量过大怎么办.md)               |
| MySQL的三大日志说一下，分别应用场景是什么？                  | **redolog**、**binlog**和**undolog**                         | [Editorial](./MySQL/MySQL的三大日志说一下，分别应用场景是什么？.md) |
| **MySQL的Binlog有哪几种格式？各自的优缺点是什么？**          | Binlog有STATEMENT、ROW、MIXED三种格式；STATEMENT体积小但有一致性风险，ROW安全但日志大，MIXED自动切换，生产常用ROW或MIXED。 | [Editorial](./MySQL/MySQL的Binlog有哪几种格式？各自的优缺点是什么？.md) |
| 慢查询是如何调试解决的？                                     | 确认慢查询、分析执行计划、优化查询语句、优化数据库结构、缓存和查询缓存 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/baidu.html#%E6%85%A2%E6%9F%A5%E8%AF%A2%E6%98%AF%E5%A6%82%E4%BD%95%E8%B0%83%E8%AF%95%E8%A7%A3%E5%86%B3%E7%9A%84) |
| MySQL 的慢查询日志是什么？如何开启和分析慢查询？             | 慢查询日志：记录慢SQL，定位数据库瓶颈，常用mysqldumpslow或pt-query-digest分析。 | [Editorial](./MySQL/MySQL 的慢查询日志是什么？如何开启和分析慢查询？.md) |
| **MySQL 的 explain 工具怎么用？各字段含义是什么？**          | EXPLAIN分析SQL执行计划，重点关注type、key、rows、extra字段，定位是否走索引、是否全表扫描，优化性能必备。 | [Editorial](./MySQL/MySQL 的 explain 工具怎么用？各字段含义是什么？.md) |
| 数据库翻页（limit）查询时，发现越往后查询越来越慢，为什么？该如何修改 SQL 能解决? | 数据库翻页使用 `LIMIT offset` 时，`offset` 越大查询越慢，因为需要跳过前面大量数据，建议用基于主键的“条件翻页”优化SQL性能。 | [Editorial](./MySQL/数据库翻页（limit）查询时，发现越往后查询越来越慢，为什么？该如何修改 SQL 能解决.md) |
| 什么是慢查询以及如何调试解决的?                              | 慢查询是指数据库中执行时间超过**设定阈值**的 SQL，通过开启**慢查询日志**、**分析执行计划**和**优化索引**或 **SQL 结构进行定位**和解决。 | [Editorial](./MySQL/什么是慢查询以及如何调试解决的.md)       |
| **什么是回表？为什么有时候会发生回表操作？举例说明。**       | 普通索引查不到的数据，需要通过主键回到聚簇索引获取；**索引覆盖**可避免回表。 | [Editorial](./MySQL/什么是回表？为什么有时候会发生回表操作？举例说明.md ) |
| MySQL 的唯一索引与普通索引有什么区别？各自的应用场景是什么?  | 唯一索引：唯一性约束+加速查询；普通索引：只加速查询，无唯一性约束。 | [Editorial](./MySQL/MySQL 的唯一索引与普通索引有什么区别？各自的应用场景是什么.md) |
| **为什么建议在InnoDB表中使用自增主键作为聚簇索引？**         | 自增主键聚簇索引：插入有序、性能高、碎片少，建议优先选择。   | [Editorial](./MySQL/为什么建议在InnoDB表中使用自增主键作为聚簇索引？.md) |
| MySQL中为什么需要分库分表，以及常见的分库分表策略有哪些？    | 分库分表：为解决单表单库性能瓶颈，常用范围、哈希、时间、逻辑分表策略。 | [Editorial](./MySQL/MySQL中为什么需要分库分表，以及常见的分库分表策略有哪些？.md) |
| MySQL为什么要使用索引？索引的弊端有哪些？                    | 索引加速查询有空间和维护开销，数量需适度，查读写平衡。       | [Editorial](./MySQL/MySQL为什么要使用索引？索引的弊端有哪些？.md) |
| MySQL的**主从复制**原理是什么？常见的主从延迟有哪些原因？    | 主从复制：binlog同步与重放，主写从读，常因写入压力、硬件、网络或大事务导致延迟。 | [Editorial](./MySQL/MySQL的主从复制原理是什么？常见的主从延迟有哪些原因？.md) |
| MySQL中的“锁表”和“锁行”有什么区别？在什么场景下会发生锁表？  | 锁表锁全表，锁行锁单行，InnoDB支持行级锁，MyISAM只支持表级锁，表锁常见于DDL或无索引大操作。 | [Editorial](./MySQL/MySQL中的“锁表”和“锁行”有什么区别？在什么场景下会发生锁表？.md) |
| MySQL 的分区表是什么？适合解决哪些问题？                     | 分区表：大表分片存储，优化大表性能，常用于时间、范围分区，提升查询和归档效率。 | [Editorial](./MySQL/MySQL 的分区表是什么？适合解决哪些问题？.md) |
| 如何在MySQL中创建一个按月份分区的订单表？                    | 分区表按规则“切片”存储大表，每个分区独立管理，常见按时间分区，DDL可直接操作分区。 | [Editorial](./MySQL/如何在MySQL中创建一个按月份分区的订单表？.md) |
|                                                              |                                                              |                                                              |

## 【Redis】

| Problems                                                     | Hints                                                        | Solution                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Redis高级数据结构的使用场景                                  | 常见的有五种数据类型：String（字符串），Hash（哈希），List（列表），Set（集合）、Zset（有序集合）。 BitMap、HyperLogLog、GEO、Stream。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/aliyun.html#redis%E9%AB%98%E7%BA%A7%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E7%9A%84%E4%BD%BF%E7%94%A8%E5%9C%BA%E6%99%AF) |
| Redis BitMap 和 HyperLogLog 的原理是什么？分别适合哪些实际应用场景？ | **BitMap 适合大规模布尔统计（如签到、活跃统计），节省空间，支持位运算。**  **HyperLogLog 适合大规模去重计数（如UV统计），空间极小但有一定误差。**  **二者都是 Redis 的“以空间换效率”的典型高阶数据结构，适用于高并发大数据量的统计场景。** | [Editorial](./Redis/Redis BitMap 和 HyperLogLog 的原理是什么？分别适合哪些实际应用场景？.md) |
| 热 key 是什么？怎么解决？                                    | Redis热key是指被频繁访问的key 。开启内存淘汰机制， 设置key的过期时间，  对热点key进行分片 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#%E7%83%AD-key-%E6%98%AF%E4%BB%80%E4%B9%88-%E6%80%8E%E4%B9%88%E8%A7%A3%E5%86%B3) |
| String 是使用什么存储的?为什么不用 c 语言中的字符串?         | Redis 的 String 字符串是用 SDS 数据结构存储的。  **len，记录了字符串长度**。  **alloc，分配给字符数组的空间长度**。  **flags，用来表示不同类型的 SDS**。  **buf[]，字符数组，用来保存实际数据**。  增加了三个元数据：len、alloc、flags，用来解决 C 语言字符串的缺陷。  O（1）复杂度获取字符串长度 ； 二进制安全 ； 不会发生缓冲区溢出 。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#string-%E6%98%AF%E4%BD%BF%E7%94%A8%E4%BB%80%E4%B9%88%E5%AD%98%E5%82%A8%E7%9A%84-%E4%B8%BA%E4%BB%80%E4%B9%88%E4%B8%8D%E7%94%A8-c-%E8%AF%AD%E8%A8%80%E4%B8%AD%E7%9A%84%E5%AD%97%E7%AC%A6%E4%B8%B2) |
| Redis有什么持久化策略？                                      | Redis持久化有RDB（快照）、AOF（日志）、混合模式。RDB恢复快适合备份，AOF安全性高适合重要数据，混合兼顾性能和安全。 | [Editorial](./Redis/Redis有什么持久化策略？.md)              |
| RDB是怎样做的？                                              | Redis 提供了两个命令来生成 RDB 文件，分别是 **save** 和 **bgsave**，他们的区别就在于是否在「主线程」里执行 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mayi.html#rdb%E6%98%AF%E6%80%8E%E6%A0%B7%E5%81%9A%E7%9A%84-%E7%AD%94%E5%87%BA%E6%9D%A5%E4%BA%86) |
| aof的写入策略，按时间写入和每次都写入的区别，优缺点          | Redis 提供了 3 种写回硬盘的策略， 在 Redis.conf 配置文件中的 appendfsync 配置项可以有以下 3 种参数可填： Always、 Everysec 、No | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mayi.html#aof%E7%9A%84%E5%86%99%E5%85%A5%E7%AD%96%E7%95%A5-%E6%8C%89%E6%97%B6%E9%97%B4%E5%86%99%E5%85%A5%E5%92%8C%E6%AF%8F%E6%AC%A1%E9%83%BD%E5%86%99%E5%85%A5%E7%9A%84%E5%8C%BA%E5%88%AB-%E4%BC%98%E7%BC%BA%E7%82%B9-%E7%AD%94%E5%87%BA%E6%9D%A5%E4%BA%86) |
| 你平常是怎么使用RDB和AOF的？                                 | 数据安全性（AOF）、数据恢复速度（RDB）、数据备份和迁移（RDB）、数据可读性（AOF） | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mayi.html#%E4%BD%A0%E5%B9%B3%E5%B8%B8%E6%98%AF%E6%80%8E%E4%B9%88%E4%BD%BF%E7%94%A8rdb%E5%92%8Caof%E7%9A%84) |
| MySQL两个线程的update语句同时处理一条数据，会不会有阻塞？    | 会，因为InnoDB的行锁。                                       | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#mysql%E4%B8%A4%E4%B8%AA%E7%BA%BF%E7%A8%8B%E7%9A%84update%E8%AF%AD%E5%8F%A5%E5%90%8C%E6%97%B6%E5%A4%84%E7%90%86%E4%B8%80%E6%9D%A1%E6%95%B0%E6%8D%AE-%E4%BC%9A%E4%B8%8D%E4%BC%9A%E6%9C%89%E9%98%BB%E5%A1%9E) |
| Redis 的压缩列表（Ziplist）和跳表（Skiplist）是什么？它们在 Redis 中分别有哪些应用？ | **压缩列表（ziplist）**：节省内存的小型线性存储结构，常用于小 List、Hash、ZSet。  **跳表（skiplist）**：高效有序数据结构，支持范围查找和排序，主要用于 ZSet 大数据量场景。 | [Editorial](./Redis/Redis 的压缩列表（Ziplist）和跳表（Skiplist）是什么？它们在 Redis 中分别有哪些应用？.md) |
| Zset 使用了什么数据结构？                                    | Zset 类型的底层数据结构是由**压缩列表或跳表**实现的          | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/byte_dance.html#zset-%E4%BD%BF%E7%94%A8%E4%BA%86%E4%BB%80%E4%B9%88%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84) |
| redis的hashset底层数据结构是什么？                           | Hash 类型的底层数据结构是由**压缩列表或哈希表**实现的。      | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/didi.html#redis%E7%9A%84hashset%E5%BA%95%E5%B1%82%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E6%98%AF%E4%BB%80%E4%B9%88) |
| 介绍一下redis中的跳表                                        | 跳表（Skip List）是一种 **基于链表的有序数据结构**，通过**多级索引**来加速查询。 | [Editorial](./Redis/跳表.md)                                 |
| 为什么 MySQL 不用 SkipList？                                 | B+树的高度在3层时存储的数据可能已达千万级别，但对于跳表而言同样去维护千万的数据量那么所造成的跳表层数过高而导致的磁盘io次数增多，也就是使用B+树在存储同样的数据下**磁盘io次数**更少 。 |                                                              |
| Redis 使用场景?                                              | **缓存，消息队列、分布式锁等场景**。                         | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/byte_dance.html#redis-%E4%BD%BF%E7%94%A8%E5%9C%BA%E6%99%AF) |
| Redis 性能好的原因是什么？                                   | 大部分操作**都在内存中完成** 、 采用单线程模型可以**避免了多线程之间的竞争** 、 采用了 **I/O 多路复用机制**处理大量的客户端 Socket 请求 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/byte_dance.html#redis-%E6%80%A7%E8%83%BD%E5%A5%BD%E7%9A%84%E5%8E%9F%E5%9B%A0%E6%98%AF%E4%BB%80%E4%B9%88) |
| Redis 和 MySQL 如何保证一致性                                | **「先更新数据库 + 再删除缓存」的方案，是可以保证数据一致性的**。 |                                                              |
| 什么情况使用MySQL，什么情况使用Redis？                       | **MySQL**： 当需要存储结构化数据，并且需要支持复杂的查询操作时，和需要支持事务处理时。  **Redis**：当需要快速访问和处理数据的缓存时，可以选择Redis，能够提供快速的数据读取和写入。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#%E4%BB%80%E4%B9%88%E6%83%85%E5%86%B5%E4%BD%BF%E7%94%A8mysql-%E4%BB%80%E4%B9%88%E6%83%85%E5%86%B5%E4%BD%BF%E7%94%A8redis) |
| 本地缓存和Redis缓存的区别                                    | **本地缓存** 适合 **单机、低并发场景**，速度极快，但**数据不共享**。**Redis 缓存** 适合 **分布式、高并发场景**，支持**持久化**，但**访问速度比本地缓存稍慢**。**最佳实践**：**本地缓存 + Redis 结合使用**，**热点数据走本地缓存**，大规模数据放 Redis 共享。 | [Editorial](./Redis/本地缓存与Redis缓存.md)                  |
| Redis的Key过期了是立马删除吗                                 | 不会，Redis 的过期删除策略是选择「**惰性删除+定期删除**」这两种策略配和使用。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mayi.html#redis%E7%9A%84key%E8%BF%87%E6%9C%9F%E4%BA%86%E6%98%AF%E7%AB%8B%E9%A9%AC%E5%88%A0%E9%99%A4%E5%90%97-%E5%9B%9E%E7%AD%94%E4%BA%86%E5%AE%9A%E6%9C%9F%E5%88%A0%E9%99%A4%E5%92%8C%E6%83%B0%E6%80%A7%E5%88%A0%E9%99%A4%E4%B8%A4%E7%A7%8D%E7%AD%96%E7%95%A5) |
| Redis的大Key问题是什么？                                     | 某个key对应的value值所占的内存空间比较大，导致Redis的性能下降、内存不足、数据不均衡以及主从同步延迟等问题。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mayi.html#redis%E7%9A%84%E5%A4%A7key%E9%97%AE%E9%A2%98%E6%98%AF%E4%BB%80%E4%B9%88-%E7%AD%94%E5%87%BA%E6%9D%A5%E4%BA%86) |
| 大Key问题的缺点？                                            | 内存占用过高 、 性能下降 、 阻塞其他操作 、 网络拥塞 、 主从同步延迟 、 数据倾斜 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mayi.html#%E5%A4%A7key%E9%97%AE%E9%A2%98%E7%9A%84%E7%BC%BA%E7%82%B9-%E7%AD%94%E5%87%BA%E6%9D%A5%E4%BA%86) |
| redis hotkey用什么查，怎么解决hotkey？                       | 使用 Monitor 命令可以实时监控 Redis 数据库的所有命令操作，包括对 Hotkey 的读取和写入操作，通过对返回的执行命令进行统计来分析 Hotkey 的分布。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/elme.html#redis-hotkey%E7%94%A8%E4%BB%80%E4%B9%88%E6%9F%A5-%E6%80%8E%E4%B9%88%E8%A7%A3%E5%86%B3hotkey) |
| redis主节点挂了怎么办？                                      | 分下面几种情况：单机单节点、主从复制结构、 Redis Sentinel（哨兵机制） 、 Redis Cluster（分布式集群模式） | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/didi.html#redis%E4%B8%BB%E8%8A%82%E7%82%B9%E6%8C%82%E4%BA%86%E6%80%8E%E4%B9%88%E5%8A%9E) |
| redis分布式锁怎么实现？                                      | **分布式锁是用于分布式环境下并发控制的一种机制，用于控制某个资源在同一时刻只能被一个应用所使用**。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/didi.html#redis%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E6%80%8E%E4%B9%88%E5%AE%9E%E7%8E%B0) |
| Redis内存淘汰策略                                            | 8种淘汰策略                                                  | [Editorial](./Redis/Redis内存淘汰策略.md)                    |
| **Redis为什么采用单线程模型，单线程模型下为什么还这么快？**  | 单线程避免了锁竞争和上下文切换，配合高效的I/O多路复用和纯内存操作，让Redis即使单线程也极快。 | [Editorial](./Redis/Redis为什么采用单线程模型，单线程模型下为什么还这么快？.md) |
| Redis为什么要采用哨兵（Sentinel）机制？它的作用和工作原理是什么？ | 哨兵（Sentinel）机制用于Redis**高可用**，**自动监控**、**故障转移**和**通知**，实现**主从切换**保障服务不中断。 | [Editorial](./Redis/Redis为什么要采用哨兵（Sentinel）机制？它的作用和工作原理是什么？.md ) |
| **Redis的过期键删除策略有哪些？为什么要用多种策略结合？**    | Redis 过期键删除采用惰性删除+定期删除+内存淘汰三种策略结合，兼顾性能和内存利用。 | [Editorial](./Redis/Redis的过期键删除策略有哪些？为什么要用多种策略结合？.md) |
| Redis如何实现分布式锁？有哪些常见的实现方式和注意事项？      | Redis分布式锁常用`SETNX+过期时间+唯一标识`，解锁要校验value，生产用RedLock提升可靠性。 | [Editorial](./Redis/Redis如何实现分布式锁？有哪些常见的实现方式和注意事项？.md) |
| **Redis集群（Cluster）是如何实现分布式存储和高可用的？主要原理和机制是什么？** | Redis Cluster通过哈希槽分片+多主多从架构，实现分布式存储和高可用，支持自动故障转移和请求自动路由。 | [Editorial](./Redis/Redis集群（Cluster）是如何实现分布式存储和高可用的？主要原理和机制是什么？.md) |
| **Redis的事务（Transaction）是如何实现的？它能保证原子性和隔离性吗？** | Redis事务通过MULTI/EXEC实现批量命令原子性，不支持回滚。WATCH可实现乐观锁，保证事务隔离性，但不是传统数据库的强隔离/原子性。 | [Editorial](./Redis/Redis的事务（Transaction）是如何实现的？它能保证原子性和隔离性吗？.md ) |
| Redis的慢查询是如何监控和分析的？出现慢查询一般怎么优化？    | Redis通过SLOWLOG监控慢查询，分析记录后优化数据结构、避免大key、合理分片和优化命令，提升整体性能。 | [Editorial](./Redis/Redis的慢查询是如何监控和分析的？出现慢查询一般怎么优化？.md) |
| Redis如何实现高并发下的数据一致性？比如缓存和数据库如何保证一致性？ | 缓存一致性推荐“先更新数据库，再删除缓存”，可配合延迟双删或消息队列，确保高并发下数据最终一致。 | [Editorial](./Redis/Redis如何实现高并发下的数据一致性？比如缓存和数据库如何保证一致性？.md) |
| Redis缓存雪崩、缓存击穿、缓存穿透分别是什么？如何应对这些问题？ | 缓存雪崩（分批过期、限流降级）、缓存击穿（互斥锁、热点永不过期）、缓存穿透（缓存空值、布隆过滤器）。 | [Editorial](./Redis/Redis缓存雪崩、缓存击穿、缓存穿透分别是什么？如何应对这些问题？.md) |
| Redis的发布/订阅（Pub/Sub）机制是怎样实现的？适用于哪些场景？ | Redis Pub/Sub实现消息即时推送，适合实时通知、聊天等场景，不适合要求消息可靠或持久化的系统。 | [Editorial](./Redis/Redis的发布订阅（PubSub）机制是怎样实现的？适用于哪些场景？.md) |
| **Redis的主从复制（Replication）是如何实现的？它在实际工作中有什么用？** | Redis主从复制通过全量+增量同步机制实现，广泛用于读写分离、高可用、数据备份等场景，是Redis高性能和高可靠性的基础技术之一。 | [Editorial](./Redis/Redis的主从复制（Replication）是如何实现的？它在实际工作中有什么用？.md ) |
| Redis管道（Pipeline）机制是什么？它的原理、优势和应用场景有哪些？ | Pipeline机制通过`批量发送命令`、`减少RTT`，显著提升批量操作性能，适合大规模数据读写场景，但不是事务，命令`不保证原子性`。 | [Editorial](./Redis/Redis管道（Pipeline）机制是什么？它的原理、优势和应用场景有哪些？.md) |
| Redis Stream 数据结构是什么？它的典型使用场景有哪些？与传统消息队列有何异同？ | Redis Stream是面向消息流的结构，支持持久化、消费组、消息确认和重试，适合异步任务队列、事件追踪、实时日志等场景，是Redis实现轻量级消息队列的首选。 | [Editorial](./Redis/Redis Stream 数据结构是什么？它的典型使用场景有哪些？与传统消息队列有何异同？.md) |
| Redis 如何实现消息队列？有哪些实现方式？各自优缺点如何？     | Redis 可用 List、Pub/Sub、Stream 实现消息队列。  **List 简单高效但功能基础**，适合轻量任务。  **Pub/Sub 支持广播但消息不可靠**，适合即时通知。  **Stream 功能最强，支持消费组与持久化**，适合可靠队列和复杂业务。 | [Editorial](./Redis/Redis 如何实现消息队列？有哪些实现方式？各自优缺点如何？.md) |
| Redis 持久化与主从复制、集群机制的关系是什么？各自如何影响数据安全和高可用？ | **持久化**保障单机数据不丢，**主从复制**实现数据冗余，**哨兵/集群**保障服务不中断。 - 生产环境要结合持久化、主从复制、自动故障转移机制，才能既高可用又高安全。 | [Editorial](./Redis/Redis 持久化与主从复制、集群机制的关系是什么？各自如何影响数据安全和高可用？.md) |
| Redis 的慢查询如何监控与分析？常见慢查询场景如何优化？       | Redis 用 SLOWLOG 监控慢查询，重点关注大 Key 和全量操作，建议用 SCAN 代替 KEYS，定期分析慢查询日志，优化数据结构和命令使用，保障高性能。 | [Editorial](./Redis/Redis 的慢查询如何监控与分析？常见慢查询场景如何优化？.md) |
| Redis 的事务机制是怎样的？能否保证原子性和隔离性？常见的事务相关命令有哪些？ | Redis 事务通过 `MULTI-EXEC` 保证命令批量有序执行，支持原子性但不支持回滚。可用 `WATCH` 实现`乐观锁`防止并发冲突，适合需要简单事务控制的场景。 | [Editorial](./Redis/Redis 的事务机制是怎样的？能否保证原子性和隔离性？常见的事务相关命令有哪些？.md) |

## 【Java基础】

| Problems                                                     | Hints                                                        | Solution                                                     |
| :----------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 双亲委派机制是什么？                                         | 是Java类加载器（ClassLoader）中的一种工作原理。  主要用于**解决类加载过程中的安全和避免重复加载的问题**。 | [Editorial](./Java基础/双亲委派机制.md)                      |
| 什么是类加载器？如何实现自定义类加载器？                     | **类加载器类型、双亲委派流程、定制场景和实现方式**要熟记。  记得举例：Tomcat 热部署、SPI 插件机制、加密 class 加载等。  刷题口诀：   类加载三类清  双亲委派防篡改  自定义 loader 灵活用 | [Editorial](./Java基础/什么是类加载器？如何实现自定义类加载器？.md) |
| 介绍一下类加载器                                             | 加载、验证、准备、解析、初始化                               | [Editorial](./Java基础/类加载器.md)                          |
| 编译型语言和解释型语言的区别？                               | **编译型语言**：在程序执行**之前**，整个源代码会被编译成机器码或者字节码，生成可执行文件。执行时直接运行编译后的代码，速度快，但跨平台性较差。  **解释型语言**：在程序执行时，逐行解释执行源代码，不生成独立的可执行文件。通常由解释器动态解释并执行代码，跨平台性好，但执行速度相对较慢。   典型的编译型语言如C、C++，典型的解释型语言如Python、JavaScript。 Java是编译型语言，但是具有解释型语言的特点。（ 字节码的实际执行是通过 JVM 的解释器完成的 ） |                                                              |
| 抽象类与接口的区别                                           | 当存在“is-a”关系，有共性代码需要复用时用抽象类。  当只需定义能力或规范，或需要多继承时用接口。 | [Editorial](./Java基础/抽象类与接口的区别.md)                |
| Java面试基础知识笔记1                                        |                                                              | [Editorial](./Java基础/Java面试基础知识笔记1.md)             |
| Java 中 try-with-resources 与 try-catch-finally 详细讲解     |                                                              | [Editorial](./Java基础/Java 中 try-with-resources 与 try-catch-finally 详细讲解.md) |
| 动态数组的实现有哪些？                                       | ArrayList和Vector都支持动态扩容，都属于动态数组。    **线程安全性**：Vector是线程安全的，ArrayList不是线程安全的。  **扩容策略**：ArrayList在底层数组不够用时在原来的基础上扩展0.5倍，Vector是扩展1倍。 |                                                              |
| HashMap 的扩容条件是什么？                                   | Java7扩容需要满足两个条件：   1、当前数据存储的数量（即size()）大小必须大于等于阈值 ；2、当前加入的数据是否发生了hash冲突。    Java8只需要满足**条件1**。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#hashmap-%E7%9A%84%E6%89%A9%E5%AE%B9%E6%9D%A1%E4%BB%B6%E6%98%AF%E4%BB%80%E4%B9%88) |
| OS线程是什么？                                               | 操作系统线程                                                 | [Editorial](./Java基础/OS线程是什么.md)                      |
| 谈谈你对线程池的理解                                         | **7个参数**：corePoolSize（核心线程数量）、 **maximumPoolSize** （ 线程池中最多可容纳的线程数量 ）、 **keepAliveTime** （ 当线程池中线程的数量大于corePoolSize，并且某个线程的空闲时间超过了keepAliveTime，那么这个线程就会被销毁。 ）、 **unit** （ 就是keepAliveTime时间的单位。 ） 、**workQueue** （工作队列）、 **threadFactory **（线程工厂） 、**handler**（拒绝策略） | [Editorial](./Java基础/谈谈你对线程池的理解.md)              |
| 线程池的参数如何设置                                         | 核心线程数、最大线程数、等待队列、拒绝策略                   | [Editorial](./Java基础/线程池的参数如何设置.md)              |
| Java 里面线程有哪些状态?                                     | new、Runnable、blocked、waiting、timed_waiting、terminated； | [Editorial](./Java基础/Java里面的线程状态.md)                |
| wait 状态下的线程如何进行恢复到 running 状态?                | 等待的线程**被其他线程对象唤醒**，`notify()`和`notifyAll()`。  如果线程**没有获取到锁**则会直接进入 Waiting 状态，其实这种本质上它就是执行了 LockSupport.park() 方法进入了Waiting 状态，那么解锁的时候会执行`LockSupport.unpark(Thread)`，与上面park方法对应，给出许可证，**解除等待状态**。 |                                                              |
| notify 和 notifyAll 的区别?                                  | **notify 只唤醒一个线程，其他线程仍在等待，若该线程未调用 notify，其余线程可能永远无法唤醒。**  **notifyAll 唤醒所有等待线程，它们竞争锁，最终只有`一个`线程执行，剩余线程继续等待锁释放。** |                                                              |
| notify 选择哪个线程?                                         | notify在源码的注释中说到notify选择唤醒的线程是**任意的**，但是依赖于具体实现的jvm。     JVM有很多实现，比较流行的就是hotspot，hotspot对notify()的实现并不是我们以为的随机唤醒,，而是**“先进先出”**的顺序唤醒。 |                                                              |
| 介绍一下sleep()、wait()、join()。                            | 都与多线程有关                                               | [editorial](./Java基础/介绍一下sleep、wait、join.md)         |
| Java 中 Condition 的理解                                     | **Condition** 是 `java.util.concurrent.locks` 包中的一个接口，配合 `Lock`（比如 `ReentrantLock`）使用，用于实现比 `Object.wait()`/`notify()` 更加灵活的线程协作机制。 | [Editorial](./Java基础/Java 中 Condition 的理解.md)          |
| 如何停止一个线程的运行?                                      | 1、使用标志位；2、使用`interrupt()`；3、结合`interrupt()`和标志位；4、使用 `FutureTask.cancel(true)` | [Editorial](./Java基础/如何停止一个线程的运行.md)            |
| 介绍NIO BIO AIO？                                            | BIO（同步阻塞）：传统 I/O 模式，适用于 小规模连接。 NIO（同步非阻塞）：通过 Selector 实现 多路复用，适用于 高并发。 AIO（异步非阻塞）：基于 回调机制，适用于 超高并发、长连接。 | [Editorial](./Java基础/介绍NIOBIOAIO.md)                     |
| volatile可见性例子                                           |                                                              | [Editorial](./Java基础/volatile可见性例子.md)                |
| volatile 保证原子性吗？                                      | volatile关键字并没有保证我们的变量的原子性，volatile是Java虚拟机提供的一种轻量级的同步机制，主要有这三个特性：**保证可见性** 、**不保证原子性**、**禁止指令重排**          使用 `synchronized`来保证原子性 |                                                              |
| synchronized 支持重入吗？如何实现的?                         | ✔ **synchronized 支持重入**，同一线程可多次获取同一把锁。  ✔ **通过对象头的“锁计数器”实现**，锁被同一线程持有时计数递增，释放时递减。  ✔ **避免死锁**，允许父子类方法或递归调用顺利执行。 🚀 | [Editorial](./Java基础/synchronized支持重入吗.md)            |
| Java创建线程有几种方式                                       | 继承Thread类，重写`run()`方法； 实现Runnable接口并实现`run()`方法，然后将实现了Runnable接口的类传递给Thread类； 使用Callable和Future接口通过Executor框架创建线程；通过线程池方式创建。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#java%E5%88%9B%E5%BB%BA%E7%BA%BF%E7%A8%8B%E6%9C%89%E5%87%A0%E7%A7%8D%E6%96%B9%E5%BC%8F) |
| 线程池有哪些优势？                                           | **减少线程创建和销毁的开销**：频繁地创建和销毁线程会消耗大量系统资源，线程池通过重用已存在的线程来减少这种开销。  **提高响应速度**：当任务到达时，无需等待线程的创建即可立即执行，因为线程池中已经有等待的线程。 |                                                              |
| 说一下面向对象3大特性理解？                                  | 封装、继承、多态                                             | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#%E8%AF%B4%E4%B8%80%E4%B8%8B%E9%9D%A2%E5%90%91%E5%AF%B9%E8%B1%A13%E5%A4%A7%E7%89%B9%E6%80%A7%E7%90%86%E8%A7%A3) |
| Java有什么常用的集合类？                                     | List、Set、Map、Queue。                                      | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#java%E6%9C%89%E4%BB%80%E4%B9%88%E5%B8%B8%E7%94%A8%E7%9A%84%E9%9B%86%E5%90%88%E7%B1%BB) |
| 有哪些集合类是线程安全的，哪些是不安全的？                   | **Vector、HashTable、Properties是线程安全的；**  **ArrayList、LinkedList、HashSet、TreeSet、HashMap、TreeMap等都是线程不安全的。** |                                                              |
| ArrayList和LinkedList区别？                                  | 都实现了**List**接口 ，底层数据结构、插入删除元素效率、随机访问效率、空间占用、使用场景、线程安全 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/elme.html#arraylist%E5%92%8Clinkedlist%E5%8C%BA%E5%88%AB) |
| 讲下HashMap？                                                | 从JDK1.7【数组+链表】与JDK1.8【小于8使用链表，超过8使用红黑树】回答 | [Editorial](./Java基础/讲下HashMap？.md)                     |
| 讲下ConcurrentHashMap？                                      | JDK1.7【数组+链表】JDK1.8【 数组 + 链表/红黑树 】            | [Editorial](./Java基础/讲下ConcurrentHashMap？.md)           |
| 讲下阻塞队列？                                               | 阻塞队列（BlockingQueue）是一种支持**阻塞插入和阻塞获取**的队列，它可以在 **生产者-消费者模型** 中高效地实现**线程安全的数据交换**。 | [Editorial](./Java基础/阻塞队列.md)                          |
| 讲下线程安全的List？                                         | 常见的线程安全的List实现包括 `Collections.synchronizedList` 和 `CopyOnWriteArrayList` 【适合频繁读写】。补充一个`vector` | [Editorial](./Java基础/讲下线程安全的List？.md)              |
| Java类加载过程                                               | 加载、验证、准备、解析、初始化                               | [Editorial](./Java基础/类加载过程.md)                        |
| 实际中类加载会遇到哪些问题？                                 | **类找不到（ClassNotFoundException）** 、   **类定义冲突（NoClassDefFoundError）** 、   **类版本不匹配（UnsupportedClassVersionError）** 、   **类加载死锁** 、   **双亲委派模型导致的类加载问题** 、   **热部署、类卸载失败** 、   **不同 ClassLoader 加载同一类** | [Editorial](./Java基础/实际中类加载会遇到哪些问题.md)        |
| 解释一下什么是可重入锁                                       | **同一个线程可以对同一把锁进行重复加锁**                     | [Editorial](./Java基础/解释一下什么是可重入锁.md)            |
| Java中有哪些常用的锁，在什么场景下使用？                     | ` synchronized 、 ReentrantLock 、 ReentrantReadWriteLock 、 StampedLock 、 Semaphore、CountDownLatch、CyclicBarrier ` | [Editorial](./Java基础/Java中有哪些常用的锁，在什么场景下使用？.md) |
| 什么是反射？有哪些使用场景？                                 | Java 反射机制是在**运行状态中**，对于**任意一个类**，都能够知道这个类中的**所有属性和方法**，对于任意一个**对象**，都能够调用它的任意一个**方法和属性**；这种动态获取的信息以及动态调用对象的方法的功能称为 Java 语言的反射机制。 | [Editorial](./Java基础/什么是反射，有哪些使用场景.md)        |
| ThreadLocal的作用和使用场景？                                | **ThreadLocal 主要用于在每个线程内部存储和隔离变量副本，实现线程间变量独立，避免多线程共享变量导致的并发问题。** | [Editorial](./Java基础/ThreadLocal作用和使用场景.md)         |
| 调用 interrupt 是如何让线程抛出异常的?                       | 每个线程都有一个初始值为 `false` 的中断状态，`interrupt()` 会更新该状态。  若线程在 `sleep()`、`join()`、`wait()` 等可中断方法中，会抛出 `InterruptedException` 并解除阻塞；否则，仅设置中断状态，线程可轮询决定是否停止。 |                                                              |
| 如果是靠变量来停止线程，缺点是什么?                          | 缺点是中断可能不够及时，循环判断时会到下一个循环才能判断出来。 |                                                              |
| 什么是不可变对象（Immutable Object）？Java中如何实现不可变对象？ | 不可变对象：final类 + final字段 + 无setter + 深拷贝引用类型字段，线程安全、可作哈希键、设计简单。 | [Editorial](./Java基础/什么是不可变对象（Immutable Object）？Java中如何实现不可变对象？.md) |
| 什么是Java中的自动装箱与拆箱（Autoboxing & Unboxing）？      | 自动装箱/拆箱：基本类型与包装类型自动转换，常见于集合和运算，注意性能和空指针风险。 | [Editorial](./Java基础/什么是Java中的自动装箱与拆箱（Autoboxing & Unboxing）？.md) |
| 什么是泛型（Generics）？Java 泛型的原理和常见使用场景?       | 泛型：类型参数化，类型检查安全，底层类型擦除，常用于集合、自定义通用类和方法。 | [Editorial](./Java基础/什么是泛型（Generics）？Java 泛型的原理和常见使用场景？.md) |
| 什么是Java中的序列化？常见的应用场景有哪些？                 | 序列化：对象转字节流用于存储或传输，常用于网络通信、持久化、分布式系统。实现`Serializable`，配合ObjectOutputStream/ObjectInputStream使用。 | [Editorial](./Java基础/什么是Java中的序列化？常见的应用场景有哪些？.md) |
| 什么是Java中的深拷贝与浅拷贝？它们的区别是什么？             | 深拷贝复制对象及其引用对象，浅拷贝只复制`引用地址`。深拷贝两对象完全独立，浅拷贝引用类型字段会相互影响。 | [Editorial](./Java基础/什么是Java中的深拷贝与浅拷贝？它们的区别是什么？.md) |
| 什么是Java中的多态？多态的实现方式和实际应用场景有哪些？     | 多态：同一接口多种实现，分为重载和重写，父类引用指向子类对象，提升代码扩展性和灵活性。 | [Editorial](./Java基础/什么是Java中的多态？多态的实现方式和实际应用场景有哪些？.md) |
| 什么是Java中的反射？反射的常见用途是什么？                   | 反射：运行时获取类信息、动态创建对象和调用方法，常用于框架、工具库、JDBC、插件机制等场景。 | [Editorial](./Java基础/什么是Java中的反射？反射的常见用途是什么？.md) |
| 什么是Java中的接口（interface）和抽象类（abstract class）？它们有什么区别，实际开发中如何选择？ | 接口注重规范、支持多实现；抽象类关注复用、可有成员变量和部分实现。只定义规范选接口，需要共性实现选抽象类。 | [Editorial](./Java基础/什么是Java中的接口（interface）和抽象类（abstract class）？它们有什么区别，实际开发中如何选择？.md) |
| 什么是Java中的异常处理机制？Checked和Unchecked异常的区别是什么？ | Java异常分Checked（编译器强制处理）、Unchecked（运行时异常），用try-catch/throws处理，提升程序健壮性和容错性。 | [Editorial](./Java基础/什么是Java中的异常处理机制？Checked和Unchecked异常的区别是什么？.md) |
| 常见的Java异常类型有哪些？异常和错误的区别是什么？           | 异常可分为运行时和受检异常，可被程序处理；错误是严重问题，通常无法恢复。 | [Editorial](./Java基础/常见的Java异常类型有哪些？异常和错误的区别是什么？.md) |
| 什么是Java中的内部类？有哪些类型？实际开发中如何使用？       | 内部类有成员、静态、局部、匿名四种，常用于封装辅助逻辑、事件回调和隐藏实现细节。可访问外部类成员，简化开发。 | [Editorial](./Java基础/什么是Java中的内部类？有哪些类型？实际开发中如何使用？.md) |
| 什么是Java中的Lambda表达式？常见的使用场景有哪些？           | Lambda表达式用于简化单方法接口实现，常用于集合操作、线程、回调等场景，使代码更简洁明了。 | [Editorial](./Java基础/什么是Java中的Lambda表达式？常见的使用场景有哪些？.md) |
| 什么是Java中的泛型擦除（Type Erasure）？泛型擦除带来了哪些限制？ | Java泛型编译后类型被擦除，运行时无泛型信息，限制了泛型数组、类型判断等操作，常需用Class参数或反射辅助。 | [Editorial](./Java基础/什么是Java中的泛型擦除（Type Erasure）？泛型擦除带来了哪些限制？.md) |
| 什么是Java中的注解（Annotation）？常见的应用场景有哪些？     | 注解用于为代码添加元数据，常见于编译检查、框架配置、自动化文档和运行时反射。支持自定义，便于自动化和解耦。 | [Editorial](./Java基础/什么是Java中的注解（Annotation）？常见的应用场景有哪些？.md) |
| 什么是JDK、JRE和JVM？三者有什么区别？                        | JDK用于开发，JRE用于运行，JVM用于跨平台。JDK包含JRE，JRE包含JVM。 | [Editorial](./Java基础/什么是JDK、JRE和JVM？三者有什么区别？.md) |
| 解释Java的跨平台原理（“一次编写，到处运行”）是如何实现的？   | Java 跨平台靠 JVM，不同平台有不同 JVM，只需编译一次字节码，就能在多种系统运行。 | [Editorial](./Java基础/解释Java的跨平台原理（“一次编写，到处运行”）是如何实现的？.md) |
| Java中的String和StringBuilder、StringBuffer有什么区别？      | String不可变，适合少量拼接；StringBuilder高效适合单线程拼接；StringBuffer线程安全适合多线程。 | [Editorial](./Java基础/Java中的String和StringBuilder、StringBuffer有什么区别？.md) |
| Java中的面向对象特性有哪些？请简要说明。                     | Java面向对象特性有封装、继承、多态和抽象，提升代码复用性、安全性和扩展性。 | [Editorial](./Java基础/Java中的面向对象特性有哪些？请简要说明。.md) |
| 简述Java类与对象的关系。                                     | 类是`模板`，对象是`实例`。类描述属性和行为，对象具体持有数据并能执行操作。 | [Editorial](./Java基础/简述Java类与对象的关系。.md)          |
| 什么是构造方法？构造方法的特点和作用是什么？                 | 构造方法用于对象创建时初始化，方法名与类名一致，无返回值，可重载。 | [Editorial](./Java基础/什么是构造方法？构造方法的特点和作用是什么？.md) |
| 什么是方法重载（Overload）和方法重写（Override）？有何区别？ | 重载是同类中方法名相同参数不同，重写是子类改变父类方法实现，二者关注点不同。 | [Editorial](./Java基础/什么是方法重载（Overload）和方法重写（Override）？有何区别？.md) |
| 什么是this关键字？Java中this的常用场景有哪些？               | this代表当前对象，常用于区分同名变量、调用本类其他构造及返回自身实例。 | [Editorial](./Java基础/什么是this关键字？Java中this的常用场景有哪些？.md) |
| 什么是static关键字？它的常见用途有哪些？                     | static修饰的成员属于类本身，常用于共享变量、工具方法、静态初始化和内部类。 | [Editorial](./Java基础/什么是static关键字？它的常见用途有哪些？.md) |
| 什么是包（package）？Java中包的作用是什么？                  | 包用于组织类，防止命名冲突，便于管理和控制访问，建议用域名倒序命名。 | [Editorial](./Java基础/什么是包（package）？Java中包的作用是什么？.md) |
| Java中的访问修饰符有哪些？分别有什么作用？                   | 四种访问修饰符：private最严格，public最开放，default包内可见，protected包及子类可见。 | [Editorial](./Java基础/Java中的访问修饰符有哪些？分别有什么作用？.md) |
| 什么是 Java 内存模型（Java Memory Model, JMM）？它解决了哪些问题？请举例说明 JMM 如何影响多线程程序的正确性。 | JMM 三大性，主内存/工作内存分离，volatile 保可见，synchronized 保原子，写多线程一定牢记！ | [Editorial](./Java基础/什么是 Java 内存模型（Java Memory Model, JMM）？它解决了哪些问题？请举例说明 JMM 如何影响多线程程序的正确性。.md) |
| 什么是乐观锁与悲观锁？它们的实现方式和适用场景各是什么？     | - 乐观锁适合读多写少，性能高但可能要重试（如 CAS）。 - 悲观锁适合写多读少，安全但效率低（如 synchronized）。 - 典型面试点：CAS、AtomicXXX、数据库版本号、synchronized 区别和应用。 | [Editorial](./Java基础/什么是乐观锁与悲观锁？它们的实现方式和适用场景各是什么？.md) |
| 什么是CAS（Compare-And-Swap）？它在Java中的实现原理、优缺点以及应用场景是什么？ | - CAS 是无锁并发的核心，compare-and-swap原理+自旋重试机制。 - 优点：高性能，无阻塞。缺点：ABA、自旋、单变量。 - 面试常考：CAS原理、ABA问题、CAS与synchronized对比及适用场景。 | [Editorial](./Java基础/什么是CAS（Compare-And-Swap）？它在Java中的实现原理、优缺点以及应用场景是什么？.md) |
| 什么是AQS的原理、应用及常见实现有哪些？                      | AQS 用 state + 队列统一管理同步器，实现锁/信号量/闭锁等并发工具 - 原理：CAS修改state，失败则排队阻塞，唤醒后重试 - 常用同步器（ReentrantLock、Semaphore、CountDownLatch 等）都基于AQS - 面试重点：AQS的队列原理、独占与共享模式、模板方法设计 | [Editorial](./Java基础/什么是AQS（AbstractQueuedSynchronizer）？AQS的原理、应用及常见实现有哪些？.md) |
| AQS源码解读                                                  |                                                              | [Editorial](./Java基础/AQS源码解读.md)                       |
| AQS的底层原理细节与面试要点                                  | 用简明语言描述AQS的结构（state+队列），说明其支持的两种模式； - 能举出常见实现类，并解释其底层原理； - 强调AQS模板方法思想，子类只需实现资源获取/释放逻辑。 | [Editorial](./Java基础/AQS的底层原理细节与面试要点.md)       |
| AQS常见面试追问与补充                                        | AQS是JUC包下锁和同步器的基础框架，核心思想是用CAS保证state原子性，用FIFO队列管理等待线程，通过模板方法支持独占与共享两种模式。常见实现有ReentrantLock、Semaphore、CountDownLatch等。AQS通过高效挂起/唤醒和公平/非公平策略，兼顾了性能和灵活性，是Java并发编程的基石。 | [Editorial](./Java基础/AQS常见面试追问与补充.md)             |
| 多线程深度面试200连环深挖题                                  |                                                              | [Editorial](./Java基础/多线程进阶200题.md)                   |

## 【JVM】

| Problems                                                     | Hints                                                        | Solution                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| JVM 入门到进阶全解析                                         |                                                              | [Editorial](./JVM/JVM 入门到进阶全解析.md)                   |
| 线程Dump（jstack）详解                                       | 线程Dump，又叫**线程快照**，是指将JVM进程中所有线程的当前运行状态、调用栈信息一次性导出。通过分析线程Dump，可以排查死锁、线程阻塞、线程数暴涨、CPU占用高等问题，是定位Java线上问题的利器。 | [Editorial](./JVM/线程Dump（jstack）详解.md)                 |
| JVM中对象的生命周期和引用类型有哪些？如何影响垃圾回收？      | 对象生命周期：创建→使用→不可达→等待回收→被回收  四种引用类型：强、软、弱、虚  引用强度影响GC回收时机  典型应用：缓存（软引用）、ThreadLocal（弱引用）、回收通知（虚引用）  复习提示：**“强软弱虚四种引用，引用越弱越易被GC”** | [Editorial](./JVM/JVM中对象的生命周期和引用类型有哪些？如何影响垃圾回收？.md) |
| 垃圾回收 cms和g1的区别是什么？                               | 回收策略、垃圾收集目标、内存划分、STW停顿时间、回收过程、吞吐量、适用场景、废弃情况 | [Editorial](./JVM/垃圾回收 cms和g1的区别是什么.md)           |
| 讲下JVM内存区域？                                            | **方法区**：存储类元数据，JDK 8 之后使用 **元空间（Metaspace）**。  **堆**：存储对象，GC 主要管理区域，分 **新生代 & 老年代**。  **虚拟机栈**：存储局部变量表、方法调用信息，递归深会导致 **StackOverflowError**。  **本地方法栈**：服务于 JNI 调用，溢出也会抛出 **StackOverflowError**。  **程序计数器**：记录当前线程执行的 **字节码指令地址**。 | [Editorial](./JVM/JVM内存区域.md)                            |
| 你知道哪些 JVM 的 GC 机制？                                  | Serial收集器（复制算法) 、 ParNew收集器 (复制算法) 、 Parallel Scavenge收集器 (复制算法) 、 Serial Old收集器 (标记-整理算法) 、 Parallel Old收集器 (标记-整理算法) 、 CMS(Concurrent Mark Sweep)收集器（标记-清除算法） 、 G1(Garbage First)收集器 (标记-整理算法) | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/meituan.html#%E4%BD%A0%E7%9F%A5%E9%81%93%E5%93%AA%E4%BA%9B-jvm-%E7%9A%84-gc-%E6%9C%BA%E5%88%B6) |
| 什么是逃逸分析？它是如何优化对象分配和同步的？               | 逃逸分析：判断对象作用域，决定对象分配位置和优化锁  优化点：栈上分配、标量替换、同步消除  典型例子：方法内临时对象、同步块优化  相关JVM参数：`-XX:+DoEscapeAnalysis`  复习提示：**“判断对象是否只在方法内使用，能否避免堆分配和无用同步”** | [Editorial](./JVM/什么是逃逸分析？它是如何优化对象分配和同步的？.md) |
| 什么是JVM内存溢出（OOM）和内存泄漏？如何定位和解决？         | OOM：JVM分配内存失败，常见于堆、元空间、栈  内存泄漏：无用对象仍被引用，无法回收  排查思路：分析日志、heap dump、监控曲线、代码审查  解决方法：优化代码、合理配置参数、用工具分析  复习提示：**“OOM看异常类型，heap dump查根因，注意静态变量和大对象引用”** | [Editorial](./JVM/什么是JVM内存溢出（OOM）和内存泄漏？如何定位和解决？.md) |
| JVM中有哪些常见的性能监控与排查工具？各自适用哪些场景？      | JDK自带工具：jps、jstack、jmap、jstat、jinfo、VisualVM、JConsole  生产/复杂场景：JMC、MAT、Arthas、YourKit/JProfiler  典型用途：查线程死锁（jstack）、查内存泄漏（jmap+MAT）、实时GC监控（jstat/VisualVM）、线上低开销采集（JMC）  复习提示：**“jps找进程、jstack查线程、jmap导内存、VisualVM/JMC图形化分析”** | [Editorial](./JVM/JVM中有哪些常见的性能监控与排查工具？各自适用哪些场景？.md) |
| JVM是如何进行类的热加载和热替换的？有哪些常见实现方式和使用场景？ | - JVM热加载/热替换：动态加载、替换类字节码，无需重启JVM - 原生HotSwap支持有限（方法体），复杂变更需第三方工具 - 常用工具：JRebel、HotswapAgent、IDEA热加载、Spring Boot Devtools - 典型场景：开发调试、插件系统、服务平滑升级 - 复习提示：**“热替换=改代码不重启，结构变更需用Agent/插件”** | [Editorial](./JVM/JVM是如何进行类的热加载和热替换的？有哪些常见实现方式和使用场景？.md) |
| JVM的即时编译（JIT）机制是什么？有哪些优化手段？如何影响运行性能？ | - JIT：热点代码动态编译为机器码，提升执行效率 - 优化手段：方法内联、逃逸分析、锁优化、循环优化 - 影响：运行越久，性能越高效（预热期） - 典型参数：`-XX:+PrintCompilation`, `-XX:+TieredCompilation` - 复习提示：**“JIT=热点编译提升性能，方法内联+逃逸分析是核心”** | [Editorial](./JVM/JVM的即时编译（JIT）机制是什么？有哪些优化手段？如何影响运行性能？.md) |
| JVM是如何实现线程安全的？有哪些内存模型和关键字保障并发正确性？ | JVM线程安全依靠JMM+关键字（volatile、synchronized、final、原子类）  关注可见性、原子性、有序性  典型场景：单例模式、原子计数、高并发下的锁和无锁  复习提示：**“JMM三性，volatile可见性/synchronized互斥，原子类无锁并发”** | [Editorial](./JVM/JVM是如何实现线程安全的？有哪些内存模型和关键字保障并发正确性？) |
| JVM垃圾回收（GC）的分代模型是什么？各代的回收器如何协同工作？ | JVM分代：新生代（Eden+Survivor）、老年代、元空间  GC分为Minor GC（新生代）、Full GC（全堆/老年代）  对象“熬老”：多次GC后晋升老年代  典型回收器组合：ParNew/CMS、Parallel Scavenge/Parallel Old、G1  复习提示：**“分代GC分新老，复制算法快，晋升规则定，回收器协同优化效率”** | [Editorial](./JVM/JVM垃圾回收（GC）的分代模型是什么？各代的回收器如何协同工作？.md) |
| JVM常见的内存参数有哪些？如何调优不同场景下的JVM内存设置？   | - JVM常用参数：-Xms、-Xmx、-Xss、-XX:MetaspaceSize - 调优思路：结合业务类型、内存监控、GC日志，合理设置堆/栈/元空间 - 典型组合：高并发服务=大堆+G1，高吞吐=Parallel GC，线程多=小-Xss - 复习提示：**“根据应用特性设内存参数，堆/栈/元空间分清楚，监控+调优”** | [Editorial](./JVM/JVM常见的内存参数有哪些？如何调优不同场景下的JVM内存设置？.md) |
| JVM的Safepoint是什么？为什么需要Safepoint？有哪些典型触发场景？ | - Safepoint：JVM让所有线程统一挂起，便于全局操作（如GC、Dump）。 - 典型触发：GC、线程Dump、类卸载、Deoptimization等。 - 停顿长原因：线程长时间无Safepoint（常见于大循环）。 - 复习提示：**“Safepoint=全线程暂停点，保障GC等全局操作安全”** | [Editorial](./JVM/JVM的Safepoint是什么？为什么需要Safepoint？有哪些典型触发场景？.md) |
| 介绍一下：线程Dump（jstack）                                 | 线程Dump（jstack）是Java并发问题排查的重要工具，可以帮助快速定位线程相关的各种异常，是Java开发和运维必备技能之一。 | [Editorial](./JVM/介绍一下：线程Dump（jstack）.md)           |
| JVM垃圾回收（GC）有哪些常见的回收器？它们各自的特点和适用场景是什么？ | - GC有串行、并行、并发、低延迟多种，按业务选型 - 响应时间敏感选CMS/G1，吞吐量优先选Parallel，高并发/大堆优先选G1、ZGC、Shenandoah - 复习提示：**“G1服务器首选，ZGC低延迟，CMS老年代并发，Parallel吞吐量优先”** | [Editorial](./JVM/JVM垃圾回收（GC）有哪些常见的回收器？它们各自的特点和适用场景是什么？.md) |
| JVM运行时数据区包含哪些部分？各自作用是什么？                | **五大区域**：PC寄存器、JVM栈、本地方法栈、堆、方法区（元空间）    **常量池**：方法区的一部分，存字面量和符号引用    **直接内存**：堆外，由 NIO 等框架使用    **OOM类型**：StackOverflowError, Java heap space, PermGen／Metaspace, Direct buffer memory    复习提示：**“PC懂指令；栈存帧；堆存对象；区分PermGen与Metaspace”** | [Editorial](./JVM/JVM运行时数据区包含哪些部分？各自作用是什么？.md) |
| JVM有哪些常见的类加载器？它们的加载顺序和作用是什么？        | - **三大内置加载器**：Bootstrap、Extension、Application   - **双亲委派**：先父后子，防篡改   - **自定义加载器**：插件隔离、热部署、加密加载   - 复习提示：**“启动扩展系统三层委派，自定义破委派灵活拓展”** | [Editorial](./JVM/JVM有哪些常见的类加载器？它们的加载顺序和作用是什么？.md) |
| 请举例说明一次实际的 JVM 参数调优过程和调整依据              | “收集（GC 日志+Heap Dump）→ 分析（晋升率+Gen 使用率）→ 策略（新生代、GC 算法、堆大小、晋升阈值）→ 小步验证→上线监控” | [Editorial](./JVM/请举例说明一次实际的 JVM 参数调优过程和调整依据.md) |
| JVM中常见的OOM错误类型有哪些？如何定位和解决？               | 常见 OOM：Heap Space、PermGen/Metaspace、GC Overhead、Direct Buffer、Native Thread    定位思路：GC 日志 → Heap Dump/Class Histogram → 分析工具（MAT/VisualVM）    解决策略：调参（堆、元空间、Direct Memory、线程栈）、代码优化（缓存、代理、线程池）    复习口诀：  “看日志、导 dump、用 MAT，定位泄漏／大对象；参数扩／收；优化代码防 OOM” | [Editorial](./JVM/JVM中常见的OOM错误类型有哪些？如何定位和解决？.md) |
| 生产环境下如何监控JVM健康状态？常见监控指标有哪些？          | - 关键监控：堆内存、GC（次数/耗时/停顿）、线程、类加载、Metaspace、CPU   - 工具链：JMX（JConsole/JMC）、Prometheus+Grafana、APM、ELK   - 告警：堆使用率＞80%、GC 停顿过长、线程饱和、业务指标异常   - 复习口诀：**“堆／GC／线程／Metaspace／CPU＋业务埋点，视图+告警+演练”** | [Editorial](./JVM/生产环境下如何监控JVM健康状态？常见监控指标有哪些？.md) |
| Java对象的创建与内存分配过程是什么？TLAB是什么？             | **步骤**：加载→分配（Eden/老年代/TLAB）→设头→归零→构造    **TLAB**：线程本地分配，减少竞争    **大对象**：直接老年代或晋升    复习提示：**“TLAB 本地分配，Eden 碰撞快，老年代晋升慎”** | [Editorial](./JVM/Java对象的创建与内存分配过程是什么？TLAB是什么？.md) |
| JVM如何支持 Java 以外的语言特性（如 Kotlin、Scala 等）？     | - “统一字节码” + “invokedynamic” + “MethodHandle”   - 类型擦除与桥接方法   - 运行时库＋编译器代码生成支持多语言特性   - 复习提示：**“JVM 执行字节码，不关心源语言；invokedynamic 与 MethodHandle 是动态语言的利器”** | [Editorial](./JVM/JVM如何支持 Java 以外的语言特性（如 Kotlin、Scala 等）？.md) |
| 如何解读GC日志？常见GC日志参数有哪些？                       | - **开启日志**：`-XX:+PrintGCDetails`／`-Xlog:gc*`   - **解析要点**：时间戳、GC类型、空间变化、停顿时长   - **算法差异**：Parallel、CMS、G1 日志标签与阶段   - **调优指标**：频率、停顿、吞吐、年龄分布   - 复习口诀：     “看日志先识类型 → 空间前后对比 → 停顿时长 → 调参（代大小+算法+线程）” | [Editorial](./JVM/如何解读GC日志？常见GC日志参数有哪些？.md) |
| JVM如何保证类与字节码的安全性？类加载安全机制有哪些？        | - **验证**：魔数→版本→常量池→数据/控制流   - **委派**：先父后子，保核心不被篡改   - **ProtectionDomain** + **SecurityManager**：代码来源→权限检查   - **JAR 签名**：完整性验证   - **JPMS**：模块封装与可见性控制   - **隔离**：自定义加载器沙箱   - 复习口诀：     “验字节→委加载→域限权→签可信→模块封→隔离沙” | [Editorial](./JVM/JVM如何保证类与字节码的安全性？类加载安全机制有哪些？.md) |
| Java 内存模型（JMM）与 volatile 的可见性保证                 | JMM：主内存 vs 工作内存    happens-before：顺序、锁、volatile、线程启动/终止    volatile：可见+有序，不原子    synchronized/CAS/AQS：互斥与高并发原语    复习口诀：“先行发生规则定序，volatile 可见有序锁互斥” | [Editorial](./JVM/Java 内存模型（JMM）与 volatile 的可见性保证.md) |
| synchronized 的实现原理和锁优化                              | “对象头 Mark Word + Monitor → 偏向／轻量级／重量级三态 → JIT 锁消除／锁粗化 → 参数调优（偏向、自旋）” | [Editorial](./JVM/synchronized 的实现原理和锁优化.md)        |
| JVM 中的字符串常量池（String Constant Pool）及 `intern()` 机制是什么？它们对内存和 GC 有什么影响？ | **字符串常量池**：复用字面量和 intern 生成的字符串，节省内存。  **intern()**：将字符串放入常量池，返回池中引用。  **GC 影响**：JDK7+ 常量池在堆，未被引用的字符串可被 GC。频繁 intern 需防 OOM。  **口诀**：`“池中复用，intern 去重，堆上易回收，滥用会 OOM”` | [Editorial](./JVM/JVM 中的字符串常量池（String Constant Pool）及 `intern()` 机制是什么？它们对内存和 GC 有什么影响？.md) |
| 什么是 GC Roots？它们如何决定对象的可达性，进而影响垃圾回收？ | **GC Roots**：栈引用、静态属性、常量、JNI、活跃线程    **可达性**：从 Roots 出发的引用链标记算法    **影响**：只要可达就不回收 → 静态缓存/单例易泄漏    **提示口诀**：  `“栈、静、常、JNI、线程 五大 Root → 图搜标记可达 → 不可达即回收”` | [Editorial](./JVM/什么是 GC Roots？它们如何决定对象的可达性，进而影响垃圾回收？.md) |
| 什么是 JVM 直接内存（Direct Memory）？它的原理、典型应用及对 GC/内存管理的影响是什么？ | **直接内存**：堆外本地内存，NIO/Netty/高性能场景常用。  **管理方式**：GC 间接触发回收，受 MaxDirectMemorySize 限制。  **风险**：大量分配或回收不及时会导致直接内存 OOM。  **口诀**：`“堆外直连高效IO，GC间接管生命周期，参数控量防 OOM”` | [Editorial](./JVM/什么是 JVM 直接内存（Direct Memory）？它的原理典型应用及对 GC内存管理的影响是什么？.md) |
| JVM 中的 Finalizer（finalize 方法）和清理机制是什么？为什么不推荐使用？如何安全地管理对象资源？ | **finalize()**：资源清理，但不安全，易泄漏  **不推荐**：不可控、不可预测、性能差  **推荐**：AutoCloseable + try-with-resources，JDK9+ 用 Cleaner  **口诀**：`“资源要手关，finalize 不可靠，try-with-resources 最安全”` | [Editorial](./JVM/JVM 中的 Finalizer（finalize 方法）和清理机制是什么？为什么不推荐使用？如何安全地管理对象资源？.md) |
| JVM 中线程栈（Stack）溢出（StackOverflowError、OutOfMemoryError: unable to create new native thread）是怎么发生的？如何排查与优化？ | **线程栈溢出**：单线程栈满 = StackOverflowError；系统线程数满 = unable to create new native thread  **排查方法**：递归/线程池/线程数量  **优化手段**：递归转迭代、合理分配线程池、控制线程数  **口诀**：`“单栈爆栈是递归，线程数爆是池管，jstack定位，参数调优”` | [Editorial](./JVM/JVM 中线程栈（Stack）溢出（StackOverflowError、OutOfMemoryError unable to create new native thread）是怎么发生的？如何排查与优化？.md) |
| JVM 的方法区、永久代（PermGen）与元空间（Metaspace）有什么区别？各自的作用与常见问题是什么？ | **方法区**：存类元数据/常量池/静态变量  **PermGen**：JDK8-，JVM进程内，易OOM  **Metaspace**：JDK8+，本地内存，物理内存限制  **口诀**：`“方法区元数据信息，PermGen易爆，Metaspace更大，一样可OOM”` | [Editorial](./JVM/JVM 的方法区、永久代（PermGen）与元空间（Metaspace）有什么区别？各自的作用与常见问题是什么？.md) |
| JVM 的方法区、永久代（PermGen）与元空间（Metaspace）有什么区别？各自的作用与常见问题是什么？ | **GC调优误区**：只调堆、不分代、参数混用、忽略非堆、只看次数、乱用Full GC  **优化建议**：结合业务场景，分析GC日志，合理分代与参数，关注非堆区域  **口诀**：`“调优不迷信，参数看回收，日志细分析，场景定策略”` | [Editorial](./JVM/JVM GC 调优中常见的误区与陷阱有哪些？如何避免？.md) |
| JVM 新生代（Young Generation）与老年代（Old Generation）是怎么划分的？对象在不同代的“晋升”与“回收”机制是什么？ | **新生代**：Eden、S0、S1，短命对象多，Minor GC 频繁  **老年代**：长寿对象、缓存、晋升/大对象  **晋升机制**：年龄、空间不足、大对象  **口诀**：`“朝生夕死新生代，历经磨难进老年，空间不足早晋升，大对象直接进老年”` | [Editorial](./JVM/JVM 新生代（Young Generation）与老年代（Old Generation）是怎么划分的？对象在不同代的“晋升”与“回收”机制是什么？.md) |



## 【Spring】

| Problems                                                     | Hints                                                        | Solution                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| spring三级缓存解决循环依赖问题？                             | **Spring 三级缓存机制通过提前暴露 Bean 的引用，使得循环依赖得以解决，同时保证 AOP 代理不丢失**。 | [Editorial](./Spring/spring三级缓存解决循环依赖问题.md)      |
| 如何使用spring实现事务？【深问：事务传播模型有哪些】         | 编程式事务（`TransactionTemplate`）、声明式事务（`@Transactional`） | [Editorial](./Spring/如何使用spring实现事务 )                |
| 介绍一下@Async                                               | `@Async` 是 Spring 提供的异步方法执行注解。  它允许你将某个方法变成**异步方法**，即调用该方法时不会阻塞当前线程，而是交由 Spring 的线程池异步执行，提升应用的并发能力和响应速度。 | [Editorial](./Spring/介绍一下@Async.md)                      |
| 分布式事务详解与面试高频问题梳理                             | 一次业务操作会跨越多个数据库、服务、系统或消息队列。例如：订单服务写订单库，库存服务扣库存库，支付服务调支付网关。**如果这些操作不能保证"要么全部成功，要么全部失败"，就会出现数据不一致问题。** https://blog.csdn.net/XQ_898878888/article/details/140407125 | [Editorial](./Spring/分布式事务详解)                         |
| springboot常用注解                                           | Bean相关的、依赖注入、读取配置、Web相关、其他注解            | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/aliyun.html#springboot%E5%B8%B8%E7%94%A8%E6%B3%A8%E8%A7%A3%E6%9C%89%E5%93%AA%E4%BA%9B) |
| MyBatis，#和$有什么区别                                      | **主要是SQL注入的问题**                                      | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mayi.html#%E6%88%91%E7%9C%8B%E4%BD%A0%E5%86%99%E5%88%B0%E4%BA%86mybatis-%E5%92%8C-%E6%9C%89%E4%BB%80%E4%B9%88%E5%8C%BA%E5%88%AB-%E4%B8%BB%E8%A6%81%E6%98%AFsql%E6%B3%A8%E5%85%A5%E7%9A%84%E9%97%AE%E9%A2%98) |
| 你说到了SQL注入，那你给我设计出一个SQL注入，具体说表中的字段，然后SQL语句是怎样的 | SQL 注入主要是由于 **拼接 SQL 语句** 造成的，攻击者可以利用它来 **绕过身份验证、窃取数据，甚至破坏数据库**。最有效的防范方法是 **使用参数化查询**，避免直接拼接用户输入到 SQL 语句中。 | [Editorial](./MySQL/SQL注入例子.md)                          |
| Bean 的生命周期                                              | 8大步                                                        | [Editorial](./Spring/Bean的生命周期.md)                      |
| Bean是否单例？                                               | Spring 中的 Bean 默认都是单例的。                            | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mayi.html#bean%E6%98%AF%E5%90%A6%E5%8D%95%E4%BE%8B) |
| Bean的单例和非单例，生命周期是否一样                         | 不一样的，Spring Bean 的生命周期完全由 IoC 容器控制。Spring 只帮我们管理单例模式 Bean 的完整生命周期，对于 `prototype` 的 Bean，Spring 在创建好交给使用者之后，则不会再管理后续的生命周期。 |                                                              |
| Spring容器里存的是什么？                                     | 在Spring容器中，存储的**主要是Bean对象**。 Bean是Spring框架中的基本组件，用于表示应用程序中的各种对象。当应用程序启动时，Spring容器会根据配置文件或注解的方式创建和管理这些Bean对象。Spring容器会负责创建、初始化、注入依赖以及销毁Bean对象。 |                                                              |
| Bean注入和xml注入最终得到了相同的效果，它们在底层是怎样做的  | **最终实现效果相同**：无论 XML 还是注解，最终都生成 **BeanDefinition**，通过 **反射实例化 Bean 并注入依赖**。  **区别在于解析方式**：   **XML** 方式**使用 `BeanFactory` + `setter` 方法**进行注入。  **`@Autowired` 注解使用 `AutowiredAnnotationBeanPostProcessor`，直接通过反射赋值**，不会调用 setter。  **推荐使用注解方式**：代码更加简洁，支持 **Spring Boot 自动装配**，XML 适用于 **复杂 XML 配置管理**（如 Spring Cloud 配置中心）。 | [Editorial](./Spring/Bean注入与xml注入.md)                   |
| spring 里@Autowired 和 @Resource 注解有什么区别？            | 来源不同、注入方式、属性、依赖性、使用场景                   | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/elme.html#spring-%E9%87%8C-autowired-%E5%92%8C-resource-%E6%B3%A8%E8%A7%A3%E6%9C%89%E4%BB%80%E4%B9%88%E5%8C%BA%E5%88%AB) |
| Spring的IOC介绍一下                                          | **IOC（控制反转）** 是一种**设计思想**，用于管理对象的依赖关系。Spring 通过 **IOC 容器** 负责创建、管理和注入对象，而不是由代码手动创建对象。 | [Editorial](./Spring/Spring的IOC介绍一下.md)                 |
| 为什么依赖注入不适合使用字段注入？                           | 字段注入可能引起的三个问题：**对象的外部可见性**;  **可能导致循环依赖**;  **无法设置注入的对象为final，也无法注入静态变量** | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/kuaishou.html#%E4%B8%BA%E4%BB%80%E4%B9%88%E4%BE%9D%E8%B5%96%E6%B3%A8%E5%85%A5%E4%B8%8D%E9%80%82%E5%90%88%E4%BD%BF%E7%94%A8%E5%AD%97%E6%AE%B5%E6%B3%A8%E5%85%A5) |
| Spring的aop介绍一下                                          | 切面编程、动态代理实现                                       | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/kuaishou.html#spring%E7%9A%84aop%E4%BB%8B%E7%BB%8D%E4%B8%80%E4%B8%8B) |
| Spring的事务，使用this调用是否生效？                         | 不能生效。因为Spring事务是通过代理对象来控制的，只有通过代理对象的方法调用才会应用事务管理的相关规则。当使用`this`直接调用时，是绕过了Spring的代理机制，因此不会应用事务设置 |                                                              |
| Spring MVC的工作流程描述一下                                 | 7大步：请求进入、寻找控制器、执行控制器、返回模型数据、解析视图、渲染视图、响应返回 | [Editorial](./Spring/Spring MVC的工作流程描述一下.md)        |
| Spring 中的 BeanFactory 和 ApplicationContext 有什么区别？   | ApplicationContext = BeanFactory + 企业级特性（如国际化、事件等），开发中优先用 ApplicationContext，BeanFactory 适合底层或特殊场景。 | [Editorial](./Spring/Spring 中的 BeanFactory 和 ApplicationContext 有什么区别？.md) |
| Spring 中的单例 Bean 是线程安全的吗？为什么？                | Spring 单例 Bean 并不保证线程安全；与线程安全无关，需开发者自行保证。无状态 Bean 通常安全，有状态需加锁或避免状态共享。 | [Editorial](./Spring/Spring 中的单例 Bean 是线程安全的吗？为什么？.md) |
| Spring 中如何实现事件发布与监听机制？                        | Spring 事件机制：发布-监听模式，解耦模块通信。发布用 ApplicationEventPublisher，监听用 @EventListener 或 ApplicationListener。常用于通知、日志、异步等场景。 | [Editorial](./Spring/Spring 中如何实现事件发布与监听机制？.md) |
| Spring 中的循环依赖是什么？Spring 是如何解决循环依赖的？     | Spring 循环依赖：A 依赖 B，B 又依赖 A。Spring 通过三级缓存（singletonObjects、earlySingletonObjects、singletonFactories）机制，提前暴露 Bean 引用，解决 setter/属性注入的循环依赖。构造器注入无法解决。 | [Editorial](./Spring/Spring 中的循环依赖是什么？Spring 是如何解决循环依赖的？.md) |
| 什么是 Spring 的依赖注入（DI）？有哪些常用的依赖注入方式？   | 依赖注入（DI）：Spring IoC 核心。构造器注入优先，Setter 适合可选依赖，字段注入不推荐。DI 解耦代码，便于测试和维护。 | [Editorial](./Spring/什么是 Spring 的依赖注入（DI）？有哪些常用的依赖注入方式？.md) |
| Spring 的事务管理是如何实现的？声明式事务和编程式事务有何区别？ | Spring 事务管理：声明式（@Transactional，推荐，自动控制）和编程式（TransactionTemplate，手动控制）。事务传播行为很重要。大多数业务用声明式，复杂场景用编程式。 | [Editorial](./Spring/Spring 的事务管理是如何实现的？声明式事务和编程式事务有何区别？.md) |
| Spring 中的 @Component、@Service、@Repository、@Controller 注解有什么区别？ | @Component 通用组件，@Service 业务服务，@Repository DAO 持久层（异常转换），@Controller Web 控制器（Spring MVC）。本质一样，主要是语义和层次区分。 | [Editorial](./Spring/Spring 中的 @Component、@Service、@Repository、@Controller 注解有什么区别？.md) |
| Spring 中的 Bean 作用域（Scope）有哪些？它们的应用场景是什么？ | Spring Bean 作用域：singleton（单例，默认），prototype（多例），request/session/application/websocket（Web 环境）。常用 singleton，原型适合有状态对象，Web 场景用 request、session。 | [Editorial](./Spring/Spring 中的 Bean 作用域（Scope）有哪些？它们的应用场景是什么？.md) |
| Spring 的 AOP（面向切面编程）是什么？有哪些常用的应用场景？  | Spring AOP：面向切面编程，横切关注点（如日志、事务、安全）自动织入方法执行，提升复用和解耦。常见注解 @Aspect、@Before、@After、@Around。 | [Editorial](./Spring/Spring 的 AOP（面向切面编程）是什么？有哪些常用的应用场景？.md) |
| Spring Boot 自动配置的原理是什么？如何自定义自动配置？       | Spring Boot 自动配置原理：@EnableAutoConfiguration + spring.factories + 条件注解。自定义自动配置需实现配置类并注册到 spring.factories。 | [Editorial](./Spring/Spring Boot 自动配置的原理是什么？如何自定义自动配置？.md) |
| Spring Boot 和 Spring Cloud 有什么区别？各自的主要功能是什么？ | Spring Boot：简化开发，自动配置、内嵌服务器、Starter。Spring Cloud：微服务基础设施，服务注册发现、配置中心、网关、熔断等。Cloud 基于 Boot，用于云原生/微服务架构。 | [Editorial](./Spring/Spring Boot 和 Spring Cloud 有什么区别？各自的主要功能是什么？.md) |
| Spring 中的条件注解（@Conditional）有什么作用？常见的条件注解有哪些？ | 条件注解：控制 Bean 是否装配（如 @ConditionalOnClass、@ConditionalOnMissingBean、@ConditionalOnProperty），常用于自动配置和环境切换，可自定义条件。 | [Editorial](./Spring/Spring 中的条件注解（@Conditional）有什么作用？常见的条件注解有哪些？.md) |
| Spring 中的事件机制（ApplicationEvent）是什么？有哪些常见应用场景？ | Spring 事件机制（ApplicationEvent）：应用内异步/同步解耦通信，事件发布者 publish，监听器监听处理，常用于业务解耦、扩展、异步任务等场景。 | [Editorial](./Spring/Spring 中的事件机制（ApplicationEvent）是什么？有哪些常见应用场景？.md) |
| Spring 的配置文件有哪些常用方式？如何实现配置的动态刷新？    | Spring 配置方式：properties、YAML、环境变量、配置中心。动态刷新常用 @RefreshScope + Spring Cloud Config/Nacos 等，支持不重启服务实时生效。 | [Editorial](./Spring/Spring 的配置文件有哪些常用方式？如何实现配置的动态刷新？.md) |
| Spring 如何实现多环境（多 profile）配置？如何切换环境？      | 多环境配置：多 profile 文件（如 application-dev.properties），用 spring.profiles.active 指定激活环境，也可用 @Profile 控制 Bean 加载。支持命令行、环境变量、YAML 多块等切换方式。 | [Editorial](./Spring/Spring 如何实现多环境（多 profile）配置？如何切换环境？.md) |
| 如何在 Spring 中实现自定义注解？自定义注解一般有哪些使用场景？ | 自定义注解：@interface 定义 + @Target/@Retention + AOP/后处理配合使用。常用于统一日志、权限、参数校验、标记元数据等场景。 | [Editorial](./Spring/如何在 Spring 中实现自定义注解？自定义注解一般有哪些使用场景？.md) |
| Spring 中的 BeanPostProcessor 有什么作用？常见的应用场景有哪些？ | BeanPostProcessor：对 Bean 初始化前后进行扩展增强，常用于 AOP 代理、自动注解处理、自定义注解逻辑、属性修改等，是 Spring 容器的重要扩展点。 | [Editorial](./Spring/Spring 中的 BeanPostProcessor 有什么作用？常见的应用场景有哪些？.md) |

## 【操作系统】

| Problems                                                     | Hints                                                        | Solution                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 进程与线程的区别?                                            | 本质区别：进程是操作系统资源分配的基本单位，而线程是任务调度和执行的基本单位 。  开销方面、稳定性方面、内存分配方面、包含关系。 | [Editorial](./操作系统/进程与线程的区别.md)                  |
| 补充 - 协程                                                  | 协程是一种`用户态`的`轻量级线程`，其调度`完全由用户程序控制`，而不需要`内核`的参与。协程拥有自己的`寄存器上下文和栈`，但与其他协程`共享堆内存`。协程的切换开销非常小，因为只需要保存和恢复协程的上下文，而无需进行内核级的上下文切换。这使得协程在处理大量并发任务时具有非常高的效率。然而，协程需要程序员显式地进行调度和管理，相对于线程和进程来说，`其编程模型更为复杂`。 |                                                              |
| 为什么进程崩溃不会对其他进程产生很大影响?                    | 进程隔离性、进程独立性。                                     | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#%E8%BF%9B%E7%A8%8B%E5%92%8C%E7%BA%BF%E7%A8%8B%E7%9A%84%E5%8C%BA%E5%88%AB) |
| 有哪些进程调度算法 ?                                         | 先来先服务 、短作业优先、最短剩余时间优先、时间片轮转、优先级调度、多级反馈队列 | [Editorial](./操作系统/有哪些进程调度算法.md)                |
| 死锁发生条件是什么？                                         | 互斥条件 、 持有并等待条件 、 不可剥夺条件 、 环路等待条件   | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mihayou.html#%E6%AD%BB%E9%94%81%E5%8F%91%E7%94%9F%E6%9D%A1%E4%BB%B6%E6%98%AF%E4%BB%80%E4%B9%88) |
| 如何避免死锁？                                               | 避免死锁问题就只需要破环其中一个条件就可以，最常见的并且可行的就是**使用资源有序分配法，来破环环路等待条件**。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mihayou.html#%E5%A6%82%E4%BD%95%E9%81%BF%E5%85%8D%E6%AD%BB%E9%94%81) |
| 介绍一下操作系统内存管理                                     | 操作系统设计了虚拟内存，每个进程都有自己的独立的虚拟内存，我们所写的程序不会直接与物理内打交道。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/mihayou.html#%E4%BB%8B%E7%BB%8D%E4%B8%80%E4%B8%8B%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F%E5%86%85%E5%AD%98%E7%AE%A1%E7%90%86) |
| 介绍copy on write                                            | 写时复制，当多个进程或线程共享同一块数据时，**只有在有写操作时才真正复制数据**，否则大家共享同一份数据副本。 | [Editorial](./操作系统/介绍copy on write.md)                 |
| Linux操作系统中哪个命令可以**查看端口**被哪个应用占用？      | 可以使用`lsof`命令或`netstat`命令查看端口被哪个应用占用。` lsof -i :端口号` 或则 `netstat -tulnp | grep 端口号` |                                                              |
| 如果服务应用部署在 Linux 上，**CPU 打满后**，想查看哪个进程导致的，用什么命令？ | 方式1：$top$  然后可以按 `P` 键来按 CPU 使用率排序，查看哪些进程占用了最多的 CPU 资源。  方式2：$htop$。 方式3：$ps$。 | [Editorial](./操作系统/如果服务应用部署在 Linux 上，CPU 打满后，想查看哪个进程导致的，用什么命令.md) |
| 如果想查看是**进程的哪个线程**，用什么命令？                 | 1、`top -H -p <进程PID>`;  2、`ps -mp <进程PID> -o THREAD,tid,time`; 3、 `ps -L -p <进程PID>` | [Editorial](./操作系统/如果想查看是进程的哪个线程，用什么命令.md) |
| **想查看代码中哪个位置导致的 CPU 高，该怎么做？Java 应用怎么排查 CPU 或内存占用率过高的问题？** | - Linux 层定位进程和线程，转换线程ID为16进制。 - 用 jstack、arthas 等工具定位具体代码位置。 - 内存问题用 jmap、MAT、VisualVM。 - 线上强烈推荐使用 Arthas，简单高效。 | [Editorial](./操作系统/想查看代码中哪个位置导致的 CPU 高，该怎么做？Java 应用怎么排查 CPU 或内存占用率过高的问题.md) |
| linux如何查看线程和进程状态                                  | Linux 查看进程和线程状态常用命令有：ps、top、pstree、以及通过 /proc 目录查看详细信息。 | [Editorial](./操作系统/linux如何查看线程和进程状态.md)       |
| **讲一下银行家算法**                                         | 银行家算法通过安全性检查，动态决定资源分配，避免死锁，但实现较复杂，适合对资源需求可预知的系统。 | [Editorial](./操作系统/讲一下银行家算法.md)                  |

## 【计算机网络】

| Problems                             | Hints                                                        | Solution                                                     |
| ------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| HTTP 与 HTTPS 协议的区别？           | 安全、端口、加密方式、证书、完整性、身份认证、SEO、适用场景  | [Editorial](./计算机网络/http与https的区别.md)               |
| HTTP原理是什么？                     | HTTP（超文本传输协议）是应用层协议 、 HTTP 是基于 TCP 协议来实现的 、 一个完整的 HTTP 请求从请求行开始 、 HTTP 是一种无状态协议，这意味着每个请求都是独立的 、 HTTP 可以传输多种类型的数据，包括文本、图像、音频、视频等 | [Editorial](./计算机网络/HTTP原理是什么.md)                  |
| TCP和UDP区别是什么？                 | 连接、服务对象、可靠性、 拥塞控制、流量控制 、首部开销、传输方式 | [Editorial](./计算机网络/TCP和UDP区别是什么？.md)            |
| TCP协议里的TIME_WAIT状态是什么？     | TIME_WAIT 状态的存在是为了确保网络连接的可靠关闭。只有主动发起关闭连接的一方（即主动关闭方）才会有 TIME_WAIT 状态。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/didi.html#tcp%E5%8D%8F%E8%AE%AE%E9%87%8C%E7%9A%84time-wait%E7%8A%B6%E6%80%81%E6%98%AF%E4%BB%80%E4%B9%88) |
| UDP怎么保证可靠性？                  | 连接迁移 、 重传机制 、 前向纠错 、 拥塞控制                 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/didi.html#udp%E6%80%8E%E4%B9%88%E4%BF%9D%E8%AF%81%E5%8F%AF%E9%9D%A0%E6%80%A7) |
| 网络有什么常用的通信协议？           | **HTTP**：用于在**Web浏览器**和**Web服务器**之间传输超文本的协议，是目前最常见的**应用层**协议。  **HTTPS**：在HTTP的基础上添加了**SSL/TLS**加密层，用于在不安全的网络上安全地传输数据。  **TCP**：面向连接的**传输层**协议，提供可靠的数据传输服务，保证数据的顺序和完整性。  **UDP**：无连接的**传输层**协议，提供了数据包传输的简单服务，适用于实时性要求高的应用。  **IP**：**网络层**协议，用于在网络中传输数据包，定义了数据包的格式和传输规则。 |                                                              |
| 前后端交互用的是什么协议？           | 用HTTP和HTTPS协议比较多。前端通过HTTP协议向服务器端发送请求，服务器端接收请求并返回相应的数据，实现了前后端的交互。HTTP协议简单、灵活，适用于各种类型的应用场景。 |                                                              |
| HTTP 常见状态码有哪些？              | 1XX：提示信息；2XX：成功；3XX：重定向；4XX：报文有误；5XX：服务器内部错误 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/meituan.html#http-%E5%B8%B8%E8%A7%81%E7%8A%B6%E6%80%81%E7%A0%81%E6%9C%89%E5%93%AA%E4%BA%9B) |
|                                      |                                                              |                                                              |
| Dns基于什么协议实现？udp 还是 tcp？  | DNS 基于UDP协议实现，DNS使用UDP协议进行域名解析和数据传输。  |                                                              |
| 为什么是udp？                        | **低延迟** 、**简单快速**、**轻量级**                        | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/kuaishou.html#%E4%B8%BA%E4%BB%80%E4%B9%88%E6%98%AFudp) |
| http的特点是什么？                   | 基于文本、可扩展性、灵活性、无状态                           | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/kuaishou.html#http%E7%9A%84%E7%89%B9%E7%82%B9%E6%98%AF%E4%BB%80%E4%B9%88) |
| http无状态体现在哪？                 | HTTP的无状态体现在每个请求之间**相互独立**，服务器不会保留之前请求的状态信息。每次客户端向服务器发送请求时，服务器都会独立处理该请求，不会记住之前的请求信息或状态。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/kuaishou.html#http%E6%97%A0%E7%8A%B6%E6%80%81%E4%BD%93%E7%8E%B0%E5%9C%A8%E5%93%AA) |
| Cookie和session的区别是什么？        | 存储位置、安全性、存储容量                                   | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/kuaishou.html#cookie%E5%92%8Csession%E7%9A%84%E5%8C%BA%E5%88%AB%E6%98%AF%E4%BB%80%E4%B9%88) |
| 服务器处理并发请求有哪几种方式？     | 单线程web服务器方式 、 多进程/多线程web服务器 、 I/O多路复用web服务器 、 多路复用多线程web服务器 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/baidu.html#%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%A4%84%E7%90%86%E5%B9%B6%E5%8F%91%E8%AF%B7%E6%B1%82%E6%9C%89%E5%93%AA%E5%87%A0%E7%A7%8D%E6%96%B9%E5%BC%8F) |
| 说一下select，poll，epoll的区别？    | `select`、`poll` 和 `epoll` 都是 **I/O 多路复用** 机制，用于 **同时监听多个文件描述符（FD）**，当某个 FD **可读/可写** 时通知应用程序。   `select`、`poll` 适用于小规模连接，**O(N) 复杂度**，随 FD 数量增加性能下降。  **现代 Linux 服务器推荐 epoll**，性能最佳！🚀 | [Editorial](./计算机网络/说一下select_poll_epoll的区别.md)   |
| https是如何防范中间人的攻击？        | 加密、身份校验机制                                           | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/baidu.html#https%E6%98%AF%E5%A6%82%E4%BD%95%E9%98%B2%E8%8C%83%E4%B8%AD%E9%97%B4%E4%BA%BA%E7%9A%84%E6%94%BB%E5%87%BB) |
| 描述一下打开百度首页后发生的网络过程 | 解析`URL`、对域名进行`dns`解析、发起`NNS`查询、 本地`DNS`服务器查询 、 根DNS服务器查询 、 顶级域名服务器查询 、 权威域名服务器查询 、 返回结果 、 建立`TCP`连接 、 三次握手 、 发送`HTTP`请求 、 服务器处理请求 、 发送`HTTP`响应 、 接收响应和渲染页面 、 关闭`TCP`连接 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/baidu.html#%E6%8F%8F%E8%BF%B0%E4%B8%80%E4%B8%8B%E6%89%93%E5%BC%80%E7%99%BE%E5%BA%A6%E9%A6%96%E9%A1%B5%E5%90%8E%E5%8F%91%E7%94%9F%E7%9A%84%E7%BD%91%E7%BB%9C%E8%BF%87%E7%A8%8B) |
| 什么是ddos攻击？怎么防范？           | 分布式拒绝服务（DDoS）攻击是通过大规模互联网流量淹没目标服务器或其周边基础设施，以破坏目标服务器、服务或网络正常流量的恶意行为。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/baidu.html#%E4%BB%80%E4%B9%88%E6%98%AFddos%E6%94%BB%E5%87%BB-%E6%80%8E%E4%B9%88%E9%98%B2%E8%8C%83) |
| 如何查看网络连接情况？               | 常用 netstat、ss、lsof、ifconfig、ip、ping 等命令，可快速查看 Linux 网络连接和状态。 | [Editorial](./计算机网络/如何查看网络连接情况.md)            |

## 【高并发场景】

| Problems                                             | Hints                                                        | Solution                                                     |
| ---------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 限流算法有哪些？                                     | 计数器、滑动窗口、令牌桶、漏桶、滑动窗口日志                 | [Editorial](./高并发场景/限流算法有哪些.md)                  |
| redis，nginx，netty 是依赖什么做的这么高性能？       | ✅**Redis**：单线程但**超快**，因 `epoll + 高效数据结构`。  ✅ **Nginx**：`epoll + sendfile` 提供**超高吞吐量**，适合 Web 服务器。  ✅ **Netty**：`epoll + ByteBuf` 提供**高并发网络通信**，用于 RPC、微服务。 | [Editorial](./高并发场景/redis_nginx_netty 是依赖什么做的这么高性能.md) |
| 如何实现高并发下的唯一订单号生成？                   | **分布式唯一ID生成方案**：数据库自增（易冲突）、UUID（不可读）、Redis自增（高并发）、雪花算法（趋势递增/高性能）  **高并发推荐**：Redis自增或雪花算法，注意时钟回拨和高可用  **记忆口诀**：自增易阻塞，UUID难查找，Redis快雪花妙，唯一有序最重要 | [Editorial](./高并发场景/如何实现高并发下的唯一订单号生成？.md) |
| 高并发场景下如何保证接口的幂等性？                   | **幂等性含义**：同一操作多次执行结果相同    **场景**：支付回调、订单创建、消息消费    **常用方案**：唯一请求号（幂等号）、数据库唯一约束、token机制、乐观锁    **高并发建议**：前端生成幂等号，后端Redis/DB去重，注意性能与存储清理 | [Editorial](./高并发场景/高并发场景下如何保证接口的幂等性？.md) |
| 高并发下如何实现分布式锁？常见方案和优缺点分析       | - **分布式锁方案**：数据库锁（简单低效）、Redis锁（高性能高并发）、ZooKeeper锁（强一致性） - **高并发推荐**：Redis锁+唯一标识+自动过期，或Redisson - **记忆口诀**：数据库易瓶颈，Redis快需防误删，ZooKeeper强一致 | [Editorial](./高并发场景/高并发下如何实现分布式锁？常见方案和优缺点分析.md) |
| 高并发场景下如何防止重复提交？                       | **防重提交方案**：幂等Token、接口Token机制、数据库唯一约束、前端防抖/节流  **高并发推荐**：幂等号+Redis存储，数据库唯一约束兜底  **记忆口诀**：幂等号拦重复，Token校验防误触，DB唯一兜底忙 | [Editorial](./高并发场景/高并发场景下如何防止重复提交？.md)  |
| 高并发下如何进行限流？常见限流算法和场景分析         | - **限流算法**：固定窗口、滑动窗口、漏桶、令牌桶 - **高并发推荐**：令牌桶适合突发，漏桶适合平滑，滑动窗口防突刺 - **记忆口诀**：窗口计数易突刺，漏桶平滑流量忙，令牌桶突发抗压强 | [Editorial](./高并发场景/高并发下如何进行限流？常见限流算法和场景分析.md) |
| 高并发下如何实现异步消息削峰填谷？                   | **削峰填谷原理**：用消息队列缓冲高并发请求，慢慢消费  **常见模型**：生产-消费模型 + 限流排队  **高并发建议**：异步写队列+多消费者+幂等消费+死信队列  **记忆口诀**：高峰进队列，后台慢处理，幂等防重复，死信防丢失 | [Editorial](./高并发场景/高并发下如何实现异步消息削峰填谷？.md) |
| 高并发下如何保证数据一致性？常见一致性方案与适用场景 | - **一致性模型**：强一致性、最终一致性、弱一致性 - **常用方案**：分布式事务（2PC/TCC/SAGA）、消息中间件+补偿、乐观锁/悲观锁 - **高并发建议**：最终一致性+幂等+补偿，关键业务用强一致 - **记忆口诀**：强一致慢安全，最终一致高性能，幂等补偿保周全 | [Editorial](./高并发场景/高并发下如何保证数据一致性？常见一致性方案与适用场景.md) |
| 高并发场景下如何合理利用多级缓存？                   | **多级缓存架构**：本地缓存（低延迟）+分布式缓存（高容量）+数据库（最终兜底）  **高并发建议**：优先读本地，未命中再查分布式，再查数据库  **常见问题**：一致性、容量、更新策略、缓存预热  **记忆口诀**：本地快，分布广，分级兜底保高并，更新一致少烦恼 | [Editorial](./高并发场景/高并发场景下如何合理利用多级缓存？.md) |
| 高并发下如何实现热点数据和热点Key的优化防护？        | - **热点Key识别与优化**：监控分析+分片分流+本地缓存+静态化+限流降级 - **高并发建议**：热点分片、本地预热、请求排队、静态内容、限流兜底 - **记忆口诀**：分片分流解热点，本地缓存降压力，静态限流兜底忙 | [Editorial](./高并发场景/高并发下如何实现热点数据和热点Key的优化防护？.md) |
| 高并发下如何设计高可用与自动故障转移机制？           | **高可用目标**：无单点、自动切换、弹性伸缩、持续服务  **常用方案**：负载均衡+多实例、主从/主备切换、集群、副本、健康检查  **高并发建议**：分层高可用（服务/缓存/数据库）、自动故障转移、监控告警  **记忆口诀**：负载均衡分流忙，主备切换保不停，集群副本抗风险，健康自愈少故障 | [Editorial](./高并发场景/高并发下如何设计高可用与自动故障转移机制？.md) |
| 高并发系统下如何实现高效日志采集与追踪？             | **日志采集目标**：高性能、集中分析、全链路追踪、高可用  **常用架构**：ELK/EFK、Kafka、SkyWalking/Jaeger  **高并发建议**：异步采集、批量入库、traceId贯穿、降采样、索引优化  **记忆口诀**：采集异步快，链路trace全，ELK集中看，Kafka削峰难 | [Editorial](./高并发场景/高并发系统下如何实现高效日志采集与追踪？.md) |
| 分布式系统中如何保证消息的可靠投递？                 | 存消息要持久，发确认要等全，消费记得要幂等，异常补偿别放松！ | [Editorial](./高并发场景/分布式系统中如何保证消息的可靠投递？.md) |
| 如何应对高并发下的数据库事务一致性问题？             |                                                              | [Editorial](./高并发场景/如何应对高并发下的数据库事务一致性问题？.md) |
| 如何排查和解决Java线上系统的内存泄漏问题？           |                                                              | [Editorial](./高并发场景/如何排查和解决Java线上系统的内存泄漏问题？.md) |

## 【中间件】

| Problems                                                     | Hints                                                        | Solution                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 中间件基础知识总结                                           | - 中间件是“系统粘合剂”，帮助各模块解耦协作、提升扩展性和可维护性。 - 常见类型：Web服务器、消息队列、缓存、数据库中间件、服务注册与发现。 - 作用关键词：解耦、复用、扩展、高可用、分布式协作。 | [Editorial](./中间件/中间件基础知识总结.md)                  |
|                                                              |                                                              |                                                              |
|                                                              |                                                              |                                                              |
| 如何设计一个支持“限流（Rate Limiting）”功能的中间件？为什么在分布式系统中限流如此重要？ | - **限流的作用**：防止系统过载，保障服务可用性和公平性。 - **常见算法**：固定窗口、滑动窗口、令牌桶、漏桶。 - **分布式难点**：状态一致性、性能瓶颈、数据同步。 - **场景记忆法**：把限流理解为“超市排队+中央排号机”。 | [Editorial](./中间件/如何设计一个支持“限流（Rate Limiting）”功能的中间件？为什么在分布式系统中限流如此重要？.md) |
| 如何实现“服务的健康检查（Health Check）”中间件？它在微服务架构中有何意义？ | - **健康检查的作用**：提升系统稳定性、自动容错与流量管理。 - **常见类型**：Liveness、Readiness、自定义业务检查。 - **微服务意义**：防止流量打到异常实例，辅助自动恢复，提升可观测性。 - **记忆法**：“航班起飞前的安全检查”——活着≠准备好了。 | [Editorial](./中间件/如何实现“服务的健康检查（Health Check）”中间件？它在微服务架构中有何意义？.md) |
| 在中间件中如何实现“请求追踪（Request Tracing）”？它为何是分布式系统开发的关键？ | **作用**：请求追踪帮助定位分布式系统中的性能瓶颈与故障。  **关键点**：生成唯一Trace ID，全链路传递，日志聚合。  **常见工具**：OpenTelemetry、Jaeger、Zipkin。  **场景记忆法**：快递单号追踪包裹轨迹。 | [Editorial](./中间件/在中间件中如何实现“请求追踪（Request Tracing）”？它为何是分布式系统开发的关键？.md) |
|                                                              |                                                              |                                                              |
|                                                              |                                                              |                                                              |
|                                                              |                                                              |                                                              |
|                                                              |                                                              |                                                              |
|                                                              |                                                              |                                                              |
|                                                              |                                                              |                                                              |

## 【设计模式】

| Problems                         | Hints | Solution                                                    |
| -------------------------------- | ----- | ----------------------------------------------------------- |
| 设计模式总结                     |       | [Editorial](./设计模式/设计模式总结.md)                     |
| 工厂方法模式与抽象工厂模式的区别 |       | [Editorial](./设计模式/工厂方法模式与抽象工厂模式的区别.md) |



## 【其他】

| Problems                                                     | Hints                                                        | Solution                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 给定a、b两个文件，各存放50亿个url，每个url各占64字节，内存限制是4G，让你找出a、b文件共同的url | `分治 + hashmap `                                            | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/baidu.html#%E7%BB%99%E5%AE%9Aa%E3%80%81b%E4%B8%A4%E4%B8%AA%E6%96%87%E4%BB%B6-%E5%90%84%E5%AD%98%E6%94%BE50%E4%BA%BF%E4%B8%AAurl-%E6%AF%8F%E4%B8%AAurl%E5%90%84%E5%8D%A064%E5%AD%97%E8%8A%82-%E5%86%85%E5%AD%98%E9%99%90%E5%88%B6%E6%98%AF4g-%E8%AE%A9%E4%BD%A0%E6%89%BE%E5%87%BAa%E3%80%81b%E6%96%87%E4%BB%B6%E5%85%B1%E5%90%8C%E7%9A%84url) |
| 介绍一下cap理论                                              | CAP 原则又称 CAP 定理, 指的是在一个分布式系统中, Consistency（一致性）、 Availability（可用性）、Partition tolerance（分区容错性）, **三者不可得兼** | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/didi.html#%E4%BB%8B%E7%BB%8D%E4%B8%80%E4%B8%8Bcap%E7%90%86%E8%AE%BA) |
|                                                              |                                                              |                                                              |


