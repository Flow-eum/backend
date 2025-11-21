package com.flow.eum_backend.cases;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/*
    cases 테이블용 Jpa 레포지토리
 */
public interface CaseRepository extends JpaRepository<CaseEntity, UUID> {

    // 나중에 displayCode로 조회하고 싶을 때를 대비한 메서드
    boolean existsByDisplayCode(String displayCode);

    CaseEntity findByDisplayCode(String displayCode);
}
