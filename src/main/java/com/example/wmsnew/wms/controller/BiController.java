package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.service.BiSqlService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/wms/bi")
public class BiController {
    private final BiSqlService biSqlService;

    public BiController(BiSqlService biSqlService) {
        this.biSqlService = biSqlService;
    }

    @GetMapping("/stock-delta/daily")
    public List<BiSqlService.DailyStockDelta> dailyStockDelta(
            @RequestParam Long storeId,
            @RequestParam Long skuId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return biSqlService.dailyStockDelta(storeId, skuId, from, to);
    }
}

