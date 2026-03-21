package com.example.wmsnew.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("wms_store_manager")
public class StoreManager {
    @Id
    private Long id;

    private Long storeId;
    private String username;
    private String displayName;
    private LocalDateTime createdAt;

    public StoreManager() {}

    public StoreManager(Long id, Long storeId, String username, String displayName) {
        this.id = id;
        this.storeId = storeId;
        this.username = username;
        this.displayName = displayName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
