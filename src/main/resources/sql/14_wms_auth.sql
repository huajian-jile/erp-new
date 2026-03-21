-- 登录/权限/目录使用记录（开发/演示用：提供 admin/admin 与 user/user）

CREATE TABLE IF NOT EXISTS wms_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  display_name VARCHAR(128) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE/LOCKED
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_user_username (username)
);

CREATE TABLE IF NOT EXISTS wms_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL,
  name VARCHAR(128) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_role_code (code)
);

CREATE TABLE IF NOT EXISTS wms_directory (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dir_key VARCHAR(64) NOT NULL,
  dir_name VARCHAR(128) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_directory_key (dir_key)
);

CREATE TABLE IF NOT EXISTS wms_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  directory_id BIGINT NOT NULL,
  code VARCHAR(64) NOT NULL, -- 直接等于 dir_key，便于授权匹配
  name VARCHAR(128) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_wms_permission_code (code),
  KEY idx_wms_permission_directory (directory_id)
);

CREATE TABLE IF NOT EXISTS wms_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, role_id),
  KEY idx_wms_user_role_user (user_id),
  KEY idx_wms_user_role_role (role_id)
);

CREATE TABLE IF NOT EXISTS wms_role_permission (
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (role_id, permission_id),
  KEY idx_wms_role_permission_role (role_id),
  KEY idx_wms_role_permission_permission (permission_id)
);

CREATE TABLE IF NOT EXISTS wms_directory_usage (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  directory_id BIGINT NOT NULL,
  used_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_wms_dir_usage_user (user_id),
  KEY idx_wms_dir_usage_dir (directory_id),
  KEY idx_wms_dir_usage_used_at (used_at)
);

-- directory seed（按左侧菜单 data-view）
INSERT INTO wms_directory(dir_key, dir_name, sort_order) VALUES
  ('products', '商品', 10),
  ('variants', '商品变体', 20),
  ('stores', '店铺', 30),
  ('store-kpi', '店铺KPI', 40),
  ('inventory-change', '库存变动', 50),
  ('warehouse-list', '现有仓库', 60),
  ('warehouse-snapshot', '多仓库库存快照', 70),
  ('rank-sales', '销量排行榜', 80),
  ('rank-profit', '利润排行榜', 90),
  ('slow', '滞销&动销率', 100),
  ('loss', '负利润看板', 110),
  ('profit-table', '商品利润表', 120),
  ('price-todos', '调价待办', 130),
  ('price-history', '调价记录', 140),
  ('new-products', '新品上架', 150),
  ('orders', '订单管理', 160),
  ('bi', 'BI趋势', 170),
  ('sql', 'SQL脚本', 180),
  ('admin-usage', '使用统计', 999),
  ('data-read', '基础数据(平台/店铺)', 2),
  ('wms-write', '业务写入(改价/库存/单据等)', 3),
  ('account-admin', '账号权限管理', 998),
  ('crud-admin', 'CRUD数据管理', 997)
ON DUPLICATE KEY UPDATE dir_name=VALUES(dir_name), sort_order=VALUES(sort_order);

-- permission seed：permission.code = dir_key
INSERT INTO wms_permission(directory_id, code, name)
SELECT id, dir_key, dir_name FROM wms_directory
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- role seed（初级/高级运营/老板/开发 + 兼容旧 admin/user）
INSERT INTO wms_role(code, name) VALUES
  ('junior-ops', '初级运营'),
  ('senior-ops', '高级运营'),
  ('boss', '老板'),
  ('dev', '开发'),
  ('admin', '管理员'),
  ('user', '运营用户')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- user seed（演示：noop 密码，适用于 dev/demo）
INSERT INTO wms_user(username, password_hash, display_name, status)
VALUES
  ('admin', '{noop}admin', '管理员', 'ACTIVE'),
  ('user', '{noop}user', '运营用户', 'ACTIVE')
ON DUPLICATE KEY UPDATE password_hash=VALUES(password_hash), display_name=VALUES(display_name), status=VALUES(status);

-- role assignment
-- admin 拥有全部权限
INSERT INTO wms_user_role(user_id, role_id)
SELECT u.id, r.id
FROM wms_user u CROSS JOIN wms_role r
WHERE u.username='admin' AND r.code='admin'
ON DUPLICATE KEY UPDATE user_id=user_id;

-- user 拥有除 sql/admin-usage 之外的权限
INSERT INTO wms_user_role(user_id, role_id)
SELECT u.id, r.id
FROM wms_user u CROSS JOIN wms_role r
WHERE u.username='user' AND r.code='user'
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 初级运营：基础数据, 商品, 商品变体
INSERT INTO wms_role_permission(role_id, permission_id)
SELECT r.id, p.id FROM wms_role r JOIN wms_permission p ON p.code IN ('data-read','products','variants')
WHERE r.code='junior-ops'
ON DUPLICATE KEY UPDATE role_id=role_id;

-- 高级运营：基础数据, 商品, 商品变体, 店铺, 店铺KPI, 库存变动
INSERT INTO wms_role_permission(role_id, permission_id)
SELECT r.id, p.id FROM wms_role r JOIN wms_permission p ON p.code IN ('data-read','products','variants','stores','store-kpi','inventory-change')
WHERE r.code='senior-ops'
ON DUPLICATE KEY UPDATE role_id=role_id;

-- 老板：所有目录权限（不含 sql/admin-usage/account-admin 等管理类，可按需加）
INSERT INTO wms_role_permission(role_id, permission_id)
SELECT r.id, p.id FROM wms_role r JOIN wms_permission p ON p.code NOT IN ('sql','admin-usage','account-admin')
WHERE r.code='boss'
ON DUPLICATE KEY UPDATE role_id=role_id;

-- 开发：拥有所有权限
INSERT INTO wms_role_permission(role_id, permission_id)
SELECT r.id, p.id FROM wms_role r JOIN wms_permission p ON 1=1
WHERE r.code='dev'
ON DUPLICATE KEY UPDATE role_id=role_id;

-- admin 拥有全部权限（兼容）
INSERT INTO wms_role_permission(role_id, permission_id)
SELECT r.id, p.id FROM wms_role r JOIN wms_permission p ON 1=1
WHERE r.code='admin'
ON DUPLICATE KEY UPDATE role_id=role_id;

-- user 拥有除 sql/admin-usage 之外的权限（兼容）
INSERT INTO wms_role_permission(role_id, permission_id)
SELECT r.id, p.id FROM wms_role r JOIN wms_permission p ON p.code NOT IN ('sql','admin-usage','wms-write','account-admin','crud-admin')
WHERE r.code='user'
ON DUPLICATE KEY UPDATE role_id=role_id;

-- 注意：不要在这里清空 wms_directory_usage
-- 否则你启用了 spring.sql.init.mode=always 时，每次重启都会把“使用统计”清零

