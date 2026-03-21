package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.TransferOrderQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferOrderQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public TransferOrderQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<TransferOrderRow> list(Long platformId, Long storeId, int limit) {
        String sql = """
                SELECT t.id, t.order_no, t.status, t.created_by, t.created_at, t.updated_at,
                       t.from_warehouse_id, wf.code AS from_code, wf.name AS from_name,
                       t.to_warehouse_id, wt.code AS to_code, wt.name AS to_name
                FROM wms_transfer_order t
                JOIN wms_warehouse wf ON wf.id = t.from_warehouse_id
                JOIN wms_warehouse wt ON wt.id = t.to_warehouse_id
                WHERE t.platform_id = :pid AND t.store_id = :sid
                ORDER BY t.id DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", storeId)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new TransferOrderRow(
                        rs.getLong("id"),
                        rs.getString("order_no"),
                        rs.getString("status"),
                        rs.getString("created_by"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class),
                        rs.getLong("from_warehouse_id"),
                        rs.getString("from_code"),
                        rs.getString("from_name"),
                        rs.getLong("to_warehouse_id"),
                        rs.getString("to_code"),
                        rs.getString("to_name")
                ));
    }

    public TransferOrderDetail detail(Long id) {
        String headSql = """
                SELECT t.id, t.order_no, t.status, t.created_by, t.created_at, t.updated_at,
                       t.platform_id, t.store_id,
                       t.from_warehouse_id, wf.code AS from_code, wf.name AS from_name,
                       t.to_warehouse_id, wt.code AS to_code, wt.name AS to_name
                FROM wms_transfer_order t
                JOIN wms_warehouse wf ON wf.id = t.from_warehouse_id
                JOIN wms_warehouse wt ON wt.id = t.to_warehouse_id
                WHERE t.id = :id
                """;
        TransferOrderRow head = jdbc.query(headSql, new MapSqlParameterSource("id", id), rs -> {
            if (!rs.next()) return null;
            return new TransferOrderRow(
                    rs.getLong("id"),
                    rs.getString("order_no"),
                    rs.getString("status"),
                    rs.getString("created_by"),
                    rs.getObject("created_at", LocalDateTime.class),
                    rs.getObject("updated_at", LocalDateTime.class),
                    rs.getLong("from_warehouse_id"),
                    rs.getString("from_code"),
                    rs.getString("from_name"),
                    rs.getLong("to_warehouse_id"),
                    rs.getString("to_code"),
                    rs.getString("to_name")
            );
        });
        if (head == null) throw new IllegalArgumentException("未找到调拨单 id=" + id);

        String itemSql = """
                SELECT i.id, i.sku_id, sku.sku, sku.title, i.qty
                FROM wms_transfer_order_item i
                JOIN wms_sku sku ON sku.id = i.sku_id
                WHERE i.transfer_order_id = :id
                ORDER BY i.id ASC
                """;
        List<TransferOrderItemRow> items = jdbc.query(itemSql, new MapSqlParameterSource("id", id), (rs, rowNum) ->
                new TransferOrderItemRow(
                        rs.getLong("id"),
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getInt("qty")
                ));
        return new TransferOrderDetail(head, items);
    }

    public record TransferOrderRow(
            Long id,
            String orderNo,
            String status,
            String createdBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long fromWarehouseId,
            String fromWarehouseCode,
            String fromWarehouseName,
            Long toWarehouseId,
            String toWarehouseCode,
            String toWarehouseName
    ) {}

    public record TransferOrderItemRow(
            Long id,
            Long skuId,
            String sku,
            String title,
            Integer qty
    ) {}

    public record TransferOrderDetail(TransferOrderRow head, List<TransferOrderItemRow> items) {}
}

