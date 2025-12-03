package com.flow.eum_backend.supervision;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupervisionRequestRepository extends JpaRepository<SupervisionRequest, UUID> {

    // B 입장에서, 나에게 들어온 PENDING 요청 목록
    List<SupervisionRequest> findBySupervisorUserIdAndStatusOrderByCreatedAtDesc(
            UUID supervisorUserId,
            SupervisionStatus status
    );

    // A 입장에서, 내가 보낸 요청 목록
    List<SupervisionRequest> findByRequesterUserIdOrderByCreatedAtDesc(UUID requesterUserId);

    /**
     * ***핵심 쿼리***
     * userId가 해당 case에 대해 APPROVED + 기간이 유효한 supervision을 갖고 있는지 여부
     *
     * 조건:
     *  - requester_user_id = :userId
     *  - case_id = :caseId
     *  - status = 'APPROVED'
     *  - (allowed_from <= now or null)
     *  - (allowed_until >= now or null)
     */
    @Query("""
        select
          case when count(r) > 0 then true else false end
        from SupervisionRequest r
        where r.caseId = :caseId
          and r.requesterUserId = :userId
          and r.status = com.flow.eum_backend.supervision.SupervisionStatus.APPROVED
          and (r.allowedFrom is null or r.allowedFrom <= :now)
          and (r.allowedUntil is null or r.allowedUntil >= :now)
        """)
    boolean hasActiveApprovedRequest(
            @Param("userId") UUID userId,
            @Param("caseId") UUID caseId,
            @Param("now") OffsetDateTime now
    );
}
