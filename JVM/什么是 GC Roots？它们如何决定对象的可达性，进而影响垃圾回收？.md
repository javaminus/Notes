## 问题：什么是 GC Roots？它们如何决定对象的可达性，进而影响垃圾回收？

### 详细解释（结合场景 & 通俗例子）

在 HotSpot JVM 中，垃圾回收（GC）要判断哪些对象需要回收，就要从一组“根”对象（GC Roots）出发，沿着引用链搜索所有可达对象，剩下的对象就是“不可达”的，可以安全回收。

#### 1. 常见的 GC Roots 类型  
- **虚拟机栈（Stack）中引用**  
  - 每个线程的局部变量表里，对象引用都算一条根。  
  - 例：方法 `foo()` 里有 `String s = "hello";`，`s` 就是一个 Root。  
- **方法区（Metaspace）中的类静态属性**  
  - 类的 `static` 字段引用的对象。  
  - 例：`static List<User> cache = new ArrayList<>();`，`cache` 始终可达。  
- **方法区中的常量引用**  
  - 字符串字面量和被 `final` 修饰的常量。  
- **JNI（Native）引用**  
  - 本地方法（C/C++）中通过 JNI 持有的对象引用。  
- **活跃的线程 & 系统类加载器**  
  - 线程对象本身和类加载器对象也看作 Roots。

#### 2. 可达性分析算法（图搜索）  
1. 从所有 GC Roots 出发，将它们加入一个「待处理队列」。  
2. 取出队列中的一个对象，标记为“可达”，然后将它所有的引用（字段或数组元素）加入队列。  
3. 重复直到队列空。  
4. 未被标记的对象视为“不可达”，GC 回收它们的内存。

#### 3. 场景示例：静态缓存导致内存泄漏  
```java
public class UserManager {
    // 静态集合永远属于 GC Roots
    private static List<User> allUsers = new ArrayList<>();

    public static void add(User u) {
        allUsers.add(u);
    }
}

// elsewhere
for (int i = 0; i < 100_000; i++) {
    UserManager.add(new User("user" + i));
}
// 此刻 allUsers 对象链永远可达，100_000 个 User 对象无法回收 → 内存泄漏
```

在上面例子中，`allUsers` 属于 GC Roots，导致它指向的 `User` 对象也始终可达，GC 不会回收它们。

---

### 总结性的回答（复习提示词）

- **GC Roots**：栈引用、静态属性、常量、JNI、活跃线程  
- **可达性**：从 Roots 出发的引用链标记算法  
- **影响**：只要可达就不回收 → 静态缓存/单例易泄漏  
- **提示口诀**：  
  `“栈、静、常、JNI、线程 五大 Root → 图搜标记可达 → 不可达即回收”`  