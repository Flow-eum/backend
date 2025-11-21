package com.flow.eum_backend.sessions.dto;

import java.time.LocalDate;

public record SessionRecordUpdateRequest(
        String title,
        LocalDate sessionDate,
        String s3Key,
        String contentSha256,
        Long sizeBytes
) {
}
