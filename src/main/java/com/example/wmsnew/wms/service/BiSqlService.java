package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.BiSqlService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BiSqlService {
    private final NamedParameterJdbcTemplate jdbc;

    public BiSqlService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<DailyStockDelta> dailyStockDelta(Long storeId, Long skuId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT day, delta_sum
                FROM v_wms_stock_txn_daily
                WHERE store_id = :storeId
                  AND sku_id = :skuId
                  AND day BETWEEN :fromDay AND :toDay
                ORDER BY day ASC
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("skuId", skuId)
                .addValue("fromDay", from)
                .addValue("toDay", to);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new DailyStockDelta(rs.getObject("day", LocalDate.class), rs.getInt("delta_sum"))
        );
    }

    public record DailyStockDelta(LocalDate day, Integer deltaSum) {}
}

