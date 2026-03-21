-- SKU 维度：30天销量 + 当前库存（仓库总库存汇总）
CREATE OR REPLACE VIEW v_wms_sku_sales_30d_all AS
SELECT
  sku.id AS sku_id,
  sku.sku,
  sku.title,
  COALESCE(s30.qty_30d, 0) AS qty_30d,
  COALESCE(stock.total_qty, 0) AS stock_total_qty,
  COALESCE(stock.available_qty, 0) AS stock_available_qty,
  COALESCE(stock.in_transit_qty, 0) AS stock_in_transit_qty
FROM wms_sku sku
LEFT JOIN v_wms_sku_sales_30d s30 ON s30.sku_id = sku.id
LEFT JOIN (
  SELECT sku_id,
         SUM(total_qty) AS total_qty,
         SUM(available_qty) AS available_qty,
         SUM(in_transit_qty) AS in_transit_qty
  FROM wms_warehouse_inventory
  GROUP BY sku_id
) stock ON stock.sku_id = sku.id;

-- 滞销/动销率：动销率=销量/(销量+期末库存)，并给出可售天数估算
CREATE OR REPLACE VIEW v_wms_sku_slow_moving_30d AS
SELECT
  sku_id,
  sku,
  title,
  qty_30d,
  stock_total_qty,
  stock_available_qty,
  stock_in_transit_qty,
  CASE
    WHEN (qty_30d + stock_total_qty) = 0 THEN 0
    ELSE ROUND(qty_30d / (qty_30d + stock_total_qty), 4)
  END AS sell_through_rate_30d,
  CASE
    WHEN qty_30d = 0 THEN NULL
    ELSE ROUND(stock_available_qty / (qty_30d / 30.0), 2)
  END AS days_of_supply
FROM v_wms_sku_sales_30d_all
ORDER BY sell_through_rate_30d ASC, qty_30d ASC;

-- 负利润 SKU（基于“标价-成本”）
CREATE OR REPLACE VIEW v_wms_negative_profit_sku AS
SELECT
  sku.id AS sku_id,
  sku.sku,
  sku.title,
  sku.cost_cent,
  sku.price_cent AS sale_price_cent,
  (sku.price_cent - sku.cost_cent) AS profit_per_unit_cent,
  CASE
    WHEN sku.price_cent = 0 THEN 0
    ELSE ROUND((sku.price_cent - sku.cost_cent) / sku.price_cent, 4)
  END AS profit_rate
FROM wms_sku sku
WHERE (sku.price_cent - sku.cost_cent) < 0
ORDER BY profit_per_unit_cent ASC;

