package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.entity.Platform;
import com.example.wmsnew.wms.service.PlatformService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/platforms")
public class PlatformController {
    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping
    public List<Platform> list() {
        return platformService.list();
    }
}

