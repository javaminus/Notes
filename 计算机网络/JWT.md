# JWT（JSON Web Token）简介

## 什么是JWT？

JWT（JSON Web Token）是一种开放标准（RFC 7519），用于在网络应用环境间以紧凑且安全的方式传递信息。它通常用于身份验证和信息交换。JWT由三部分组成：头部（Header）、载荷（Payload）、签名（Signature）。

---

## JWT的结构

JWT通常由三部分组成，每部分用英文句点`.`分隔：

```
xxxxx.yyyyy.zzzzz
```

1. **Header（头部）**  
   头部通常由两部分信息组成：令牌的类型（即JWT）和所使用的签名算法（如HMAC SHA256或RSA等）。

   ```json
   {
     "alg": "HS256",
     "typ": "JWT"
   }
   ```

   然后将其进行Base64Url编码。

2. **Payload（载荷）**  
   载荷部分包含声明（Claims），即要传递的数据。声明分为三类：注册声明、公共声明和私有声明。

   例子：

   ```json
   {
     "sub": "1234567890",
     "name": "John Doe",
     "admin": true
   }
   ```

   载荷同样使用Base64Url编码。

3. **Signature（签名）**  
   签名用于验证消息在传递过程中是否被篡改。使用编码后的header和payload，以及一个密钥，通过header中指定的算法进行签名。例如，对于HMAC SHA256算法：

   ```
   HMACSHA256(
     base64UrlEncode(header) + "." +
     base64UrlEncode(payload),
     secret
   )
   ```

---

## JWT的优点

- **紧凑性**：可以通过URL、POST参数或在HTTP header中传递。
- **自包含性**：载荷中包含了所有需要的信息，避免多次查询数据库。

---

## JWT的常见使用场景

- 用户身份认证（单点登录：SSO）
- 信息安全传递
- API认证

---

## JWT的安全性注意事项

- 不要在JWT中存储敏感信息（如明文密码）。
- 应合理设置过期时间（exp Claim）。
- 建议使用HTTPS传输JWT，防止中间人攻击。

---

## 参考资料

