package com.acxiom.ams.controller;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.po.FolderPo;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.vo.TenantExtVo;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.service.FolderService;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.impl.AdvanceLookalikeServiceImpl;
import com.acxiom.ams.util.Constant;
import com.acxiom.ams.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AdvanceLookalikeControllerTest {

    @InjectMocks
    AdvanceLookalikeServiceImpl advanceLookalikeService;
    @Mock
    ServiceAPI.BitmapAPI bitmapAPI;
    @Mock
    ServiceAPI.UserCenterAPI userCenterAPI;
    @Mock
    ServiceAPI.AdvanceLookalikeAPI advanceLookalikeAPI;
    @Mock
    TenantService tenantService;
    @Mock
    FolderService folderService;
    @Mock
    AudiencePoJPA audiencePoJPA;
    @Mock
    ServiceAPI.MessageCenterAPI messageCenterAPI;

    Long tenantId;
    Long folderId;
    Model model = Model.getInstance();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        tenantId = 1L;
        folderId = 1L;
    }

    @Test
    public void createLookalike() throws AMSRMIException, AMSInvalidInputException {
        TenantPo tenantPo = new TenantPo();
        tenantPo.setTenantId("1");
        Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(tenantPo);
        FolderPo folderPo = new FolderPo();
        folderPo.setId(1L);
        Mockito.when(folderService.getFolderById(folderId)).thenReturn(folderPo);
        String taxonomyId = UUID.GetTaxonomyID();
        String reqParams = "{\"clientID\":\"test\",\"taxonomyid\":\"" + taxonomyId + "\",\"tenantID\":1," +
                "\"folderId\":1," +
                "\"rules\":{\"cap\":0,\"rm-duplicates\":false,\"test-control\":0,\"destination\":\"\"," +
                "\"name\":\"vivian0530\",\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\"," +
                "\"name\":\"Email\",\"type\":\"node\"}],\"name\":\"Contact Channel\"," +
                "\"objid\":\"5b07b74bc9e77c00062e02fe\",\"style\":\"checkbox\"," +
                "\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\",\"items\":[]}]," +
                "\"frozenCount\":1322720,\"count\":1322720,\"exclude\":[]," +
                "\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]}," +
                "\"userID\":\"cd59e860-543a-4b2f-8920-9412e590afb2\"}";
        String resp = "{\"success\":true,\"data\":{\"clientID\":\"test\"," +
                "\"taxonomyid\":\"04130c28-1f7e-4b37-986d-a62ea3e93c57\"," +
                "\"tenantID\":1,\"folderId\":1,\"rules\":{\"cap\":0,\"rm-duplicates\":false,\"test-control\":0," +
                "\"destination\":\"\"," +
                "\"name\":\"vivian0530\",\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                "\"segments\":" +
                "[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\",\"sourceType\":\"TAXONOMY\"," +
                "\"origin\":\"tree\"," +
                "\"values\":[{\"node\":\"99011\",\"name\":\"Email\",\"type\":\"node\"}],\"name\":\"Contact Channel\"," +
                "\"objid\":\"5b07b74bc9e77c00062e02fe\",\"style\":\"checkbox\"," +
                "\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\"," +
                "\"logic\":\"and\",\"items\":[]}],\"frozenCount\":1322720,\"count\":1322720,\"exclude\":[]," +
                "\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]}," +
                "\"userID\":\"cd59e860-543a-4b2f-8920-9412e590afb2\"}}";
        Mockito.when(bitmapAPI.createBitmapV7(reqParams)).thenReturn(resp);
        TenantExtVo tenantExtUser = new TenantExtVo();
        tenantExtUser.setValue("test");
        Mockito.when(userCenterAPI.getTenantExtByKey(tenantPo.getTenantId(), Constant.ADVANCE_USER_TENANT_EXT_KEY)).thenReturn(tenantExtUser);
        ReflectionTestUtils.setField(Constant.class, "ADVANCE_S3_PATH", "http://localhost/%s");
        TenantExtVo tenantExtAccessKey = new TenantExtVo();
        tenantExtAccessKey.setValue("test");
        Mockito.when(userCenterAPI.getTenantExtByKey(tenantPo.getTenantId(),
                Constant.ADVANCE_ACCESS_KEY_TENANT_EXT_KEY)).thenReturn(tenantExtAccessKey);
        TenantExtVo tenantExtSecretKey = new TenantExtVo();
        tenantExtSecretKey.setValue("test");
        Mockito.when(userCenterAPI.getTenantExtByKey(tenantPo.getTenantId(),
                Constant.ADVANCE_SECRET_KEY_TENANT_EXT_KEY)).thenReturn(tenantExtSecretKey);
        advanceLookalikeService.createLookalike(reqParams);
        verify(bitmapAPI, times(1)).createBitmapV7(anyString());
        verify(bitmapAPI, times(1)).createAdvanceLookalike(anyString());
    }

    @Test
    public void createBitmapCallback() throws Exception {
        String reqParams = "{\"status\":\"LOOKALIKE_RUNNING\",\"id\":\"1110111\",\"fileName\":\"s3://com-liveramp-eu-external-kaminostaging-gbr/client=coke/upload/cokezerolovers.csv\",\"size\":\"100\"}";
        AudiencePo audiencePo = new AudiencePo();
        audiencePo.setTaxonomyId("1110111");
        audiencePo.setTenantId(1L);
        audiencePo.setSegmentStatusType(SegmentStatusType.LOOKALIKE_RUNNING);
        Mockito.when(audiencePoJPA.findAudiencePoByTaxonomyId("1110111")).thenReturn(audiencePo);
        Long tenantId = 1L;
        TenantPo tenantPo = new TenantPo();
        tenantPo.setId(tenantId);
        Mockito.when(tenantService.getTenantById(audiencePo.getTenantId())).thenReturn(tenantPo);
        TenantExtVo tenantExtBaseData = new TenantExtVo("","advanceBaseData","InfoBase_UK");
        Mockito.when(userCenterAPI.getTenantExtByKey(tenantPo.getTenantId(), Constant.ADVANCE_BASE_DATA_TENANT_EXT_KEY)).thenReturn(tenantExtBaseData);
        TenantExtVo tenantExtUniverseName = new TenantExtVo("","","YouGov");
        Mockito.when(userCenterAPI.getTenantExtByKey(tenantPo.getTenantId(), Constant.ADVANCE_UNIVERSE_NAME_TENANT_EXT_KEY)).thenReturn(tenantExtUniverseName);
        TenantExtVo tenantExtAccessKey = new TenantExtVo("","","AAAAAA");
        Mockito.when(userCenterAPI.getTenantExtByKey(tenantPo.getTenantId(), Constant.ADVANCE_ACCESS_KEY_TENANT_EXT_KEY)).thenReturn(tenantExtAccessKey);
        TenantExtVo tenantExtSecretKey = new TenantExtVo("","","BBBBBB");
        Mockito.when(userCenterAPI.getTenantExtByKey(tenantPo.getTenantId(), Constant.ADVANCE_SECRET_KEY_TENANT_EXT_KEY)).thenReturn(tenantExtSecretKey);

        String jsonString = "{\"job-id\":\"\"}";
        Mockito.when(advanceLookalikeAPI.buildModel(anyString(), anyString(), anyObject())).thenReturn(jsonString);
        advanceLookalikeService.createBitmapCallback(reqParams);

        String reqParams1 = "{\"status\":\"LOOKALIKE_DONE\",\"id\":\"1110111\",\"fileName\":\"s3://com-liveramp-eu-external-kaminostaging-gbr/client=coke/upload/cokezerolovers.csv\",\"size\":\"100\"}";
        advanceLookalikeService.createBitmapCallback(reqParams1);
        String reqParams2 = "{\"status\":\"LOOKALIKE_FAILED\",\"id\":\"1110111\",\"fileName\":\"s3://com-liveramp-eu-external-kaminostaging-gbr/client=coke/upload/cokezerolovers.csv\",\"size\":\"100\"}";
        advanceLookalikeService.createBitmapCallback(reqParams2);

    }

    @Test
    public void getLookalikeResultById() {
        AudiencePo audiencePo = new AudiencePo();
        audiencePo.setLookalikeResult("");
        Mockito.when(audiencePoJPA.findOne(1L)).thenReturn(audiencePo);
        advanceLookalikeService.getLookalikeResultById(1L);
    }

    @Test
    public void deployLookalike() throws AMSRMIException, AMSInvalidInputException {
        AudiencePo audiencePo = new AudiencePo();
        audiencePo.setTenantId(1L);
        audiencePo.setLookalikeJobId("1");
        Mockito.when(audiencePoJPA.findOne(1L)).thenReturn(audiencePo);
        TenantPo tenantPo = new TenantPo();
        tenantPo.setTenantId("1");
        Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(tenantPo);
        TenantExtVo tenantExtAccessKey = new TenantExtVo();
        tenantExtAccessKey.setValue("test");
        Mockito.when(userCenterAPI.getTenantExtByKey(tenantPo.getTenantId(), Constant
        .ADVANCE_ACCESS_KEY_TENANT_EXT_KEY)).thenReturn(tenantExtAccessKey);
        TenantExtVo tenantExtSecretKey = new TenantExtVo();
        tenantExtSecretKey.setValue("test");
        Mockito.when(userCenterAPI.getTenantExtByKey(tenantPo.getTenantId(), Constant
        .ADVANCE_SECRET_KEY_TENANT_EXT_KEY)).thenReturn(tenantExtSecretKey);
        try {
            advanceLookalikeService.deployLookalike(1L, 1, 1L);
            verify(advanceLookalikeAPI, times(1)).deployModel(tenantExtAccessKey.getValue(), tenantExtSecretKey
            .getValue(), anyObject());
        } catch (Exception e) {
            if(!(e instanceof IllegalArgumentException)){
                Assert.fail("Failed to deploy lookalike");
            }
        }
    }
}