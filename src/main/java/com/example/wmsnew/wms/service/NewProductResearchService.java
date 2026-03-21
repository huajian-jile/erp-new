package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.dto.NewProductRequests;
import com.example.wmsnew.wms.entity.NewProductResearch;
import com.example.wmsnew.wms.repository.NewProductResearchRepository;
import com.example.wmsnew.wms.service.NewProductResearchService;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewProductResearchService {
    private final NewProductResearchRepository repo;

    public NewProductResearchService(NewProductResearchRepository repo) {
        this.repo = repo;
    }

    public NewProductResearch create(NewProductRequests.Create req) {
        try {
            NewProductResearch r = new NewProductResearch();
            r.setPlatformId(req.platformId());
            r.setCandidateCode(req.candidateCode());
            r.setName(req.name());
            r.setCategory(req.category());
            r.setExpectedCostCent(req.expectedCostCent());
            r.setExpectedPriceCent(req.expectedPriceCent());
            r.setRemark(req.remark());
            r.setStatus("NEW");
            r.setCreatedBy(req.createdBy());
            return repo.save(r);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("candidate_code 已存在: " + req.candidateCode());
        }
    }

    public NewProductResearch get(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("未找到新品调研 id=" + id));
    }

    public List<NewProductResearch> list() {
        List<NewProductResearch> res = new ArrayList<>();
        repo.findAll().forEach(res::add);
        return res;
    }

    public List<NewProductResearch> listByPlatform(Long platformId) {
        // 简单实现：在小数据量 mock 下足够；需要可再改成 jdbc 分页/过滤
        return list().stream().filter(r -> platformId.equals(r.getPlatformId())).toList();
    }

    public NewProductResearch update(Long id, NewProductRequests.Update req) {
        NewProductResearch r = get(id);
        r.setName(req.name());
        r.setCategory(req.category());
        r.setExpectedCostCent(req.expectedCostCent());
        r.setExpectedPriceCent(req.expectedPriceCent());
        r.setRemark(req.remark());
        r.setStatus(req.status());
        return repo.save(r);
    }
}

