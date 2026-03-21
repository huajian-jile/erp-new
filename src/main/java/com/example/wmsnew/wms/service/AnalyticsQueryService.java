package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.AnalyticsQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public AnalyticsQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<SalesRankRow> salesRank30d(int limit) {
        String sql = """
                SELECT sku_id, sku, title, qty_30d
                FROM v_wms_rank_sales_qty_30d
                LIMIT :lim
                """;
        return jdbc.query(sql, new MapSqlParameterSource("lim", limit), (rs, rowNum) ->
                new SalesRankRow(
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getInt("qty_30d")
                ));
    }

    public List<ProfitRankRow> profitRank30d(int limit) {
        String sql = """
                SELECT sku_id, sku, title, profit_cent_30d, revenue_cent_30d, profit_rate_30d
                FROM v_wms_rank_profit_30d
                LIMIT :lim
                """;
        return jdbc.query(sql, new MapSqlParameterSource("lim", limit), (rs, rowNum) ->
                new ProfitRankRow(
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getLong("profit_cent_30d"),
                        rs.getLong("revenue_cent_30d"),
                        rs.getBigDecimal("profit_rate_30d")
                ));
    }

    public record SalesRankRow(Long skuId, String sku, String title, Integer qty30d) {}
    public record ProfitRankRow(Long skuId, String sku, String title, Long profitCent30d, Long revenueCent30d, java.math.BigDecimal profitRate30d) {}
}

