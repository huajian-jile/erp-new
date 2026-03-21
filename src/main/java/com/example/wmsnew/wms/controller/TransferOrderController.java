package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.service.TransferOrderCommandService;
import com.example.wmsnew.wms.service.TransferOrderQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/transfer/orders")
public class TransferOrderController {
    private final TransferOrderQueryService query;
    private final TransferOrderCommandService cmd;

    public TransferOrderController(TransferOrderQueryService query, TransferOrderCommandService cmd) {
        this.query = query;
        this.cmd = cmd;
    }

    @GetMapping
    public List<TransferOrderQueryService.TransferOrderRow> list(
            @RequestParam Long platformId,
            @RequestParam Long storeId,
            @RequestParam(defaultValue = "50") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 200);
        return query.list(platformId, storeId, lim);
    }

    @GetMapping("/{id}")
    public TransferOrderQueryService.TransferOrderDetail detail(@PathVariable Long id) {
        return query.detail(id);
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

