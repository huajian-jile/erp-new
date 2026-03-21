-- 平台（多渠道/多平台）
CREATE TABLE IF NOT EXISTS wms_platform (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(32) NOT NULL,
  name VARCHAR(64) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_platform_code (code)
);

-- 平台SKU映射：同一个“内部SKU”在不同平台/店铺上的外部SKU/标题/价格
CREATE TABLE IF NOT EXISTS wms_channel_sku (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  external_sku VARCHAR(64) NOT NULL,
  external_title VARCHAR(128) NOT NULL,
  external_price_cent BIGINT NOT NULL DEFAULT 0,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_channel_sku (platform_id, store_id, external_sku),
  KEY idx_wms_channel_sku_sku_id (sku_id),
  KEY idx_wms_channel_sku_store_id (store_id)
);

-- 备货（最小实现）：备货计划单（按店铺+SKU）/ 执行后入库
CREATE TABLE IF NOT EXISTS wms_replenishment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  plan_qty INT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'CREATED', -- CREATED / EXECUTED / CANCELLED
  created_by VARCHAR(64) NOT NULL DEFAULT 'system',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  executed_at DATETIME NULL,
  KEY idx_wms_replenishment_store_sku (store_id, sku_id),
  KEY idx_wms_replenishment_created_at (created_at)
);

