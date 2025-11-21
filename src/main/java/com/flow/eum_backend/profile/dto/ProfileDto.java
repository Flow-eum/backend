package com.flow.eum_backend.profile.dto;

import com.flow.eum_backend.profile.Profile;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProfileDto(
        UUID id,
        String name,
        boolean canSupervise,
        String organizationName,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static ProfileDto fromEntity(Profile p) {
        return new ProfileDto(
                p.getId(),
                p.getName(),
                p.isCanSupervise(),
                p.getOrganizationName(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
