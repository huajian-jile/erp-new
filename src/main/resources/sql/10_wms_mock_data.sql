-- 先确保已执行 01_wms_schema.sql / 02_wms_views.sql / 03_wms_functions_and_materialized.sql

-- 平台
INSERT INTO wms_platform(code, name, status)
VALUES
('PDD', '拼多多', 'ACTIVE'),
('TB', '淘宝', 'ACTIVE'),
('DEWU', '得物', 'ACTIVE')
ON DUPLICATE KEY UPDATE name=VALUES(name), status=VALUES(status);

-- 分类
INSERT INTO wms_category(parent_id, name)
VALUES
(NULL, '饮料'),
(NULL, '零食'),
(NULL, '日用品')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 商品
INSERT INTO wms_product(code, name, status)
VALUES
('P1001', '可口可乐 330ml', 'ACTIVE'),
('P1002', '百事可乐 330ml', 'ACTIVE'),
('P2001', '乐事薯片 原味', 'ACTIVE'),
('P2002', '奥利奥 饼干', 'ACTIVE'),
('P3001', '清风 抽纸', 'ACTIVE'),
('P4001', '运动鞋（示例）', 'ACTIVE')
ON DUPLICATE KEY UPDATE name=VALUES(name), status=VALUES(status);

-- 补商品扩展字段
UPDATE wms_product SET
  image_url = 'https://picsum.photos/seed/p1001/200/200',
  category_id = (SELECT id FROM wms_category WHERE name='饮料' LIMIT 1),
  sales_status = 'ONSALE',
  refreshed_at = NOW()
WHERE code='P1001';

UPDATE wms_product SET
  image_url = 'https://picsum.photos/seed/p1002/200/200',
  category_id = (SELECT id FROM wms_category WHERE name='饮料' LIMIT 1),
  sales_status = 'ONSALE',
  refreshed_at = NOW()
WHERE code='P1002';

UPDATE wms_product SET
  image_url = 'https://picsum.photos/seed/p2001/200/200',
  category_id = (SELECT id FROM wms_category WHERE name='零食' LIMIT 1),
  sales_status = 'ONSALE',
  refreshed_at = NOW()
WHERE code='P2001';

UPDATE wms_product SET
  image_url = 'https://picsum.photos/seed/p2002/200/200',
  category_id = (SELECT id FROM wms_category WHERE name='零食' LIMIT 1),
  sales_status = 'ONSALE',
  refreshed_at = NOW()
WHERE code='P2002';

UPDATE wms_product SET
  image_url = 'https://picsum.photos/seed/p3001/200/200',
  category_id = (SELECT id FROM wms_category WHERE name='日用品' LIMIT 1),
  sales_status = 'ONSALE',
  refreshed_at = NOW()
WHERE code='P3001';

UPDATE wms_product SET
  image_url = 'https://picsum.photos/seed/p4001/200/200',
  category_id = (SELECT id FROM wms_category WHERE name='日用品' LIMIT 1),
  sales_status = 'ONSALE',
  refreshed_at = NOW()
WHERE code='P4001';

-- SKU
INSERT INTO wms_sku(product_id, sku, title, price_cent, cost_cent, barcode, status)
SELECT p.id, 'SKU-CC-330', '可口可乐 330ml 单罐', 350, 220, '690000000001', 'ACTIVE'
FROM wms_product p WHERE p.code='P1001'
ON DUPLICATE KEY UPDATE title=VALUES(title), price_cent=VALUES(price_cent), cost_cent=VALUES(cost_cent), barcode=VALUES(barcode), status=VALUES(status);

INSERT INTO wms_sku(product_id, sku, title, price_cent, cost_cent, barcode, status)
SELECT p.id, 'SKU-PS-330', '百事可乐 330ml 单罐', 330, 380, '690000000002', 'ACTIVE'
FROM wms_product p WHERE p.code='P1002'
ON DUPLICATE KEY UPDATE title=VALUES(title), price_cent=VALUES(price_cent), cost_cent=VALUES(cost_cent), barcode=VALUES(barcode), status=VALUES(status);

INSERT INTO wms_sku(product_id, sku, title, price_cent, cost_cent, barcode, status)
SELECT p.id, 'SKU-LS-ORG', '乐事薯片 原味 70g', 590, 360, '690000000003', 'ACTIVE'
FROM wms_product p WHERE p.code='P2001'
ON DUPLICATE KEY UPDATE title=VALUES(title), price_cent=VALUES(price_cent), cost_cent=VALUES(cost_cent), barcode=VALUES(barcode), status=VALUES(status);

