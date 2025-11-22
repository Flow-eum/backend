package com.flow.eum_backend.cases;

import com.flow.eum_backend.audit.AuditLogService;
import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.dto.CaseCreateRequest;
import com.flow.eum_backend.cases.dto.CaseDetailDto;
import com.flow.eum_backend.cases.dto.CaseMemberDto;
import com.flow.eum_backend.cases.dto.CaseSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.DispatcherServlet;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/*
    사례(cases) 관련 핵심 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseMemberRepository caseMemberRepository;
    private final CurrentUser currentUser;
    private final AuditLogService auditLogService;

    /*
        새 사례 생성
     */
    @Transactional
    public CaseDetailDto createCase(CaseCreateRequest request) {
        UUID userId = currentUser.getUserIdOrThrow();
        OffsetDateTime now = OffsetDateTime.now();

        String displayCode = request.displayCode();
        if (displayCode == null || displayCode.isBlank()) {
            displayCode = generateDisplayCode();
        }

        // 혹시 중목되면 단순히 예외 던짐
        if (caseRepository.existsByDisplayCode(displayCode)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "이미 사례 중인 사례 코드입니다: " + displayCode
            );
        }

        CaseEntity entity = CaseEntity.builder()
                .id(UUID.randomUUID())
                .displayCode(displayCode)
                .title(request.title())
                .status("open")
                .createdByUserId(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        caseRepository.save(entity);

        CaseMember member = CaseMember.builder()
                .id(UUID.randomUUID())
                .caseId(entity.getId())
                .userId(userId)
                .role("primary_counselor")
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        caseMemberRepository.save(member);

        auditLogService.logCurrentUserAction(
                "CREATE_CASE",
                "case",
                entity.getId(),
                Map.of(
                        "displayCode", entity.getDisplayCode(),
                        "title", entity.getTitle()
                )
        );

        List<CaseMemberDto> members = List.of(CaseMemberDto.fromEntity(member));

        return CaseDetailDto.fromEntity(entity, members);
    }

    /*
        - 내가 멤버로 참여 중인 케이스 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CaseSummaryDto> listMyCases() {
        UUID userId = currentUser.getUserIdOrThrow();

        List<CaseMember> memberships =
                caseMemberRepository.findByUserIdAndIsActiveTrue(userId);

        List<UUID> caseIds = memberships.stream()
                .map(CaseMember::getCaseId)
                .distinct()
                .toList();

        if (caseIds.isEmpty()) {
            return List.of();
        }

        List<CaseEntity> cases = caseRepository.findAllById(caseIds);

        return cases.stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .map(CaseSummaryDto::fromEntity)
                .toList();
    }


    /*
        - 단일 케이스 상세 조회
     */
    @Transactional(readOnly = true)
    public CaseDetailDto getCaseDetail(UUID caseId) {
        UUID userId = currentUser.getUserIdOrThrow();

        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "이 사례에 접근할 권한이 없습니다."
                ));

        CaseEntity entity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 사례를 찾을 수 없습니다."
                ));

        List<CaseMemberDto> members = caseMemberRepository
                .findByCaseIdAndIsActiveTrue(caseId)
                .stream()
                .map(CaseMemberDto::fromEntity)
                .collect(Collectors.toList());

        auditLogService.logCurrentUserAction(
                "VIEW_CASE_DETAIL",
                "case",
                caseId,
                Map.of(
                        "displayCode", entity.getDisplayCode(),
                        "title", entity.getTitle()
                )
        );
        return CaseDetailDto.fromEntity(entity, members);
    }

     // 매우 단순한 사례 코드 생성 로직
    private String generateDisplayCode() {
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CASE-" + random;
    }
}
