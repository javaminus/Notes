下面是 **Timer/TimerTask**、**ScheduledExecutorService**、**DelayQueue** 三种定时任务方式的优缺点及应用场景总结：

---

### 1. Timer / TimerTask

**优点：**
- 使用简单，API易于理解。
- 支持定时和周期性任务。

**缺点：**
- 只用单线程，所有任务串行执行，任务间互相影响。
- 如果某个任务抛出异常，会导致整个 Timer 线程终止，其他任务不再执行。
- 精度较低，受系统时钟影响，不能保证高精度。
- 不支持任务并发执行。

**应用场景：**
- 适合简单、少量、对精度和并发要求不高的定时任务。
- 适合单线程环境或历史项目代码维护。

---

### 2. ScheduledExecutorService

**优点：**
- 基于线程池，支持任务并发执行，互不影响。
- 任务异常不会影响其他任务。
- 支持更丰富的调度方式（延迟/固定速率/固定延迟）。
- 精度更高，管理更灵活。

**缺点：**
- 相较 Timer，使用略复杂。
- 需要合理配置线程池，否则可能出现资源竞争或线程泄漏。

**应用场景：**
- 绝大多数需要高并发、高可靠性的定时任务场景。
- 业务系统定时调度、周期性执行任务、定时检查等。

```java
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskExample {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            // 你的任务逻辑
            System.out.println("任务执行：" + System.currentTimeMillis());
        };

        // 延迟0秒开始，每隔20分钟执行一次
        scheduler.scheduleAtFixedRate(task, 0, 20, TimeUnit.MINUTES);

        // 如果你想让主线程一直不退出，可以阻塞等待
        // new CountDownLatch(1).await();
    }
}
```



---

### 3. DelayQueue

**优点：**
- 支持任务按任意延迟时间调度，非常灵活。
- 任务可以动态插入，延迟时间可定制。
- 适合延迟队列、延迟消息、定时处理。

**缺点：**
- 只能按延迟时间执行一次任务，不支持周期性任务（需自行实现）。
- 需要自己实现任务取出和执行逻辑。
- 不自带线程池，需结合 Executor 组件。

**应用场景：**
- 消息延迟投递、订单超时处理、缓存过期、分布式系统中的延迟任务。
- 需要“某个时间点执行一次”而非周期性任务的场景。

---

**总结：**
- 日常推荐使用 **ScheduledExecutorService**，功能强大，适合绝大多数定时任务。
- **Timer** 适合简单场景，但已逐步被 ScheduledExecutorService 替代。
- **DelayQueue** 用于延迟队列、定时一次性任务或需要灵活延迟的场景。 

以上几种方案，相比于xxl-job这种定时任务调度框架来说，他实现起来简单，不须要依赖第三方的调度框架和类库。方案更加轻量级。  

当然这个方案也不是没有缺点的，首先，以上方案都是**基于JVM内存**的，需要把定时任务提前放进去，那如果数据量太大的话，可能会导致**OOM的问题**；另外，基于JVM内存的方案，**一旦机器重启了，里面的数据就都没有了**，所以一般都需要配合数据库的持久化一起用，并且在应用启动的时候也需要做重新加载。  

还有就是，现在很多应用都是集群部署的，那么集群中多个实例上的多个任务如何配合是一个很大的问题。 



# Problem

下面分别用 **Timer/TimerTask**、**ScheduledExecutorService** 和 **DelayQueue** 三种方式实现你的需求：  
“有一个静态变量，如果该变量最近20分钟没有变化，则执行定时任务。”

假定静态变量是 `volatile static int value`，每次变化时调用 `updateValue()` 方法，记录最新修改时间。定时器每隔一分钟检查一次，如果距离上次修改时间超过20分钟，则执行任务。

---

## 1. Timer/TimerTask 实现

> - 每个`Timer`实例内部会初始化一个`TimerThread`线程（继承自`Thread`类）
> - 构造方法调用时会自动启动该线程（通过`thread.start()`）
> - 线程名称可通过参数指定（如示例中的"20MinTimer"）

```java

import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class TwentyMinuteTimer {
    public static void main(String[] args) {
        Timer timer = new Timer("20MinTimer"); 
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("定时任务触发时间: " + new Date());
                // 这里添加你的业务逻辑
            }
        };
        
        // 立即执行第一次，之后每20分钟(1200000毫秒)执行一次
        timer.scheduleAtFixedRate(task, 0, 20 * 60 * 1000);
        
        System.out.println("定时器已启动，首次执行立即开始...");
    }
}

```

---

## 2. ScheduledExecutorService 实现

```java
import java.util.concurrent.Executors; // 注意这里是JUC的包
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskExample {
	public static void main(String[] args) {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		Runnable task = () -> {
			// 你的任务逻辑
			System.out.println("任务执行：" + System.currentTimeMillis());
		};

		// 延迟0秒开始，每隔20分钟执行一次，核心方法scheduleAtFixedRate(task, 0, 20, TimeUnit.MINUTES)
		scheduler.scheduleAtFixedRate(task, 0, 20, TimeUnit.MINUTES);
	}
}
```

---

## 3. DelayQueue 实现

```java
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayQueueCheckDemo {
    private static volatile int value = 0;
    private static volatile long lastUpdateTime = System.currentTimeMillis();

    // 每次修改时重启延迟任务
    private static final DelayQueue<DelayedTask> queue = new DelayQueue<>();
    private static Thread worker;

    public static void updateValue(int newValue) {
        value = newValue;
        lastUpdateTime = System.currentTimeMillis();
        // 清空队列，重新加入任务
        queue.clear();
        queue.put(new DelayedTask(20 * 60 * 1000));
    }

    static class DelayedTask implements Delayed {
        private final long executeTime;

        public DelayedTask(long delayMillis) {
            this.executeTime = System.currentTimeMillis() + delayMillis;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = executeTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.executeTime, ((DelayedTask) o).executeTime);
        }
    }

    public static void main(String[] args) {
        // 启动工作线程
        worker = new Thread(() -> {
            while (true) {
                try {
                    queue.take(); // 阻塞直到有任务到期
                    if (System.currentTimeMillis() - lastUpdateTime >= 20 * 60 * 1000) {
                        System.out.println("Value unchanged for 20 minutes, executing task!");
                        // 执行你的定时任务
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        worker.setDaemon(true);
        worker.start();

        // 初始化第一次任务
        queue.put(new DelayedTask(20 * 60 * 1000));
    }
}
```

---

**核心思路**：  
- 定期检查静态变量的更新时间，超时则执行任务。  
- DelayQueue 方案每次变量变化就重建延迟任务，只有延迟期间无变化才执行。  
- 以上代码均为最简逻辑，可根据实际需求扩展和异常处理。