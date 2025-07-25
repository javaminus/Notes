## 问题：Redis BitMap 和 HyperLogLog 的原理是什么？分别适合哪些实际应用场景？

### 详细解释（结合场景，含通俗例子）

#### 1. BitMap

**原理：**  
BitMap 是用二进制位（bit）来高效存储和统计大量布尔信息的数据结构，本质上是一个超长的位数组。每个 bit 只能是 0 或 1，占用极少内存。

**典型应用场景：**
- **用户签到统计**：每天用一个 bit 记录用户是否签到，1 代表已签到，0 代表未签到。
- **活跃用户统计**：用用户 ID 作为偏移量，统计某天哪些用户活跃。
- **去重统计**：大规模用户唯一行为的标记，如广告曝光、活动参与等。

**通俗例子：**
想象有一亿个用户，每人是否签到用一张纸上一个小格记录（bit）。用 BitMap 只需几 MB 内存即可实现签到情况查询与统计，非常高效。

---

#### 2. HyperLogLog

**原理：**  
HyperLogLog 是一种基于概率算法的数据结构，用于近似统计海量数据的基数（即去重后元素的数量），但只需极小内存（通常 12KB 左右）。

**典型应用场景：**
- **独立访客数（UV）统计**：统计网站/APP每日独立访问用户数。
- **大规模数据去重计数**：如广告曝光去重、独立 IP 统计。
- **日志分析**：高频、高并发数据流中的去重场景。

**通俗例子：**
你要统计一年内全国所有进出商场的人数，传统方式要记住每个人信息，内存爆炸。HyperLogLog 用概率算法“采样”特征，能用很小空间近似估算人数，误差极小。

---

### 总结性回答（复习提示）

- **BitMap 适合大规模布尔统计（如签到、活跃统计），节省空间，支持位运算。**
- **HyperLogLog 适合大规模去重计数（如UV统计），空间极小但有一定误差。**
- **二者都是 Redis 的“以空间换效率”的典型高阶数据结构，适用于高并发大数据量的统计场景。**

Redis 的 bitmap 和 HyperLogLog 都属于特殊的数据结构，各自有一些常用的操作方法（命令）。下面分别介绍：

---

## 1. Bitmap 常用方法

**Bitmap** 实际上是用字符串类型（String）来按位存储数据，可以高效实现布尔类型的集合（如签到、活跃统计等）。

### 常用命令

- `SETBIT key offset value`  
  设置指定 key 的某一位（offset）为 0 或 1。
  ```shell
  SETBIT usersign 1001 1   # 设置第1001位为1
  ```

- `GETBIT key offset`  
  获取指定 key 的某一位的值（0或1）。
  ```shell
  GETBIT usersign 1001
  ```

- `BITCOUNT key [start end]`  
  统计 key 中值为 1 的 bit 数（可选范围）。
  ```shell
  BITCOUNT usersign
  ```

- `BITOP operation destkey key1 [key2 ...]`  
  对一个或多个 bitmap 做位运算（AND, OR, XOR, NOT）。
  ```shell
  BITOP OR allusersign usersign1 usersign2
  ```

- `BITPOS key bit [start] [end]`  
  返回第一个等于指定 bit（0或1）的位置。
  ```shell
  BITPOS usersign 1
  ```

---

## 2. HyperLogLog 常用方法

**HyperLogLog** 是一种概率数据结构，用于基数（去重计数）统计，能高效统计不同元素的数量，占用内存极小。

### 常用命令

- `PFADD key element [element ...]`  
  向 HyperLogLog 添加元素。
  ```shell
  PFADD uv:20250723 user1 user2 user3
  ```

- `PFCOUNT key [key ...]`  
  返回 HyperLogLog 估算的不同元素数量（基数）。
  ```shell
  PFCOUNT uv:20250723
  ```

- `PFMERGE destkey sourcekey [sourcekey ...]`  
  合并多个 HyperLogLog，结果存到 destkey。
  ```shell
  PFMERGE uv:total uv:20250723 uv:20250724
  ```

---

## 总结表

| 数据结构    | 常用命令      | 作用                  |
| ----------- | ------------- | --------------------- |
| Bitmap      | SETBIT/GETBIT | 设置/获取某一位       |
|             | BITCOUNT      | 统计 1 的个数         |
|             | BITOP         | 位运算                |
|             | BITPOS        | 查找第一个 0/1 的位置 |
| HyperLogLog | PFADD         | 添加元素              |
|             | PFCOUNT       | 统计去重后元素数量    |
|             | PFMERGE       | 合并多个 HyperLogLog  |

如需具体用法示例或者实际场景，可以继续提问！

