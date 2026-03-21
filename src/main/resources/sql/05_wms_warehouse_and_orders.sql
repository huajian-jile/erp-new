-- 分类
CREATE TABLE IF NOT EXISTS wms_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NULL,
  name VARCHAR(64) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_category_parent_name (parent_id, name)
);

-- 商品/SKU 扩展字段已合并到 01_wms_schema.sql（避免 MySQL 5.7 不支持 IF NOT EXISTS）

-- 仓库
CREATE TABLE IF NOT EXISTS wms_warehouse (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL,
  name VARCHAR(128) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_warehouse_code (code)
);

-- 仓库维度库存（可用/在途/总数）
CREATE TABLE IF NOT EXISTS wms_warehouse_inventory (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  warehouse_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  available_qty INT NOT NULL DEFAULT 0,
  in_transit_qty INT NOT NULL DEFAULT 0,
  total_qty INT NOT NULL DEFAULT 0,
  refreshed_at DATETIME NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_wh_inv (warehouse_id, sku_id),
  KEY idx_wms_wh_inv_sku (sku_id)
);

-- 采购单
CREATE TABLE IF NOT EXISTS wms_purchase_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  warehouse_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'CREATED', -- CREATED/APPROVED/SHIPPED/RECEIVED/CANCELLED
  supplier_name VARCHAR(128) NULL,
  created_by VARCHAR(64) NOT NULL DEFAULT 'system',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_purchase_order_no (order_no),
  KEY idx_wms_po_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS wms_purchase_order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  purchase_order_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  qty INT NOT NULL,
  cost_cent BIGINT NOT NULL DEFAULT 0,
  KEY idx_wms_poi_po (purchase_order_id),
  KEY idx_wms_poi_sku (sku_id)
);

-- 调拨单（仓库->仓库）
CREATE TABLE IF NOT EXISTS wms_transfer_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  from_warehouse_id BIGINT NOT NULL,
  to_warehouse_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'CREATED', -- CREATED/SHIPPED/RECEIVED/CANCELLED
  created_by VARCHAR(64) NOT NULL DEFAULT 'system',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_transfer_order_no (order_no),
  KEY idx_wms_to_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS wms_transfer_order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  transfer_order_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  qty INT NOT NULL,
  KEY idx_wms_toi_to (transfer_order_id),
  KEY idx_wms_toi_sku (sku_id)
);

-- 销售单（用于销量/利润/漏斗等分析的地基）
CREATE TABLE IF NOT EXISTS wms_sales_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  warehouse_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'CREATED', -- CREATED/PAID/SHIPPED/DELIVERED/CANCELLED/RETURNED
  paid_at DATETIME NULL,
  shipped_at DATETIME NULL,
  delivered_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_sales_order_no (order_no),
  KEY idx_wms_so_created_at (created_at),
  KEY idx_wms_so_paid_at (paid_at)
);

CREATE TABLE IF NOT EXISTS wms_sales_order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sales_order_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  qty INT NOT NULL,
  sale_price_cent BIGINT NOT NULL DEFAULT 0,
  cost_cent BIGINT NOT NULL DEFAULT 0,
  KEY idx_wms_soi_so (sales_order_id),
  KEY idx_wms_soi_sku (sku_id)
);

-- 送仓记录（采购/备货到仓的运输/签收）
CREATE TABLE IF NOT EXISTS wms_delivery_to_warehouse (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  warehouse_id BIGINT NOT NULL,
  ref_type VARCHAR(32) NOT NULL, -- PURCHASE/REPLENISH/OTHER
  ref_no VARCHAR(64) NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'CREATED', -- CREATED/IN_TRANSIT/SIGNED
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  signed_at DATETIME NULL,
  KEY idx_wms_dtw_created_at (created_at)
);

