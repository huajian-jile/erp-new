package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.dto.SkuRequests;
import com.example.wmsnew.wms.entity.CostChange;
import com.example.wmsnew.wms.entity.PriceChange;
import com.example.wmsnew.wms.entity.Sku;
import com.example.wmsnew.wms.repository.CostChangeRepository;
import com.example.wmsnew.wms.repository.PriceChangeRepository;
import com.example.wmsnew.wms.repository.SkuRepository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SkuService {
    private final SkuRepository skuRepository;
    private final PriceChangeRepository priceChangeRepository;
    private final CostChangeRepository costChangeRepository;

    public SkuService(SkuRepository skuRepository, PriceChangeRepository priceChangeRepository, CostChangeRepository costChangeRepository) {
        this.skuRepository = skuRepository;
        this.priceChangeRepository = priceChangeRepository;
        this.costChangeRepository = costChangeRepository;
    }

    public Sku create(SkuRequests.Create req) {
        try {
            return skuRepository.save(new Sku(null, req.productId(), req.sku(), req.title(), req.priceCent(), req.costCent()));
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("sku 已存在: " + req.sku());
        }
    }

    public Sku get(Long id) {
        return skuRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("未找到 sku id=" + id));
    }

    public List<Sku> list() {
        List<Sku> res = new ArrayList<>();
        skuRepository.findAll().forEach(res::add);
        return res;
    }

    @Transactional
    public Sku changePrice(Long skuId, SkuRequests.ChangePrice req) {
        Sku sku = get(skuId);
        Long old = sku.getPriceCent();
        sku.setPriceCent(req.newPriceCent());
        Sku saved = skuRepository.save(sku);
        priceChangeRepository.save(new PriceChange(null, skuId, old, req.newPriceCent(), req.operator(), req.reason()));
        return saved;
    }

    @Transactional
    public Sku changeCost(Long skuId, SkuRequests.ChangeCost req) {
        Sku sku = get(skuId);
        Long old = sku.getCostCent();
        sku.setCostCent(req.newCostCent());
        Sku saved = skuRepository.save(sku);
        costChangeRepository.save(new CostChange(null, skuId, old, req.newCostCent(), req.operator(), req.reason()));
        return saved;
    }
}

