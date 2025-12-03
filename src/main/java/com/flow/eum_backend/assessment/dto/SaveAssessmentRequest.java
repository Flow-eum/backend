package com.flow.eum_backend.assessment.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveAssessmentRequest {

    private String assessmentType;
    private LocalDate assessmentDate;
    private UUID preparedByUserId;

    private AssessmentFormDto form;
}
