Java 中常用的 List 扩容机制，主要指的是 `ArrayList`（因为 `LinkedList` 本质不扩容）。下面详细说明：

---

## 1. **ArrayList 扩容机制**

### a. **初始容量**
- 默认初始容量为 10（无参构造）。

### b. **扩容触发时机**
- 当元素个数超出当前数组长度时（即，add 时容量不足），触发扩容。

### c. **扩容策略（JDK 8 及以后）**
- 新容量 = 原容量的 1.5 倍（即：`newCapacity = oldCapacity + (oldCapacity >> 1)`）。
- 如果新容量还不够，则直接用需要的容量。

### d. **扩容过程**
1. 申请更大的数组。
2. 将原数组元素复制到新数组。
3. 用新数组替换原数组。

### e. **源码片段**
```java
// ArrayList.java (JDK 8)
private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity < minCapacity)
        newCapacity = minCapacity;
    // 省略最大容量判断
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

---

## 2. **扩容注意事项**

- **扩容成本高**：每次扩容需要复制所有元素，性能消耗大。
- **建议预估容量**：构造时指定初始容量，减少扩容次数。
- **线程安全性**：ArrayList 扩容过程非线程安全，多线程需用 `Collections.synchronizedList` 或 `CopyOnWriteArrayList`。

---

## 3. **LinkedList 不扩容**
- LinkedList 是链表结构，不需要扩容。
- 每次 add 新节点只需分配一个节点对象。

---

## 4. **其他 List 实现**

- **CopyOnWriteArrayList**：底层也是数组，会有扩容机制，原理类似 ArrayList，但每次写操作都复制整个数组，适用于读多写少场景。

---

## **结论**

- ArrayList 扩容按 1.5 倍增长，性能消耗大，建议预估容量。
- LinkedList 无需扩容。
- 多线程环境注意安全性。

如需更多源码分析或特殊场景优化建议，可继续提问！