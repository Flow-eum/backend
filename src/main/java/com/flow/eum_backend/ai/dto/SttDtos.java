package com.flow.eum_backend.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SttDtos {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SttResult {
        private String text;
        private String language;
        private Double duration;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SttDataWrapper {
        private SttResult stt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SttApiResponse {
        private boolean success;
        private String message;
        private SttDataWrapper data;
    }
}
