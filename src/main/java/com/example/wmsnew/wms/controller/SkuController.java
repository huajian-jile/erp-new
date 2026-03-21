package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.dto.SkuRequests;
import com.example.wmsnew.wms.entity.Sku;
import com.example.wmsnew.wms.service.SkuService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/catalog/skus")
public class SkuController {
    private final SkuService skuService;

    public SkuController(SkuService skuService) {
        this.skuService = skuService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sku create(@RequestBody @Valid SkuRequests.Create req) {
        return skuService.create(req);
    }

    @GetMapping("/{id}")
    public Sku get(@PathVariable Long id) {
        return skuService.get(id);
    }

    @GetMapping
    public List<Sku> list() {
        return skuService.list();
    }

    @PostMapping("/{id}/price:change")
    public Sku changePrice(@PathVariable Long id, @RequestBody @Valid SkuRequests.ChangePrice req) {
        return skuService.changePrice(id, req);
    }

    @PostMapping("/{id}/cost:change")
    public Sku changeCost(@PathVariable Long id, @RequestBody @Valid SkuRequests.ChangeCost req) {
        return skuService.changeCost(id, req);
    }
}