- [RFC 7519 - JSON Web Token (JWT)](https://datatracker.ietf.org/doc/html/rfc7519)
- [JWT.io 官方网站](https://jwt.io/)



# JWT 在 Spring Boot + Vue3 项目中的典型使用流程

---

## 一、后端（Spring Boot）实现

### 1. 添加依赖

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

### 2. JWT 工具类

```java
// JwtUtil.java
import io.jsonwebtoken.*;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "yourSecretKey";

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1小时有效期
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
```

### 3. 登录接口

```java
// AuthController.java
@RestController
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String password = user.get("password");
        // 假设用户名密码验证通过
        if ("admin".equals(username) && "123456".equals(password)) {
            String token = JwtUtil.generateToken(username);
            return Map.of("token", token);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
    }
}
```

### 4. 拦截器校验 JWT

```java
// JwtFilter.java
@WebFilter(urlPatterns = "/api/*")
public class JwtFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                String username = JwtUtil.parseToken(token);
                // 可设置到 request attribute 或 ThreadLocal
                chain.doFilter(req, res);
                return;
            } catch (Exception e) {
                // token 无效
            }
        }
        ((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效或缺失Token");
    }
}
```

---

## 二、前端（Vue3）实现

### 1. 登录页面发起请求并保存 Token

```js
// 登录方法示例
import axios from 'axios';

async function login(username, password) {
  const res = await axios.post('/login', {username, password});
  localStorage.setItem('jwt_token', res.data.token);
}
```

### 2. 请求时携带 JWT

```js
// axios 拦截器统一加 token
import axios from 'axios';

axios.interceptors.request.use(config => {
  const token = localStorage.getItem('jwt_token');
  if (token) {
    config.headers.Authorization = 'Bearer ' + token;
  }
  return config;
});
```

### 3. 发起受保护接口请求

```js
// 例如获取用户信息
axios.get('/api/userinfo')
  .then(res => {
    // 处理数据
  })
  .catch(err => {
    if (err.response && err.response.status === 401) {
      // 处理未登录或token过期
    }
  });
```

---

## 总结

- **后端**：登录接口签发 JWT，拦截器统一校验。
- **前端**：登录后本地保存 token，请求时统一携带，处理过期或未授权情况。

---

> 可根据实际需求扩展 JWT 的 payload、过期策略及刷新机制（如 Refresh Token）。

---

# JWT 常见面试问题与参考答案

---

### 1. JWT 和 Session 有什么区别？各自的优缺点是什么？

**答：**  
JWT 是一种无状态的身份验证机制，Session 是有状态的。  
- **JWT**：令牌保存在客户端，每次请求携带，服务端不保存会话数据；适合分布式、微服务系统，扩展性好，但如果需要强制失效某个 JWT 较难。  
- **Session**：会话信息保存在服务端，客户端只保存 session_id，易于强制失效，但扩展性差，分布式部署需共享会话数据。

---

### 2. JWT 的三部分分别是什么？每一部分的作用是什么？

**答：**  

头部（Header）、载荷（Payload）、签名（Signature）

1. **Header**：声明类型和签名算法，如 `{"alg": "HS256", "typ": "JWT"}`。  
2. **Payload**：携带声明信息（如用户ID、权限等），可自定义。  
3. **Signature**：对前两部分签名，防止数据篡改。

---

### 3. JWT 如何防止被篡改？签名机制的原理是什么？

**答：**  
JWT 使用签名算法（如 HMAC SHA256），对 header 和 payload 以及密钥进行签名。服务端收到 JWT 后用同样的算法和密钥重新计算签名，若一致则说明内容未被篡改。

---

### 4. JWT 的签名算法有哪些？HS256 和 RS256 有什么区别？

**答：**  
常见算法有 HS256（对称）、RS256（非对称）等。  
- **HS256**：用同一个密钥签名和校验。  
- **RS256**：用私钥签名、公钥校验，适合多服务间分发和验证。

---

### 5. JWT 为什么不建议存储敏感信息？应该如何安全使用 JWT？

**答：**  
JWT 的 payload 是明文编码（Base64Url），易被解码，任何拿到 JWT 的人都能查看内容，所以不应存储明文密码等敏感数据。  
建议：  
- 只存储必要的非敏感信息  
- 通过 HTTPS 传输  
- 合理设置过期时间  
- 使用较强的签名密钥

---

### 6. JWT 如何实现过期？如果 JWT 被盗，如何让它失效？

**答：**  
通过 `exp` 字段设置过期时间。JWT 一旦签发就不可随意撤销，如需提前失效，可结合维护 token 黑名单、缩短有效期+Refresh Token 等方案。

---

### 7. JWT 如何做权限管理？payload 中哪些字段可以用来做鉴权？

**答：**  
可在 payload 中包含用户角色、权限列表等，如 `role`、`scope` 字段，服务端在校验时根据这些字段判定用户权限。

---

### 8. JWT 令牌如何刷新（Token Refresh）？什么是 Refresh Token？

**答：**  
通常签发有效期短的 Access Token + 有效期长的 Refresh Token。Access Token 过期后，客户端用 Refresh Token 换取新 Token，避免频繁登录。

---

### 9. JWT 有哪些常见的安全漏洞？如何防御？

**答：**  
- **暴力破解签名密钥**：使用复杂密钥  
- **算法投毒攻击**：服务端应只接受指定算法  
- **XSS、CSRF**：不要在前端暴露 JWT，配合防护机制  
- **泄露后无法撤销**：结合黑名单、短有效期等措施

---

### 10. JWT 适合哪些场景？有哪些不适合使用 JWT 的情况？

**答：**  
适合：分布式、微服务、移动端、单点登录（SSO）等场景。  
不适合：高安全性、需要频繁撤销/失效 Token 的场景。





# 现代项目中用户信息存储方式的主流选择

## 1. 服务端存储（如 Sa-Token）

- **原理**：用户登录后，服务端生成一个 token（如随机字符串或 uuid），并将用户信息（如 userId、权限等）存储在服务端（内存、Redis、数据库等）关联该 token。客户端只保存 token，每次请求带上 token，服务端解析并获取对应的用户信息。
- **代表框架**：Sa-Token、Spring Session、Shiro、传统 Session 机制。
- **优点**：
  - 用户信息可以动态更新、强制下线、单点登录等操作更灵活。
  - Token 可以随时失效（踢人、登出等）。
  - 数据安全性高，敏感信息不暴露给客户端。
- **缺点**：
  - 需要服务端保存会话数据，分布式部署需要 session 共享方案（如 Redis）。
  - 扩展性相对稍差，尤其是大规模高并发时。

---

## 2. 客户端存储（如 JWT）

- **原理**：用户登录后，服务端生成一个 JWT，用户信息（如 userId、角色等）直接加密签名后写入 token，客户端保存 JWT。每次请求带上 token，服务端只做签名校验，不需要存储用户会话数据。
- **代表技术**：JWT、部分 OAuth2 实现。
- **优点**：
  - 完全无状态，服务端不用存储会话数据，便于分布式和弹性扩容。
  - 适合微服务、API网关等场景。
- **缺点**：
  - JWT 一旦签发，内容不可变，无法强制让单个 token 失效（除非采用黑名单等机制）。
  - 不宜存储敏感信息，token 被盗有一定风险。
  - 不能随时更新用户权限等信息。
  - 过期控制、刷新机制要自己实现。

---

## 3. 当前主流选择

- **大多数传统业务系统、企业后台**，仍然以**服务端存储**为主（如 Sa-Token、Session），因为安全性和灵活性更高，便于管理。
- **微服务、前后端分离、移动端、API网关**等场景，则常选用**JWT**作为无状态认证方案，便于扩展和跨服务鉴权。

---

## 4. 总结建议

- **安全性优先**、需要管理登录状态/踢人/权限动态变化的，建议用服务端存储（如 Sa-Token）。
- **扩展性优先**、无状态、服务端不希望保存会话的，建议用 JWT 或类似机制，但需注意安全和失效控制问题。

---

**实际项目中也可以结合使用**：如登录使用 JWT，重要操作二次校验服务端 session，或用短效 JWT + Refresh Token 机制。

---