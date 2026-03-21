package com.example.wmsnew.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("wms_replenishment")
public class Replenishment {
    @Id
    private Long id;

    private Long platformId;
    private Long storeId;
    private Long skuId;
    private Integer planQty;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;

    public Replenishment() {}

    public Replenishment(Long id, Long platformId, Long storeId, Long skuId, Integer planQty, String status, String createdBy) {
        this.id = id;
        this.platformId = platformId;
        this.storeId = storeId;
        this.skuId = skuId;
        this.planQty = planQty;
        this.status = status;
        this.createdBy = createdBy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlatformId() { return platformId; }
    public void setPlatformId(Long platformId) { this.platformId = platformId; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public Long getSkuId() { return skuId; }
    public void setSkuId(Long skuId) { this.skuId = skuId; }
    public Integer getPlanQty() { return planQty; }
    public void setPlanQty(Integer planQty) { this.planQty = planQty; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
}
