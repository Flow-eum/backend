package com.flow.eum_backend.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flow.eum_backend.ai.dto.SttDtos;
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
    public byte[] renderGenogram(String genogramJson) {
        try {
            byte[] jsonBytes = genogramJson.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            MultipartBodyBuilder mb = new MultipartBodyBuilder();
            mb.part("file", new ByteArrayResource(jsonBytes) {
                        @Override
                        public String getFilename() {
                            return "genogram.json";
                        }
                    })
                    .contentType(MediaType.APPLICATION_JSON);

            Mono<byte[]> mono = client()
                    .post()
                    .uri("/genogram/render")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(mb.build()))
                    .retrieve()
                    .bodyToMono(byte[].class);

            return mono.block(Duration.ofMinutes(3));
        } catch (Exception e) {
            throw new RuntimeException("Genogram 렌더링 실패", e);
        }
    }
}
