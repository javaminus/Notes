# Redis五大数据类型的底层数据结构详解

Redis的五种基本数据类型在底层使用了不同的数据结构实现，以平衡内存使用和操作效率。下面是详细介绍：

## 1. String (字符串)

**底层实现：**
- **SDS (Simple Dynamic String)**：Redis自定义的字符串结构，len，alloc，flag，char[] buf
  - 二进制安全(可存储任何数据)
  - 知道字符串长度(O(1)复杂度)
  - 动态扩容和缩容
- **整数编码 (int)**：当字符串是整数且范围在LONG_MIN到LONG_MAX之间时，直接用整数(long)存储

## 2. Hash (哈希)

**底层实现：**
- **Listpack** (Redis 7.0+) / **Ziplist** (Redis 7.0前)：
  - 用于元素少且小的场景
  - 节省内存，紧凑排列
  - 条件：元素数量 < hash-max-ziplist-entries (默认512)
  - 条件：所有值长度 < hash-max-ziplist-value (默认64字节)

- **Dict (字典)**：
  - 当不满足上述条件时，自动转为dict
  - 基于哈希表实现，支持O(1)查找
  - 使用链地址法解决冲突

## 3. List (列表)

**底层实现：**
- **Quicklist** (Redis 3.2+)：
  - 双向链表 + listpack/ziplist的混合结构
  - 每个节点是一个listpack/ziplist
  - 平衡了内存效率和操作性能

- 历史实现：
  - Redis 3.2前：纯链表(linkedlist)或纯压缩列表(ziplist)

## 4. Set (集合)

**底层实现：**
- **Intset (整数集合)**：
  - 用于所有元素都是整数且元素数量较少的场景
  - 条件：元素全是整数且数量 < set-max-intset-entries (默认512)

- **Dict (字典)**：
  - 当包含非整数或元素过多时使用
  - 只用key部分，value设为NULL

## 5. Zset (有序集合)

**底层实现：**
- **Listpack/Ziplist**：
  - 用于元素少且小的场景
  - 条件：元素数量 < zset-max-ziplist-entries (默认128)
  - 条件：所有成员长度 < zset-max-ziplist-value (默认64字节)

- **Skiplist (跳表) + Dict (字典)**：
  - 当不满足上述条件时使用
  - Skiplist：保证有序性，支持高效的范围查询，O(logN)时间复杂度
  - Dict：映射成员到分数，支持O(1)的成员查找

## 转换阈值

Redis会根据数据规模自动在不同实现间切换：

| 数据类型 | 转换参数                 | 默认阈值 |
| -------- | ------------------------ | -------- |
| Hash     | hash-max-ziplist-entries | 512      |
| Hash     | hash-max-ziplist-value   | 64 bytes |
| Set      | set-max-intset-entries   | 512      |
| Zset     | zset-max-ziplist-entries | 128      |
| Zset     | zset-max-ziplist-value   | 64 bytes |
| List     | list-max-ziplist-size    | -2       |

这些阈值可通过配置文件调整，以适应特定应用场景的需求。

##dict

