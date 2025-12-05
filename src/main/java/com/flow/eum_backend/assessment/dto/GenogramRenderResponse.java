package com.flow.eum_backend.assessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenogramRenderResponse {
    private String url; // Supabase signed URL
}
