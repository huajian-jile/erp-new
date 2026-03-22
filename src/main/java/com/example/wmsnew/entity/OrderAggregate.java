package com.example.wmsnew.entity;

import com.example.wmsnew.gen.AutoCrud;
import com.example.wmsnew.gen.OneToMany;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 聚合实体示例：主表 Order + 子表 OrderItem，一个实体文件定义多表 CRUD。
 */
@AutoCrud
@Table("wms_order_agg")
public class OrderAggregate {
    @Id
    private Long id;
    private String orderNo;
    private Long storeId;
    private LocalDateTime createdAt;

    @Transient
    @OneToMany(childTable = "wms_order_agg_item", fkColumn = "order_id")
    private List<OrderItemLine> items = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<OrderItemLine> getItems() { return items; }
    public void setItems(List<OrderItemLine> items) { this.items = items != null ? items : new ArrayList<>(); }

    @Table("wms_order_agg_item")
    public static class OrderItemLine {
        @Id
        private Long id;
        private Long orderId;
        private String sku;
        private Integer qty;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
    }
}
