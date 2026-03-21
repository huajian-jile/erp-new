-- 库存变动模块：分配记录/同步记录/清空记录

CREATE TABLE IF NOT EXISTS wms_inventory_allocation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  alloc_qty INT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ALLOCATED', -- ALLOCATED / SYNCED / FAILED / CLEARED
  operator VARCHAR(64) NOT NULL DEFAULT 'system',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_wms_inv_alloc_store_sku (store_id, sku_id),
  KEY idx_wms_inv_alloc_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS wms_inventory_sync_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  allocation_id BIGINT NOT NULL,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  sku_id BIGINT NOT NULL,
  alloc_qty INT NOT NULL,
  success TINYINT NOT NULL DEFAULT 0,
  request_payload TEXT NULL,
  response_payload TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_wms_inv_sync_alloc (allocation_id),
  KEY idx_wms_inv_sync_store_created (store_id, created_at)
);

CREATE TABLE IF NOT EXISTS wms_inventory_clear_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  source_store_id BIGINT NOT NULL, -- 哪个店铺分配了库存
  cleared_store_id BIGINT NOT NULL, -- 哪个店铺被清空
  sku_id BIGINT NOT NULL,
  cleared_qty INT NOT NULL,
  reason VARCHAR(128) NOT NULL DEFAULT 'ALLOCATE_CLEAR_OTHER_STORES',
  operator VARCHAR(64) NOT NULL DEFAULT 'system',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_wms_inv_clear_source (source_store_id, created_at),
  KEY idx_wms_inv_clear_cleared (cleared_store_id, created_at)
);

