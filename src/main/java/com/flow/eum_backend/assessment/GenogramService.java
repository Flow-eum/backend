package com.flow.eum_backend.assessment;

import com.flow.eum_backend.ai.FastApiClient;
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

    @Transactional(readOnly = true)
    public String renderGenogramAndGetUrl(UUID caseId) {
        AssessmentStructured assessment = assessmentRepository
                .findFirstByCaseIdOrderByAssessmentDateDesc(caseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 사례의 사정 기록을 찾을 수 없습니다."
                ));

        String genogramJson = assessment.getGenogramJson();
        if (genogramJson == null || genogramJson.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "해당 사례에는 가계도(genogram_json) 정보가 없습니다."
            );
        }

        // 2) FastAPI로 렌더링 요청 → SVG bytes
        byte[] svgBytes = fastApiClient.renderGenogram(genogramJson);

        // 3) Supabase Storage 업로드
        String objectPath = storageClient.uploadGenogramSvg(caseId, svgBytes);

        // 4) Signed URL 생성 (예: 1시간)
        return storageClient.createSignedGenogramUrl(objectPath, 3600);
    }
}
