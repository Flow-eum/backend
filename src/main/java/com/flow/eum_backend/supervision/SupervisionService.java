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

    private final SupervisionRequestRepository requestRepository;
    private final SupervisionShareRepository shareRepository;
    private final CaseRepository caseRepository;
    private final CaseMemberRepository caseMemberRepository;
    private final SessionRecordMetaRepository sessionRecordMetaRepository;
    private final CaseDocumentMetaRepository caseDocumentMetaRepository;
    private final CurrentUser currentUser;

    /*
        특정 사례에 대해 슈퍼비전 요청 생성
     */
    @Transactional
    public SupervisionRequestDto createRequest(UUID caseId, SupervisionRequestCreateRequest request) {
        UUID userId = currentUser.getUserIdOrThrow();

        // 1) 사례 존재 여부 확인
        CaseEntity caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 사례를 찾을 수 없습니다."
                ));

        // 2) 이 사례에 대한 멤버인지(상담자인지) 확인
        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseEntity.getId(), userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "이 사례에 대해 슈퍼비전을 요청할 권한이 없습니다."
                ));

        // 3) 자기 자신에게 슈퍼비전 요청 보내는건 막기 (옵션)
        if (userId.equals(request.supervisorUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "자기 자신에게 슈퍼비전을 요청할 수 없습니다."
            );
        }

        OffsetDateTime now = OffsetDateTime.now();

        SupervisionRequest entity = SupervisionRequest.builder()
                .id(UUID.randomUUID())
                .caseId(caseEntity.getId())
                .requesterUserId(userId)
                .supervisorUserId(request.supervisorUserId())
                .status("pending")
                .reason(request.reason())
                .allowedFrom(request.allowedFrom())
                .allowedUntil(request.allowedUntil())
                .createdAt(now)
                .updatedAt(now)
                .build();

        requestRepository.save(entity);

        return SupervisionRequestDto.fromEntity(entity);
    }

    /*
        내가 요청자로 보낸 모든 슈퍼비전 요청 목록
     */
    @Transactional(readOnly = true)
    public List<SupervisionRequestDto> listMyRequestsAsRequester() {
        UUID userId = currentUser.getUserIdOrThrow();

        return requestRepository.findByRequesterUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(SupervisionRequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    /*
        내가 슈퍼바이저로 받은 모든 슈퍼비전 요청 목록
     */
    @Transactional(readOnly = true)
    public List<SupervisionRequestDto> listMyRequestsAsSupervisor() {
        UUID userId = currentUser.getUserIdOrThrow();

        return requestRepository.findBySupervisorUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(SupervisionRequestDto::fromEntity)
                .collect(Collectors.toList());
    }


    /*
        슈퍼비전 요청 상태 변경
     */
    @Transactional
    public SupervisionRequestDto updateRequestStatus(
            UUID requestId,
            SupervisionRequestUpdateStatusRequest request
    ) {
        UUID userId = currentUser.getUserIdOrThrow();

        SupervisionRequest entity = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 슈퍼비전 요청을 찾을 수 없습니다."
                ));

        String newStatus = request.status();

        // 간단한 권한/상태 체크 (MVP)
        switch (newStatus) {
            case "approved", "rejected" -> {
                // supervisor 만 가능
                if (!entity.getSupervisorUserId().equals(userId)) {
                    throw new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "승인/거절은 슈퍼바이저만 할 수 있습니다."
                    );
                }
                if (!"pending".equals(entity.getStatus())) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "pending 상태에서만 승인/거절이 가능합니다."
                    );
                }
                entity.setStatus(newStatus);
                // 승인 시 기간 수정 가능
                entity.setAllowedFrom(request.allowedFrom());
                entity.setAllowedUntil(request.allowedUntil());
            }
            case "revoked" -> {
                // requester 만 가능
                if (!entity.getRequesterUserId().equals(userId)) {
                    throw new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "요청 취소는 요청자만 할 수 있습니다."
                    );
                }
                if ("rejected".equals(entity.getStatus())) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "이미 거절된 요청은 취소할 수 없습니다."
                    );
                }
                entity.setStatus("revoked");
            }
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "지원하지 않는 상태 값입니다: " + newStatus
            );
        }

        entity.setUpdatedAt(OffsetDateTime.now());
        requestRepository.save(entity);

        return SupervisionRequestDto.fromEntity(entity);
    }

    /*
        특정 요청에 대해 share 생성
     */
    @Transactional
    public SupervisionShareDto createShare(
            UUID requestId,
            SupervisionShareCreateRequest request
    ) {
        UUID userId = currentUser.getUserIdOrThrow();

        SupervisionRequest supervisionRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 슈퍼비전 요청을 찾을 수 없습니다."
                ));

        // 요청 상태 확인
        if (!"approved".equals(supervisionRequest.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "승인된 요청에 대해서만 문서를 공유할 수 있습니다."
            );
        }

        // 요청자만 share 생성 가능
        if (!supervisionRequest.getRequesterUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "이 슈퍼비전 요청에 대해 share 를 생성할 권한이 없습니다."
            );
        }

        UUID caseId = supervisionRequest.getCaseId();

        // recordType / recordId 가 해당 case 에 속하는지 검증
        switch (request.recordType()) {
            case "session_record" -> {
                SessionRecordMeta session = sessionRecordMetaRepository.findById(request.recordId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "해당 회기 메타를 찾을 수 없습니다."
                        ));
                if (!session.getCaseId().equals(caseId)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "해당 회기는 이 슈퍼비전 요청의 사례에 속하지 않습니다."
                    );
                }
            }
            case "document" -> {
                CaseDocumentMeta doc = caseDocumentMetaRepository.findById(request.recordId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "해당 문서 메타를 찾을 수 없습니다."
                        ));
                if (!doc.getCaseId().equals(caseId)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "해당 문서는 이 슈퍼비전 요청의 사례에 속하지 않습니다."
                    );
                }
            }
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "지원하지 않는 recordType 입니다: " + request.recordType()
            );
        }

        OffsetDateTime now = OffsetDateTime.now();

        SupervisionShare share = SupervisionShare.builder()
                .id(UUID.randomUUID())
                .supervisionRequestId(supervisionRequest.getId())
                .recordType(request.recordType())
                .recordId(request.recordId())
                .wrappedDekForSupervisor(request.wrappedDekForSupervisor())
                .expiresAt(request.expiresAt())
                .revokedAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();

        shareRepository.save(share);

        return SupervisionShareDto.fromEntity(share);
    }

    /*
        특정 요청에 대한 share 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SupervisionShareDto> listShares(UUID requestId) {
        UUID userId = currentUser.getUserIdOrThrow();

        SupervisionRequest supervisionRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 슈퍼비전 요청을 찾을 수 없습니다."
                ));

        if (!supervisionRequest.getRequesterUserId().equals(userId) &&
                !supervisionRequest.getSupervisorUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "이 슈퍼비전 요청의 share 목록을 조회할 권한이 없습니다."
            );
        }

        return shareRepository.findBySupervisionRequestIdOrderByCreatedAtDesc(requestId)
                .stream()
                .map(SupervisionShareDto::fromEntity)
                .collect(Collectors.toList());
    }
}
