package com.numberscope.phone.dto;

import jakarta.validation.constraints.NotBlank;

public record PhoneAnalyzeRequest(
        @NotBlank String rawInput,
        String defaultRegion
) {}