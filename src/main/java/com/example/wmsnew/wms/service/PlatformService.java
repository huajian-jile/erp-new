package com.example.wmsnew.wms.service;
import com.example.wmsnew.wms.entity.Platform;
import com.example.wmsnew.wms.repository.PlatformRepo;
import com.example.wmsnew.wms.service.PlatformService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlatformService {
    private final PlatformRepo platformRepo;

    public PlatformService(PlatformRepo platformRepo) {
        this.platformRepo = platformRepo;
    }

    public List<Platform> list() {
        List<Platform> res = new ArrayList<>();
        platformRepo.findAll().forEach(res::add);
        return res;
    }
}

