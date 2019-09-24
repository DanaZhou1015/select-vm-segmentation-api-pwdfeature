package com.acxiom.ams.repository;

import com.acxiom.ams.model.po.UniverseIntegrationPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UniverseIntegrationPoJPA extends JpaRepository<UniverseIntegrationPo, Long> {
    UniverseIntegrationPo findByUniverseId(Long universeId);
    List<UniverseIntegrationPo> findAllByUniverseIdIn(List<Long> universeIdList);
}
