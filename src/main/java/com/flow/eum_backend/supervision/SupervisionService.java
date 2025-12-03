package com.flow.eum_backend.supervision;

import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.CaseEntity;
import com.flow.eum_backend.cases.CaseMemberRepository;
import com.flow.eum_backend.cases.CaseRepository;
import com.flow.eum_backend.documents.CaseDocumentMeta;
import com.flow.eum_backend.documents.CaseDocumentMetaRepository;
import com.flow.eum_backend.sessions.SessionRecordMeta;
import com.flow.eum_backend.sessions.SessionRecordMetaRepository;
import com.flow.eum_backend.supervision.dto.*;
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
public class SupervisionService {

    private final SupervisionRequestRepository supervisionRequestRepository;
    private final CurrentUser currentUser;

    /*
        A가 B에게 특정 case 열람 요청 보내기
     */
    @Transactional
    public SupervisionRequestResponse requestView(SupervisionRequestCreateRequest req) {
        UUID requesterId = currentUser.getUserIdOrThrow();

        SupervisionRequest entity = SupervisionRequest.builder()
                .caseId(req.caseId())
                .requesterUserId(requesterId)
                .supervisorUserId(req.supervisorUserId())
                .status(SupervisionStatus.PENDING)
                .reason(req.reason())
                .build();

        supervisionRequestRepository.save(entity);

        return SupervisionRequestResponse.fromEntity(entity);
    }

    /*
        B입장에서, 나에게 들어온 PENDING 요청 목록
     */
    @Transactional(readOnly = true)
    public List<SupervisionRequestResponse> listIncomingPending() {
        UUID supervisorId = currentUser.getUserIdOrThrow();

        return supervisionRequestRepository
                .findBySupervisorUserIdAndStatusOrderByCreatedAtDesc(
                        supervisorId,
                        SupervisionStatus.PENDING
                )
                .stream()
                .map(SupervisionRequestResponse::fromEntity)
                .toList();
    }

    /*
        A 입장에서, 내가 보낸 요청 목록
     */
    @Transactional(readOnly = true)
    public List<SupervisionRequestResponse> listMyRequests() {
        UUID requesterId = currentUser.getUserIdOrThrow();

        return supervisionRequestRepository
                .findByRequesterUserIdOrderByCreatedAtDesc(requesterId)
                .stream()
                .map(SupervisionRequestResponse::fromEntity)
                .toList();
    }

    /*
        B가 승인
     */
    @Transactional
    public SupervisionRequestResponse approve(
            UUID requestId,
            OffsetDateTime allowedFrom,
            OffsetDateTime allowedUntil
    ) {
        UUID supervisorId = currentUser.getUserIdOrThrow();

        SupervisionRequest entity = supervisionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "요청을 찾을 수 없습니다."
                ));

        if (!entity.getSupervisorUserId().equals(supervisorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "승인 권한이 없습니다.");
        }

        entity.setStatus(SupervisionStatus.APPROVED);
        entity.setAllowedFrom(allowedFrom != null ? allowedFrom : OffsetDateTime.now());
        entity.setAllowedUntil(allowedUntil);

        return SupervisionRequestResponse.fromEntity(entity);
    }

    /*
        B가 거절
     */
    @Transactional
    public SupervisionRequestResponse reject(UUID requestId) {
        UUID supervisorId = currentUser.getUserIdOrThrow();

        SupervisionRequest entity = supervisionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "요청을 찾을 수 없습니다."
                ));

        if (!entity.getSupervisorUserId().equals(supervisorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "거절 권한이 없습니다.");
        }

        entity.setStatus(SupervisionStatus.REJECTED);
        entity.setAllowedFrom(null);
        entity.setAllowedUntil(null);

        return SupervisionRequestResponse.fromEntity(entity);
    }

    /*
        현재 userId가 이 caseId를 supervision 기반으로 열람 가능 여부
     */
    @Transactional(readOnly = true)
    public boolean hasSupervisionAccess(UUID userId, UUID caseId) {
        return supervisionRequestRepository.hasActiveApprovedRequest(
                userId,
                caseId,
                OffsetDateTime.now()
        );
    }
}
