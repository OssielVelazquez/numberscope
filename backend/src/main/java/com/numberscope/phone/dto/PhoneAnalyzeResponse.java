package com.numberscope.phone.dto;

import java.util.List;

public record PhoneAnalyzeResponse(
        String rawInput,
        boolean valid,
        String e164,
        String country,
        String region,
        String carrier,
        String lineType,
        List<String> timezones,
        Double regionLat,
        Double regionLng,
        String regionName,
        String lada,
        String error
) {}