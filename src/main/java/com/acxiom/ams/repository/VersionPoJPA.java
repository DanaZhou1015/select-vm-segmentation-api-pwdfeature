package com.acxiom.ams.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.acxiom.ams.model.em.TemplateStatusType;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.po.VersionPo;

/**
 * Created by cldong on 12/5/2017.
 */
public interface VersionPoJPA extends JpaRepository<VersionPo, Long> {
     VersionPo findById(Long id);

     VersionPo findFirstByTenantPoAndOperationFlag(TenantPo tenantPo,TemplateStatusType flag);

     VersionPo findFirstByTenantPoAndNameAndCreatedBy(TenantPo tenantPo, String tenantName, String userId);

     Page<VersionPo> findAll(Specification<VersionPo> spec, Pageable pageable);

     List<VersionPo> findAllByTenantPoInAndOperationFlag(TenantPo[] tenantPoList, TemplateStatusType flag);

    @Modifying
    @Query("update VersionPo v set v.operationFlag = ?1 where v.id = ?2")
     void updateOperationFlagById(TemplateStatusType flag, Long versionId);

    @Modifying
    @Query("update VersionPo v set v.operationFlag = ?1 where v.tenantPo.id = ?2")
     void updateOperationFlagByTenantId(TemplateStatusType flag, Long tenantId);

    @Modifying
    @Query("update VersionPo v set v.treeId = ?1 where v.id = ?2")
     void updateTreeIdById(String treeId, Long versionId);

    @Modifying
    @Query("update VersionPo v set v.datasourceId = ?1 where v.id = ?2")
    void updateDatasourceIdById(String datasourceId, Long versionId);

     List<VersionPo> findAllByTenantPoOrderByUpdateTimeDesc(TenantPo tenantPo);

     List<VersionPo> findAllByTenantPoAndAndIdIn(TenantPo tenantPo, List<Long> idList);

     List<VersionPo> findByTenantPoAndName(TenantPo tenantPo, String name);

     List<VersionPo> findByTenantPoAndOperationFlagIn(TenantPo tenantPo,  TemplateStatusType[] operationFlag);

    @Modifying
    @Query("update VersionPo v set v.syncFlag = ?1 where v.id = ?2")
     void updateSyncFlagById(Integer flag, Long versionId);

    List<VersionPo> findByDatasourceIdNotNull();

}
