在Kafka的批量消费中，经常会出现丢消息的情况，稍有不慎就会丢，甚至有时候你还不知道会丢，因为很多人没这个意识，不知道有这种可能。 

### 丢消息的情况 

首先**第一种**情况，就是当使用自动提交的时候，可能会丢消息。加入你的kafka中有以下配置： 

```java
enable.auto.commit=true
auto.commit.interval.ms=5000
```

这样配置表示每隔 5 秒自动提交当前 poll 到的最大 offset。 那么就会出现这样的情况： 

-  消费者从 Kafka 拉取了一批消息。 
- Kafka 客户端自动在 5 秒后提交 offset。 
-  但是应用代码**还没处理完**这批消息，有可能执行过程中出错或者失败了。 
- 但是 `Kafka` 因为接收到了`offset`，那么他就会认为这批消息已经处理完，不再重新发送了。 

那么，还有**第二种**情况， 如果用了手动提交，就没问题了吗？看以下代码： 

```java
@KafkaListener(topics = "my-topic", containerFactory = "kafkaListenerContainerFactory")
public void listen(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
    try{
        // 批量处理逻辑
    }finally{
        ack.acknowledge();  //手动提交偏移量
    }
    
}
```

在finally中调用偏移量提交，这时候会把最大的偏移量+1提交掉，也就意味着，不管你的try执行成功还是失败，都会提交，那么就会出现上面一样的情况，消息执行失败，但是偏移量被提交了，导致丢消息。 

### 如何避免丢消息 

搞清楚了消息是怎么丢的，那么就能解决丢消息的问题了。即用手动提交， 并且确保消息都成功之后再提交。 

```java
@KafkaListener(topics = "my-topic", containerFactory = "kafkaListenerContainerFactory")
public void listen(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {

    CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);
    List<Future<Boolean>> futures = new ArrayList<>();

    // 1. 提交所有任务
    records.forEach(messageExt -> {
        Callable<Boolean> task = () -> {
            try {
                //单条消费逻辑，失败抛异常
                // ....
            } catch (Exception e) {
                log.error("Task failed", e);
                return false; // 标记失败
            }
        };
        futures.add(completionService.submit(task));
    });

    // 2. 检查结果
    boolean allSuccess = true;
    try {
        for (int i = 0; i < records.size(); i++) {
            Future<Boolean> future = completionService.take();
            if (!future.get()) { // 3.发现一个失败立即终止
                allSuccess = false;
                break;
            }
        }
    } catch (Exception e) {
        allSuccess = false;
    }

    // 3. 根据结果提交偏移量
    if(allSuccess){
        ack.acknowledge();
    }
}
```

这么做消息不会丢了，但是也会带来一个问题那就是消息会重投的，因为只要有一个失败了，就不提交偏移量了，消息就会**整体重投**，但是这个其实还好，我们可以在消息的消费逻辑中**做好幂等**即可。总比丢消息要好的。 