package com.example.wmsnew.wms.dto;
import com.example.wmsnew.wms.dto.InventoryChangeRequests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class InventoryChangeRequests {
    private InventoryChangeRequests() {}

    public record Allocate(
            @NotNull Long platformId,
            @NotNull Long storeId,
            @NotNull Long skuId,
            @NotNull @Min(1) Integer allocQty,
            @Size(max = 64) String operator
    ) {}
}

