## 【MySQL】

| Problems                                   | Hints                                                        | Solution                                                     |
| ------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 事务隔离级别有哪些？                       | 四种隔离级别：读未提交、读已提交、可重复读、串行化           | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/aliyun.html#%E4%BA%8B%E5%8A%A1%E9%9A%94%E7%A6%BB%E7%BA%A7%E5%88%AB%E6%9C%89%E5%93%AA%E4%BA%9B) |
| 脏读和幻读的区别？                         | **脏读**：一个事务读到了「未提交事务修改过的数据」**幻读**：在一个事务内多次查询某个符合查询条件的「记录数量」，如果前后两次查询到的记录数量不一样。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/aliyun.html#%E5%B9%BB%E8%AF%BB%E5%92%8C%E8%84%8F%E8%AF%BB%E7%9A%84%E5%8C%BA%E5%88%AB) |
| 如何防止幻读？                             | **针对快照读**（普通 select 语句），是通过 MVCC 方式解决了幻读；  **针对当前读**（select ... for update等语句），是通过 `next-key lock`（记录锁+间隙锁） | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/aliyun.html#%E5%A6%82%E4%BD%95%E9%98%B2%E6%AD%A2%E5%B9%BB%E8%AF%BB) |
| 事务的mvcc机制原理是什么？                 | MVCC（Multi-Version Concurrency Control，多版本并发控制）是一种**无锁并发控制机制**，用于解决数据库事务的**可见性**问题，避免 **脏读、不可重复读、幻读**，同时提高数据库的**并发性能**。 主要依赖机制： （1）隐藏列（事务 ID & 回滚指针） 、 （2）Undo Log（回滚日志） | [Editorial](./MySQL/事务的MVCC机制原理是什么？.md)           |
| mysql的什么命令会加上间隙锁？              | 在可重复读隔离级别下。 使用非唯一索引进行带`where`语句的查询、删除、更新 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/aliyun.html#mysql%E7%9A%84%E4%BB%80%E4%B9%88%E5%91%BD%E4%BB%A4%E4%BC%9A%E5%8A%A0%E4%B8%8A%E9%97%B4%E9%9A%99%E9%94%81) |
| MySQL 的存储引擎有哪些？为什么常用InnoDB？ | InnoDB【支持事务、最小锁的粒度是行锁】、MyISAM、Memory       | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#mysql-%E7%9A%84%E5%AD%98%E5%82%A8%E5%BC%95%E6%93%8E%E6%9C%89%E5%93%AA%E4%BA%9B-%E4%B8%BA%E4%BB%80%E4%B9%88%E5%B8%B8%E7%94%A8innodb) |
| B+ 树和 B 树的比较                         | 叶子节点存储数据不同、B+树支持范围查询（叶子节点通过双向链表连接）、B+树修改树的效率更高（矮胖） | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#b-%E6%A0%91%E5%92%8C-b-%E6%A0%91%E7%9A%84%E6%AF%94%E8%BE%83) |
| 索引失效的情况                             | 使用左或者左右模糊匹配 、 对索引列使用函数 、 对索引列进行表达式计算 、 联合索引没有正确使用需要遵循最左匹配原则 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#%E7%B4%A2%E5%BC%95%E5%A4%B1%E6%95%88%E7%9A%84%E6%83%85%E5%86%B5) |
| 二级索引存放的有哪些数据？                 | 主键索引（聚簇索引）叶子节点存放完整数据，二级索引存放主键。 |                                                              |

## 【Redis】

| Problems                                                  | Hints                                                        | Solution                                                     |
| --------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Redis高级数据结构的使用场景                               | 常见的有五种数据类型：String（字符串），Hash（哈希），List（列表），Set（集合）、Zset（有序集合）。 BitMap、HyperLogLog、GEO、Stream。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/aliyun.html#redis%E9%AB%98%E7%BA%A7%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E7%9A%84%E4%BD%BF%E7%94%A8%E5%9C%BA%E6%99%AF) |
| 热 key 是什么？怎么解决？                                 | Redis热key是指被频繁访问的key 。开启内存淘汰机制， 设置key的过期时间，  对热点key进行分片 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#%E7%83%AD-key-%E6%98%AF%E4%BB%80%E4%B9%88-%E6%80%8E%E4%B9%88%E8%A7%A3%E5%86%B3) |
| String 是使用什么存储的?为什么不用 c 语言中的字符串?      | Redis 的 String 字符串是用 SDS 数据结构存储的。  **len，记录了字符串长度**。  **alloc，分配给字符数组的空间长度**。  **flags，用来表示不同类型的 SDS**。  **buf[]，字符数组，用来保存实际数据**。  增加了三个元数据：len、alloc、flags，用来解决 C 语言字符串的缺陷。  O（1）复杂度获取字符串长度 ； 二进制安全 ； 不会发生缓冲区溢出 。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#string-%E6%98%AF%E4%BD%BF%E7%94%A8%E4%BB%80%E4%B9%88%E5%AD%98%E5%82%A8%E7%9A%84-%E4%B8%BA%E4%BB%80%E4%B9%88%E4%B8%8D%E7%94%A8-c-%E8%AF%AD%E8%A8%80%E4%B8%AD%E7%9A%84%E5%AD%97%E7%AC%A6%E4%B8%B2) |
| Redis有什么持久化策略？                                   | **AOF 日志** 、 **RDB 快照** 、 **混合持久化方式**           | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#redis%E6%9C%89%E4%BB%80%E4%B9%88%E6%8C%81%E4%B9%85%E5%8C%96%E7%AD%96%E7%95%A5) |
| MySQL两个线程的update语句同时处理一条数据，会不会有阻塞？ | 会，因为InnoDB的行锁。                                       | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#mysql%E4%B8%A4%E4%B8%AA%E7%BA%BF%E7%A8%8B%E7%9A%84update%E8%AF%AD%E5%8F%A5%E5%90%8C%E6%97%B6%E5%A4%84%E7%90%86%E4%B8%80%E6%9D%A1%E6%95%B0%E6%8D%AE-%E4%BC%9A%E4%B8%8D%E4%BC%9A%E6%9C%89%E9%98%BB%E5%A1%9E) |
|                                                           |                                                              |                                                              |
|                                                           |                                                              |                                                              |
|                                                           |                                                              |                                                              |

## 【Java基础】

| Problems             | Hints                                                        | Solution                                |
| -------------------- | ------------------------------------------------------------ | --------------------------------------- |
| 双亲委派机制是什么？ | 是Java类加载器（ClassLoader）中的一种工作原理。  主要用于**解决类加载过程中的安全和避免重复加载的问题**。 | [Editorial](./Java基础/双亲委派机制.md) |
|                      |                                                              |                                         |
|                      |                                                              |                                         |

## 【Spring】

| Problems | Hints | Solution |
| -------- | ----- | -------- |
|          |       |          |
|          |       |          |
|          |       |          |

