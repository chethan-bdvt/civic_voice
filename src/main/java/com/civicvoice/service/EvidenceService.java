package com.civicvoice.service;

import com.civicvoice.dto.EvidenceUploadResponse;
import com.civicvoice.entity.EvidenceFile;
import com.civicvoice.entity.Report;
import com.civicvoice.exception.ApiException;
import com.civicvoice.repository.EvidenceRepository;
import com.civicvoice.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvidenceService {

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024;

    private final ReportRepository reportRepository;
    private final EvidenceRepository evidenceRepository;

    @Value("${app.evidence.storage-path:storage/evidence}")
    private String evidenceStoragePath;

    public EvidenceUploadResponse uploadEvidence(Long reportId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("Evidence file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ApiException("File size exceeds the 10 MB limit");
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ApiException("Report not found with id: " + reportId));

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("application/pdf")
                || contentType.startsWith("image/"))) {
            throw new ApiException("Only PDF and image files are allowed");
        }

        String originalFileName = sanitizeFileName(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;
        Path storageDir = Paths.get(evidenceStoragePath).toAbsolutePath().normalize();

        try {
            Files.createDirectories(storageDir);
            Path destination = storageDir.resolve(storedFileName);
            file.transferTo(destination);

            String checksum = sha256(file);
            EvidenceFile evidence = EvidenceFile.builder()
                    .reportId(report.getId())
                    .originalFileName(originalFileName)
                    .storedFileName(storedFileName)
                    .storagePath(destination.toString())
                    .contentType(contentType)
                    .fileSize(file.getSize())
                    .checksum(checksum)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            EvidenceFile saved = evidenceRepository.save(evidence);
            return new EvidenceUploadResponse(
                    saved.getId(),
                    saved.getReportId(),
                    saved.getOriginalFileName(),
                    saved.getStoredFileName(),
                    saved.getContentType(),
                    saved.getFileSize(),
                    saved.getChecksum(),
                    saved.getUploadedAt()
            );
        } catch (IOException e) {
            throw new ApiException("Failed to store evidence file: " + e.getMessage());
        }
    }

    public List<EvidenceUploadResponse> getEvidenceByReportId(Long reportId) {
        if (reportId == null) {
            throw new ApiException("Report id is required");
        }
        return evidenceRepository.findByReportId(reportId).stream()
                .map(e -> new EvidenceUploadResponse(
                        e.getId(),
                        e.getReportId(),
                        e.getOriginalFileName(),
                        e.getStoredFileName(),
                        e.getContentType(),
                        e.getFileSize(),
                        e.getChecksum(),
                        e.getUploadedAt()
                ))
                .toList();
    }

    public EvidenceFile getEvidenceFile(Long reportId, Long evidenceId) {
        if (reportId == null || evidenceId == null) {
            throw new ApiException("Report id and evidence id are required");
        }

        reportRepository.findById(reportId)
                .orElseThrow(() -> new ApiException("Report not found with id: " + reportId));

        EvidenceFile evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new ApiException("Evidence not found with id: " + evidenceId));

        if (!Objects.equals(evidence.getReportId(), reportId)) {
            throw new ApiException("Evidence does not belong to the specified report");
        }

        Path filePath = Paths.get(evidence.getStoragePath());
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ApiException("Evidence file is missing from storage");
        }

        return evidence;
    }

    public byte[] downloadEvidence(Long reportId, Long evidenceId) {
        EvidenceFile evidence = getEvidenceFile(reportId, evidenceId);
        try {
            return Files.readAllBytes(Paths.get(evidence.getStoragePath()));
        } catch (IOException e) {
            throw new ApiException("Failed to read evidence file: " + e.getMessage());
        }
    }

    private String sanitizeFileName(String originalFileName) {
        String cleaned = originalFileName == null ? "upload" : originalFileName.trim();
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9._-]", "_");
        return cleaned.isBlank() ? "upload" : cleaned;
    }

    private String sha256(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = file.getBytes();
            byte[] hash = digest.digest(bytes);
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new ApiException("Could not generate checksum for uploaded file");
        }
    }
}
