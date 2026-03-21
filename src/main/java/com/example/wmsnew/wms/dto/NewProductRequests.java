package com.example.wmsnew.wms.dto;
import com.example.wmsnew.wms.dto.NewProductRequests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class NewProductRequests {
    private NewProductRequests() {}

    public record Create(
            @NotNull Long platformId,
            @NotBlank @Size(max = 64) String candidateCode,
            @NotBlank @Size(max = 128) String name,
            @Size(max = 64) String category,
            @NotNull @Min(0) Long expectedCostCent,
            @NotNull @Min(0) Long expectedPriceCent,
            @Size(max = 255) String remark,
            @NotBlank @Size(max = 64) String createdBy
    ) {}

    public record Update(
            @NotBlank @Size(max = 128) String name,
            @Size(max = 64) String category,
            @NotNull @Min(0) Long expectedCostCent,
            @NotNull @Min(0) Long expectedPriceCent,
            @Size(max = 255) String remark,
            @NotBlank @Size(max = 32) String status
    ) {}
}

