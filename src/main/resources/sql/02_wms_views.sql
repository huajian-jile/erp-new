CREATE OR REPLACE VIEW v_wms_inventory_snapshot AS
SELECT
  inv.store_id,
  s.code AS store_code,
  inv.sku_id,
  sku.sku,
  sku.title,
  inv.qty,
  sku.price_cent,
  (inv.qty * sku.price_cent) AS stock_value_cent,
  inv.updated_at
FROM wms_inventory inv
JOIN wms_store s ON s.id = inv.store_id
JOIN wms_sku sku ON sku.id = inv.sku_id;

CREATE OR REPLACE VIEW v_wms_stock_txn_daily AS
SELECT
  store_id,
  sku_id,
  DATE(created_at) AS day,
  SUM(delta) AS delta_sum,
  COUNT(*) AS txn_cnt
FROM wms_stock_txn
GROUP BY store_id, sku_id, DATE(created_at);
