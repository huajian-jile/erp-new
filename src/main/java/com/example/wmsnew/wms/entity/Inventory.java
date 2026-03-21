package com.example.wmsnew.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("wms_inventory")
public class Inventory {
    @Id
    private Long id;

    private Long storeId;
    private Long skuId;
    private Integer qty;
    private LocalDateTime updatedAt;

    public Inventory() {}

    public Inventory(Long id, Long storeId, Long skuId, Integer qty) {
        this.id = id;
        this.storeId = storeId;
        this.skuId = skuId;
        this.qty = qty;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public Long getSkuId() { return skuId; }
    public void setSkuId(Long skuId) { this.skuId = skuId; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
