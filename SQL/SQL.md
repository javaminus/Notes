# SQL语句的执行顺序

SQL语句的执行顺序与书写顺序存在显著差异，其标准执行流程如下（按执行步骤编号）：

1. **FROM & JOIN**  
   首先确定数据源表并处理表连接（包括笛卡尔积和ON条件过滤），生成初始虚拟表。

2. **WHERE**  
   对连接后的数据进行行级过滤（不可使用聚合函数或SELECT别名）。

3. **GROUP BY**  
   按指定列分组，为聚合计算做准备。

4. **HAVING**  
   对分组结果进行筛选（可使用聚合函数）。

5. **SELECT**  
   选择输出列并计算表达式（包括聚合函数和窗口函数）。

6. **DISTINCT**  
   去除重复行。

7. **ORDER BY**  
   对结果排序（可使用SELECT定义的别名）。

8. **LIMIT/OFFSET**  
   最终限制返回行数或分页。

关键注意事项：
- WHERE与HAVING的区别：WHERE在分组前过滤单条记录，HAVING在分组后过滤整个分组。
- 别名使用规则：ORDER BY可使用SELECT别名，但WHERE不能。
- 多表连接时，JOIN操作按从左到右顺序执行。

完整执行顺序图示：  
`FROM → JOIN/ON → WHERE → GROUP BY → HAVING → SELECT → DISTINCT → ORDER BY → LIMIT`

---

# SQL通配符

SQL中的通配符是用于模糊匹配的特殊字符，需与`LIKE`操作符配合使用，主要包含以下类型及用法：

1. **百分号（%）**  
   匹配任意长度（包括零个）的字符序列。例如：  
   ```sql
   SELECT * FROM table WHERE column LIKE 'abc%'  -- 匹配以"abc"开头的值
   ```

2. **下划线（_）**  
   精确匹配单个任意字符。例如：  
   ```sql
   SELECT * FROM table WHERE column LIKE 'a_c'  -- 匹配如"a1c"、"axc"的值
   ```

3. **方括号（[]）**  
   匹配指定字符集合中的单个字符。例如：  
   ```sql
   SELECT * FROM table WHERE column LIKE '[ABC]%'  -- 匹配以A、B或C开头的值
   ```

