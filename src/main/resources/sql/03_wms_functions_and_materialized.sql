-- MySQL 没有真正的“物化视图”，常见替代：
-- 1) 用一张汇总表当作“物化结果表”
-- 2) 写存储过程 refresh_* 负责重算/增量
-- 3) 结合 EVENT 定时刷新（或用任务调度器/作业平台）

DELIMITER $$

DROP FUNCTION IF EXISTS fn_wms_sku_value_cent $$
CREATE FUNCTION fn_wms_sku_value_cent(qty INT, price_cent BIGINT)
RETURNS BIGINT
DETERMINISTIC
BEGIN
  RETURN qty * price_cent;
END $$

DROP PROCEDURE IF EXISTS sp_refresh_wms_inventory_mv $$
CREATE PROCEDURE sp_refresh_wms_inventory_mv()
BEGIN
  CREATE TABLE IF NOT EXISTS mv_wms_inventory_value (
    store_id BIGINT NOT NULL,
    sku_id BIGINT NOT NULL,
    qty INT NOT NULL,
    price_cent BIGINT NOT NULL,
    stock_value_cent BIGINT NOT NULL,
    refreshed_at DATETIME NOT NULL,
    PRIMARY KEY (store_id, sku_id)
  );

  REPLACE INTO mv_wms_inventory_value(store_id, sku_id, qty, price_cent, stock_value_cent, refreshed_at)
  SELECT
    inv.store_id,
    inv.sku_id,
    inv.qty,
    sku.price_cent,
    fn_wms_sku_value_cent(inv.qty, sku.price_cent) AS stock_value_cent,
    NOW()
  FROM wms_inventory inv
  JOIN wms_sku sku ON sku.id = inv.sku_id;
END $$

DELIMITER ;

-- 可选：定时刷新（需要开启 event_scheduler）
-- SET GLOBAL event_scheduler = ON;
-- CREATE EVENT IF NOT EXISTS ev_refresh_inventory_mv
-- ON SCHEDULE EVERY 5 MINUTE
-- DO CALL sp_refresh_wms_inventory_mv();
