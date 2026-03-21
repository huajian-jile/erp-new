package com.example.wmsnew.wms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("wms_store_api_key")
public class StoreApiKey {
    @Id
    private Long id;

    private Long storeId;
    private String keyId;
    private String keySecretHash;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;

    public StoreApiKey() {}

    public StoreApiKey(Long id, Long storeId, String keyId, String keySecretHash, String status) {
        this.id = id;
        this.storeId = storeId;
        this.keyId = keyId;
        this.keySecretHash = keySecretHash;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
    public String getKeySecretHash() { return keySecretHash; }
    public void setKeySecretHash(String keySecretHash) { this.keySecretHash = keySecretHash; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }
}
