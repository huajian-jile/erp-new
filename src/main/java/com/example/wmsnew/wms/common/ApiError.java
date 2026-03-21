package com.example.wmsnew.wms.common;

public record ApiError(
        String code,
        String message
) {}

