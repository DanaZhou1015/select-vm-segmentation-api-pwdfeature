package com.acxiom.ams.controller;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.mapper.VersionPoMapper;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.dto.VersionDTO;
import com.acxiom.ams.model.po.VersionPo;
import com.acxiom.ams.repository.VersionPoJPA;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.VersionPoService;
import com.acxiom.ams.service.impl.VersionPoServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 9:54 AM 11/6/2018
 */
public class VersionControllerTest {
    @InjectMocks
    VersionPoServiceImpl versionPoService;
    @Mock
    VersionPoJPA versionPoJPA;
    @Mock
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Mock
    TenantService tenantService;
    Long tenantId;
    Long versionId;
    Model model = Model.getInstance();
    @Before
    public void init() throws AMSInvalidInputException {
        MockitoAnnotations.initMocks(this);
        tenantId = 1L;
        versionId = 1L;
        Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
    }

    @Test
    public void findVersionByTenantIdAndPage() {

    }

    @Test
    public void findVersionById() {
    }

    @Test
    public void saveVersion() {
        VersionDTO versionDTO = new VersionDTO(2L,"test-2","amsdemo","5ae1b89e9fa30d472c89c288","");
        try {
            versionPoService.saveVersion(tenantId, versionDTO);
            ArgumentCaptor<VersionPo> personCaptor = ArgumentCaptor.forClass(VersionPo.class);
            verify(versionPoJPA).save(personCaptor.capture());
            VersionPo request = personCaptor.getValue();
            Assert.assertTrue("Failed to update version in case 4", StringUtils.equals(request.getName(),
                    versionDTO.getName()));
        }catch (AMSException e){
            Assert.fail("Failed to save version");
        }
        try {
            Mockito.when(versionPoJPA
                    .findFirstByTenantPoAndNameAndCreatedBy(model.tenantPo, versionDTO.getName(),
                            versionDTO.getUserId())).thenReturn(model.versionPo);
            long resp = versionPoService.saveVersion(tenantId, versionDTO);
            Assert.assertTrue("Failed to save version", resp == -1);
        }catch (AMSException e){
            Assert.fail("Failed to save version");
        }
    }
}