INSERT INTO wms_sku(product_id, sku, title, price_cent, cost_cent, barcode, status)
SELECT p.id, 'SKU-OL-STD', '奥利奥 原味 12片', 490, 300, '690000000004', 'ACTIVE'
FROM wms_product p WHERE p.code='P2002'
ON DUPLICATE KEY UPDATE title=VALUES(title), price_cent=VALUES(price_cent), cost_cent=VALUES(cost_cent), barcode=VALUES(barcode), status=VALUES(status);

INSERT INTO wms_sku(product_id, sku, title, price_cent, cost_cent, barcode, status)
SELECT p.id, 'SKU-QF-TP', '清风 抽纸 3层*100抽', 1290, 820, '690000000005', 'ACTIVE'
FROM wms_product p WHERE p.code='P3001'
ON DUPLICATE KEY UPDATE title=VALUES(title), price_cent=VALUES(price_cent), cost_cent=VALUES(cost_cent), barcode=VALUES(barcode), status=VALUES(status);

INSERT INTO wms_sku(product_id, sku, title, price_cent, cost_cent, barcode, status)
SELECT p.id, 'SKU-SN-001', '运动鞋 42码', 5999, 6200, '690000000006', 'ACTIVE'
FROM wms_product p WHERE p.code='P4001'
ON DUPLICATE KEY UPDATE title=VALUES(title), price_cent=VALUES(price_cent), cost_cent=VALUES(cost_cent), barcode=VALUES(barcode), status=VALUES(status);

-- 店铺
INSERT INTO wms_store(code, name, platform_id)
SELECT 'PDD-S001', '拼多多-华东一店', p.id FROM wms_platform p WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE name=VALUES(name), platform_id=VALUES(platform_id);

-- 同平台第二店铺（用于“清空其它店铺库存”演示）
INSERT INTO wms_store(code, name, platform_id)
SELECT 'PDD-S002', '拼多多-华东二店', p.id FROM wms_platform p WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE name=VALUES(name), platform_id=VALUES(platform_id);

-- 同平台第三店铺（更多数据）
INSERT INTO wms_store(code, name, platform_id)
SELECT 'PDD-S003', '拼多多-华北三店', p.id FROM wms_platform p WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE name=VALUES(name), platform_id=VALUES(platform_id);

INSERT INTO wms_store(code, name, platform_id)
SELECT 'TB-S001', '淘宝-华东一店', p.id FROM wms_platform p WHERE p.code='TB'
ON DUPLICATE KEY UPDATE name=VALUES(name), platform_id=VALUES(platform_id);

INSERT INTO wms_store(code, name, platform_id)
SELECT 'TB-S002', '淘宝-华南二店', p.id FROM wms_platform p WHERE p.code='TB'
ON DUPLICATE KEY UPDATE name=VALUES(name), platform_id=VALUES(platform_id);

INSERT INTO wms_store(code, name, platform_id)
SELECT 'DEWU-S001', '得物-华东一店', p.id FROM wms_platform p WHERE p.code='DEWU'
ON DUPLICATE KEY UPDATE name=VALUES(name), platform_id=VALUES(platform_id);

INSERT INTO wms_store(code, name, platform_id)
SELECT 'DEWU-S002', '得物-华北二店', p.id FROM wms_platform p WHERE p.code='DEWU'
ON DUPLICATE KEY UPDATE name=VALUES(name), platform_id=VALUES(platform_id);

-- 店长
INSERT INTO wms_store_manager(store_id, username, display_name)
SELECT s.id, 'manager', '店长'
FROM wms_store s WHERE s.code='PDD-S001'
ON DUPLICATE KEY UPDATE display_name=VALUES(display_name);

-- 仓库
INSERT INTO wms_warehouse(code, name, status)
VALUES
('WH-SH', '上海仓', 'ACTIVE'),
('WH-GZ', '广州仓', 'ACTIVE')
ON DUPLICATE KEY UPDATE name=VALUES(name), status=VALUES(status);

-- 店铺授权（示例：每个平台店铺都有一条 ACTIVE 授权记录）
INSERT INTO wms_store_auth(platform_id, store_id, auth_type, status, access_token, refresh_token, token_expires_at, authorized_at, refreshed_at)
SELECT p.id, st.id, 'OAUTH', 'ACTIVE',
       CONCAT('access_', p.code, '_', st.code),
       CONCAT('refresh_', p.code, '_', st.code),
       NOW() + INTERVAL 7 DAY,
       NOW() - INTERVAL 1 DAY,
       NOW()
