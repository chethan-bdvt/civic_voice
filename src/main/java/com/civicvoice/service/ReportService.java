package com.civicvoice.service;

import com.civicvoice.dto.AdminDashboardResponse;
import com.civicvoice.dto.PublicSummaryResponse;
import com.civicvoice.dto.ReportRequest;
import com.civicvoice.dto.ReportResponse;
import com.civicvoice.entity.Report;
import com.civicvoice.entity.ReportStatus;
import com.civicvoice.exception.ApiException;
import com.civicvoice.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportResponse createReport(ReportRequest request) {
        validateRequest(request);

        String reportId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Report report = Report.builder()
                .reportId(reportId)
                .status(ReportStatus.SUBMITTED)
                .state(normalizeText(request.state()))
                .district(normalizeText(request.district()))
                .subDistrict(normalizeText(request.subDistrict()))
                .department(normalizeText(request.department()))
                .reason(normalizeText(request.reason()))
                .designation(normalizeText(request.designation()))
                .incidentDate(request.incidentDate())
                .demandedAmount(request.demandedAmount())
                .paidAmount(request.paidAmount())
                .description(request.description().trim())
                .proofReference(request.proofReference() == null ? null : request.proofReference().trim())
                .anonymityRequested(Boolean.TRUE.equals(request.anonymityRequested()))
                .officialData(request.officialData().trim())
                .reportedData(request.reportedData().trim())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Report saved = reportRepository.save(report);
        return toResponse(saved);
    }

    public ReportResponse getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ApiException("Report not found with id: " + id));
        return toResponse(report);
    }

    public List<ReportResponse> searchReports(
            String state,
            String district,
            String subDistrict,
            String department,
            LocalDate fromDate,
            LocalDate toDate,
            String status
    ) {
        ReportStatus reportStatus = status == null || status.isBlank() ? null : ReportStatus.valueOf(status.toUpperCase());

        return reportRepository.searchReports(
                        normalizeNullable(state),
                        normalizeNullable(district),
                        normalizeNullable(subDistrict),
                        normalizeNullable(department),
                        fromDate,
                        toDate,
                        reportStatus
                ).stream()
                .map(this::toResponse)
                .toList();
    }

    public PublicSummaryResponse getPublicSummary() {
        List<Report> reports = reportRepository.findAll();

        Map<String, Long> reportsByDepartment = new LinkedHashMap<>();
        Map<String, Long> reportsByState = new LinkedHashMap<>();
        Map<String, Long> reportsByDistrict = new LinkedHashMap<>();

        reports.forEach(report -> {
            reportsByDepartment.merge(report.getDepartment(), 1L, Long::sum);
            reportsByState.merge(report.getState(), 1L, Long::sum);
            reportsByDistrict.merge(report.getDistrict(), 1L, Long::sum);
        });

        long totalReports = reports.size();
        long unverified = reports.stream().filter(r -> r.getStatus() == ReportStatus.UNVERIFIED_ALLEGATION).count();
        long rejected = reports.stream().filter(r -> r.getStatus() == ReportStatus.REJECTED).count();

        return new PublicSummaryResponse(
                totalReports,
                unverified,
                rejected,
                reportsByDepartment,
                reportsByState,
                reportsByDistrict
        );
    }

    public AdminDashboardResponse getAdminDashboardSummary() {
        List<Report> reports = reportRepository.findAll();

        long totalReports = reports.size();
        long submitted = reports.stream().filter(r -> r.getStatus() == ReportStatus.SUBMITTED).count();
        long underReview = reports.stream().filter(r -> r.getStatus() == ReportStatus.UNDER_REVIEW).count();
        long unverified = reports.stream().filter(r -> r.getStatus() == ReportStatus.UNVERIFIED_ALLEGATION).count();
        long rejected = reports.stream().filter(r -> r.getStatus() == ReportStatus.REJECTED).count();
        long archived = reports.stream().filter(r -> r.getStatus() == ReportStatus.ARCHIVED).count();

        return new AdminDashboardResponse(totalReports, submitted, underReview, unverified, rejected, archived);
    }

    public List<ReportResponse> getReportsForReview() {
        return reportRepository.findAll().stream()
                .filter(report -> report.getStatus() == ReportStatus.SUBMITTED || report.getStatus() == ReportStatus.UNDER_REVIEW)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toResponse)
                .toList();
    }

    public ReportResponse updateReportStatus(Long reportId, String status, String notes, String reviewedBy) {
        if (reportId == null) {
            throw new ApiException("Report id is required");
        }

        if (status == null || status.isBlank()) {
            throw new ApiException("Status is required");
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ApiException("Report not found with id: " + reportId));

        ReportStatus newStatus;
        try {
            newStatus = ReportStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Unsupported report status: " + status);
        }

        report.setStatus(newStatus);
        report.setModeratorNotes(notes == null ? null : notes.trim());
        report.setReviewedBy(reviewedBy == null ? "system" : reviewedBy.trim());
        report.setReviewedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());

        Report updated = reportRepository.save(report);
        return toResponse(updated);
    }

    private void validateRequest(ReportRequest request) {
        if (request == null) {
            throw new ApiException("Report request is required");
        }

        if (request.demandedAmount().compareTo(BigDecimal.ZERO) < 0 || request.paidAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ApiException("Amounts cannot be negative");
        }

        if (request.incidentDate() != null && request.incidentDate().isAfter(LocalDate.now())) {
            throw new ApiException("Incident date cannot be in the future");
        }

        if (request.description() == null || request.description().isBlank()) {
            throw new ApiException("Description is required");
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getReportId(),
                report.getStatus(),
                report.getState(),
                report.getDistrict(),
                report.getSubDistrict(),
                report.getDepartment(),
                report.getReason(),
                report.getDesignation(),
                report.getIncidentDate(),
                report.getDemandedAmount(),
                report.getPaidAmount(),
                report.getDescription(),
                report.getProofReference(),
                report.getAnonymityRequested(),
                report.getOfficialData(),
                report.getReportedData(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}
