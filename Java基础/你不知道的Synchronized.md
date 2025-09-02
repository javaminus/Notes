# Java 同步锁（synchronized）详解

## 一、synchronized 的四种使用方式

### 1. 修饰实例方法

```java
public synchronized void instanceMethod() {
    // 方法体
}
```

- **锁对象**：当前实例对象（this）
- **锁范围**：整个方法体
- **特点**：不同实例之间互不影响，同一实例的多线程调用该方法会被同步

### 2. 修饰静态方法

```java
public static synchronized void staticMethod() {
    // 方法体
}
```

- **锁对象**：类的 Class 对象（如 MyClass.class）
- **锁范围**：整个静态方法体
- **特点**：全局锁，同一个类的所有实例共享此锁

### 3. 修饰代码块（使用this）

```java
public void method() {
    // 非同步代码
    
    synchronized(this) {
        // 同步代码块
    }
    
    // 非同步代码
}
```

- **锁对象**：当前实例对象（this）
- **锁范围**：仅同步代码块内的内容
- **特点**：比修饰实例方法更细粒度，可优化性能

### 4. 修饰代码块（使用其他对象）

```java
private final Object lock = new Object(); // 专用锁对象

public void method() {
    synchronized(lock) { // 或任何其他对象引用
        // 同步代码块
    }
}
```

- **锁对象**：指定的对象（如 lock、Class 对象等）
- **锁范围**：仅同步代码块内的内容
- **特点**：最灵活的锁定方式，可以使用不同对象控制不同的同步区域

## 二、static synchronized 与 synchronized 的详细对比

| 特性             | static synchronized    | synchronized 实例方法    |
| ---------------- | ---------------------- | ------------------------ |
| 锁对象           | 类的 Class 对象        | 实例对象 (this)          |
| 锁粒度           | 类级别（所有实例共享） | 对象级别（每个实例独立） |
| 多实例影响       | 所有实例互相影响       | 不同实例互不影响         |
| 无实例时能否使用 | 可以（通过类名调用）   | 不可以（需要实例）       |
| 应用场景         | 保护静态变量/资源      | 保护实例变量/资源        |

## 三、锁的作用范围示例

### 场景 1: 实例方法锁

```java
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
}
```

- Counter 实例 A 的线程和实例 B 的线程互不影响
- 仅当同一个 Counter 实例被多线程访问时才会产生同步效果

### 场景 2: 静态方法锁

```java
class Counter {
    private static int globalCount = 0;
    
    public static synchronized void incrementGlobal() {
        globalCount++;
    }
}
```

- 无论哪个线程、通过哪个 Counter 实例调用，都将同步
- 即使没有 Counter 实例，通过 `Counter.incrementGlobal()` 调用也会被同步

### 场景 3: 混合使用不同锁

```java
class MixedCounter {
    private int instanceCount = 0;
    private static int classCount = 0;
    
    // 锁实例
    public synchronized void incrementInstance() {
        instanceCount++;
    }
    
    // 锁类
    public static synchronized void incrementClass() {
        classCount++;
    }
    
    // 锁实例内的特定对象
    private final Object lock = new Object();
    public void incrementWithLock() {
        synchronized(lock) {
            instanceCount++;
        }
    }
    
    // 锁类对象
    public void incrementWithClassLock() {
        synchronized(MixedCounter.class) {
            classCount++;
        }
    }
}
```

## 四、关键注意事项

1. **静态方法与实例方法的锁互不干扰**
   - 一个线程进入 static synchronized 方法
   - 另一个线程仍可以进入同一个对象的 synchronized 实例方法
   - 因为它们锁的是不同对象（类对象 vs 实例对象）

2. **锁的颗粒度**
   - 方法级锁：锁定整个方法，颗粒度较粗
   - 代码块锁：可以只锁定关键代码，颗粒度更细，性能更好

3. **死锁风险**
   - 使用多个锁时，注意获取顺序一致性，避免死锁

4. **非线程安全类的同步**
   - 对于如 ArrayList、HashMap 等非线程安全类，可以通过 synchronized 代码块保护访问

## 五、代码完整示例

```java
public class SynchronizationExample {
    private int instanceValue = 0;
    private static int staticValue = 0;
    private final Object lockA = new Object();
    private final Object lockB = new Object();
    
    // 1. 实例方法锁
    public synchronized void instanceMethod() {
        instanceValue++;
        System.out.println("Instance method: " + instanceValue);
    }
    
    // 2. 静态方法锁
    public static synchronized void staticMethod() {
        staticValue++;
        System.out.println("Static method: " + staticValue);
    }
    
    // 3. this锁代码块
    public void thisLockMethod() {
        // 非同步代码
        System.out.println("Non-synchronized part");
        
        synchronized(this) {
            instanceValue++;
            System.out.println("This-locked block: " + instanceValue);
        }
    }
    
    // 4. 对象锁代码块
    public void objectLockMethod() {
        synchronized(lockA) {
            System.out.println("Lock A section");
            // 只有获得lockA的线程才能执行
        }
        
        synchronized(lockB) {
            System.out.println("Lock B section");
            // 只有获得lockB的线程才能执行
        }
    }
    
    // 5. 类对象锁代码块
    public void classLockMethod() {
        synchronized(SynchronizationExample.class) {
            staticValue++;
            System.out.println("Class-locked block: " + staticValue);
        }
    }
}
```

希望这个详细的解释能帮助你理解 Java 中 synchronized 的所有使用方式和锁的范围！