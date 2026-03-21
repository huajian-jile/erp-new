package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.entity.Store;
import com.example.wmsnew.wms.repository.StoreRepo;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StoreRepo extends CrudRepository<Store, Long> {
    Optional<Store> findByCode(String code);
    Iterable<Store> findByPlatformId(Long platformId);
}

