### 问题

**Spring Boot 和 Spring Cloud 有什么区别？各自的主要功能是什么？**

---

#### 详细解释

Spring Boot 和 Spring Cloud 都是 Spring 生态的重要组成部分，但它们的定位和功能侧重点不同。

#### 1. Spring Boot

- **定位**：专注于简化单体应用（也可微服务）开发和部署的“快速开发脚手架”。
- **主要功能**：
  - 自动配置（Auto-configuration）：大幅减少 XML 和 Java 配置。
  - 内嵌服务器（如 Tomcat、Jetty）：无需单独部署，打包即运行。
  - 各类 Starter 依赖：快速集成常见中间件和第三方组件（如数据库、消息队列等）。
  - 健康检查、监控（Actuator）、外部化配置等。
- **目标**：让开发者专注于业务逻辑，极大提升生产效率。

#### 2. Spring Cloud

- **定位**：专注于“微服务架构”下的分布式系统基础设施整合，提供一站式解决方案。
- **主要功能**：
  - 服务注册与发现（如 Eureka、Consul、Zookeeper）
  - 负载均衡（如 Ribbon、Spring Cloud LoadBalancer）
  - 配置中心（如 Spring Cloud Config）
  - 服务网关（如 Zuul、Gateway）
  - 熔断和容错（如 Hystrix、Resilience4j）
  - 链路追踪与监控（如 Sleuth、Zipkin）
  - 消息驱动（如 Stream、Bus）
- **目标**：让开发者专注于业务微服务，基础设施和分布式问题交给 Spring Cloud。

#### 关系与区别

- **Spring Boot** 是基础，简化单个应用开发；**Spring Cloud** 是基于 Spring Boot 的微服务解决方案，管理和协调多个 Spring Boot 应用。
- Spring Cloud 依赖 Spring Boot，但 Spring Boot 不依赖 Spring Cloud。
- Spring Boot 适合单体和简单微服务；Spring Cloud 适合微服务架构下的复杂场景。

**举例说明：**
- 你用 Spring Boot 能快速开发一个电商网站的订单服务；
- 你用 Spring Cloud 能让订单服务和用户服务、库存服务等实现注册发现、配置集中、链路追踪等微服务能力。

---

#### 总结性回答（复习提示词）

> Spring Boot：简化开发，自动配置、内嵌服务器、Starter。Spring Cloud：微服务基础设施，服务注册发现、配置中心、网关、熔断等。Cloud 基于 Boot，用于云原生/微服务架构。