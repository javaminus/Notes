## HTTPS 如何防范中间人攻击（MITM）？

---

### 1. 中间人攻击（MITM）简介

- 中间人攻击指攻击者在客户端和服务器之间“截获”“篡改”或“伪造”通信内容，通常发生在不安全网络（如公共 WiFi）环境下。
- HTTP 明文传输，极易被窃听和篡改，无法防御 MITM。

---

### 2. HTTPS 防范原理

HTTPS = HTTP + SSL/TLS  
HTTPS 通过加密、安全认证和完整性校验，有效防止中间人攻击：

#### 2.1 加密通信

- SSL/TLS 协议在客户端与服务器之间建立加密通道，所有数据都被加密后传输，攻击者即使截获数据也无法解密内容。

#### 2.2 服务器身份认证（证书机制）

- 服务器必须提供受权威 CA 签发的数字证书，客户端校验证书合法性，防止攻击者伪造服务器。如果证书不可信，浏览器会发出警告。
- 证书中包含服务器公钥，客户端用公钥加密密钥协商信息，只有服务器能用私钥解密。

#### 2.3 数据完整性校验

- SSL/TLS 使用 MAC（消息认证码）/哈希算法对数据包做完整性校验，防止数据被篡改。
- 若数据被修改，校验失败，通信会被中断。

---

### 3. 总结流程

1. 客户端发起 HTTPS 请求，获得服务器证书。
2. 客户端校验证书合法性（防止伪造）。
3. 双方协商对称加密密钥（过程已加密，防止密钥泄漏）。
4. 后续通信均加密传输，并有完整性校验。

---

### 4. 面试官深问参考

- **Q: 如果 CA 被攻击怎么办？**
  - CA 失守会导致信任体系崩溃，用户可通过证书吊销机制、HSTS、证书透明机制等增强安全。
- **Q: 客户端如何校验证书？**
  - 校验证书链、有效期、域名匹配、是否被吊销等。

---

## 结论

HTTPS 通过加密、身份认证和完整性校验三重机制，有效防止中间人攻击，保障数据安全。