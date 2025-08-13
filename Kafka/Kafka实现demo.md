当然可以！下面将为你提供一个简单的Java Spring Boot + Kafka 的生产者和消费者Demo，实现最基础的消息发送与接收，适合初学者快速搭建和理解Kafka的基本用法。

---

## 1. 添加依赖

在你的`pom.xml`中加入：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

---

## 2. application.yml 配置

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: test-group
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```
> 注意：`localhost:9092`为你的Kafka服务地址。

---

## 3. 生产者代码

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
```

---

## 4. 消费者代码

```java
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "demo-topic", groupId = "test-group")
    public void listen(String message) {
        System.out.println("接收到消息: " + message);
    }
}
```

---

## 5. Controller测试接口

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class KafkaDemoController {
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @GetMapping("/send")
    public String send(@RequestParam String msg) {
        kafkaProducerService.sendMessage("demo-topic", msg);
        return "消息已发送: " + msg;
    }
}
```

---

## 6. 启动Kafka服务与测试

1. **保证本地或远端有Kafka服务已启动（zookeeper和kafka server）**
2. 启动你的Spring Boot项目
3. 访问：http://localhost:8080/kafka/send?msg=HelloKafka
4. 控制台会看到消费者打印出的消息

---

## 总结

这个Demo包括生产者、消费者及一个简单的接口进行消息发送，适合快速入门和验证Kafka集成。实际开发中可以扩展为JSON消息、批量消费、错误重试、分区处理等更复杂场景。如需更复杂的例子请随时告知！