package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.dto.NewProductRequests;
import com.example.wmsnew.wms.entity.NewProductResearch;
import com.example.wmsnew.wms.service.NewProductResearchService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/catalog/new-products")
public class NewProductResearchController {
    private final NewProductResearchService service;

    public NewProductResearchController(NewProductResearchService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewProductResearch create(@RequestBody @Valid NewProductRequests.Create req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public NewProductResearch get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<NewProductResearch> list(@RequestParam(required = false) Long platformId) {
        if (platformId == null) return service.list();
        return service.listByPlatform(platformId);
    }

    @PutMapping("/{id}")
    public NewProductResearch update(@PathVariable Long id, @RequestBody @Valid NewProductRequests.Update req) {
        return service.update(id, req);
    }
}

