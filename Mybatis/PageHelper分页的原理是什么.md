PageHelper分页的原理是：**通过MyBatis拦截器机制，在SQL执行前自动修改原始SQL，添加对应数据库的分页语句（如LIMIT、OFFSET），并自动执行总数统计SQL（count），把分页信息和结果封装到Page对象中返回。**

---

### 详细原理说明

1. **拦截器原理**  
  PageHelper是一个MyBatis插件，注册为MyBatis的Executor拦截器。每次你在业务代码里调用`PageHelper.startPage(pageNum, pageSize)`，PageHelper就在本线程记录分页参数。

2. **SQL改写**  
  当你执行Mapper查询方法时，PageHelper会拦截SQL执行过程，根据当前线程的分页参数，把原始SQL自动改写为带分页的SQL语句。例如：
- MySQL：`SELECT ... FROM ... WHERE ... LIMIT offset, pageSize`
- Oracle：`SELECT * FROM (SELECT a.*, ROWNUM r FROM (原SQL) a WHERE ROWNUM <= endRow) WHERE r > startRow`

3. **自动统计总数**  
  PageHelper会自动生成一条`SELECT COUNT(*) FROM ...`统计SQL，先查出总记录数，为前端提供总页数和导航信息。

4. **分页结果封装**  
  PageHelper把查询结果和总数、页码、总页数等信息，统一封装到`Page`或`PageInfo`对象中，开发者直接使用即可。

5. **线程安全和无侵入**  
  所有分页参数只在本线程生效，不影响其它请求；业务代码无需关心分页SQL和总数统计，所有分页逻辑PageHelper自动处理，极大简化开发。

---

#### **一句话总结（面试用）**
> PageHelper通过MyBatis拦截器自动改写SQL，添加分页语句和总数统计，实现数据库层的高效物理分页，并将分页结果统一封装返回，开发者无需手动编写分页SQL和count语句。