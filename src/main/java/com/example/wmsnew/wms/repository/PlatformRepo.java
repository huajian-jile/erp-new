package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.entity.Platform;
import com.example.wmsnew.wms.repository.PlatformRepo;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlatformRepo extends CrudRepository<Platform, Long> {
    Optional<Platform> findByCode(String code);
}

