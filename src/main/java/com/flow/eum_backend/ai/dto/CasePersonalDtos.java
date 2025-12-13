package com.flow.eum_backend.ai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CasePersonalDtos {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CasePersonalSaveResponse {
        private boolean success;
        private String message;
        private Data data;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Data {
            private Saved saved;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Saved {
            private String case_id;  // CSV에 저장된 케이스 ID
            private String payload;  // 문자열로 저장된 전체 JSON
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CaseSimilarResponse {
        private boolean success;
        private String message;
        private SimilarData data;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class SimilarData {
            private java.util.List<String> case_ids;
        }
    }
}
