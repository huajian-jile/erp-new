-- 仓库库存快照（仓库维度）
CREATE OR REPLACE VIEW v_wms_wh_inventory_snapshot AS
SELECT
  wh.id AS warehouse_id,
  wh.code AS warehouse_code,
  wh.name AS warehouse_name,
  inv.sku_id,
  sku.sku,
  sku.title,
  inv.available_qty,
  inv.in_transit_qty,
  inv.total_qty,
  sku.price_cent AS sale_price_cent,
  sku.cost_cent,
  (sku.price_cent - sku.cost_cent) AS profit_per_unit_cent,
  CASE
    WHEN sku.price_cent = 0 THEN 0
    ELSE ROUND((sku.price_cent - sku.cost_cent) / sku.price_cent, 4)
  END AS profit_rate,
  inv.refreshed_at AS data_refreshed_at,
  inv.updated_at
FROM wms_warehouse_inventory inv
JOIN wms_warehouse wh ON wh.id = inv.warehouse_id
JOIN wms_sku sku ON sku.id = inv.sku_id;

-- 近 X 天销量（按SKU）
CREATE OR REPLACE VIEW v_wms_sku_sales_30d AS
SELECT
  soi.sku_id,
  SUM(soi.qty) AS qty_30d
FROM wms_sales_order_item soi
JOIN wms_sales_order so ON so.id = soi.sales_order_id
WHERE so.paid_at >= (NOW() - INTERVAL 30 DAY)
  AND so.status IN ('PAID','SHIPPED','DELIVERED')
GROUP BY soi.sku_id;

-- 销量排行榜（按SKU，30天）
CREATE OR REPLACE VIEW v_wms_rank_sales_qty_30d AS
SELECT
  sku.id AS sku_id,
  sku.sku,
  sku.title,
  COALESCE(s.qty_30d, 0) AS qty_30d
FROM wms_sku sku
LEFT JOIN v_wms_sku_sales_30d s ON s.sku_id = sku.id
ORDER BY qty_30d DESC, sku.id ASC;

-- 利润排行榜（按SKU，30天）
CREATE OR REPLACE VIEW v_wms_rank_profit_30d AS
SELECT
  sku.id AS sku_id,
  sku.sku,
  sku.title,
  SUM(soi.qty * (soi.sale_price_cent - soi.cost_cent)) AS profit_cent_30d,
  SUM(soi.qty * soi.sale_price_cent) AS revenue_cent_30d,
  CASE
    WHEN SUM(soi.qty * soi.sale_price_cent) = 0 THEN 0
    ELSE ROUND(SUM(soi.qty * (soi.sale_price_cent - soi.cost_cent)) / SUM(soi.qty * soi.sale_price_cent), 4)
  END AS profit_rate_30d
FROM wms_sales_order_item soi
JOIN wms_sales_order so ON so.id = soi.sales_order_id
JOIN wms_sku sku ON sku.id = soi.sku_id
WHERE so.paid_at >= (NOW() - INTERVAL 30 DAY)
  AND so.status IN ('PAID','SHIPPED','DELIVERED')
GROUP BY sku.id, sku.sku, sku.title
ORDER BY profit_cent_30d DESC;

-- 销售漏斗（按天）：创建->支付->发货->签收
CREATE OR REPLACE VIEW v_wms_sales_funnel_daily AS
SELECT
  DATE(created_at) AS day,
  COUNT(*) AS created_cnt,
  SUM(CASE WHEN paid_at IS NOT NULL THEN 1 ELSE 0 END) AS paid_cnt,
  SUM(CASE WHEN shipped_at IS NOT NULL THEN 1 ELSE 0 END) AS shipped_cnt,
  SUM(CASE WHEN delivered_at IS NOT NULL THEN 1 ELSE 0 END) AS delivered_cnt
FROM wms_sales_order
GROUP BY DATE(created_at)
ORDER BY day DESC;

