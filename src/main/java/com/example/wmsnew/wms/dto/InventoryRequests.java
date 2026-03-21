package com.example.wmsnew.wms.dto;
import com.example.wmsnew.wms.dto.InventoryRequests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class InventoryRequests {
    private InventoryRequests() {}

    public record AdjustStock(
            @NotNull Long storeId,
            @NotNull Long skuId,
            @NotNull Integer delta,
            @NotBlank @Size(max = 64) String reason,
            @Size(max = 64) String refNo
    ) {}
}

