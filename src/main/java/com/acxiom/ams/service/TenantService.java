package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.model.dto.TenantDTO;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.vo.TenantExtVo;
import com.acxiom.ams.model.vo.TenantVo;
import java.util.List;

/**
 * Created by cldong on 12/12/2017.
 */
public interface TenantService {

    Long createTenant(TenantDTO tenantDTO);

    TenantVo getTenantByTenantId(String tenantId) throws AMSInvalidInputException;

    List<TenantVo> getAllTenantList();

    String getPrincipal(String username, String appUrl)
        throws AMSException;

    TenantExtVo getTenantExtById(Long tenantId, String key) throws AMSException;

    TenantPo getTenantById(Long tenantId) throws AMSInvalidInputException;

    List<TenantVo> getWhiteListRemoveBlackListByTenantId(String tenantId) throws AMSRMIException;
}
