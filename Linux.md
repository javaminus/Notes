# Linux 常用命令 + 参数详细注释

## 文件和目录操作

```bash
ls -al
# ls：列出目录内容
# -a：显示所有文件，包括隐藏文件（以.开头的）
# -l：使用长列表格式，显示详细信息
```

```bash
cd /var/log
# cd：切换目录
# /var/log：目标目录路径
```

```bash
pwd
# pwd：显示当前目录的完整路径
```

```bash
mkdir my_project
# mkdir：创建目录
# my_project：新目录名
```

```bash
rm -r my_dir
# rm：删除文件或目录
# -r：递归删除，目录及其内容一起删
# my_dir：目标目录
```

```bash
cp -r src_dir/ dest_dir/
# cp：复制文件或目录
# -r：递归复制目录及内容
# src_dir/：源目录
# dest_dir/：目标目录
```

```bash
mv old_name.txt new_name.txt
# mv：移动或重命名文件/目录
# old_name.txt：原文件名
# new_name.txt：新文件名
```

```bash
touch new_file.log
# touch：创建空文件或更新文件时间戳
# new_file.log：文件名
```

```bash
find /home -name "*.py"
# find：查找文件
# /home：在/home目录下查找
# -name "*.py"：文件名匹配.py后缀
```

```bash
du -sh /var/log
# du：显示磁盘使用情况
# -s：只显示总计
# -h：人类可读的格式（如 MB, GB）
# /var/log：目标目录
```

```bash
df -h
# df：显示文件系统磁盘空间情况
# -h：人类可读格式
```

## 文本处理

```bash
grep "error" app.log
# grep：在文件中查找匹配行
# "error"：要搜索的字符串
# app.log：目标文件
```

```bash
grep -C 2 "ERROR" app.log
# -C 2：上下各显示2行
```

```bash
sed 's/foo/bar/g' file.txt
# sed：流编辑器
# 's/foo/bar/g'：将foo替换为bar（g为全局替换）
# file.txt：目标文件
```

```bash
awk '{print $1, $3}' data.txt
# awk：文本处理工具
# '{print $1, $3}'：打印每行的第1和第3列
# data.txt：目标文件
```

```bash
sort names.txt
# sort：排序
# names.txt：目标文件
```

```bash
uniq -c
# uniq：去重
# -c：统计每行出现次数
```

```bash
wc -l file.txt
# wc：统计行数/字数/字节数
# -l：只统计行数
# file.txt：目标文件
```

## 系统信息和监控

```bash
uname -a
# uname：显示系统信息
# -a：显示所有信息
```

```bash
top
# top：实时系统监控，显示进程/资源使用
```

```bash
free -h
# free：显示内存使用
# -h：人类可读格式
```

```bash
ps aux
# ps：显示进程状态
# a：显示所有用户进程
# u：显示详细信息（用户等）
# x：显示无终端的进程
```

## 进程管理

```bash
kill 12345
# kill：向进程发送信号（默认SIGTERM终止）
# 12345：进程ID
```

```bash
kill -9 12345
# -9：发送SIGKILL信号，强制终止
```

```bash
pkill -f "python my_app.py"
# pkill：按进程名匹配并杀死
# -f：匹配完整命令行
# "python my_app.py"：匹配内容
```

## 网络命令

```bash
ping google.com
# ping：测试网络连通性
# google.com：目标主机
```

```bash
ip addr show
# ip：网络管理命令
# addr show：显示所有网络接口IP地址
```

```bash
netstat -tuln
# netstat：显示网络相关信息
# -t：显示TCP协议
# -u：显示UDP协议
# -l：只显示监听端口
# -n：以数字方式显示地址和端口号
```

```bash
ssh user@remote_host
# ssh：安全远程登录
# user@remote_host：用户名和主机
```

```bash
scp file.txt user@remote:/home/user/
# scp：安全复制
# file.txt：本地文件
# user@remote:/home/user/：目标主机及目录
```

## 权限管理

```bash
chmod 755 script.sh
# chmod：更改权限
# 755：所有者读写执行(7)，组和其他读执行(5)
# script.sh：目标文件
```

```bash
chown user:group file.txt
# chown：更改文件所有者和组
# user:group：新用户和组
# file.txt：目标文件
```

