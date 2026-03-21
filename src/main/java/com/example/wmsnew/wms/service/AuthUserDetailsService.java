package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.AuthUserDetailsService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final NamedParameterJdbcTemplate jdbc;

    public AuthUserDetailsService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MapSqlParameterSource p = new MapSqlParameterSource("username", username);
        List<AuthUserRow> rows = jdbc.query("""
                SELECT id, username, password_hash, display_name
                FROM wms_user
                WHERE username = :username AND status='ACTIVE'
                """, p, (rs, rowNum) -> new AuthUserRow(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("display_name")
        ));

        if (rows.isEmpty()) {
            throw new UsernameNotFoundException("未找到用户: " + username);
        }

        AuthUserRow u = rows.get(0);
        List<String> authorities = jdbc.query("""
                SELECT DISTINCT p.code
                FROM wms_user u
                JOIN wms_user_role ur ON ur.user_id = u.id
                JOIN wms_role_permission rp ON rp.role_id = ur.role_id
                JOIN wms_permission p ON p.id = rp.permission_id
                WHERE u.username = :username
                """, p, (rs, rowNum) -> rs.getString("code"));

        List<SimpleGrantedAuthority> auth = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        // UserDetails 的用户名用于 SecurityContext 获取；密码由 PasswordEncoder 检验
        return User.withUsername(u.username())
                .password(u.passwordHash())
                .authorities(auth)
                .build();
    }

    private record AuthUserRow(Long id, String username, String passwordHash, String displayName) {}
}

