package com.flow.eum_backend.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SupabaseStorageClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${supabase.base-url}")
    private String supabaseBaseUrl;

    @Value("${supabase.service-role-key}")
    private String supabaseServiceRoleKey;

    @Value("${supabase.storage.audio-bucket}")
    private String audioBucket;

    @Value("${supabase.storage.genogram-bucket}")
    private String genogramBucket;

    private WebClient client() {
        return webClientBuilder
                .baseUrl(supabaseBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseServiceRoleKey)
                .defaultHeader("apikey", supabaseServiceRoleKey)
                .build();
    }

    /*
        세션 녹음 파일 업로드
     */
    public String uploadSessionAudio(UUID caseId,
                                     UUID sessionId,
                                     String filename,
                                     byte[] bytes,
                                     String contentType) {

        String objectPath = "cases/%s/sessions/%s/%s".formatted(caseId, sessionId, filename);

        String fullUri = "/storage/v1/object/" + audioBucket + "/" + objectPath;

        // 3. API 호출
        client()
                .post()
                .uri(fullUri)
                // [해결책 1] 이미 파일이 있어도 에러 내지 말고 덮어써라! (이게 없으면 400 에러 자주 남)
                .header("x-upsert", "true")
                // [해결책 2] 타입을 명확하게 '바이너리 스트림'으로 고정 (가장 안전함)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(bytes)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofMinutes(1));

        return objectPath;
    }

    /*
        가계도 SVG 업로드
     */
    public String uploadGenogramSvg(UUID caseId, byte[] svgBytes) {
        String filename = UUID.randomUUID() + ".svg";
        String objectPath = "cases/%s/%s".formatted(caseId, filename);

        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        mb.part("file", new ByteArrayResource(svgBytes) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                })
                .contentType(MediaType.valueOf("image/svg+xml"));
        mb.part("path", objectPath);

        client()
                .post()
                .uri("/storage/v1/object/{bucket}", genogramBucket)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(mb.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofMinutes(1));

        return objectPath;
    }

    /*
        Presigned URL 생성
     */
    public String createSignedUrl(String bucket, String objectPath, long expiresInSeconds) {
        Map<String, Object> body = Map.of(
                "expiresIn", expiresInSeconds,
                "paths", List.of(objectPath)
        );

        List<Map<String, Object>> resp = client()
                .post()
                .uri("/storage/v1/object/sign/{bucket}", bucket)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(List.class)
                .block(Duration.ofSeconds(10));

        if (resp == null || resp.isEmpty()) {
            throw new RuntimeException("Signed URL 생성 실패");
        }

        Object signedUrl = resp.get(0).get("signedURL");
        if (signedUrl == null) {
            signedUrl = resp.get(0).get("signedUrl");
        }
        if (signedUrl == null) {
            throw new RuntimeException("Signed URL 응답 포맷 오류: " + resp);
        }

        return supabaseBaseUrl + signedUrl.toString();
    }

    public String createSignedGenogramUrl(String objectPath, long expiresInSeconds) {
        return createSignedUrl(genogramBucket, objectPath, expiresInSeconds);
    }

    public String createSignedAudioUrl(String objectPath, long expiresInSeconds) {
        return createSignedUrl(audioBucket, objectPath, expiresInSeconds);
    }
}
