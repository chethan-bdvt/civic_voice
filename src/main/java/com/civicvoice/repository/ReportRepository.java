package com.civicvoice.repository;

import com.civicvoice.entity.Report;
import com.civicvoice.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("""
            select r from Report r
            where (:state is null or r.state = :state)
              and (:district is null or r.district = :district)
              and (:subDistrict is null or r.subDistrict = :subDistrict)
              and (:department is null or r.department = :department)
              and (:fromDate is null or r.incidentDate >= :fromDate)
              and (:toDate is null or r.incidentDate <= :toDate)
              and (:status is null or r.status = :status)
            order by r.createdAt desc
            """)
    List<Report> searchReports(
            @Param("state") String state,
            @Param("district") String district,
            @Param("subDistrict") String subDistrict,
            @Param("department") String department,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("status") ReportStatus status
    );

    boolean existsByReportId(String reportId);
}