## 压缩和归档

```bash
tar -czvf archive.tar.gz /path/to/dir
# tar：归档工具
# -c：创建归档
# -z：通过gzip压缩
# -v：显示详细过程
# -f：指定归档文件名
# archive.tar.gz：归档目标文件名
# /path/to/dir：要归档的目录
```

```bash
tar -xzvf archive.tar.gz
# -x：解包归档
# -z：通过gzip解压
# -v：显示详细过程
# -f：指定归档文件名
```

```bash
gzip file.txt
# gzip：压缩文件
# file.txt：目标文件
```

```bash
gunzip file.txt.gz
# gunzip：解压.gz文件
# file.txt.gz：压缩文件
```

## 其他常用命令

```bash
sudo apt-get update
# sudo：以管理员权限执行
# apt-get：包管理工具
# update：更新软件包索引
```

```bash
man ls
# man：查看命令手册
# ls：目标命令
```

```bash
echo "Hello, World!" > greeting.txt
# echo：输出文本
# "Hello, World!"：要输出的内容
# > greeting.txt：重定向到greeting.txt文件
```

```bash
ps aux | grep "nginx"
# ps aux：显示所有进程
# |：管道，将前面输出作为后面输入
# grep "nginx"：过滤包含nginx的行
```

---

> 面试时，能够解释每个参数的作用，会让你加分不少！

# 高频Linux面试题（含详细参数注释）

---

## 1. 如何查看当前系统的内核版本和系统类型？  
**答：**  
```bash
uname -a
# uname：显示系统信息
# -a：显示所有可用的信息（包含内核名、主机名、内核版本、时间、架构等）
```

---

## 2. 如何查看当前所有正在运行的进程？  
**答：**  
```bash
ps aux
# ps：显示进程状态
# a：显示所有用户的进程
# u：以用户为主显示详细信息
# x：显示无控制终端的进程
```

---

## 3. 如何查找端口被哪个进程占用？  
**答：**  
```bash
netstat -tulnp | grep 80
# netstat：显示网络相关信息
# -t：显示TCP协议
# -u：显示UDP协议
# -l：显示正在监听的端口
# -n：以数字显示地址和端口号
# -p：显示哪个进程占用了端口
# | grep 80：筛选出包含80端口的行
```

---

## 4. 如何统计文件data.txt的行数？  
**答：**  
```bash
wc -l data.txt
# wc：统计行数/字数/字节数
# -l：只显示行数
# data.txt：目标文件
```

---

## 5. 如何查看目录下文件按修改时间排序？  
**答：**  
```bash
ls -lt
# ls：列出目录内容
# -l：长列表显示
# -t：按时间排序（最近修改的在前）
```

---

## 6. 如何递归查找并批量替换文本内容？

**答：**  
```bash
grep -rl 'foo' . | xargs sed -i 's/foo/bar/g'
# grep：查找内容
# -r：递归查找
# -l：只输出匹配的文件名
# 'foo'：要查找的字符串
# .：当前目录
# xargs：把前面输出作为后面命令的参数
# sed -i：直接修改文件内容
# 's/foo/bar/g'：替换foo为bar，g表示全局替换
```

---

## 7. 如何查看系统内存使用情况？  
**答：**  
```bash
free -h
# free：显示内存使用情况
# -h：以人类可读格式显示（如MB/GB）
```

---

## 8. 如何查看磁盘空间使用情况？  
**答：**  
```bash
df -h
# df：显示各文件系统磁盘空间
# -h：以人类可读格式显示
```

---

## 9. 如何查找最近7天内被修改的文件？  
**答：**  
```bash
find . -type f -mtime -7
# find：查找文件
# .：当前目录
# -type f：只查找文件（file）
# -mtime -7：7天内被修改过的（-7指少于7天）
```

---

## 10. 如何列出系统中所有登录用户？  
**答：**  
```bash
who
# who：显示当前登录用户
```

---

## 11. 如何让某个脚本在后台持续运行，即使关闭终端也不断开？  
**答：**  
```bash
nohup ./myscript.sh &
# nohup：不挂断地运行命令
# ./myscript.sh：要运行的脚本
# &：放到后台运行
```

---

