package com.example.wmsnew.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("wms_stock_txn")
public class StockTxn {
    @Id
    private Long id;

    private Long storeId;
    private Long skuId;
    private Integer delta;
    private String reason;
    private String refNo;
    private LocalDateTime createdAt;

    public StockTxn() {}

    public StockTxn(Long id, Long storeId, Long skuId, Integer delta, String reason, String refNo) {
        this.id = id;
        this.storeId = storeId;
        this.skuId = skuId;
        this.delta = delta;
        this.reason = reason;
        this.refNo = refNo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public Long getSkuId() { return skuId; }
    public void setSkuId(Long skuId) { this.skuId = skuId; }
    public Integer getDelta() { return delta; }
    public void setDelta(Integer delta) { this.delta = delta; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getRefNo() { return refNo; }
    public void setRefNo(String refNo) { this.refNo = refNo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
