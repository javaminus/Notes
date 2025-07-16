下面是**Spring Boot常用注解**，按照你的分类方式分组整理：

---

## 1. Bean 相关注解
- `@Component`：通用组件，受Spring容器管理
- `@Service`：标记服务层组件
- `@Repository`：标记数据访问层（DAO）组件
- `@Controller`：标记控制器组件
- `@RestController`：组合了`@Controller`和`@ResponseBody`
- `@Configuration`：声明配置类
- `@Bean`：方法级注解，声明一个Bean对象交由Spring容器管理
- `@ComponentScan`：指定要扫描的包

---

## 2. 依赖注入相关注解
- `@Autowired`：自动注入依赖（按类型，默认required=true）
- `@Qualifier`：与`@Autowired`配合，按名称注入
- `@Resource`：JSR-250注解，按名称/类型注入
- `@Inject`：JSR-330注解，基本等同于`@Autowired`
- `@Value`：注入配置文件中的值（如`${xxx}`）

---

## 3. 读取配置相关注解
- `@ConfigurationProperties`：绑定配置文件到Java对象
- `@Value`：注入单个配置值
- `@PropertySource`：指定额外的properties文件
- `@EnableConfigurationProperties`：开启`@ConfigurationProperties`注解的支持

---

## 4. Web 相关注解
- `@RestController`：开发RESTful API的常用注解
- `@Controller`：处理Web请求
- `@RequestMapping`：请求路径映射（类或方法级别）
- `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping`：对应HTTP方法的快捷映射
- `@PathVariable`：获取URL中的变量
- `@RequestParam`：获取请求参数
- `@RequestBody`：获取请求体中的JSON数据
- `@ResponseBody`：方法返回值直接写入HTTP响应体
- `@ResponseStatus`：自定义响应状态码
- `@CrossOrigin`：跨域支持

---

## 5. 其他常用注解
- `@SpringBootApplication`：Spring Boot项目入口，组合了`@Configuration`、`@EnableAutoConfiguration`和`@ComponentScan`
- `@EnableAutoConfiguration`：启用自动配置
- `@EnableScheduling`：开启定时任务支持
- `@EnableAsync`：开启异步方法支持
- `@EnableTransactionManagement`：开启事务管理
- `@Transactional`：声明事务
- `@Profile`：指定Bean在哪些profile环境下生效
- `@Slf4j`、`@Log4j2`等：Lombok日志注解

---

如果你需要某一类注解的详细用法或示例，可以继续提问！