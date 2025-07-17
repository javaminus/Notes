RowBounds分页的原理是：**MyBatis在应用层（Java代码）通过RowBounds对象控制查询结果的偏移量（offset）和记录数（limit），然后在查询完成后，对结果集进行截取和分页处理。**

具体流程如下：

1. **参数传递**  
  开发者在调用Mapper方法时，传入一个RowBounds对象（包含offset和limit），如：
```java
RowBounds rowBounds = new RowBounds(offset, limit);
userMapper.selectUsers(rowBounds);
```

2. **SQL执行**  
  MyBatis会把RowBounds传递到底层。如果没有配置分页插件或拦截器，MyBatis默认不会修改原始SQL，而是执行完整查询，把所有结果查出来。

3. **结果集截取**  
  查询结果返回后，MyBatis在Java内存中对结果集按照RowBounds指定的offset和limit进行截取，只返回指定范围的数据给调用者。

4. **注意事项**  
- **RowBounds默认是内存分页**：即先查出所有数据，再做分页处理，适合小数据量，数据量大时会导致内存溢出和性能问题。
- **部分插件（如PageHelper）可以把RowBounds参数转化为真正的物理分页SQL（如LIMIT语句），此时分页在数据库层完成，效率更高。**

---

**一句话总结（面试用）：**  
RowBounds分页本质是在内存中对查询结果集进行截取分页，只有配合分页插件才可能生成物理分页SQL，否则数据量大时容易出现性能和内存问题。