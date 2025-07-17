好的，这里给你详细介绍一下 Spring 的 `@Scheduled` 注解的**实现原理、用法**，以及相关**源码分析**，适合面试答题或深入理解。

---

## 一、用法简介

`@Scheduled` 是 Spring 提供的定时任务注解，可以标注在方法上，定时自动执行。常见用法如下：

```java
@Component
public class MyTask {

    // 每5秒执行一次
    @Scheduled(fixedRate = 5000)
    public void executeTask() {
        System.out.println("Task executed!");
    }
}
```

常用参数有：
- `fixedRate`：按固定速率执行（上次开始到下次开始的间隔）
- `fixedDelay`：按固定延迟执行（上次结束到下次开始的间隔）
- `cron`：按 cron 表达式执行

---

## 二、实现原理

### 1. 核心流程

- Spring 会在启动时扫描所有 `@Scheduled` 注解的方法。
- 然后通过调度器（默认是 `ThreadPoolTaskScheduler`）定期触发这些方法执行。

### 2. 关键源码

#### 2.1. 注解声明

源码位置：`org.springframework.scheduling.annotation.Scheduled`

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scheduled {
    ...
}
```

#### 2.2. 解析注解

Spring 在启动阶段会用 `ScheduledAnnotationBeanPostProcessor` 扫描所有 Bean，查找标注了 `@Scheduled` 的方法。

源码位置：`org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor`

核心方法：
```java
public void postProcessAfterInitialization(Object bean, String beanName) {
    ...
    // 找到所有 @Scheduled 的方法
    Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(bean.getClass());
    for (Method method : methods) {
        if (method.isAnnotationPresent(Scheduled.class)) {
            processScheduled(bean, beanName, method);
        }
    }
}
```

#### 2.3. 注册任务

- 解析注解参数（fixedRate、cron等）
- 把方法封装成 `Runnable`
- 注册到 `ScheduledTaskRegistrar`
- 由 `ThreadPoolTaskScheduler` 线程池执行

关键调用链：

1. `ScheduledAnnotationBeanPostProcessor` 负责扫描和注册
2. `ScheduledTaskRegistrar` 负责管理任务
3. `ThreadPoolTaskScheduler` 负责调度任务

简化流程图：
```
Spring启动
  ↓
ScheduledAnnotationBeanPostProcessor扫描Bean
  ↓
发现@Scheduled方法，解析参数
  ↓
把方法封装为Runnable任务
  ↓
注册到ScheduledTaskRegistrar
  ↓
ThreadPoolTaskScheduler定时执行任务
```

---

## 三、经典源码片段分析

### 1. 扫描和处理

```java
// ScheduledAnnotationBeanPostProcessor.java
private void processScheduled(Object bean, String beanName, Method method) {
    Scheduled scheduled = method.getAnnotation(Scheduled.class);
    Runnable task = new ScheduledMethodRunnable(bean, method);
    // 解析参数并注册任务
    this.registrar.scheduleCronTask(new CronTask(task, scheduled.cron()));
    // 其他参数类似
}
```

### 2. 任务执行

```java
// ThreadPoolTaskScheduler.java
public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
    return this.scheduledExecutor.schedule(new ReschedulingRunnable(task, trigger, ...), ...);
}
```

`ReschedulingRunnable` 会在任务执行后重新安排下次执行。

---

## 四、注意事项

- `@Scheduled` 方法**不能有参数**，且必须是 `public`。
- 需要在配置类加 `@EnableScheduling` 开启定时任务功能。
- 默认线程池大小为 1，可通过配置自定义。

---

## 五、面试简洁回答

> Spring 的 `@Scheduled` 注解通过 `ScheduledAnnotationBeanPostProcessor` 在启动时扫描所有 Bean，解析标注的方法和参数，然后把方法注册为定时任务，交给 `ThreadPoolTaskScheduler` 线程池按指定的周期自动执行。底层核心流程包括注解解析、任务注册和线程池调度，源码主要分布在 `ScheduledAnnotationBeanPostProcessor` 和 `ScheduledTaskRegistrar` 等类。

---

如需详细源码某一部分或补充 Cron 表达式用法等，欢迎继续提问！