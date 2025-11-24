package com.flow.eum_backend.profile;

import com.flow.eum_backend.profile.dto.SupervisorSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupervisorSearchService {

    private final ProfileRepository profileRepository;

    public List<SupervisorSummaryDto> searchSupervisors(String query) {
        String q = (query == null) ? "" : query.trim();
        if (q.isEmpty()) {

            return profileRepository.findByCanSuperviseTrueAndNameContainingIgnoreCase("")
                    .stream()
                    .map(SupervisorSummaryDto::fromEntity)
                    .toList();
        }

        return profileRepository.findByCanSuperviseTrueAndNameContainingIgnoreCase(q)
                .stream()
                .map(SupervisorSummaryDto::fromEntity)
                .toList();
    }
}
