下面是对 XXL-Job 的详细介绍、在 Java 中的基本使用方式，以及其任务“只触发一次”保障机制的说明。

---

## 一、XXL-Job 简介

**XXL-Job** 是一个分布式任务调度中间件，专为 Java 开发者设计，广泛用于定时任务、分布式任务管理、任务分片和调度等场景。主要特点包括：

- **分布式调度**：支持多节点执行任务，负载均衡与容错。
- **动态任务管理**：任务可以在线新增、修改、删除。
- **调度日志追踪**：详细的任务执行日志与失败告警。
- **多种执行方式**：支持 Java Bean、Shell、Python、PHP 等多语言任务。
- **高可用**：调度中心支持集群部署。

---

## 二、在 Java 中如何使用 XXL-Job

### 1. 引入依赖

在 Maven 项目中添加依赖：

```xml
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>2.4.0</version>
</dependency>
```

### 2. 配置执行器

在 `application.properties` 或 `application.yml` 中配置 XXL-Job 执行器参数：

```properties
xxl.job.admin.addresses=http://127.0.0.1:8080/xxl-job-admin
xxl.job.executor.appname=xxl-job-executor-sample
xxl.job.executor.address=
xxl.job.executor.ip=
xxl.job.executor.port=9999
xxl.job.accessToken=
```

### 3. 启动执行器

在 Spring Boot 项目中，添加 XXL-Job 执行器配置类：

```java
@Configuration
public class XxlJobConfig {
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses("http://127.0.0.1:8080/xxl-job-admin");
        executor.setAppname("xxl-job-executor-sample");
        executor.setPort(9999);
        return executor;
    }
}
```

### 4. 编写任务处理方法

用 `@XxlJob` 注解标记你的任务方法：

```java
@Component
public class SampleJob {

    @XxlJob("demoJobHandler")
    public ReturnT<String> execute(String param) throws Exception {
        System.out.println("XXL-JOB, Hello World! param:" + param);
        return ReturnT.SUCCESS;
    }
}
```

在 XXL-Job 管理后台添加任务，指定 `JobHandler` 名称为 `demoJobHandler`，即可自动调度执行。

---

## 三、XXL-Job 如何保证一任务只会触发一次？

在分布式调度场景下，XXL-Job 通过以下机制确保每个任务在某个时间点只会触发一次：

### 1. **调度中心唯一性**

- 任务的调度和触发都由调度中心（Admin）统一管理。
- 即使有多个执行器，只有调度中心负责任务的分发，防止重复触发。

### 2. **分布式锁与调度日志**

- 调度中心在触发任务前，会先写入调度日志（数据库表）。
- 使用数据库事务和唯一索引保证同一时间点同一任务只会有一条调度日志插入成功。
- 如果因并发或异常导致重复触发请求，只有第一个会成功，其余会被数据库拦截。

### 3. **去重机制**

- 调度中心以“任务ID + 触发时间”为唯一键，防止重复调度。
- 如果网络抖动或服务重启导致调度请求重复，调度日志唯一性能够保证只触发一次。

### 4. **执行器幂等性建议**

- 虽然调度中心已做防重，但建议任务本身实现幂等，确保偶发重复触发时不会造成业务损失。

---

## 总结

XXL-Job 是 Java 领域主流的分布式任务调度平台，使用简单，支持多种调度和执行方式。通过调度中心的统一调度、数据库分布式锁和调度日志唯一性等机制，XXL-Job 能够有效保证每个任务只会被触发一次。

如需示例代码或更详细的源码分析，可以进一步提问！