package com.civicvoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReportRequest(
        @NotBlank(message = "State is required") String state,
        @NotBlank(message = "District is required") String district,
        @NotBlank(message = "Sub district is required") String subDistrict,
        @NotBlank(message = "Department is required") String department,
        @NotBlank(message = "Reason is required") String reason,
        @NotBlank(message = "Designation is required") String designation,
        @NotBlank(message = "Description is required") String description,
        @NotBlank(message = "Official data is required") String officialData,
        @NotBlank(message = "Reported data is required") String reportedData,
        @NotNull(message = "Demanded amount is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Demanded amount must be zero or a positive number")
        BigDecimal demandedAmount,
        @NotNull(message = "Paid amount is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Paid amount must be zero or a positive number")
        BigDecimal paidAmount,
        @NotNull(message = "Incident date is required")
        @PastOrPresent(message = "Incident date cannot be in the future")
        LocalDate incidentDate,
        Boolean anonymityRequested,
        String proofReference
) {
}
