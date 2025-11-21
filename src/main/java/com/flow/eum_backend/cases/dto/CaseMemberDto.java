package com.flow.eum_backend.cases.dto;

import com.flow.eum_backend.cases.CaseMember;

import java.util.UUID;

public record CaseMemberDto(
        UUID userId,
        String role,
        boolean isActive
) {
    public static CaseMemberDto fromEntity(CaseMember member) {
        return new CaseMemberDto(
                member.getUserId(),
                member.getRole(),
                member.isActive()
        );
    }
}
