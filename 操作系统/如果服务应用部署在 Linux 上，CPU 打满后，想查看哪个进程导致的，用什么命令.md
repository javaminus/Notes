当你的服务应用部署在 Linux 上，发现**CPU 打满**（CPU 使用率 100% 或非常高）时，想要查看是哪个进程导致的，可以使用以下常用命令：

---

## 1. top

```bash
top
```
- 默认按照 CPU 使用率排序，`%CPU` 列显示各进程消耗的 CPU 百分比。
- 按下**P**键可以重新按 CPU 排序（一般默认就是）。
- 观察最上面的进程，即为消耗 CPU 最高的进程。

---

## 2. htop（更友好，需要安装）

```bash
htop
```
- 类似于 top，但界面更友好，支持用方向键直接选择进程。
- 如果未安装，可用 `sudo apt install htop` 或 `sudo yum install htop` 安装。

---

## 3. ps 命令

```bash
ps -eo pid,ppid,cmd,%mem,%cpu --sort=-%cpu | head
```
- 该命令按照 CPU 使用率降序显示前几位进程。

---

### 补充：常见排查流程

1. 先用 `top` 或 `htop` 找到 CPU 占用高的进程对应的 PID。
2. 可以用 `ps aux | grep <PID>` 再进一步查看进程详情。
3. 若需查看是哪个线程占用 CPU，可结合 `top -H -p <PID>` 查看该进程内各线程的 CPU 使用情况。

---

**总结**  
最常用的命令是 `top`，配合 `ps` 可以快速定位高 CPU 占用的进程。