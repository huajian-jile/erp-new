package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.PricingQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PricingQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public PricingQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<StoreSkuProfitRow> storeSkuProfit(Long platformId, Long storeId, int limit) {
        String sql = """
                SELECT platform_id, store_id, store_code, store_name,
                       product_code, product_name, sales_status,
                       sku_id, sku, title,
                       price_cent, cost_cent, profit_cent, profit_margin_pct,
                       store_stock_qty, stock_profit_cent, stock_updated_at, stock_age_days
                FROM v_wms_store_sku_profit
                WHERE platform_id = :pid AND store_id = :sid
                ORDER BY stock_profit_cent DESC, store_stock_qty DESC, sku_id DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", storeId)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) -> new StoreSkuProfitRow(
                rs.getLong("platform_id"),
                rs.getLong("store_id"),
                rs.getString("store_code"),
                rs.getString("store_name"),
                rs.getString("product_code"),
                rs.getString("product_name"),
                rs.getString("sales_status"),
                rs.getLong("sku_id"),
                rs.getString("sku"),
                rs.getString("title"),
                rs.getLong("price_cent"),
                rs.getLong("cost_cent"),
                rs.getLong("profit_cent"),
                rs.getBigDecimal("profit_margin_pct"),
                rs.getInt("store_stock_qty"),
                rs.getLong("stock_profit_cent"),
                rs.getObject("stock_updated_at", LocalDateTime.class),
                rs.getInt("stock_age_days")
        ));
    }

    public List<PriceChangeTodoRow> priceChangeTodos(Long platformId, Long storeId, int minDays, int limit) {
        String sql = """
                SELECT platform_id, store_id, store_code, store_name,
                       sku_id, sku, title,
                       store_stock_qty, stock_updated_at, stock_age_days, qty_7d,
                       price_cent, cost_cent, profit_cent, profit_margin_pct,
                       suggested_price_cent
                FROM v_wms_price_change_todo
                WHERE platform_id = :pid AND store_id = :sid
                  AND stock_age_days >= :minDays
                ORDER BY stock_age_days DESC, store_stock_qty DESC, sku_id DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", storeId)
                .addValue("minDays", minDays)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) -> new PriceChangeTodoRow(
                rs.getLong("platform_id"),
                rs.getLong("store_id"),
                rs.getString("store_code"),
                rs.getString("store_name"),
                rs.getLong("sku_id"),
                rs.getString("sku"),
                rs.getString("title"),
                rs.getInt("store_stock_qty"),
                rs.getObject("stock_updated_at", LocalDateTime.class),
                rs.getInt("stock_age_days"),
                rs.getInt("qty_7d"),
                rs.getLong("price_cent"),
                rs.getLong("cost_cent"),
                rs.getLong("profit_cent"),
                rs.getBigDecimal("profit_margin_pct"),
                rs.getLong("suggested_price_cent")
        ));
    }

    public List<PriceChangeRow> priceChanges(Long skuId, int limit) {
        String sql = """
                SELECT pc.id, pc.sku_id, sku.sku, sku.title,
                       pc.old_price_cent, pc.new_price_cent, pc.operator, pc.reason, pc.created_at
                FROM wms_price_change pc
                JOIN wms_sku sku ON sku.id = pc.sku_id
                WHERE (:skuId IS NULL OR pc.sku_id = :skuId)
                ORDER BY pc.id DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("skuId", skuId)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) -> new PriceChangeRow(
                rs.getLong("id"),
                rs.getLong("sku_id"),
                rs.getString("sku"),
                rs.getString("title"),
                rs.getLong("old_price_cent"),
                rs.getLong("new_price_cent"),
                rs.getString("operator"),
                rs.getString("reason"),
                rs.getObject("created_at", LocalDateTime.class)
        ));
    }

    public List<CostChangeRow> costChanges(Long skuId, int limit) {
        String sql = """
                SELECT cc.id, cc.sku_id, sku.sku, sku.title,
                       cc.old_cost_cent, cc.new_cost_cent, cc.operator, cc.reason, cc.created_at
                FROM wms_cost_change cc
                JOIN wms_sku sku ON sku.id = cc.sku_id
                WHERE (:skuId IS NULL OR cc.sku_id = :skuId)
                ORDER BY cc.id DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("skuId", skuId)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) -> new CostChangeRow(
                rs.getLong("id"),
                rs.getLong("sku_id"),
                rs.getString("sku"),
                rs.getString("title"),
                rs.getLong("old_cost_cent"),
                rs.getLong("new_cost_cent"),
                rs.getString("operator"),
                rs.getString("reason"),
                rs.getObject("created_at", LocalDateTime.class)
        ));
    }

    public ProfitCalcResult calc(long priceCent, long costCent) {
        long profit = priceCent - costCent;
        double marginPct = (priceCent == 0) ? 0.0 : (profit * 100.0 / priceCent);
        return new ProfitCalcResult(priceCent, costCent, profit, Math.round(marginPct * 100.0) / 100.0);
    }

    public record StoreSkuProfitRow(
            Long platformId,
            Long storeId,
            String storeCode,
            String storeName,
            String productCode,
            String productName,
            String salesStatus,
            Long skuId,
            String sku,
            String title,
            Long priceCent,
            Long costCent,
            Long profitCent,
            java.math.BigDecimal profitMarginPct,
            Integer storeStockQty,
            Long stockProfitCent,
            LocalDateTime stockUpdatedAt,
            Integer stockAgeDays
    ) {}

    public record PriceChangeTodoRow(
            Long platformId,
            Long storeId,
            String storeCode,
            String storeName,
            Long skuId,
            String sku,
            String title,
            Integer storeStockQty,
            LocalDateTime stockUpdatedAt,
            Integer stockAgeDays,
            Integer qty7d,
            Long priceCent,
            Long costCent,
            Long profitCent,
            java.math.BigDecimal profitMarginPct,
            Long suggestedPriceCent
    ) {}

    public record PriceChangeRow(
            Long id,
            Long skuId,
            String sku,
            String title,
            Long oldPriceCent,
            Long newPriceCent,
            String operator,
            String reason,
            LocalDateTime createdAt
    ) {}

    public record CostChangeRow(
            Long id,
            Long skuId,
            String sku,
            String title,
            Long oldCostCent,
            Long newCostCent,
            String operator,
            String reason,
            LocalDateTime createdAt
    ) {}

    public record ProfitCalcResult(
            Long priceCent,
            Long costCent,
            Long profitCent,
            Double profitMarginPct
    ) {}
}

