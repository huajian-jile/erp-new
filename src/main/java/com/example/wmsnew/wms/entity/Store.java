package com.example.wmsnew.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("wms_store")
public class Store {
    @Id
    private Long id;

    private Long platformId;
    private String code;
    private String name;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Store() {}

    public Store(Long id, Long platformId, String code, String name) {
        this.id = id;
        this.platformId = platformId;
        this.code = code;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlatformId() { return platformId; }
    public void setPlatformId(Long platformId) { this.platformId = platformId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
