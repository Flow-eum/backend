package com.flow.eum_backend.ai;

import com.flow.eum_backend.ai.dto.SttDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AiSttClient {

    private final RestTemplate aiRestTemplate;
    private final String aiApiBaseUrl;

    public SttDtos.SttResult transcribe(MultipartFile file) {
        try {
            // multipart/form-data 생성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null
                            ? file.getOriginalFilename()
                            : "audio.m4a";
                }
            };

            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            String url = aiApiBaseUrl + "/stt/transcribe";

            ResponseEntity<SttDtos.SttApiResponse> response = aiRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    SttDtos.SttApiResponse.class
            );

            SttDtos.SttApiResponse bodyObj = response.getBody();
            if (bodyObj == null || !bodyObj.isSuccess() || bodyObj.getData() == null) {
                throw new RuntimeException("STT 서버 오류: " +
                        (bodyObj != null ? bodyObj.getMessage() : "no response body"));
            }

            return bodyObj.getData().getStt();

        } catch (Exception e) {
            throw new RuntimeException("STT 호출 실패", e);
        }
    }
}
