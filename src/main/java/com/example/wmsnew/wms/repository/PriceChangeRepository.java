package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.entity.PriceChange;
import com.example.wmsnew.wms.repository.PriceChangeRepository;

import org.springframework.data.repository.CrudRepository;

public interface PriceChangeRepository extends CrudRepository<PriceChange, Long> {}

