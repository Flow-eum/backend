package com.flow.eum_backend.profile;

import com.flow.eum_backend.auth.CurrentUser;
import com.flow.eum_backend.profile.dto.ProfileDto;
import com.flow.eum_backend.profile.dto.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/*
    profiles 관련 비즈니스 로직
    - 현재 로그인한 사용자 프로필을 조회할 때
    - DB에 없으면 기본 레코드 하나 생성
    - name, organizationName을 수정할 수 있는 api 제공
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final CurrentUser currentUser;

    @Transactional
    public ProfileDto getOrCreateMyProfile() {
        UUID userId = currentUser.getUserIdOrThrow();

        Profile profile = profileRepository.findById(userId)
                .orElseGet(() -> {
                    // 최초 로그인 사용자의 경우 여기로 들어옴.
                    Profile p = new Profile();
                    p.setId(userId);
                    p.setName(null);
                    p.setCanSupervise(false);
                    p.setOrganizationName(null);
                    p.setCreatedAt(OffsetDateTime.now());
                    p.setUpdatedAt(OffsetDateTime.now());

                    return profileRepository.save(p);
                });
        return ProfileDto.fromEntity(profile);
    }

    // 현재 로그인한 사용자의 프로필을 name / organizationName 기준으로 수정
    @Transactional
    public ProfileDto updateMyProfile(ProfileUpdateRequest request) {
        UUID userId = currentUser.getUserIdOrThrow();

        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Profile not found for current user"));

        profile.setName(request.name());
        profile.setOrganizationName(request.organizationName());
        profile.setUpdatedAt(OffsetDateTime.now());

        return ProfileDto.fromEntity(profile);
    }
}
