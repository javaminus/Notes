# Git 面试题讲解与示例

---

## 1. Git 和 SVN 的区别

- **分布式 vs 集中式**：Git 是分布式版本控制系统，SVN 是集中式。
- **本地操作**：Git 支持所有操作本地完成，SVN 必须联网。
- **分支管理**：Git 分支轻量高效，SVN 分支重量级。
- **速度**：Git 操作快（本地），SVN 操作慢（依赖服务器）。

---

## 2. Git 工作区、暂存区、版本库的区别

- **工作区（Working Directory）**：你正在编辑的本地目录。
- **暂存区（Stage/Index）**：准备提交的数据区域。
- **版本库（Repository）**：保存所有历史提交的数据库（.git 目录）。

> **例子：**
> ```bash
> git add file.txt     # 工作区 -> 暂存区
> git commit -m "msg"  # 暂存区 -> 版本库
> ```

---

## 3. 常用 Git 命令及用途

- `git init`：初始化仓库
- `git clone <url>`：克隆远程仓库
- `git add <file>`：添加到暂存区
- `git commit -m "msg"`：提交到本地仓库
- `git status`：查看状态
- `git diff`：查看差异
- `git log`：查看历史提交
- `git branch`：列出分支
- `git checkout <branch>`：切换分支
- `git merge <branch>`：合并分支
- `git pull`：拉取代码
- `git push`：推送代码

---

## 4. 常见场景及操作

### 4.1 撤销操作

- 撤销文件修改：`git checkout -- <file>`
- 撤销暂存区文件：`git reset HEAD <file>`
- 撤销最近一次提交（保留修改）：`git reset --soft HEAD~1`

---

### 4.2 分支管理

- 创建新分支：`git branch new-feature`
- 切换分支：`git checkout new-feature`
- 合并分支：`git merge new-feature`
- 删除分支：`git branch -d new-feature`

---

### 4.3 解决冲突

- 合并时产生冲突，手动编辑冲突文件，再 `git add`，`git commit`。

---

### 4.4 回滚操作

- 回滚到某个 commit（保留历史）：`git revert <commit_id>`
- 强制回退到某个 commit（丢失历史）：`git reset --hard <commit_id>`

---

### 4.5 远程操作

- 添加远程仓库：`git remote add origin <url>`
- 查看远程仓库：`git remote -v`
- 推送本地分支：`git push origin master`
- 跟踪远程分支：`git checkout -b dev origin/dev`

---

## 5. Git 分支模型及工作流

- **Git Flow**：master/develop/feature/release/hotfix 多分支协作
- **Fork & Pull Request**：开源社区协作模式
- **Trunk-Based**：主干开发，feature分支短期存在

---

## 6. Git Rebase 与 Merge 的区别

- `merge`：保留分支历史，产生合并 commit
- `rebase`：将分支上的提交“平铺”在目标分支之后，历史更线性

> **例子：**
> ```bash
> git rebase master
> # vs
> git merge master
> ```



当然可以，下面用通俗语言和形象例子帮你理解`merge`和`rebase`的区别：

---

1. `git merge` —— 把两条线拧成一股绳

- **做什么？**  
  把A分支的内容合并到B分支上，**保留两条分支各自的历史**，并生成一个“合并点”（merge commit）。
- **历史长什么样？**  
  像两条河流汇合，分支线条清晰可见。
- **优点**：历史真实，开发过程完整可追溯。
- **缺点**：历史会出现多条分支和合并节点，看起来比较“乱”。

**举例：**
```
A: --a1--a2--
               \
B: ------b1--b2--M   (M为合并节点)
```

---

2. `git rebase` —— 把分叉的线“接”到主线上，像没分过叉一样

- **做什么？**  
  把A分支的改动“剪切”下来，**平铺到B分支最后面**，历史被串成一条直线，好像A分支是从B分支末尾直接开发的一样。
- **历史长什么样？**  
  看起来像是一条直线，没有分叉。
