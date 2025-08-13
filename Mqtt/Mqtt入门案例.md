# MQTT + Spring Boot + EMQX 入门案例

我将为您创建一个基础的 MQTT + Spring Boot + EMQX 的入门案例，包含完整的源代码和使用说明。

## 项目结构

```
mqtt-springboot-demo/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── mqttdemo/
│   │   │               ├── MqttDemoApplication.java
│   │   │               ├── config/
│   │   │               │   └── MqttConfig.java
│   │   │               ├── service/
│   │   │               │   ├── MqttPublisherService.java
│   │   │               │   └── MqttSubscriberService.java
│   │   │               └── controller/
│   │   │                   └── MqttController.java
│   │   └── resources/
│   │       └── application.properties
└── README.md
```

## 文件内容

首先，创建 `pom.xml` 文件：

```xml name=pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.14</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>mqtt-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>mqtt-demo</name>
    <description>Spring Boot MQTT demo with EMQX</description>
    
    <properties>
        <java.version>11</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.integration</groupId>
            <artifactId>spring-integration-mqtt</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

应用程序主类：

```java name=src/main/java/com/example/mqttdemo/MqttDemoApplication.java
package com.example.mqttdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MqttDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttDemoApplication.class, args);
    }
}
```

配置文件：

```properties name=src/main/resources/application.properties
server.port=8080

# MQTT 配置
mqtt.broker.url=tcp://localhost:1883
mqtt.client.id=spring-mqtt-client
mqtt.username=admin
mqtt.password=public
mqtt.default.topic=test/topic
mqtt.default.qos=1
mqtt.completion.timeout=3000
```

MQTT 配置类：

```java name=src/main/java/com/example/mqttdemo/config/MqttConfig.java
package com.example.mqttdemo.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.default.topic}")
    private String defaultTopic;

    @Value("${mqtt.default.qos}")
    private int qos;

    @Value("${mqtt.completion.timeout}")
    private int completionTimeout;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { brokerUrl });
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    // 配置入站通道（接收消息）
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    // 配置MQTT消息驱动通道适配器
    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId + "-consumer", mqttClientFactory(),
                        defaultTopic);
        adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    // 配置出站通道（发送消息）
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    // 配置MQTT消息处理器
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(clientId + "-producer", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(defaultTopic);
        messageHandler.setDefaultQos(qos);
        return messageHandler;
    }
}
```

MQTT 发布服务：

```java name=src/main/java/com/example/mqttdemo/service/MqttPublisherService.java
package com.example.mqttdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

@Service
public class MqttPublisherService {

    @Autowired
    private MessageChannel mqttOutboundChannel;

    /**
     * 发送消息到默认主题
     * @param payload 消息内容
     */
    public void publish(String payload) {
        Message<String> message = MessageBuilder
                .withPayload(payload)
                .build();
        mqttOutboundChannel.send(message);
    }

