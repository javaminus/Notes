### **`synchronized` 是否支持重入？如何实现的？**  

#### **1. `synchronized` 支持重入**
` synchronized` 是**可重入锁（Reentrant Lock）**，即**同一线程**在获取锁后，**可以再次获取该锁而不会被阻塞**。  

#### **2. `synchronized` 如何实现重入？**
- **底层依赖** **对象头（Mark Word）** 里的**锁计数器（recursion count）**。  
- 当**同一线程**多次进入 `synchronized` 代码块时：
  1. **锁计数器 +1**  
  2. 线程退出 `synchronized` 代码块时，**锁计数器 -1**  
  3. **计数器归零时**，锁才会真正释放。

#### **3. 示例：**
```java
class ReentrantLockExample {
    public synchronized void method1() {
        System.out.println(Thread.currentThread().getName() + " - method1");
        method2(); // 同一线程可再次获取锁
    }

    public synchronized void method2() {
        System.out.println(Thread.currentThread().getName() + " - method2");
    }

    public static void main(String[] args) {
        ReentrantLockExample example = new ReentrantLockExample();
        new Thread(example::method1).start();
    }
}
```
**输出：**
```
Thread-0 - method1
Thread-0 - method2
```
**说明：**
- `method1()` 持有锁，调用 `method2()` 时不会被阻塞。
- **锁计数器递增，退出时递减**，保证锁的释放。

#### **4. `synchronized` 重入的应用场景**
- **子类继承父类的 `synchronized` 方法**
- **递归调用 `synchronized` 方法**
- **同一线程嵌套调用多个 `synchronized` 方法**

---

### **总结**
✔ **`synchronized` 支持重入**，同一线程可多次获取同一把锁。  
✔ **通过对象头的“锁计数器”实现**，锁被同一线程持有时计数递增，释放时递减。  
✔ **避免死锁**，允许父子类方法或递归调用顺利执行。 🚀