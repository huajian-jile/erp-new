package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.entity.Warehouse;
import com.example.wmsnew.wms.repository.WarehouseRepo;
import com.example.wmsnew.wms.service.WarehouseInventoryQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/wms/warehouses")
public class WarehouseController {
    private final WarehouseRepo warehouseRepo;
    private final WarehouseInventoryQueryService warehouseInventoryQueryService;

    public WarehouseController(WarehouseRepo warehouseRepo, WarehouseInventoryQueryService warehouseInventoryQueryService) {
        this.warehouseRepo = warehouseRepo;
        this.warehouseInventoryQueryService = warehouseInventoryQueryService;
    }

    @GetMapping
    public List<Warehouse> list() {
        List<Warehouse> res = new ArrayList<>();
        warehouseRepo.findAll().forEach(res::add);
        return res;
    }

    @GetMapping("/inventory/snapshot")
    public Object inventorySnapshot(@RequestParam Long warehouseId) {
        return warehouseInventoryQueryService.snapshot(warehouseId);
    }
}

