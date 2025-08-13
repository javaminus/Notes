`LambdaQueryWrapper` 和 `QueryWrapper` 都是 MyBatis-Plus 提供的条件构造器，用于方便且优雅地拼接 SQL 条件，但二者的**主要区别**如下：

---

## 1. 字段引用方式不同

- **QueryWrapper**：字段名用字符串表示，容易写错，重构时不安全。
  ```java
  QueryWrapper<User> wrapper = new QueryWrapper<>();
  wrapper.eq("name", "Tom"); // "name" 是字符串
  ```

- **LambdaQueryWrapper**：字段名用**方法引用**（如 `User::getName`），编译安全，字段名变更代码自动提示。
  ```java
  LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
  wrapper.eq(User::getName, "Tom"); // User::getName 是方法引用
  ```

---

## 2. 类型安全

- **QueryWrapper**：字段名拼错，运行时才报错。
- **LambdaQueryWrapper**：字段名拼错，编译时就报错，避免低级错误。

---

## 3. 适用场景

- 推荐**优先使用 LambdaQueryWrapper**，尤其是业务表字段经常变更、多人协作的大项目。
- QueryWrapper 适合简单快速写 demo 或拼接动态 SQL。

---

## 4. 代码示例对比

```java
// QueryWrapper 示例（字符串字段名）
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("name", "Tom").ge("age", 18);

// LambdaQueryWrapper 示例（方法引用）
LambdaQueryWrapper<User> lambdaWrapper = new LambdaQueryWrapper<>();
lambdaWrapper.eq(User::getName, "Tom").ge(User::getAge, 18);
```

---

## 5. 总结口诀

- **QueryWrapper 用字符串，LambdaQueryWrapper 用方法引用。**
- **LambdaQueryWrapper 更安全、更智能、更推荐！**

---

如需更详细示例、使用场景、底层实现等，欢迎追问！