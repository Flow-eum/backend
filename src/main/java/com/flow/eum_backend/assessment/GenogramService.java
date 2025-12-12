package com.flow.eum_backend.assessment;

import com.flow.eum_backend.ai.FastApiClient;
import com.flow.eum_backend.assessment.dto.GenogramPayload;
import com.flow.eum_backend.infra.SupabaseStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenogramService {

    private final AssessmentStructuredRepository assessmentRepository;
    private final FastApiClient fastApiClient;
    private final SupabaseStorageClient storageClient;
    private final GenogramConverter genogramConverter;

    @Transactional(readOnly = true)
    public String renderGenogramAndGetUrl(UUID caseId) {
        AssessmentStructured assessment = assessmentRepository
                .findFirstByCaseIdOrderByAssessmentDateDesc(caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 사례의 사정 기록을 찾을 수 없습니다."
                ));

        // 1) 사정기록(form) → GenogramPayload
        GenogramPayload payload = genogramConverter.fromAssessmentStructured(assessment);

        // 2) FastAPI 호출 (아래 4번에서 FastApiClient 방식에 따라 코드가 살짝 달라짐)
        byte[] svgBytes = fastApiClient.renderGenogram(payload);

        // 3) Supabase Storage 업로드
        String objectPath = storageClient.uploadGenogramSvg(caseId, svgBytes);

        // 4) Signed URL 생성
        return storageClient.createSignedGenogramUrl(objectPath, 3600);
    }
}
