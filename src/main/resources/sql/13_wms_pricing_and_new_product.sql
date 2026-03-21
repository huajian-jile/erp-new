-- 成本变更记录
CREATE TABLE IF NOT EXISTS wms_cost_change (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sku_id BIGINT NOT NULL,
  old_cost_cent BIGINT NOT NULL,
  new_cost_cent BIGINT NOT NULL,
  operator VARCHAR(64) NOT NULL,
  reason VARCHAR(128) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_wms_cost_change_sku_id (sku_id),
  KEY idx_wms_cost_change_created_at (created_at)
);

-- 商品利润表（SKU维度）
CREATE OR REPLACE VIEW v_wms_sku_profit AS
SELECT
  p.id AS product_id,
  p.code AS product_code,
  p.name AS product_name,
  p.sales_status,
  s.id AS sku_id,
  s.sku,
  s.title,
  s.price_cent,
  s.cost_cent,
  (s.price_cent - s.cost_cent) AS profit_cent,
  CASE
    WHEN s.price_cent = 0 THEN 0
    ELSE ROUND(((s.price_cent - s.cost_cent) / s.price_cent) * 100, 2)
  END AS profit_margin_pct,
  s.updated_at AS sku_updated_at
FROM wms_product p
JOIN wms_sku s ON s.product_id = p.id;

-- 店铺维度商品利润表（带库存）
CREATE OR REPLACE VIEW v_wms_store_sku_profit AS
SELECT
  st.platform_id,
  st.id AS store_id,
  st.code AS store_code,
  st.name AS store_name,
  sp.product_id,
  sp.product_code,
  sp.product_name,
  sp.sales_status,
  sp.sku_id,
  sp.sku,
  sp.title,
  sp.price_cent,
  sp.cost_cent,
  sp.profit_cent,
  sp.profit_margin_pct,
  COALESCE(inv.qty, 0) AS store_stock_qty,
  (COALESCE(inv.qty, 0) * sp.profit_cent) AS stock_profit_cent,
  inv.updated_at AS stock_updated_at,
  DATEDIFF(CURDATE(), DATE(inv.updated_at)) AS stock_age_days
FROM v_wms_sku_profit sp
JOIN wms_inventory inv ON inv.sku_id = sp.sku_id
JOIN wms_store st ON st.id = inv.store_id;

-- 调价待办（SQL判断：库存>0 + 库龄>=minDays + 近7天销量为0）
CREATE OR REPLACE VIEW v_wms_price_change_todo AS
SELECT
  st.platform_id,
  st.id AS store_id,
  st.code AS store_code,
  st.name AS store_name,
  inv.sku_id,
  sku.sku,
  sku.title,
  inv.qty AS store_stock_qty,
  inv.updated_at AS stock_updated_at,
  DATEDIFF(CURDATE(), DATE(inv.updated_at)) AS stock_age_days,
  COALESCE(s7.qty_7d, 0) AS qty_7d,
  sku.price_cent,
  sku.cost_cent,
  (sku.price_cent - sku.cost_cent) AS profit_cent,
  CASE
    WHEN sku.price_cent = 0 THEN 0
    ELSE ROUND(((sku.price_cent - sku.cost_cent) / sku.price_cent) * 100, 2)
  END AS profit_margin_pct,
  GREATEST(0, ROUND(sku.price_cent * 0.9)) AS suggested_price_cent
FROM wms_inventory inv
JOIN wms_store st ON st.id = inv.store_id
JOIN wms_sku sku ON sku.id = inv.sku_id
LEFT JOIN v_wms_store_sku_sales_7d s7
  ON s7.platform_id = st.platform_id AND s7.store_id = st.id AND s7.sku_id = inv.sku_id
WHERE inv.qty > 0
  AND DATEDIFF(CURDATE(), DATE(inv.updated_at)) >= 30
  AND COALESCE(s7.qty_7d, 0) = 0
ORDER BY stock_age_days DESC, inv.qty DESC;

-- 新品上架（调研候选品）
CREATE TABLE IF NOT EXISTS wms_new_product_research (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  candidate_code VARCHAR(64) NOT NULL,
  name VARCHAR(128) NOT NULL,
  category VARCHAR(64) NULL,
  expected_cost_cent BIGINT NOT NULL DEFAULT 0,
  expected_price_cent BIGINT NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'NEW', -- NEW/APPROVED/REJECTED/ONBOARDED
  created_by VARCHAR(64) NOT NULL DEFAULT 'mock',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_new_prod_platform_code (platform_id, candidate_code),
  KEY idx_wms_new_prod_status (status),
  KEY idx_wms_new_prod_created_at (created_at)
);

