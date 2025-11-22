package com.flow.eum_backend.documents;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaseDocumentMetaRepository extends JpaRepository<CaseDocumentMeta, UUID> {

    // 사례별 문서 전체를 최신 수정순으로 조회
    List<CaseDocumentMeta> findByCaseIdOrderByUpdatedAtDesc(UUID caseId);

    // 사례 + 문서 타입별 필터링 조회
    List<CaseDocumentMeta> findByCaseIdAndDocumentTypeOrderByUpdatedAtDesc(UUID caseId, String documentType);

    // caseId까지 함께 조건으로 걸어서 안전하게 찾기
    Optional<CaseDocumentMeta> findByIdAndCaseId(UUID id, UUID caseId);
}
