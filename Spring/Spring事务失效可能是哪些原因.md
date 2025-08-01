Spring事务失效常见原因有以下几种：

---

1. **方法不是public**
   - Spring AOP 默认只拦截 public 方法，非 public 方法上的 `@Transactional` 不生效。

2. **自调用（同类内部方法调用）**
   - 类内部一个方法调用另一个加了 `@Transactional` 的方法，AOP代理无法拦截，事务不生效。

3. **异常被捕获且未抛出**
   - 默认只有抛出未被捕获的运行时异常（`RuntimeException`及其子类）才会回滚，手动捕获后未再抛出，事务不会回滚。

4. **异常类型不匹配**
   - 默认只回滚`RuntimeException`和`Error`，如果抛出的是`Checked Exception`（非运行时异常），事务不会自动回滚，需通过`@Transactional(rollbackFor=...)`指定。

5. **事务注解使用在接口或父类上**
   - 事务注解只能加在实现类或实现方法上，加在接口或父类上无效。

6. **未被Spring管理（未被Spring容器代理）**
   - 如果对象不是Spring Bean或者没有被AOP代理，事务注解不生效。

7. **多线程/异步场景**
   - 新线程或异步执行的方法不会被原有事务管理（每个线程独立），`@Transactional`不生效。

8. **数据库本身不支持事务或配置问题**
   - 比如MySQL表类型不是InnoDB（MyISAM不支持事务），或者事务传播、隔离级别配置有误。

9. **propagation设置不当**
   - 事务传播行为设置不当，导致事务未按预期传播或开启。

10. **手动提交/回滚事务冲突**
   - 代码中手动管理事务与Spring声明式事务冲突。

---

**总结一句话：**  
Spring事务失效主要是AOP代理机制未生效、异常未正确抛出、方法修饰符不对、自调用、数据库原因等导致的。实际开发中要特别注意这些细节！
