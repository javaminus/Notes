# MyBatis 工作原理详解

## 1. MyBatis 简介

MyBatis 是一款优秀的持久层框架，它支持自定义 SQL、存储过程以及高级映射。MyBatis 消除了几乎所有的 JDBC 代码和参数的手动设置以及结果集的检索。MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects）为数据库中的记录。

## 2. 核心组件

### 2.1 SqlSessionFactoryBuilder

- **作用**：根据配置信息构建 SqlSessionFactory 实例
- **生命周期**：一旦创建了 SqlSessionFactory，就不再需要 SqlSessionFactoryBuilder

### 2.2 SqlSessionFactory

- **作用**：创建 SqlSession 实例
- **生命周期**：应用运行期间一直存在，是线程安全的
- **实现方式**：通常利用单例模式或者静态单例模式

### 2.3 SqlSession

- **作用**：提供执行 SQL 语句、获取映射器和管理事务的方法
- **生命周期**：非线程安全，使用完毕需要关闭
- **重要接口**：
  - selectOne()、selectList()：查询
  - insert()、update()、delete()：增删改
  - commit()、rollback()：事务控制

### 2.4 Mapper 接口

- **作用**：定义操作数据库的方法
- **实现方式**：通过动态代理实现

### 2.5 映射文件

- **作用**：定义 SQL 语句和映射规则
- **内容**：包含 SQL 语句、参数映射、结果映射等

## 3. MyBatis 完整工作流程

1. **加载配置并初始化**：
   - 读取配置文件（mybatis-config.xml）
   - 加载映射文件（Mapper.xml）
   - 创建 SqlSessionFactory

2. **接收调用请求**：
   - 创建 SqlSession
   - 获取接口的代理对象 Mapper

3. **SQL 解析与执行**：
   - 根据调用的接口方法找到对应的 SQL
   - 解析 SQL 语句（动态 SQL 处理）
   - 参数映射和转换
   - 执行 SQL 语句

4. **结果映射与返回**：
   - 将数据库结果集映射为 Java 对象
   - 返回处理结果

## 4. 执行原理详解

### 4.1 初始化阶段

```
┌─────────────────────┐      ┌─────────────────────┐      ┌─────────────────────┐
│  配置文件解析       │─────>│  创建Configuration  │─────>│ 构建SqlSessionFactory│
└─────────────────────┘      └─────────────────────┘      └─────────────────────┘
```

1. **配置文件加载解析**：
   - 解析全局配置文件（mybatis-config.xml）
   - 解析映射文件（Mapper.xml）
   - 创建 Configuration 对象，保存所有配置信息

2. **创建 SqlSessionFactory**：
   - 通过 SqlSessionFactoryBuilder 构建 SqlSessionFactory
   - 默认实现是 DefaultSqlSessionFactory

### 4.2 执行阶段

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  获取SqlSession │────>│  获取Mapper代理 │────>│  执行SQL语句    │────>│  结果集映射     │
└─────────────────┘     └─────────────────┘     └─────────────────┘     └─────────────────┘
```

1. **获取 SqlSession**：
   - 从 SqlSessionFactory 创建 SqlSession 对象（DefaultSqlSession）
   - 创建执行器 Executor（默认是 SimpleExecutor）

2. **获取 Mapper 代理对象**：
   - 通过 SqlSession.getMapper(Class) 获取接口代理
   - 使用 JDK 动态代理生成代理对象

3. **执行 SQL**：
   - 代理对象拦截接口方法
   - 根据方法找到对应的 MappedStatement
   - 通过 Executor 执行 SQL
   - 底层依然使用 JDBC 进行数据库操作

4. **结果处理**：
   - 使用 ResultSetHandler 处理结果集
   - 将结果映射为 Java 对象
   - 返回给调用者

## 5. 动态代理实现

MyBatis 通过 JDK 动态代理为 Mapper 接口生成代理对象：

1. **代理创建**：
   ```java
   // 简化的代理创建逻辑
   Mapper mapper = sqlSession.getMapper(Mapper.class);
   ```

2. **代理实现**：
   - MapperProxy 实现 InvocationHandler 接口
   - 拦截接口方法调用
   - 根据方法签名找到对应的 SQL 语句
   - 通过 SqlSession 执行 SQL

3. **方法映射**：
   - 接口方法与 XML 中的 SQL 语句通过 namespace + id 映射
   - 方法参数与 SQL 参数通过 ParameterHandler 映射
   - 结果集通过 ResultSetHandler 映射为返回值

## 6. 缓存机制

MyBatis 提供了两级缓存：

### 6.1 一级缓存（会话级缓存）

- **范围**：SqlSession 级别，默认开启
- **工作机制**：
  - 同一个 SqlSession 内，相同的查询不会再次访问数据库
  - 增删改操作会清空一级缓存
- **实现**：HashMap 存储

### 6.2 二级缓存（应用级缓存）

- **范围**：namespace 级别（跨 SqlSession）
- **配置方式**：
  - 全局配置文件中开启缓存 `<setting name="cacheEnabled" value="true"/>`
  - 映射文件中配置 `<cache/>`
- **生命周期**：
  - 跟应用同生命周期
  - 可自定义实现

## 7. SQL 执行流程源码分析

### 7.1 SqlSession 创建

```java
// 构建 SqlSessionFactory
String resource = "mybatis-config.xml";
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

// 创建 SqlSession
SqlSession sqlSession = sqlSessionFactory.openSession();
```

### 7.2 获取 Mapper 代理

```java
// Configuration 中的 getMapper 方法
public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
  return mapperRegistry.getMapper(type, sqlSession);
}

// MapperRegistry 的 getMapper 方法
public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
  final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
  if (mapperProxyFactory == null) {
    throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
  }
  try {
    return mapperProxyFactory.newInstance(sqlSession);
  } catch (Exception e) {
    throw new BindingException("Error getting mapper instance. Cause: " + e, e);
  }
}
```

### 7.3 SQL 执行

```java
// MapperProxy 的 invoke 方法
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
  if (Object.class.equals(method.getDeclaringClass())) {
    return method.invoke(this, args);
  } else {
    return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
  }
}

// DefaultSqlSession 的 selectList 方法
public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
  try {
    MappedStatement ms = configuration.getMappedStatement(statement);
    return executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
  } catch (Exception e) {
    throw ExceptionFactory.wrapException("Error querying database. Cause: " + e, e);
  } finally {
    ErrorContext.instance().reset();
  }
}
```

## 8. 与 JDBC 的对比

| 特性       | JDBC                       | MyBatis            |
| ---------- | -------------------------- | ------------------ |
| SQL 编写   | 代码中硬编码               | XML 或注解分离     |
| 参数设置   | 手动设置参数位置和类型     | 自动映射参数       |
| 结果集处理 | 手动遍历和映射结果集       | 自动映射到对象     |
| 资源管理   | 手动管理连接、语句和结果集 | 自动管理资源       |
| 缓存支持   | 无内置缓存                 | 提供一级和二级缓存 |
| 动态 SQL   | 需要字符串拼接             | 内置动态 SQL 标签  |

## 9. 总结

MyBatis 的工作原理可以概括为：通过配置文件加载和初始化配置信息，使用动态代理技术实现接口方法与 SQL 映射，底层执行时仍然使用 JDBC 与数据库交互，但对外提供了更加面向对象和易于使用的 API。其核心优势在于解耦了 SQL 和代码，实现了参数和结果集的自动映射，并提供了缓存等高级特性。