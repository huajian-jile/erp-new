package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.entity.Warehouse;
import com.example.wmsnew.wms.repository.WarehouseRepo;

import org.springframework.data.repository.CrudRepository;

public interface WarehouseRepo extends CrudRepository<Warehouse, Long> {}

