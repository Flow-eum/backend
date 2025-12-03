package com.flow.eum_backend.sessions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.CaseEntity;
import com.flow.eum_backend.cases.CaseMemberRepository;
import com.flow.eum_backend.cases.CaseRepository;
import com.flow.eum_backend.sessions.dto.*;
import com.flow.eum_backend.supervision.SupervisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;
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
    private final ObjectMapper objectMapper;
    private final SupervisionService supervisionService;

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
        checkCaseAccessOrThrow(caseId, userId, "이 사례의 상담 회차를 조회할 권한이 없습니다.");

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
        checkCaseAccessOrThrow(caseId, userId, "이 사례의 상담 회차를 조회할 권한이 없습니다.");

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

    /*
        세션별 Ai 출력 저장
     */
    @Transactional
    public void saveAiOutput(UUID caseId, UUID sessionId, SaveAiOutputRequest request) {
        UUID userId = currentUser.getUserIdOrThrow();

        // 케이스 멤버 권한 체크
        checkCaseAccessOrThrow(caseId, userId, "이 사례의 AI 결과를 저장할 권한이 없습니다.");

        SessionRecordMeta meta = sessionRecordMetaRepository
                .findByIdAndCaseId(sessionId, caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 상담 회차를 찾을 수 없습니다."
                ));

        Map<String, Object> outputs = parseAiOutputs(meta.getAiOutputs());
        outputs.put(request.getKey(), request.getValue());

        String json = writeAiOutputs(outputs);
        meta.setAiOutputs(json);
    }

    /*
        세션멸 Ai 출력 전체 조회
     */
    @Transactional(readOnly = true)
    public AiOutputsResponse getAiOutputs(UUID caseId, UUID sessionId) {
        UUID userId = currentUser.getUserIdOrThrow();

        checkCaseAccessOrThrow(caseId, userId, "이 사례의 AI 결과를 조회할 권한이 없습니다.");

        SessionRecordMeta meta = sessionRecordMetaRepository
                .findByIdAndCaseId(sessionId, caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 상담 회차를 찾을 수 없습니다."
                ));

        Map<String, Object> outputs = parseAiOutputs(meta.getAiOutputs());

        return new AiOutputsResponse(outputs);
    }

    /*
        세션별 Ai 출력 중 특정 key만 조회
     */
    public Optional<Object> getAiOutputByKey(UUID caseId, UUID sessionId, String key) {
        AiOutputsResponse all = getAiOutputs(caseId, sessionId);

        return Optional.ofNullable(all.getOutputs().get(key));
    }

    private Map<String, Object> parseAiOutputs(String json) {
        if (json == null || json.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            // JSON이 깨져 있어도 서비스 전체가 죽지 않도록 방어
            return new HashMap<>();
        }
    }

    private String writeAiOutputs(Map<String, Object> outputs) {
        try {
            return objectMapper.writeValueAsString(outputs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize ai_outputs", e);
        }
    }

    private void checkCaseAccessOrThrow(UUID caseId, UUID userId, String messageIfForbidden) {
        boolean isMember = caseMemberRepository
                .findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .isPresent();

        boolean hasSupervision = supervisionService.hasSupervisionAccess(userId, caseId);

        if (!isMember && !hasSupervision) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageIfForbidden);
        }
    }
}
