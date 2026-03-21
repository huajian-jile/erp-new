-- 店铺授权（平台->店铺的授权/令牌保存）
CREATE TABLE IF NOT EXISTS wms_store_auth (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  auth_type VARCHAR(32) NOT NULL DEFAULT 'OAUTH', -- OAUTH/COOKIE/OTHER
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',   -- ACTIVE/EXPIRED/REVOKED
  access_token TEXT NULL,
  refresh_token TEXT NULL,
  token_expires_at DATETIME NULL,
  authorized_at DATETIME NULL,
  refreshed_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_store_auth (platform_id, store_id),
  KEY idx_wms_store_auth_store (store_id)
);

-- 店铺 KPI（按天，选择平台后按店铺维度看）
CREATE TABLE IF NOT EXISTS wms_store_kpi_daily (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  day DATE NOT NULL,
  gmv_cent BIGINT NOT NULL DEFAULT 0,
  order_cnt INT NOT NULL DEFAULT 0,
  refund_cent BIGINT NOT NULL DEFAULT 0,
  ad_cost_cent BIGINT NOT NULL DEFAULT 0,
  gross_profit_cent BIGINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_store_kpi (platform_id, store_id, day),
  KEY idx_wms_store_kpi_day (day)
);

-- 广告支出（明细，可汇总到 KPI）
CREATE TABLE IF NOT EXISTS wms_ad_spend (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  day DATE NOT NULL,
  channel VARCHAR(32) NOT NULL DEFAULT 'UNKNOWN', -- SEARCH/FEED/LIVE/...
  spend_cent BIGINT NOT NULL DEFAULT 0,
  impressions BIGINT NOT NULL DEFAULT 0,
  clicks BIGINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_wms_ad_spend_store_day (store_id, day)
);

-- 重置记录（运营动作留痕）
CREATE TABLE IF NOT EXISTS wms_reset_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  reset_type VARCHAR(32) NOT NULL, -- PRICE/INVENTORY/AUTH/OTHER
  reason VARCHAR(255) NULL,
  operator VARCHAR(64) NOT NULL DEFAULT 'system',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_wms_reset_record_store_created (store_id, created_at)
);

-- 收支流水（财务明细）
CREATE TABLE IF NOT EXISTS wms_cash_flow (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  platform_id BIGINT NOT NULL,
  store_id BIGINT NOT NULL,
  day DATE NOT NULL,
  direction VARCHAR(8) NOT NULL, -- IN/OUT
  biz_type VARCHAR(32) NOT NULL,  -- SALES/REFUND/AD/FEE/OTHER
  amount_cent BIGINT NOT NULL DEFAULT 0,
  ref_no VARCHAR(64) NULL,
  remark VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_wms_cash_flow_store_day (store_id, day),
  KEY idx_wms_cash_flow_biz (biz_type)
);

