package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.entity.StoreApiKey;
import com.example.wmsnew.wms.repository.StoreApiKeyRepo;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StoreApiKeyRepo extends CrudRepository<StoreApiKey, Long> {
    Optional<StoreApiKey> findByKeyId(String keyId);
}

