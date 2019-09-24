package com.acxiom.ams.controller.v2;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.mapper.TenantAndChannelPoMapper;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.dto.v2.TenantAndChannelDTO;
import com.acxiom.ams.model.po.TenantAndChannelPo;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.repository.TenantAndChannelPoJPA;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.impl.ChannelServiceImpl;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TenantAndChannelControllerTest {
    @InjectMocks
    ChannelServiceImpl channelService;
    @Mock
    TenantAndChannelPoJPA tenantAndChannelPoJPA;
    @Mock
    TenantPoJPA tenantPoJPA;
    @Mock
    TenantService tenantService;
    @Mock
    TenantAndChannelPoMapper tenantAndChannelPoMapper;
    @Mock
    ServiceAPI.UserCenterAPI userCenterAPI;
    @Mock
    ErrorMessageSourceHandler errorMessageSourceHandler;
    Model model = Model.getInstance();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void deleteTenantAndChannelById() {
    }

    @Test
    public void configChannelAndTenant() throws AMSInvalidInputException {

        TenantPo tenantPo = new TenantPo();
        tenantPo.setId(1L);
        Mockito.when(tenantService.getTenantById(1L)).thenReturn(tenantPo);
        TenantAndChannelDTO tenantAndChannel = new TenantAndChannelDTO("amsdemo", "10.210.28.43", "", "", "123456", "/home" +
                "/test", 22, "test", "test", 1L,"","65536","","","","");
        try {
            channelService.configChannelAndTenant(tenantAndChannel);
        } catch (AMSException e) {
            Assert.fail("Failed to config tenant and  channel");
        }
    }

    @Test
    public void getChannelListByTenantId() throws AMSInvalidInputException {
        Long tenantId = 1L;
        TenantPo tenantPo = new TenantPo();
        tenantPo.setId(tenantId);
        Mockito.when(tenantService.getTenantById(1L)).thenReturn(tenantPo);
        try {
            channelService.getChannelListByTenantId(tenantId);
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to get channel list by tenant id ");
        }
    }

    @Test
    public void getAllChannelList() {
    }

    @Test
    public void updateTenantAndChannelById() throws AMSRMIException {
        Long id = 1L;
        TenantPo tenantPo = new TenantPo();
        tenantPo.setId(1L);
        TenantAndChannelDTO tenantAndChannel = new TenantAndChannelDTO("amsdemo", "10.210.28.43", "", "", "123456", "/home" +
                "/test", 22, "test", "test", 1L,"","65536","","","","");
        TenantAndChannelPo tenantAndChannelPo = new TenantAndChannelPo();
        tenantAndChannelPo.setCreatedBy("amsdemo");
        tenantAndChannelPo.setChannelName("test");
        tenantAndChannelPo.setTenantPo(tenantPo);
        Mockito.when(tenantAndChannelPoJPA.findOne(id)).thenReturn(tenantAndChannelPo);
        Mockito.when(tenantPoJPA.findOne(tenantAndChannel.getTenantId())).thenReturn(tenantPo);
        Mockito.when(tenantPoJPA.findAll()).thenReturn(Lists.newArrayList(model.tenantPo));
        Mockito.when(userCenterAPI.getAllTenant()).thenReturn("[]");
        try {
            channelService.updateTenantChannelById(id, tenantAndChannel);
        } catch (AMSException e) {
            Assert.fail("Failed to update tenant and  channel by id");
        }
    }

    @Test
    public void getTenantTypeAndDestinationsByTenantId() {
    }
}