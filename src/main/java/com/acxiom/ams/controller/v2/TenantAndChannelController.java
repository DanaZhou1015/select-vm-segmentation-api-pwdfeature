package com.acxiom.ams.controller.v2;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.mapper.TenantAndChannelPoMapper;
import com.acxiom.ams.model.dto.v2.TenantAndChannelDTO;
import com.acxiom.ams.model.vo.TenantAndChannelVo;
import com.acxiom.ams.model.vo.TenantTypeAndDestinationsVO;
import com.acxiom.ams.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:24 1/4/2018
 */
@RestController
@RequestMapping("/v2/tenant/and/channel")
public class TenantAndChannelController {

    @Autowired
    ChannelService channelService;
    @Autowired
    TenantAndChannelPoMapper tenantAndChannelPoMapper;
    
    
    @DeleteMapping(value = "/{id}")
    public void deleteTenantChannelById(@PathVariable(value = "id") Long id)
            throws AMSInvalidInputException {
        channelService.deleteTenantChannelById(id);
    }

    @PostMapping
    public void configTenantChannel(@RequestBody @Valid TenantAndChannelDTO tenantAndChannelDTO)
            throws AMSException {
        channelService.configChannelAndTenant(tenantAndChannelDTO);
    }
    
    //get the list of channel  
    @GetMapping(value = "/{tenantId}")
    public List<TenantAndChannelVo> getChannelListByTenantId(@PathVariable(value = "tenantId") long tenantId) throws AMSInvalidInputException {
        return channelService.getChannelListByTenantId(tenantId);
    }
    
    
    @PutMapping(value = "/{id}")
    public void updateTenantChannelById(@PathVariable(value = "id") long id,
                                           @RequestBody @Valid TenantAndChannelDTO tenantAndChannelDTO)
            throws AMSException {
        channelService.updateTenantChannelById(id, tenantAndChannelDTO);
    }

    @GetMapping(value = "/type/{tenantId}")
    public TenantTypeAndDestinationsVO getTenantTypeAndDestinationsByTenantId(@PathVariable("tenantId") Long tenantId) throws AMSException {
        return channelService.getTenantTypeAndDestinationsByTenantId(tenantId);
    }
    
    
    //enter the channnel
    @GetMapping
    public TenantAndChannelVo getTenantChannelById(@RequestParam("id") Long id)throws AMSInvalidInputException{
        return tenantAndChannelPoMapper.map(channelService.getTenantChannelById(id));
    }


}
