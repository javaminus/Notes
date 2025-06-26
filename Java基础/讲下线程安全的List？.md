线程安全的 List 是指在多个线程同时访问和修改列表时，能够保证数据一致性、不发生竞态条件的 List 实现。在 Java 中，如果你在多线程环境中使用 List，就需要特别注意线程安全问题。

------

### 一、常见线程安全的 List 实现

| 类型                                 | 特点                                   | 适用场景                     |
| ------------------------------------ | -------------------------------------- | ---------------------------- |
| `Vector`                             | 方法同步（synchronized），性能较差     | 老旧但仍可用，轻量多线程环境 |
| `Collections.synchronizedList(List)` | 对普通 List 进行包装，加锁实现线程安全 | 推荐使用，但需要手动同步遍历 |
| `CopyOnWriteArrayList`               | 读写分离，写时复制，读无需加锁         | 读多写少的高并发场景         |

------

### 二、三种线程安全 List 简要说明

#### 1. `Vector`

- 所有方法都加了 `synchronized`。
- 缺点：性能较低，因为即使是读操作也会加锁。

```
List<String> list = new Vector<>();
```

#### 2. `Collections.synchronizedList`

- 通过包装普通的 List 实现线程安全：

```
List<String> list = Collections.synchronizedList(new ArrayList<>());
```

- **注意**：在使用迭代器遍历时需要手动加锁：

```
synchronized (list) {
    for (String s : list) {
        // 操作 s
    }
}
```

#### 3. `CopyOnWriteArrayList`

- 来自 `java.util.concurrent`，底层在写入时复制数组。
- 特点：
  - 读操作无锁，非常快。
  - 写操作会复制一份新数组，代价较高。
- 适合 **读多写少** 的场景，如缓存、事件监听器等。

```
List<String> list = new CopyOnWriteArrayList<>();
```

------

### 三、线程不安全的 List 示例

- `ArrayList` 是非线程安全的，在多线程环境中可能出现：
  - 数据丢失
  - 越界异常（`IndexOutOfBoundsException`）
  - 并发修改异常（`ConcurrentModificationException`）

------

### 四、如何选择

| 场景             | 推荐                           |
| ---------------- | ------------------------------ |
| 单线程           | `ArrayList`                    |
| 多线程，读写频繁 | `Collections.synchronizedList` |
| 多线程，读多写少 | `CopyOnWriteArrayList`         |

------

如果你想我讲讲具体实现原理或场景举例，也可以告诉我。