package com.civicvoice.controller;

import com.civicvoice.dto.EvidenceUploadResponse;
import com.civicvoice.entity.EvidenceFile;
import com.civicvoice.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EvidenceController {

    private final EvidenceService evidenceService;

    @PostMapping("/reports/{reportId}/evidence")
    public ResponseEntity<EvidenceUploadResponse> uploadEvidence(
            @PathVariable Long reportId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(evidenceService.uploadEvidence(reportId, file));
    }

    @GetMapping("/reports/{reportId}/evidence")
    public ResponseEntity<List<EvidenceUploadResponse>> getEvidenceByReport(@PathVariable Long reportId) {
        return ResponseEntity.ok(evidenceService.getEvidenceByReportId(reportId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/{reportId}/evidence/{evidenceId}/download")
    public ResponseEntity<byte[]> downloadEvidence(@PathVariable Long reportId, @PathVariable Long evidenceId) {
        EvidenceFile evidence = evidenceService.getEvidenceFile(reportId, evidenceId);
        byte[] content = evidenceService.downloadEvidence(reportId, evidenceId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + evidence.getOriginalFileName() + "\"")
                .contentType(MediaType.parseMediaType(evidence.getContentType()))
                .contentLength(content.length)
                .body(content);
    }
}
