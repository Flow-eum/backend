package com.flow.eum_backend.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    // can_supervise = true 이면서, 이름에 keyword 가 포함된 상담사들 검색
    List<Profile> findByCanSuperviseTrueAndNameContainingIgnoreCase(String namePart);
}
