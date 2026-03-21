package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.dto.InventoryChangeRequests;
import com.example.wmsnew.wms.service.InventoryChangeCommandService;
import com.example.wmsnew.wms.service.InventoryChangeQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryChangeCommandService {
    private final NamedParameterJdbcTemplate jdbc;
    private final InventoryChangeQueryService queryService;

    public InventoryChangeCommandService(NamedParameterJdbcTemplate jdbc, InventoryChangeQueryService queryService) {
        this.jdbc = jdbc;
        this.queryService = queryService;
    }

    @Transactional
    public InventoryChangeQueryService.AllocationRow allocate(InventoryChangeRequests.Allocate req) {
        String op = (req.operator() == null || req.operator().isBlank()) ? "system" : req.operator();

        jdbc.update("""
                INSERT INTO wms_inventory_allocation(platform_id, store_id, sku_id, alloc_qty, status, operator)
                VALUES (:pid, :sid, :skuId, :qty, 'ALLOCATED', :op)
                """, new MapSqlParameterSource()
                .addValue("pid", req.platformId())
                .addValue("sid", req.storeId())
                .addValue("skuId", req.skuId())
                .addValue("qty", req.allocQty())
                .addValue("op", op));

        Long id = jdbc.query("SELECT LAST_INSERT_ID()", new MapSqlParameterSource(), rs -> rs.next() ? rs.getLong(1) : null);
        return queryService.allocations(req.platformId(), req.storeId(), 20).stream()
                .filter(a -> a.id().equals(id))
                .findFirst()
                .orElseThrow();
    }

    @Transactional
    public void sync(Long allocationId) {
        Alloc a = getAlloc(allocationId);
        jdbc.update("""
                INSERT INTO wms_inventory_sync_log(allocation_id, platform_id, store_id, sku_id, alloc_qty, success, request_payload, response_payload)
                VALUES (:aid, :pid, :sid, :skuId, :qty, 1, :req, :resp)
                """, new MapSqlParameterSource()
                .addValue("aid", allocationId)
                .addValue("pid", a.platformId())
                .addValue("sid", a.storeId())
                .addValue("skuId", a.skuId())
                .addValue("qty", a.allocQty())
                .addValue("req", "{\"action\":\"syncInventory\",\"allocationId\":" + allocationId + "}")
                .addValue("resp", "{\"success\":true}"));

        jdbc.update("""
                UPDATE wms_inventory_allocation
                SET status='SYNCED'
                WHERE id=:id
                """, new MapSqlParameterSource("id", allocationId));
    }

    /**
     * 清空同平台下“其它店铺”的该 SKU 库存（wms_inventory），并记录清空记录。
     */
    @Transactional
    public void clearOtherStores(Long allocationId) {
        Alloc a = getAlloc(allocationId);

        List<Cleared> cleared = jdbc.query("""
                SELECT inv.store_id, inv.qty
                FROM wms_inventory inv
                JOIN wms_store st ON st.id = inv.store_id
                WHERE st.platform_id = :pid
                  AND inv.sku_id = :skuId
                  AND inv.store_id <> :storeId
                  AND inv.qty > 0
                """, new MapSqlParameterSource()
                .addValue("pid", a.platformId())
                .addValue("skuId", a.skuId())
                .addValue("storeId", a.storeId()), (rs, rowNum) ->
                new Cleared(rs.getLong("store_id"), rs.getInt("qty")));

        // 清空
        jdbc.update("""
                UPDATE wms_inventory inv
                JOIN wms_store st ON st.id = inv.store_id
                SET inv.qty = 0
                WHERE st.platform_id = :pid
                  AND inv.sku_id = :skuId
                  AND inv.store_id <> :storeId
                """, new MapSqlParameterSource()
                .addValue("pid", a.platformId())
                .addValue("skuId", a.skuId())
                .addValue("storeId", a.storeId()));

        for (Cleared c : cleared) {
            jdbc.update("""
                    INSERT INTO wms_inventory_clear_record(platform_id, source_store_id, cleared_store_id, sku_id, cleared_qty, operator)
                    VALUES (:pid, :sourceSid, :clearedSid, :skuId, :qty, 'system')
                    """, new MapSqlParameterSource()
                    .addValue("pid", a.platformId())
                    .addValue("sourceSid", a.storeId())
                    .addValue("clearedSid", c.storeId())
                    .addValue("skuId", a.skuId())
                    .addValue("qty", c.qty()));
        }

        jdbc.update("""
                UPDATE wms_inventory_allocation
                SET status='CLEARED'
                WHERE id=:id
                """, new MapSqlParameterSource("id", allocationId));
    }

    private Alloc getAlloc(Long allocationId) {
        Alloc a = jdbc.query("""
                        SELECT platform_id, store_id, sku_id, alloc_qty
                        FROM wms_inventory_allocation
                        WHERE id = :id
                        """,
                new MapSqlParameterSource("id", allocationId),
                rs -> rs.next()
                        ? new Alloc(rs.getLong("platform_id"), rs.getLong("store_id"), rs.getLong("sku_id"), rs.getInt("alloc_qty"))
                        : null);
        if (a == null) throw new IllegalArgumentException("未找到分配记录 id=" + allocationId);
        return a;
    }

    private record Alloc(Long platformId, Long storeId, Long skuId, Integer allocQty) {}
    private record Cleared(Long storeId, Integer qty) {}
}

