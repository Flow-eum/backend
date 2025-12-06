package com.flow.eum_backend.infra;

import lombok.RequiredArgsConstructor;
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

    private String supabaseBaseUrl;

    private String supabaseServiceRoleKey;

    private String audioBucket;

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

        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        mb.part("file", new ByteArrayResource(bytes) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                })
                .contentType(contentType != null
                        ? MediaType.parseMediaType(contentType)
                        : MediaType.APPLICATION_OCTET_STREAM);
        mb.part("path", objectPath);

        client()
                .post()
                .uri("/storage/v1/object/{bucket}", audioBucket)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(mb.build()))
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
