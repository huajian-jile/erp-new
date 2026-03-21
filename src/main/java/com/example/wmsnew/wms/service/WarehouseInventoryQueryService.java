package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.WarehouseInventoryQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WarehouseInventoryQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public WarehouseInventoryQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<WarehouseInventoryRow> snapshot(Long warehouseId) {
        String sql = """
                SELECT warehouse_id, warehouse_code, warehouse_name, sku_id, sku, title,
                       available_qty, in_transit_qty, total_qty,
                       sale_price_cent, cost_cent, profit_per_unit_cent, profit_rate,
                       data_refreshed_at, updated_at
                FROM v_wms_wh_inventory_snapshot
                WHERE warehouse_id = :wid
                ORDER BY sku_id ASC
                """;
        return jdbc.query(sql, new MapSqlParameterSource("wid", warehouseId), (rs, rowNum) ->
                new WarehouseInventoryRow(
                        rs.getLong("warehouse_id"),
                        rs.getString("warehouse_code"),
                        rs.getString("warehouse_name"),
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getInt("available_qty"),
                        rs.getInt("in_transit_qty"),
                        rs.getInt("total_qty"),
                        rs.getLong("sale_price_cent"),
                        rs.getLong("cost_cent"),
                        rs.getLong("profit_per_unit_cent"),
                        rs.getBigDecimal("profit_rate"),
                        rs.getObject("data_refreshed_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class)
                ));
    }

    public record WarehouseInventoryRow(
            Long warehouseId,
            String warehouseCode,
            String warehouseName,
            Long skuId,
            String sku,
            String title,
            Integer availableQty,
            Integer inTransitQty,
            Integer totalQty,
            Long salePriceCent,
            Long costCent,
            Long profitPerUnitCent,
            java.math.BigDecimal profitRate,
            LocalDateTime dataRefreshedAt,
            LocalDateTime updatedAt
    ) {}
}

