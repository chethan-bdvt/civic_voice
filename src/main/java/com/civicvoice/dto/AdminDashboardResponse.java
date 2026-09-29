package com.civicvoice.dto;

public record AdminDashboardResponse(
        long totalReports,
        long submitted,
        long underReview,
        long unverifiedAllegations,
        long rejected,
        long archived
) {
}
