package com.example.telecom.change.repository;

import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.ChangeStatus;
import com.example.telecom.change.entity.NetworkChangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@code NetworkChangeEntity}.
 *
 * Demonstrates:
 * - {@code JpaRepository<NetworkChangeEntity, Long>} generic binding
 * - derived queries (method-name based)
 * - {@code @Query} JPQL with named parameters
 * - native query
 * - named queries (declared on the entity via {@code @NamedQuery})
 */
public interface NetworkChangeJpaRepository extends JpaRepository<NetworkChangeEntity, Long> {

    /** Derived query — method name produces the WHERE clause. */
    List<NetworkChangeEntity> findByRegionCodeAndStatus(String regionCode, ChangeStatus status);

    /** Derived query by single field. */
    Optional<NetworkChangeEntity> findByChangeId(String changeId);

    /** Explicit JPQL with named parameters. */
    @Query("select c from NetworkChangeEntity c where c.risk = :risk and c.status in :statuses")
    List<NetworkChangeEntity> findRiskQueue(@Param("risk") ChangeRisk risk,
                                            @Param("statuses") Collection<ChangeStatus> statuses);

    /** Native SQL query. */
    @Query(value = "select * from network_change where region_code = :regionCode",
           nativeQuery = true)
    List<NetworkChangeEntity> findByRegionNative(@Param("regionCode") String regionCode);

    /**
     * Named native query reference — the SQL body lives on the entity via
     * {@code @NamedNativeQuery(name = "NetworkChangeEntity.findByFamilyNative", ...)}.
     * Spring Data recognizes the method name as matching a named native query.
     */
    List<NetworkChangeEntity> findByFamilyNative(@Param("family") String family);

    /**
     * Named query reference — the JPQL body lives on the entity via
     * {@code @NamedQuery(name = "NetworkChangeEntity.findByTitle", ...)}.
     * Spring Data recognizes the method name as matching a named query.
     */
    List<NetworkChangeEntity> findByTitle(String title);

    /** Named query for active changes in a region. */
    List<NetworkChangeEntity> findActiveByRegion(@Param("region") String region);
}
