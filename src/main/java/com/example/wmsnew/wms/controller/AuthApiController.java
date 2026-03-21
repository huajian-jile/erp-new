package com.example.wmsnew.wms.controller;
import com.example.wmsnew.wms.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wms/auth")
public class AuthApiController {
    private final AuthService service;

    public AuthApiController(AuthService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public AuthService.Me me() {
        return service.me();
    }

    @GetMapping("/allowed-views")
    public List<String> allowedViews() {
        return service.allowedViews(service.currentUsername());
    }

    @PostMapping("/dir-usage")
    @ResponseStatus(HttpStatus.CREATED)
    public void dirUsage(@RequestBody @Valid DirUsageReq req) {
        service.logUsage(req.directoryKey());
    }

    public record DirUsageReq(@jakarta.validation.constraints.NotBlank String directoryKey) {}
}