4. **排除型方括号（[或[!]）**  
   匹配不在指定集合中的单个字符。例如：  
   ```sql
   SELECT * FROM table WHERE column LIKE '[-9]%'  -- 匹配不以数字开头的值
   ```

5. **字符范围（[-]）**  
   在方括号内定义连续字符范围。例如：  
   ```sql
   SELECT * FROM table WHERE column LIKE '[A-Z]%'  -- 匹配任意大写字母开头的值
   ```

**注意事项**：  
- 通配符搜索通常比精确匹配更耗性能，大数据表慎用；  
- 部分数据库（如MySQL）默认不区分大小写，但可通过`BINARY`关键字强制区分；  
- 方括号语法在某些数据库中可能不支持（如MySQL需改用`REGEXP`）。  

示例综合应用：  
```sql
-- 查找第二个字符为数字且包含"code"的值
SELECT * FROM table WHERE column LIKE '_[0-9]%code%'
```

---

---

#  正则表达式

一般来说，如果你被要求匹配一个字符串，应该最先想到写一个正则表达式模式进行匹配。

正则表达式提供各种功能，以下是一些相关功能：

^：表示一个字符串或行的开头

[a-z]：表示一个字符范围，匹配从 a 到 z 的任何字符。

[0-9]：表示一个字符范围，匹配从 0 到 9 的任何字符。

[a-zA-Z]：这个变量匹配从 a 到 z 或 A 到 Z 的任何字符。请注意，你可以在方括号内指定的字符范围的数量没有限制，您可以添加想要匹配的其他字符或范围。

[^a-z]：这个变量匹配不在 a 到 z 范围内的任何字符。请注意，字符 ^ 用来否定字符范围，它在方括号内的含义与它的方括号外表示开始的含义不同。

[a-z]*：表示一个字符范围，匹配从 a 到 z 的任何字符 0 次或多次。

[a-z]+：表示一个字符范围，匹配从 a 到 z 的任何字符 1 次或多次。

`.`：匹配任意一个字符。

`\.`：表示句点字符。请注意，反斜杠用于转义句点字符，因为句点字符在正则表达式中具有特殊含义。还要注意，在许多语言中，你需要转义反斜杠本身，因此需要使用\\.。

`$`：表示一个字符串或行的结尾。

# 试炼

```sql
# 组合两张表
# 考察left join on 的组合
select p.firstName, p.lastName, a.city, a.state
from Person p left join Address a on p.PersonId = a.personid
```



```sql
# 超过经理收入的员工
# 使用from employee a, employee b会做内连接；其实后面是否接where语句都是不会报错的，会生成笛卡尔积，比如表A是2*3，表B是5*6，会生成10*9的笛卡尔积；使用where语句只是为了过滤罢了；

# 直接报错 Table 'test.b' doesnot exist；
select a.name
from employee a, employee b
where a.salary > (select salary from b where a.managerId = b.id)

# 正确的写法（推荐）
select a.name employee
from employee a, employee b
where a.managerId = b.id and a.salary > b.salary

# 正确的写法（嵌套查询，不推荐，但是比较万能）
SELECT a.name employee
FROM employee a
WHERE a.salary > (SELECT salary FROM employee WHERE id = a.managerId)
```



```sql
# 查找重复的电子邮箱
-- select email
-- from Person
-- group by count(email) > 1
select email
from Person
group by email having count(email) > 1
```



## 补充`in`和`exists`的的区别

> 这里补充`in`和`exists`的的区别：
>
> `in`的语法：where id in ( )；先执行子查询，获取结果集后进行比较；子查询结果集大时效率较低 ；比较所有值 
>
> `exists`的语法：where exists ( )；执行子查询直到找到一行匹配立即返回 ；通常更高效，找到一行即可停止 ；只关心是否存在匹配 

```sql
# 从不订购的客户
# 方法一：使用 in
select c.name customers
from customers c
where c.id not in(select customerId from orders)

# 方法二：使用 exists (推荐，性能最好)
# 错误写法
select c.name customers
from customers c
where c.id not exists(select customerId from orders)
# 正确的写法
select c.name customers
from customers c
where not exists(select 1 from orders where c.id = orders.customerId)

# 方法三：使用left join on
# 不能使用c.id = null，这样会报错，固定搭配是is null 或者 is not null;
# 错误写法，因为是左连接
select c.name Customers
from customers c left join orders o on c.id = o.CustomerId
where c.id is null;
# 正确写法
select c.name Customers
from customers c left join orders o on c.id = o.CustomerId
where o.CustomerId is null;
```



```sql
# 删除重复的电子邮箱 
# 题意：删除 所有重复的电子邮件，只保留一个具有最小 id 的唯一电子邮件。
delete p2
from person p1, person p2
where p1.email = p2.email and p1.id < p2.id
```



## 日期比较用datediff(a, b) = 1；前 - 后

> 日期比较用datediff(a, b) = 1；前 - 后

```sql
# 上升的温度：找出与之前（昨天的）日期相比温度更高的所有日期的 id 。
select w1.id
from Weather w1, Weather w2
where w1.temperature > w2.temperature and datediff(w1.recordDate, w2.recordDate) = 1
```



```sql
# 游戏玩法分析 I: 查询每位玩家 第一次登录平台的日期。
select a.player_id, min(a.event_date) first_login
from activity a
group by a.player_id
```



```sql
# 员工奖金：报告每个奖金 少于 1000 的员工的姓名和奖金数额。
select e.name, b.bonus
from Employee e left join Bonus b 
on e.empId = b.empId 
where b.bonus < 1000 or b.bonus is null
```



```sql
# 销售员：找出没有任何与名为 “RED” 的公司相关的订单的所有销售人员的姓名。
# 方案一：
select name 
from SalesPerson
where sales_id not in(
select sales_id
from company c, orders o
where c.com_id = o.com_id and c.name = 'RED'
)
```



## 学习如何增加一列

> 学习如何增加一列：`case when 条件 then 'Yes' else 'No' end as XX  `
>
> `END AS`是CASE表达式的固定结束标记组合 

```sql
# 判断三角形
select x, y, z, case when x+y>z and x+z>y and y+z>x then 'Yes' else 'No' end as triangle
from Triangle
```



```sql
# 只出现一次的最大数字
# 错误写法
select max(num)
from MyNumbers 
group by num having count(num) = 1

# 正确写法
select max(num) num
from MyNumbers 
where num in(
select num from MyNumbers group by num having count(num) = 1
)
```



## `(CASE 字段 WHEN 值1 THEN 结果1 ELSE 默认结果 END) as 字段 `

> `(CASE 字段 WHEN 值1 THEN 结果1 ELSE 默认结果 END) as 字段 ` 

```sql
# 换座位：交换每两个连续的学生的座位号。如果学生的数量是奇数，则最后一个学生的id不交换。按 id 升序 返回结果表。
select (
    case 
    when id%2 = 1 and cnt != id then id + 1
    when id%2 = 1 and cnt = id then id
    else id - 1
    end
) as id, student
from seat, (select count(*) as cnt from seat) as a
order by id
```



```sql
# 变更性别
# update 表名
update Salary set sex = (case when sex = 'f' then 'm' else 'f' end) 
```



```sql
# 买下所有产品的客户：报告 Customer 表中购买了 Product 表中所有产品的客户的 id
select customer_id
from Customer
group by customer_id having count(distinct product_key) = (select count(*) from product)
```



## 这里的`goup by`居然可以对两个字段使用

> 这里的`goup by`居然可以对两个字段使用

```sql
# 合作过至少三次的演员和导演
select actor_id, director_id
from ActorDirector
group by actor_id, director_id having count(*) >= 3
```



## （字段1， 字段2） in  (select 字段1， 字段2)

> （字段1， 字段2） in  (select 字段1， 字段2)

```sql
# 产品销售分析 III：选出每个售出过的产品 第一年 销售的产品 id、年份、数量 和 价格。
select product_id, year as first_year, quantity, price
from Sales
where (product_id, year) in (select product_id, min(year) from Sales group by product_id)
```



```sql
# 销售分析 III:即仅在 2019-01-01 （含）至 2019-03-31 （含）之间出售的商品。
# Write your MySQL query statement below
select 
p.product_id,p.product_name 
from
Product p left join Sales s on s.product_id = p.product_id
group by
p.product_id
having
max(sale_date)<='2019-03-31' and min(sale_date)>='2019-01-01'
```



```sql
# 查询近30天活跃用户数
SELECT activity_date AS day, count(DISTINCT user_id) AS active_users
FROM Activity
WHERE DATEDIFF("2019-07-27",activity_date) BETWEEN 0 AND 29
GROUP BY activity_date
```



```sql
# 使得 每个月 都有一个部门 id 列和一个收入列
SELECT id, 
SUM(CASE WHEN month='Jan' THEN revenue END) AS Jan_Revenue,
SUM(CASE WHEN month='Feb' THEN revenue END) AS Feb_Revenue,
SUM(CASE WHEN month='Mar' THEN revenue END) AS Mar_Revenue,
SUM(CASE WHEN month='Apr' THEN revenue END) AS Apr_Revenue,
SUM(CASE WHEN month='May' THEN revenue END) AS May_Revenue,
SUM(CASE WHEN month='Jun' THEN revenue END) AS Jun_Revenue,
SUM(CASE WHEN month='Jul' THEN revenue END) AS Jul_Revenue,
SUM(CASE WHEN month='Aug' THEN revenue END) AS Aug_Revenue,
SUM(CASE WHEN month='Sep' THEN revenue END) AS Sep_Revenue,
SUM(CASE WHEN month='Oct' THEN revenue END) AS Oct_Revenue,
SUM(CASE WHEN month='Nov' THEN revenue END) AS Nov_Revenue,
SUM(CASE WHEN month='Dec' THEN revenue END) AS Dec_Revenue
FROM department
GROUP BY id
ORDER BY id;
```



```sql
# 这个难，按照query_name分类，统计平均值，不合格率
select query_name, round(avg(rating/position), 2) quality,
round(sum(if(rating < 3, 1, 0)) * 100 / count(*), 2) poor_query_percentage
from queries
where query_name is not null
group by query_name
```



## COALESCE 接受多个参数，返回第一个非 NULL 的值

> - COALESCE 接受多个参数，返回第一个非 NULL 的值
> - 如果所有参数都为 NULL，则返回 NULL

```sql
# 查找每种产品的平均售价
SELECT 
    p.product_id,
    COALESCE(ROUND(SUM(p.price * u.units) / SUM(u.units), 2), 0) AS average_price
FROM 
    Prices p
LEFT JOIN 
    UnitsSold u ON p.product_id = u.product_id
    AND u.purchase_date BETWEEN p.start_date AND p.end_date
GROUP BY 
    p.product_id
```



```sql
# 学生们参加各科测试的次数：查询出每个学生参加每一门科目测试的次数，结果按 student_id 和 subject_name 排序。
SELECT 
    s.student_id, s.student_name, sub.subject_name, coalesce(grouped.attended_exams, 0) AS attended_exams
FROM 
    Students s
CROSS JOIN 
    Subjects sub
LEFT JOIN (
    SELECT student_id, subject_name, COUNT(*) AS attended_exams
    FROM Examinations
    GROUP BY student_id, subject_name // 这里真的很厉害
) grouped 
ON s.student_id = grouped.student_id AND sub.subject_name = grouped.subject_name
ORDER BY s.student_id, sub.subject_name;
```



```sql
# 列出指定时间段内所有的下单产品：要求获取在 2020 年 2 月份下单的数量不少于 100 的产品的名字和数目。
select p.product_name, sum(o.unit) unit
from Products p left join Orders o on p.product_id = o.product_id
where o.order_date between '2020-02-01' and '2020-02-29'
group by product_name having unit>=100
```



```sql
# b表按照user_id合并总步数，然后连表查询
select name, coalEsce(b.distance, 0) travelled_distance
from Users a left join(
    select user_id, sum(distance) distance from rides group by user_id
) as b on a.id = b.user_id
order by travelled_distance desc, name
```



## `group_concat([distinct] 要连接的字段 [order by 排序字段][separator '分隔符']) `

> `group_concat([distinct] 要连接的字段 [order by 排序字段][separator '分隔符']) `

```sql
# 按日期分组销售产品：编写解决方案找出每个日期、销售的不同产品的数量及其名称。每个日期的销售产品名称应按词典序排列。
select sell_date, count(distinct(product)) as num_sold, 
group_concat(distinct product order by product separator ',') as products
from Activities
group by sell_date
```



## REGEXP_LIKE(字符串，正则，'c')表示大小写敏感 

> REGEXP_LIKE(字符串，正则，'c')表示大小写敏感 

```sql
# 查找拥有有效邮箱的用户：前缀 名称是一个字符串，可以包含字母（大写或小写），数字，下划线 '_' ，点 '.' 和（或）破折号 '-' 。前缀名称 必须 以字母开头。域 为 '@leetcode.com' 。
select * 
from users
where regexp_like(mail, '^[a-zA-Z][a-zA-Z0-9_.-]*@leetcode\\.com$','c') // 如果末尾不加结束符$，那么@leetcode.comAVG也算对的
```



```sql
# 1527. 患某种疾病的患者 查询患有 I 类糖尿病的患者 ID （patient_id）、患者姓名（patient_name）以及其患有的所有疾病代码（conditions）。I 类糖尿病的代码总是包含前缀 DIAB1 。
# Write your MySQL query statement below
select patient_id, patient_name, conditions
from patients
where conditions like '% DIAB1%'
# 注意上面的DIAB1前有一个空格，防止匹配到类似'ABCDIAB1'这种字符串
or conditions like 'DIAB1%'
# 为了匹配conditions字段以DIAB1开头的记录，因此没有空格
```



```sql
# 进店却未进行过交易的顾客
select customer_id, count(*) as count_no_trans
from Visits a left join Transactions b on a.visit_id = b.visit_id
where transaction_id is null
group by customer_id
```



```sql
# 银行账户概要 II
select a.name, sum(amount) as BALANCE
from Users a left join Transactions b on a.account = b.account
group by a.account having sum(amount) > 10000
```



```sql
# 各赛事的用户注册率
select contest_id, round(count(contest_id) * 100 / (select count(*) from users), 2) as percentage
from register b left join users a  on a.user_id = b.user_id
group by contest_id
order by percentage desc, contest_id asc
```



```sql
# 每台机器的进程平均运行时间
select a.machine_id, round(avg(b.timestamp - a.timestamp), 3) as processing_time
from activity a left join activity b on a.machine_id = b.machine_id
where a.process_id = b.process_id and a.activity_type = 'start' and b.activity_type = 'end'
group by a.machine_id
```



## sql函数

SUBSTRING(column_name, start, length)：这将从列的值中提取一个子字符串，从指定的起始位置开始，直到指定的长度。

UPPER(expression)：这会将字符串表达式转换为大写。

LOWER(expression)：这会将字符串表达式转换为小写。

CONCAT(string1, string2, ...)：这会将两个或多个字符串连接成一个字符串。

```sql
# 修复表中的名字：编写解决方案，修复名字，使得只有第一个字符是大写的，其余都是小写的。
SELECT user_id, CONCAT(UPPER(SUBSTRING(name, 1, 1)), LOWER(SUBSTRING(name, 2))) AS name
FROM Users
ORDER BY user_id;
```



## CHAR_LENGTH(content) > 15

```sql
SELECT 
    tweet_id
FROM 
    tweets
WHERE 
    CHAR_LENGTH(content) > 15
```



## count(1)和count(*)一样

```sql
SELECT
    date_id,
    make_name,
    COUNT(DISTINCT lead_id) AS unique_leads,
    COUNT(DISTINCT partner_id) AS unique_partners
FROM
    DailySales
GROUP BY date_id, make_name;
```

