package com.flow.eum_backend.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiGenogramClient {

    private final RestTemplate aiRestTemplate;
    private final String aiApiBaseUrl;

    /*
        가계도 json 문자열을 Fast api에 보내서 SVG 바이너리를 받아옴
     */
    public byte[] renderGenogram(String genogramJson) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource resource = new ByteArrayResource(genogramJson.getBytes()) {
                @Override
                public String getFilename() {
                    return "genogram.json";
                }
            };

            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            String url = aiApiBaseUrl + "/genogram/render";

            ResponseEntity<byte[]> response = aiRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Genogram render failed: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Genogram 서버 호출 실패", e);
        }
    }
}
