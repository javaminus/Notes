# Java 集合框架介绍

Java 集合框架 (Java Collections Framework) 是 Java 提供的用于存储和操作对象组的架构，它是 Java 编程中最常用、最基础的 API 之一。

## 1. 集合框架的层次结构

Java 集合框架主要分为两大部分：

- **Collection 接口**：单个元素的集合
- **Map 接口**：键值对的集合

```
java.util
 ├── Collection (interface)
 │    ├── List (interface)
 │    │    ├── ArrayList
 │    │    ├── LinkedList
 │    │    └── Vector
 │    │         └── Stack
 │    ├── Set (interface)
 │    │    ├── HashSet
 │    │    ├── LinkedHashSet
 │    │    └── TreeSet (SortedSet)
 │    └── Queue (interface)
 │         ├── PriorityQueue
 │         └── Deque (interface)
 │              ├── ArrayDeque
 │              └── LinkedList
 └── Map (interface)
      ├── HashMap
      ├── LinkedHashMap
      ├── TreeMap (SortedMap)
      ├── Hashtable
      └── Properties
```

## 2. 主要集合类型详解

### 2.1 List（有序、可重复）
- **ArrayList**：
  - 基于动态数组实现
  - 随机访问快 O(1)
  - 插入删除较慢 O(n)
  - 非线程安全

- **LinkedList**：
  - 基于双向链表实现
  - 随机访问慢 O(n)
  - 首尾插入删除快 O(1)
  - 实现了List和Deque接口

- **Vector**：
  - 类似ArrayList但线程安全
  - 性能较ArrayList差
  - 已被Collections.synchronizedList()取代

### 2.2 Set（不允许重复）
- **HashSet**：
  - 基于HashMap实现
  - 无序，不保证迭代顺序
  - 快速的添加、删除、查找 O(1)

- **LinkedHashSet**：
  - 基于LinkedHashMap实现
  - 维护插入顺序
  - 比HashSet慢一点，但迭代更快

- **TreeSet**：
  - 基于TreeMap（红黑树）实现
  - 元素自然排序或自定义比较器排序
  - 操作复杂度为O(log n)

### 2.3 Queue/Deque（队列）
- **PriorityQueue**：
  - 基于优先堆实现的优先队列
  - 元素按优先级出队，而非先进先出

- **ArrayDeque**：
  - 基于可调整大小的数组
  - 比LinkedList更高效的双端队列实现
  - 可用作栈或队列

### 2.4 Map（键值对映射）
- **HashMap**：
  - 基于哈希表实现
  - 快速查找、插入、删除 O(1)
  - 允许null键和值
  - JDK 1.8后链表长度>8转红黑树

- **LinkedHashMap**：
  - 维护插入顺序或访问顺序
  - 比HashMap慢一点，但迭代更快

- **TreeMap**：
  - 基于红黑树实现
  - 键按自然顺序或比较器排序
  - 操作复杂度为O(log n)

- **Hashtable**：
  - 类似HashMap但线程安全
  - 不允许null键和值
  - 已被ConcurrentHashMap取代

## 3. 并发集合

Java提供了专门的并发集合实现：

- **ConcurrentHashMap**：线程安全的HashMap
- **CopyOnWriteArrayList**：线程安全的ArrayList
- **CopyOnWriteArraySet**：线程安全的Set
- **ConcurrentLinkedQueue**：线程安全的队列

## 4. 集合工具类

- **Collections**：提供对集合操作的静态方法
  - 排序：`sort()`
  - 查找：`binarySearch()`
  - 混排：`shuffle()`
  - 最大/最小值：`max()`/`min()`
  - 同步包装：`synchronizedXxx()`
  - 不可修改包装：`unmodifiableXxx()`

## 5. 选择合适的集合

| 需求                | 推荐集合                    |
| ------------------- | --------------------------- |
| 快速随机访问        | ArrayList                   |
| 频繁在两端添加/删除 | LinkedList或ArrayDeque      |
| 唯一元素集合        | HashSet                     |
| 有序不重复集合      | TreeSet                     |
| 键值映射，快速查找  | HashMap                     |
| 有序键值映射        | TreeMap                     |
| 保持插入顺序        | LinkedHashMap/LinkedHashSet |
| 线程安全需求        | ConcurrentHashMap等并发集合 |

