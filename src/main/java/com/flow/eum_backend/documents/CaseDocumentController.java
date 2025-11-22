package com.flow.eum_backend.documents;

import com.flow.eum_backend.documents.dto.CaseDocumentCreateRequest;
import com.flow.eum_backend.documents.dto.CaseDocumentDetailDto;
import com.flow.eum_backend.documents.dto.CaseDocumentSummaryDto;
import com.flow.eum_backend.documents.dto.CaseDocumentUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*
    사례 문서(case_document_meta) 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/cases/{caseId}/documents")
@Tag(name = "CaseDocuments", description = "사례 문서 메타 API")
@SecurityRequirement(name = "bearerAuth")
public class CaseDocumentController {

    private final CaseDocumentService documentService;

    public CaseDocumentController(CaseDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    @Operation(
            summary = "사례 문서 메타 생성",
            description = """
                    특정 사례에 문서를 등록합니다.
                    - 실제 파일은 클라이언트에서 암호화 후 Supabase Storage 등에 업로드한 상태여야 하며,
                      이 API에는 s3Key / contentSha256 / sizeBytes 같은 메타 정보만 전달합니다.
                    """
    )
    public ResponseEntity<CaseDocumentDetailDto> createDocument(
            @PathVariable("caseId") UUID caseId,
            @RequestBody CaseDocumentCreateRequest request
    ) {
        return ResponseEntity.ok(documentService.createDocument(caseId, request));
    }

    @GetMapping
    @Operation(
            summary = "사례 문서 목록 조회",
            description = "해당 사례에 등록된 문서 목록을 반환합니다. documentType 으로 필터링할 수 있습니다."
    )
    public ResponseEntity<List<CaseDocumentSummaryDto>> listDocuments(
            @PathVariable("caseId") UUID caseId,
            @RequestParam(name = "documentType", required = false) String documentType
    ) {
        return ResponseEntity.ok(documentService.listDocuments(caseId, documentType));
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "단일 문서 메타 조회")
    public ResponseEntity<CaseDocumentDetailDto> getDocument(
            @PathVariable("caseId") UUID caseId,
            @PathVariable("documentId") UUID documentId
    ) {
        return ResponseEntity.ok(documentService.getDocument(caseId, documentId));
    }

    @PatchMapping("/{documentId}")
    @Operation(summary = "문서 메타 수정")
    public ResponseEntity<CaseDocumentDetailDto> updateDocument(
            @PathVariable("caseId") UUID caseId,
            @PathVariable("documentId") UUID documentId,
            @RequestBody CaseDocumentUpdateRequest request
    ) {
        return ResponseEntity.ok(documentService.updateDocument(caseId, documentId, request));
    }
}
