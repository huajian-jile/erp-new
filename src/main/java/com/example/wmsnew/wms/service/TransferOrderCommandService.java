package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.TransferOrderCommandService;
import com.example.wmsnew.wms.service.WarehouseStockService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransferOrderCommandService {
    private final NamedParameterJdbcTemplate jdbc;
    private final WarehouseStockService warehouseStockService;

    public TransferOrderCommandService(NamedParameterJdbcTemplate jdbc, WarehouseStockService warehouseStockService) {
        this.jdbc = jdbc;
        this.warehouseStockService = warehouseStockService;
    }

    /**
     * 发出：from 仓可用减少、在途增加；to 仓在途增加（总数不变）。
     */
    @Transactional
    public void ship(Long id) {
        TransferHead h = head(id);
        int n = jdbc.update("""
                UPDATE wms_transfer_order
                SET status='SHIPPED'
                WHERE id=:id AND status='CREATED'
                """, new MapSqlParameterSource("id", id));
        if (n == 0) throw new IllegalArgumentException("调拨单状态不可发出（需要 CREATED）");

        for (Item it : items(id)) {
            // from: available -qty, in_transit +qty, total 0
            warehouseStockService.adjust(h.fromWarehouseId(), it.skuId(), -it.qty(), it.qty(), 0);
            // to: available 0, in_transit +qty, total 0
            warehouseStockService.adjust(h.toWarehouseId(), it.skuId(), 0, it.qty(), 0);
        }
    }

    /**
     * 收货：to 仓在途减少、可用增加；总数不变（只是状态转换）。
     */
    @Transactional
    public void receive(Long id) {
        TransferHead h = head(id);
        int n = jdbc.update("""
                UPDATE wms_transfer_order
                SET status='RECEIVED'
                WHERE id=:id AND status='SHIPPED'
                """, new MapSqlParameterSource("id", id));
        if (n == 0) throw new IllegalArgumentException("调拨单状态不可收货（需要 SHIPPED）");

        for (Item it : items(id)) {
            // to: in_transit -qty, available +qty
            warehouseStockService.adjust(h.toWarehouseId(), it.skuId(), it.qty(), -it.qty(), 0);
        }
    }

    private TransferHead head(Long id) {
        TransferHead h = jdbc.query("""
                        SELECT from_warehouse_id, to_warehouse_id
                        FROM wms_transfer_order
                        WHERE id = :id
                        """,
                new MapSqlParameterSource("id", id),
                rs -> rs.next() ? new TransferHead(rs.getLong("from_warehouse_id"), rs.getLong("to_warehouse_id")) : null);
        if (h == null) throw new IllegalArgumentException("未找到调拨单 id=" + id);
        return h;
    }

    private List<Item> items(Long id) {
        return jdbc.query("""
                SELECT sku_id, qty
                FROM wms_transfer_order_item
                WHERE transfer_order_id = :id
                """, new MapSqlParameterSource("id", id), (rs, rowNum) ->
                new Item(rs.getLong("sku_id"), rs.getInt("qty")));
    }

    private record TransferHead(Long fromWarehouseId, Long toWarehouseId) {}
    private record Item(Long skuId, Integer qty) {}
}