## 12. 如何查看端口开放情况（比如80端口）？  
**答：**  
```bash
ss -tnl | grep 80
# ss：显示套接字（socket）信息（比netstat快）
# -t：只显示TCP端口
# -n：以数字显示
# -l：只显示监听端口
# | grep 80：筛选80端口
```

---

## 13. 如何杀死所有与nginx相关的进程？  
**答：**  
```bash
pkill nginx
# pkill：按进程名杀进程
# nginx：进程名

kill pid
需要知道具体的进程号，适合单个进程操作
```

---

## 14. 如何查看文件内容的前10行和后10行？  
**答：**  
```bash
head -n 10 file.txt
# head：显示文件开头部分
# -n 10：显示前10行

tail -n 10 file.txt
# tail：显示文件结尾部分
# -n 10：显示后10行
```

---

## 15. 如何查看某命令的帮助信息？  
**答：**  
```bash
man ls
# man：查阅命令手册
# ls：要查的命令

ls --help
# --help：显示简要帮助信息
```

---

## 16. 如何批量解压当前目录下所有 tar.gz 文件？  
**答：**  
```bash
for i in *.tar.gz; do tar -xzvf "$i"; done
# for i in *.tar.gz; do ...; done：遍历所有tar.gz文件
# tar：归档解压工具
# -x：解包
# -z：解压缩（gzip格式）
# -v：显示详细过程
# -f "$i"：指定要操作的归档文件
```

---

## 17. 如何显示当前目录下所有文件及其大小？  
**答：**  
```bash
ls -lh
# -l：长列表
# -h：人类可读格式（如K/M/G）
```

---

## 18. 如何设置文件权限为所有者可读写执行，组和其他用户只读？  
**答：**  
```bash
chmod 744 file.txt
# chmod：修改权限
# 7（所有者）：读4+写2+执行1=7
# 4（组）：只读
# 4（其他）：只读
```

---

## 19. 如何递归删除一个目录及其所有内容？  
**答：**  
```bash
rm -rf mydir
# rm：删除
# -r：递归删除目录及内容
# -f：强制删除，无需确认
# mydir：目录名
```

---

## 20. 如何把当前shell下所有环境变量保存到一个文件？  
**答：**  
```bash
env > env.txt
# env：显示所有环境变量
# > env.txt：重定向到文件
```

---

## 21. 如何只显示当前的年月日？  
**答：**  
```bash
date "+%Y-%m-%d"
# date：显示日期时间
# "+%Y-%m-%d"：格式化输出，Y年m月d日
```

---

## 22. 如何统计文件中“error”出现的次数？  
**答：**  
```bash
grep -o "error" file.txt | wc -l
# grep -o：只输出匹配到的内容
# "error"：要查找的字符串
# file.txt：目标文件
# | wc -l：统计行数，即出现次数
```

---

## 23. 如何查看当前系统运行了多长时间？  
**答：**  
```bash
uptime
# uptime：显示系统已运行时间、用户数和负载
```

---

## 24. 如何查看最近登录失败记录？  
**答：**  
```bash
lastb
# lastb：显示失败登录记录
```

---

## 25. 如何查看进程树？  
**答：**  
```bash
pstree
# pstree：以树状结构显示进程
```

---

## 26. 如何查看某个用户的历史命令？  
**答：**  
```bash
cat /home/用户名/.bash_history
# .bash_history：用户的命令历史文件
```

---

## 27. 如何查看所有定时任务？  
**答：**  
```bash
crontab -l
# crontab：定时任务管理
# -l：列出当前用户的定时任务

cat /etc/crontab
# 查看系统级任务
```

---

## 28. 如何查看所有开放端口？  
**答：**  
```bash
netstat -tunlp
# -t：TCP
# -u：UDP
# -n：数字显示
# -l：监听
# -p：显示进程
```

---

## 29. 如何查看本机IP地址？  
**答：**  
```bash
ip addr show
# ip：网络配置工具
# addr show：显示所有IP地址信息
```

---

## 30. 如何压缩和解压文件？  
**答：**  
```bash
tar -czvf archive.tar.gz dir/
# -c：创建归档
# -z：gzip压缩
# -v：详细显示过程
# -f：指定文件名

tar -xzvf archive.tar.gz
# -x：解包
# -z：解压
# -v：详细显示过程
# -f：指定文件名
```