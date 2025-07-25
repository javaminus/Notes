# XXL-JOB + Spring Boot 3 完整使用教程

本文档面向初学者，详细介绍如何在 Spring Boot 3 项目中集成、配置并使用分布式任务调度框架 XXL-JOB。

---

## 目录

1. [环境准备与原理介绍](#环境准备与原理介绍)
2. [搭建 XXL-JOB 管理后台](#搭建-xxl-job-管理后台)
3. [Spring Boot 3 项目集成 XXL-JOB 执行器](#spring-boot-3-项目集成-xxl-job-执行器)
4. [JobHandler 编写与注册](#jobhandler-编写与注册)
5. [任务调度与管理](#任务调度与管理)
6. [常见问题与解决方案](#常见问题与解决方案)
7. [参考资料](#参考资料)

---

## 环境准备与原理介绍

- JDK 17+
- Spring Boot 3.x
- Maven/Gradle 构建工具
- MySQL 用于 XXL-JOB 管理后台数据存储

### XXL-JOB 架构简介

- **管理后台**：任务配置、调度、监控中心
- **执行器**：任务实际执行节点
- **JobHandler**：实际业务任务实现
- **通信协议**：HTTP/RPC（管理员推送任务到执行器）

---

## 搭建 XXL-JOB 管理后台

1. **下载管理后台源码**  
   官方地址：[https://github.com/xuxueli/xxl-job](https://github.com/xuxueli/xxl-job)

2. **准备数据库**  
   使用 MySQL，新建数据库 `xxl-job`，导入 `xxl-job-admin/src/main/resources/db/tables_xxl_job.sql`。

3. **启动 XXL-JOB Admin**  
   可用 IDEA/Eclipse 运行 `xxl-job-admin` 模块，或打包后运行：

   ```sh
   cd xxl-job-admin/target
   java -jar xxl-job-admin-*.jar
   ```

   配置文件（`application.properties`）主要参数：

   ```
   server.port=8080
   spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl-job?Unicode=true&characterEncoding=UTF-8
   spring.datasource.username=root
   spring.datasource.password=123456
   ```

4. **访问管理后台**  
   默认地址：[http://localhost:8080/xxl-job-admin](http://localhost:8080/xxl-job-admin)  
   默认账号密码：admin/123456

---

## Spring Boot 3 项目集成 XXL-JOB 执行器

### 1. 添加 Maven 依赖

```xml
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
    <version>2.4.0</version>
</dependency>
```

### 2. 配置参数 `application.yml`

```yaml
xxl:
  job:
    admin:
      addresses: http://localhost:8080/xxl-job-admin
    executor:
      appname: demo-executor
      ip:
      port: 9999
      logpath: /data/applogs/xxl-job/jobhandler
      logretentiondays: 30
```

### 3. 编写配置类，注册执行器 Bean

```java
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XxlJobConfig {

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.executor.appname}")
    private String appName;

    @Value("${xxl.job.executor.ip:}")
    private String ip;

    @Value("${xxl.job.executor.port}")
    private int port;

    @Value("${xxl.job.executor.logpath}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays}")
    private int logRetentionDays;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(adminAddresses);
        executor.setAppname(appName);
        executor.setIp(ip);
        executor.setPort(port);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(logRetentionDays);
        return executor;
    }
}
```

---

## JobHandler 编写与注册

### 1. 编写任务代码

```java
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

@Component
public class DemoJobHandler {

    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        System.out.println("Hello XXL-JOB, Spring Boot 3!");
        // 具体任务逻辑
    }
}
```

### 2. 在管理后台注册任务

1. 执行器管理 → 新建执行器（如 demo-executor，需与项目配置一致）
2. 任务管理 → 新建任务  
   - **JobHandler** 填写为 `demoJobHandler`
   - 配置调度方式、Cron 表达式等
   - 支持参数传递、路由策略、失败重试等

---

## 任务调度与管理

- **调度方式**  
  支持 Cron、固定频率、手动触发等
- **路由策略**  
  支持随机、轮询、分片等
- **任务分片**  
  多执行器节点下分片执行
- **任务日志**  
  后台可实时查看任务执行日志

---

## 常见问题与解决方案

### 1. 执行器注册失败

- 检查 `admin.addresses` 配置，确保能访问管理后台
- 检查执行器端口是否被占用
- 管理后台与执行器时间同步

### 2. JobHandler 未被发现

- Handler 名字要与后台配置一致
- 确认 Bean 被 Spring 扫描
- 查看日志排查异常

### 3. 日志未生成

- 检查日志目录权限与路径
- 日志保留天数可配置

### 4. 任务执行异常

- 查看后台任务日志
- 检查参数与环境配置
- 优化任务代码，避免阻塞与死循环

---

## 参考资料

- [XXL-JOB 官方文档](https://www.xuxueli.com/xxl-job/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [开源项目地址](https://github.com/xuxueli/xxl-job)
- [JobHandler 注解源码](https://github.com/xuxueli/xxl-job/blob/master/xxl-job-core/src/main/java/com/xxl/job/core/handler/annotation/XxlJob.java)

---

如需进一步交流，建议加入 XXL-JOB 社区或关注官方 Issues。