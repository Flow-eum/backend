package com.flow.eum_backend.profile.dto;

import com.flow.eum_backend.profile.Profile;

import java.util.UUID;

public record SupervisorSummaryDto(
        UUID id,
        String name,
        String organizationName,
        boolean canSupervise
) {
    public static SupervisorSummaryDto fromEntity(Profile p) {
        return new SupervisorSummaryDto(
                p.getId(),
                p.getName(),
                p.getOrganizationName(),
                p.isCanSupervise()
        );
    }
}
