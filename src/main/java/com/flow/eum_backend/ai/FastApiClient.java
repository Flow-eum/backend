package com.flow.eum_backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flow.eum_backend.ai.dto.CasePersonalDtos;
import com.flow.eum_backend.ai.dto.SttDtos;
import com.flow.eum_backend.assessment.dto.GenogramPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class FastApiClient {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${ai.api.base-url}")
    private String aiApiBaseUrl;

    private WebClient client() {
        return webClientBuilder
                .baseUrl(aiApiBaseUrl)
                .build();
    }

    /**
     * FastAPI /stt/transcribe 호출
     */
    public SttDtos.SttResult transcribe(byte[] audioBytes,
                                        String originalFilename,
                                        String contentType) {

        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        mb.part("file", new ByteArrayResource(audioBytes) {
                    @Override
                    public String getFilename() {
                        return originalFilename != null ? originalFilename : "audio.wav";
                    }
                })
                .contentType(contentType != null
                        ? MediaType.parseMediaType(contentType)
                        : MediaType.APPLICATION_OCTET_STREAM);

        Mono<SttDtos.SttApiResponse> mono = client()
                .post()
                .uri("/stt/transcribe")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(mb.build()))
                .retrieve()
                .bodyToMono(SttDtos.SttApiResponse.class);

        SttDtos.SttApiResponse resp = mono.block(Duration.ofMinutes(3));

        if (resp == null || !resp.isSuccess()
                || resp.getData() == null
                || resp.getData().getStt() == null) {
            throw new RuntimeException(
                    "STT 실패 또는 응답 포맷 오류: "
                            + (resp != null ? resp.getMessage() : "response is null")
            );
        }

        // data.stt 그대로 반환
        return resp.getData().getStt();
    }

    /**
     * FastAPI /genogram/render 호출
     * - 입력: genogram JSON 문자열
     * - 출력: SVG 바이너리
     */
    public byte[] renderGenogram(GenogramPayload payload) {
        Mono<byte[]> mono = client()
                .post()
                .uri("/genogram/render")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)   // WebClient가 내부에서 JSON으로 직렬화
                .retrieve()
                .bodyToMono(byte[].class);

        return mono.block(Duration.ofMinutes(3));
    }

    /*
        FastAPI /cases/personal 호출
     */
    public CasePersonalDtos.CasePersonalSaveResponse saveCasePersonal(JsonNode payload) {

        Mono<CasePersonalDtos.CasePersonalSaveResponse> mono = client()
                .post()
                .uri("/cases/personal")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(CasePersonalDtos.CasePersonalSaveResponse.class);

        CasePersonalDtos.CasePersonalSaveResponse resp = mono.block(Duration.ofSeconds(10));
        if (resp == null || !resp.isSuccess()) {
            throw new RuntimeException(
                    "개인정보 저장 실패: " + (resp != null ? resp.getMessage() : "response is null")
            );
        }
        return resp;
    }

    /*
        FastAPI /cases/similar
     */
    public CasePersonalDtos.CaseSimilarResponse findSimilarCases(String caseId, int topK) {

        JsonNode body = objectMapper.createObjectNode()
                .put("case_id", caseId)
                .put("top_k", topK);

        Mono<CasePersonalDtos.CaseSimilarResponse> mono = client()
                .post()
                .uri("/cases/similar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CasePersonalDtos.CaseSimilarResponse.class);

        CasePersonalDtos.CaseSimilarResponse resp = mono.block(Duration.ofSeconds(10));
        if (resp == null || !resp.isSuccess()) {
            throw new RuntimeException(
                    "유사사례 조회 실패: " + (resp != null ? resp.getMessage() : "response is null")
            );
        }
        return resp;
    }
}
