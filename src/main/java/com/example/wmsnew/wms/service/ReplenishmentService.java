package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.dto.InventoryRequests;
import com.example.wmsnew.wms.dto.ReplenishmentRequests;
import com.example.wmsnew.wms.entity.Inventory;
import com.example.wmsnew.wms.entity.Replenishment;
import com.example.wmsnew.wms.repository.ReplenishmentRepo;
import com.example.wmsnew.wms.service.InventoryService;
import com.example.wmsnew.wms.service.ReplenishmentService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReplenishmentService {
    private final ReplenishmentRepo replenishmentRepo;
    private final InventoryService inventoryService;

    public ReplenishmentService(ReplenishmentRepo replenishmentRepo, InventoryService inventoryService) {
        this.replenishmentRepo = replenishmentRepo;
        this.inventoryService = inventoryService;
    }

    public Replenishment create(ReplenishmentRequests.Create req) {
        String by = (req.createdBy() == null || req.createdBy().isBlank()) ? "system" : req.createdBy();
        return replenishmentRepo.save(new Replenishment(null, req.platformId(), req.storeId(), req.skuId(), req.planQty(), "CREATED", by));
    }

    @Transactional
    public Inventory execute(Long replenishmentId) {
        Replenishment r = replenishmentRepo.findById(replenishmentId)
                .orElseThrow(() -> new IllegalArgumentException("未找到备货单 id=" + replenishmentId));
        if (!"CREATED".equals(r.getStatus())) {
            throw new IllegalArgumentException("备货单状态不可执行: " + r.getStatus());
        }
        Inventory inv = inventoryService.adjust(new InventoryRequests.AdjustStock(
                r.getStoreId(),
                r.getSkuId(),
                r.getPlanQty(),
                "REPLENISH",
                "REPLENISH-" + replenishmentId
        ));
        r.setStatus("EXECUTED");
        r.setExecutedAt(LocalDateTime.now());
        replenishmentRepo.save(r);
        return inv;
    }
}

