CREATE TABLE IF NOT EXISTS wms_product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL,
  name VARCHAR(128) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  image_url VARCHAR(255) NULL,
  category_id BIGINT NULL,
  sales_status VARCHAR(32) NOT NULL DEFAULT 'ONSALE', -- ONSALE/OFFSALE
  refreshed_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_product_code (code)
);

CREATE TABLE IF NOT EXISTS wms_sku (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  sku VARCHAR(64) NOT NULL,
  title VARCHAR(128) NOT NULL,
  price_cent BIGINT NOT NULL DEFAULT 0,
  cost_cent BIGINT NOT NULL DEFAULT 0,
  barcode VARCHAR(64) NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_sku_sku (sku),
  KEY idx_wms_sku_product_id (product_id)
);

CREATE TABLE IF NOT EXISTS wms_store (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL DEFAULT 0,
  code VARCHAR(64) NOT NULL,
  name VARCHAR(128) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_store_code (code)
);

CREATE TABLE IF NOT EXISTS wms_store_manager (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  username VARCHAR(64) NOT NULL,
  display_name VARCHAR(64) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_store_manager_store_username (store_id, username),
  KEY idx_wms_store_manager_store_id (store_id)
);

CREATE TABLE IF NOT EXISTS wms_store_api_key (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  key_id VARCHAR(64) NOT NULL,
  key_secret_hash VARCHAR(128) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  revoked_at DATETIME NULL,
  UNIQUE KEY uk_wms_store_api_key_key_id (key_id),
  KEY idx_wms_store_api_key_store_id (store_id)
);

CREATE TABLE IF NOT EXISTS wms_inventory (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  qty INT NOT NULL DEFAULT 0,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_inventory_store_sku (store_id, sku_id),
  KEY idx_wms_inventory_sku_id (sku_id)
);

CREATE TABLE IF NOT EXISTS wms_stock_txn (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  store_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  delta INT NOT NULL,
  reason VARCHAR(64) NOT NULL,
  ref_no VARCHAR(64) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_wms_stock_txn_store_sku (store_id, sku_id),
  KEY idx_wms_stock_txn_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS wms_price_change (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sku_id BIGINT NOT NULL,
  old_price_cent BIGINT NOT NULL,
  new_price_cent BIGINT NOT NULL,
  operator VARCHAR(64) NOT NULL,
  reason VARCHAR(128) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_wms_price_change_sku_id (sku_id),
  KEY idx_wms_price_change_created_at (created_at)
);
