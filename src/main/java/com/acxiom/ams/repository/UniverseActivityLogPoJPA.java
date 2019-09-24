package com.acxiom.ams.repository;

import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.UniverseActivityLogPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface UniverseActivityLogPoJPA extends JpaRepository<UniverseActivityLogPo, Long> {

    UniverseActivityLogPo findFirstByAudienceIdAndDestinationIdOrderByCreatedTimeDesc(Long audienceId, Long universeId);

    Page<UniverseActivityLogPo> findAll(Specification<UniverseActivityLogPo> specification, Pageable pageable);

    /**
     * get tenant id by owner tenant id
     *
     * @param id
     * @return
     */
    @Query(value = "SELECT DISTINCT(tenant_id) FROM universe_activity_log WHERE owner_tenant_id = ?1 and is_deleted = 0", nativeQuery = true)
    List<BigInteger> findDistinctTenantIdByOwnerTenantId(Long id);

    UniverseActivityLogPo findFirstByAudienceIdAndAudienceStatus(Long audienceId, SegmentStatusType audienceStatus);

    List<UniverseActivityLogPo> findByAudienceId(Long audienceId);
}
