批量消费指的是一次性拉过来一批消息，然后进行批量处理。  

Kafka想要实现批量消费有很多种方案。其中比较简单的就是基于**@KafkaListener**  实现，这也是比较推荐的方案。（还有些其他方案，比如用原生kafka肯定也能，包括Spring Cloud Stream也支持kafka的批量消费，但是用的都不多。）  

首先需要依赖spring-kafka这个包： 

```xml

<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>2.2.4.RELEASE</version>
</dependency>
```

接着需要配置一个消费者工厂： 

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    //开启批量消费
    factory.setBatchListener(true); 
    //设置手动提交
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);//设置手动提交ackMode
    return factory;
}
```

这里通过`factory.setBatchListener(true); `的方式设置采用批量消费，但是需要注意的是，`ConcurrentKafkaListenerContainerFactory`的默认的提交方式是自动提交，如果在自动提交模式下，批量消费是有可能会丢消息的，所以，**需要设置为手动提交**。 

接下来就可以用这个消费者工厂来配置监听器做批量的消息监听了。 

```java
@KafkaListener(topics = "my-topic", containerFactory = "kafkaListenerContainerFactory")
public void listen(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
    // 批量处理逻辑
    
    // 处理完毕后统一提交 offset
    ack.acknowledge();  //手动提交偏移量
}
```

这里使用`@KafkaListener`注解，然后配置`containerFactory`为刚刚我们创建的批量消费的工厂，然后再listen方法的入参中，使用`List<ConsumerRecord<?, ?>> `来接收一批消息。之后就可以批量处理这些消息了，比如开个线程池并发处理。  

但是需要注意，这里一定要确保在所有的消息都处理成功之后，再手动提交偏移量。手动提交偏移量的时候，会把这批消息中最大的offset + 1进行提交，所以，一定要确保所有消息都成功了再提交，否则就会丢消息。 

**比如我看网上有些代码，在finally中去调用ack.acknowledge(); 这是不对的，因为这样你无法确保消息都处理成功。** 

