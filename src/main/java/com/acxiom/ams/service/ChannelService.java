package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.dto.v2.TenantAndChannelDTO;
import com.acxiom.ams.model.po.TenantAndChannelPo;
import com.acxiom.ams.model.vo.TenantAndChannelVo;
import com.acxiom.ams.model.vo.TenantTypeAndDestinationsVO;

import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 10:43 1/3/2018
 */
public interface ChannelService {

    void configChannelAndTenant(TenantAndChannelDTO tenantAndChannelDTO)
            throws AMSException;

    List<TenantAndChannelVo> getChannelListByTenantId(long tenantId) throws AMSInvalidInputException;

    void deleteTenantChannelById(Long id) throws AMSInvalidInputException;

    TenantTypeAndDestinationsVO getTenantTypeAndDestinationsByTenantId(Long tenantId) throws AMSException;

    void updateTenantChannelById(long id, TenantAndChannelDTO tenantAndChannelDTO)
            throws AMSException;

    TenantAndChannelPo getTenantChannelById(Long id) throws AMSInvalidInputException;
    
     String transfer(String str);
}
