package com.civicvoice.dto;

import jakarta.validation.constraints.NotBlank;

public record ModeratorReviewRequest(
        @NotBlank(message = "Status is required") String status,
        String notes,
        String reviewedBy
) {
}
