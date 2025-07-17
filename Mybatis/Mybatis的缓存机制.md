# MyBatis 缓存机制详解

## 1. 缓存概述

MyBatis 提供了强大的查询缓存特性，可以有效减少数据库压力，提高系统性能。MyBatis 内置了两级缓存体系：一级缓存和二级缓存。默认情况下，只启用了一级缓存。

## 2. 一级缓存（SqlSession级别缓存）

### 2.1 基本概念

一级缓存是 SqlSession 级别的缓存，也称为本地缓存（Local Cache）。一级缓存默认启用，且无法关闭。

### 2.2 工作原理

- **缓存范围**：同一个 SqlSession 实例
- **缓存实现**：底层使用 PerpetualCache（实际是HashMap）实现
- **生命周期**：与 SqlSession 相同，SqlSession 关闭后缓存即清空

### 2.3 缓存处理流程

1. 对于某个查询，MyBatis 会创建一个缓存 key（由 SQL ID + SQL语句 + SQL参数 + RowBounds 组成）
2. 执行查询前，先检查一级缓存是否存在对应的 key
3. 如果缓存命中，直接返回缓存结果，不再查询数据库
4. 如果缓存未命中，查询数据库，将结果存入一级缓存，并返回结果

### 2.4 一级缓存失效情况

以下情况会导致一级缓存失效：

1. **不同 SqlSession**：不同的 SqlSession 之间的缓存是相互隔离的
2. **同一 SqlSession 执行增删改操作**：会清空当前 SqlSession 的所有缓存
3. **手动清空缓存**：调用 `sqlSession.clearCache()` 方法
4. **调用 flushCache="true" 的查询**：一些映射语句设置 flushCache="true" 会清空缓存
5. **SqlSession关闭**：当 SqlSession 关闭时，一级缓存也会被清空

### 2.5 一级缓存源码分析

```java
// BaseExecutor.query 方法（简化版）
public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
  CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
  return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
}

// 检查一级缓存
public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
  // 从一级缓存中查找
  List<E> list = (List<E>) localCache.getObject(key);
  if (list != null) {
    // 命中缓存，处理结果集
    handleLocalCachedOutputParameters(ms, key, parameter, boundSql);
  } else {
    // 未命中缓存，查询数据库
    list = queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
  }
  return list;
}
```

## 3. 二级缓存（全局缓存）

### 3.1 基本概念

二级缓存是 namespace 级别的缓存，也称为全局缓存，可以被多个 SqlSession 共享。

### 3.2 工作原理

- **缓存范围**：同一个 namespace 下的所有操作语句
- **缓存实现**：默认同样使用 PerpetualCache，但可配置为第三方缓存
- **生命周期**：与应用同生命周期

### 3.3 启用二级缓存

启用二级缓存需要进行以下配置：

1. **全局配置中启用**：在 mybatis-config.xml 中设置
   ```xml
   <settings>
       <setting name="cacheEnabled" value="true"/>
   </settings>
   ```

2. **Mapper 映射文件中配置**：在各 Mapper.xml 中添加 `<cache/>` 标签
   ```xml
   <mapper namespace="com.example.UserMapper">
       <cache/>
       <!-- 映射语句 -->
   </mapper>
   ```

3. **实体类需要可序列化**：二级缓存中的对象必须实现 Serializable 接口
   ```java
   public class User implements Serializable {
       private static final long serialVersionUID = 1L;
       // 属性和方法
   }
   ```

### 3.4 二级缓存配置选项

```xml
<cache
  eviction="FIFO"               <!-- 缓存回收策略 -->
  flushInterval="60000"         <!-- 刷新间隔（毫秒） -->
  size="512"                    <!-- 最大缓存对象数量 -->
  readOnly="false"/>            <!-- 是否只读 -->
```

- **eviction**：缓存回收策略
  - LRU（默认）：最近最少使用，移除最长时间不被使用的对象
  - FIFO：先进先出
  - SOFT：软引用，基于垃圾回收器状态和软引用规则
  - WEAK：弱引用，更积极地基于垃圾收集器状态和弱引用规则

- **flushInterval**：刷新间隔，单位毫秒，默认不设置，表示只有在调用时刷新

- **size**：缓存引用数目，默认1024

- **readOnly**：是否只读
  - true：只读缓存，返回缓存对象的引用，性能高但不安全
  - false（默认）：可读写缓存，会返回缓存对象的副本，安全但性能稍差

### 3.5 二级缓存生效条件

1. mybatis-config.xml 中 cacheEnabled 设置为 true（默认）
2. Mapper.xml 中配置了 `<cache>` 标签
3. 查询语句没有设置 useCache="false"
4. 查询语句没有设置 flushCache="true"
5. **SqlSession 必须被关闭或提交才会将数据写入二级缓存**（重要！）

### 3.6 二级缓存失效情况

1. 执行增删改操作会清空整个namespace的二级缓存
2. 配置了 flushCache="true" 的查询会清空缓存
3. 执行 `sqlSession.clearCache()` 不会清空二级缓存（只清空一级缓存）

