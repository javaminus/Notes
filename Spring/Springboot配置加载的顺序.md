

## 1. 命令行参数（最高优先级）
直接在启动命令里传递参数。

```bash
java -jar myapp.jar --server.port=9001
```

此时，`server.port` 会被设置为 **9001**。

---

## 2. 系统属性（Java 系统变量）
用 `-D` 方式传递参数。

```bash
java -Dserver.port=9002 -jar myapp.jar
```

此时，`server.port` 会被设置为 **9002**（如果没有命令行参数覆盖）。

---

## 3. 环境变量（操作系统环境变量）
在操作系统中设置变量。

**Linux/Mac：**
```bash
export SERVER_PORT=9003
java -jar myapp.jar
```
**Windows：**
```cmd
set SERVER_PORT=9003
java -jar myapp.jar
```
此时，`server.port` 会被设置为 **9003**（如果上面两者未覆盖）。

---

## 4. profile配置（application-xxx.properties/yml）
比如你有个 `application-dev.properties`：

```properties
server.port=9004
```
启动时指定 profile：
```bash
java -jar myapp.jar --spring.profiles.active=dev
```
此时，`server.port` 会被设置为 **9004**（命令行/系统属性/环境变量未覆盖时）。

---

## 5. 主配置（application.properties/yml）
位于资源目录（`src/main/resources`）下的 `application.properties`：

```properties
server.port=9005
```
如果以上都没有，这里生效。

---

## 6. jar外（jar 包外部的配置文件）
如果你把一个 `application.properties` 文件放在 jar 旁边：

```
/deploy
  ├─ myapp.jar
  └─ application.properties   # jar外
```
内容比如：
```properties
server.port=9006
```
启动时没指定其它配置，则用 **9006**。

---

## 7. jar内（jar 包内部的配置文件）
打包时，`src/main/resources/application.properties` 被打进 jar 包里。

内容比如：
```properties
server.port=9007
```
如果没有上面所有配置，则使用 **9007**。

---

## 8. 默认值（Spring Boot 内部默认值）
如果所有都没有配置，Spring Boot 会用自己的默认值。比如 `server.port=8080`。

---

## 总结举例

假设你同时有这些配置：

- 启动命令：`java -Dserver.port=9002 -jar myapp.jar --server.port=9001`
- 操作系统环境变量：`SERVER_PORT=9003`
- application-dev.properties: `server.port=9004`
- application.properties（jar外）: `server.port=9006`
- application.properties（jar内）: `server.port=9007`

**最终端口是多少？**  
- **答案：9001（命令行参数，优先级最高）**

如果你去掉命令行参数，端口是多少？  
- **答案：9002（系统属性）**

如果都去掉，只留环境变量？  
- **答案：9003**

如果只剩下 profile 文件？  
- **答案：9004**

如果啥都不配？  
- **答案：8080（Spring Boot 默认）**

---

## 总结口诀

> **“谁在上面，谁优先，外部高于内部，命令行最大！”**

你也可以记住一句话：  
**“命令行 > 系统变量 > 环境变量 > profile配置 > 主配置 > jar外 > jar内 > 默认”**
