package com.flow.eum_backend.profile.dto;

public record ProfileUpdateRequest(
        String name,
        String organizationName
) {
}
