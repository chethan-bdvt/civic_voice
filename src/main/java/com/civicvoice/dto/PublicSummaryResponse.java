package com.civicvoice.dto;

import java.util.Map;

public record PublicSummaryResponse(
        long totalReports,
        long unverifiedAllegationReports,
        long rejectedReports,
        Map<String, Long> reportsByDepartment,
        Map<String, Long> reportsByState,
        Map<String, Long> reportsByDistrict
) {
}
