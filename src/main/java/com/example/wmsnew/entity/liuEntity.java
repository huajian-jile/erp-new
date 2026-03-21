package com.example.wmsnew.entity;

import com.example.wmsnew.gen.AutoCrud;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@AutoCrud
@Table("wms_liu_entity")
public class liuEntity {
    @Id
    private Long id;
    private String name;
    private Integer status;
    private String accuount;
    private String paih;
    private LocalDateTime updateAt;
    private LocalDateTime createdAt;
    private LocalDateTime updateOn;
    private LocalDateTime createdOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAccuount() {
        return accuount;
    }

    public void setAccuount(String accuount) {
        this.accuount = accuount;
    }

    public String getPaih() {
        return paih;
    }

    public void setPaih(String paih) {
        this.paih = paih;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateOn() {
        return updateOn;
    }

    public void setUpdateOn(LocalDateTime updateOn) {
        this.updateOn = updateOn;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
