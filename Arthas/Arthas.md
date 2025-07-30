# Arthas 详细学习文档

```
jps // 查看所有Java进程及其PID
选择目标Java进程PID

jstat -gc PID // 实时查看GC/内存统计
IF YGC/FGC异常频繁 或 Heap使用率高:
    说明存在内存泄漏或GC压力大
    jmap -heap PID // 查看堆内存详细分布与GC算法
    jmap -histo:live PID // 查看堆中存活对象数量与占用
    IF 某些对象数量/内存占用异常高:
        jmap -dump:live,format=b,file=heap.bin PID // 导出堆dump文件
        使用MAT/VisualVM分析heap.bin，定位内存泄漏或大对象
    ENDIF
ENDIF

jstat -gcutil PID // 观察GC各区利用率
IF Old区利用率持续高 或频繁FullGC:
    进一步分析堆外内存与GC原因
ENDIF

top/htop // 查看CPU占用
IF Java进程CPU异常高:
    jstack PID // 导出所有线程堆栈
    分析高CPU线程ID（top -H -p PID），用jstack查找对应线程栈
    定位死循环、死锁或热点方法
ENDIF

lsof -p PID // 查看文件句柄数
IF 文件句柄数异常高:
    检查是否有句柄泄漏
ENDIF

IF 需要进一步确认类加载/卸载等信息:
    jcmd PID VM.classloaders // 查看类加载器信息
    jcmd PID GC.heap_info    // 查询堆信息
ENDIF

记录排查过程，形成结论，准备修复方案
```



```
启动 Arthas
Attach 到目标 Java 进程

dashboard // 查看整体状态
IF 发现异常:
    // 常见异常参考
    // 1. CPU高（整体或线程） 2. 内存高（heap使用率高、频繁GC）
    // 3. 线程数异常 4. TPS骤降 5. 类加载数异常

    thread // 分析线程详情
    IF 定位到高CPU/死锁/阻塞线程:
        记录线程ID和堆栈，准备修复
    ELSE:
        monitor 目标方法 // 监控方法调用次数和平均耗时
        IF 有性能热点方法:
            watch 目标方法 // 观察方法参数、返回值、异常
            trace 目标方法 // 分析方法调用路径与各步骤耗时
            profiler start/stop // 采样生成火焰图，查找CPU/内存热点
            IF 需要查看实际代码逻辑:
                jad 目标类 // 反编译线上实际运行的类
            ENDIF
            IF 需要临时修复:
                本地修改class -> 上传服务器
                mc 或 retransform // 热加载新class
                IF 需要临时修改变量或调用对象方法:
                    ognl // 动态执行表达式或修改变量
                ENDIF
            ENDIF
        ELSE:
            继续观察或记录当前现象
        ENDIF
    ENDIF
ELSE:
    继续观察或退出
ENDIF

记录排查和修复过程，归纳经验
```



## 1. Arthas 基础知识

### 1.1 什么是 Arthas？

Arthas（阿尔萨斯）是阿里巴巴开源的 Java 诊断工具，深受开发者喜爱。它能够帮助开发人员解决 Java 应用的诊断问题：如线上问题排查、性能调优、热更新等。

### 1.2 主要特性

- 提供进程内的线程、内存、类加载等状态观测
- 支持方法级别的监控，包括执行时间、调用次数、异常统计等
- 支持方法调用追踪，可以输出方法调用关系
- 支持代码热更新，无需重启进程
- 支持 JDK 6+，支持 Linux/Mac/Windows 平台

## 2. 安装与启动

### 2.1 快速安装

```bash
# 下载安装脚本
curl -O https://arthas.aliyun.com/arthas-boot.jar

# 启动 Arthas
java -jar arthas-boot.jar
```

### 2.2 启动方式

```bash
# 列出所有 Java 进程
java -jar arthas-boot.jar

# 选择进程编号进行连接
java -jar arthas-boot.jar [PID]
```

## 3. 核心命令详解

### 3.1 监控相关命令

#### dashboard - 系统整体监控
显示 JVM 中所有线程的运行状态，内存使用情况。

```
$ dashboard
ID   NAME                           GROUP          PRIORITY  STATE    %CPU    DELTA_TIME TIME   INTERRUPTED DAEMON
-1   VM Periodic Task Thread        -              -1        -        0.0     0.000      0:0    false       true
 1   main                           main           5         RUNNABLE 0.0     0.000      0:2    false       false
 2   Reference Handler              system         10        WAITING  0.0     0.000      0:0    false       true
```

