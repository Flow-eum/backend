package com.flow.eum_backend.sessions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
    session_record_meta 테이블용 JPA 레포지토리
 */
public interface SessionRecordMetaRepository extends JpaRepository<SessionRecordMeta, UUID> {

    // 특정 사례의 모든 회차를 seq 오름차순으로 조회
    List<SessionRecordMeta> findByCaseIdOrderBySeqAsc(UUID caseId);

    // 특정 사례에서 가장 큰 seq 값을 가진 회차를 찾기
    Optional<SessionRecordMeta> findTopByCaseIdOrderBySeqDesc(UUID caseId);

    // caseId + sessionId 함께 조회
    Optional<SessionRecordMeta> findByIdAndCaseId(UUID id, UUID caseId);
}
