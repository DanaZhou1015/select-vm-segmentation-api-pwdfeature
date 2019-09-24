package com.acxiom.ams.controller.v2;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.mapper.TenantVoMapper;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.dto.v2.UniverseDTO;
import com.acxiom.ams.model.dto.v2.UniverseForUpdateDTO;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.em.UniverseType;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.po.UniversePo;
import com.acxiom.ams.model.vo.SourceItem;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.repository.UniversePoJPA;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.VersionPoService;
import com.acxiom.ams.service.impl.UniverseServiceImpl;
import com.acxiom.ams.util.UUID;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class UniverseControllerTest {
    @InjectMocks
    UniverseServiceImpl universeService;
    @Mock
    TenantService tenantService;
    @Mock
    UniversePoJPA universePoJPA;
    @Mock
    ServiceAPI.BitmapAPI bitmapAPI;
    @Mock
    VersionPoService versionPoService;
    @Mock
    ServiceAPI.TaxonomyAPI taxonomyAPI;
    @Mock
    AudiencePoJPA audiencePoJPA;
    @Mock
    TenantPoJPA tenantPoJPA;
    @Mock
    TenantVoMapper tenantVoMapper;
    @Mock
    ErrorMessageSourceHandler errorMessageSourceHandler;
    Model model = Model.getInstance();

    Long tenantId;
    Long universeId;
    AudiencePo campaign = new AudiencePo();
    UniversePo UPDATE_UNIVERSE = new UniversePo();

    private void initUniverse() {
        UPDATE_UNIVERSE.setId(1L);
        UPDATE_UNIVERSE.setUniverseStatus(SegmentStatusType.UNIVERSE_PROCESSING);
        UPDATE_UNIVERSE.setUniverseName("Test");
        UPDATE_UNIVERSE.setUniverseRuleJson("");
        UPDATE_UNIVERSE.setUniverseSystemName("test");
        UPDATE_UNIVERSE.setUniverseCount(1000L);
        UPDATE_UNIVERSE.setTenantPath("test");
        UPDATE_UNIVERSE.setTenantId(1L);
        UPDATE_UNIVERSE.setUniverseType(UniverseType.DEFAULT);
    }

    private void initCampaign() {
        campaign.setAudienceType(FolderType.CAMPAIGN);
        campaign.setCount(600L);
        campaign.setRuleJson("");
        campaign.setName("test_count_under_limit");
        campaign.setTenantId(1L);
        campaign.setId(5L);
        campaign.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        campaign.setCreatedBy("amsdemo");
        campaign.setTaxonomyId("110115");
        campaign.setCreatedTime(new Date());
        campaign.setUniverseIds("1");
        campaign.setUpdateTime(new Date());
        campaign.setLegalFlag(true);
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        tenantId = 1L;
        universeId = 1L;
    }

    @Test
    public void createUniverse() {
        UniverseDTO universeDTO = new UniverseDTO("test123", "", "amsdemo", 1000L, 900, 900L);
        // case 1:No exception
        try {
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            universeService.createUniverse(tenantId, universeDTO);
            ArgumentCaptor<UniversePo> personCaptor = ArgumentCaptor.forClass(UniversePo.class);
            verify(universePoJPA).save(personCaptor.capture());
            UniversePo universePo = personCaptor.getValue();
            Assert.assertTrue("Failed to create universe in case 1", SegmentStatusType.UNIVERSE_PROCESSING.equals
                    (universePo.getUniverseStatus()));
        } catch (AMSException e) {
            Assert.fail("Failed to create universe in case 1");
        }
        //case 2: universe has exist
        try {
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            Mockito.when(universePoJPA.findByUniverseNameOrUniverseSystemName("test123", "universe_test123")).thenReturn
                    (Lists.newArrayList(model.TEST_UNIVERSE));
            universeService.createUniverse(tenantId, universeDTO);
            Assert.fail("Failed to create universe in case 2");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to create universe in case 2", StringUtils.equals(e.getCode(), "020258"));
        }
    }

    @Test
    public void callbackUniverseStatus() {
        UniversePo universePo = new UniversePo();
        universePo.setId(1L);
        universePo.setUniverseStatus(SegmentStatusType.UNIVERSE_PROCESSING);
        universePo.setUniverseName("Test123");
        universePo.setUniverseRuleJson("");
        universePo.setUniverseSystemName("test123");
        universePo.setUniverseCount(1000L);
        universePo.setUniverseThreshold(0.66f);
        universePo.setTenantPath("mvpda");
        universePo.setTenantId(1L);
        universePo.setUniverseJobId("11011");
        universePo.setUniverseType(UniverseType.DEFAULT);
        String universeSystemName = "universe_test";
        String universeJobId = "11012";
        String tenantPath = "test";
        initCampaign();
     //   Mockito.when(audiencePoJPA.findByDestinationId(universePo.getId())).thenReturn(Lists.newArrayList(campaign));
        // case 1: success
        try {
            Mockito.when(universePoJPA.findByUniverseSystemNameAndTenantPath(universeSystemName, tenantPath)).thenReturn(universePo);
            universeService.callbackUniverseStatus(tenantPath, universeSystemName, universeJobId, true);
            ArgumentCaptor<UniversePo> personCaptor = ArgumentCaptor.forClass(UniversePo.class);
            verify(universePoJPA).save(personCaptor.capture());
            UniversePo request = personCaptor.getValue();
            Assert.assertTrue("Failed to callback universe status in case 1", SegmentStatusType.UNIVERSE_SUCCESS.equals
                    (request.getUniverseStatus()));
        } catch (AMSException e) {
            Assert.fail("Failed to callback universe status in case 1");
        }

        // case 2: failed
        try {
            Mockito.when(universePoJPA.findByUniverseSystemNameAndTenantPath(universeSystemName, tenantPath)).thenReturn(universePo);
            universeService.callbackUniverseStatus(tenantPath, universeSystemName, universeJobId, false);
            ArgumentCaptor<UniversePo> personCaptor = ArgumentCaptor.forClass(UniversePo.class);
            verify(universePoJPA, times(2)).save(personCaptor.capture());
            UniversePo request = personCaptor.getValue();
            Assert.assertTrue("Failed to callback universe status in case 2", SegmentStatusType.UNIVERSE_FAILED.equals
                    (request.getUniverseStatus()));
        } catch (AMSException e) {
            Assert.fail("Failed to callback universe status in case 2");
        }
    }


    @Test
    public void getMyDataByUniverseId() {
        try {
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            Mockito.when(versionPoService.getActiveVersionByTenant(model.tenantPo)).thenReturn(model.versionPo);
            Mockito.when(taxonomyAPI
                    .getTaxonomyList(String.valueOf(model.versionPo.getId()), model.versionPo.getTreeId()))
                    .thenReturn(Lists.newArrayList(model.taxonomy));
            List<SourceItem> sourceItemList = universeService.getMyDataByTenantId(tenantId);
            Assert.assertTrue("Failed to get my data by universe id", sourceItemList.size() == 0);
        } catch (AMSException e) {
            Assert.fail("Failed to get my data by universe id");
        }
    }

    @Test
    public void updateUniverse() throws AMSInvalidInputException, AMSRMIException {
        initUniverse();
        UniverseForUpdateDTO universeForUpdateDTO = new UniverseForUpdateDTO("Test123", "", "amsdemo", 1000L, 900, 900L);
        Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
        Mockito.when(universePoJPA.findOne(universeId)).thenReturn(UPDATE_UNIVERSE);
      //  Mockito.when(audiencePoJPA.findByDestinationId(universeId)).thenReturn(Lists.newArrayList(campaign));
        // case 1: The universe has exist
        try {
            Mockito.when(universePoJPA.findByUniverseNameAndTenantPath("Test123", model.tenantPo.getPath())).thenReturn(UPDATE_UNIVERSE);
            universeService.updateUniverse(1L, 1L, universeForUpdateDTO);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update universe in case 1", StringUtils.equals(e.getCode(), "020258"));
        }
        String paramMap = "{\"ownerUniverseId\":1,\"universeSysName\":\"test\",\"universeId\":1,\"tenantId\":1," +
                "\"ownerTenantPath\":\"test\",\"tenantPath\":\"test\",\"universeName\":\"Test123\"}";
        // status don't change
        try {
            String resp = "{\"data\":{\"finishFlag\":true}}";
            Mockito.when(bitmapAPI.createUniverse(paramMap)).thenReturn(resp);
            Mockito.when(universePoJPA.findByUniverseNameAndTenantPath("Test123", model.tenantPo.getPath())).thenReturn(null);
            Mockito.when(universePoJPA.findByUniverseSystemNameAndOwnerTenantPath(UPDATE_UNIVERSE.getUniverseSystemName(),
                    UPDATE_UNIVERSE.getTenantPath())).thenReturn(Lists.newArrayList(UPDATE_UNIVERSE));
            universeService.updateUniverse(1L, 1L, universeForUpdateDTO);
            Assert.assertTrue("Failed to update universe in case 2", SegmentStatusType.UNIVERSE_PROCESSING.equals(UPDATE_UNIVERSE.getUniverseStatus()));
        } catch (Exception e) {
            Assert.fail("Failed to update universe in case 2");
        }
        // status change to UNIVERSE_UPDATING
        try {
            String resp = "{\"data\":{\"finishFlag\":false}}";
            Mockito.when(bitmapAPI.createUniverse(paramMap)).thenReturn(resp);
            UPDATE_UNIVERSE.setUniverseStatus(SegmentStatusType.UNIVERSE_SUCCESS);
            universeService.updateUniverse(1L, 1L, universeForUpdateDTO);
            Assert.assertTrue("Failed to update universe in case 3", SegmentStatusType.UNIVERSE_UPDATING.equals(UPDATE_UNIVERSE.getUniverseStatus()));
        } catch (Exception e) {
            Assert.fail("Failed to update universe in case 3");
        }
    }

    @Test
    public void getShareUniverseTenantInfoByTenantId() throws AMSInvalidInputException {
        TenantPo tenantPo = new TenantPo();
        tenantPo.setId(1L);
        Mockito.when(tenantService.getTenantById(1L)).thenReturn(tenantPo);
        List<UniversePo> universePoList = new ArrayList<>();
        UniversePo universePo = new UniversePo();
        universePo.setId(1L);
        universePo.setTenantId(2L);
        universePo.setTenantPath("test");
        universePoList.add(universePo);
        Mockito.when(universePoJPA.findAllByAndOwnerTenantPath(tenantPo.getPath())).thenReturn(universePoList);
        List<TenantPo> tenantPoList = new ArrayList<>();
        tenantPoList.add(tenantPo);
        Mockito.when(tenantPoJPA.findAll(anyList())).thenReturn(tenantPoList);
        try {
            universeService.getShareUniverseTenantInfoByTenantId(1L);
        } catch (Exception e) {
            Assert.fail("Failed to update universe in case 3");
        }
    }
}