package com.flow.eum_backend.profile;

import com.flow.eum_backend.profile.dto.ProfileDto;
import com.flow.eum_backend.profile.dto.ProfileUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
    현재 로그인한 사용자의 프로필을 조회/수정하는 api
 */
@RestController
@RequestMapping("/api/me/profile")
@Tag(name = "Profiles", description = "bearerAuth")
@SecurityRequirement(name = "bearerAuth") // Swagger에서 jwt 필요하다고 표시
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필을 반환합니다. 처음 호출 시 프로필이 없으면 자동으로 생성합니다.")
    public ResponseEntity<ProfileDto> getMyProfile() {
        ProfileDto dto = profileService.getOrCreateMyProfile();

        return ResponseEntity.ok(dto);
    }

    @PutMapping
    @Operation(summary = "내 프로필 수정", description = "현재 로그인한 사용자의 이름 및 기관 정보를 수정합니다.")
    public ResponseEntity<ProfileDto> updatedMyProfile(
            @RequestBody ProfileUpdateRequest request
    ) {
        ProfileDto dto = profileService.updateMyProfile(request);

        return ResponseEntity.ok(dto);
    }
}
