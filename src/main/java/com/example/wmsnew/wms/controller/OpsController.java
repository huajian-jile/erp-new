package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.service.OpsQueryService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/wms/ops")
public class OpsController {
    private final OpsQueryService opsQueryService;

    public OpsController(OpsQueryService opsQueryService) {
        this.opsQueryService = opsQueryService;
    }

    @GetMapping("/kpi")
    public List<OpsQueryService.StoreKpiRow> kpi(
            @RequestParam Long platformId,
            @RequestParam Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return opsQueryService.kpi30d(platformId, storeId, from, to);
    }

    @GetMapping("/cash-flow/daily")
    public List<OpsQueryService.CashFlowDailyRow> cashFlowDaily(
            @RequestParam Long platformId,
            @RequestParam Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return opsQueryService.cashFlowDaily(platformId, storeId, from, to);
    }

    @GetMapping("/ad-spend/daily")
    public List<OpsQueryService.AdSpendDailyRow> adSpendDaily(
            @RequestParam Long platformId,
            @RequestParam Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return opsQueryService.adSpendDaily(platformId, storeId, from, to);
    }

    @GetMapping("/sales-funnel/daily")
    public List<OpsQueryService.SalesFunnelDailyRow> salesFunnelDaily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return opsQueryService.salesFunnelDaily(from, to);
    }
}