FROM wms_platform p
JOIN wms_store st ON st.platform_id = p.id
WHERE st.code IN ('PDD-S001','PDD-S002','PDD-S003','TB-S001','TB-S002','DEWU-S001','DEWU-S002')
ON DUPLICATE KEY UPDATE status=VALUES(status), refreshed_at=VALUES(refreshed_at), token_expires_at=VALUES(token_expires_at);

-- 平台SKU映射（示例：同一个内部 SKU，在不同平台外部SKU不同、标题不同、价格不同）
INSERT INTO wms_channel_sku(platform_id, store_id, sku_id, external_sku, external_title, external_price_cent, status)
SELECT p.id, st.id, sk.id, 'PDD-EXT-CC330', '拼多多 可口可乐330ml', 339, 'ACTIVE'
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S001'
JOIN wms_sku sk ON sk.sku='SKU-CC-330'
WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE external_title=VALUES(external_title), external_price_cent=VALUES(external_price_cent), status=VALUES(status);

INSERT INTO wms_channel_sku(platform_id, store_id, sku_id, external_sku, external_title, external_price_cent, status)
SELECT p.id, st.id, sk.id, 'TB-EXT-CC330', '淘宝 可口可乐330ml', 359, 'ACTIVE'
FROM wms_platform p
JOIN wms_store st ON st.code='TB-S001'
JOIN wms_sku sk ON sk.sku='SKU-CC-330'
WHERE p.code='TB'
ON DUPLICATE KEY UPDATE external_title=VALUES(external_title), external_price_cent=VALUES(external_price_cent), status=VALUES(status);

INSERT INTO wms_channel_sku(platform_id, store_id, sku_id, external_sku, external_title, external_price_cent, status)
SELECT p.id, st.id, sk.id, 'DEWU-EXT-CC330', '得物 可口可乐330ml', 399, 'ACTIVE'
FROM wms_platform p
JOIN wms_store st ON st.code='DEWU-S001'
JOIN wms_sku sk ON sk.sku='SKU-CC-330'
WHERE p.code='DEWU'
ON DUPLICATE KEY UPDATE external_title=VALUES(external_title), external_price_cent=VALUES(external_price_cent), status=VALUES(status);

-- 店铺维度库存台账（直接给初始库存，流水用下面补）
INSERT INTO wms_inventory(store_id, sku_id, qty)
SELECT st.id, sk.id, 120
FROM wms_store st JOIN wms_sku sk
WHERE st.code='PDD-S001' AND sk.sku='SKU-CC-330'
ON DUPLICATE KEY UPDATE qty=VALUES(qty);

-- 给 PDD-S002 同 SKU 一些库存（用于清零）
INSERT INTO wms_inventory(store_id, sku_id, qty)
SELECT st.id, sk.id, 66
FROM wms_store st JOIN wms_sku sk
WHERE st.code='PDD-S002' AND sk.sku='SKU-CC-330'
ON DUPLICATE KEY UPDATE qty=VALUES(qty);

-- 更多店铺&SKU库存（丰富页面数据）
INSERT INTO wms_inventory(store_id, sku_id, qty)
SELECT st.id, sk.id, 35
FROM wms_store st JOIN wms_sku sk
WHERE st.code='PDD-S003' AND sk.sku='SKU-OL-STD'
ON DUPLICATE KEY UPDATE qty=VALUES(qty);

INSERT INTO wms_inventory(store_id, sku_id, qty)
SELECT st.id, sk.id, 18
FROM wms_store st JOIN wms_sku sk
WHERE st.code='TB-S001' AND sk.sku='SKU-QF-TP'
ON DUPLICATE KEY UPDATE qty=VALUES(qty);

INSERT INTO wms_inventory(store_id, sku_id, qty)
SELECT st.id, sk.id, 52
FROM wms_store st JOIN wms_sku sk
WHERE st.code='TB-S002' AND sk.sku='SKU-LS-ORG'
ON DUPLICATE KEY UPDATE qty=VALUES(qty);

INSERT INTO wms_inventory(store_id, sku_id, qty)
SELECT st.id, sk.id, 9
FROM wms_store st JOIN wms_sku sk
WHERE st.code='DEWU-S001' AND sk.sku='SKU-CC-330'
ON DUPLICATE KEY UPDATE qty=VALUES(qty);

