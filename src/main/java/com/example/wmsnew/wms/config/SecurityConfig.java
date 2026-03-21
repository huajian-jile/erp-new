package com.example.wmsnew.wms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        // 允许使用 {noop}xxx 作为演示密码（14_wms_auth.sql seed）
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/login.html", "/assets/**").permitAll()
                        // 账号权限管理（需 account-admin）
                        .requestMatchers("/api/wms/admin/accounts/**", "/api/wms/admin/roles", "/api/wms/admin/roles/**", "/api/wms/admin/directories", "/api/wms/admin/directories/**").hasAuthority("account-admin")
                        // 使用统计等管理接口（需权限 admin-usage，与左侧「使用统计」目录一致）
                        .requestMatchers("/api/wms/admin/**").hasAuthority("admin-usage")
                        // 第二层权限：基础查询（平台/店铺列表）
                        .requestMatchers(HttpMethod.GET, "/api/wms/platforms", "/api/wms/platforms/**").hasAuthority("data-read")
                        .requestMatchers(HttpMethod.GET, "/api/wms/stores", "/api/wms/stores/**").hasAuthority("data-read")
                        .requestMatchers("/api/wms/auth/allowed-views", "/api/wms/auth/me").authenticated()
                        .requestMatchers("/api/wms/auth/**").authenticated()
                        // AutoCrud 生成的 REST API（需 data-read + wms-write）
                        .requestMatchers("/crud-entities.json").hasAuthority("crud-admin")
                        .requestMatchers(HttpMethod.GET, "/api/crud/**").hasAuthority("data-read")
                        .requestMatchers(HttpMethod.POST, "/api/crud/**").hasAuthority("wms-write")
                        .requestMatchers(HttpMethod.PUT, "/api/crud/**").hasAuthority("wms-write")
                        .requestMatchers(HttpMethod.DELETE, "/api/crud/**").hasAuthority("wms-write")
                        // 第二层权限：业务写入（POST/PUT；dir-usage 等仍走上面 auth/** 仅登录即可）
                        .requestMatchers(HttpMethod.POST, "/api/wms/catalog/**").hasAuthority("wms-write")
                        .requestMatchers(HttpMethod.PUT, "/api/wms/catalog/**").hasAuthority("wms-write")
                        .requestMatchers(HttpMethod.POST, "/api/wms/inventory/**").hasAuthority("wms-write")
                        .requestMatchers(HttpMethod.POST, "/api/wms/inventory-change/**").hasAuthority("wms-write")
                        .requestMatchers(HttpMethod.POST, "/api/wms/procurement/**").hasAuthority("wms-write")
                        .requestMatchers(HttpMethod.POST, "/api/wms/transfer/**").hasAuthority("wms-write")
                        .requestMatchers(HttpMethod.POST, "/api/wms/replenishment/**").hasAuthority("wms-write")
                        .requestMatchers(HttpMethod.POST, "/api/wms/stores", "/api/wms/stores/**").hasAuthority("wms-write")
                        // 目录级权限（部分 API；通用的 platforms/stores 不做硬限制，避免下拉框被拦）
                        .requestMatchers("/api/wms/catalog/products", "/api/wms/catalog/products/**").hasAuthority("products")
                        .requestMatchers("/api/wms/catalog/skus", "/api/wms/catalog/skus/**").hasAnyAuthority("variants", "bi")
                        .requestMatchers("/api/wms/catalog/new-products", "/api/wms/catalog/new-products/**").hasAuthority("new-products")

                        .requestMatchers(
                                "/api/wms/pricing/profit-table",
                                "/api/wms/pricing/profit-table/**",
                                "/api/wms/pricing/profit/calc",
                                "/api/wms/pricing/profit/calc/**"
                        ).hasAuthority("profit-table")
                        .requestMatchers(
                                "/api/wms/pricing/price-change-todos",
                                "/api/wms/pricing/price-change-todos/**"
                        ).hasAuthority("price-todos")
                        .requestMatchers(
                                "/api/wms/pricing/price-changes",
                                "/api/wms/pricing/price-changes/**",
                                "/api/wms/pricing/cost-changes",
                                "/api/wms/pricing/cost-changes/**"
                        ).hasAuthority("price-history")
                        .requestMatchers("/api/wms/sales/orders", "/api/wms/sales/orders/**").hasAuthority("orders")

                        .requestMatchers("/api/wms/ops/**").hasAuthority("store-kpi")
                        .requestMatchers("/api/wms/inventory-change/**", "/api/wms/inventory/**", "/api/wms/procurement/**", "/api/wms/transfer/**", "/api/wms/replenishment/**").hasAuthority("inventory-change")

                        .requestMatchers("/api/wms/analytics/rank/sales-qty-30d", "/api/wms/analytics/rank/sales-qty-30d/**").hasAuthority("rank-sales")
                        .requestMatchers("/api/wms/analytics/rank/profit-30d", "/api/wms/analytics/rank/profit-30d/**").hasAuthority("rank-profit")
                        .requestMatchers("/api/wms/analytics/slow-moving", "/api/wms/analytics/slow-moving/**").hasAuthority("slow")
                        .requestMatchers("/api/wms/analytics/negative-profit/skus", "/api/wms/analytics/negative-profit/skus/**").hasAuthority("loss")

                        .requestMatchers("/api/wms/warehouses", "/api/wms/warehouses/**").hasAnyAuthority("warehouse-list", "warehouse-snapshot")
                        .requestMatchers("/api/wms/bi/**").hasAuthority("bi")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=1")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=1")
                        .permitAll()
                )
                ;

        return http.build();
    }
}

