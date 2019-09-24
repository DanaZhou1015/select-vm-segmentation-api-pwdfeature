package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.model.dto.VersionDTO;
import com.acxiom.ams.model.dto.v2.VersionDTOCreate;
import com.acxiom.ams.model.dto.v2.VersionDTOUpdateTaxonomy;
import com.acxiom.ams.model.dto.v2.VersionDatasourceDTO;
import com.acxiom.ams.model.dto.v2.VersionDtoDelete;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.po.VersionPo;
import com.acxiom.ams.model.vo.VersionPageVo;
import com.acxiom.ams.model.vo.VersionVo;

import java.util.List;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 3:47 PM
 */
public interface VersionPoService {

    VersionPageVo findVersionByTenantIdAndPage(Long tenantId, Integer page, Integer pageSize) throws AMSInvalidInputException;

    VersionPo findVersionById(Long tenantId, Long versionId) throws AMSInvalidInputException;

    void setVersionFlagActive(Long tenantId, Long versionId) throws AMSInvalidInputException;

    void updateVersionTreeId(Long tenantId, Long versionId, String treeId) throws AMSInvalidInputException;

    void deleteVersionById(Long tenantId, Long versionId) throws AMSInvalidInputException;


    long saveVersion(Long tenantId, VersionDTO versionDTO) throws AMSInvalidInputException;

    VersionPo getVersionById(Long versionId) throws AMSInvalidInputException;

    //add for new version table
    List<VersionVo> findByTenant(Long tenantId) throws AMSInvalidInputException;

    VersionVo findByTenantAndId(Long tenantId, Long versionId) throws AMSInvalidInputException;

    Long createVersion(VersionDTOCreate versionDTOCreate, Long tenantId) throws AMSInvalidInputException;

    void updateVersion(VersionDTOUpdateTaxonomy versionDTOUpdateTaxonomy, Long tenantId)
            throws AMSException;

    void updateVersionFlag(Long tenantId, Long versionId, Integer activeFlag)
            throws AMSInvalidInputException;

    void deleteVersionByIdList(Long tenantId, VersionDtoDelete versionDtoDelete)
            throws AMSInvalidInputException, AMSRMIException;

    void migrationTaxonomyTree() throws AMSRMIException;

    void updateSyncFlagById(Integer flag, Long versionId);

    Long duplicateVersionById(Long versionId, String username) throws AMSInvalidInputException;

    List<VersionVo> listVersionByDatasourceId(Integer datasourceId);

    String listTenantPathByIds(Long versionIdList) throws AMSInvalidInputException;

    VersionPo getActiveVersionByTenant(TenantPo tenantPo) throws AMSInvalidInputException;

    List<VersionDatasourceDTO> findVersionIdsByDatasourceIds(List<String> dataSourceIds);
}
