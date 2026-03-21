package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.SalesOrderQueryService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalesOrderQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public SalesOrderQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<SalesOrderRow> list(Long platformId, Long storeId, LocalDate from, LocalDate to, String status, int limit) {
        String sql = """
                SELECT so.id, so.order_no, so.status, so.created_at, so.paid_at, so.shipped_at, so.delivered_at,
                       so.platform_id, so.store_id, st.code AS store_code,
                       so.warehouse_id, wh.code AS warehouse_code,
                       COALESCE(SUM(soi.qty),0) AS item_qty,
                       COALESCE(SUM(soi.qty * soi.sale_price_cent),0) AS amount_cent
                FROM wms_sales_order so
                JOIN wms_store st ON st.id = so.store_id
                JOIN wms_warehouse wh ON wh.id = so.warehouse_id
                LEFT JOIN wms_sales_order_item soi ON soi.sales_order_id = so.id
                WHERE so.platform_id = :pid
                  AND so.store_id = :sid
                  AND DATE(so.created_at) BETWEEN :fromDay AND :toDay
                  AND (:status IS NULL OR so.status = :status)
                GROUP BY so.id, so.order_no, so.status, so.created_at, so.paid_at, so.shipped_at, so.delivered_at,
                         so.platform_id, so.store_id, st.code, so.warehouse_id, wh.code
                ORDER BY so.id DESC
                LIMIT :lim
                """;
        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("pid", platformId)
                .addValue("sid", storeId)
                .addValue("fromDay", from)
                .addValue("toDay", to)
                .addValue("status", (status == null || status.isBlank()) ? null : status)
                .addValue("lim", limit);
        return jdbc.query(sql, p, (rs, rowNum) ->
                new SalesOrderRow(
                        rs.getLong("id"),
                        rs.getString("order_no"),
                        rs.getString("status"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("paid_at", LocalDateTime.class),
                        rs.getObject("shipped_at", LocalDateTime.class),
                        rs.getObject("delivered_at", LocalDateTime.class),
                        rs.getLong("platform_id"),
                        rs.getLong("store_id"),
                        rs.getString("store_code"),
                        rs.getLong("warehouse_id"),
                        rs.getString("warehouse_code"),
                        rs.getInt("item_qty"),
                        rs.getLong("amount_cent")
                ));
    }

    public SalesOrderDetail detail(Long id) {
        String headSql = """
                SELECT so.id, so.order_no, so.status, so.created_at, so.paid_at, so.shipped_at, so.delivered_at,
                       so.platform_id, so.store_id, st.code AS store_code,
                       so.warehouse_id, wh.code AS warehouse_code
                FROM wms_sales_order so
                JOIN wms_store st ON st.id = so.store_id
                JOIN wms_warehouse wh ON wh.id = so.warehouse_id
                WHERE so.id = :id
                """;
        SalesOrderRow head = jdbc.query(headSql, new MapSqlParameterSource("id", id), rs -> {
            if (!rs.next()) return null;
            return new SalesOrderRow(
                    rs.getLong("id"),
                    rs.getString("order_no"),
                    rs.getString("status"),
                    rs.getObject("created_at", LocalDateTime.class),
                    rs.getObject("paid_at", LocalDateTime.class),
                    rs.getObject("shipped_at", LocalDateTime.class),
                    rs.getObject("delivered_at", LocalDateTime.class),
                    rs.getLong("platform_id"),
                    rs.getLong("store_id"),
                    rs.getString("store_code"),
                    rs.getLong("warehouse_id"),
                    rs.getString("warehouse_code"),
                    0,
                    0L
            );
        });
        if (head == null) throw new IllegalArgumentException("未找到订单 id=" + id);

        String itemsSql = """
                SELECT i.id, i.sku_id, sku.sku, sku.title, i.qty, i.sale_price_cent, i.cost_cent,
                       (i.sale_price_cent - i.cost_cent) AS profit_per_unit_cent
                FROM wms_sales_order_item i
                JOIN wms_sku sku ON sku.id = i.sku_id
                WHERE i.sales_order_id = :id
                ORDER BY i.id ASC
                """;
        List<SalesOrderItemRow> items = jdbc.query(itemsSql, new MapSqlParameterSource("id", id), (rs, rowNum) ->
                new SalesOrderItemRow(
                        rs.getLong("id"),
                        rs.getLong("sku_id"),
                        rs.getString("sku"),
                        rs.getString("title"),
                        rs.getInt("qty"),
                        rs.getLong("sale_price_cent"),
                        rs.getLong("cost_cent"),
                        rs.getLong("profit_per_unit_cent")
                ));
        return new SalesOrderDetail(head, items);
    }

    public record SalesOrderRow(
            Long id,
            String orderNo,
            String status,
            LocalDateTime createdAt,
            LocalDateTime paidAt,
            LocalDateTime shippedAt,
            LocalDateTime deliveredAt,
            Long platformId,
            Long storeId,
            String storeCode,
            Long warehouseId,
            String warehouseCode,
            Integer itemQty,
            Long amountCent
    ) {}

    public record SalesOrderItemRow(
            Long id,
            Long skuId,
            String sku,
            String title,
            Integer qty,
            Long salePriceCent,
            Long costCent,
            Long profitPerUnitCent
    ) {}

    public record SalesOrderDetail(SalesOrderRow head, List<SalesOrderItemRow> items) {}
}

