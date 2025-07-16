在 Spring 中，@Autowired 和 @Resource 都可以用来实现依赖注入，但它们有以下区别：

### 1. 来源不同
- **@Autowired**：是 Spring 框架提供的注解（位于 `org.springframework.beans.factory.annotation` 包）。
- **@Resource**：是 JSR-250 规范中提供的注解（位于 `javax.annotation` 包），属于 Java 标准注解。

### 2. 注入方式不同
- **@Autowired**：默认按类型（by type）自动注入。如果有多个同类型的 Bean，可以结合 @Qualifier 注解按名称注入。
- **@Resource**：默认按名称（by name）注入。如果找不到与属性名同名的 Bean，则回退为按类型注入。

### 3. 常用属性
- **@Autowired**：
  - `required` 属性，默认是 true，表示注入失败时会抛出异常。
- **@Resource**：
  - 有 `name` 和 `type` 两个属性，常用的是 `name`，指定要注入的 Bean 名称。

### 4. 使用示例

```java
// @Autowired 按类型注入
@Autowired
private UserService userService;

// @Resource 按名称注入
@Resource(name = "userServiceImpl")
private UserService userService;
```

### 5. 总结

| 注解       | 主要依据 | 提供方       | 其他                   |
| ---------- | -------- | ------------ | ---------------------- |
| @Autowired | 类型     | Spring       | 可搭配@Qualifier按名称 |
| @Resource  | 名称     | JSR-250/Java | 有name和type属性       |

**建议：**
- 如果只用 Spring，推荐用 @Autowired。
- 如果需要兼容 JavaEE 标准或第三方框架，可选择 @Resource。

如需详细用法或实际代码示例，请告知！