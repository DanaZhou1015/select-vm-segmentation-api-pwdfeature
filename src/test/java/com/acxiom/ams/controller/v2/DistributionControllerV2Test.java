package com.acxiom.ams.controller.v2;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.dto.v2.CampaignDistributeParamDTO;
import com.acxiom.ams.model.dto.v2.DistributeReturnParamV2;
import com.acxiom.ams.model.dto.v2.UniverseActivityLogParamForReview;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.*;
import com.acxiom.ams.repository.*;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.UniverseService;
import com.acxiom.ams.service.impl.BitmapServiceImpl;
import com.acxiom.ams.service.impl.DistributionServiceImpl;
import com.acxiom.ams.service.impl.ExportServiceImpl;
import com.acxiom.ams.util.Constant;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:39 AM 10/18/2018
 */
public class DistributionControllerV2Test {
    @InjectMocks
    DistributionServiceImpl distributionService;
    @Mock
    AudiencePoJPA audiencePoJPA;
    @Mock
    TenantPoJPA tenantPoJPA;
    @Mock
    AudienceDistributeJobPoJPA audienceDistributeJobPoJPA;
    @Mock
    ServiceAPI.BitmapAPI bitmapAPI;
    @Mock
    UniverseService universeService;
    @Mock
    TenantService tenantService;
    @Mock
    UniversePoJPA universePoJPA;
    @Mock
    UniverseIntegrationPoJPA universeIntegrationPoJPA;
    @Mock
    UniverseActivityLogPoJPA universeActivityLogPoJPA;
    @InjectMocks
    @Spy
    ExportServiceImpl exportService = new ExportServiceImpl();
    @Mock
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @InjectMocks
    @Spy
    BitmapServiceImpl bitmapService = new BitmapServiceImpl();
    @Mock
    ServiceAPI.DataSourceAPI dataSourceAPI;

    Model model = Model.getInstance();
    List<Long> folderIdList = Lists.newArrayList(1L);
    List<Long> campaignIdList = Lists.newArrayList(11396L);
    List<AudiencePo> campaignList = Lists.newArrayList(model.campaign);

    @Before
    public void init() throws AMSInvalidInputException {
        MockitoAnnotations.initMocks(this);
        // distribute mock
        Mockito.when(audiencePoJPA.getSegmentListByFolderId(StringUtils.join(folderIdList, ","))).thenReturn
                (campaignList);
        Mockito.when(audiencePoJPA.findAll(campaignIdList)).thenReturn(campaignList);
        Mockito.when(tenantService.getTenantById(model.tenantId)).thenReturn(model.tenantPo);
    }

    @Test
    public void distributeCampaigns() throws AMSException {
        Long tenantId = 1L;
        Long universeId = 1L;
        TenantPo tenantPo = new TenantPo();
        tenantPo.setPath("mvpda");
        Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(tenantPo);
        Mockito.when(universeService.getUniverseById(universeId)).thenReturn(model.SUCCESS_UNIVERSE);
        CampaignDistributeParamDTO distributeParamV2 = new CampaignDistributeParamDTO(tenantId, "test@liveramp.com", campaignIdList, model.username);
        AudiencePo audiencePo = new AudiencePo();
        audiencePo.setUniverseIds(String.valueOf(universeId));
        audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
        audiencePo.setId(1L);
        audiencePo.setCount(300L);
        //  audiencePo.set
        Mockito.when(audiencePoJPA.findAll(distributeParamV2.getCampaignIdList())).thenReturn(Lists.newArrayList(audiencePo));
        UniverseActivityLogPo universeActivityLogPo = new UniverseActivityLogPo();
        universeActivityLogPo.setAudienceStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
        Mockito.when(universeActivityLogPoJPA.findFirstByAudienceIdAndDestinationIdOrderByCreatedTimeDesc(audiencePo.getId(),1L)).thenReturn(universeActivityLogPo);
        Mockito.when(universePoJPA.findAll(Lists.newArrayList(1L))).thenReturn(Lists.newArrayList(model.universePo));
        UniverseIntegrationPo universeIntegrationPo = new UniverseIntegrationPo(1L, "/home/test", "4496", "", "", "", "");
        Mockito.when(universeIntegrationPoJPA.findAllByUniverseIdIn(Lists.newArrayList(1L))).thenReturn(Lists.newArrayList(universeIntegrationPo));
        // case 1: audience type can not be distributed
        try {
            distributionService.distributeCampaigns(distributeParamV2);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to distribute campaigns in case 1", Constant.ERROR_CODE_0233.equals(e.getCode()));
        }
        audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        // case 2: audience count is under universe threshold
//        try {
//            distributionService.distributeCampaigns(distributeParamV2);
//        } catch (AMSException e) {
//            Assert.assertTrue("Failed to distribute campaigns in case 2", Constant.ERROR_CODE_0268.equals(e.getCode()));
//        }
        audiencePo.setCount(400L);
        // case 3:no exception
        audiencePo.setRuleJson("{\"id\":\"a5955945-d62c-61d4-f616-47ff90a94286\"," +
                "\"name\":\"universe-integration-test\",\"cap\":0,\"rm-duplicates\":false,\"test-control\":0," +
                "\"defaultrule\":[],\"destination\":43,\"segments\":[{\"id\":\"5c6f208c-0a9c-3719-cf95-eeeb719eeeec" +
                "\",\"name\":\"segment1\",\"include\":[{\"id\":\"d8c92e57-c48c-0c54-330f-9276a8ba7b3f\"," +
                "\"objid\":\"5c14ac7946e0fb00059648e1\",\"name\":\"Pets22\",\"path\":\"My Data / Universe50011 / " +
                "Pets\",\"style\":\"checkbox\",\"logic\":\"and\",\"origin\":\"tree\",\"sourceType\":\"TAXONOMY\"," +
                "\"values\":[{\"type\":\"node\",\"name\":\"Pig\",\"node\":\"8_506_10011\"}],\"items\":[]," +
                "\"infobaseFlag\":false,\"dataType\":\"3p\",\"owner\":\"tv50011\"}],\"exclude\":[],\"cap\":0," +
                "\"count\":7981,\"frozenCount\":7981,\"frozenNativeCount\":7981}],\"campaignId\":5597136128652845000}");
        try {
            distributionService.distributeCampaigns(distributeParamV2);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to distribute campaigns in case 3", Constant.ERROR_CODE_0233.equals(e.getCode()));
        }
    }