- **优点**：历史简洁，提交顺序清晰。
- **缺点**：会“篡改”已有提交的历史，不适合在公共分支操作。

**举例：**
```
A: --a1--a2--
               \
B: ------b1--b2--a1'--a2'   (a1',a2'是a1,a2的新副本)
```



- **merge**：  
  就像两队人马各自走自己的路，最后在一个大路口集合，大家的足迹都看得见。
- **rebase**：  
  就像你把自己走的小路移到大路的尽头，好像你是一直跟在主队后面走的，别人看不出你分过叉。



> merge保留分叉历史，rebase让历史变直线。

---

如果你喜欢可视化，可以在本地多做几次`merge`和`rebase`，用`git log --oneline --graph`看看历史结构就会很直观啦！

## 7. tag（标签）的作用及用法

- 标记重要节点（如版本发布）
- `git tag v1.0`
- `git push origin v1.0`

---

## 8. .gitignore 用法

- 排除不需要纳入版本控制的文件
- 常见如 log、编译产物、IDE配置等

> **例子：**
> ```
> node_modules/
> *.class
> *.log
> ```

---

## 9. 如何恢复误删文件？

1. 如果未 add：`git checkout -- <file>`
2. 如果 add 但未 commit：`git reset HEAD <file>`，再 `git checkout -- <file>`
3. 如果已 commit：`git log` 找到 commit id，`git checkout <commit_id> -- <file>`

---

## 10. 子模块（submodule）是什么？

- 用来管理项目依赖的外部 Git 仓库，如依赖第三方库
- `git submodule add <url> path`
- `git submodule update --init --recursive`

---

## 11. Git Hooks 是什么？

- 自动化脚本，触发如 commit、push、merge 等生命周期
- 位于 `.git/hooks/` 目录，如 `pre-commit`、`pre-push`

---

## 12. cherry-pick 的作用

- 将指定 commit 应用到当前分支
- 常用于补丁、热修复回溯
- `git cherry-pick <commit_id>`

---

## 13. 如何删除远程分支？

- `git push origin --delete branch_name`

---

## 14. 常见面试题答题思路

- **冲突原因和解决办法？**  
  代码修改重叠导致，需手动合并冲突后提交。

- **如何回滚线上误操作？**  
  建议用`revert`而不是`reset`，避免历史丢失。

- **分支策略选型？**  
  说明 Git Flow、主干开发、PR 模式等适合场景。

- **rebase、merge、cherry-pick 区别？**  
  见第6条和第12条。

---

## 15. Trick 总结

- 用 `git stash` 暂存当前改动，临时切换分支。
- 用 `git bisect` 二分查找 bug 引入的 commit。
- 用 `git reflog` 恢复误删分支或 commit。

---

## 16. 参考题目

1. Git 的三区是什么？各自作用？
2. 如何处理合并冲突？
3. 误删分支/文件怎么恢复？
4. `git rebase` 的优缺点？
5. 如何 tag、如何打补丁？
6. 多人协作下如何保证代码质量？
7. 提交历史如何保持整洁？
8. submodule、hook、cherry-pick 的实际应用场景？

---

## 17. 实战例子

### 场景：开发新功能并合并到主分支

```bash
git checkout -b feature/login
# 开发&提交
git add .
git commit -m "add login feature"
# 合并到主分支
git checkout master
git merge feature/login
git push origin master
```

---

### 场景：线上的 bug 修复补丁迁移到其它分支

```bash
git checkout hotfix/bug
# 修复bug并提交
git commit -am "fix bug"
git checkout develop
git cherry-pick <bug-fix-commit>
```

---

## 18. 常见陷阱与注意事项

- 不要在公共分支随意 `git push --force`
- 合并前先 `git pull` 避免因版本落后产生冲突
- 保持 commit 信息清晰、规范

---

> **复习口诀：分布式、三区分，分支合并与回滚，冲突解决 cherry-pick，tag hook submodule全覆盖！**

---