INSERT INTO wms_inventory(store_id, sku_id, qty)
SELECT st.id, sk.id, 80
FROM wms_store st JOIN wms_sku sk
WHERE st.code='PDD-S001' AND sk.sku='SKU-PS-330'
ON DUPLICATE KEY UPDATE qty=VALUES(qty);

INSERT INTO wms_inventory(store_id, sku_id, qty)
SELECT st.id, sk.id, 40
FROM wms_store st JOIN wms_sku sk
WHERE st.code='PDD-S001' AND sk.sku='SKU-LS-ORG'
ON DUPLICATE KEY UPDATE qty=VALUES(qty);

-- 仓库维度库存（可用/在途/总数/刷新时间）
INSERT INTO wms_warehouse_inventory(warehouse_id, sku_id, available_qty, in_transit_qty, total_qty, refreshed_at)
SELECT wh.id, sk.id, 500, 80, 580, NOW()
FROM wms_warehouse wh JOIN wms_sku sk
WHERE wh.code='WH-SH' AND sk.sku='SKU-CC-330'
ON DUPLICATE KEY UPDATE available_qty=VALUES(available_qty), in_transit_qty=VALUES(in_transit_qty), total_qty=VALUES(total_qty), refreshed_at=VALUES(refreshed_at);

INSERT INTO wms_warehouse_inventory(warehouse_id, sku_id, available_qty, in_transit_qty, total_qty, refreshed_at)
SELECT wh.id, sk.id, 260, 20, 280, NOW()
FROM wms_warehouse wh JOIN wms_sku sk
WHERE wh.code='WH-GZ' AND sk.sku='SKU-CC-330'
ON DUPLICATE KEY UPDATE available_qty=VALUES(available_qty), in_transit_qty=VALUES(in_transit_qty), total_qty=VALUES(total_qty), refreshed_at=VALUES(refreshed_at);

-- 给另外两个 SKU 也塞仓库库存，便于做滞销/动销率展示
INSERT INTO wms_warehouse_inventory(warehouse_id, sku_id, available_qty, in_transit_qty, total_qty, refreshed_at)
SELECT wh.id, sk.id, 300, 0, 300, NOW()
FROM wms_warehouse wh JOIN wms_sku sk
WHERE wh.code='WH-SH' AND sk.sku='SKU-PS-330'
ON DUPLICATE KEY UPDATE available_qty=VALUES(available_qty), in_transit_qty=VALUES(in_transit_qty), total_qty=VALUES(total_qty), refreshed_at=VALUES(refreshed_at);

INSERT INTO wms_warehouse_inventory(warehouse_id, sku_id, available_qty, in_transit_qty, total_qty, refreshed_at)
SELECT wh.id, sk.id, 999, 0, 999, NOW()
FROM wms_warehouse wh JOIN wms_sku sk
WHERE wh.code='WH-SH' AND sk.sku='SKU-LS-ORG'
ON DUPLICATE KEY UPDATE available_qty=VALUES(available_qty), in_transit_qty=VALUES(in_transit_qty), total_qty=VALUES(total_qty), refreshed_at=VALUES(refreshed_at);

-- 库存流水（给趋势图用：近 7 天随机变化）
INSERT INTO wms_stock_txn(store_id, sku_id, delta, reason, ref_no, created_at)
SELECT st.id, sk.id, d.delta, 'MOCK', CONCAT('MOCK-', DATE_FORMAT(d.day, '%Y%m%d')), CONCAT(d.day, ' 10:00:00')
FROM (
  SELECT CURDATE() - INTERVAL 6 DAY AS day,  10 AS delta UNION ALL
  SELECT CURDATE() - INTERVAL 5 DAY,         -3 UNION ALL
  SELECT CURDATE() - INTERVAL 4 DAY,          8 UNION ALL
  SELECT CURDATE() - INTERVAL 3 DAY,         -6 UNION ALL
  SELECT CURDATE() - INTERVAL 2 DAY,          5 UNION ALL
  SELECT CURDATE() - INTERVAL 1 DAY,         -2 UNION ALL
  SELECT CURDATE(),                           4
) d
JOIN wms_store st ON st.code='PDD-S001'
JOIN wms_sku sk ON sk.sku='SKU-CC-330';

