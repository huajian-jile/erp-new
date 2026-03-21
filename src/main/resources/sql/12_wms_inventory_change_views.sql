-- 待分配库存（按店铺+SKU）
-- 口径：
-- - 近7天销量：wms_sales_order_item 汇总（按 paid_at，状态=PAID/SHIPPED/DELIVERED）
-- - 日均销量 = qty_7d / 7
-- - 目标备货 = 日均销量 * 7（也就是 qty_7d）
-- - 当 目标备货 > 店铺当前库存(wms_inventory.qty) 时，进入“待分配”

CREATE OR REPLACE VIEW v_wms_store_sku_sales_7d AS
SELECT
  so.platform_id,
  so.store_id,
  soi.sku_id,
  SUM(soi.qty) AS qty_7d
FROM wms_sales_order_item soi
JOIN wms_sales_order so ON so.id = soi.sales_order_id
WHERE so.paid_at >= (NOW() - INTERVAL 7 DAY)
  AND so.status IN ('PAID','SHIPPED','DELIVERED')
GROUP BY so.platform_id, so.store_id, soi.sku_id;

CREATE OR REPLACE VIEW v_wms_inventory_pending_allocation AS
SELECT
  s7.platform_id,
  s7.store_id,
  st.code AS store_code,
  s7.sku_id,
  sku.sku,
  sku.title,
  s7.qty_7d,
  ROUND(s7.qty_7d / 7.0, 2) AS avg_daily_qty_7d,
  COALESCE(inv.qty, 0) AS store_stock_qty,
  GREATEST(s7.qty_7d - COALESCE(inv.qty, 0), 0) AS need_alloc_qty
FROM v_wms_store_sku_sales_7d s7
JOIN wms_store st ON st.id = s7.store_id
JOIN wms_sku sku ON sku.id = s7.sku_id
LEFT JOIN wms_inventory inv ON inv.store_id = s7.store_id AND inv.sku_id = s7.sku_id
WHERE (s7.qty_7d > COALESCE(inv.qty, 0))
ORDER BY need_alloc_qty DESC, s7.qty_7d DESC;

