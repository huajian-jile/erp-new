package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.dto.InventoryChangeRequests;
import com.example.wmsnew.wms.service.InventoryChangeCommandService;
import com.example.wmsnew.wms.service.InventoryChangeQueryService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/inventory-change")
public class InventoryChangeController {
    private final InventoryChangeQueryService queryService;
    private final InventoryChangeCommandService commandService;

    public InventoryChangeController(InventoryChangeQueryService queryService, InventoryChangeCommandService commandService) {
        this.queryService = queryService;
        this.commandService = commandService;
    }

    @GetMapping("/pending")
    public List<InventoryChangeQueryService.PendingRow> pending(
            @RequestParam(required = false) Long platformId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(defaultValue = "100") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 500);
        return queryService.pending(platformId, storeId, lim);
    }

    @GetMapping("/allocations")
    public List<InventoryChangeQueryService.AllocationRow> allocations(
            @RequestParam(required = false) Long platformId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(defaultValue = "100") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 500);
        return queryService.allocations(platformId, storeId, lim);
    }

    @PostMapping("/allocations")
    public InventoryChangeQueryService.AllocationRow allocate(@RequestBody @Valid InventoryChangeRequests.Allocate req) {
        return commandService.allocate(req);
    }

    @PostMapping("/allocations/{id}:sync")
    public void sync(@PathVariable Long id) {
        commandService.sync(id);
    }

    @PostMapping("/allocations/{id}:clear-other-stores")
    public void clearOtherStores(@PathVariable Long id) {
        commandService.clearOtherStores(id);
    }

    @GetMapping("/sync-logs")
    public List<InventoryChangeQueryService.SyncLogRow> syncLogs(
            @RequestParam(required = false) Long storeId,
            @RequestParam(defaultValue = "200") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 1000);
        return queryService.syncLogs(storeId, lim);
    }

    @GetMapping("/clear-records")
    public List<InventoryChangeQueryService.ClearRecordRow> clearRecords(
            @RequestParam(required = false) Long platformId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(defaultValue = "200") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 1000);
        return queryService.clearRecords(platformId, storeId, lim);
    }
}

