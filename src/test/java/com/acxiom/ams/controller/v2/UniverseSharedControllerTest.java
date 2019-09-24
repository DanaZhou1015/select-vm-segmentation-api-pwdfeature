package com.acxiom.ams.controller.v2;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.po.UniverseIntegrationPo;
import com.acxiom.ams.model.po.UniversePo;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.repository.UniverseIntegrationPoJPA;
import com.acxiom.ams.repository.UniversePoJPA;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.impl.UniverseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class UniverseSharedControllerTest {

    @InjectMocks
    UniverseServiceImpl universeService;
    @Mock
    TenantService tenantService;
    @Mock
    UniversePoJPA universePoJPA;
    @Mock
    ServiceAPI.BitmapAPI bitmapAPI;
    @Mock
    ServiceAPI.UserCenterAPI userCenterAPI;
    @Mock
    UniverseIntegrationPoJPA universeIntegrationPoJPA;

    Model model = Model.getInstance();


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getWhiteListByTenantId() {
        Long tenantId = 8L;
        TenantPo tenantPo = new TenantPo();
        tenantPo.setTenantId("c2f89e4b-7311-4368-8421-dd170d35da18");
        try {
            Mockito.when( tenantService.getTenantById(tenantId)).thenReturn(tenantPo);
            universeService.getWhiteListByTenantId(tenantId);
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to get white tenant list");
        }

    }

    @Test
    public void createSharedUniverse() {
        Long universeId = 8L;
        String targetTenantId = "03baa1e7-dcaf-42c8-976d-e2be9b39e40f";
        TenantVo tenantPo = new TenantVo();
        tenantPo.setTenantId(targetTenantId);
        tenantPo.setPath("mvpdb");
        try {
            Mockito.when(tenantService.getTenantByTenantId(targetTenantId)).thenReturn(tenantPo);
            Mockito.when(universePoJPA.findOne(universeId)).thenReturn(model.universePo);
            UniverseIntegrationPo universeIntegrationPo = new UniverseIntegrationPo();
            Mockito.when(universeIntegrationPoJPA.findByUniverseId(universeId)).thenReturn(universeIntegrationPo);
            universeService.createSharedUniverse(universeId,targetTenantId);
            ArgumentCaptor<UniversePo> personCaptor = ArgumentCaptor.forClass(UniversePo.class);
            verify(universePoJPA).save(personCaptor.capture());
            UniversePo request = personCaptor.getValue();
            Assert.assertTrue("Failed to create shared universe", StringUtils.equals(request.getOwnerTenantPath() ,model.universePo.getTenantPath()));
        } catch (AMSException e) {
            Assert.fail("Failed to create shared universe");
        }
    }
}