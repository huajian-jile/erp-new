package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.InventoryQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public InventoryQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<InventorySnapshotRow> snapshot(Long storeId) {
        String sql = """
                SELECT store_id, store_code, sku_id, sku, title, qty, price_cent, stock_value_cent, updated_at
                FROM v_wms_inventory_snapshot
                WHERE store_id = :storeId
                ORDER BY sku_id ASC
                """;
        MapSqlParameterSource p = new MapSqlParameterSource("storeId", storeId);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new InventorySnapshotRow(
                        rs.getLong("store_id"),
                        rs.getString("store_code"),
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getInt("qty"),
                        rs.getLong("price_cent"),
                        rs.getLong("stock_value_cent"),
                        rs.getObject("updated_at", LocalDateTime.class)
                ));
    }

    public record InventorySnapshotRow(
            Long storeId,
            String storeCode,
            Long skuId,
            String sku,
            String title,
            Integer qty,
            Long priceCent,
            Long stockValueCent,
            LocalDateTime updatedAt
    ) {}
}

