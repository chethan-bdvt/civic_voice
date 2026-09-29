package com.civicvoice.controller;

import com.civicvoice.dto.AdminDashboardResponse;
import com.civicvoice.dto.ModeratorReviewRequest;
import com.civicvoice.dto.PublicSummaryResponse;
import com.civicvoice.dto.ReportRequest;
import com.civicvoice.dto.ReportResponse;
import com.civicvoice.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody ReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReportResponse>> searchReports(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String subDistrict,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(reportService.searchReports(state, district, subDistrict, department, fromDate, toDate, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping("/summary")
    public ResponseEntity<PublicSummaryResponse> getPublicSummary() {
        return ResponseEntity.ok(reportService.getPublicSummary());
    }

    @GetMapping("/admin/dashboard")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboardSummary() {
        return ResponseEntity.ok(reportService.getAdminDashboardSummary());
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<ReportResponse>> getReportsForReview() {
        return ResponseEntity.ok(reportService.getReportsForReview());
    }

    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<ReportResponse> updateReportStatus(
            @PathVariable Long id,
            @Valid @RequestBody ModeratorReviewRequest request
    ) {
        return ResponseEntity.ok(reportService.updateReportStatus(id, request.status(), request.notes(), request.reviewedBy()));
    }
}
