XXL-JOB是一个开源的分布式任务调度平台，其核心设计理念是"调度与执行解耦"。下面从几个关键方面说明其工作原理和特点：

1. **核心组件**
- 调度中心：独立部署的服务，负责触发任务和管理执行器注册信息
- 执行器：嵌入业务系统的组件，通过注解方式定义具体任务逻辑


2. **工作流程**
- 执行器启动时自动向调度中心注册地址和端口信息
- 调度中心通过CRON表达式配置触发时间，通过HTTP请求调用执行器
- 执行器根据路由策略分配任务节点，执行后返回结果和日志

3. **分布式特性**
- 采用注册机制动态感知可用执行器，支持集群部署和弹性扩缩容
- 提供多种路由策略（轮询/随机/故障转移/分片广播）避免任务重复执行


4. **任务配置**
- 通过@XxlJob注解定义任务方法，需在调度中心配置对应任务信息
- 支持动态调整参数和路由策略，无需重启服务
- 配置文件需指定调度中心地址实现通信

下面是一个**Spring Boot 集成 XXL-Job**，并实现**每30分钟执行一次任务**的完整案例，包括关键代码和后台配置说明。

---

## 1. 添加 Maven 依赖

```xml
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>2.4.0</version>
</dependency>
```

---

## 2. 配置 application.properties

```properties
xxl.job.executor.appname=springboot-xxljob-demo
xxl.job.admin.addresses=http://localhost:8080/xxl-job-admin
xxl.job.executor.ip=
xxl.job.executor.port=9999
xxl.job.accessToken=
xxl.job.executor.logpath=/data/applogs/xxl-job/jobhandler
xxl.job.executor.logretentiondays=30
```

---

## 3. 编写 XXL-Job 配置类

```java name=XxlJobConfig.java
package com.example.demo.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XxlJobConfig {
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAppname("springboot-xxljob-demo");
        executor.setAdminAddresses("http://localhost:8080/xxl-job-admin");
        executor.setPort(9999);
        executor.setLogPath("/data/applogs/xxl-job/jobhandler");
        executor.setLogRetentionDays(30);
        return executor;
    }
}
```

---

## 4. 编写定时任务 Job Handler

```java name=ThirtyMinuteJob.java
package com.example.demo.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

@Component
public class ThirtyMinuteJob {

    @XxlJob("thirtyMinuteJobHandler")
    public void execute() {
        System.out.println("每30分钟执行一次的定时任务，当前时间：" + System.currentTimeMillis());
        // 这里写你的业务代码
    }
}
```

---

## 5. 在 XXL-Job 管理后台配置定时任务

1. 登录 xxl-job-admin 后台（如：http://localhost:8080/xxl-job-admin）
2. 任务管理 → 新增任务
   - **JobHandler** 填写 `thirtyMinuteJobHandler`
   - **调度类型** 选择 CRON
   - **CRON 表达式** 填写 `0 0/30 * * * ?`
     - 含义：每小时的第0分和第30分执行，即每30分钟执行一次
   - 选择你的执行器（appname 和端口要与代码一致）
   - 其他参数可按需填写
3. 保存并启动任务

---

## 6. 项目结构参考

```
src/
 └─ main/
      ├─ java/
      │    └─ com.example.demo/
      │         ├─ config/
      │         │    └─ XxlJobConfig.java
      │         └─ job/
      │              └─ ThirtyMinuteJob.java
      └─ resources/
            └─ application.properties
```

---

## 7. CRON表达式说明

- `0 0/30 * * * ?`：每小时的第0分和第30分执行一次
- 你也可以在管理后台直接测试、执行一次，或者调整表达式实现更多复杂定时策略

---

如需更多功能（如传递参数、分片广播等），欢迎继续提问！