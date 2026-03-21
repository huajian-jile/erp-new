package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.dto.InventoryRequests;
import com.example.wmsnew.wms.entity.Inventory;
import com.example.wmsnew.wms.service.InventoryQueryService;
import com.example.wmsnew.wms.service.InventoryService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wms/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private final InventoryQueryService inventoryQueryService;

    public InventoryController(InventoryService inventoryService, InventoryQueryService inventoryQueryService) {
        this.inventoryService = inventoryService;
        this.inventoryQueryService = inventoryQueryService;
    }

    @PostMapping("/stock:adjust")
    public Inventory adjust(@RequestBody @Valid InventoryRequests.AdjustStock req) {
        return inventoryService.adjust(req);
    }

    @GetMapping("/snapshot")
    public Object snapshot(@RequestParam Long storeId) {
        return inventoryQueryService.snapshot(storeId);
    }
}

