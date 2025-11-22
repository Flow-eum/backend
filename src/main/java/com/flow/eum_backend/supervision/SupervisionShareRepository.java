package com.flow.eum_backend.supervision;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupervisionShareRepository extends JpaRepository<SupervisionShare, UUID> {

    List<SupervisionShare> findBySupervisionRequestIdOrderByCreatedAtDesc(UUID requestId);
}
