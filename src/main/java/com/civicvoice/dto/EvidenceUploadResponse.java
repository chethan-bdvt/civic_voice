package com.civicvoice.dto;

import java.time.LocalDateTime;

public record EvidenceUploadResponse(
        Long id,
        Long reportId,
        String originalFileName,
        String storedFileName,
        String contentType,
        long fileSize,
        String checksum,
        LocalDateTime uploadedAt
) {
}
