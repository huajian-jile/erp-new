package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.entity.Sku;
import com.example.wmsnew.wms.repository.SkuRepository;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SkuRepository extends CrudRepository<Sku, Long> {
    Optional<Sku> findBySku(String sku);
}
