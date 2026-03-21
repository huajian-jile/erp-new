package com.example.wmsnew.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("wms_new_product_research")
public class NewProductResearch {
    @Id
    private Long id;

    private Long platformId;
    private String candidateCode;
    private String name;
    private String category;
    private Long expectedCostCent;
    private Long expectedPriceCent;
    private String remark;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NewProductResearch() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlatformId() { return platformId; }
    public void setPlatformId(Long platformId) { this.platformId = platformId; }
    public String getCandidateCode() { return candidateCode; }
    public void setCandidateCode(String candidateCode) { this.candidateCode = candidateCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Long getExpectedCostCent() { return expectedCostCent; }
    public void setExpectedCostCent(Long expectedCostCent) { this.expectedCostCent = expectedCostCent; }
    public Long getExpectedPriceCent() { return expectedPriceCent; }
    public void setExpectedPriceCent(Long expectedPriceCent) { this.expectedPriceCent = expectedPriceCent; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
