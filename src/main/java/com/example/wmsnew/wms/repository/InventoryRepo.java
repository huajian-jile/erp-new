package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.entity.Inventory;
import com.example.wmsnew.wms.repository.InventoryRepo;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InventoryRepo extends CrudRepository<Inventory, Long> {
    Optional<Inventory> findByStoreIdAndSkuId(Long storeId, Long skuId);
}