#### thread - 线程相关信息
查看线程信息，查找死锁等。

```
$ thread -b  # 查找死锁线程
$ thread -n 3  # 展示前3个最忙的线程
$ thread [ID]  # 查看指定线程栈
```

#### jvm - JVM相关信息
显示 JVM 参数、内存等信息。

```
$ jvm
RUNTIME
---------------------------------------------------------------------------------------------------------
 MACHINE-NAME                   37006@localhost
 JVM-START-TIME                 2023-12-20 10:10:22
 MANAGEMENT-SPEC-VERSION        1.2
 SPEC-NAME                      Java Virtual Machine Specification
```

### 3.2 类和方法相关命令

#### sc - Search Class
查找类加载信息。

```
$ sc java.util.ArrayList
$ sc -d java.util.ArrayList  # 展示详细信息
```

#### sm - Search Method
查找类中的方法。

```
$ sm java.util.ArrayList
$ sm -d java.util.ArrayList add  # 查看add方法详细信息
```

#### jad - 反编译类
反编译已加载类的源码。

```
$ jad java.lang.String
$ jad --source-only com.example.MyClass
```

### 3.3 方法监控与追踪

#### monitor - 方法监控
对方法执行进行监控统计。

```
$ monitor -c 5 com.example.MyClass myMethod
```

#### watch - 观察方法执行
观察方法的入参、返回值和异常等。

```
$ watch com.example.MyClass myMethod '{params, returnObj, throwExp}' -x 3
$ watch com.example.MyClass myMethod '{params[0], returnObj}' 'params[0]>10'  # 条件表达式过滤
```

#### trace - 方法内部调用路径
跟踪方法内部调用，并统计各调用的耗时。

```
$ trace com.example.MyClass myMethod
$ trace -j 'org.springframework.web.servlet.DispatcherServlet *' '#cost>100'  # 匹配多个方法
```

#### stack - 方法调用来源
查看方法被哪些地方调用。

```
$ stack com.example.MyClass myMethod
```

### 3.4 增强与修改类行为

#### tt - Time Tunnel 时光隧道
记录方法每次调用的现场信息，支持事后查看与回放。

```
$ tt -t com.example.MyClass myMethod
$ tt -i 1000 -p  # 回放索引1000的调用
```

#### redefine - 热更新类文件
不重启 JVM 更新类定义。

```
$ redefine /tmp/com/example/MyClass.class
```

## 4. 高级功能

### 4.1 Arthas Tunnel
远程连接线上环境的 Arthas。

```
# 启动隧道服务器
java -jar arthas-tunnel-server.jar

# 客户端连接
java -jar arthas-boot.jar --tunnel-server 'ws://tunnel-server:7777/ws' --agent-id 'my-agent'
```

### 4.2 WebConsole
通过浏览器连接 Arthas。

```
# 启动时启用 WebConsole
java -jar arthas-boot.jar --target-ip 0.0.0.0
```

### 4.3 内存分析

```
$ heapdump /tmp/dump.hprof  # 生成堆转储文件
$ vmtool --action getInstances --className java.lang.String --limit 10  # 获取类实例
```

## 5. 常见问题排查场景

### 5.1 CPU 使用率高问题

```bash
# 1. 查看系统整体情况
dashboard

# 2. 查找高CPU线程
thread -n 3

# 3. 查看具体线程栈
thread 线程ID
```

### 5.2 内存泄漏问题

```bash
# 1. 查看内存情况
dashboard

# 2. 查看大对象占用
memory

# 3. 生成堆转储
heapdump /tmp/dump.hprof
```

### 5.3 接口响应慢问题

```bash
# 1. 跟踪接口方法
trace com.example.controller.UserController getUserInfo

# 2. 监控方法执行情况
monitor -c 10 com.example.service.UserService getUserDetails
```

### 5.4 代码热修复

```bash
# 1. 反编译查看问题代码
jad com.example.service.BuggyService

# 2. 修复代码并编译

# 3. 热更新类
redefine /path/to/fixed/BuggyService.class
```

## 6. 面试官常见问题与回答

### Q1: 什么是 Arthas？它有什么优势？

**答**：Arthas 是阿里巴巴开源的 Java 诊断工具，主要优势包括：
1. **无侵入性**：不需要修改应用代码，通过 Java Agent 技术动态注入
2. **实时诊断**：可以在生产环境实时排查问题，不需要重启应用
3. **功能全面**：覆盖线程分析、内存分析、方法跟踪、热修复等多个方面
4. **使用简便**：交互式命令行，命令简洁直观
5. **安全机制**：提供会话超时、权限控制等安全特性