    /**
     * 发送消息到指定主题
     * @param topic 主题
     * @param payload 消息内容
     * @param qos 服务质量（0, 1, 2）
     */
    public void publish(String topic, String payload, int qos) {
        Message<String> message = MessageBuilder
                .withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, qos)
                .build();
        mqttOutboundChannel.send(message);
    }
}
```

MQTT 订阅服务：

```java name=src/main/java/com/example/mqttdemo/service/MqttSubscriberService.java
package com.example.mqttdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MqttSubscriberService {

    /**
     * 接收MQTT消息的处理方法
     * @param message MQTT消息
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
        String payload = (String) message.getPayload();
        
        log.info("接收到MQTT消息 - 主题: {}, 内容: {}", topic, payload);
        
        // 在这里处理接收到的消息
        // 例如：保存到数据库、触发其他业务逻辑等
    }
}
```

REST控制器用于测试：

```java name=src/main/java/com/example/mqttdemo/controller/MqttController.java
package com.example.mqttdemo.controller;

import com.example.mqttdemo.service.MqttPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mqtt")
public class MqttController {

    @Autowired
    private MqttPublisherService mqttPublisherService;

    @Value("${mqtt.default.topic}")
    private String defaultTopic;

    @Value("${mqtt.default.qos}")
    private int defaultQos;

    @PostMapping("/publish")
    public ResponseEntity<?> publishMessage(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        if (message == null) {
            return ResponseEntity.badRequest().body("消息内容不能为空");
        }
        
        mqttPublisherService.publish(message);
        return ResponseEntity.ok().body("消息已发送到默认主题: " + defaultTopic);
    }

    @PostMapping("/publish/{topic}")
    public ResponseEntity<?> publishMessageToTopic(
            @PathVariable String topic,
            @RequestBody Map<String, String> payload,
            @RequestParam(defaultValue = "-1") int qos) {
        
        String message = payload.get("message");
        if (message == null) {
            return ResponseEntity.badRequest().body("消息内容不能为空");
        }
        
        // 如果没有指定QoS，使用默认值
        int actualQos = qos >= 0 ? qos : defaultQos;
        
        mqttPublisherService.publish(topic, message, actualQos);
        return ResponseEntity.ok().body("消息已发送到主题: " + topic + ", QoS: " + actualQos);
    }
}
```

最后，创建 README 文件：

````markdown name=README.md
# Spring Boot + MQTT + EMQX 入门案例

这是一个使用 Spring Boot 集成 MQTT 并连接到 EMQX 消息中间件的示例项目。

## 技术栈

- Spring Boot 2.7.x
- Spring Integration MQTT
- EMQX (MQTT Broker)
- Java 11

## 功能特性

- MQTT 消息发布（支持指定主题和QoS）
- MQTT 消息订阅（自动订阅配置的主题）
- RESTful API 接口用于测试

## 准备工作

1. 安装并启动 EMQX
   - 使用 Docker 安装 EMQX:
     ```bash
     docker run -d --name emqx -p 1883:1883 -p 8083:8083 -p 8084:8084 -p 8883:8883 -p 18083:18083 emqx/emqx:latest
     ```
   - 或从[EMQX官网](https://www.emqx.io/downloads)下载安装包安装

2. 配置 EMQX (可选)
   - 访问 EMQX 控制台: http://localhost:18083
   - 默认用户名/密码: admin/public

## 配置项目

修改 `application.properties` 文件以匹配你的 EMQX 配置:

​```properties
mqtt.broker.url=tcp://localhost:1883  # EMQX 服务器地址
mqtt.client.id=spring-mqtt-client     # 客户端ID，确保唯一性
mqtt.username=admin                   # EMQX 用户名
mqtt.password=public                  # EMQX 密码
mqtt.default.topic=test/topic         # 默认主题
mqtt.default.qos=1                    # 默认QoS
mqtt.completion.timeout=3000          # 连接超时(毫秒)
​```

## 运行项目

1. 使用Maven编译并运行项目:
   ```bash
   mvn spring-boot:run
   ```

2. 或者在IDE中直接运行 `MqttDemoApplication` 类

## 测试API

1. 发送消息到默认主题:
   ```bash
   curl -X POST http://localhost:8080/api/mqtt/publish \
     -H "Content-Type: application/json" \
     -d '{"message":"Hello MQTT World!"}'
   ```

2. 发送消息到指定主题:
   ```bash
   curl -X POST http://localhost:8080/api/mqtt/publish/custom/topic \
     -H "Content-Type: application/json" \
     -d '{"message":"Hello from custom topic!"}' \
     -G -d "qos=2"
   ```

## 消息查看

1. 通过控制台日志查看订阅的消息
2. 使用MQTT客户端工具(如MQTT Explorer)连接到EMQX并订阅相关主题

## 主要组件说明

- **MqttConfig**: 配置MQTT连接、消息通道和处理器
- **MqttPublisherService**: 提供消息发布功能
- **MqttSubscriberService**: 处理接收到的MQTT消息
- **MqttController**: REST API接口用于测试消息发送

## 常见问题

- **连接失败**: 检查EMQX是否正常运行，以及配置的用户名密码是否正确
- **消息未收到**: 检查订阅的主题是否正确，QoS级别是否匹配
- **客户端ID冲突**: 确保每个客户端使用唯一的客户端ID
````

## 使用指南

1. 创建一个新的Spring Boot项目，并将上面的文件内容复制到对应的位置。

2. 确保已安装并启动EMQX服务（可以使用Docker安装）:
   ```bash
   docker run -d --name emqx -p 1883:1883 -p 18083:18083 emqx/emqx:latest
   ```

3. 使用Maven编译并运行项目:
   ```bash
   mvn spring-boot:run
   ```

4. 测试发布消息:
   ```bash
   curl -X POST http://localhost:8080/api/mqtt/publish \
     -H "Content-Type: application/json" \
     -d '{"message":"测试消息"}'
   ```

5. 在应用程序日志中查看接收到的消息。

这个入门案例涵盖了MQTT与Spring Boot集成的基本用法，可以作为您深入学习的基础。