package com.civicvoice.dto;

import com.civicvoice.entity.ReportStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReportResponse(
        String reportId,
        ReportStatus status,
        String state,
        String district,
        String subDistrict,
        String department,
        String reason,
        String designation,
        LocalDate incidentDate,
        BigDecimal demandedAmount,
        BigDecimal paidAmount,
        String description,
        String proofReference,
        Boolean anonymityRequested,
        String officialData,
        String reportedData,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
