package com.flow.eum_backend.sessions;

import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.CaseEntity;
import com.flow.eum_backend.cases.CaseMemberRepository;
import com.flow.eum_backend.cases.CaseRepository;
import com.flow.eum_backend.sessions.dto.SessionRecordCreateRequest;
import com.flow.eum_backend.sessions.dto.SessionRecordDetailDto;
import com.flow.eum_backend.sessions.dto.SessionRecordSummaryDto;
import com.flow.eum_backend.sessions.dto.SessionRecordUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/*
    상담 회차 관련 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class SessionRecordService {

    private final SessionRecordMetaRepository sessionRecordMetaRepository;
    private final CaseRepository caseRepository;
    private final CaseMemberRepository caseMemberRepository;
    private final CurrentUser currentUser;

    /*
        새 상담 회차 메타 생성
     */
    @Transactional
    public SessionRecordDetailDto createSessionRecord(UUID caseId, SessionRecordCreateRequest request) {
        UUID userId = currentUser.getUserIdOrThrow();

        // 케이스 존재 여부 확인
        CaseEntity caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 사례를 찾을 수 없습니다."
                ));

        // 권한 체크: 현재 사용자가 이 케이스의 멤버인지 확인
        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "이 사례의 상담 회차를 작성할 권한이 없습니다."
                ));

        // seq 결정
        int seq;
        if (request.seq() != null) {
            seq = request.seq();
        } else {
            int nextSeq = sessionRecordMetaRepository
                    .findTopByCaseIdOrderBySeqDesc(caseId)
                    .map(e -> e.getSeq() + 1)
                    .orElse(1);
            seq = nextSeq;
        }

        OffsetDateTime now = OffsetDateTime.now();

        SessionRecordMeta entity = SessionRecordMeta.builder()
                .id(UUID.randomUUID())
                .caseId(caseId)
                .seq(seq)
                .title(request.title())
                .sessionDate(request.sessionDate())
                .s3Key(request.s3Key())
                .contentSha256(request.contentSha256())
                .sizeBytes(request.sizeBytes())
                .version(1)
                .createdByUserId(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        sessionRecordMetaRepository.save(entity);

        return SessionRecordDetailDto.fromEntity(entity);
    }


    /*
        특정 사례의 모든 상담 회차 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SessionRecordSummaryDto> listSessionByCase(UUID caseId) {
        UUID userId = currentUser.getUserIdOrThrow();

        // 이 케이스의 멤버인지 확인
        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "이 사례의 상담 회차를 조회할 권한이 없습니다."
                ));

        List<SessionRecordMeta> list =
                sessionRecordMetaRepository.findByCaseIdOrderBySeqAsc(caseId);

        return list.stream()
                .map(SessionRecordSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }


    /*
        단일 회차 메타 상세 조회
     */
    @Transactional(readOnly = true)
    public SessionRecordDetailDto getSessionDetail(UUID caseId, UUID sessionId) {
        UUID userId = currentUser.getUserIdOrThrow();

        // 권한 체크
        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "이 사례의 상담 회차를 조회할 권한이 없습니다."
                ));

        SessionRecordMeta entity = sessionRecordMetaRepository
                .findByIdAndCaseId(sessionId, caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 상담 회차를 찾을 수 없습니다."
                ));

        return SessionRecordDetailDto.fromEntity(entity);
    }


    /*
        회차 메타 수정
     */
    public SessionRecordDetailDto updateSession(
            UUID caseId,
            UUID sessionId,
            SessionRecordUpdateRequest request
    ) {
        UUID userId = currentUser.getUserIdOrThrow();

        // 권한 체크
        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "이 사례의 상담 회차를 수정할 권한이 없습니다."
                ));

        SessionRecordMeta entity = sessionRecordMetaRepository
                .findByIdAndCaseId(sessionId, caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 상담 회차를 찾을 수 없습니다."
                ));

        // 필드 업데이트 (null 아닌 값만 덮어쓰기)
        if (request.title() != null) {
            entity.setTitle(request.title());
        }
        if (request.sessionDate() != null) {
            entity.setSessionDate(request.sessionDate());
        }
        if (request.s3Key() != null) {
            entity.setS3Key(request.s3Key());
        }
        if (request.contentSha256() != null) {
            entity.setContentSha256(request.contentSha256());
        }
        if (request.sizeBytes() != null) {
            entity.setSizeBytes(request.sizeBytes());
        }

        // 버전 올리기 (MVP: 수정 시마다 +1)
        entity.setVersion(entity.getVersion() + 1);

        entity.setUpdatedAt(OffsetDateTime.now());

        // JPA 는 엔티티가 이미 영속 상태라 save() 호출 필요는 없지만,
        // 명시적으로 호출해도 문제는 없다.
        sessionRecordMetaRepository.save(entity);

        return SessionRecordDetailDto.fromEntity(entity);
    }
}
