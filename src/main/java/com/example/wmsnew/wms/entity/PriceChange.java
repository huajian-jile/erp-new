package com.example.wmsnew.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("wms_price_change")
public class PriceChange {
    @Id
    private Long id;

    private Long skuId;
    private Long oldPriceCent;
    private Long newPriceCent;
    private String operator;
    private String reason;
    private LocalDateTime createdAt;

    public PriceChange() {}

    public PriceChange(Long id, Long skuId, Long oldPriceCent, Long newPriceCent, String operator, String reason) {
        this.id = id;
        this.skuId = skuId;
        this.oldPriceCent = oldPriceCent;
        this.newPriceCent = newPriceCent;
        this.operator = operator;
        this.reason = reason;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSkuId() { return skuId; }
    public void setSkuId(Long skuId) { this.skuId = skuId; }
    public Long getOldPriceCent() { return oldPriceCent; }
    public void setOldPriceCent(Long oldPriceCent) { this.oldPriceCent = oldPriceCent; }
    public Long getNewPriceCent() { return newPriceCent; }
    public void setNewPriceCent(Long newPriceCent) { this.newPriceCent = newPriceCent; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