> 在Redis的源码和底层实现中，**dict（字典）**是一种高效的**哈希表（Hash Table）**数据结构，用于存储键值对（key-value pairs）。dict是Redis实现很多数据结构和功能的基础，比如：普通的hash类型、数据库键空间、跳表中的分数查找索引等。
>
> ---
>
> ## 1. dict的本质
>
> - **dict**就是哈希表的具体实现，Redis源码中叫做`dict`。
> - 它通过哈希函数将key映射到一个table（数组）的位置，实现高效的插入、查找和删除。
> - 每个桶里可以存放一个或多个元素（链表解决冲突）。
>
> ---
>
> ## 2. 主要用途
>
> - **Redis的数据库键空间**：所有的key-value数据都存放在dict里。
> - **hash类型的底层实现**：当hash类型数据较多或成员较大时，底层结构会由ziplist/listpack变为dict。
> - **zset跳表结构的分数查找**：zset用dict保存成员到分数的映射。
>
> ---
>
> ## 3. dict的结构特点
>
> - redis的dict实现支持**渐进式rehash（渐进式扩容）**，避免一次性扩容对性能的影响。
> - 支持高效的O(1)平均时间复杂度的插入、查找和删除。
> - 通过链地址法（拉链法）解决哈希冲突。
>
> ---
>
> ## 4. 和普通Python字典的区别
>
> | 特点     | Redis dict   | Python dict  |
> | -------- | ------------ | ------------ |
> | 语言     | C            | Python       |
> | 内存管理 | 手动         | 自动         |
> | 扩容方式 | 渐进式rehash | 一次性resize |
> | 用途     | 底层实现     | 直接用       |
>
> ---
>
> ## 5. 举例
>
> - 假如你有一个 zset：`zadd myset 1 one 2 two`  
>   Redis会用一个dict存`one:1`和`two:2`的映射，用跳表存score排序。
> - 如果你有一个 hash：`hset myhash a 1 b 2`，数据多时底层用dict存储`a:1`和`b:2`。
>
> ---
>
> ### 总结
>
> **dict**是Redis底层用来存储键值对的哈希表结构，在很多Redis数据类型和功能中都有广泛应用，是Redis高性能的基础之一。

## Listpack/ziplist

> `ziplist` 和 `listpack` 都是 Redis 用于实现某些数据结构（如 list、zset、hash）的小型、节省内存的底层数据结构，但它们有如下区别：
>
> ---
>
> ## 1. 结构设计和用途
>
> - **ziplist**  
>   - 早期 Redis 版本（5.0 及以前）用的紧凑型内存结构。
>   - 用于 list、zset、hash 的底层实现（当元素较少、数据小的时候）。
>   - 结构是单向的，遍历时只能从头到尾顺序访问。
>
> - **listpack**  
>   - Redis 5.0 引入，Redis 7.0 后完全取代 ziplist。
>   - 主要用于 zset、hash 的底层实现（list 类型则用 quicklist+listpack，而不是直接 listpack）。
>   - 设计更现代，支持更多编码，效率更高。
>
> ---
>
> ## 2. 内存效率和性能
>
> - **ziplist**
>   - 每个节点存储前后长度信息，指针操作相对繁琐。
>   - 插入/删除操作效率较低，容易出现内存碎片。
>   - 存在“数据扩容溢出漏洞”，即 ziplist 溢出导致安全问题（已被发现并弃用）。
>
> - **listpack**
>   - 存储方式更紧凑，对整数和字符串有更高效的编码。
>   - 插入和遍历效率高于 ziplist，结构更简单。
>   - 安全性更高，解决了 ziplist 的安全隐患。
>
> ---
>
> ## 3. 兼容性
>
> - ziplist：**Redis 7.0 以后已删除**，不再支持。
> - listpack：**Redis 5.0 开始引入，7.0 以后全面替代 ziplist。**
>
> ---
>
> ## 4. 主要区别总结
>
> | 特点     | ziplist            | listpack               |
> | -------- | ------------------ | ---------------------- |
> | 引入版本 | Redis 2.6          | Redis 5.0              |
> | 适用类型 | list, zset, hash   | zset, hash, list(间接) |
> | 是否维护 | 已弃用（7.0 移除） | 仍在维护               |
> | 安全性   | 有溢出漏洞         | 更安全                 |
> | 编码效率 | 一般               | 更高                   |
> | 结构     | 单向，复杂         | 简化，紧凑             |
>
> ---
>
> ### 总结
>
> - **ziplist**：老一代结构，已弃用，安全性和效率都不如新方案。
> - **listpack**：现代替代品，编码更高效，安全性更好，现在是 Redis 推荐和应用的压缩型底层结构。
>
> 如需了解更底层的实现细节，可以查阅 Redis 官方文档或源码注释。