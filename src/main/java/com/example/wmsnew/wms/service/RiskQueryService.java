package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.RiskQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public RiskQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<SlowMovingRow> slowMoving(int limit) {
        String sql = """
                SELECT sku_id, sku, title, qty_30d, stock_total_qty, stock_available_qty, stock_in_transit_qty,
                       sell_through_rate_30d, days_of_supply
                FROM v_wms_sku_slow_moving_30d
                LIMIT :lim
                """;
        return jdbc.query(sql, new MapSqlParameterSource("lim", limit), (rs, rowNum) ->
                new SlowMovingRow(
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getInt("qty_30d"),
                        rs.getInt("stock_total_qty"),
                        rs.getInt("stock_available_qty"),
                        rs.getInt("stock_in_transit_qty"),
                        rs.getBigDecimal("sell_through_rate_30d"),
                        rs.getBigDecimal("days_of_supply")
                ));
    }

    public List<NegativeProfitSkuRow> negativeProfitSkus(int limit) {
        String sql = """
                SELECT sku_id, sku, title, cost_cent, sale_price_cent, profit_per_unit_cent, profit_rate
                FROM v_wms_negative_profit_sku
                LIMIT :lim
                """;
        return jdbc.query(sql, new MapSqlParameterSource("lim", limit), (rs, rowNum) ->
                new NegativeProfitSkuRow(
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getLong("cost_cent"),
                        rs.getLong("sale_price_cent"),
                        rs.getLong("profit_per_unit_cent"),
                        rs.getBigDecimal("profit_rate")
                ));
    }

    public record SlowMovingRow(
            Long skuId,
            String sku,
            String title,
            Integer qty30d,
            Integer stockTotalQty,
            Integer stockAvailableQty,
            Integer stockInTransitQty,
            java.math.BigDecimal sellThroughRate30d,
            java.math.BigDecimal daysOfSupply
    ) {}

    public record NegativeProfitSkuRow(
            Long skuId,
            String sku,
            String title,
            Long costCent,
            Long salePriceCent,
            Long profitPerUnitCent,
            java.math.BigDecimal profitRate
    ) {}
}