-- 销售单+明细（用于排行榜/利润/漏斗）
INSERT INTO wms_sales_order(order_no, platform_id, store_id, warehouse_id, status, paid_at, shipped_at, delivered_at, created_at)
SELECT 'SO-0001',
       p.id,
       st.id,
       wh.id,
       'DELIVERED',
       NOW() - INTERVAL 5 DAY,
       NOW() - INTERVAL 4 DAY,
       NOW() - INTERVAL 3 DAY,
       NOW() - INTERVAL 6 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S001'
JOIN wms_warehouse wh ON wh.code='WH-SH'
WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO wms_sales_order_item(sales_order_id, sku_id, qty, sale_price_cent, cost_cent)
SELECT so.id, sk.id, 30, 339, 220
FROM wms_sales_order so JOIN wms_sku sk
WHERE so.order_no='SO-0001' AND sk.sku='SKU-CC-330';

INSERT INTO wms_sales_order(order_no, platform_id, store_id, warehouse_id, status, paid_at, shipped_at, delivered_at, created_at)
SELECT 'SO-0002',
       p.id,
       st.id,
       wh.id,
       'SHIPPED',
       NOW() - INTERVAL 2 DAY,
       NOW() - INTERVAL 1 DAY,
       NULL,
       NOW() - INTERVAL 2 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S001'
JOIN wms_warehouse wh ON wh.code='WH-SH'
WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO wms_sales_order_item(sales_order_id, sku_id, qty, sale_price_cent, cost_cent)
SELECT so.id, sk.id, 12, 339, 220
FROM wms_sales_order so JOIN wms_sku sk
WHERE so.order_no='SO-0002' AND sk.sku='SKU-CC-330';

-- 更多销售单（多店铺/多状态/多SKU，用于“订单管理”列表）
INSERT INTO wms_sales_order(order_no, platform_id, store_id, warehouse_id, status, paid_at, shipped_at, delivered_at, created_at)
SELECT 'SO-0101', p.id, st.id, wh.id, 'CREATED', NULL, NULL, NULL, NOW() - INTERVAL 1 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S002'
JOIN wms_warehouse wh ON wh.code='WH-SH'
WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO wms_sales_order_item(sales_order_id, sku_id, qty, sale_price_cent, cost_cent)
SELECT so.id, sk.id, 2, 339, 220
FROM wms_sales_order so JOIN wms_sku sk
WHERE so.order_no='SO-0101' AND sk.sku='SKU-CC-330';

INSERT INTO wms_sales_order_item(sales_order_id, sku_id, qty, sale_price_cent, cost_cent)
SELECT so.id, sk.id, 1, 5999, 6200
FROM wms_sales_order so JOIN wms_sku sk
WHERE so.order_no='SO-0101' AND sk.sku='SKU-SN-001';

INSERT INTO wms_sales_order(order_no, platform_id, store_id, warehouse_id, status, paid_at, shipped_at, delivered_at, created_at)
SELECT 'SO-0102', p.id, st.id, wh.id, 'PAID', NOW() - INTERVAL 2 DAY, NULL, NULL, NOW() - INTERVAL 2 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S002'
JOIN wms_warehouse wh ON wh.code='WH-SH'
WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO wms_sales_order_item(sales_order_id, sku_id, qty, sale_price_cent, cost_cent)
SELECT so.id, sk.id, 3, 339, 220
FROM wms_sales_order so JOIN wms_sku sk
WHERE so.order_no='SO-0102' AND sk.sku='SKU-CC-330';

INSERT INTO wms_sales_order(order_no, platform_id, store_id, warehouse_id, status, paid_at, shipped_at, delivered_at, created_at)
SELECT 'SO-0103', p.id, st.id, wh.id, 'CANCELLED', NULL, NULL, NULL, NOW() - INTERVAL 4 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S002'
JOIN wms_warehouse wh ON wh.code='WH-GZ'
WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO wms_sales_order_item(sales_order_id, sku_id, qty, sale_price_cent, cost_cent)
SELECT so.id, sk.id, 1, 1999, 1200
FROM wms_sales_order so JOIN wms_sku sk
WHERE so.order_no='SO-0103' AND sk.sku='SKU-PS-330';

INSERT INTO wms_sales_order(order_no, platform_id, store_id, warehouse_id, status, paid_at, shipped_at, delivered_at, created_at)
SELECT 'SO-0201', p.id, st.id, wh.id, 'DELIVERED',
       NOW() - INTERVAL 8 DAY,
       NOW() - INTERVAL 7 DAY,
       NOW() - INTERVAL 6 DAY,
       NOW() - INTERVAL 9 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='TB-S001'
