在 Java 中，`Future` 和 `Promise`（对应的是 `CompletableFuture`）都是用来支持**异步编程**的核心机制，允许你在**子线程执行任务后，主线程获取结果**，实现线程间的通信与协作。

------

## 🔹1. `Future` 接口（Java 5 引入）

### ✅ 概念：

表示一个**异步计算的结果**，通过 `ExecutorService.submit()` 提交任务后返回 `Future` 对象。

### 📦 常用方法：

```java
Future<T> future = executorService.submit(callable);

future.get();     // 阻塞等待结果
future.cancel();  // 取消任务
future.isDone();  // 是否完成
future.isCancelled(); // 是否被取消
```

### ✅ 优点：

- 简单易用，支持线程池任务结果获取
- 可取消任务

### ❌ 缺点：

- `get()` 是阻塞的，**无法注册回调函数**
- 不支持多个任务的组合或链式操作
- 功能较弱

------

## 🔹2. `CompletableFuture`（Java 8 引入）🌟

Java 对 `Future` 的增强版，既是 `Future`，又是 `CompletionStage`，支持**非阻塞获取结果 + 回调 + 异步流式编程**。

### ✅ 特点：

- 异步编排：支持 `thenApply()`、`thenAccept()`、`thenCompose()` 等方法链式操作
- 非阻塞：注册回调，无需阻塞主线程
- 支持多个任务组合（`allOf`, `anyOf`）
- 可手动完成（即 "Promise" 行为）

------

### 🔧 使用示例：

#### ✅ 创建异步任务

```
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return "Hello, world!";
});
```

#### ✅ 注册回调（非阻塞）

```
future.thenAccept(result -> {
    System.out.println("结果是：" + result);
});
```

#### ✅ 链式操作

```
CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> "Java")
    .thenApply(str -> str + " Future")
    .thenApply(str -> str.toUpperCase());

System.out.println(result.get());  // JAVA FUTURE
```

#### ✅ 组合多个任务

```
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B");

CompletableFuture<Void> combined = CompletableFuture.allOf(f1, f2);
combined.join(); // 阻塞等待所有完成
```

------

### 🟡 手动完成（Promise-like）

```
CompletableFuture<String> promise = new CompletableFuture<>();

// 在线程中手动完成
new Thread(() -> {
    try {
        Thread.sleep(1000);
        promise.complete("Done!");
    } catch (Exception e) {
        promise.completeExceptionally(e);
    }
}).start();

System.out.println(promise.get()); // 等待异步结果
```

------

## ✅ Future vs CompletableFuture 对比表

| 特性                    | `Future`     | `CompletableFuture`            |
| ----------------------- | ------------ | ------------------------------ |
| 支持异步执行            | ✅            | ✅                              |
| 支持取消任务            | ✅            | ✅                              |
| 支持阻塞等待结果        | ✅（`get()`） | ✅（`get()` or `join()`）       |
| 支持非阻塞回调          | ❌            | ✅（`thenApply`, `thenAccept`） |
| 支持任务组合            | ❌            | ✅（`thenCombine`, `allOf` 等） |
| 支持异常处理            | ❌            | ✅（`exceptionally`, `handle`） |
| 可作为 Promise 手动完成 | ❌            | ✅（`complete()` 方法）         |

------

## 🔚 总结

| 场景                     | 推荐方式                       |
| ------------------------ | ------------------------------ |
| 简单任务提交+获取结果    | `Future` + `ExecutorService`   |
| 高级异步编排、非阻塞处理 | `CompletableFuture`            |
| 实现 Promise 风格逻辑    | `CompletableFuture.complete()` |

------

如果你需要一个完整的代码示例或在具体项目中使用它（如 Web、IO、数据库异步调用），我可以提供相应模板。