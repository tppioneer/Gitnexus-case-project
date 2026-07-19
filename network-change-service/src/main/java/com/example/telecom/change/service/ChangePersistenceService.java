package com.example.telecom.change.service;

import com.example.telecom.change.domain.ChangeRisk;
import com.example.telecom.change.domain.ChangeStatus;
import com.example.telecom.change.entity.NetworkChangeEntity;
import com.example.telecom.change.mapper.NetworkChangeMapper;
import com.example.telecom.change.repository.NetworkChangeJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Persistence service that works directly with the JPA repository.
 * Exercises {@code save}, {@code findByChangeId}, derived queries,
 * JPQL queries, and native queries end-to-end from a business service.
 */
@Service
public class ChangePersistenceService {

    private final NetworkChangeJpaRepository jpaRepository;
    private final NetworkChangeMapper mapper;

    public ChangePersistenceService(NetworkChangeJpaRepository jpaRepository,
                                    NetworkChangeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Transactional
    public NetworkChangeEntity save(NetworkChangeEntity entity) {
        return jpaRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public NetworkChangeEntity findByChangeId(String changeId) {
        return jpaRepository.findByChangeId(changeId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<NetworkChangeEntity> findByRegionAndStatus(String region, ChangeStatus status) {
        return jpaRepository.findByRegionCodeAndStatus(region, status);
    }

    @Transactional(readOnly = true)
    public List<NetworkChangeEntity> findRiskQueue(ChangeRisk risk, Collection<ChangeStatus> statuses) {
        return jpaRepository.findRiskQueue(risk, statuses);
    }

    @Transactional(readOnly = true)
    public List<NetworkChangeEntity> findByRegionNative(String region) {
        return jpaRepository.findByRegionNative(region);
    }

    @Transactional(readOnly = true)
    public List<NetworkChangeEntity> findByTitle(String title) {
        return jpaRepository.findByTitle(title);
    }

    @Transactional(readOnly = true)
    public List<NetworkChangeEntity> findActiveByRegion(String region) {
        return jpaRepository.findActiveByRegion(region);
    }

    @Transactional(readOnly = true)
    public List<NetworkChangeEntity> findByFamilyNative(String family) {
        return jpaRepository.findByFamilyNative(family);
    }
}
