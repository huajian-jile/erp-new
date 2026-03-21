package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.service.AnalyticsQueryService;
import com.example.wmsnew.wms.service.RiskQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/analytics")
public class AnalyticsController {
    private final AnalyticsQueryService analyticsQueryService;
    private final RiskQueryService riskQueryService;

    public AnalyticsController(AnalyticsQueryService analyticsQueryService, RiskQueryService riskQueryService) {
        this.analyticsQueryService = analyticsQueryService;
        this.riskQueryService = riskQueryService;
    }

    @GetMapping("/rank/sales-qty-30d")
    public List<AnalyticsQueryService.SalesRankRow> salesRank(@RequestParam(defaultValue = "20") int limit) {
        return analyticsQueryService.salesRank30d(Math.min(Math.max(limit, 1), 200));
    }

    @GetMapping("/rank/profit-30d")
    public List<AnalyticsQueryService.ProfitRankRow> profitRank(@RequestParam(defaultValue = "20") int limit) {
        return analyticsQueryService.profitRank30d(Math.min(Math.max(limit, 1), 200));
    }

    @GetMapping("/slow-moving")
    public List<RiskQueryService.SlowMovingRow> slowMoving(@RequestParam(defaultValue = "50") int limit) {
        return riskQueryService.slowMoving(Math.min(Math.max(limit, 1), 200));
    }

    @GetMapping("/negative-profit/skus")
    public List<RiskQueryService.NegativeProfitSkuRow> negativeProfitSkus(@RequestParam(defaultValue = "50") int limit) {
        return riskQueryService.negativeProfitSkus(Math.min(Math.max(limit, 1), 200));
    }
}

