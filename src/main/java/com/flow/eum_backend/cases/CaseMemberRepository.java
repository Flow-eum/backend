package com.flow.eum_backend.cases;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
    case_members 테이블용 JPA 레포지토리
 */
public interface CaseMemberRepository extends JpaRepository<CaseMember, UUID> {

    // 특정 사용자가 멤버로 참여하고 있는 모든 case_id 리스트
    List<CaseMember> findByUserIdAndIsActiveTrue(UUID userId);

    // 한 케이스에 참여 중인 모든 멤버
    List<CaseMember> findByCaseIdAndIsActiveTrue(UUID caseId);

    // 이 케이스에 이 사용자가 멤버인지 체크
    Optional<CaseMember> findByCaseIdAndUserIdAndIsActiveTrue(UUID caseId, UUID userId);
}
