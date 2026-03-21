package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.service.PurchaseOrderCommandService;
import com.example.wmsnew.wms.service.PurchaseOrderQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/procurement/purchase-orders")
public class PurchaseOrderController {
    private final PurchaseOrderQueryService query;
    private final PurchaseOrderCommandService cmd;

    public PurchaseOrderController(PurchaseOrderQueryService query, PurchaseOrderCommandService cmd) {
        this.query = query;
        this.cmd = cmd;
    }

    @GetMapping
    public List<PurchaseOrderQueryService.PurchaseOrderRow> list(
            @RequestParam Long platformId,
            @RequestParam Long storeId,
            @RequestParam(defaultValue = "50") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 200);
        return query.list(platformId, storeId, lim);
    }

    @GetMapping("/{id}")
    public PurchaseOrderQueryService.PurchaseOrderDetail detail(@PathVariable Long id) {
        return query.detail(id);
    }

    @PostMapping("/{id}:approve")
    public void approve(@PathVariable Long id) {
        cmd.approve(id);
    }

    @PostMapping("/{id}:ship")
    public void ship(@PathVariable Long id) {
        cmd.ship(id);
    }

    @PostMapping("/{id}:receive")
    public void receive(@PathVariable Long id) {
        cmd.receive(id);
    }
}

