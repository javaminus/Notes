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
| 二级索引存放的有哪些数据？                 | 主键索引（聚簇索引）叶子节点存放**完整数据**，二级索引存放**主键**。 |                                                              |
| 事务的特性是什么？如何实现的？             | 原子性（   undo log（回滚日志） ）、隔离性（  MVCC（多版本并发控制） 或锁机制 ）、持久性（ redo log （重做日志） ）、一致性（ 持久性+原子性+隔离性 ）； | [Editorial](https://www.xiaolincoding.com/interview/mysql.html#%E4%BA%8B%E5%8A%A1%E7%9A%84%E7%89%B9%E6%80%A7%E6%98%AF%E4%BB%80%E4%B9%88-%E5%A6%82%E4%BD%95%E5%AE%9E%E7%8E%B0%E7%9A%84) |
| 间隙锁的原理                               | 只存在于可重复读隔离级别，目的是为了解决可重复读隔离级别下幻读的现象。 | [Editorial](./MySQL/间隙锁的原理.md)                         |
|                                            |                                                              |                                                              |
|                                            |                                                              |                                                              |
|                                            |                                                              |                                                              |
|                                            |                                                              |                                                              |
|                                            |                                                              |                                                              |
|                                            |                                                              |                                                              |
|                                            |                                                              |                                                              |
|                                            |                                                              |                                                              |

## 【Redis】

| Problems                                                  | Hints                                                        | Solution                                                     |
| --------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Redis高级数据结构的使用场景                               | 常见的有五种数据类型：String（字符串），Hash（哈希），List（列表），Set（集合）、Zset（有序集合）。 BitMap、HyperLogLog、GEO、Stream。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/aliyun.html#redis%E9%AB%98%E7%BA%A7%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E7%9A%84%E4%BD%BF%E7%94%A8%E5%9C%BA%E6%99%AF) |
| 热 key 是什么？怎么解决？                                 | Redis热key是指被频繁访问的key 。开启内存淘汰机制， 设置key的过期时间，  对热点key进行分片 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#%E7%83%AD-key-%E6%98%AF%E4%BB%80%E4%B9%88-%E6%80%8E%E4%B9%88%E8%A7%A3%E5%86%B3) |
| String 是使用什么存储的?为什么不用 c 语言中的字符串?      | Redis 的 String 字符串是用 SDS 数据结构存储的。  **len，记录了字符串长度**。  **alloc，分配给字符数组的空间长度**。  **flags，用来表示不同类型的 SDS**。  **buf[]，字符数组，用来保存实际数据**。  增加了三个元数据：len、alloc、flags，用来解决 C 语言字符串的缺陷。  O（1）复杂度获取字符串长度 ； 二进制安全 ； 不会发生缓冲区溢出 。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#string-%E6%98%AF%E4%BD%BF%E7%94%A8%E4%BB%80%E4%B9%88%E5%AD%98%E5%82%A8%E7%9A%84-%E4%B8%BA%E4%BB%80%E4%B9%88%E4%B8%8D%E7%94%A8-c-%E8%AF%AD%E8%A8%80%E4%B8%AD%E7%9A%84%E5%AD%97%E7%AC%A6%E4%B8%B2) |
| Redis有什么持久化策略？                                   | **AOF 日志** 、 **RDB 快照** 、 **混合持久化方式**           | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#redis%E6%9C%89%E4%BB%80%E4%B9%88%E6%8C%81%E4%B9%85%E5%8C%96%E7%AD%96%E7%95%A5) |
| MySQL两个线程的update语句同时处理一条数据，会不会有阻塞？ | 会，因为InnoDB的行锁。                                       | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/taobao.html#mysql%E4%B8%A4%E4%B8%AA%E7%BA%BF%E7%A8%8B%E7%9A%84update%E8%AF%AD%E5%8F%A5%E5%90%8C%E6%97%B6%E5%A4%84%E7%90%86%E4%B8%80%E6%9D%A1%E6%95%B0%E6%8D%AE-%E4%BC%9A%E4%B8%8D%E4%BC%9A%E6%9C%89%E9%98%BB%E5%A1%9E) |
| Zset 使用了什么数据结构？                                 | Zset 类型的底层数据结构是由**压缩列表或跳表**实现的          | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/byte_dance.html#redis-%E6%9C%89%E5%93%AA%E4%BA%9B%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84) |
| 介绍一下redis中的跳表                                     | 跳表（Skip List）是一种 **基于链表的有序数据结构**，通过**多级索引**来加速查询。 | [Editorial](./Redis/跳表.md)                                 |
| 为什么 MySQL 不用 SkipList？                              | B+树的高度在3层时存储的数据可能已达千万级别，但对于跳表而言同样去维护千万的数据量那么所造成的跳表层数过高而导致的磁盘io次数增多，也就是使用B+树在存储同样的数据下**磁盘io次数**更少 。 |                                                              |
| Redis 使用场景?                                           | **缓存，消息队列、分布式锁等场景**。                         | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/byte_dance.html#redis-%E4%BD%BF%E7%94%A8%E5%9C%BA%E6%99%AF) |
| Redis 性能好的原因是什么？                                | 大部分操作**都在内存中完成** 、 采用单线程模型可以**避免了多线程之间的竞争** 、 采用了 **I/O 多路复用机制**处理大量的客户端 Socket 请求 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/byte_dance.html#redis-%E6%80%A7%E8%83%BD%E5%A5%BD%E7%9A%84%E5%8E%9F%E5%9B%A0%E6%98%AF%E4%BB%80%E4%B9%88) |
| Redis 和 MySQL 如何保证一致性                             | **「先更新数据库 + 再删除缓存」的方案，是可以保证数据一致性的**。 |                                                              |
| 调用 interrupt 是如何让线程抛出异常的?                    | 每个线程都有一个初始值为 `false` 的中断状态，`interrupt()` 会更新该状态。  若线程在 `sleep()`、`join()`、`wait()` 等可中断方法中，会抛出 `InterruptedException` 并解除阻塞；否则，仅设置中断状态，线程可轮询决定是否停止。 |                                                              |
| 如果是靠变量来停止线程，缺点是什么?                       | 缺点是中断可能不够及时，循环判断时会到下一个循环才能判断出来。 |                                                              |
| volatile 保证原子性吗？                                   | volatile关键字并没有保证我们的变量的原子性，volatile是Java虚拟机提供的一种轻量级的同步机制，主要有这三个特性：**保证可见性** 、**不保证原子性**、**禁止指令重排**          使用 `synchronized`来保证原子性 |                                                              |
| synchronized 支持重入吗？如何实现的?                      | ✔ **synchronized 支持重入**，同一线程可多次获取同一把锁。  ✔ **通过对象头的“锁计数器”实现**，锁被同一线程持有时计数递增，释放时递减。  ✔ **避免死锁**，允许父子类方法或递归调用顺利执行。 🚀 | [Editorial](./Java基础/synchronized 支持重入吗，如何实现的.md) |
|                                                           |                                                              |                                                              |
|                                                           |                                                              |                                                              |
|                                                           |                                                              |                                                              |
|                                                           |                                                              |                                                              |

## 【Java基础】

| Problems                                      | Hints                                                        | Solution                                                     |
| --------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 双亲委派机制是什么？                          | 是Java类加载器（ClassLoader）中的一种工作原理。  主要用于**解决类加载过程中的安全和避免重复加载的问题**。 | [Editorial](./Java基础/双亲委派机制.md)                      |
| 编译型语言和解释型语言的区别？                | **编译型语言**：在程序执行**之前**，整个源代码会被编译成机器码或者字节码，生成可执行文件。执行时直接运行编译后的代码，速度快，但跨平台性较差。  **解释型语言**：在程序执行时，逐行解释执行源代码，不生成独立的可执行文件。通常由解释器动态解释并执行代码，跨平台性好，但执行速度相对较慢。   典型的编译型语言如C、C++，典型的解释型语言如Python、JavaScript。 |                                                              |
| 动态数组的实现有哪些？                        | ArrayList和Vector都支持动态扩容，都属于动态数组。    **线程安全性**：Vector是线程安全的，ArrayList不是线程安全的。  **扩容策略**：ArrayList在底层数组不够用时在原来的基础上扩展0.5倍，Vector是扩展1倍。 |                                                              |
| HashMap 的扩容条件是什么？                    | Java7扩容需要满足两个条件：   1、当前数据存储的数量（即size()）大小必须大于等于阈值 ；2、当前加入的数据是否发生了hash冲突。    Java8只需要满足**条件1**。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#hashmap-%E7%9A%84%E6%89%A9%E5%AE%B9%E6%9D%A1%E4%BB%B6%E6%98%AF%E4%BB%80%E4%B9%88) |
| Java 里面线程有哪些状态?                      | new、Runnable、blocked、waiting、timed_waiting、terminated； | [Editorial](./Java基础/Java里面的线程状态.md)                |
| wait 状态下的线程如何进行恢复到 running 状态? | 等待的线程**被其他线程对象唤醒**，`notify()`和`notifyAll()`。  如果线程**没有获取到锁**则会直接进入 Waiting 状态，其实这种本质上它就是执行了 LockSupport.park() 方法进入了Waiting 状态，那么解锁的时候会执行`LockSupport.unpark(Thread)`，与上面park方法对应，给出许可证，**解除等待状态**。 |                                                              |
| notify 和 notifyAll 的区别?                   | **notify 只唤醒一个线程，其他线程仍在等待，若该线程未调用 notify，其余线程可能永远无法唤醒。**  **notifyAll 唤醒所有等待线程，它们竞争锁，最终只有一个线程执行，剩余线程继续等待锁释放。** |                                                              |
| notify 选择哪个线程?                          | notify在源码的注释中说到notify选择唤醒的线程是**任意的**，但是依赖于具体实现的jvm。     JVM有很多实现，比较流行的就是hotspot，hotspot对notofy()的实现并不是我们以为的随机唤醒,，而是**“先进先出”**的顺序唤醒。 |                                                              |
| 如何停止一个线程的运行?                       | 1、使用标志位；2、使用`interrupt()`；3、结合`interrupt()`和标志位；4、使用 `FutureTask.cancel(true)` | [Editorial](./Java基础/如何停止一个线程的运行.md)            |

## 【JVM】

| Problems                       | Hints                                                        | Solution                                           |
| ------------------------------ | ------------------------------------------------------------ | -------------------------------------------------- |
| 垃圾回收 cms和g1的区别是什么？ | 回收策略、垃圾收集目标、内存划分、STW停顿时间、回收过程、吞吐量、适用场景、废弃情况 | [Editorial](./JVM/垃圾回收 cms和g1的区别是什么.md) |
|                                |                                                              |                                                    |
|                                |                                                              |                                                    |



## 【Spring】

| Problems | Hints | Solution |
| -------- | ----- | -------- |
|          |       |          |
|          |       |          |
|          |       |          |

## 【操作系统】

| Problems                                  | Hints                                                        | Solution                                                     |
| ----------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 进程与线程的区别?                         | 本质区别：进程是操作系统资源分配的基本单位，而线程是任务调度和执行的基本单位 。  开销方面、稳定性方面、内存分配方面、包含关系。 | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#%E8%BF%9B%E7%A8%8B%E5%92%8C%E7%BA%BF%E7%A8%8B%E7%9A%84%E5%8C%BA%E5%88%AB) |
| 为什么进程崩溃不会对其他进程产生很大影响? | 进程隔离性、进程独立性。                                     | [Editorial](https://www.xiaolincoding.com/backend_interview/internet_giants/tencent.html#%E8%BF%9B%E7%A8%8B%E5%92%8C%E7%BA%BF%E7%A8%8B%E7%9A%84%E5%8C%BA%E5%88%AB) |
| 有哪些进程调度算法 ?                      | 先来先服务 、短作业优先、最短剩余时间优先、时间片轮转、优先级调度、多级反馈队列 | [Editorial](./操作系统/有哪些进程调度算法.md)                |
|                                           |                                                              |                                                              |
|                                           |                                                              |                                                              |
|                                           |                                                              |                                                              |
|                                           |                                                              |                                                              |
|                                           |                                                              |                                                              |

## 【计算机网络】

| Problems                   | Hints                                                       | Solution                                       |
| -------------------------- | ----------------------------------------------------------- | ---------------------------------------------- |
| HTTP 与 HTTPS 协议的区别？ | 安全、端口、加密方式、证书、完整性、身份认证、SEO、适用场景 | [Editorial](./计算机网络/http与https的区别.md) |
|                            |                                                             |                                                |
|                            |                                                             |                                                |

