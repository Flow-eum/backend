package com.flow.eum_backend.plans;

import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.CaseEntity;
import com.flow.eum_backend.cases.CaseMemberRepository;
import com.flow.eum_backend.cases.CaseRepository;
import com.flow.eum_backend.plans.dto.IndividualPlanCreateRequest;
import com.flow.eum_backend.plans.dto.IndividualPlanDetailDto;
import com.flow.eum_backend.plans.dto.IndividualPlanSummaryDto;
import com.flow.eum_backend.plans.dto.IndividualPlanUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndividualPlanService {

    private final IndividualPlanRepository planRepository;
    private final CaseRepository caseRepository;
    private final CaseMemberRepository caseMemberRepository;
    private final CurrentUser currentUser;

    /*
        새 개입 계획 버전 생성
     */
    @Transactional
    public IndividualPlanDetailDto createPlan(UUID caseId, IndividualPlanCreateRequest request) {
        UUID userId = currentUser.getUserIdOrThrow();

        // 케이스 존재 확인
        CaseEntity caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 사례를 찾을 수 없습니다."
                ));

        // 권한 확인
        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "이 사례의 개입 계획을 작성할 권한이 없습니다."
                ));

        // version_no 계산
        int nextVersion = planRepository.findTopByCaseIdOrderByVersionNoDesc(caseId)
                .map(p -> p.getVersionNo() + 1)
                .orElse(1);

        OffsetDateTime now = OffsetDateTime.now();

        String status =(request.planStatus() == null || request.planStatus().isBlank())
                ? "draft"
                : request.planStatus();

        IndividualPlan plan = IndividualPlan.builder()
                .id(UUID.randomUUID())
                .caseId(caseEntity.getId())
                .title(request.title())
                .planStatus(status)
                .s3Key(request.s3Key())
                .contentSha256(request.contentSha256())
                .versionNo(nextVersion)
                .preparedByUserId(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        planRepository.save(plan);

        return IndividualPlanDetailDto.fromEntity(plan);
    }

    /*
        사례별 모든 개입 계획 목록 조회(버전 내림차순)
     */
    @Transactional(readOnly = true)
    public List<IndividualPlanSummaryDto> listPlans(UUID caseId) {
        UUID userId = currentUser.getUserIdOrThrow();

        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "이 사례의 개입 계획을 조회할 권한이 없습니다."
                ));

        return planRepository.findByCaseIdOrderByVersionNoDesc(caseId)
                .stream()
                .map(IndividualPlanSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /*
        단일 개입 계획 상세 조회
     */
    @Transactional(readOnly = true)
    public IndividualPlanDetailDto getPlan(UUID caseId, UUID planId) {
        UUID userId = currentUser.getUserIdOrThrow();

        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "이 사례의 개입 계획을 조회할 권한이 없습니다."
                ));

        IndividualPlan plan = planRepository.findByIdAndCaseId(planId, caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 개입 계획을 찾을 수 없습니다."
                ));

        return IndividualPlanDetailDto.fromEntity(plan);
    }

    /*
        plan_status, 제목, 파일 메타 수정
     */
    @Transactional
    public IndividualPlanDetailDto updatePlan(
            UUID caseId,
            UUID planId,
            IndividualPlanUpdateRequest request
    ) {
        UUID userId = currentUser.getUserIdOrThrow();

        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "이 사례의 개입 계획을 수정할 권한이 없습니다."
                ));

        IndividualPlan plan = planRepository.findByIdAndCaseId(planId, caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 개입 계획을 찾을 수 없습니다."
                ));

        if (request.title() != null) {
            plan.setTitle(request.title());
        }
        if (request.planStatus() != null) {
            plan.setPlanStatus(request.planStatus());
        }
        if (request.s3Key() != null) {
            plan.setS3Key(request.s3Key());
        }
        if (request.contentSha256() != null) {
            plan.setContentSha256(request.contentSha256());
        }

        plan.setUpdatedAt(OffsetDateTime.now());
        planRepository.save(plan);

        return IndividualPlanDetailDto.fromEntity(plan);
    }

    @Transactional(readOnly = true)
    public IndividualPlanDetailDto getActivePlan(UUID caseId) {
        UUID userId = currentUser.getUserIdOrThrow();

        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "이 사례의 개입 계획을 조회할 권한이 없습니다."
                ));

        IndividualPlan plan = planRepository
                .findFirstByCaseIdAndPlanStatusOrderByVersionNoDesc(caseId, "active")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "active 상태의 개입 계획이 없습니다."
                ));

        return IndividualPlanDetailDto.fromEntity(plan);
    }
}