    @Test
    public void callbackSegmentStatus() throws AMSInvalidInputException {
        AudiencePo campaignNew = new AudiencePo();
        campaignNew.setAudienceType(FolderType.CAMPAIGN);
        campaignNew.setCount(1000L);
        campaignNew.setRuleJson(model.rule);
        campaignNew.setName("test_new");
        campaignNew.setTenantId(1L);
        campaignNew.setId(1L);
        campaignNew.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        campaignNew.setCreatedBy("amsdemo");
        campaignNew.setTaxonomyId("110111");
        campaignNew.setCreatedTime(new Date());
        campaignNew.setFolderPo(model.campaignFolder);
        campaignNew.setUniverseIds("1");
        Mockito.when(audiencePoJPA.findOne(model.campaign_new.getId())).thenReturn(campaignNew);
        Mockito.when(audienceDistributeJobPoJPA.findOne(model.campaignJobId)).thenReturn(model.audienceDistributeJobPo);
        Mockito.when(audienceDistributeJobPoJPA
                .findByAudienceIdAndDestinationIdInOrderByUpdateTimeDesc(1L, Lists.newArrayList(1L))).thenReturn(Lists.newArrayList(model.audienceDistributeJobPo));
        DistributeReturnParamV2 distributeSuccess = new DistributeReturnParamV2(true, model.campaign_new.getId(), "testCampaign",
                "distribute success", 10L,1L);
        distributionService.callbackSegmentStatusV2(distributeSuccess);
        ArgumentCaptor<AudiencePo> personCaptor1 = ArgumentCaptor.forClass(AudiencePo.class);
        verify(audiencePoJPA, times(1)).save(personCaptor1.capture());
        AudiencePo audiencePo = personCaptor1.getValue();
        ArgumentCaptor<AudienceDistributeJobPo> personCaptor2 = ArgumentCaptor.forClass(AudienceDistributeJobPo.class);
        verify(audienceDistributeJobPoJPA).save(personCaptor2.capture());
        AudienceDistributeJobPo audienceDistributeJobPo = personCaptor2.getValue();
        Assert.assertTrue("Failed to callback distribute status", SegmentStatusType.CAMPAIGN_DISTRIBUTED.equals
                (audiencePo.getSegmentStatusType()));
        Assert.assertTrue("Failed to callback distribute status", SegmentStatusType.CAMPAIGN_DISTRIBUTED.equals
                (audienceDistributeJobPo.getStatus()));

        // failed
        DistributeReturnParamV2 distributeFailed = new DistributeReturnParamV2(false, model.campaign_new.getId(), "testCampaign",
                "distribute success", 10L,1L);
        distributionService.callbackSegmentStatusV2(distributeFailed);
        ArgumentCaptor<AudiencePo> personCaptor3 = ArgumentCaptor.forClass(AudiencePo.class);
        verify(audiencePoJPA, times(2)).save(personCaptor3.capture());
        AudiencePo audiencePo1 = personCaptor3.getValue();
        ArgumentCaptor<AudienceDistributeJobPo> personCaptor4 = ArgumentCaptor.forClass(AudienceDistributeJobPo.class);
        verify(audienceDistributeJobPoJPA, times(2)).save(personCaptor4.capture());
        AudienceDistributeJobPo audienceDistributeJobPo1 = personCaptor4.getValue();
        Assert.assertTrue("Failed to callback distribute status", SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED.equals
                (audiencePo1.getSegmentStatusType()));
        Assert.assertTrue("Failed to callback distribute status", SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED.equals
                (audienceDistributeJobPo1.getStatus()));
    }

