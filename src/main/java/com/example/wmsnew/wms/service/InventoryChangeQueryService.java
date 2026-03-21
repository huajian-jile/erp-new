package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.InventoryChangeQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryChangeQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public InventoryChangeQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<PendingRow> pending(Long platformId, Long storeId, int limit) {
        String sql = """
                SELECT platform_id, store_id, store_code, sku_id, sku, title,
                       qty_7d, avg_daily_qty_7d, store_stock_qty, need_alloc_qty
                FROM v_wms_inventory_pending_allocation
                WHERE (:pid IS NULL OR platform_id = :pid)
                  AND (:sid IS NULL OR store_id = :sid)
                ORDER BY need_alloc_qty DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", storeId)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new PendingRow(
                        rs.getLong("platform_id"),
                        rs.getLong("store_id"),
                        rs.getString("store_code"),
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getLong("qty_7d"),
                        rs.getBigDecimal("avg_daily_qty_7d"),
                        rs.getInt("store_stock_qty"),
                        rs.getInt("need_alloc_qty")
                ));
    }

    public List<AllocationRow> allocations(Long platformId, Long storeId, int limit) {
        String sql = """
                SELECT a.id, a.platform_id, a.store_id, st.code AS store_code,
                       a.sku_id, sku.sku, sku.title,
                       a.alloc_qty, a.status, a.operator, a.created_at, a.updated_at
                FROM wms_inventory_allocation a
                JOIN wms_store st ON st.id = a.store_id
                JOIN wms_sku sku ON sku.id = a.sku_id
                WHERE (:pid IS NULL OR a.platform_id = :pid)
                  AND (:sid IS NULL OR a.store_id = :sid)
                ORDER BY a.id DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", storeId)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new AllocationRow(
                        rs.getLong("id"),
                        rs.getLong("platform_id"),
                        rs.getLong("store_id"),
                        rs.getString("store_code"),
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getInt("alloc_qty"),
                        rs.getString("status"),
                        rs.getString("operator"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class)
                ));
    }

    public List<SyncLogRow> syncLogs(Long storeId, int limit) {
        String sql = """
                SELECT id, allocation_id, platform_id, store_id, sku_id, alloc_qty, success,
                       created_at
                FROM wms_inventory_sync_log
                WHERE (:sid IS NULL OR store_id = :sid)
                ORDER BY id DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("sid", storeId)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new SyncLogRow(
                        rs.getLong("id"),
                        rs.getLong("allocation_id"),
                        rs.getLong("platform_id"),
                        rs.getLong("store_id"),
                        rs.getLong("sku_id"),
                        rs.getInt("alloc_qty"),
                        rs.getInt("success") == 1,
                        rs.getObject("created_at", LocalDateTime.class)
                ));
    }

    public List<ClearRecordRow> clearRecords(Long platformId, Long sourceStoreId, int limit) {
        String sql = """
                SELECT c.id,
                       c.source_store_id, s1.code AS source_store_code,
                       c.cleared_store_id, s2.code AS cleared_store_code,
                       c.sku_id, sku.sku, sku.title,
                       c.cleared_qty,
                       c.created_at
                FROM wms_inventory_clear_record c
                JOIN wms_store s1 ON s1.id = c.source_store_id
                JOIN wms_store s2 ON s2.id = c.cleared_store_id
                JOIN wms_sku sku ON sku.id = c.sku_id
                WHERE (:pid IS NULL OR c.platform_id = :pid)
                  AND (:sid IS NULL OR c.source_store_id = :sid)
                ORDER BY c.id DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", sourceStoreId)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new ClearRecordRow(
                        rs.getLong("id"),
                        rs.getLong("source_store_id"),
                        rs.getString("source_store_code"),
                        rs.getLong("cleared_store_id"),
                        rs.getString("cleared_store_code"),
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getInt("cleared_qty"),
                        rs.getObject("created_at", LocalDateTime.class)
                ));
    }

    public record PendingRow(
            Long platformId,
            Long storeId,
            String storeCode,
            Long skuId,
            String sku,
            String title,
            Long qty7d,
            java.math.BigDecimal avgDailyQty7d,
            Integer storeStockQty,
            Integer needAllocQty
    ) {}

    public record AllocationRow(
            Long id,
            Long platformId,
            Long storeId,
            String storeCode,
            Long skuId,
            String sku,
            String title,
            Integer allocQty,
            String status,
            String operator,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record SyncLogRow(
            Long id,
            Long allocationId,
            Long platformId,
            Long storeId,
            Long skuId,
            Integer allocQty,
            Boolean success,
            LocalDateTime createdAt
    ) {}

    public record ClearRecordRow(
            Long id,
            Long sourceStoreId,
            String sourceStoreCode,
            Long clearedStoreId,
            String clearedStoreCode,
            Long skuId,
            String sku,
            String title,
            Integer clearedQty,
            LocalDateTime createdAt
    ) {}
}

