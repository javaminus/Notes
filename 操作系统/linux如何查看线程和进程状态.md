在 Linux 系统下，可以通过多种方式查看线程和进程的状态：

### 1. 查看进程状态

- **ps 命令**  
  查看所有进程及其状态：
  ```bash
  ps aux
  ```
  查看特定用户的进程：
  ```bash
  ps -u 用户名
  ```

- **top 或 htop 命令**  
  实时查看系统进程：
  ```bash
  top
  ```
  （htop 需要单独安装，界面更友好）

- **pstree 命令**  
  以树状结构显示进程：
  ```bash
  pstree
  ```

### 2. 查看线程状态

- **ps -L 或 ps -T**
  查看某进程的所有线程：
  ```bash
  ps -L -p 进程号
  ```
  或
  ```bash
  ps -T -p 进程号
  ```

- **top 命令加 H 参数**
  显示线程信息：
  ```bash
  top -H
  ```

### 3. 进程/线程状态含义

在 ps 命令输出中，STAT 列显示状态，例如：
- R（Running）：运行中
- S（Sleeping）：休眠
- D（Uninterruptible sleep）：不可中断睡眠
- T（Stopped）：停止
- Z（Zombie）：僵尸进程

### 4. 通过 /proc 目录查看

每个进程在 /proc/目录下有一个以 PID 命名的目录。  
例如，查看 1234 号进程的线程及状态：
```bash
cat /proc/1234/status
ls /proc/1234/task/
```
上面会列出该进程的所有线程（每个线程有一个子目录）。

---

如需更详细的某个命令用法或某种状态的具体解释，欢迎补充说明！