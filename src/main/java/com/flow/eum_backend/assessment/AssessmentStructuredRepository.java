package com.flow.eum_backend.assessment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentStructuredRepository extends JpaRepository<AssessmentStructured, UUID> {
    List<AssessmentStructured> findByCaseId(UUID caseId);

    Optional<AssessmentStructured> findFirstByCaseIdOrderByAssessmentDateDesc(UUID caseId);

    Optional<AssessmentStructured> findByCaseIdAndAssessmentType(UUID caseId, String assessmentType);
}
