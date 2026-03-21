package com.example.wmsnew.wms.dto;
import com.example.wmsnew.wms.dto.StoreRequests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class StoreRequests {
    private StoreRequests() {}

    public record CreateStore(
            @NotNull Long platformId,
            @NotBlank @Size(max = 64) String code,
            @NotBlank @Size(max = 128) String name
    ) {}

    public record CreateManager(
            @NotNull Long storeId,
            @NotBlank @Size(max = 64) String username,
            @NotBlank @Size(max = 64) String displayName
    ) {}
}