### 3.7 二级缓存源码分析

```java
// CachingExecutor.query 方法（简化版）
public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
  BoundSql boundSql = ms.getBoundSql(parameterObject);
  CacheKey key = createCacheKey(ms, parameterObject, rowBounds, boundSql);
  return query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
}

public <E> List<E> query(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
  // 获取 Cache 对象
  Cache cache = ms.getCache();
  if (cache != null) {
    // 判断是否需要刷新缓存
    flushCacheIfRequired(ms);
    // 判断语句是否使用缓存
    if (ms.isUseCache() && resultHandler == null) {
      // 从二级缓存中获取数据
      @SuppressWarnings("unchecked")
      List<E> list = (List<E>) tcm.getObject(cache, key);
      if (list == null) {
        // 二级缓存未命中，查询数据库（通过装饰的一级缓存）
        list = delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
        // 将查询结果放入二级缓存
        tcm.putObject(cache, key, list);
      }
      return list;
    }
  }
  // 如果没有二级缓存或不使用缓存，则通过一级缓存查询
  return delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
}
```

## 4. 自定义缓存

除了默认的 PerpetualCache 实现，MyBatis 还允许使用自定义缓存实现或第三方缓存库。

### 4.1 配置自定义缓存

```xml
<cache type="com.domain.CustomCache">
  <property name="configProperty" value="configValue"/>
</cache>
```

### 4.2 常用第三方缓存集成

MyBatis 可以集成多种第三方缓存库，如 EhCache、Redis 等：

**EhCache 集成示例**:
```xml
<!-- 添加依赖 -->
<dependency>
  <groupId>org.mybatis.caches</groupId>
  <artifactId>mybatis-ehcache</artifactId>
  <version>x.x.x</version>
</dependency>

<!-- Mapper.xml 配置 -->
<cache type="org.mybatis.caches.ehcache.EhcacheCache">
  <property name="timeToIdleSeconds" value="3600"/>
  <property name="timeToLiveSeconds" value="3600"/>
  <property name="maxEntriesLocalHeap" value="1000"/>
  <property name="maxEntriesLocalDisk" value="10000000"/>
  <property name="memoryStoreEvictionPolicy" value="LRU"/>
</cache>
```

## 5. 缓存使用最佳实践

### 5.1 一级缓存建议

- 对于单体应用，一级缓存有助于减少同一事务内的重复查询
- 在分布式环境中，可以考虑将一级缓存范围设置为 STATEMENT（默认是 SESSION）
- 在对数据一致性要求高的场景，也应考虑将缓存范围设置为 STATEMENT

### 5.2 二级缓存使用建议

- **注意数据一致性问题**：二级缓存跨 SqlSession 共享，可能导致脏读
- **避免在多表查询中使用**：多表查询时，任一表更新都应该使缓存失效，但 MyBatis 只会使更新表对应的命名空间缓存失效
- **适合读多写少的场景**：如字典数据、配置信息等相对静态的数据
- **考虑使用专业缓存代替**：在复杂系统中，可以考虑使用 Redis, Memcached 等专业缓存系统

### 5.3 缓存设置语句级控制

在特定的查询语句上可以单独控制缓存行为：

```xml
<!-- 不使用缓存 -->
<select id="selectUsers" resultType="User" useCache="false">
  SELECT * FROM users
</select>

<!-- 刷新缓存 -->
<select id="selectUsers" resultType="User" flushCache="true">
  SELECT * FROM users
</select>
```

## 6. 常见缓存问题

### 6.1 一级缓存导致的数据不一致

问题：在一个 SqlSession 中查询数据后，数据库中的数据被其他进程修改，再次查询仍返回旧数据。

解决：
- 在关键查询前调用 `clearCache()`
- 设置 `flushCache="true"`
- 对于关键业务数据，配置为 `localCacheScope="STATEMENT"`

### 6.2 二级缓存导致的脏读

问题：多个命名空间操作相同的数据表，更新操作只会使当前命名空间缓存失效。

解决：
- 避免在多表关联查询时使用二级缓存
- 使用 `<cache-ref>` 在多个命名空间之间共享缓存
- 对关联性强的表使用同一个 Mapper 接口
- 考虑使用外部缓存系统替代二级缓存

## 7. 总结

MyBatis 缓存机制通过一级缓存和二级缓存提供了灵活的数据缓存策略：

- **一级缓存** 默认启用，作用于 SqlSession 级别，适合减少同一事务内的重复查询
- **二级缓存** 需要手动配置，作用于 namespace 级别，适合读多写少的场景
- 在实际应用中，需要根据业务场景和系统需求，合理配置和使用缓存机制
- 对于复杂系统或高并发场景，可以考虑使用专业的缓存系统替代 MyBatis 内置缓存

通过合理使用缓存，可以有效提升应用性能，减少数据库压力，但同时也需要注意缓存带来的数据一致性问题。