package com.flow.eum_backend.supervision;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SupervisionRequestRepository extends JpaRepository<SupervisionRequest, UUID> {

    // 내가 요청자료 보낸 슈퍼비전 요청들
    List<SupervisionRequest> findByRequesterUserIdOrderByCreatedAtDesc(UUID requesterUserId);

    // 내가 슈퍼바이저로 받은 슈퍼비전 요청들
    List<SupervisionRequest> findBySupervisorUserIdOrderByCreatedAtDesc(UUID supervisorUserId);

    // caseId로 제한하면서 찾을 때 사용
    Optional<SupervisionRequest> findByIdAndCaseId(UUID id, UUID caseId);
}
