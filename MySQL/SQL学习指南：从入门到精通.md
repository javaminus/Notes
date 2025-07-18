# SQL 学习指南：从入门到精通

## 目录
1.  [简介](#1-简介)
2.  [准备工作：示例数据](#2-准备工作示例数据)
3.  [基础查询](#3-基础查询)
    *   [SELECT：选择数据](#select选择数据)
    *   [WHERE：过滤数据](#where过滤数据)
    *   [ORDER BY：排序数据](#orderby排序数据)
4.  [聚合与分组](#4-聚合与分组)
    *   [聚合函数](#聚合函数)
    *   [GROUP BY：分组数据](#groupby分组数据)
    *   [HAVING：过滤分组](#having过滤分组)
5.  [连接查询 (JOINs)](#5-连接查询-joins)
    *   [INNER JOIN：内连接](#inner-join内连接)
    *   [LEFT JOIN：左连接](#left-join左连接)
6.  [子查询与公用表表达式 (CTE)](#6-子查询与公用表表达式-cte)
    *   [子查询](#子查询)
    *   [公用表表达式 (CTE)](#公用表表达式-cte)
7.  [窗口函数 (Window Functions)](#7-窗口函数-window-functions)
    *   [什么是窗口函数？](#什么是窗口函数)
    *   [窗口函数的基本语法](#窗口函数的基本语法)
    *   [常用窗口函数示例](#常用窗口函数示例)
        *   [排名函数: `ROW_NUMBER`, `RANK`, `DENSE_RANK`](#排名函数-row_number-rank-dense_rank)
        *   [偏移函数: `LAG`, `LEAD`](#偏移函数-lag-lead)
        *   [聚合窗口函数: `SUM`, `AVG`, `COUNT`](#聚合窗口函数-sum-avg-count)
8.  [总结](#8-总结)

---

### 1. 简介

SQL (Structured Query Language) 是用于管理和操作关系型数据库的标准化语言。通过SQL，你可以查询数据、插入、更新和删除记录，以及创建和修改数据库结构。本指南将带你从基础开始，逐步掌握SQL的核心概念，特别是强大的窗口函数。

### 2. 准备工作：示例数据

为了更好地学习，我们创建两个表：`employees` (员工表) 和 `departments` (部门表)。

**`departments` 表**
| id   | name       |
| ---- | ---------- |
| 1    | 技术部     |
| 2    | 市场部     |
| 3    | 人力资源部 |

**`employees` 表**
| id   | name | salary | department_id |
| ---- | ---- | ------ | ------------- |
| 1    | 张三 | 8000   | 1             |
| 2    | 李四 | 9000   | 1             |
| 3    | 王五 | 12000  | 1             |
| 4    | 赵六 | 6000   | 2             |
| 5    | 孙七 | 7500   | 2             |
| 6    | 周八 | 6500   | 3             |

你可以使用以下SQL语句创建并填充这些表：
```sql
CREATE TABLE departments (
    id INT PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE employees (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    salary DECIMAL(10, 2),
    department_id INT
);

INSERT INTO departments (id, name) VALUES
(1, '技术部'),
(2, '市场部'),
(3, '人力资源部');

INSERT INTO employees (id, name, salary, department_id) VALUES
(1, '张三', 8000, 1),
(2, '李四', 9000, 1),
(3, '王五', 12000, 1),
(4, '赵六', 6000, 2),
(5, '孙七', 7500, 2),
(6, '周八', 6500, 3);
```

### 3. 基础查询

#### SELECT：选择数据
`SELECT` 用于从数据库中选取数据。

* **选取所有列**
    ```sql
    SELECT * FROM employees;
    ```
* **选取指定列**
    ```sql
    SELECT name, salary FROM employees;
    ```

#### WHERE：过滤数据
`WHERE` 用于根据指定条件过滤记录。

* **查找薪水高于8500的员工**
    ```sql
    SELECT name, salary FROM employees WHERE salary > 8500;
    ```
* **查找技术部的所有员工**
    ```sql
    SELECT name FROM employees WHERE department_id = 1;
    ```

#### ORDER BY：排序数据
`ORDER BY` 用于对结果集进行排序，默认为升序 (`ASC`)。

* **按薪水降序排列所有员工**
    ```sql
    SELECT name, salary FROM employees ORDER BY salary DESC;
    ```

### 4. 聚合与分组

#### 聚合函数
聚合函数对一组值进行计算，并返回单个值。
* `COUNT()`: 计数
* `SUM()`: 求和
* `AVG()`: 平均值
* `MAX()`: 最大值
* `MIN()`: 最小值

* **计算员工总数和平均薪水**
    ```sql
    SELECT COUNT(*), AVG(salary) FROM employees;
    ```

#### GROUP BY：分组数据
`GROUP BY` 语句通常与聚合函数一同使用，根据一个或多个列对结果集进行分组。

* **计算每个部门的员工人数和平均薪水**
    ```sql
    SELECT
        department_id,
        COUNT(*) AS employee_count,
        AVG(salary) AS avg_salary
    FROM employees
    GROUP BY department_id;
    ```

#### HAVING：过滤分组
`WHERE` 过滤行，`HAVING` 过滤分组。`HAVING` 在 `GROUP BY` 之后应用。

* **找出平均薪水高于7000的部门**
    ```sql
    SELECT
        department_id,
        AVG(salary) AS avg_salary
    FROM employees
    GROUP BY department_id
    HAVING AVG(salary) > 7000;
    ```

### 5. 连接查询 (JOINs)
JOIN 用于根据两个或多个表中的相关列组合行。

#### INNER JOIN：内连接
获取两个表中字段匹配的记录。

* **查询每个员工及其所在的部门名称**
    ```sql
    SELECT
        e.name AS employee_name,          -- 员工姓名
        d.name AS department_name         -- 部门名称
    FROM employees AS e                   -- 从员工表（employees）取别名为 e
    INNER JOIN departments AS d           -- 内连接部门表（departments），取别名为 d
        ON e.department_id = d.id;        -- 连接条件：员工的 department_id 等于部门的 id
    ```

#### LEFT JOIN：左连接
返回左表的所有记录，以及右表中匹配的记录。如果右表没有匹配项，则结果为 NULL。

* **列出所有部门以及其中的员工（即使部门没有员工）**
    ```sql
    SELECT
        d.name AS department_name,
        e.name AS employee_name
    FROM departments AS d
    LEFT JOIN employees AS e ON d.id = e.department_id;
    ```

### 6. 子查询与公用表表达式 (CTE)

#### 子查询
子查询是嵌套在另一个查询中的查询。

* **找出薪水高于平均薪水的员工**
    ```sql
    SELECT name, salary
    FROM employees
    WHERE salary > (SELECT AVG(salary) FROM employees);
    ```

#### 公用表表达式 (CTE)
CTE 提供了一种定义临时命名结果集的方法，可以使复杂查询更具可读性。

* **使用 CTE 实现与上面相同的功能**
    ```sql
    WITH AvgSalary AS (
        SELECT AVG(salary) AS avg_sal FROM employees
    )
    SELECT e.name, e.salary
    FROM employees AS e, AvgSalary
    WHERE e.salary > AvgSalary.avg_sal;
    ```

### 7. 窗口函数 (Window Functions)

#### 什么是窗口函数？
窗口函数对一组与当前行相关的表行（这个组被称为“窗口”）执行计算。与聚合函数不同，窗口函数**不会将多行压缩为一行**，而是为结果集中的每一行返回一个值。

#### 窗口函数的基本语法
```sql
FUNCTION_NAME() OVER (
    [PARTITION BY partition_expression, ... ]
    [ORDER BY sort_expression [ASC|DESC], ... ]
)
```
*   `FUNCTION_NAME()`: 窗口函数的名称，如 `ROW_NUMBER()`, `SUM()`。
*   `OVER()`: 定义窗口的子句。
*   `PARTITION BY`: 将行分成多个分区（组），窗口函数在每个分区内独立应用。这类似于 `GROUP BY`，但不会折叠行。
*   `ORDER BY`: 定义分区内行的顺序。

#### 常用窗口函数示例

##### 排名函数: `ROW_NUMBER`, `RANK`, `DENSE_RANK`
这些函数用于为分区中的每一行分配一个排名。

* `ROW_NUMBER()`: 为分区中的每一行分配一个从1开始的唯一连续整数。
* `RANK()`: 为分区中的每一行分配一个排名。如果存在相同的值，则排名相同，但后续排名会跳过相应数量的数字。
* `DENSE_RANK()`: 与 `RANK()` 类似，但即使存在相同的值，后续排名也是连续的。

* **用例：按部门对员工薪水进行排名**
    ```sql
    SELECT
        name,
        salary,
        department_id,
        ROW_NUMBER() OVER(PARTITION BY department_id ORDER BY salary DESC) AS row_num,
        RANK()       OVER(PARTITION BY department_id ORDER BY salary DESC) AS rnk,
        DENSE_RANK() OVER(PARTITION BY department_id ORDER BY salary DESC) AS dense_rnk
    FROM employees;
    ```
    **结果分析：**
    *   `PARTITION BY department_id` 将员工按部门分开。
    *   `ORDER BY salary DESC` 在每个部门内按薪水从高到低排序。
    *   你可以看到技术部 (id=1) 的员工被一起排名，市场部 (id=2) 的员工被一起排名。

##### 偏移函数: `LAG`, `LEAD`
这两个函数可以从当前行访问同一结果集中的前一行 (`LAG`) 或后一行 (`LEAD`) 的数据。

* `LAG(column, offset, default_value)`: 获取当前行之前第 `offset` 行的 `column` 值。
* `LEAD(column, offset, default_value)`: 获取当前行之后第 `offset` 行的 `column` 值。

* **用例：查询每个员工以及其同部门内薪水紧随其后的员工薪水**
    ```sql
    SELECT
        name,
        salary,
        department_id,
        LEAD(salary, 1, 0) OVER (PARTITION BY department_id ORDER BY salary DESC) AS next_highest_salary
    FROM employees;
    ```
    **结果分析：**
    *   对于技术部的王五（薪水12000），`next_highest_salary` 是李四的薪水9000。
    *   对于技术部的张三（薪水8000），他是部门里薪水最低的，所以 `next_highest_salary` 是我们提供的默认值0。

##### 聚合窗口函数: `SUM`, `AVG`, `COUNT`
聚合函数也可以用作窗口函数。

* **用例：计算每个部门的累计薪水**
    ```sql
    SELECT
        name,
        salary,
        department_id,
        SUM(salary) OVER (PARTITION BY department_id ORDER BY salary) AS cumulative_salary
    FROM employees;
    ```
    **结果分析：**
    *   `ORDER BY salary` 定义了累加的顺序。
    *   对于技术部，第一行（张三，8000）的累计薪水是8000；第二行（李四，9000）的累计薪水是 8000 + 9000 = 17000；第三行（王五，12000）的累计薪水是 17000 + 12000 = 29000。

* **用例：计算每个员工的薪水与该员工所在部门平均薪水的差额**
    ```sql
    SELECT
        name,
        salary,
        department_id,
        AVG(salary) OVER (PARTITION BY department_id) AS department_avg_salary,
        salary - AVG(salary) OVER (PARTITION BY department_id) AS salary_difference
    FROM employees;
    ```
    **结果分析：**
    *   `PARTITION BY department_id` 计算每个部门的平均薪水。
    *   因为没有 `ORDER BY`，所以窗口是整个分区。技术部所有员工的 `department_avg_salary` 都是相同的。
    *   然后我们可以直接计算每个员工薪水与部门平均薪水的差值。

### 8. 总结
SQL 是一项强大的数据处理技能。从基础的 `SELECT` 和 `WHERE` 到复杂的 `JOIN` 和窗口函数，每一个概念都有其特定的应用场景。窗口函数尤其强大，它允许你在保持原始行数不变的情况下执行复杂的分析，如排名、趋势分析和累计计算。

持续练习是掌握SQL的关键。尝试在你自己的数据集上应用这些概念，解决实际问题。祝你学习愉快！