package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.dto.InventoryRequests;
import com.example.wmsnew.wms.entity.Inventory;
import com.example.wmsnew.wms.entity.StockTxn;
import com.example.wmsnew.wms.repository.InventoryRepo;
import com.example.wmsnew.wms.repository.StockTxnRepo;
import com.example.wmsnew.wms.service.InventoryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {
    private final InventoryRepo inventoryRepo;
    private final StockTxnRepo txnRepo;

    public InventoryService(InventoryRepo inventoryRepo,
                            StockTxnRepo txnRepo) {
        this.inventoryRepo = inventoryRepo;
        this.txnRepo = txnRepo;
    }

    @Transactional
    public Inventory adjust(InventoryRequests.AdjustStock req) {
        Inventory inv = inventoryRepo.findByStoreIdAndSkuId(req.storeId(), req.skuId())
                .orElseGet(() -> new Inventory(null, req.storeId(), req.skuId(), 0));
        int newQty = inv.getQty() + req.delta();
        if (newQty < 0) {
            throw new IllegalArgumentException("库存不足：当前=" + inv.getQty() + " delta=" + req.delta());
        }
        inv.setQty(newQty);
        Inventory saved = inventoryRepo.save(inv);
        txnRepo.save(new StockTxn(null, req.storeId(), req.skuId(), req.delta(), req.reason(), req.refNo()));
        return saved;
    }
}

