package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.service.ProductService;

import com.example.wmsnew.wms.dto.ProductRequests;
import com.example.wmsnew.wms.entity.Product;
import com.example.wmsnew.wms.repository.ProductRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(ProductRequests.Create req) {
        try {
            return productRepository.save(new Product(null, req.code(), req.name(), "ACTIVE"));
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("product.code 已存在: " + req.code());
        }
    }

    public Product get(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("未找到 product id=" + id));
    }

    public List<Product> list() {
        List<Product> res = new ArrayList<>();
        productRepository.findAll().forEach(res::add);
        return res;
    }
}
