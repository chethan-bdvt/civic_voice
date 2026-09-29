package com.civicvoice.repository;

import com.civicvoice.entity.EvidenceFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenceRepository extends JpaRepository<EvidenceFile, Long> {
    List<EvidenceFile> findByReportId(Long reportId);
}
