package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.service.SalesOrderQueryService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/wms/sales/orders")
public class SalesOrderController {
    private final SalesOrderQueryService query;

    public SalesOrderController(SalesOrderQueryService query) {
        this.query = query;
    }

    @GetMapping
    public List<SalesOrderQueryService.SalesOrderRow> list(
            @RequestParam Long platformId,
            @RequestParam Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "100") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 500);
        return query.list(platformId, storeId, from, to, status, lim);
    }

    @GetMapping("/{id}")
    public SalesOrderQueryService.SalesOrderDetail detail(@PathVariable Long id) {
        return query.detail(id);
    }
}