JOIN wms_warehouse wh ON wh.code='WH-GZ'
WHERE p.code='TB'
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO wms_sales_order_item(sales_order_id, sku_id, qty, sale_price_cent, cost_cent)
SELECT so.id, sk.id, 5, 339, 220
FROM wms_sales_order so JOIN wms_sku sk
WHERE so.order_no='SO-0201' AND sk.sku='SKU-CC-330';

INSERT INTO wms_sales_order_item(sales_order_id, sku_id, qty, sale_price_cent, cost_cent)
SELECT so.id, sk.id, 2, 12999, 9800
FROM wms_sales_order so JOIN wms_sku sk
WHERE so.order_no='SO-0201' AND sk.sku='SKU-LS-ORG';

INSERT INTO wms_sales_order(order_no, platform_id, store_id, warehouse_id, status, paid_at, shipped_at, delivered_at, created_at)
SELECT 'SO-0301', p.id, st.id, wh.id, 'RETURNED',
       NOW() - INTERVAL 12 DAY,
       NOW() - INTERVAL 11 DAY,
       NOW() - INTERVAL 10 DAY,
       NOW() - INTERVAL 13 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='DEWU-S001'
JOIN wms_warehouse wh ON wh.code='WH-SH'
WHERE p.code='DEWU'
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO wms_sales_order_item(sales_order_id, sku_id, qty, sale_price_cent, cost_cent)
SELECT so.id, sk.id, 1, 5999, 6200
FROM wms_sales_order so JOIN wms_sku sk
WHERE so.order_no='SO-0301' AND sk.sku='SKU-SN-001';

-- 制造“待分配库存”：给 PDD-S001 最近7天销量很高，但店铺库存很低
INSERT INTO wms_sales_order(order_no, platform_id, store_id, warehouse_id, status, paid_at, shipped_at, delivered_at, created_at)
SELECT 'SO-0003',
       p.id,
       st.id,
       wh.id,
       'PAID',
       NOW() - INTERVAL 1 DAY,
       NULL,
       NULL,
       NOW() - INTERVAL 1 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S001'
JOIN wms_warehouse wh ON wh.code='WH-SH'
WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO wms_sales_order_item(sales_order_id, sku_id, qty, sale_price_cent, cost_cent)
SELECT so.id, sk.id, 300, 339, 220
FROM wms_sales_order so JOIN wms_sku sk
WHERE so.order_no='SO-0003' AND sk.sku='SKU-CC-330';

-- 把店铺库存压低，确保触发 need_alloc_qty
INSERT INTO wms_inventory(store_id, sku_id, qty)
SELECT st.id, sk.id, 10
FROM wms_store st JOIN wms_sku sk
WHERE st.code='PDD-S001' AND sk.sku='SKU-CC-330'
ON DUPLICATE KEY UPDATE qty=VALUES(qty);

-- KPI/广告/收支（按店铺维度，近 7 天）
INSERT INTO wms_store_kpi_daily(platform_id, store_id, day, gmv_cent, order_cnt, refund_cent, ad_cost_cent, gross_profit_cent)
SELECT p.id, st.id, d.day,
       d.gmv_cent, d.order_cnt, d.refund_cent, d.ad_cost_cent,
       (d.gmv_cent - d.refund_cent - d.ad_cost_cent) AS gross_profit_cent
FROM wms_platform p
JOIN wms_store st ON st.platform_id = p.id
JOIN (
  SELECT CURDATE() - INTERVAL 6 DAY AS day, 200000 AS gmv_cent, 30 AS order_cnt, 10000 AS refund_cent, 8000 AS ad_cost_cent UNION ALL
  SELECT CURDATE() - INTERVAL 5 DAY,        180000,             28,             9000,              7000 UNION ALL
  SELECT CURDATE() - INTERVAL 4 DAY,        210000,             33,             8000,              9000 UNION ALL
  SELECT CURDATE() - INTERVAL 3 DAY,        260000,             40,             15000,             12000 UNION ALL
  SELECT CURDATE() - INTERVAL 2 DAY,        240000,             37,             11000,             10000 UNION ALL
  SELECT CURDATE() - INTERVAL 1 DAY,        300000,             46,             20000,             16000 UNION ALL
  SELECT CURDATE(),                         320000,             50,             18000,             15000
) d
WHERE p.code='PDD' AND st.code='PDD-S001'
ON DUPLICATE KEY UPDATE gmv_cent=VALUES(gmv_cent), order_cnt=VALUES(order_cnt), refund_cent=VALUES(refund_cent), ad_cost_cent=VALUES(ad_cost_cent), gross_profit_cent=VALUES(gross_profit_cent);

