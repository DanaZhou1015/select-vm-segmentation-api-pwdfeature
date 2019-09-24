package com.acxiom.ams.repository;

import com.acxiom.ams.model.po.ProfilingPo;
import com.acxiom.ams.model.po.TenantPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 5:11 PM
 */
public interface ProfilingJPA extends JpaRepository<ProfilingPo, Long> {

    List<ProfilingPo> findProfilingPoByTenantPo(TenantPo tenantPo);

    ProfilingPo findProfilingPoByTenantPoAndName(TenantPo tenantPo, String name);

    ProfilingPo findProfilingPoByTenantPoAndId(TenantPo tenantPo, Long id);

    ProfilingPo findProfilingPoByTenantPoAndActive(TenantPo tenantPo, boolean active);

    @Modifying
    @Query("update ProfilingPo p set p.active = ?1 where p.tenantPo = ?2")
    void updateActiveByTenantPo(boolean active, TenantPo tenantPo);

    @Modifying
    @Query("update ProfilingPo p set p.active = ?1 where p.tenantPo = ?2 and p.id = ?3")
    void updateActiveByTenantPoAndId(boolean active, TenantPo tenantPo, Long id);

    ProfilingPo findByDestinationIdAndActive(Long destinationId,boolean active);

    @Modifying
    @Query("update ProfilingPo p set p.active = ?1 where p.destinationId = ?2")
    int updateActiveByDestinationId(boolean active, Long destinationId);

    ProfilingPo findPoByDestinationIdAndName(Long destinationId, String name);

    List<ProfilingPo> findByDestinationId(Long destinationId);

    ProfilingPo findPoByDestinationIdAndId(Long destinationId, Long id);
}
