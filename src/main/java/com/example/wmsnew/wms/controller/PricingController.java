package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.service.PricingQueryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/pricing")
public class PricingController {
    private final PricingQueryService query;

    public PricingController(PricingQueryService query) {
        this.query = query;
    }

    @GetMapping("/profit-table")
    public List<PricingQueryService.StoreSkuProfitRow> profitTable(
            @RequestParam Long platformId,
            @RequestParam Long storeId,
            @RequestParam(defaultValue = "200") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 1000);
        return query.storeSkuProfit(platformId, storeId, lim);
    }

    @GetMapping("/profit/calc")
    public PricingQueryService.ProfitCalcResult calc(
            @RequestParam long priceCent,
            @RequestParam long costCent
    ) {
        if (priceCent < 0 || costCent < 0) throw new IllegalArgumentException("priceCent/costCent 不能为负数");
        return query.calc(priceCent, costCent);
    }

    @GetMapping("/price-changes")
    public List<PricingQueryService.PriceChangeRow> priceChanges(
            @RequestParam(required = false) Long skuId,
            @RequestParam(defaultValue = "200") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 1000);
        return query.priceChanges(skuId, lim);
    }

    @GetMapping("/cost-changes")
    public List<PricingQueryService.CostChangeRow> costChanges(
            @RequestParam(required = false) Long skuId,
            @RequestParam(defaultValue = "200") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 1000);
        return query.costChanges(skuId, lim);
    }

    @GetMapping("/price-change-todos")
    public List<PricingQueryService.PriceChangeTodoRow> priceChangeTodos(
            @RequestParam Long platformId,
            @RequestParam Long storeId,
            @RequestParam(defaultValue = "30") int minDays,
            @RequestParam(defaultValue = "200") int limit
    ) {
        int days = Math.min(Math.max(minDays, 1), 3650);
        int lim = Math.min(Math.max(limit, 1), 1000);
        return query.priceChangeTodos(platformId, storeId, days, lim);
    }
}

