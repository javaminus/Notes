## JWT 登录认证简例

### 后端（Spring Boot）生成和校验 JWT

```java
@RestController
public class AuthController {
  @PostMapping("/api/login")
  public ResponseEntity<?> login(@RequestBody LoginVO vo) {
    // 校验用户名密码
    // 生成JWT
    String token = JwtUtil.generateToken(vo.getUsername());
    return ResponseEntity.ok(Map.of("token", token));
  }

  @GetMapping("/api/profile")
  public ResponseEntity<?> profile(@RequestHeader("Authorization") String token) {
    String username = JwtUtil.verifyToken(token.replace("Bearer ", ""));
    // 返回用户信息
    return ResponseEntity.ok(Map.of("username", username));
  }
}
```

### 前端（Vue）登录与请求

```javascript
// 登录时
axios.post('/api/login', { username, password }).then(res => {
  localStorage.setItem('token', res.data.token);
});

// 请求受保护接口时
axios.get('/api/profile', {
  headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') }
}).then(res => {
  console.log(res.data);
});
```

---

## 常见问题

- 前端如何跨域访问 Spring Boot？
  - Spring Boot 配置 CORS（@CrossOrigin 或 WebMvcConfigurer）
- 如何安全传递 Token？
  - 推荐通过请求头（Authorization），并使用 HTTPS

---

# 这里的token不就是一个普通的参数吗？为什么还要用jwt这种麻烦的东西？ 

你的问题非常有代表性，下面详细解释一下为什么实际开发中常用 JWT（Json Web Token）而不仅仅是“一个普通的参数”：

---

## 1. “普通参数”与 JWT 的区别

- **普通参数**（比如 sessionId、token 字符串等）只是一个标识符，通常需要在服务器上保存和管理，比如 session 存储在数据库或内存。
- **JWT** 是一种**自包含**的令牌，里面可以包含用户信息、权限信息等，并且可以验证是否被篡改。

---

## 2. 为什么要用 JWT？

**优点如下：**

### 1）无状态（Stateless）
- JWT 是自包含的，服务端不需要保存 token 数据，减轻了服务器压力，便于扩展和分布式部署。

### 2）安全性
- JWT 可以用**签名算法（如HS256）**保证内容未被篡改。
- 可以设置过期时间，避免长期有效导致的安全隐患。

### 3）携带更多信息
- JWT 可以在 payload 中携带用户ID、角色等信息，前后端都可以解析，灵活性高。

### 4）跨语言、跨平台
- 多种语言都支持 JWT，前后端技术栈不用担心兼容问题。

---

## 3. 普通 token 的不足

- 需要服务端保存和查找 session/token 信息，服务器压力大，分布式扩展难。
- 不能自验证，容易被伪造。
- 通常只能作为身份标识，不能携带其他信息。

---

## 4. JWT 的工作原理简述

1. 用户登录成功，服务器签发 JWT，发给前端。
2. 前端每次请求带上 JWT（比如放到 Authorization header）。
3. 服务器收到请求后，直接验证 JWT 的签名和时间，无需查数据库。
4. 验证通过，解析 payload，获取用户信息，完成业务逻辑。

---

## 5. JWT 示例

JWT 实际就是 3 段字符串拼接，内容包括：

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9 // header
.eyJ1c2VySWQiOiIxMjMiLCJyb2xlIjoiYWRtaW4iLCJleHAiOjE2MzExMTQ0MDB9 // payload
.dBj8qvJEOv2v7qk2Jw2r2M9bXj8dPXXe1S4rD5sKXfA // signature
```

你可以把它当成“一个参数”，但它比普通参数更安全、更灵活、更高效。

---

## 总结

- **JWT 是一种安全、灵活、便于分布式扩展的认证方式。**
- 普通参数只能做简单标识，安全和扩展性都不如 JWT。

如有更多关于 JWT 或实际开发场景的问题，欢迎继续交流！

