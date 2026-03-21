package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.service.AuthService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/admin/usage")
public class UsageApiController {
    private final AuthService service;

    public UsageApiController(AuthService service) {
        this.service = service;
    }

    @GetMapping("/top-directories")
    public List<AuthService.UsageTopDirRow> topDirectories(@RequestParam(defaultValue = "10") int limit) {
        int lim = Math.min(Math.max(limit, 1), 100);
        return service.topDirectories(lim);
    }

    @GetMapping("/top-users")
    public List<AuthService.UsageTopUserRow> topUsers(
            @RequestParam String directoryKey,
            @RequestParam(defaultValue = "20") int limit
    ) {
        int lim = Math.min(Math.max(limit, 1), 200);
        return service.topUsers(directoryKey, lim);
    }
}

