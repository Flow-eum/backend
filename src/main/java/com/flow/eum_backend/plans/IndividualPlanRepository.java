package com.flow.eum_backend.plans;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndividualPlanRepository extends JpaRepository<IndividualPlan, UUID> {

    // 사례별 모든 계획을 최신 버전순으로 조회
    List<IndividualPlan> findByCaseIdOrderByVersionNoDesc(UUID caseId);

    // 특정 사례에서 가장 높은 버전 번호 찾기
    Optional<IndividualPlan> findTopByCaseIdOrderByVersionNoDesc(UUID caseId);

    // caseId 로 제한
    Optional<IndividualPlan> findByIdAndCaseId(UUID id, UUID caseId);

    // active 계획 한 개 가져오기 (있다면)
    Optional<IndividualPlan> findFirstByCaseIdAndPlanStatusOrderByVersionNoDesc(UUID caseId, String planStatus);
}
