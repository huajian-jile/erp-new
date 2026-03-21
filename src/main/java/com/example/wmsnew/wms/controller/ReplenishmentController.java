package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.dto.ReplenishmentRequests;
import com.example.wmsnew.wms.entity.Replenishment;
import com.example.wmsnew.wms.entity.Inventory;
import com.example.wmsnew.wms.service.ReplenishmentService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wms/replenishment")
public class ReplenishmentController {
    private final ReplenishmentService replenishmentService;

    public ReplenishmentController(ReplenishmentService replenishmentService) {
        this.replenishmentService = replenishmentService;
    }

    @PostMapping("/plans")
    @ResponseStatus(HttpStatus.CREATED)
    public Replenishment create(@RequestBody @Valid ReplenishmentRequests.Create req) {
        return replenishmentService.create(req);
    }

    @PostMapping("/{id}:execute")
    public Inventory execute(@PathVariable Long id) {
        return replenishmentService.execute(id);
    }
}

