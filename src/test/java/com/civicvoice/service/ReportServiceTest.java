package com.civicvoice.service;

import com.civicvoice.dto.ReportRequest;
import com.civicvoice.dto.ReportResponse;
import com.civicvoice.entity.Report;
import com.civicvoice.entity.ReportStatus;
import com.civicvoice.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EvidenceService evidenceService;

    @Test
    void shouldCreateReportAndSetSubmittedStatus() {
        ReportRequest request = new ReportRequest(
                "Maharashtra",
                "Pune",
                "Hinjawadi",
                "Urban Development",
                "Demanded cash for permit",
                "Junior Engineer",
                "Official demanded cash for a permit approval.",
                "Survey record and office note",
                "Complainant record and timeline",
                new BigDecimal("15000.00"),
                new BigDecimal("12000.00"),
                LocalDate.now().minusDays(5),
                true,
                "proof-1.pdf"
        );

        ReportResponse response = reportService.createReport(request);

        assertNotNull(response);
        assertNotNull(response.reportId());
        assertEquals(ReportStatus.SUBMITTED, response.status());
        assertEquals("Maharashtra", response.state());
    }

    @Test
    void shouldSearchReportsByStateAndDepartment() {
        ReportRequest request = new ReportRequest(
                "Karnataka",
                "Bengaluru",
                "Koramangala",
                "Transport",
                "Demanded bribe for registration",
                "Assistant Motor Vehicle Inspector",
                "Officer requested payment to process a registration",
                "Inspection note and appointment record",
                "Applicant timeline and complaint record",
                new BigDecimal("20000.00"),
                new BigDecimal("18000.00"),
                LocalDate.now().minusDays(10),
                true,
                "proof-2.pdf"
        );

        reportService.createReport(request);

        List<ReportResponse> results = reportService.searchReports(
                "Karnataka",
                null,
                null,
                "Transport",
                LocalDate.now().minusDays(30),
                LocalDate.now(),
                null
        );

        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.department().equals("Transport")));
    }

    @Test
    void shouldUpdateReportStatusForModeration() {
        ReportRequest request = new ReportRequest(
                "Tamil Nadu",
                "Chennai",
                "Adyar",
                "Health",
                "Demanded payment for medical clearance",
                "Health Inspector",
                "Medical permit was held until a cash demand was made.",
                "Internal inspection memo",
                "Complaint file and timeline",
                new BigDecimal("7500.00"),
                new BigDecimal("5000.00"),
                LocalDate.now().minusDays(2),
                true,
                "proof-3.pdf"
        );

        reportService.createReport(request);

        Report persisted = reportRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .orElseThrow();

        ReportResponse updated = reportService.updateReportStatus(
                persisted.getId(),
                "UNVERIFIED_ALLEGATION",
                "Evidence is under review; allegation remains unverified.",
                "moderator01"
        );

        assertNotNull(updated);
        assertEquals(ReportStatus.UNVERIFIED_ALLEGATION, updated.status());
        assertEquals("moderator01", reportRepository.findById(persisted.getId()).orElseThrow().getReviewedBy());
    }

    @Test
    void shouldUploadEvidenceAndComputeDashboardSummary() {
        ReportRequest request = new ReportRequest(
                "Punjab",
                "Amritsar",
                "Ranjit Avenue",
                "Revenue",
                "Demanded extra payment for file approval",
                "Revenue Clerk",
                "A cash demand was made to move the file forward.",
                "Office record and file movement log",
                "Complaint note and timeline",
                new BigDecimal("5000.00"),
                new BigDecimal("5000.00"),
                LocalDate.now().minusDays(1),
                true,
                "proof-4.pdf"
        );

        ReportResponse created = reportService.createReport(request);
        Report persisted = reportRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .orElseThrow();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof-4.pdf",
                "application/pdf",
                "%PDF-1.4\n%test".getBytes()
        );

        var evidence = evidenceService.uploadEvidence(persisted.getId(), file);
        var dashboard = reportService.getAdminDashboardSummary();

        assertNotNull(evidence);
        assertEquals("proof-4.pdf", evidence.originalFileName());
        assertTrue(dashboard.totalReports() >= 1);
    }

    @Test
    void shouldDownloadEvidenceBytesForStoredFile() {
        ReportRequest request = new ReportRequest(
                "Haryana",
                "Gurugram",
                "Cyber City",
                "Municipal",
                "Extra charge for clearance",
                "Junior Engineer",
                "Payment was demanded to process municipal clearance.",
                "Internal memo and timeline",
                "Complaint note and supporting file",
                new BigDecimal("9000.00"),
                new BigDecimal("6000.00"),
                LocalDate.now().minusDays(3),
                true,
                "proof-5.pdf"
        );

        reportService.createReport(request);
        Report persisted = reportRepository.findAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .orElseThrow();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "proof-5.pdf",
                "application/pdf",
                "JVBERi0xLjQKJcTl8uXr".getBytes()
        );

        var uploaded = evidenceService.uploadEvidence(persisted.getId(), file);
        byte[] downloaded = evidenceService.downloadEvidence(persisted.getId(), uploaded.id());

        assertNotNull(downloaded);
        assertTrue(downloaded.length > 0);
        assertEquals("JVBERi0xLjQKJcTl8uXr", new String(downloaded));
    }
}
