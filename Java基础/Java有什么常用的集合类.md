Java常用的集合类主要来自**java.util包**，分为三大类：**List、Set、Map**，还有一些辅助类和队列类。下面是常见集合类简介：

---

## 1. List（有序、可重复）

- **ArrayList**：基于动态数组，查询快，插入删除慢，常用。
- **LinkedList**：基于链表结构，插入删除快，查询慢，可做队列、栈等。
- **Vector**：早期版本，线程安全，基本已被 ArrayList 取代。

---

## 2. Set（无序、不重复）

- **HashSet**：基于哈希表，元素唯一、无序，常用。
- **LinkedHashSet**：有序的 HashSet，保持插入顺序。
- **TreeSet**：基于红黑树，元素唯一，自动排序。

---

## 3. Map（键值对，无序，键唯一）

- **HashMap**：基于哈希表，键值对存储，键唯一，常用。
- **LinkedHashMap**：有序的 HashMap，保持插入顺序。
- **TreeMap**：基于红黑树，键自动排序。
- **Hashtable**：线程安全的哈希表，基本已被 HashMap 取代。
- **ConcurrentHashMap**：支持并发访问，线程安全。

---

## 4. Queue（队列）

- **PriorityQueue**：优先队列，自动排序。
- **ArrayDeque**：双端队列，可作栈或队列。

---

## 5. 辅助类

- **Collections**：集合工具类，静态方法如排序、线程安全包装等。
- **Arrays**：数组工具类，提供数组与集合互转等方法。

---

### 常见用法示例

```java
List<String> list = new ArrayList<>();
Set<Integer> set = new HashSet<>();
Map<String, Integer> map = new HashMap<>();
Queue<String> queue = new LinkedList<>();
```

---

如需某个集合类的详细用法或原理说明，欢迎继续提问！