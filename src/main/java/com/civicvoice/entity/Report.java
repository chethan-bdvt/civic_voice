package com.civicvoice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reportId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String subDistrict;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private String designation;

    @Column(nullable = false)
    private LocalDate incidentDate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal demandedAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal paidAmount;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(length = 500)
    private String proofReference;

    @Column(nullable = false)
    private Boolean anonymityRequested;

    @Column(length = 500)
    private String officialData;

    @Column(length = 500)
    private String reportedData;

    @Column(length = 1000)
    private String moderatorNotes;

    @Column(length = 200)
    private String reviewedBy;

    @Column
    private LocalDateTime reviewedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