    @Test
    public void SearchActivityList() {
    }

    @Test
    public void updateActivity() throws AMSException {
        UniverseActivityLogParamForReview universeActivityLogParam = new UniverseActivityLogParamForReview();
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        universeActivityLogParam.setIdList(idList);
        universeActivityLogParam.setStatus("approve");
        universeActivityLogParam.setUsername("approve");
        List<UniverseActivityLogPo> returnList = new ArrayList<>();
        UniverseActivityLogPo universeActivityLogPo = new UniverseActivityLogPo();
        universeActivityLogPo.setId(1L);
        universeActivityLogPo.setAudienceId(1L);
        universeActivityLogPo.setDestinationId(1L);
        universeActivityLogPo.setAudienceStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED);
        returnList.add(universeActivityLogPo);
        Mockito.when(universeActivityLogPoJPA.findAll(universeActivityLogParam.getIdList())).thenReturn(returnList);
        AudiencePo audiencePo = new AudiencePo();
        audiencePo.setUniverseIds("1");
        audiencePo.setTenantId(1L);
        audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
        audiencePo.setId(1L);
        audiencePo.setCount(300L);
        audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        audiencePo.setRuleJson("{\"id\":\"a5955945-d62c-61d4-f616-47ff90a94286\"," +
                "\"name\":\"universe-integration-test\",\"cap\":0,\"rm-duplicates\":false,\"test-control\":0," +
                "\"defaultrule\":[],\"destination\":43,\"segments\":[{\"id\":\"5c6f208c-0a9c-3719-cf95-eeeb719eeeec" +
                "\",\"name\":\"segment1\",\"include\":[{\"id\":\"d8c92e57-c48c-0c54-330f-9276a8ba7b3f\"," +
                "\"objid\":\"5c14ac7946e0fb00059648e1\",\"name\":\"Pets22\",\"path\":\"My Data / Universe50011 / " +
                "Pets\",\"style\":\"checkbox\",\"logic\":\"and\",\"origin\":\"tree\",\"sourceType\":\"TAXONOMY\"," +
                "\"values\":[{\"type\":\"node\",\"name\":\"Pig\",\"node\":\"8_506_10011\"}],\"items\":[]," +
                "\"infobaseFlag\":false,\"dataType\":\"3p\",\"owner\":\"tv50011\"}],\"exclude\":[],\"cap\":0," +
                "\"count\":7981,\"frozenCount\":7981,\"frozenNativeCount\":7981}],\"campaignId\":5597136128652845000}");
        Mockito.when(audiencePoJPA.findOne(universeActivityLogPo.getAudienceId())).thenReturn(audiencePo);
        UniversePo universePo = new UniversePo();
        universePo.setId(1L);
        UniverseIntegrationPo universeIntegrationPo = new UniverseIntegrationPo();
        universeIntegrationPo.setId(1L);
        Mockito.when(universeService.getUniverseById(1L)).thenReturn(universePo);
        Mockito.when(universeService.getUniverseIntegrationByUniverseId(1L)).thenReturn(universeIntegrationPo);
        try {
            distributionService.reviewActivity(universeActivityLogParam);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to review campaigns in", Constant.ERROR_CODE_0233.equals(e.getCode()));
        }
    }

    @Test
    public void exportActivity() {
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        List<UniverseActivityLogPo> universeActivityLogPoList = new ArrayList<>();
        UniverseActivityLogPo universeActivityLogPo = new UniverseActivityLogPo();
        universeActivityLogPo.setDestinationId(1L);
        universeActivityLogPo.setAudienceName("test");
        universeActivityLogPo.setAudienceRuleJson(model.campaignRule);
        universeActivityLogPo.setUpdateTime(new Date());
        universeActivityLogPoList.add(universeActivityLogPo);
        Mockito.when(universeActivityLogPoJPA.findAll(idList)).thenReturn(universeActivityLogPoList);
        Long destinationId = 1L;
        try {
            ReflectionTestUtils.setField(exportService, "exportTemplateFile", "./init/tmp/CampaignTemplate.xlsx");
            ReflectionTestUtils.setField(exportService, "tempFile", "/tmp");
            String reqParams = "{\"universeSysNames\":[\"test123\"],\"campaign\":{\"cap\":5,\"rm-duplicates\":false," +
                    "\"campaignId\":4590370230252204000,\"test-control\":0,\"destination\":13,\"name\":\"Audience7\"," +
                    "\"id\":\"bf8b44c3-07b8-8d4d-78ba-6643d68168ac\",\"defaultrule\":[]," +
                    "\"segments\":[{\"include\":[{\"owner\":\"PartnerA\",\"path\":\"AudienceH / SegmentH(derived 1p)" +
                    "\",\"campaignId\":1667,\"dataType\":\"1p\",\"origin\":\"segment\"," +
                    "\"values\":[{\"node\":\"cae9861c-7672-08a7-e1cc-6440cc767076\"}],\"name\":\"SegmentH(derived 1p)" +
                    "\",\"objid\":\"\",\"id\":\"955b3658-b3b0-5674-3945-abd7a851d1b2\",\"logic\":\"and\"," +
                    "\"items\":[]},{\"owner\":\"PartnerA\",\"infobaseFlag\":false,\"dataType\":\"1p\"," +
                    "\"origin\":\"tree\",\"values\":[{\"node\":\"23_410_S404\",\"name\":\"Tourist\"," +
                    "\"type\":\"node\"}],\"path\":\"Client Data / PartnerA / 1P_2P_PartnerA_557320 / " +
                    "CustomerSegmentation\",\"sourceType\":\"TAXONOMY\",\"name\":\"CustomerSegmentation\"," +
                    "\"objid\":\"5b86452446e0fb000500a727\",\"style\":\"checkbox\"," +
                    "\"id\":\"e146998f-ac19-0d37-7f82-bc3461e2fae2\",\"logic\":\"or\",\"items\":[]}]," +
                    "\"frozenCount\":4,\"count\":4,\"name\":\"test1\",\"exclude\":[]," +
                    "\"id\":\"d62d3185-7722-3fa5-59c5-037e9a3bf7a2\"},{\"include\":[{\"owner\":\"PartnerA\"," +
                    "\"path\":\"AudienceH / SegmentH(derived 1p)\",\"campaignId\":1667,\"dataType\":\"1p\"," +
                    "\"origin\":\"segment\",\"values\":[{\"node\":\"cae9861c-7672-08a7-e1cc-6440cc767076\"}]," +
                    "\"name\":\"SegmentH(derived 1p)\",\"objid\":\"\"," +
                    "\"id\":\"11a9b8ba-1394-b734-1d11-dd1323e3048f\",\"logic\":\"and\",\"items\":[]}," +
                    "{\"owner\":\"PartnerB\",\"infobaseFlag\":false,\"dataType\":\"3p\",\"origin\":\"tree\"," +
                    "\"values\":[{\"node\":\"23_412_S301\",\"name\":\"36-40\",\"type\":\"node\"}],\"path\":\"Data " +
                    "Store / ProviderA / data_store_PartnerA_557321 / AgeGroup\",\"sourceType\":\"TAXONOMY\"," +
                    "\"name\":\"AgeGroup\",\"objid\":\"5b86452446e0fb000500a729\",\"style\":\"checkbox\"," +
                    "\"id\":\"0f918698-6028-36ad-ed8b-66ba672ecb14\",\"logic\":\"or\",\"items\":[]}]," +
                    "\"frozenCount\":2,\"cap\":0,\"count\":2,\"name\":\"segment1\",\"exclude\":[]," +
                    "\"id\":\"4b2fb0c9-480e-cc61-a016-4bb2d3368370\"}]},\"tenantPath\":\"mvpda\"}";
            String resp = "{\"data\":{\"campaignInfo\":[{\"allTestCount\":0,\"testCount\":0,\"controlCount\":4}," +
                    "{\"allTestCount\":0,\"testCount\":0,\"controlCount\":2}]}}";
            Mockito.when(bitmapAPI.getCampaignInfoV2(reqParams)).thenReturn(resp);
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            Mockito.when(tenantService.getTenantById(23L)).thenReturn(model.tenantPo);
            Mockito.when(dataSourceAPI.getParentPathByTaxonomyId(anyString(), anyString(), anyInt())).thenReturn("");
            exportService.exportUniverseActivityLog(idList, 1L);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to export campaign", StringUtils.equals(e.getCode(), "020228"));
        }
    }
}