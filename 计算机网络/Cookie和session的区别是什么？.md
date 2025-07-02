## Cookie 和 Session 的区别

---

### 1. 存储位置不同

- **Cookie**：保存在客户端浏览器（本地），每次请求自动随请求头发送到服务器。
- **Session**：保存在服务器端，客户端只保存 Session ID（通常通过 Cookie 传递）。

---

### 2. 安全性

- **Cookie**：容易被窃取、篡改（如 XSS、劫持），不适合存储敏感信息。
- **Session**：敏感数据存储在服务器，安全性更高，客户端只持有 ID。

---

### 3. 存储容量限制

- **Cookie**：单个大小约 4KB，数量有限（每域名最多 20 个左右）。
- **Session**：理论容量大，受服务器内存限制。

---

### 4. 生命周期

- **Cookie**：可设置过期时间，支持持久化（磁盘/内存）。
- **Session**：通常有服务器超时机制，关闭浏览器或超时后失效。

---

### 5. 适用场景

- **Cookie**：适合保存不敏感、需长期保存的数据（如主题、自动登录信息）。
- **Session**：适合存储敏感、临时数据（如登录状态、购物车等）。

---

### 6. 工作机制

- 客户端首次请求时，服务器生成 Session 并返回 Session ID（通过 Cookie 或 URL 传递）。
- 以后每次请求，客户端携带 Session ID，服务器端根据 Session ID 查找和维护用户状态。

---

## 总结

Cookie 用于客户端本地存储，容易被窃取，容量有限，适合保存非敏感数据。  
Session 用于服务器端存储，安全性高，适合保存敏感和临时会话数据。两者常结合使用，综合提升 Web 应用的功能与安全性。

## 面试官可能深问的问题及参考答案

---

### 1. 为什么 Session 更安全？Session 一定安全吗？

**参考答案：**  
Session 将敏感数据存储在服务器端，客户端仅持有 Session ID，相比 Cookie 不易被直接篡改或窃取。但 Session 并非绝对安全，Session ID 可能被劫持（如 XSS、CSRF、网络监听）。防护措施包括：  
- 使用 HTTPS 传输
- 设置 HttpOnly、Secure 属性
- 防止 Session Fixation（会话固定攻击）
- 定期更换 Session ID

---

### 2. Session ID 丢失或泄露会有什么安全隐患？如何防护？

**参考答案：**  
Session ID 一旦被窃取，攻击者可冒充用户进行操作。常见防护措施有：  
- 设置 HttpOnly，防止 JS 访问
- 设置 Secure，仅 HTTPS 传输
- Session ID 绑定用户信息（如 IP、User-Agent）
- 定期更换 Session ID（如用户登录后）
- 实现 CSRF 令牌机制

---

### 3. 如果用户禁用 Cookie，Session 还能用吗？

**参考答案：**  
Session 依赖 Session ID，通常通过 Cookie 传递。如果 Cookie 被禁用，可以采用 URL 重写（在 URL 参数中附加 Session ID），但存在泄露风险（如被第三方获取）。实际生产中建议优先使用 Cookie，并提示用户开启。

---

### 4. Session 如何实现分布式（如多台服务器下）共享？

**参考答案：**  
传统 Session 存储在单台服务器内存，分布式架构下可采用：  
- Session 共享存储（如 Redis、Memcached）

- Session 粘性（Sticky Session）：同一用户请求分配到同一服务器

- Token 机制（如 JWT），在客户端存储状态，不依赖服务器 Session

- > JWT（JSON Web Token）是一种用于在网络应用环境中安全传递声明（claims）的开放标准（RFC 7519）。它常用于身份认证和信息交换。
  >
  > **核心要点如下：**
  >
  > - **结构**：JWT 由三部分组成，用点（.）分隔：
  >   1. Header（头部）：声明类型（typ: JWT）和签名算法（如 HS256）。
  >   2. Payload（负载）：存放实际要传递的数据（如用户ID、权限等声明），也可自定义字段。
  >   3. Signature（签名）：对前两部分进行签名，确保数据未被篡改。
  >
  >   一个典型的 JWT 如下：
  >   ```
  >   xxxxx.yyyyy.zzzzz
  >   ```
  >
  > - **无状态**：JWT 通常存储于客户端（如 Cookie/LocalStorage），每次请求携带，服务器只需验证签名，无需存储会话数据，适合分布式系统。
  >
  > - **安全性**：
  >   - 签名机制可以防止数据被篡改，但默认内容是明文编码（Base64Url），不可存敏感信息。
  >   - 支持对称（如 HS256）和非对称（如 RS256）加密算法。
  >
  > - **应用场景**：
  >   - 用户登录后的身份认证（Token-Based Authentication）
  >   - 微服务间安全通信
  >   - 信息安全传递（如 OAuth、OpenID Connect）
  >
  > **总结**：  
  > JWT 是一种轻量级、跨平台的令牌格式，便于安全地在客户端和服务端之间传递信息，广泛应用于现代 Web 认证和授权体系。

---

### 5. Cookie 有哪些属性？如何提升安全性？

**参考答案：**  
常用属性有：  
- Expires / Max-Age：过期时间
- Path / Domain：作用路径/域
- Secure：仅 HTTPS 传输
- HttpOnly：禁止 JS 访问
- SameSite：防御 CSRF

提升安全性建议：  
- 开启 Secure、HttpOnly
- 设置合理的 SameSite
- 避免存储敏感数据

---

### 6. 如何防止 Session 被劫持/固定（Session Hijacking/Session Fixation）？

**参考答案：**  
- 登录/敏感操作后立即重新生成 Session ID
- 绑定 Session ID 与用户特征（如 IP、User-Agent）
- 设置合理的超时时间
- 使用 HTTPS 加密
- 开启 HttpOnly、Secure

---

### 7. Session 过期后，用户体验如何处理？

**参考答案：**  
Session 过期（如超时、服务器重启），用户需重新登录。可通过友好提示、自动跳转登录页、保存未提交数据等方式提升体验。

---

### 8. Cookie 和 LocalStorage 的区别？

**参考答案：**  
- Cookie：每次请求自动携带，支持服务器读写，容量小（约 4KB）
- LocalStorage：仅本地存储，容量大（约 5MB），不会随请求发送，仅前端 JS 访问

---

### 9. 一个用户在多个浏览器或设备登录，Session 和 Cookie 行为有何不同？

**参考答案：**  
- Cookie 和 Session 均为单一浏览器实例独立维护，跨设备/浏览器互不影响。  
- “记住我”等功能通常用持久化 Cookie 实现。

---

### 10. 说一说你对 Session 跨站请求伪造（CSRF）攻击的理解及防护措施？

**参考答案：**  
CSRF 利用受信任用户的身份发起请求，盗用用户会话。防护手段：  
- 设置 SameSite=Strict/Lax
- 检查 Referer/Origin
- 实现 CSRF Token

---