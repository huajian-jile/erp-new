package com.example.wmsnew.wms.controller;

import com.example.wmsnew.wms.dto.ProductRequests;
import com.example.wmsnew.wms.entity.Product;
import com.example.wmsnew.wms.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/catalog/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody @Valid ProductRequests.Create req) {
        return productService.create(req);
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productService.get(id);
    }

    @GetMapping
    public List<Product> list() {
        return productService.list();
    }
}
