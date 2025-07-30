AOP动态代理是面向切面编程（AOP）的一种实现方式，**通过在运行时为目标对象创建代理对象**，以拦截方法调用并织入增强（如事务、日志、安全等）。这种代理是在程序运行时动态生成的，因此称为“动态代理”。

---

### 1. **原理和流程**

- **代理对象**并不是真正的业务对象（目标对象），而是由AOP框架（如Spring）在运行时生成的一个对象，这个对象会“包裹”原有对象。
- 当调用目标对象的方法时，实际上是调用了代理对象的方法，代理对象可以在方法执行前后插入一些额外的逻辑（切面增强）。
- 动态代理的实现方式主要有：
  - **JDK动态代理**：只支持接口代理，即目标对象必须实现接口。
  - **CGLIB动态代理**：通过生成目标类的子类来实现代理，目标对象无需实现接口。

---

### 2. **AOP动态代理与静态代理的区别**

| 类型     | 生成时机 | 灵活性 | 实现方式             |
| -------- | -------- | ------ | -------------------- |
| 静态代理 | 编译期间 | 低     | 手动编写代理类       |
| 动态代理 | 运行期间 | 高     | 框架自动生成代理对象 |

---

### 3. **应用场景**

- **Spring AOP**就是通过动态代理实现的，可以自动为业务对象添加事务、日志、权限等切面逻辑。
- 只对Spring容器管理的Bean生效。
- 常用于业务开发中的横切关注点处理。

---

### 4. **简单示例**

#### JDK动态代理（必须有接口）

```java
// 接口
public interface UserService {
    void saveUser();
}

// 实现类
public class UserServiceImpl implements UserService {
    public void saveUser() { /* ... */ }
}

// 代理生成
UserService proxy = (UserService) Proxy.newProxyInstance(
    UserServiceImpl.class.getClassLoader(),
    new Class[]{UserService.class},
    (proxyObj, method, args) -> {
        // 前置增强
        System.out.println("before");
        Object result = method.invoke(new UserServiceImpl(), args);
        // 后置增强
        System.out.println("after");
        return result;
    }
);
```

#### Spring AOP自动生成代理对象

```java
@Service
public class MyService {
    @Transactional
    public void doSomething() { /* ... */ }
}
// Spring会为MyService生成代理对象，实现事务增强
```

---

**总结**：  
AOP动态代理就是利用代理技术，在运行时为目标对象生成代理对象，实现方法调用的拦截和增强，是AOP最核心的实现机制之一。