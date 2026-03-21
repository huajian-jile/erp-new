package com.example.wmsnew.wms.dto;
import com.example.wmsnew.wms.dto.ReplenishmentRequests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class ReplenishmentRequests {
    private ReplenishmentRequests() {}

    public record Create(
            @NotNull Long platformId,
            @NotNull Long storeId,
            @NotNull Long skuId,
            @NotNull @Min(1) Integer planQty,
            @Size(max = 64) String createdBy
    ) {}
}

