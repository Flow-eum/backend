package com.flow.eum_backend.ai;

import com.flow.eum_backend.ai.dto.CasePersonalDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SimilarCaseService {

    private final FastApiClient fastApiClient;

    public CasePersonalDtos.CaseSimilarResponse getSimilarCases(UUID caseId, int topk) {

        return fastApiClient.findSimilarCases(caseId.toString(), topk);
    }
}
