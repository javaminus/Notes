# ☕ Java秋招高频场景题 5  
## 场景题：如何排查和解决Java线上系统的内存泄漏问题？

---

### 🧩 **场景描述**  
你负责的微服务系统长时间运行后内存不断上涨，最终触发了频繁的Full GC甚至`OutOfMemoryError`。面试官让你分析排查思路、常见漏点、定位与优化方法。

---

### 🎯 **核心考点**

- JVM内存结构与GC机制
- 常见内存泄漏场景
- 分析与排查工具（如MAT、jmap、VisualVM）
- 代码层面的优化实践

---

### 🛠️ **详细拆解与答题要点**

#### 1. JVM内存结构&GC简要

| 区域             | 说明             |
| ---------------- | ---------------- |
| 🧒 堆内存         | 存放对象实例     |
| 📦 非堆（方法区） | 类元数据、常量等 |
| 🗄️ 本地方法栈     | JNI调用相关      |

- GC分代：新生代（Eden、Survivor）、老年代  
- Full GC频繁=老年代回收压力大，可能有内存泄漏

---

#### 2. 常见内存泄漏场景

| 场景                | 原因                        | 典型代码      |
| ------------------- | --------------------------- | ------------- |
| 长生命周期集合      | 静态Map/缓存未清理          | `static List` |
| 监听器/回调未注销   | 注册但未释放                |               |
| 线程池/定时任务     | 任务未结束/未移除           |               |
| 数据库连接未关闭    | ResultSet/Connection泄漏    |               |
| 不当使用ThreadLocal | 未remove导致ClassLoader泄漏 |               |

---

#### 3. 排查流程

1. **线上监控**  
   - 观察堆使用率、Full GC、OOM日志  
   - 工具：Prometheus、JMX、Arthas

2. **导出堆快照**  
   - `jmap -dump:format=b,file=heap.bin <pid>`

3. **分析堆快照**
   - 使用MAT（Eclipse Memory Analyzer）或VisualVM打开
   - 关注：大对象、泄漏疑点（如大Map、ClassLoader、ThreadLocal）

4. **定位代码问题**  
   - 结合MAT分析的GC Roots路径，追查未释放引用

---

#### 4. 代码优化与防范示例

```java
// 错误用法：静态集合未清理
private static final List<User> CACHE = new ArrayList<>();

// 正确用法：定期清理、弱引用、限制缓存大小
private static final Map<String, WeakReference<User>> CACHE = new WeakHashMap<>();
```

- 使用try-with-resources自动释放数据库/IO资源
- 使用线程池时，及时remove不再需要的任务
- ThreadLocal用完后必须remove

---

### 💡 **常见陷阱**

- 只关注对象未释放，忽略静态/线程相关泄漏
- 不会用分析工具，只靠日志排查
- 只重启不根治，导致问题复现

---

### 🏆 **口诀总结**

> **“堆溢出，先监控，导快照，MAT查根；集合静态要小心，ThreadLocal莫忘清。”**  
>
> - 监控→dump→分析→定位→优化，环环相扣

---

### 📊 **思维导图（文字版）**

```
内存泄漏排查
├── 监控指标（堆、GC、OOM）
├── 堆快照导出（jmap）
├── 工具分析（MAT/VisualVM）
├── 常见场景
│   ├── 静态集合
│   ├── 监听器
│   ├── 线程池
│   ├── ThreadLocal
│   └── 数据库/IO未关闭
└── 代码优化
    └── WeakReference、try-with-resources、及时remove
```

---

> 🔖 **面试官追问Tip：**  
> - “如何让缓存既避免内存泄漏又有命中率？”（答：使用`WeakHashMap`、定期清理、设置容量上限等）
> - “Full GC频繁但对象没泄漏怎么办？”（分析老年代大对象分配、参数调优）