package com.flow.eum_backend.documents;

import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.cases.CaseEntity;
import com.flow.eum_backend.cases.CaseMemberRepository;
import com.flow.eum_backend.cases.CaseRepository;
import com.flow.eum_backend.documents.dto.CaseDocumentCreateRequest;
import com.flow.eum_backend.documents.dto.CaseDocumentDetailDto;
import com.flow.eum_backend.documents.dto.CaseDocumentSummaryDto;
import com.flow.eum_backend.documents.dto.CaseDocumentUpdateRequest;
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
    사례 문서(case_document_meta) 관련 바즈니스 로작
 */
@Service
@RequiredArgsConstructor
public class CaseDocumentService {

    private final CaseDocumentMetaRepository documentRepository;
    private final CaseRepository caseRepository;
    private final CaseMemberRepository caseMemberRepository;
    private final CurrentUser currentUser;

    /*
        문서 메타 생성
     */
    public CaseDocumentDetailDto createDocument(UUID caseId, CaseDocumentCreateRequest request) {
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
                        "이 사례 문서를 등록할 권한이 없습니다."
                ));

        OffsetDateTime now = OffsetDateTime.now();

        CaseDocumentMeta entity = CaseDocumentMeta.builder()
                .id(UUID.randomUUID())
                .caseId(caseEntity.getId())
                .documentType(request.documentType())
                .title(request.title())
                .s3Key(request.s3Key())
                .contentSha256(request.contentSha256())
                .sizeBytes(request.sizeBytes())
                .version(1)
                .createdByUserId(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        documentRepository.save(entity);

        return CaseDocumentDetailDto.fromEntity(entity);
    }

    /*
        사례별 문서 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CaseDocumentSummaryDto> listDocuments(UUID caseId, String documentType) {
        UUID userId = currentUser.getUserIdOrThrow();

        // 권한 확인
        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "이 사례 문서를 조회할 권한이 없습니다."
                ));

        List<CaseDocumentMeta> list;

        if (documentType == null || documentType.isBlank()) {
            list = documentRepository.findByCaseIdOrderByUpdatedAtDesc(caseId);
        } else {
            list = documentRepository.findByCaseIdAndDocumentTypeOrderByUpdatedAtDesc(caseId, documentType);
        }

        return list.stream()
                .map(CaseDocumentSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /*
        단일 문서 상세 조회
     */
    @Transactional(readOnly = true)
    public CaseDocumentDetailDto getDocument(UUID caseId, UUID documentId) {
        UUID userId = currentUser.getUserIdOrThrow();

        // 권한 확인
        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "이 사례 문서를 조회할 권한이 없습니다."
                ));

        CaseDocumentMeta entity = documentRepository
                .findByIdAndCaseId(documentId, caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 문서를 찾을 수 없습니다."
                ));

        return CaseDocumentDetailDto.fromEntity(entity);
    }

    /*
        문서 메타 수정
     */
    @Transactional
    public CaseDocumentDetailDto updateDocument(
            UUID caseId,
            UUID documentId,
            CaseDocumentUpdateRequest request
    ) {
        UUID userId = currentUser.getUserIdOrThrow();

        // 권한 확인
        caseMemberRepository.findByCaseIdAndUserIdAndIsActiveTrue(caseId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "이 사례 문서를 수정할 권한이 없습니다."
                ));

        CaseDocumentMeta entity = documentRepository
                .findByIdAndCaseId(documentId, caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 문서를 찾을 수 없습니다."
                ));

        // null 아닌 필드만 반영
        if (request.title() != null) {
            entity.setTitle(request.title());
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

        // 수정 시 버전 증가
        entity.setVersion(entity.getVersion() + 1);
        entity.setUpdatedAt(OffsetDateTime.now());

        documentRepository.save(entity);

        return CaseDocumentDetailDto.fromEntity(entity);
    }
}
