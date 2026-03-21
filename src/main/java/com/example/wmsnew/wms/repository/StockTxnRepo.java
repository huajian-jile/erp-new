package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.entity.StockTxn;
import com.example.wmsnew.wms.repository.StockTxnRepo;

import org.springframework.data.repository.CrudRepository;

public interface StockTxnRepo extends CrudRepository<StockTxn, Long> {}

