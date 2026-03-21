package com.example.wmsnew.wms.dto;
import com.example.wmsnew.wms.dto.SkuRequests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class SkuRequests {
    private SkuRequests() {}

    public record Create(
            @NotNull Long productId,
            @NotBlank @Size(max = 64) String sku,
            @NotBlank @Size(max = 128) String title,
            @NotNull @Min(0) Long priceCent,
            @NotNull @Min(0) Long costCent
    ) {}

    public record ChangePrice(
            @NotNull @Min(0) Long newPriceCent,
            @NotBlank @Size(max = 64) String operator,
            @Size(max = 128) String reason
    ) {}

    public record ChangeCost(
            @NotNull @Min(0) Long newCostCent,
            @NotBlank @Size(max = 64) String operator,
            @Size(max = 128) String reason
    ) {}
}

