-- 店铺 KPI 汇总（7天/30天，可做趋势图）
CREATE OR REPLACE VIEW v_wms_store_kpi_30d AS
SELECT
  platform_id,
  store_id,
  day,
  gmv_cent,
  order_cnt,
  refund_cent,
  ad_cost_cent,
  gross_profit_cent,
  CASE
    WHEN gmv_cent = 0 THEN 0
    ELSE ROUND(gross_profit_cent / gmv_cent, 4)
  END AS gross_profit_rate
FROM wms_store_kpi_daily
WHERE day >= (CURDATE() - INTERVAL 30 DAY);

-- 收支报表（按店铺按天）
CREATE OR REPLACE VIEW v_wms_cash_flow_daily AS
SELECT
  platform_id,
  store_id,
  day,
  SUM(CASE WHEN direction='IN' THEN amount_cent ELSE 0 END) AS income_cent,
  SUM(CASE WHEN direction='OUT' THEN amount_cent ELSE 0 END) AS expense_cent,
  SUM(CASE WHEN direction='IN' THEN amount_cent ELSE -amount_cent END) AS net_cent
FROM wms_cash_flow
GROUP BY platform_id, store_id, day;

-- 广告支出按天（按店铺）
CREATE OR REPLACE VIEW v_wms_ad_spend_daily AS
SELECT
  platform_id,
  store_id,
  day,
  SUM(spend_cent) AS spend_cent,
  SUM(impressions) AS impressions,
  SUM(clicks) AS clicks
FROM wms_ad_spend
GROUP BY platform_id, store_id, day;

