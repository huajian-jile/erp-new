package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.AuthService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthService {
    private static final Set<String> NON_MENU_PERMISSIONS = Set.of("data-read", "wms-write");

    private final NamedParameterJdbcTemplate jdbc;

    public AuthService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("未登录");
        return auth.getName();
    }

    public Long currentUserId() {
        MapSqlParameterSource p = new MapSqlParameterSource("username", currentUsername());
        return jdbc.queryForObject("SELECT id FROM wms_user WHERE username=:username", p, Long.class);
    }

    /**
     * 用户拥有的全部权限码（含 data-read / wms-write 等“非菜单”权限），按 sort_order 去重保留顺序。
     */
    public List<String> allPermissionKeysOrdered(String username) {
        MapSqlParameterSource p = new MapSqlParameterSource("username", username);
        List<String> rows = jdbc.query("""
                SELECT d.dir_key
                FROM wms_user u
                JOIN wms_user_role ur ON ur.user_id=u.id
                JOIN wms_role_permission rp ON rp.role_id=ur.role_id
                JOIN wms_permission perm ON perm.id=rp.permission_id
                JOIN wms_directory d ON d.id=perm.directory_id
                WHERE u.username=:username AND u.status='ACTIVE'
                ORDER BY d.sort_order, d.dir_key
                """, p, (rs, rowNum) -> rs.getString("dir_key"));
        return List.copyOf(new LinkedHashSet<>(rows));
    }

    /**
     * 左侧/顶部分组菜单可用的目录 key（不含 data-read、wms-write）。
     */
    public List<String> allowedViews(String username) {
        return allPermissionKeysOrdered(username).stream()
                .filter(k -> !NON_MENU_PERMISSIONS.contains(k))
                .toList();
    }

    public Me me() {
        String username = currentUsername();
        MapSqlParameterSource p = new MapSqlParameterSource("username", username);
        UserHead head = jdbc.queryForObject("""
                SELECT id, username, display_name
                FROM wms_user
                WHERE username=:username
                """, p, (rs, rowNum) -> new UserHead(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("display_name")));
        List<String> keys = allPermissionKeysOrdered(username);
        boolean canWrite = keys.contains("wms-write");
        List<String> menu = keys.stream().filter(k -> !NON_MENU_PERMISSIONS.contains(k)).toList();
        return new Me(head.id(), head.username(), head.displayName(), menu, canWrite);
    }

    public void logUsage(String directoryKey) {
        if (NON_MENU_PERMISSIONS.contains(directoryKey)) {
            throw new IllegalArgumentException("非法目录 key: " + directoryKey);
        }
        String username = currentUsername();
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("username", username)
                .addValue("dirKey", directoryKey);

        int cnt = jdbc.update("""
                INSERT INTO wms_directory_usage(user_id, directory_id, used_at)
                SELECT u.id, d.id, NOW()
                FROM wms_user u
                JOIN wms_user_role ur ON ur.user_id=u.id
                JOIN wms_role_permission rp ON rp.role_id=ur.role_id
                JOIN wms_permission perm ON perm.id=rp.permission_id
                JOIN wms_directory d ON d.id=perm.directory_id
                WHERE u.username=:username
                  AND u.status='ACTIVE'
                  AND perm.code=:dirKey
                """, p);
        if (cnt <= 0) {
            throw new IllegalArgumentException("无权限记录目录使用: " + directoryKey);
        }
    }

    public List<UsageTopDirRow> topDirectories(int limit) {
        MapSqlParameterSource p = new MapSqlParameterSource("lim", limit);
        return jdbc.query("""
                SELECT d.dir_key, d.dir_name, COUNT(*) AS usage_count
                FROM wms_directory_usage u
                JOIN wms_directory d ON d.id=u.directory_id
                GROUP BY d.id, d.dir_key, d.dir_name
                ORDER BY usage_count DESC
                LIMIT :lim
                """, p, (rs, rowNum) -> new UsageTopDirRow(
                rs.getString("dir_key"),
                rs.getString("dir_name"),
                rs.getLong("usage_count")
        ));
    }

    public List<UsageTopUserRow> topUsers(String directoryKey, int limit) {
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("dirKey", directoryKey)
                .addValue("lim", limit);

        return jdbc.query("""
                SELECT u.username, u.display_name, COUNT(*) AS usage_count
                FROM wms_directory_usage du
                JOIN wms_directory d ON d.id=du.directory_id
                JOIN wms_user u ON u.id=du.user_id
                WHERE d.dir_key=:dirKey
                GROUP BY u.id, u.username, u.display_name
                ORDER BY usage_count DESC
                LIMIT :lim
                """, p, (rs, rowNum) -> new UsageTopUserRow(
                rs.getString("username"),
                rs.getString("display_name"),
                rs.getLong("usage_count")
        ));
    }

    /**
     * 账号管理：列出所有用户及其权限、密码显示（仅 {noop} 可还原，用于演示）。
     */
    public List<AccountRow> listAccounts() {
        List<AccountBase> users = jdbc.query("""
                SELECT u.id, u.username, u.display_name, u.status, u.password_hash
                FROM wms_user u
                ORDER BY u.id
                """, (rs, rowNum) -> new AccountBase(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("display_name"),
                rs.getString("status"),
                rs.getString("password_hash")));

        return users.stream().map(u -> {
            List<String> roles = jdbc.query("""
                    SELECT r.code FROM wms_user_role ur
                    JOIN wms_role r ON r.id=ur.role_id
                    WHERE ur.user_id=:uid
                    """, new MapSqlParameterSource("uid", u.id()), (rs, rn) -> rs.getString("code"));
            List<String> views = allowedViews(u.username());
            String pwdDisplay = formatPasswordDisplay(u.passwordHash());
            return accountRowForList(u, roles, views, pwdDisplay);
        }).toList();
    }

    /** {noop}xxx 显示明文（演示用）；其他显示已加密 */
    private static String formatPasswordDisplay(String hash) {
        if (hash == null || hash.isEmpty()) return "-";
        if (hash.startsWith("{noop}")) return hash.substring(6);
        return "****";
    }

    public void resetPassword(long userId, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("新密码不能为空");
        }
        String encoded = "{noop}" + newPassword;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("uid", userId)
                .addValue("hash", encoded);
        int cnt = jdbc.update("UPDATE wms_user SET password_hash=:hash WHERE id=:uid", p);
        if (cnt <= 0) throw new IllegalArgumentException("用户不存在: " + userId);
    }

    public void createUser(String username, String password, String displayName, String status, List<Long> roleIds) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("账号不能为空");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("密码不能为空");
        if (displayName == null || displayName.isBlank()) displayName = username;
        if (status == null || status.isBlank()) status = "ACTIVE";
        String hash = "{noop}" + password;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("username", username.trim())
                .addValue("hash", hash)
                .addValue("displayName", displayName.trim())
                .addValue("status", status);
        jdbc.update("INSERT INTO wms_user(username, password_hash, display_name, status) VALUES (:username,:hash,:displayName,:status)", p);
        Long userId = jdbc.queryForObject("SELECT id FROM wms_user WHERE username=:username", new MapSqlParameterSource("username", username.trim()), Long.class);
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long rid : roleIds) {
                jdbc.update("INSERT INTO wms_user_role(user_id, role_id) VALUES (:uid,:rid)", new MapSqlParameterSource().addValue("uid", userId).addValue("rid", rid));
            }
        }
    }

    public AccountRow getAccount(long userId) {
        AccountBase u = jdbc.queryForObject("SELECT id,username,display_name,status,password_hash FROM wms_user WHERE id=:uid", new MapSqlParameterSource("uid", userId), (rs, rn) -> new AccountBase(rs.getLong("id"), rs.getString("username"), rs.getString("display_name"), rs.getString("status"), rs.getString("password_hash")));
        if (u == null) throw new IllegalArgumentException("用户不存在");
        List<String> roles = jdbc.query("SELECT r.code FROM wms_user_role ur JOIN wms_role r ON r.id=ur.role_id WHERE ur.user_id=:uid", new MapSqlParameterSource("uid", userId), (rs, rn) -> rs.getString("code"));
        List<Long> roleIds = jdbc.query("SELECT role_id FROM wms_user_role WHERE user_id=:uid", new MapSqlParameterSource("uid", userId), (rs, rn) -> rs.getLong("role_id"));
        List<String> views = allowedViews(u.username());
        return new AccountRow(u.id(), u.username(), u.displayName(), u.status(), formatPasswordDisplay(u.passwordHash()), roles, views, roleIds);
    }

    public AccountRow accountRowForList(AccountBase u, List<String> roles, List<String> views, String pwdDisplay) {
        return new AccountRow(u.id(), u.username(), u.displayName(), u.status(), pwdDisplay, roles, views, null);
    }

    public void updateUser(long userId, String displayName, String status, List<Long> roleIds) {
        MapSqlParameterSource p = new MapSqlParameterSource("uid", userId);
        if (jdbc.queryForObject("SELECT COUNT(*) FROM wms_user WHERE id=:uid", p, Long.class) == 0) throw new IllegalArgumentException("用户不存在");
        if (displayName != null) jdbc.update("UPDATE wms_user SET display_name=:dn WHERE id=:uid", new MapSqlParameterSource().addValue("uid", userId).addValue("dn", displayName.trim()));
        if (status != null) jdbc.update("UPDATE wms_user SET status=:st WHERE id=:uid", new MapSqlParameterSource().addValue("uid", userId).addValue("st", status));
        jdbc.update("DELETE FROM wms_user_role WHERE user_id=:uid", p);
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long rid : roleIds) {
                jdbc.update("INSERT INTO wms_user_role(user_id, role_id) VALUES (:uid,:rid)", new MapSqlParameterSource().addValue("uid", userId).addValue("rid", rid));
            }
        }
    }

    public void deleteUser(long userId) {
        if (userId == currentUserId()) throw new IllegalArgumentException("不能删除当前登录账号");
        jdbc.update("DELETE FROM wms_user_role WHERE user_id=:uid", new MapSqlParameterSource("uid", userId));
        int cnt = jdbc.update("DELETE FROM wms_user WHERE id=:uid", new MapSqlParameterSource("uid", userId));
        if (cnt <= 0) throw new IllegalArgumentException("用户不存在");
    }

    public List<RoleRow> listRoles() {
        return jdbc.query("SELECT id, code, name FROM wms_role ORDER BY id", (rs, rn) -> new RoleRow(rs.getLong("id"), rs.getString("code"), rs.getString("name")));
    }

    public List<DirectoryRow> listDirectories() {
        List<DirectoryBase> dirs = jdbc.query("SELECT d.id, d.dir_key, d.dir_name, d.sort_order, p.id as perm_id FROM wms_directory d JOIN wms_permission p ON p.directory_id=d.id ORDER BY d.sort_order, d.dir_key", (rs, rn) -> new DirectoryBase(rs.getLong("id"), rs.getString("dir_key"), rs.getString("dir_name"), rs.getInt("sort_order"), rs.getLong("perm_id")));
        return dirs.stream().map(d -> {
            List<String> roleCodes = jdbc.query("SELECT r.code FROM wms_role_permission rp JOIN wms_role r ON r.id=rp.role_id WHERE rp.permission_id=:pid", new MapSqlParameterSource("pid", d.permId()), (rs, rn) -> rs.getString("code"));
            return new DirectoryRow(d.id(), d.dirKey(), d.dirName(), d.sortOrder(), d.permId(), roleCodes);
        }).toList();
    }

    /** 角色及其拥有的目录（权限名称 : 目录列表），用于「权限与目录」展示 */
    public List<RoleWithDirectoriesRow> listRolesWithDirectories() {
        List<RoleRow> roles = listRoles();
        return roles.stream().map(r -> {
            List<String> dirNames = jdbc.query("""
                    SELECT d.dir_name FROM wms_role_permission rp
                    JOIN wms_permission p ON p.id=rp.permission_id
                    JOIN wms_directory d ON d.id=p.directory_id
                    WHERE rp.role_id=:rid
                    ORDER BY d.sort_order, d.dir_key
                    """, new MapSqlParameterSource("rid", r.id()), (rs, rn) -> rs.getString("dir_name"));
            List<Long> permIds = jdbc.query("SELECT permission_id FROM wms_role_permission WHERE role_id=:rid", new MapSqlParameterSource("rid", r.id()), (rs, rn) -> rs.getLong("permission_id"));
            return new RoleWithDirectoriesRow(r.id(), r.code(), r.name(), dirNames, permIds);
        }).toList();
    }

    public void updateRolePermissions(long roleId, List<Long> permissionIds) {
        jdbc.update("DELETE FROM wms_role_permission WHERE role_id=:rid", new MapSqlParameterSource("rid", roleId));
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long pid : permissionIds) {
                jdbc.update("INSERT INTO wms_role_permission(role_id, permission_id) VALUES (:rid,:pid)", new MapSqlParameterSource().addValue("rid", roleId).addValue("pid", pid));
            }
        }
    }

    public void createRole(String code, String name, List<Long> permissionIds) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("角色 code 不能为空");
        code = code.trim().toLowerCase().replaceAll("\\s+", "-");
        if (name == null || name.isBlank()) name = code;
        jdbc.update("INSERT INTO wms_role(code, name) VALUES (:code,:name)", new MapSqlParameterSource().addValue("code", code).addValue("name", name.trim()));
        Long roleId = jdbc.queryForObject("SELECT id FROM wms_role WHERE code=:code", new MapSqlParameterSource("code", code), Long.class);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long pid : permissionIds) {
                jdbc.update("INSERT INTO wms_role_permission(role_id, permission_id) VALUES (:rid,:pid)", new MapSqlParameterSource().addValue("rid", roleId).addValue("pid", pid));
            }
        }
    }

    public void updateRole(long roleId, String name, List<Long> permissionIds) {
        if (jdbc.queryForObject("SELECT COUNT(*) FROM wms_role WHERE id=:rid", new MapSqlParameterSource("rid", roleId), Long.class) == 0)
            throw new IllegalArgumentException("角色不存在");
        if (name != null) jdbc.update("UPDATE wms_role SET name=:name WHERE id=:rid", new MapSqlParameterSource().addValue("rid", roleId).addValue("name", name.trim()));
        if (permissionIds != null) updateRolePermissions(roleId, permissionIds);
    }

    public void deleteRole(long roleId) {
        jdbc.update("DELETE FROM wms_user_role WHERE role_id=:rid", new MapSqlParameterSource("rid", roleId));
        jdbc.update("DELETE FROM wms_role_permission WHERE role_id=:rid", new MapSqlParameterSource("rid", roleId));
        int cnt = jdbc.update("DELETE FROM wms_role WHERE id=:rid", new MapSqlParameterSource("rid", roleId));
        if (cnt <= 0) throw new IllegalArgumentException("角色不存在");
    }

    public void createDirectory(String dirKey, String dirName, int sortOrder, List<Long> roleIds) {
        if (dirKey == null || dirKey.isBlank()) throw new IllegalArgumentException("目录 key 不能为空");
        dirKey = dirKey.trim().toLowerCase().replaceAll("\\s+", "-");
        if (dirName == null || dirName.isBlank()) dirName = dirKey;
        MapSqlParameterSource p = new MapSqlParameterSource().addValue("key", dirKey).addValue("name", dirName).addValue("sort", sortOrder);
        jdbc.update("INSERT INTO wms_directory(dir_key, dir_name, sort_order) VALUES (:key,:name,:sort)", p);
        Long dirId = jdbc.queryForObject("SELECT id FROM wms_directory WHERE dir_key=:key", new MapSqlParameterSource("key", dirKey), Long.class);
        jdbc.update("INSERT INTO wms_permission(directory_id, code, name) VALUES (:did,:code,:name)", new MapSqlParameterSource().addValue("did", dirId).addValue("code", dirKey).addValue("name", dirName));
        Long permId = jdbc.queryForObject("SELECT id FROM wms_permission WHERE code=:key", new MapSqlParameterSource("key", dirKey), Long.class);
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long rid : roleIds) {
                jdbc.update("INSERT INTO wms_role_permission(role_id, permission_id) VALUES (:rid,:pid)", new MapSqlParameterSource().addValue("rid", rid).addValue("pid", permId));
            }
        }
    }

    public void updateDirectory(long dirId, String dirName, Integer sortOrder, List<Long> roleIds) {
        Long permId = jdbc.queryForObject("SELECT p.id FROM wms_permission p JOIN wms_directory d ON d.id=p.directory_id WHERE d.id=:did", new MapSqlParameterSource("did", dirId), Long.class);
        if (permId == null) throw new IllegalArgumentException("目录不存在");
        if (dirName != null) {
            jdbc.update("UPDATE wms_directory SET dir_name=:name WHERE id=:did", new MapSqlParameterSource().addValue("did", dirId).addValue("name", dirName.trim()));
            jdbc.update("UPDATE wms_permission SET name=:name WHERE id=:pid", new MapSqlParameterSource().addValue("pid", permId).addValue("name", dirName.trim()));
        }
        if (sortOrder != null) jdbc.update("UPDATE wms_directory SET sort_order=:sort WHERE id=:did", new MapSqlParameterSource().addValue("did", dirId).addValue("sort", sortOrder));
        if (roleIds != null) {
            jdbc.update("DELETE FROM wms_role_permission WHERE permission_id=:pid", new MapSqlParameterSource("pid", permId));
            for (Long rid : roleIds) {
                jdbc.update("INSERT INTO wms_role_permission(role_id, permission_id) VALUES (:rid,:pid)", new MapSqlParameterSource().addValue("rid", rid).addValue("pid", permId));
            }
        }
    }

    public void deleteDirectory(long dirId) {
        Long permId = jdbc.queryForObject("SELECT p.id FROM wms_permission p JOIN wms_directory d ON d.id=p.directory_id WHERE d.id=:did", new MapSqlParameterSource("did", dirId), Long.class);
        if (permId == null) throw new IllegalArgumentException("目录不存在");
        jdbc.update("DELETE FROM wms_role_permission WHERE permission_id=:pid", new MapSqlParameterSource("pid", permId));
        jdbc.update("DELETE FROM wms_permission WHERE id=:pid", new MapSqlParameterSource("pid", permId));
        int cnt = jdbc.update("DELETE FROM wms_directory WHERE id=:did", new MapSqlParameterSource("did", dirId));
        if (cnt <= 0) throw new IllegalArgumentException("目录不存在");
    }

    public record AccountRow(long id, String username, String displayName, String status,
                            String passwordDisplay, List<String> roles, List<String> allowedViews, List<Long> roleIds) {
        public AccountRow(long id, String username, String displayName, String status, String passwordDisplay, List<String> roles, List<String> allowedViews) {
            this(id, username, displayName, status, passwordDisplay, roles, allowedViews, null);
        }
    }

    public record RoleRow(long id, String code, String name) {}

    public record RoleWithDirectoriesRow(long roleId, String roleCode, String roleName, List<String> dirNames, List<Long> permissionIds) {}

    public record DirectoryRow(long id, String dirKey, String dirName, int sortOrder, long permissionId, List<String> roleCodes) {}

    private record DirectoryBase(long id, String dirKey, String dirName, int sortOrder, long permId) {}

    private record AccountBase(long id, String username, String displayName, String status, String passwordHash) {}

    public record Me(Long userId, String username, String displayName, List<String> allowedViews, boolean canWrite) {
        public Me(Long userId, String username, String displayName) {
            this(userId, username, displayName, List.of(), false);
        }
    }

    public record UsageTopDirRow(String dirKey, String dirName, Long usageCount) {}

    public record UsageTopUserRow(String username, String displayName, Long usageCount) {}

    private record UserHead(Long id, String username, String displayName) {}
}
