package com.example.wmsnew.wms.repository;
import com.example.wmsnew.wms.repository.ProductRepository;

import com.example.wmsnew.wms.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {
    Optional<Product> findByCode(String code);
}
