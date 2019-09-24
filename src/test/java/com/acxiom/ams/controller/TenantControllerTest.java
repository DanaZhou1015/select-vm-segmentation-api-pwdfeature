package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.dto.TenantDTO;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.service.impl.TenantServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.mockito.exceptions.base.MockitoAssertionError;

import static org.mockito.Mockito.verify;


/**
 * @Author: Fermi.Tang
 * @Date: Created in 10:44 AM 9/14/2018
 */
public class TenantControllerTest {
    @InjectMocks
    TenantServiceImpl tenantService;

    @Mock
    TenantPoJPA tenantPoJPA;
    TenantDTO tenantDTO;
    Long tenantId;
    TenantPo tenantPo;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        tenantId = 1L;
        tenantDTO = new TenantDTO("junit_test", "fermi", "56ee1800-8551-4cbb-89c9-3a2ea32487a4", "junittest");
        tenantPo = new TenantPo();
        tenantPo.setId(1);
        tenantPo.setName("junit_test");
        Mockito.when(tenantPoJPA.findTenantPoByTenantId(tenantDTO.getTenantId())).thenReturn(null);
        Mockito.when(tenantPoJPA.save(tenantPo)).thenReturn(null);
    }

    @Test
    public void createTenant() {
        try {
            tenantService.createTenant(tenantDTO);
            verify(tenantPoJPA).findTenantPoByTenantId(tenantDTO.getTenantId());
            ArgumentCaptor<TenantPo> personCaptor = ArgumentCaptor.forClass(TenantPo.class);
            verify(tenantPoJPA).save(personCaptor.capture());
            TenantPo tenantPo = personCaptor.getValue();
            Assert.assertTrue("Failed to create tenant", StringUtils.equals(tenantPo.getName(), tenantDTO.getName()));
        } catch (MockitoAssertionError error) {
            Assert.fail("Failed to create tenant");
        }
    }
}