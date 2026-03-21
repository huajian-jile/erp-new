package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.WarehouseStockService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarehouseStockService {
    private final NamedParameterJdbcTemplate jdbc;

    public WarehouseStockService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 调整仓库库存：available/in_transit/total 同步变化。
     * - deltaAvailable: 可用变化（可正可负）
     * - deltaInTransit: 在途变化（可正可负）
     * - deltaTotal: 总数变化（可正可负）
     */
    @Transactional
    public void adjust(Long warehouseId, Long skuId, int deltaAvailable, int deltaInTransit, int deltaTotal) {
        String upsert = """
                INSERT INTO wms_warehouse_inventory(warehouse_id, sku_id, available_qty, in_transit_qty, total_qty, refreshed_at)
                VALUES (:wid, :sid, 0, 0, 0, NOW())
                ON DUPLICATE KEY UPDATE refreshed_at = NOW()
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("wid", warehouseId)
                .addValue("sid", skuId);
        jdbc.update(upsert, p);

        String update = """
                UPDATE wms_warehouse_inventory
                SET available_qty = available_qty + :da,
                    in_transit_qty = in_transit_qty + :dt,
                    total_qty = total_qty + :dtotal,
                    refreshed_at = NOW()
                WHERE warehouse_id = :wid AND sku_id = :sid
                  AND available_qty + :da >= 0
                  AND in_transit_qty + :dt >= 0
                  AND total_qty + :dtotal >= 0
                """;
        MapSqlParameterSource p2 = new MapSqlParameterSource()
                .addValue("wid", warehouseId)
                .addValue("sid", skuId)
                .addValue("da", deltaAvailable)
                .addValue("dt", deltaInTransit)
                .addValue("dtotal", deltaTotal);
        int n = jdbc.update(update, p2);
        if (n == 0) {
            throw new IllegalArgumentException("仓库库存不足或记录不存在（wid=" + warehouseId + ", skuId=" + skuId + "）");
        }
    }
}

