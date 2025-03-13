### **SQL 注入示例**

SQL 注入（SQL Injection）是一种通过在用户输入的 SQL 语句中插入恶意代码，从而篡改数据库查询逻辑的攻击方式。下面我们设计一个 SQL 注入的具体案例，包括表结构、查询语句和注入方式。

------

## **1. 设计数据库表**

假设我们有一个 `users` 表，用于存储用户登录信息：

```
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);
```

表中的数据：

```
INSERT INTO users (username, password) VALUES ('admin', '123456');
INSERT INTO users (username, password) VALUES ('user', 'password123');
```

------

## **2. 存在 SQL 注入的代码**

如果后端代码直接拼接 SQL 语句，而不使用 **参数化查询**，就可能导致 SQL 注入。例如：

```
String userInput = "admin' OR '1'='1";  // 攻击者输入
String sql = "SELECT * FROM users WHERE username = '" + userInput + "' AND password = '" + password + "'";
```

如果 `password` 也是随意输入的，比如 `"abc"`，最终 SQL 变成：

```
SELECT * FROM users WHERE username = 'admin' OR '1'='1' AND password = 'abc';
```

由于 **'1'='1'** 恒成立，该查询就变成：

```
SELECT * FROM users WHERE username = 'admin' OR TRUE;
```

这样，攻击者无需知道密码，就可以绕过认证，成功登录。

------

## **3. 其他 SQL 注入案例**

### **（1） 注入 OR 1=1 绕过登录**

攻击者在用户名输入框中输入：

```
admin' OR '1'='1' --
```

最终 SQL 变成：

```
SELECT * FROM users WHERE username = 'admin' OR '1'='1' -- ' AND password = 'xxxx';
```

由于 `--` 是 SQL 的**注释符**，后面的 `AND password = 'xxxx'` 被忽略，直接返回所有用户信息，成功登录。

### **（2） 使用 UNION SELECT 窃取数据库信息**

如果 SQL 语句是：

```
SELECT username, password FROM users WHERE username = 'input';
```

攻击者输入：

```
' UNION SELECT database(), user() --
```

最终 SQL 变成：

```
SELECT username, password FROM users WHERE username = '' UNION SELECT database(), user() --';
```

这将返回数据库名和数据库用户信息。

### **（3） 盲注（布尔型）**

如果应用不显示查询结果，而是仅返回 **登录成功** 或 **失败**，攻击者可以通过 **二分法测试字符**：

```
' AND IF(SUBSTRING((SELECT database()),1,1)='a', SLEEP(5), 1) --
```

如果数据库名的第一个字符是 `a`，服务器会 **延迟 5 秒**，否则不会延迟。通过不断测试不同字符，可以逐个猜出数据库名称。

------

## **4. 如何防止 SQL 注入？**

### **✅ 1. 使用参数化查询（PreparedStatement）**

```
PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
stmt.setString(1, username);
stmt.setString(2, password);
ResultSet rs = stmt.executeQuery();
```

### **✅ 2. 过滤用户输入**

- **拒绝拼接 SQL 语句**，避免用户输入影响 SQL 逻辑。
- **转义特殊字符**，如 `'`、`--`、`;` 等。

### **✅ 3. 使用最低权限账户**

- **限制数据库用户权限**，防止 `DROP TABLE`、`UPDATE`、`DELETE` 等操作。

### **✅ 4. 使用 Web 应用防火墙（WAF）**

- 通过 **WAF（Web Application Firewall）** 过滤 SQL 注入攻击。

------

## **5. 总结**

SQL 注入主要是由于 **拼接 SQL 语句** 造成的，攻击者可以利用它来 **绕过身份验证、窃取数据，甚至破坏数据库**。最有效的防范方法是 **使用参数化查询**，避免直接拼接用户输入到 SQL 语句中。