# 综合SQL语法指南

## 目录
- [基本查询语法](#基本查询语法)
- [数据操作语言 (DML)](#数据操作语言-dml)
- [数据定义语言 (DDL)](#数据定义语言-ddl)
- [数据控制语言 (DCL)](#数据控制语言-dcl)
- [事务控制语言 (TCL)](#事务控制语言-tcl)
- [约束和键](#约束和键)
- [连接操作](#连接操作)
- [子查询](#子查询)
- [聚合函数](#聚合函数)
- [窗口函数](#窗口函数)
- [公共表表达式 (CTE)](#公共表表达式-cte)
- [视图](#视图)
- [存储过程](#存储过程)
- [触发器](#触发器)
- [索引](#索引)
- [SQL函数](#sql函数)

## 基本查询语法

### SELECT语句
```sql
-- 基本SELECT
SELECT column1, column2, ... 
FROM table_name;

-- 使用通配符选择所有列
SELECT * FROM table_name;

-- 使用DISTINCT去重
SELECT DISTINCT column1, column2, ...
FROM table_name;

-- WHERE子句进行过滤
SELECT column1, column2, ...
FROM table_name
WHERE condition;

-- ORDER BY排序
SELECT column1, column2, ...
FROM table_name
ORDER BY column1 [ASC|DESC], column2 [ASC|DESC], ...;

-- GROUP BY分组
SELECT column1, column2, ..., aggregate_function(column)
FROM table_name
GROUP BY column1, column2, ...;

-- HAVING子句（用于分组后的过滤）
SELECT column1, column2, ..., aggregate_function(column)
FROM table_name
GROUP BY column1, column2, ...
HAVING condition;

-- LIMIT和OFFSET分页（MySQL, PostgreSQL语法）
SELECT column1, column2, ...
FROM table_name
LIMIT number OFFSET number;

-- TOP子句（SQL Server语法）
SELECT TOP number column1, column2, ...
FROM table_name;

-- FETCH FIRST子句（标准SQL语法）
SELECT column1, column2, ...
FROM table_name
FETCH FIRST number ROWS ONLY;
```

## 数据操作语言 (DML)

### INSERT语句
```sql
-- 插入单行
INSERT INTO table_name (column1, column2, column3, ...)
VALUES (value1, value2, value3, ...);

-- 插入多行
INSERT INTO table_name (column1, column2, column3, ...)
VALUES 
    (value1, value2, value3, ...),
    (value4, value5, value6, ...),
    ...;

-- 从另一个表中插入数据
INSERT INTO table_name (column1, column2, column3, ...)
SELECT column1, column2, column3, ...
FROM another_table
WHERE condition;
```

### UPDATE语句
```sql
-- 更新表中的数据
UPDATE table_name
SET column1 = value1, column2 = value2, ...
WHERE condition;

-- 使用子查询更新
UPDATE table_name
SET column1 = (SELECT column_x FROM another_table WHERE condition)
WHERE condition;

-- 使用JOIN更新（某些数据库支持）
UPDATE table1 t1
JOIN table2 t2 ON t1.id = t2.id
SET t1.column1 = t2.column2
WHERE condition;
```

### DELETE语句
```sql
-- 删除满足条件的行
DELETE FROM table_name
WHERE condition;

-- 删除所有行
DELETE FROM table_name;

-- 使用子查询的DELETE
DELETE FROM table_name
WHERE column_name IN (SELECT column_name FROM another_table WHERE condition);

-- 使用JOIN的DELETE（某些数据库支持）
DELETE t1
FROM table1 t1
JOIN table2 t2 ON t1.id = t2.id
WHERE condition;
```

### MERGE语句（UPSERT）
```sql
-- 标准SQL语法（Oracle, SQL Server等支持）
MERGE INTO target_table t
USING source_table s
ON (t.key_column = s.key_column)
WHEN MATCHED THEN
    UPDATE SET t.column1 = s.column1, t.column2 = s.column2, ...
WHEN NOT MATCHED THEN
    INSERT (column1, column2, ...) VALUES (s.column1, s.column2, ...);

-- MySQL语法
INSERT INTO table_name (key_column, column1, column2, ...)
VALUES (key_value, value1, value2, ...)
ON DUPLICATE KEY UPDATE
    column1 = value1,
    column2 = value2,
    ...;

-- PostgreSQL语法
INSERT INTO table_name (key_column, column1, column2, ...)
VALUES (key_value, value1, value2, ...)
ON CONFLICT (key_column)
DO UPDATE SET
    column1 = value1,
    column2 = value2,
    ...;
```

## 数据定义语言 (DDL)

### CREATE语句
```sql
-- 创建数据库
CREATE DATABASE database_name;

-- 创建表
CREATE TABLE table_name (
    column1 datatype [constraint],
    column2 datatype [constraint],
    column3 datatype [constraint],
    ...
    [table_constraint1],
    [table_constraint2],
    ...
);

-- 创建表示例
CREATE TABLE employees (
    employee_id INT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    hire_date DATE DEFAULT CURRENT_DATE,
    salary DECIMAL(10,2) CHECK (salary > 0),
    department_id INT,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- 创建临时表
CREATE TEMPORARY TABLE temp_table (
    column1 datatype,
    column2 datatype,
    ...
);

-- 从现有表创建表
CREATE TABLE new_table AS
SELECT column1, column2, ...
FROM existing_table
WHERE condition;
```

### ALTER语句
```sql
-- 添加列
ALTER TABLE table_name
ADD column_name datatype [constraint];

-- 修改列数据类型
ALTER TABLE table_name
ALTER COLUMN column_name TYPE new_datatype;

-- 修改列（SQL Server语法）
ALTER TABLE table_name
ALTER COLUMN column_name new_datatype;

-- 重命名列（不同数据库有不同语法）
ALTER TABLE table_name
RENAME COLUMN old_column_name TO new_column_name;

-- 删除列
ALTER TABLE table_name
DROP COLUMN column_name;

-- 添加约束
ALTER TABLE table_name
ADD CONSTRAINT constraint_name constraint_definition;

-- 删除约束
ALTER TABLE table_name
DROP CONSTRAINT constraint_name;

-- 添加外键
ALTER TABLE table_name
ADD CONSTRAINT fk_name FOREIGN KEY (column_name) REFERENCES ref_table(ref_column);

-- 修改表名
ALTER TABLE old_table_name
RENAME TO new_table_name;
```

### DROP语句
```sql
-- 删除表
DROP TABLE [IF EXISTS] table_name;

-- 删除数据库
DROP DATABASE [IF EXISTS] database_name;

-- 删除索引
DROP INDEX [IF EXISTS] index_name;

-- 删除视图
DROP VIEW [IF EXISTS] view_name;

-- 删除存储过程
DROP PROCEDURE [IF EXISTS] procedure_name;

-- 删除触发器
DROP TRIGGER [IF EXISTS] trigger_name;

-- 删除函数
DROP FUNCTION [IF EXISTS] function_name;
```

### TRUNCATE语句
```sql
-- 快速删除所有行（无法回滚）
TRUNCATE TABLE table_name;
```

## 数据控制语言 (DCL)

### GRANT语句
```sql
-- 授予数据库权限
GRANT privilege [, ...] 
ON database_name
TO role_name;

-- 授予表权限
GRANT privilege [, ...] 
ON table_name
TO role_name;

-- 授予列权限
GRANT privilege (column1, column2, ...) 
ON table_name
TO role_name;

-- 授予所有权限
GRANT ALL PRIVILEGES
ON table_name
TO role_name;

-- 授权传播权限
GRANT privilege [, ...] 
ON object_name
TO role_name
WITH GRANT OPTION;
```

### REVOKE语句
```sql
-- 撤销数据库权限
REVOKE privilege [, ...] 
ON database_name
FROM role_name;

-- 撤销表权限
REVOKE privilege [, ...] 
ON table_name
FROM role_name;

-- 撤销所有权限
REVOKE ALL PRIVILEGES
ON table_name
FROM role_name;

-- 撤销授权传播权限
REVOKE GRANT OPTION FOR privilege [, ...]
ON object_name
FROM role_name;
```

### CREATE USER/ROLE语句
```sql
-- 创建用户
CREATE USER user_name 
IDENTIFIED BY 'password';

-- 创建角色
CREATE ROLE role_name;

-- 将角色分配给用户
GRANT role_name TO user_name;
```

## 事务控制语言 (TCL)

### 事务控制
```sql
-- 开始事务
BEGIN TRANSACTION;  -- 或 START TRANSACTION; (MySQL和PostgreSQL)
BEGIN; -- 简化形式

-- 提交事务
COMMIT;

-- 回滚事务
ROLLBACK;

-- 保存点
SAVEPOINT savepoint_name;

-- 回滚到保存点
ROLLBACK TO SAVEPOINT savepoint_name;

-- 释放保存点
RELEASE SAVEPOINT savepoint_name;

-- 设置事务隔离级别
SET TRANSACTION ISOLATION LEVEL {
    READ UNCOMMITTED |
    READ COMMITTED |
    REPEATABLE READ |
    SERIALIZABLE
};
```

## 约束和键

### 主键、外键和其他约束
```sql
-- 主键约束
CREATE TABLE table_name (
    id INT PRIMARY KEY,
    -- 其他列
);

-- 复合主键
CREATE TABLE table_name (
    column1 datatype,
    column2 datatype,
    -- 其他列
    PRIMARY KEY (column1, column2)
);

-- 外键约束
CREATE TABLE child_table (
    id INT PRIMARY KEY,
    parent_id INT,
    -- 其他列
    FOREIGN KEY (parent_id) REFERENCES parent_table(id)
);

-- 带有级联操作的外键
CREATE TABLE child_table (
    id INT PRIMARY KEY,
    parent_id INT,
    -- 其他列
    FOREIGN KEY (parent_id) 
        REFERENCES parent_table(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- 唯一约束
CREATE TABLE table_name (
    column1 datatype UNIQUE,
    -- 其他列
);

-- 复合唯一约束
CREATE TABLE table_name (
    column1 datatype,
    column2 datatype,
    -- 其他列
    CONSTRAINT constraint_name UNIQUE (column1, column2)
);

-- 检查约束
CREATE TABLE table_name (
    column1 datatype CHECK (condition),
    -- 例如：age INT CHECK (age >= 18)
    -- 其他列
);

-- NOT NULL约束
CREATE TABLE table_name (
    column1 datatype NOT NULL,
    -- 其他列
);

-- 默认值约束
CREATE TABLE table_name (
    column1 datatype DEFAULT default_value,
    -- 例如：created_date DATE DEFAULT CURRENT_DATE
    -- 其他列
);
```

## 连接操作

### 各种JOIN类型
```sql
-- 内连接
SELECT t1.column, t2.column
FROM table1 t1
INNER JOIN table2 t2 ON t1.common_field = t2.common_field;

-- 左外连接
SELECT t1.column, t2.column
FROM table1 t1
LEFT [OUTER] JOIN table2 t2 ON t1.common_field = t2.common_field;

-- 右外连接
SELECT t1.column, t2.column
FROM table1 t1
RIGHT [OUTER] JOIN table2 t2 ON t1.common_field = t2.common_field;

-- 全外连接
SELECT t1.column, t2.column
FROM table1 t1
FULL [OUTER] JOIN table2 t2 ON t1.common_field = t2.common_field;

-- 交叉连接（笛卡尔积）
SELECT t1.column, t2.column
FROM table1 t1
CROSS JOIN table2 t2;

-- 自连接
SELECT e1.name AS employee, e2.name AS manager
FROM employees e1
JOIN employees e2 ON e1.manager_id = e2.employee_id;

-- 自然连接（基于同名列自动连接）
SELECT t1.column, t2.column
FROM table1 t1
NATURAL JOIN table2 t2;

-- 使用USING简化连接条件（当连接列同名时）
SELECT t1.column, t2.column
FROM table1 t1
JOIN table2 t2 USING (common_field);

-- 多表连接
SELECT t1.column, t2.column, t3.column
FROM table1 t1
JOIN table2 t2 ON t1.field = t2.field
JOIN table3 t3 ON t2.field = t3.field;
```

## 子查询

### 子查询类型和用法
```sql
-- WHERE子句中的子查询
SELECT column1, column2
FROM table1
WHERE column1 IN (SELECT column1 FROM table2 WHERE condition);

-- FROM子句中的子查询（派生表）
SELECT t.column1, t.column2
FROM (SELECT column1, column2 FROM table1 WHERE condition) AS t;

-- SELECT子句中的标量子查询
SELECT column1, 
       (SELECT AVG(column2) FROM table2 WHERE table2.id = table1.id) AS avg_value
FROM table1;

-- HAVING子句中的子查询
SELECT column1, COUNT(*)
FROM table1
GROUP BY column1
HAVING COUNT(*) > (SELECT AVG(count) FROM (
    SELECT column1, COUNT(*) AS count 
    FROM table1 
    GROUP BY column1
) AS subquery);

-- EXISTS子查询
SELECT column1
FROM table1 t1
WHERE EXISTS (SELECT 1 FROM table2 t2 WHERE t2.id = t1.id AND condition);

-- NOT EXISTS子查询
SELECT column1
FROM table1 t1
WHERE NOT EXISTS (SELECT 1 FROM table2 t2 WHERE t2.id = t1.id);

-- 比较运算符与子查询
SELECT column1
FROM table1
WHERE column1 > ANY (SELECT column1 FROM table2 WHERE condition);

-- ALL运算符与子查询
SELECT column1
FROM table1
WHERE column1 > ALL (SELECT column1 FROM table2 WHERE condition);

-- 相关子查询
SELECT column1, column2
FROM table1 t1
WHERE column1 > (SELECT AVG(column1) FROM table1 t2 WHERE t2.group_id = t1.group_id);

-- INSERT语句中的子查询
INSERT INTO table1 (column1, column2)
SELECT column1, column2
FROM table2
WHERE condition;

-- UPDATE语句中的子查询
UPDATE table1
SET column1 = (SELECT column2 FROM table2 WHERE table2.id = table1.id)
WHERE condition;

-- 多列子查询
SELECT column1, column2
FROM table1
WHERE (column1, column2) IN (SELECT column1, column2 FROM table2 WHERE condition);
```

## 聚合函数

### 常用聚合函数
```sql
-- COUNT函数
SELECT COUNT(*) FROM table_name;
SELECT COUNT(column_name) FROM table_name; -- 不计算NULL值
SELECT COUNT(DISTINCT column_name) FROM table_name; -- 计算唯一值的数量

-- SUM函数
SELECT SUM(column_name) FROM table_name;

-- AVG函数
SELECT AVG(column_name) FROM table_name;

-- MIN函数
SELECT MIN(column_name) FROM table_name;

-- MAX函数
SELECT MAX(column_name) FROM table_name;

-- 多个聚合函数
SELECT 
    COUNT(*) as total_count,
    SUM(column1) as total_sum,
    AVG(column1) as average,
    MIN(column1) as minimum,
    MAX(column1) as maximum
FROM table_name;

-- 使用GROUP BY进行分组聚合
SELECT column1, COUNT(*), SUM(column2)
FROM table_name
GROUP BY column1;

-- 多列分组
SELECT column1, column2, COUNT(*)
FROM table_name
GROUP BY column1, column2;

-- 使用HAVING筛选分组
SELECT column1, COUNT(*)
FROM table_name
GROUP BY column1
HAVING COUNT(*) > 5;

-- 字符串聚合（数据库特定）
-- MySQL
SELECT GROUP_CONCAT(column_name SEPARATOR ', ') FROM table_name GROUP BY group_column;
-- PostgreSQL
SELECT string_agg(column_name, ', ') FROM table_name GROUP BY group_column;
-- Oracle
SELECT LISTAGG(column_name, ', ') WITHIN GROUP (ORDER BY column_name) FROM table_name GROUP BY group_column;
-- SQL Server
SELECT STRING_AGG(column_name, ', ') FROM table_name GROUP BY group_column;

-- 条件聚合
SELECT 
    SUM(CASE WHEN condition THEN column1 ELSE 0 END) AS conditional_sum,
    COUNT(CASE WHEN condition THEN 1 ELSE NULL END) AS conditional_count
FROM table_name;
```

## 窗口函数

没问题，我再用更简单的话解释一下窗口函数：

窗口函数就是一种可以在SQL查询结果的每一行上，计算一组行的统计值（比如排名、累计和、平均值等）的函数。它不会像GROUP BY那样把多行合成一行，而是能在每一行旁边增加一个统计结果，非常适合做排名、分组内排序、累计等分析。

比如你想查每个学生的成绩排名，可以这样写：

```sql
SELECT name, score, 
       RANK() OVER (ORDER BY score DESC) AS rank
FROM students;
```

这样，每个学生的成绩排名就会在结果表中单独显示，不会影响原本每行的数据。

**核心点：**
- 不会合并行，原有数据还在
- 可以做排名、累计等分析
- 通过 OVER 子句定义统计范围

如果还有不懂的地方，可以告诉我你具体哪里不理解，我再详细解释！

### 基本窗口函数
```sql
-- 基本语法
SELECT column1, column2,
       window_function(expression) OVER (
           [PARTITION BY partition_column]
           [ORDER BY sort_column [ASC|DESC]]
           [frame_clause]
       )
FROM table_name;

-- ROW_NUMBER()：为每一行分配唯一的序号
SELECT column1, column2, 
       ROW_NUMBER() OVER (ORDER BY column2) AS row_num
FROM table_name;

-- RANK()：为排序分配排名（相同值获得相同排名，排名不连续）
SELECT column1, column2, 
       RANK() OVER (ORDER BY column2) AS rank_num
FROM table_name;

-- DENSE_RANK()：为排序分配排名（相同值获得相同排名，排名连续）
SELECT column1, column2, 
       DENSE_RANK() OVER (ORDER BY column2) AS dense_rank_num
FROM table_name;

-- NTILE(n)：将有序数据分成n个桶
SELECT column1, column2, 
       NTILE(4) OVER (ORDER BY column2) AS quartile
FROM table_name;

-- 分区窗口函数
SELECT column1, column2, column3,
       ROW_NUMBER() OVER (PARTITION BY column1 ORDER BY column2) AS row_num_per_group
FROM table_name;

-- 多列排序窗口函数
SELECT column1, column2, column3,
       RANK() OVER (ORDER BY column2, column3) AS rank_num
FROM table_name;
```

### 高级窗口函数
```sql
-- 聚合窗口函数
SELECT column1, column2,
       SUM(column2) OVER (PARTITION BY column1) AS group_sum,
       AVG(column2) OVER (PARTITION BY column1) AS group_avg,
       COUNT(*) OVER (PARTITION BY column1) AS group_count,
       MIN(column2) OVER (PARTITION BY column1) AS group_min,
       MAX(column2) OVER (PARTITION BY column1) AS group_max
FROM table_name;

-- 滑动聚合窗口函数（前n行和当前行）
SELECT column1, column2,
       SUM(column2) OVER (ORDER BY column1 ROWS BETWEEN n PRECEDING AND CURRENT ROW) AS moving_sum,
       AVG(column2) OVER (ORDER BY column1 ROWS BETWEEN n PRECEDING AND CURRENT ROW) AS moving_avg
FROM table_name;

-- 滑动聚合窗口函数（前n行和后m行）
SELECT column1, column2,
       SUM(column2) OVER (ORDER BY column1 ROWS BETWEEN n PRECEDING AND m FOLLOWING) AS window_sum,
       AVG(column2) OVER (ORDER BY column1 ROWS BETWEEN n PRECEDING AND m FOLLOWING) AS window_avg
FROM table_name;

-- 范围窗口（当排序值相同时，所有这些行被视为同一行）
SELECT column1, column2,
       SUM(column2) OVER (ORDER BY column1 RANGE BETWEEN n PRECEDING AND CURRENT ROW) AS range_sum
FROM table_name;

-- 无界窗口（从分区第一行到当前行）
SELECT column1, column2,
       SUM(column2) OVER (PARTITION BY column1 ORDER BY column2 ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS running_sum
FROM table_name;

-- 全窗口（整个分区）
SELECT column1, column2,
       SUM(column2) OVER (PARTITION BY column1 ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS total_sum
FROM table_name;

-- LEAD()：访问当前行后面的行
SELECT column1, column2,
       LEAD(column2, 1) OVER (ORDER BY column1) AS next_value,
       LEAD(column2, 1, 0) OVER (ORDER BY column1) AS next_value_with_default -- 第三个参数为默认值
FROM table_name;

-- LAG()：访问当前行前面的行
SELECT column1, column2,
       LAG(column2, 1) OVER (ORDER BY column1) AS previous_value,
       LAG(column2, 1, 0) OVER (ORDER BY column1) AS previous_value_with_default -- 第三个参数为默认值
FROM table_name;

-- FIRST_VALUE()：获取窗口框架的第一个值
SELECT column1, column2,
       FIRST_VALUE(column2) OVER (PARTITION BY column1 ORDER BY column2) AS first_value
FROM table_name;

-- LAST_VALUE()：获取窗口框架的最后一个值（需要适当的框架子句才能有用）
SELECT column1, column2,
       LAST_VALUE(column2) OVER (
           PARTITION BY column1 
           ORDER BY column2
           ROWS BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING
       ) AS last_value
FROM table_name;

-- NTH_VALUE()：获取窗口框架中第N个值
SELECT column1, column2,
       NTH_VALUE(column2, 2) OVER (
           PARTITION BY column1 
           ORDER BY column2
           ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
       ) AS second_value
FROM table_name;

-- PERCENT_RANK()：计算排名的百分比排名
SELECT column1, column2,
       PERCENT_RANK() OVER (ORDER BY column2) AS percent_rank
FROM table_name;

-- CUME_DIST()：计算累积分布
SELECT column1, column2,
       CUME_DIST() OVER (ORDER BY column2) AS cume_dist
FROM table_name;

-- 百分位计算
SELECT column1, column2,
       PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY column2) OVER (PARTITION BY column1) AS median
FROM table_name;

-- 多个窗口函数在同一查询中
SELECT column1, column2,
       ROW_NUMBER() OVER w AS row_num,
       RANK() OVER w AS rank_num,
       SUM(column2) OVER w AS running_sum
FROM table_name
WINDOW w AS (PARTITION BY column1 ORDER BY column2);
```

## 公共表表达式 (CTE)

### CTE语法和用法
```sql
-- 基本CTE语法
WITH cte_name AS (
    SELECT column1, column2, ...
    FROM table_name
    WHERE condition
)
SELECT column1, column2, ...
FROM cte_name;

-- 多个CTE
WITH cte1 AS (
    SELECT column1, column2, ...
    FROM table1
    WHERE condition1
),
cte2 AS (
    SELECT column1, column2, ...
    FROM table2
    WHERE condition2
)
SELECT c1.column1, c2.column2
FROM cte1 c1
JOIN cte2 c2 ON c1.common_field = c2.common_field;

-- 递归CTE
WITH RECURSIVE cte_name AS (
    -- 基础查询（非递归部分）
    SELECT initial_column1, initial_column2, ...
    FROM initial_table
    WHERE condition
    
    UNION [ALL]
    
    -- 递归查询部分
    SELECT recursive_column1, recursive_column2, ...
    FROM recursive_table r
    JOIN cte_name c ON r.join_column = c.join_column
    WHERE recursive_condition
)
SELECT column1, column2, ...
FROM cte_name;

-- 递归CTE示例（生成数列）
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 10
)
SELECT n FROM numbers;

-- 递归CTE示例（层次查询）
WITH RECURSIVE org_hierarchy AS (
    -- 基础查询（找出所有顶级员工）
    SELECT employee_id, employee_name, manager_id, 1 AS level
    FROM employees
    WHERE manager_id IS NULL
    
    UNION ALL
    
    -- 递归查询（找出所有下级员工）
    SELECT e.employee_id, e.employee_name, e.manager_id, h.level + 1
    FROM employees e
    JOIN org_hierarchy h ON e.manager_id = h.employee_id
)
SELECT * FROM org_hierarchy;
```

## 视图

### 视图创建和管理
```sql
-- 创建视图
CREATE VIEW view_name AS
SELECT column1, column2, ...
FROM table_name
WHERE condition;

-- 创建或替换视图
CREATE OR REPLACE VIEW view_name AS
SELECT column1, column2, ...
FROM table_name
WHERE condition;

-- 创建临时视图
CREATE TEMPORARY VIEW view_name AS
SELECT column1, column2, ...
FROM table_name;

-- 带有检查选项的视图
CREATE VIEW view_name AS
SELECT column1, column2, ...
FROM table_name
WHERE condition
WITH CHECK OPTION;

-- 物化视图（某些数据库支持，如Oracle, PostgreSQL）
CREATE MATERIALIZED VIEW mat_view_name AS
SELECT column1, column2, ...
FROM table_name
WHERE condition;

-- 刷新物化视图
REFRESH MATERIALIZED VIEW mat_view_name;

-- 从视图查询
SELECT column1, column2, ...
FROM view_name
WHERE condition;

-- 删除视图
DROP VIEW [IF EXISTS] view_name;

-- 修改视图
ALTER VIEW view_name AS
SELECT column1, column2, ...
FROM table_name
WHERE new_condition;
```

## 存储过程

### 创建和使用存储过程
```sql
-- MySQL存储过程
DELIMITER //
CREATE PROCEDURE procedure_name(
    IN parameter1 datatype,
    OUT parameter2 datatype,
    INOUT parameter3 datatype
)
BEGIN
    -- 存储过程体
    DECLARE variable_name datatype DEFAULT default_value;
    
    -- SQL语句
    SELECT column1, column2 INTO variable_name, parameter2
    FROM table_name
    WHERE column1 = parameter1;
    
    -- 条件语句
    IF condition THEN
        -- 语句
    ELSEIF another_condition THEN
        -- 语句
    ELSE
        -- 语句
    END IF;
    
    -- 循环
    WHILE condition DO
        -- 语句
    END WHILE;
    
    -- 设置输出参数
    SET parameter3 = new_value;
END //
DELIMITER ;

-- SQL Server存储过程
CREATE PROCEDURE procedure_name
    @parameter1 datatype,
    @parameter2 datatype OUTPUT,
    @parameter3 datatype
AS
BEGIN
    -- 变量声明
    DECLARE @variable_name datatype;
    
    -- SQL语句
    SELECT @variable_name = column1, @parameter2 = column2
    FROM table_name
    WHERE column1 = @parameter1;
    
    -- 条件语句
    IF condition
    BEGIN
        -- 语句
    END
    ELSE
    BEGIN
        -- 语句
    END
    
    -- 循环
    WHILE condition
    BEGIN
        -- 语句
    END
END;

-- PostgreSQL存储过程
CREATE OR REPLACE PROCEDURE procedure_name(
    parameter1 datatype,
    INOUT parameter2 datatype,
    parameter3 datatype
)
LANGUAGE plpgsql
AS $$
DECLARE
    variable_name datatype;
BEGIN
    -- SQL语句
    SELECT column1, column2 INTO variable_name, parameter2
    FROM table_name
    WHERE column1 = parameter1;
    
    -- 条件语句
    IF condition THEN
        -- 语句
    ELSIF another_condition THEN
        -- 语句
    ELSE
        -- 语句
    END IF;
    
    -- 循环
    WHILE condition LOOP
        -- 语句
    END LOOP;
END;
$$;

-- 调用存储过程
-- MySQL
CALL procedure_name(param1, @param2, @param3);
SELECT @param2, @param3;

-- SQL Server
DECLARE @result datatype;
EXEC procedure_name 'param1', @result OUTPUT, 'param3';
SELECT @result;

-- PostgreSQL
CALL procedure_name('param1', 'param2', 'param3');
```

## 触发器

### 创建和使用触发器
```sql
-- MySQL触发器
DELIMITER //
CREATE TRIGGER trigger_name
{BEFORE | AFTER} {INSERT | UPDATE | DELETE}
ON table_name
FOR EACH ROW
BEGIN
    -- 触发器体
    -- NEW表示新值，OLD表示旧值
    IF NEW.column1 > 100 THEN
        SET NEW.column1 = 100;
    END IF;
END //
DELIMITER ;

-- SQL Server触发器
CREATE TRIGGER trigger_name
ON table_name
{AFTER | INSTEAD OF} {INSERT | UPDATE | DELETE}
AS
BEGIN
    -- 触发器体
    -- 使用inserted和deleted表引用新旧值
    IF (SELECT COUNT(*) FROM inserted) > 0
    BEGIN
        -- INSERT或UPDATE操作
    END
    
    IF (SELECT COUNT(*) FROM deleted) > 0
    BEGIN
        -- DELETE或UPDATE操作
    END
END;

-- PostgreSQL触发器
-- 先创建触发器函数
CREATE OR REPLACE FUNCTION trigger_function()
RETURNS TRIGGER AS $$
BEGIN
    -- NEW引用新行，OLD引用旧行
    IF (TG_OP = 'DELETE') THEN
        -- 删除操作逻辑
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        -- 更新操作逻辑
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        -- 插入操作逻辑
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 然后创建触发器
CREATE TRIGGER trigger_name
{BEFORE | AFTER | INSTEAD OF} {INSERT | UPDATE | DELETE}
ON table_name
FOR EACH {ROW | STATEMENT}
EXECUTE FUNCTION trigger_function();

-- 删除触发器
DROP TRIGGER [IF EXISTS] trigger_name ON table_name;
```

## 索引

### 创建和管理索引
```sql
-- 创建基本索引
CREATE INDEX index_name
ON table_name (column1, column2, ...);

-- 创建唯一索引
CREATE UNIQUE INDEX index_name
ON table_name (column1, column2, ...);

-- 创建部分索引（仅PostgreSQL等支持）
CREATE INDEX index_name
ON table_name (column1, column2)
WHERE condition;

-- 创建函数索引
CREATE INDEX index_name
ON table_name (function(column));

-- 创建降序索引
CREATE INDEX index_name
ON table_name (column1 ASC, column2 DESC);

-- 创建全文索引
CREATE FULLTEXT INDEX index_name
ON table_name (column1, column2, ...);

-- 创建空间索引
CREATE SPATIAL INDEX index_name
ON table_name (geometry_column);

-- 使用INCLUDE为索引添加非键列（SQL Server, PostgreSQL等）
CREATE INDEX index_name
ON table_name (key_column)
INCLUDE (non_key_column1, non_key_column2);

-- 删除索引
DROP INDEX [IF EXISTS] index_name ON table_name;

-- 重建索引
ALTER INDEX index_name REBUILD;

-- 禁用索引（SQL Server）
ALTER INDEX index_name ON table_name DISABLE;

-- 启用索引（SQL Server）
ALTER INDEX index_name ON table_name REBUILD;
```

## SQL函数

### 字符串函数
```sql
-- 字符串连接
SELECT CONCAT(string1, string2, ...);
SELECT string1 || string2; -- Oracle, PostgreSQL等

-- 字符串长度
SELECT LENGTH(string);
SELECT LEN(string); -- SQL Server

-- 子字符串提取
SELECT SUBSTRING(string, start_position, length);
SELECT SUBSTR(string, start_position, length); -- Oracle
SELECT SUBSTRING(string FROM start_position FOR length); -- 标准SQL

-- 大小写转换
SELECT UPPER(string);
SELECT LOWER(string);

-- 去除空格
SELECT TRIM(string);
SELECT LTRIM(string); -- 去除左侧空格
SELECT RTRIM(string); -- 去除右侧空格

-- 替换
SELECT REPLACE(string, substring_to_replace, replacement);

-- 位置查找
SELECT POSITION(substring IN string);
SELECT INSTR(string, substring); -- Oracle
SELECT CHARINDEX(substring, string); -- SQL Server

-- 填充
SELECT LPAD(string, length, pad_string);
SELECT RPAD(string, length, pad_string);

-- 正则表达式
SELECT REGEXP_REPLACE(string, pattern, replacement); -- Oracle, PostgreSQL
SELECT REGEXP_SUBSTR(string, pattern); -- Oracle
SELECT string REGEXP pattern; -- MySQL
```

### 数值函数
```sql
-- 绝对值
SELECT ABS(number);

-- 向上取整
SELECT CEIL(number);
SELECT CEILING(number); -- SQL Server

-- 向下取整
SELECT FLOOR(number);

-- 四舍五入
SELECT ROUND(number, decimal_places);

-- 截断
SELECT TRUNCATE(number, decimal_places); -- MySQL
SELECT TRUNC(number, decimal_places); -- Oracle, PostgreSQL

-- 取余
SELECT MOD(number, divisor);
SELECT number % divisor; -- 某些数据库支持

-- 幂运算
SELECT POWER(base, exponent);

-- 平方根
SELECT SQRT(number);

-- 随机数
SELECT RAND(); -- MySQL
SELECT RANDOM(); -- PostgreSQL
SELECT DBMS_RANDOM.VALUE(); -- Oracle

-- 符号函数
SELECT SIGN(number);
```

### 日期和时间函数
```sql
-- 当前日期和时间
SELECT CURRENT_DATE;
SELECT CURRENT_TIME;
SELECT CURRENT_TIMESTAMP;
SELECT NOW();

-- 提取日期部分
SELECT EXTRACT(YEAR FROM date);
SELECT EXTRACT(MONTH FROM date);
SELECT EXTRACT(DAY FROM date);
SELECT EXTRACT(HOUR FROM timestamp);
SELECT EXTRACT(MINUTE FROM timestamp);
SELECT EXTRACT(SECOND FROM timestamp);

-- 格式化日期
SELECT TO_CHAR(date, 'YYYY-MM-DD'); -- Oracle, PostgreSQL
SELECT DATE_FORMAT(date, '%Y-%m-%d'); -- MySQL
SELECT FORMAT(date, 'yyyy-MM-dd'); -- SQL Server

-- 日期运算
SELECT date + INTERVAL '1 day';
SELECT date + INTERVAL '2 month';
SELECT date + INTERVAL '3 year';
SELECT DATEADD(day, 1, date); -- SQL Server

-- 日期差值
SELECT DATEDIFF('day', date1, date2); -- SQL Server
SELECT date2 - date1; -- PostgreSQL (返回天数)
SELECT TO_DAYS(date2) - TO_DAYS(date1); -- MySQL

-- 截断到特定精度
SELECT DATE_TRUNC('month', timestamp); -- PostgreSQL
SELECT TRUNC(date, 'MM'); -- Oracle
SELECT DATE(timestamp); -- 截断到日

-- 上个月最后一天
SELECT LAST_DAY(date);
```

### 转换函数
```sql
-- 显式类型转换
SELECT CAST(expression AS datatype);
SELECT CONVERT(datatype, expression); -- SQL Server
SELECT TO_CHAR(number_or_date);
SELECT TO_NUMBER(string);
SELECT TO_DATE(string, format);
```

### 条件函数
```sql
-- CASE表达式
SELECT CASE 
           WHEN condition1 THEN result1
           WHEN condition2 THEN result2
           ...
           ELSE default_result
       END;

-- COALESCE函数（返回第一个非NULL值）
SELECT COALESCE(value1, value2, ..., default_value);

-- NULLIF函数（如果value1=value2返回NULL，否则返回value1）
SELECT NULLIF(value1, value2);

-- NVL/IFNULL/ISNULL函数（NULL值替换）
SELECT NVL(value, replacement); -- Oracle
SELECT IFNULL(value, replacement); -- MySQL
SELECT ISNULL(value, replacement); -- SQL Server
```

### JSON函数（现代数据库支持）
```sql
-- 提取JSON值
SELECT JSON_EXTRACT(json_column, '$.property'); -- MySQL
SELECT json_column->'property'; -- PostgreSQL
SELECT JSON_VALUE(json_column, '$.property'); -- SQL Server, Oracle

-- 检查路径是否存在
SELECT JSON_EXISTS(json_column, '$.property'); -- Oracle
SELECT json_column ? 'property'; -- PostgreSQL

-- 修改JSON
SELECT JSON_SET(json_column, '$.property', new_value); -- MySQL
SELECT JSON_MODIFY(json_column, '$.property', new_value); -- SQL Server
```

### 分析函数
```sql
-- ROW_NUMBER：在分组内分配唯一的行号
SELECT ROW_NUMBER() OVER(PARTITION BY column1 ORDER BY column2) AS row_num;

-- RANK：在分组内分配排名，相等值获得相同排名，排名不连续
SELECT RANK() OVER(PARTITION BY column1 ORDER BY column2) AS rank_num;

-- DENSE_RANK：在分组内分配排名，相等值获得相同排名，排名连续
SELECT DENSE_RANK() OVER(PARTITION BY column1 ORDER BY column2) AS dense_rank_num;

-- FIRST_VALUE：返回窗口框架第一行的值
SELECT FIRST_VALUE(column1) OVER(PARTITION BY column2 ORDER BY column3) AS first_val;

-- LAST_VALUE：返回窗口框架最后一行的值
SELECT LAST_VALUE(column1) OVER(
    PARTITION BY column2 
    ORDER BY column3
    ROWS BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING
) AS last_val;
```