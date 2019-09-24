package com.acxiom.ams.repository;

import com.acxiom.ams.model.em.UniverseType;
import com.acxiom.ams.model.po.UniversePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UniversePoJPA extends JpaRepository<UniversePo, Long> {
    UniversePo findByIdAndTenantId(Long universeId, Long tenantId);
    UniversePo findByUniverseSystemNameAndTenantPath(String universeSystemName, String tenantPath);
    List<UniversePo> findByUniverseNameOrUniverseSystemName(String universeName, String universeSystemName);
    UniversePo findByUniverseNameAndTenantPath(String universeName, String tenantPath);
    List<UniversePo> findAllByTenantId(Long tenantId);
    UniversePo findByTenantIdAndUniverseType(Long tenantId, UniverseType universeType);
    UniversePo findByUniverseSystemNameAndTenantPathAndOwnerTenantPath(String universeSystemName, String tenantPath, String ownerTenantPath);
    List<UniversePo> findByUniverseSystemNameAndOwnerTenantPath(String universeSystemName, String ownerTenantPath);
    List<UniversePo> findAllByAndOwnerTenantPath(String tenantPath);
}
