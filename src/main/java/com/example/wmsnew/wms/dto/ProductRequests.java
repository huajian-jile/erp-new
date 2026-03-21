package com.example.wmsnew.wms.dto;
import com.example.wmsnew.wms.dto.ProductRequests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ProductRequests {
    private ProductRequests() {}

    public record Create(
            @NotBlank @Size(max = 64) String code,
            @NotBlank @Size(max = 128) String name
    ) {}
}
