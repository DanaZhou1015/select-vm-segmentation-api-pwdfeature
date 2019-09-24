package com.acxiom.ams.repository;

import com.acxiom.ams.model.po.TenantPo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by cldong on 12/5/2017.
 */
public interface TenantPoJPA extends JpaRepository<TenantPo, Long> {
    TenantPo findTenantPoById(Long id);

    TenantPo findTenantPoByTenantId(String tenantId);

    List<TenantPo> findByNameIn(List<String> tenantNameList);

    TenantPo findFirstByPath(String tenantPath);
}
