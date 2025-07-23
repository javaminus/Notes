# 库存扣减、创建订单如何拆成TCC？

TCC（Try-Confirm-Cancel）是分布式事务的经典解决方案，将每个业务操作分为三个阶段，每个阶段都是独立事务。拿**库存扣减**和**订单创建**为例，具体拆解如下：

---

## 一、库存扣减的TCC拆解

### 1. Try阶段（资源预留）

- **目的**：校验库存并“冻结”要扣减的库存，防止超卖。
- **典型SQL**:
  ```sql
  UPDATE inventory_table
  SET frozen_inventory = frozen_inventory + #{count}
  WHERE id = #{skuId} AND (saleable_inventory - frozen_inventory) >= #{count};
  ```
- **说明**：只修改冻结库存，不实际扣减，外部用户不可见。

---

### 2. Confirm阶段（正式扣减）

- **目的**：所有参与者Try成功后，正式扣减库存。
- **典型SQL**:
  ```sql
  UPDATE inventory_table
  SET
    frozen_inventory = frozen_inventory - #{count},
    saleable_inventory = saleable_inventory - #{count}
  WHERE id = #{skuId};
  ```
- **说明**：冻结库存减少，可售库存减少，完成扣减。

---

### 3. Cancel阶段（释放库存）

- **目的**：任一参与者Try失败或需要回滚时，释放预留资源。
- **典型SQL**:
  ```sql
  UPDATE inventory_table
  SET frozen_inventory = frozen_inventory - #{count}
  WHERE id = #{skuId};
  ```
- **说明**：只减少冻结库存，不影响可售库存。

---

## 二、订单创建的TCC拆解

### 1. Try阶段（订单预创建）

- **目的**：创建初始订单，用户不可见，不可支付。
- **典型SQL**:
  ```sql
  INSERT INTO order_table(id, create_time, order_id, state)
  VALUES (#{id}, NOW(), #{orderId}, 'INIT');
  ```
- **说明**：订单状态为 INIT，业务侧不可见。

---

### 2. Confirm阶段（订单生效）

- **目的**：所有Try成功后，订单正式生效，用户可见、可支付。
- **典型SQL**:
  ```sql
  UPDATE order_table
  SET state = 'TO_PAY'
  WHERE id = #{id};
  ```
- **说明**：订单状态转为“待支付”。

---

### 3. Cancel阶段（订单废弃）

- **目的**：任何环节失败，废弃订单。
- **典型SQL**:
  ```sql
  UPDATE order_table
  SET state = 'DISCARD'
  WHERE id = #{id};
  ```
- **说明**：订单作废，用户不可见，可定期清理。

---

## 总结

- **TCC三步都是独立事务**，分别处理资源预留、正式提交和补偿回滚。
- 资源预留：冻结库存、创建INIT订单；
- 提交确认：正式扣减、订单可见；
- 异常回滚：释放冻结库存、订单作废。

> 💡 **一句话理解：TCC把核心业务（如扣库存、建订单）都变成Try/Confirm/Cancel三个阶段，每一步都是单独的事务，确保分布式场景下要么全部成功，要么全部回滚。**