package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.dto.StoreRequests;
import com.example.wmsnew.wms.entity.Store;
import com.example.wmsnew.wms.entity.StoreManager;
import com.example.wmsnew.wms.service.StoreService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/stores")
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Store createStore(@RequestBody @Valid StoreRequests.CreateStore req) {
        return storeService.createStore(req);
    }

    @GetMapping
    public List<Store> listStores(@RequestParam(required = false) Long platformId) {
        if (platformId == null) {
            return storeService.listStores();
        }
        return storeService.listStoresByPlatform(platformId);
    }

    @PostMapping("/managers")
    @ResponseStatus(HttpStatus.CREATED)
    public StoreManager createManager(@RequestBody @Valid StoreRequests.CreateManager req) {
        return storeService.createManager(req);
    }

    @PostMapping("/{storeId}/api-keys:issue")
    public StoreService.ApiKeyIssued issueKey(@PathVariable Long storeId) {
        return storeService.issueApiKey(storeId);
    }
}

