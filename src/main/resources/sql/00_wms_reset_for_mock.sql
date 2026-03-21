-- 【开发/演示专用】重置库：删除视图/表，保证“自动建表+灌数”在 MySQL 5.7/8.x 都可用
-- 注意：会清空 wms 库所有业务数据，仅用于模拟数据环境！

-- views
DROP VIEW IF EXISTS v_wms_inventory_snapshot;
DROP VIEW IF EXISTS v_wms_stock_txn_daily;
DROP VIEW IF EXISTS v_wms_wh_inventory_snapshot;
DROP VIEW IF EXISTS v_wms_sku_sales_30d;
DROP VIEW IF EXISTS v_wms_rank_sales_qty_30d;
DROP VIEW IF EXISTS v_wms_rank_profit_30d;
DROP VIEW IF EXISTS v_wms_sales_funnel_daily;
DROP VIEW IF EXISTS v_wms_store_kpi_30d;
DROP VIEW IF EXISTS v_wms_cash_flow_daily;
DROP VIEW IF EXISTS v_wms_ad_spend_daily;
DROP VIEW IF EXISTS v_wms_sku_sales_30d_all;
DROP VIEW IF EXISTS v_wms_sku_slow_moving_30d;
DROP VIEW IF EXISTS v_wms_negative_profit_sku;

-- tables (reverse dependency order)
DROP TABLE IF EXISTS wms_cash_flow;
DROP TABLE IF EXISTS wms_reset_record;
DROP TABLE IF EXISTS wms_ad_spend;
DROP TABLE IF EXISTS wms_store_kpi_daily;
DROP TABLE IF EXISTS wms_store_auth;

DROP TABLE IF EXISTS wms_delivery_to_warehouse;
DROP TABLE IF EXISTS wms_sales_order_item;
DROP TABLE IF EXISTS wms_sales_order;

DROP TABLE IF EXISTS wms_transfer_order_item;
DROP TABLE IF EXISTS wms_transfer_order;

DROP TABLE IF EXISTS wms_purchase_order_item;
DROP TABLE IF EXISTS wms_purchase_order;

DROP TABLE IF EXISTS wms_warehouse_inventory;
DROP TABLE IF EXISTS wms_warehouse;

DROP TABLE IF EXISTS wms_replenishment;
DROP TABLE IF EXISTS wms_channel_sku;
DROP TABLE IF EXISTS wms_platform;

DROP TABLE IF EXISTS wms_stock_txn;
DROP TABLE IF EXISTS wms_inventory;
DROP TABLE IF EXISTS wms_store_api_key;
DROP TABLE IF EXISTS wms_store_manager;
DROP TABLE IF EXISTS wms_store;

DROP TABLE IF EXISTS wms_price_change;
DROP TABLE IF EXISTS wms_sku;
DROP TABLE IF EXISTS wms_product;
DROP TABLE IF EXISTS wms_category;

