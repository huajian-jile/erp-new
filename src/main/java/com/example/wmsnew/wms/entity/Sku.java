package com.example.wmsnew.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("wms_sku")
public class Sku {
    @Id
    private Long id;

    private Long productId;
    private String sku;
    private String title;
    private Long priceCent;
    private Long costCent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Sku() {}

    public Sku(Long id, Long productId, String sku, String title, Long priceCent, Long costCent) {
        this.id = id;
        this.productId = productId;
        this.sku = sku;
        this.title = title;
        this.priceCent = priceCent;
        this.costCent = costCent;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getPriceCent() { return priceCent; }
    public void setPriceCent(Long priceCent) { this.priceCent = priceCent; }
    public Long getCostCent() { return costCent; }
    public void setCostCent(Long costCent) { this.costCent = costCent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
