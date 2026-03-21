package com.example.wmsnew.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("wms_cost_change")
public class CostChange {
    @Id
    private Long id;

    private Long skuId;
    private Long oldCostCent;
    private Long newCostCent;
    private String operator;
    private String reason;
    private LocalDateTime createdAt;

    public CostChange() {}

    public CostChange(Long id, Long skuId, Long oldCostCent, Long newCostCent, String operator, String reason) {
        this.id = id;
        this.skuId = skuId;
        this.oldCostCent = oldCostCent;
        this.newCostCent = newCostCent;
        this.operator = operator;
        this.reason = reason;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSkuId() { return skuId; }
    public void setSkuId(Long skuId) { this.skuId = skuId; }
    public Long getOldCostCent() { return oldCostCent; }
    public void setOldCostCent(Long oldCostCent) { this.oldCostCent = oldCostCent; }
    public Long getNewCostCent() { return newCostCent; }
    public void setNewCostCent(Long newCostCent) { this.newCostCent = newCostCent; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
