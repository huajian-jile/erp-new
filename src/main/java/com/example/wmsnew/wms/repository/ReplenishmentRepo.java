package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.entity.Replenishment;
import com.example.wmsnew.wms.repository.ReplenishmentRepo;

import org.springframework.data.repository.CrudRepository;

public interface ReplenishmentRepo extends CrudRepository<Replenishment, Long> {}

