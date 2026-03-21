package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.PurchaseOrderQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseOrderQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public PurchaseOrderQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<PurchaseOrderRow> list(Long platformId, Long storeId, int limit) {
        String sql = """
                SELECT po.id, po.order_no, po.status, po.supplier_name, po.created_by, po.created_at, po.updated_at,
                       po.platform_id, po.store_id, po.warehouse_id,
                       wh.code AS warehouse_code, wh.name AS warehouse_name
                FROM wms_purchase_order po
                JOIN wms_warehouse wh ON wh.id = po.warehouse_id
                WHERE po.platform_id = :pid AND po.store_id = :sid
                ORDER BY po.id DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", storeId)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new PurchaseOrderRow(
                        rs.getLong("id"),
                        rs.getString("order_no"),
                        rs.getString("status"),
                        rs.getString("supplier_name"),
                        rs.getString("created_by"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class),
                        rs.getLong("warehouse_id"),
                        rs.getString("warehouse_code"),
                        rs.getString("warehouse_name")
                ));
    }

    public PurchaseOrderDetail detail(Long id) {
        String headSql = """
                SELECT po.id, po.order_no, po.status, po.supplier_name, po.created_by, po.created_at, po.updated_at,
                       po.platform_id, po.store_id, po.warehouse_id,
                       wh.code AS warehouse_code, wh.name AS warehouse_name
                FROM wms_purchase_order po
                JOIN wms_warehouse wh ON wh.id = po.warehouse_id
                WHERE po.id = :id
                """;
        PurchaseOrderRow head = jdbc.query(headSql, new MapSqlParameterSource("id", id), rs -> {
            if (!rs.next()) return null;
            return new PurchaseOrderRow(
                    rs.getLong("id"),
                    rs.getString("order_no"),
                    rs.getString("status"),
                    rs.getString("supplier_name"),
                    rs.getString("created_by"),
                    rs.getObject("created_at", LocalDateTime.class),
                    rs.getObject("updated_at", LocalDateTime.class),
                    rs.getLong("warehouse_id"),
                    rs.getString("warehouse_code"),
                    rs.getString("warehouse_name")
            );
        });
        if (head == null) throw new IllegalArgumentException("未找到采购单 id=" + id);

        String itemSql = """
                SELECT i.id, i.sku_id, sku.sku, sku.title, i.qty, i.cost_cent
                FROM wms_purchase_order_item i
                JOIN wms_sku sku ON sku.id = i.sku_id
                WHERE i.purchase_order_id = :id
                ORDER BY i.id ASC
                """;
        List<PurchaseOrderItemRow> items = jdbc.query(itemSql, new MapSqlParameterSource("id", id), (rs, rowNum) ->
                new PurchaseOrderItemRow(
                        rs.getLong("id"),
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getInt("qty"),
                        rs.getLong("cost_cent")
                ));
        return new PurchaseOrderDetail(head, items);
    }

    public record PurchaseOrderRow(
            Long id,
            String orderNo,
            String status,
            String supplierName,
            String createdBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long warehouseId,
            String warehouseCode,
            String warehouseName
    ) {}

    public record PurchaseOrderItemRow(
            Long id,
            Long skuId,
            String sku,
            String title,
            Integer qty,
            Long costCent
    ) {}

    public record PurchaseOrderDetail(PurchaseOrderRow head, List<PurchaseOrderItemRow> items) {}
}