### Q2: Arthas 的实现原理是什么？

**答**：Arthas 主要基于以下技术实现：
1. **Java Agent 技术**：使用 `instrument` 包实现动态附加到目标 JVM
2. **Java Instrumentation API**：实现类定义的修改和方法增强
3. **ASM/Byte-buddy**：用于字节码操作，实现方法的拦截、修改等
4. **JVM TI（JVM Tool Interface）**：获取 JVM 内部信息
5. **Java Compiler API**：支持动态编译 Java 源码

核心原理是通过动态字节码增强，在目标方法前后插入代码，收集调用信息、参数、返回值等。

### Q3: Arthas 和 JProfiler、MAT 等工具相比有什么不同？

**答**：
1. **使用场景不同**：
   - Arthas 主要用于生产环境实时问题排查
   - JProfiler 主要用于开发阶段性能分析
   - MAT 主要用于离线内存分析

2. **侵入性**：
   - Arthas 无需重启应用，动态注入
   - JProfiler 通常需要启动时配置

3. **功能侧重点**：
   - Arthas 侧重快速定位和解决线上问题
   - JProfiler 侧重完整的性能分析
   - MAT 专注于内存分析

4. **使用方式**：
   - Arthas 是命令行交互式
   - JProfiler 和 MAT 提供图形界面

### Q4: Arthas 如何实现热更新？有哪些限制？

**答**：Arthas 通过 `redefine` 命令实现热更新，底层使用 `Instrumentation.redefineClasses()` 方法。

**实现原理**：
1. 加载新的字节码文件
2. 使用 `Instrumentation` 替换类定义
3. 保持类身份不变（ClassLoader 和类名相同）

**主要限制**：
1. 不能新增、删除、修改字段和方法的签名
2. 不能修改方法的修饰符（如 static、final 等）
3. 不能修改继承关系
4. 不能处理构造函数
5. 如果方法正在执行，新的代码要等下次调用时才生效
6. 静态代码块不会重新执行
7. JDK 11+ 需要启用 `--add-opens java.base/jdk.internal.misc=ALL-UNNAMED`

### Q5: 如何使用 Arthas 定位线上 CPU 高负载问题？

**答**：定位 CPU 高负载的完整步骤：

1. **连接应用**：`java -jar arthas-boot.jar` 并选择目标进程

2. **查看整体情况**：`dashboard` 命令查看系统负载和高 CPU 线程

3. **分析热点线程**：`thread -n 3` 查看最耗 CPU 的 3 个线程

4. **查看线程栈**：`thread 线程ID` 查看详细调用栈

5. **方法级分析**：
   ```
   // 使用 trace 查看方法内部调用耗时
   trace 可疑类名 可疑方法名
   
   // 使用 stack 查看方法调用来源
   stack 可疑类名 可疑方法名
   ```

6. **持续监控**：`monitor` 命令监控方法执行情况

7. **热修复**（如需要）：修复代码并使用 `redefine` 热更新

关键是要能够分析调用栈，找出真正的热点方法。

### Q6: 如何排查方法执行慢的问题？

**答**：排查方法执行慢的步骤：

1. **定位慢方法**：通过用户反馈或监控系统确定可疑接口

2. **使用 trace 命令**：
   ```
   trace com.example.OrderService getOrderDetails '#cost > 200'
   ```
   追踪方法内部调用链路及耗时，条件表达式过滤耗时大于 200ms 的调用

3. **分析热点**：找出最耗时的子调用（如数据库查询、远程服务调用等）

4. **进一步 trace**：对热点子方法继续跟踪
   ```
   trace com.example.dao.OrderDao queryOrderItems '#cost > 100'
   ```

5. **查看方法入参**：使用 watch 命令查看入参和返回值
   ```
   watch com.example.dao.OrderDao queryOrderItems '{params, returnObj}' -x 2
   ```

6. **模拟执行**：使用 tt 命令记录调用
   ```
   tt -t com.example.dao.OrderDao queryOrderItems
   ```
   找到慢调用的索引后，使用 `-i` 参数回放

### Q7: Arthas 有哪些安全风险？如何规避？

**答**：Arthas 安全风险及规避措施：

1. **风险**：获取敏感数据（如密码、token）
   - **规避**：限制 watch/tt 命令使用，避免打印敏感字段；配置敏感信息过滤

2. **风险**：热更新代码带来的风险
   - **规避**：严格控制 redefine 命令权限，建立代码审查流程

