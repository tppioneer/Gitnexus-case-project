package com.example.telecom.change.repository;

import com.example.telecom.change.entity.ChangeAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@code ChangeAuditEntity}.
 */
public interface ChangeAuditJpaRepository extends JpaRepository<ChangeAuditEntity, Long> {

    /** Derived query by change reference. */
    List<ChangeAuditEntity> findByChangeId(Long changeId);

    /** Derived query by action. */
    List<ChangeAuditEntity> findByAction(String action);
}
