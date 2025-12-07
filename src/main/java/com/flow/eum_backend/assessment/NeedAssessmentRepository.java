package com.flow.eum_backend.assessment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NeedAssessmentRepository extends JpaRepository<NeedAssessment, UUID> {
    // 최신 한 건 조회 (assessment_date → created_at 순으로)
    Optional<NeedAssessment> findFirstByCaseIdOrderByAssessmentDateDescCreatedAtDesc(UUID caseId);
}