INSERT INTO wms_ad_spend(platform_id, store_id, day, channel, spend_cent, impressions, clicks)
SELECT p.id, st.id, CURDATE() - INTERVAL 1 DAY, 'SEARCH', 9000, 120000, 2400
FROM wms_platform p JOIN wms_store st ON st.code='PDD-S001' WHERE p.code='PDD';

-- 更多广告日报（近 7 天，多店铺）
INSERT INTO wms_ad_spend(platform_id, store_id, day, channel, spend_cent, impressions, clicks)
SELECT p.id, st.id, d.day, 'FEED', d.spend_cent, d.impressions, d.clicks
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S002'
JOIN (
  SELECT CURDATE() - INTERVAL 6 DAY AS day, 6000 AS spend_cent, 80000 AS impressions, 1600 AS clicks UNION ALL
  SELECT CURDATE() - INTERVAL 5 DAY,        6500,             82000,             1700 UNION ALL
  SELECT CURDATE() - INTERVAL 4 DAY,        7200,             90000,             1900 UNION ALL
  SELECT CURDATE() - INTERVAL 3 DAY,        8000,             98000,             2100 UNION ALL
  SELECT CURDATE() - INTERVAL 2 DAY,        7600,             93000,             2000 UNION ALL
  SELECT CURDATE() - INTERVAL 1 DAY,        8400,             99000,             2200 UNION ALL
  SELECT CURDATE(),                         9100,             110000,            2400
) d
WHERE p.code='PDD';

INSERT INTO wms_cash_flow(platform_id, store_id, day, direction, biz_type, amount_cent, ref_no, remark)
SELECT p.id, st.id, CURDATE() - INTERVAL 1 DAY, 'IN', 'SALES', 300000, 'CF-SALES-1', '销售回款'
FROM wms_platform p JOIN wms_store st ON st.code='PDD-S001' WHERE p.code='PDD';

INSERT INTO wms_cash_flow(platform_id, store_id, day, direction, biz_type, amount_cent, ref_no, remark)
SELECT p.id, st.id, CURDATE() - INTERVAL 1 DAY, 'OUT', 'AD', 9000, 'CF-AD-1', '广告支出'
FROM wms_platform p JOIN wms_store st ON st.code='PDD-S001' WHERE p.code='PDD';

-- 更多收支日报（近 7 天，多店铺）
INSERT INTO wms_cash_flow(platform_id, store_id, day, direction, biz_type, amount_cent, ref_no, remark)
SELECT p.id, st.id, d.day, 'IN', 'SALES', d.income_cent, CONCAT('CF-IN-', DATE_FORMAT(d.day,'%Y%m%d')), '销售回款'
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S002'
JOIN (
  SELECT CURDATE() - INTERVAL 6 DAY AS day, 180000 AS income_cent UNION ALL
  SELECT CURDATE() - INTERVAL 5 DAY,        165000 UNION ALL
  SELECT CURDATE() - INTERVAL 4 DAY,        190000 UNION ALL
  SELECT CURDATE() - INTERVAL 3 DAY,        210000 UNION ALL
  SELECT CURDATE() - INTERVAL 2 DAY,        205000 UNION ALL
  SELECT CURDATE() - INTERVAL 1 DAY,        230000 UNION ALL
  SELECT CURDATE(),                         240000
) d
WHERE p.code='PDD';

INSERT INTO wms_cash_flow(platform_id, store_id, day, direction, biz_type, amount_cent, ref_no, remark)
SELECT p.id, st.id, d.day, 'OUT', 'FEE', d.fee_cent, CONCAT('CF-FEE-', DATE_FORMAT(d.day,'%Y%m%d')), '平台服务费'
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S002'
JOIN (
  SELECT CURDATE() - INTERVAL 6 DAY AS day, 3000 AS fee_cent UNION ALL
  SELECT CURDATE() - INTERVAL 5 DAY,        2800 UNION ALL
  SELECT CURDATE() - INTERVAL 4 DAY,        3200 UNION ALL
  SELECT CURDATE() - INTERVAL 3 DAY,        3500 UNION ALL
  SELECT CURDATE() - INTERVAL 2 DAY,        3300 UNION ALL
  SELECT CURDATE() - INTERVAL 1 DAY,        3700 UNION ALL
  SELECT CURDATE(),                         3900
) d
WHERE p.code='PDD';

