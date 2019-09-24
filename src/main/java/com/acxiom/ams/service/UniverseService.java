package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.model.dto.v2.UniverseDTO;
import com.acxiom.ams.model.dto.v2.UniverseForUpdateDTO;
import com.acxiom.ams.model.dto.v2.UniverseIntegrationDTO;
import com.acxiom.ams.model.po.UniverseIntegrationPo;
import com.acxiom.ams.model.po.UniversePo;
import com.acxiom.ams.model.rmi.Taxonomy;
import com.acxiom.ams.model.vo.SourceItem;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.model.vo.WhiteTenant;

import java.util.List;

public interface UniverseService {
    Long createUniverse(Long tenantId, UniverseDTO universeDTO) throws
            AMSException;

    void callbackUniverseStatus(String tenantPath, String universeSystemName, String universeJobId, Boolean status) throws
            AMSInvalidInputException;

    String calculateUniverse(Long tenantId, String rules) throws AMSException;

    UniversePo getUniverseByIdAndTenantId(Long universeId, Long tenantId) throws AMSInvalidInputException;

    List<SourceItem> getMyDataByTenantId(Long tenantId) throws AMSException;

    UniversePo getUniverseById(Long universeId) throws AMSInvalidInputException;

    List<UniversePo> listUniverseByTenantId(Long tenantId, String username) throws AMSException;

    List<Taxonomy> listAllUniverseAttributeByRootIdAndKey(Long tenantId, String key, String rootId) throws
            AMSException;

    String getIconByTenantId(Long tenantId) throws AMSException;

    void updateUniverse(Long tenantId, Long universeId, UniverseForUpdateDTO universeForUpdateDTO) throws
            AMSException;
    void createUniverseIntegration(UniverseIntegrationDTO universeIntegrationDTO) throws AMSInvalidInputException;

    UniverseIntegrationPo getUniverseIntegrationByUniverseId(Long universeId);

    List<WhiteTenant> getWhiteListByTenantId(Long tenantId) throws AMSInvalidInputException;

    Long createSharedUniverse(Long universeId, String targetTenantId) throws AMSInvalidInputException, AMSRMIException;

    void callbackSharedUniverseStatus( String tenantPath, String universeSystemName, String universeJobId) throws AMSInvalidInputException;

    List<TenantVo> getShareUniverseTenantInfoByTenantId(Long tenantId) throws AMSException;

    UniversePo getIconByUniverseSysNameAndTenantId(String universeSysName,String tenantId) throws AMSException;
}
