package com.flow.eum_backend.sessions.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveAiOutputRequest {

    /**
     * 어떤 기능의 결과인지 구분하는 키.
     * 예: "progress_report_md", "realtime_summary", "summary_report",
     *     "supervision_draft", "transcribed_note_md"
     */
    private String key;

    /**
     * Ollama가 돌려준 결과 그대로.
     * (마크다운 문자열이든, JSON 문자열이든 상관 없음)
     */
    private String value;
}