-- 采购单（CREATED -> APPROVED -> SHIPPED -> RECEIVED）
INSERT INTO wms_purchase_order(order_no, platform_id, store_id, warehouse_id, status, supplier_name, created_by, created_at)
SELECT 'PO-0001', p.id, st.id, wh.id, 'CREATED', '可口可乐供应商', 'mock', NOW() - INTERVAL 3 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S001'
JOIN wms_warehouse wh ON wh.code='WH-SH'
WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO wms_purchase_order_item(purchase_order_id, sku_id, qty, cost_cent)
SELECT po.id, sk.id, 200, 220
FROM wms_purchase_order po JOIN wms_sku sk
WHERE po.order_no='PO-0001' AND sk.sku='SKU-CC-330';

-- 调拨单（SH 上海仓 -> 广州仓）
INSERT INTO wms_transfer_order(order_no, platform_id, store_id, from_warehouse_id, to_warehouse_id, status, created_by, created_at)
SELECT 'TO-0001', p.id, st.id, whFrom.id, whTo.id, 'CREATED', 'mock', NOW() - INTERVAL 2 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S001'
JOIN wms_warehouse whFrom ON whFrom.code='WH-SH'
JOIN wms_warehouse whTo ON whTo.code='WH-GZ'
WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO wms_transfer_order_item(transfer_order_id, sku_id, qty)
SELECT t.id, sk.id, 50
FROM wms_transfer_order t JOIN wms_sku sk
WHERE t.order_no='TO-0001' AND sk.sku='SKU-CC-330';

-- 送仓记录（示例：采购单送仓）
INSERT INTO wms_delivery_to_warehouse(platform_id, store_id, warehouse_id, ref_type, ref_no, status, created_at)
SELECT p.id, st.id, wh.id, 'PURCHASE', 'PO-0001', 'IN_TRANSIT', NOW() - INTERVAL 2 DAY
FROM wms_platform p
JOIN wms_store st ON st.code='PDD-S001'
JOIN wms_warehouse wh ON wh.code='WH-SH'
WHERE p.code='PDD';

-- 调价记录（给调价历史用）
INSERT INTO wms_price_change(sku_id, old_price_cent, new_price_cent, operator, reason, created_at)
SELECT sk.id, 350, 360, 'mock', '价格上调', NOW() - INTERVAL 2 DAY
FROM wms_sku sk WHERE sk.sku='SKU-CC-330';

-- 成本变更记录（用于利润率计算演示）
INSERT INTO wms_cost_change(sku_id, old_cost_cent, new_cost_cent, operator, reason, created_at)
SELECT sk.id, 220, 235, 'mock', '成本上调', NOW() - INTERVAL 1 DAY
FROM wms_sku sk WHERE sk.sku='SKU-CC-330';

-- 制造“调价待办”：库存有，但长期未动销（用库存 updated_at 作为库龄口径）
UPDATE wms_inventory inv
JOIN wms_store st ON st.id = inv.store_id
JOIN wms_sku sk ON sk.id = inv.sku_id
SET inv.updated_at = NOW() - INTERVAL 60 DAY
WHERE st.code='PDD-S002' AND sk.sku='SKU-QF-TP' AND inv.qty > 0;

-- 新品上架（调研候选品）
INSERT INTO wms_new_product_research(platform_id, candidate_code, name, category, expected_cost_cent, expected_price_cent, remark, status, created_by, created_at)
SELECT p.id, 'NP-0001', '低糖气泡水 330ml', '饮料', 180, 399, '竞品销量好，考虑上架 PDD/TB', 'NEW', 'mock', NOW() - INTERVAL 3 DAY
FROM wms_platform p WHERE p.code='PDD'
ON DUPLICATE KEY UPDATE name=VALUES(name), expected_cost_cent=VALUES(expected_cost_cent), expected_price_cent=VALUES(expected_price_cent), status=VALUES(status);

INSERT INTO wms_new_product_research(platform_id, candidate_code, name, category, expected_cost_cent, expected_price_cent, remark, status, created_by, created_at)
SELECT p.id, 'NP-0002', '山姆同款坚果礼盒', '零食', 8800, 12900, '季节性，关注投放成本', 'APPROVED', 'mock', NOW() - INTERVAL 10 DAY
FROM wms_platform p WHERE p.code='TB'
ON DUPLICATE KEY UPDATE name=VALUES(name), expected_cost_cent=VALUES(expected_cost_cent), expected_price_cent=VALUES(expected_price_cent), status=VALUES(status);
