package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.PurchaseOrderCommandService;
import com.example.wmsnew.wms.service.WarehouseStockService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseOrderCommandService {
    private final NamedParameterJdbcTemplate jdbc;
    private final WarehouseStockService warehouseStockService;

    public PurchaseOrderCommandService(NamedParameterJdbcTemplate jdbc, WarehouseStockService warehouseStockService) {
        this.jdbc = jdbc;
        this.warehouseStockService = warehouseStockService;
    }

    @Transactional
    public void approve(Long id) {
        transition(id, "CREATED", "APPROVED");
    }

    @Transactional
    public void ship(Long id) {
        transition(id, "APPROVED", "SHIPPED");
    }

    /**
     * 收货：将采购单明细数量入库到仓库 available & total。
     */
    @Transactional
    public void receive(Long id) {
        Long warehouseId = jdbc.query("""
                        SELECT warehouse_id FROM wms_purchase_order WHERE id = :id
                        """,
                new MapSqlParameterSource("id", id),
                rs -> rs.next() ? rs.getLong("warehouse_id") : null);
        if (warehouseId == null) throw new IllegalArgumentException("未找到采购单 id=" + id);

        int n = jdbc.update("""
                UPDATE wms_purchase_order
                SET status = 'RECEIVED'
                WHERE id = :id AND status = 'SHIPPED'
                """, new MapSqlParameterSource("id", id));
        if (n == 0) throw new IllegalArgumentException("采购单状态不可收货（需要 SHIPPED）");

        List<Item> items = jdbc.query("""
                SELECT sku_id, qty
                FROM wms_purchase_order_item
                WHERE purchase_order_id = :id
                """, new MapSqlParameterSource("id", id), (rs, rowNum) ->
                new Item(rs.getLong("sku_id"), rs.getInt("qty")));

        for (Item it : items) {
            warehouseStockService.adjust(warehouseId, it.skuId(), it.qty(), 0, it.qty());
        }
    }

    private void transition(Long id, String from, String to) {
        int n = jdbc.update("""
                UPDATE wms_purchase_order
                SET status = :to
                WHERE id = :id AND status = :from
                """, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("from", from)
                .addValue("to", to));
        if (n == 0) {
            throw new IllegalArgumentException("采购单状态不可流转：需要 " + from);
        }
    }

    private record Item(Long skuId, Integer qty) {}
}