通过合理选择集合类型，可以极大地提高程序的性能和可维护性。

> 在 Java 中，将集合转换为线程安全集合的常用操作是通过 **`Collections.synchronizedXXX()`** 方法。  
> 这些是 `java.util.Collections` 工具类提供的静态方法，可以把普通集合包装成线程安全的集合。
>
> ## 常用方法如下：
>
> - **List：**
>   ```java
>   List<T> syncList = Collections.synchronizedList(list);
>   ```
> - **Set：**
>   ```java
>   Set<T> syncSet = Collections.synchronizedSet(set);
>   ```
> - **Map：**
>   ```java
>   Map<K, V> syncMap = Collections.synchronizedMap(map);
>   ```
> - **SortedMap：**
>   ```java
>   SortedMap<K,V> syncSortedMap = Collections.synchronizedSortedMap(sortedMap);
>   ```
> - **Collection：**
>   ```java
>   Collection<T> syncCollection = Collections.synchronizedCollection(collection);
>   ```
>
> ## 示例代码
> ```java
> List<String> list = new ArrayList<>();
> List<String> syncList = Collections.synchronizedList(list);
> ```
>
> ## 注意事项
>
> - 这些方法返回的是原集合的**线程安全包装类**，内部通过`synchronized`关键字实现同步。
> - 在多线程环境下，**遍历包装后的集合时，仍需手动同步**，因为Collections.synchronizedXXX()只提供安全的add、remove、get等方法，但是并不能保证遍历操作的安全。
>   ```java
>   synchronized(syncList) {
>       for(String item : syncList) {
>           // 线程安全遍历
>       }
>   }
>   
>   为什么需要这一步？
>   这是因为 Collections.synchronizedXXX() 方法虽然为集合的基本操作（如add、remove、get等）提供了线程安全的包装，但并不能保证在遍历（迭代）集合时的线程安全。
>   ```
>
> ---
>
> **总结：**  
> Java中将集合转换为线程安全集合的操作是使用`Collections.synchronizedXXX()`系列方法。
>
> # 那是在set方法里面直接加的synchronized吗？ 
>
> 不是的，`Collections.synchronizedXXX()` 并不是在集合的 `set` 方法（比如 `add()`、`set()`、`put()` 等）内部直接加的 `synchronized` 关键字。  
> 而是**通过为集合包装一个同步代理类**，这个代理类在每个方法（包括 `set`、`add`、`put`、`remove`、`get` 等）内部统一加锁实现的。
>
> ### 工作原理
>
> 以 `synchronizedList` 为例，源码大致结构如下（简化版）：
>
> ```java
> public class SynchronizedList<E> implements List<E> {
>     final List<E> list;  // 原始集合
>     final Object mutex;  // 同步锁对象
> 
>     public SynchronizedList(List<E> list) {
>         this.list = list;
>         this.mutex = this;
>     }
> 
>     public synchronized boolean add(E e) {
>         synchronized (mutex) {
>             return list.add(e);
>         }
>     }
> 
>     public synchronized E get(int index) {
>         synchronized (mutex) {
>             return list.get(index);
>         }
>     }
> 
>     // 其他方法类似，都用 synchronized (mutex) 包裹
> }
> ```
>
> 实际实现中，**所有对集合的操作方法都被 `synchronized (mutex)` 包裹**，而不是只给 `set()` 或 `add()` 单独加锁。
>
> ### 结论
>
> - 不是只在 `set()` 方法里加 `synchronized`，而是**代理类的所有方法内部都加了同步锁**。
> - 这种方式保证了对集合的所有操作（增删改查）都具备原子性和线程安全。
>
> ---
>
> **补充：**  
> 遍历（如 `iterator()`）返回的只是普通迭代器，所以遍历时仍需手动加锁，否则存在并发问题。