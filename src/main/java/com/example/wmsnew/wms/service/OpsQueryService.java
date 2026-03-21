package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.OpsQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OpsQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public OpsQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<StoreKpiRow> kpi30d(Long platformId, Long storeId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT platform_id, store_id, day, gmv_cent, order_cnt, refund_cent, ad_cost_cent, gross_profit_cent, gross_profit_rate
                FROM v_wms_store_kpi_30d
                WHERE platform_id = :pid
                  AND store_id = :sid
                  AND day BETWEEN :fromDay AND :toDay
                ORDER BY day ASC
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", storeId)
                .addValue("fromDay", from)
                .addValue("toDay", to);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new StoreKpiRow(
                        rs.getLong("platform_id"),
                        rs.getLong("store_id"),
                        rs.getObject("day", LocalDate.class),
                        rs.getLong("gmv_cent"),
                        rs.getInt("order_cnt"),
                        rs.getLong("refund_cent"),
                        rs.getLong("ad_cost_cent"),
                        rs.getLong("gross_profit_cent"),
                        rs.getBigDecimal("gross_profit_rate")
                ));
    }

    public List<CashFlowDailyRow> cashFlowDaily(Long platformId, Long storeId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT platform_id, store_id, day, income_cent, expense_cent, net_cent
                FROM v_wms_cash_flow_daily
                WHERE platform_id = :pid
                  AND store_id = :sid
                  AND day BETWEEN :fromDay AND :toDay
                ORDER BY day ASC
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", storeId)
                .addValue("fromDay", from)
                .addValue("toDay", to);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new CashFlowDailyRow(
                        rs.getLong("platform_id"),
                        rs.getLong("store_id"),
                        rs.getObject("day", LocalDate.class),
                        rs.getLong("income_cent"),
                        rs.getLong("expense_cent"),
                        rs.getLong("net_cent")
                ));
    }

    public List<AdSpendDailyRow> adSpendDaily(Long platformId, Long storeId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT platform_id, store_id, day, spend_cent, impressions, clicks
                FROM v_wms_ad_spend_daily
                WHERE platform_id = :pid
                  AND store_id = :sid
                  AND day BETWEEN :fromDay AND :toDay
                ORDER BY day ASC
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", storeId)
                .addValue("fromDay", from)
                .addValue("toDay", to);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new AdSpendDailyRow(
                        rs.getLong("platform_id"),
                        rs.getLong("store_id"),
                        rs.getObject("day", LocalDate.class),
                        rs.getLong("spend_cent"),
                        rs.getLong("impressions"),
                        rs.getLong("clicks")
                ));
    }

    public List<SalesFunnelDailyRow> salesFunnelDaily(LocalDate from, LocalDate to) {
        String sql = """
                SELECT day, created_cnt, paid_cnt, shipped_cnt, delivered_cnt
                FROM v_wms_sales_funnel_daily
                WHERE day BETWEEN :fromDay AND :toDay
                ORDER BY day ASC
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("fromDay", from)
                .addValue("toDay", to);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new SalesFunnelDailyRow(
                        rs.getObject("day", LocalDate.class),
                        rs.getInt("created_cnt"),
                        rs.getInt("paid_cnt"),
                        rs.getInt("shipped_cnt"),
                        rs.getInt("delivered_cnt")
                ));
    }

    public record StoreKpiRow(
            Long platformId,
            Long storeId,
            LocalDate day,
            Long gmvCent,
            Integer orderCnt,
            Long refundCent,
            Long adCostCent,
            Long grossProfitCent,
            java.math.BigDecimal grossProfitRate
    ) {}

    public record CashFlowDailyRow(
            Long platformId,
            Long storeId,
            LocalDate day,
            Long incomeCent,
            Long expenseCent,
            Long netCent
    ) {}

    public record AdSpendDailyRow(
            Long platformId,
            Long storeId,
            LocalDate day,
            Long spendCent,
            Long impressions,
            Long clicks
    ) {}

    public record SalesFunnelDailyRow(
            LocalDate day,
            Integer createdCnt,
            Integer paidCnt,
            Integer shippedCnt,
            Integer deliveredCnt
    ) {}
}