3. **风险**：长时间 trace/monitor 带来性能影响
   - **规避**：设置合理的执行次数(-c)和时间(-n)限制

4. **风险**：连接未授权访问
   - **规避**：
     - 使用 `--target-ip 127.0.0.1` 限制只能本机访问
     - 设置访问认证 `--auth-password`
     - 配置会话超时 `--session-timeout`

5. **风险**：日志中可能包含敏感信息
   - **规避**：注意日志文件权限，定期清理

最佳实践是在生产环境使用时，限制可使用的命令集，并记录操作日志。

### Q8: 如何用 Arthas 解决 OOM 问题？

**答**：解决 OOM 问题的方法：

1. **确认 OOM 类型**：使用 `thread` 命令查看是否有 "java.lang.OutOfMemoryError" 异常的线程

2. **查看内存使用**：使用 `dashboard` 和 `memory` 命令查看内存使用情况

3. **生成堆转储**：
   ```
   heapdump /tmp/dump.hprof
   ```

4. **查找大对象**：
   ```
   vmtool --action getInstances --className 可疑类名 --limit 10
   ```

5. **查看类加载情况**：
   ```
   classloader -l    # 列出所有类加载器
   classloader -t    # 查看类加载器层次结构
   ```

6. **检查泄漏点**：
   ```
   watch 可疑类构造方法 '{params, target, "对象数:" + @可疑类@静态集合.size()}'
   ```

7. **临时解决**：
   - 使用 `redefine` 修复可能的内存泄漏代码
   - 使用 `vmtool --action forceGc` 强制执行 GC

OOM 问题通常需要结合 Arthas 的堆转储和离线分析工具（如 MAT）一起分析。

### Q9: Arthas 中如何编写复杂的条件表达式？

**答**：Arthas 支持 OGNL (Object-Graph Navigation Language) 表达式，可以编写复杂的条件表达式：

1. **基本语法**：
   ```
   watch com.example.UserService getUserById '{params, returnObj, throwExp}' 'params[0]==10001'
   ```

2. **访问对象属性**：
   ```
   watch com.example.OrderService createOrder '{params, returnObj}' 'params[0].userId==10001'
   ```

3. **调用方法**：
   ```
   watch com.example.OrderService createOrder '{params, returnObj}' 'params[0].getItems().size()>2'
   ```

4. **复杂逻辑**：
   ```
   watch com.example.OrderService createOrder '{params, returnObj}' 'params[0].amount>1000 && params[0].getItems().size()>5'
   ```

5. **使用静态方法或字段**：
   ```
   watch com.example.OrderService createOrder '{params, returnObj}' '@com.example.Constants@VIP_LEVEL==params[0].userLevel'
   ```

6. **调用目标对象方法**：
   ```
   watch com.example.UserService getUserById '{params, target.isVip(params[0]), returnObj}'
   ```

7. **判断异常类型**：
   ```
   watch com.example.OrderService createOrder '{params, throwExp}' 'throwExp instanceof java.lang.IllegalArgumentException'
   ```

### Q10: Arthas 的类隔离机制是什么？

**答**：Arthas 使用自定义的类加载器实现了类隔离机制，确保不会与目标应用的类产生冲突。

**主要实现方式**：
1. **独立 ClassLoader**：Arthas 核心类通过独立的 `ArthasClassLoader` 加载，避免与应用类混淆
2. **间接调用**：通过反射、动态代理等方式间接调用应用类
3. **引用管理**：避免 Arthas 类持有应用类的强引用，防止类卸载问题

**隔离的好处**：
1. 避免依赖冲突（如 Arthas 使用的 ASM 与应用使用的版本不同）
2. 确保 Arthas 可以被完全卸载，不影响应用后续运行
3. 防止 Arthas 内部异常影响应用正常运行

**注意事项**：在编写复杂表达式时，需要注意类加载器隔离带来的限制，有时需要显式指定类全名。

## 7. 总结与最佳实践

1. **谨慎使用**：在生产环境谨慎使用修改类行为的命令
2. **设置超时**：为命令设置执行次数和时间限制，避免长时间影响性能
3. **组合使用**：多个命令结合使用效果更好（如 thread + trace + watch）
4. **构建知识库**：记录常见问题的排查命令，形成排查手册
5. **配合监控**：与 Prometheus、Grafana 等监控系统配合使用效果更佳
6. **持续学习**：关注 Arthas 的版本更新，利用新特性解决问题

通过熟练掌握 Arthas，可以大大提高 Java 应用问题排查和性能优化的效率，减少线上问题处理时间。