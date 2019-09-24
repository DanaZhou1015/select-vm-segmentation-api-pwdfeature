package com.acxiom.ams.controller.v2;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.mapper.TaxonomyMapper;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.dto.*;
import com.acxiom.ams.model.dto.v2.AudienceCountByUniverseDTO;
import com.acxiom.ams.model.dto.v2.CampaignParam;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.em.TemplateStatusType;
import com.acxiom.ams.model.po.*;
import com.acxiom.ams.model.rmi.Taxonomy;
import com.acxiom.ams.model.vo.*;
import com.acxiom.ams.repository.*;
import com.acxiom.ams.service.*;
import com.acxiom.ams.service.impl.AudiencePoServiceImpl;
import com.acxiom.ams.service.impl.BitmapServiceImpl;
import com.acxiom.ams.util.UUID;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.Mockito.verify;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:06 AM 10/30/2018
 */
public class CampaignControllerTest {
    @InjectMocks
    AudiencePoServiceImpl audiencePoService;
    @Mock
    TenantPoJPA tenantPoJPA;
    @Mock
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Mock
    TenantAndChannelPoJPA tenantAndChannelPoJPA;
    @Mock
    FolderService folderService;
    @Mock
    ServiceAPI.TaxonomyAPI taxonomyAPI;
    @Mock
    TenantService tenantService;
    @Mock
    VersionPoService versionPoService;
    @Mock
    ChannelService channelService;
    @Mock
    VersionPoJPA versionPoJPA;
    @InjectMocks
    @Spy
    BitmapServiceImpl bitmapService = new BitmapServiceImpl();
    @Mock
    ServiceAPI.BitmapAPI bitmapAPI;
    @Mock
    AudiencePoJPA audiencePoJPA;
    @Mock
    AudienceDistributeJobPoJPA audienceDistributeJobPoJPA;
    @Mock
    TaxonomyMapper taxonomyMapper;
    @Mock
    ServiceAPI.UserCenterAPI userCenterAPI;
    @Mock
    ServiceAPI.DataSourceAPI dataSourceAPI;
    @Mock
    UniversePoJPA universePoJPA;
    @Mock
    UniverseService universeService;
    @Mock
    TenantAndUniversePoJPA tenantAndUniversePoJPA;

    Model model = Model.getInstance();

    Long tenantId;

    Long destinationId;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        tenantId = 1L;
        destinationId = 1L;
    }

    @Test
    public void getDestination() throws AMSRMIException {
        List<UniversePo> universePoList = Lists.newArrayList(model.SUCCESS_UNIVERSE);
        Mockito.when(universePoJPA.findAllByTenantId(tenantId)).thenReturn(universePoList);
        TenantAndUniverseKey tenantAndUniverseKey = new TenantAndUniverseKey(1L,"amsdemo");
        TenantAndUniversePo tenantAndUniversePo = new TenantAndUniversePo(tenantAndUniverseKey,"2");
        Mockito.when(tenantAndUniversePoJPA.findOne(tenantAndUniverseKey)).thenReturn(tenantAndUniversePo);
        List<DestinationVo> destinationVoList = audiencePoService.getDestination(tenantId, "amsdemo");
        Assert.assertTrue("Failed to ge destination", destinationVoList.size() == 1);
    }

    @Test
    public void getTaxonomyItem() {
        try {
            List<Long> universeIdList = new ArrayList<>();
            String id = "5b30571ae6f63729dc8627ea";
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.tenantPo);
            TenantPo[] tenantPos = new TenantPo[]{model.tenantPo};
            Mockito.when(versionPoJPA.findAllByTenantPoInAndOperationFlag(tenantPos, TemplateStatusType.ACTIVE))
                    .thenReturn(Lists.newArrayList(model.versionPo));
            Mockito.when(taxonomyAPI
                    .getTaxonomyListByNode(String.valueOf(model.versionPo.getId()), id)).thenReturn(model.taxonomy
                    .getTaxonomyIncludes());
            Mockito.when(taxonomyMapper.map(model.taxonomy.getTaxonomyIncludes())).thenReturn(Lists.newArrayList
                    (model.parseTaxonomy(model.taxonomy.getTaxonomyIncludes().get(0))));
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            List<TaxonomyItemVo> taxonomyItemVoList = audiencePoService.getTaxonomyTreeItem(destinationId, universeIdList, id);
            Assert.assertTrue("Failed to get child node", taxonomyItemVoList.size() == 1);
        } catch (AMSException e) {
            Assert.fail("Failed to get child node");
        }
    }

    @Test
    public void getTaxonomyEndTypeItem() {
        try {
            Integer limit = 100;
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.tenantPo);
            TenantPo[] tenantPos = new TenantPo[]{model.tenantPo};
            Mockito.when(versionPoJPA.findAllByTenantPoInAndOperationFlag(tenantPos, TemplateStatusType.ACTIVE))
                    .thenReturn(Lists.newArrayList(model.versionPo));
            Mockito.when(taxonomyAPI
                    .getTaxonomyEndTypeItemWithinLimit(String.valueOf(model.versionPo.getId()), 1, limit)).thenReturn
                    (model.taxonomy
                            .getTaxonomyIncludes());
            Mockito.when(taxonomyMapper.map(model.taxonomy.getTaxonomyIncludes())).thenReturn(Lists.newArrayList
                    (model.parseTaxonomy(model.taxonomy.getTaxonomyIncludes().get(0))));
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            List<TaxonomyItemVo> taxonomyItemVoList = audiencePoService
                    .getTaxonomyTreeEndTypeItemDestinationIdWithinLimit(destinationId, limit);
            Assert.assertTrue("Failed to get end node by limit", taxonomyItemVoList.size() == 1);
        } catch (AMSException e) {
            Assert.fail("Failed to get end node by limit");
        }
    }

    @Test
    public void createCampaign() throws AMSInvalidInputException {
        CampaignParam campaignParam = new CampaignParam();
        campaignParam.setName("campaign-1101");
        campaignParam.setRule(model.rule);
        campaignParam.setCreatedBy(model.createdBy);
        campaignParam.setAudienceType(FolderType.SAVED_SEGMENT);
        campaignParam.setFolderId(3L);
        campaignParam.setUserId(model.userId);
        campaignParam.setUniverseIdList(Lists.newArrayList(2L));
        List<AudienceCountByUniverseDTO> audienceCountByUniverseDTOList = new ArrayList<>();
        AudienceCountByUniverseDTO audienceCountByUniverseDTO = new AudienceCountByUniverseDTO(2L,300L,null);
        audienceCountByUniverseDTOList.add(audienceCountByUniverseDTO);
        campaignParam.setAudienceCountByUniverseDTOList(audienceCountByUniverseDTOList);
       // campaignParam.setTempCampaignId(111L);
        campaignParam.setAudienceCount(100L);
        Mockito.when(universePoJPA.findAll(Lists.newArrayList(2L))).thenReturn(Lists.newArrayList(model.SUCCESS_UNIVERSE));
        // case 1: audience count under limit
        try {
            Mockito.when(folderService.getFolderById(campaignParam.getFolderId())).thenReturn(model.campaignFolder);
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            audiencePoService.createCampaign(tenantId, campaignParam);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to save campaign in case 1", StringUtils.equals("020268", e.getCode()));
        }
        // case 2:Audience already exists
        try {
            audienceCountByUniverseDTO.setAudienceCountByUniverse(800L);
            campaignParam.setAudienceCount(800L);
            Mockito.when(audiencePoJPA
                    .findAudiencePoByAudienceTypeAndTenantIdAndName(campaignParam.getAudienceType(),
                            tenantId, campaignParam.getName())).thenReturn(model.campaign_new);
            audiencePoService.createCampaign(tenantId, campaignParam);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to save campaign in case 2", StringUtils.equals("020213", e.getCode()));
        }
        // case 3:no exception
        try {
            String rule = "{\"clientID\":\"mvpda\",\"campaignId\":0,\"tenantID\":1,\"rule\":\"{\\\"cap\\\":0," +
                    "\\\"defaultrule\\\":[],\\\"destination\\\":\\\"\\\"," +
                    "\\\"id\\\":\\\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\\\",\\\"name\\\":\\\"vivian0530\\\"," +
                    "\\\"rm-duplicates\\\":false,\\\"segments\\\":[{\\\"count\\\":1322720,\\\"exclude\\\":[]," +
                    "\\\"frozenCount\\\":1322720,\\\"id\\\":\\\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\\\"," +
                    "\\\"include\\\":[{\\\"id\\\":\\\"7b8d706e-6124-7df0-5e0d-c1f025962b30\\\",\\\"items\\\":[]," +
                    "\\\"logic\\\":\\\"and\\\",\\\"name\\\":\\\"Contact Channel\\\"," +
                    "\\\"objid\\\":\\\"5b07b74bc9e77c00062e02fe\\\",\\\"origin\\\":\\\"tree\\\"," +
                    "\\\"path\\\":\\\"Carrefour Demo / Contact Channel\\\",\\\"sourceType\\\":\\\"TAXONOMY\\\"," +
                    "\\\"style\\\":\\\"checkbox\\\",\\\"values\\\":[{\\\"name\\\":\\\"Email\\\"," +
                    "\\\"node\\\":\\\"99011\\\",\\\"type\\\":\\\"node\\\"}]}]}],\\\"test-control\\\":0}\"," +
                    "\"destinationID\":2,\"userID\":\"\",\"delete\":false,\"tempCampaignId\":111}";
            String resp = "{\"error\":\"\"}";
            Mockito.when(audiencePoJPA
                    .findAudiencePoByAudienceTypeAndTenantIdAndName(campaignParam.getAudienceType(),
                            tenantId, campaignParam.getName())).thenReturn(null);
            Mockito.when(bitmapAPI.createBitmapForTV(rule)).thenReturn(resp);
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            audiencePoService.createCampaign(tenantId, campaignParam);
        } catch (Exception e) {
            Assert.fail("Failed to save campaign in case 3");
        }
    }

    @Test
    public void calculate() {
        try {
            String reqParams = "{\"universeSysNames\":[\"test123\"],\"campaign\":{\"cap\":0,\"rm-duplicates\":false," +
                    "\"test-control\":0,\"destination\":\"\",\"name\":\"vivian0530\"," +
                    "\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                    "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                    "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\"," +
                    "\"name\":\"Email\",\"type\":\"node\"}],\"name\":\"Contact Channel\"," +
                    "\"objid\":\"5b07b74bc9e77c00062e02fe\",\"style\":\"checkbox\"," +
                    "\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\",\"items\":[]}]," +
                    "\"frozenCount\":1322720,\"count\":1322720,\"exclude\":[]," +
                    "\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]},\"tenantPath\":\"mvpda\"}";
            String resp = "{\"data\":{\"taxonomyid\":\"110111\",\"count\":1000}}";
            Mockito.when(universePoJPA.findAll(Lists.newArrayList(destinationId))).thenReturn(Lists.newArrayList(model.universePo));
            Mockito.when(bitmapAPI.calculateV2(reqParams)).thenReturn(resp);
            audiencePoService.calculateV2(Lists.newArrayList(destinationId), model.rule);
        } catch (Exception e) {
            Assert.fail("Failed to calculate campaign");
        }
    }

    @Test
    public void refreshCampaign() {
        AudiencePo campaign = new AudiencePo();
        campaign.setAudienceType(FolderType.CAMPAIGN);
        campaign.setCount(1000L);
        campaign.setRuleJson(model.rule);
        campaign.setName("test_new");
        campaign.setTenantId(1L);
        campaign.setId(1L);
        campaign.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        campaign.setCreatedBy("amsdemo");
        campaign.setTaxonomyId("110111");
        campaign.setCreatedTime(new Date());
        campaign.setFolderPo(model.campaignFolder);
        campaign.setUniverseIds("1");
        List<Long> folderIdList = Lists.newArrayList(1L);
        List<Long> audienceIdList = Lists.newArrayList(1L);
        String owner = "test";
        Mockito.when(audiencePoJPA.getSegmentListByFolderId(StringUtils.join(folderIdList, ","))).thenReturn(Lists
                .newArrayList(model.campaign_distributed));
        FolderAndAudience folderAndAudience = new FolderAndAudience(folderIdList, audienceIdList, owner);
        // case 1: No permission to refresh
        try {
            audiencePoService.refreshCampaign(folderAndAudience);
            Assert.fail("Failed to refresh campaign in case 1");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to refresh campaign in case 1", StringUtils.equals(e.getCode(), "020206"));
        }
        // case 2: This status is not allowed to be refreshed
        try {
            folderAndAudience.setOwner("amsdemo");
            audiencePoService.refreshCampaign(folderAndAudience);
            Assert.fail("Failed to refresh campaign in case 2");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to refresh campaign in case 2", StringUtils.equals(e.getCode(), "020215"));
        }

        // case 3: No exception
        try {
            Mockito.when(audiencePoJPA.getSegmentListByFolderId(StringUtils.join(folderIdList, ","))).thenReturn(Lists
                    .newArrayList(campaign));
            String reqParams = "{\"universeSysNames\":[\"test123\"],\"campaign\":{\"cap\":0,\"rm-duplicates\":false," +
                    "\"test-control\":0,\"destination\":\"\",\"name\":\"vivian0530\"," +
                    "\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                    "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                    "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\"," +
                    "\"name\":\"Email\",\"type\":\"node\"}],\"name\":\"Contact Channel\"," +
                    "\"objid\":\"5b07b74bc9e77c00062e02fe\",\"style\":\"checkbox\"," +
                    "\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\",\"items\":[]}]," +
                    "\"frozenCount\":1322720,\"count\":1322720,\"exclude\":[]," +
                    "\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]},\"tenantPath\":\"mvpda\"}";
//            String resp = "{\"data\":{\"total\":\"1500\",\"counts\":{\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\":1000}," +
//                    "\"nativeCounts\":{\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\":1200}}}";
            String resp = "{\"data\":{\"universe_tv50011\":{\"total\":1500,\"counts\":{\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\":1000}," +
                    "\"nativeCounts\":{\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\":1000}}}}";
            Mockito.when(bitmapAPI.calculateV2(reqParams)).thenReturn(resp);
            Mockito.when(universePoJPA.findAll(Lists.newArrayList(destinationId))).thenReturn(Lists.newArrayList(model.universePo));
            audiencePoService.refreshCampaign(folderAndAudience);
            ArgumentCaptor<List> personCaptor = ArgumentCaptor.forClass(List.class);
            verify(audiencePoJPA).save(personCaptor.capture());
            List<AudiencePo> request = personCaptor.getValue();
            Assert.assertTrue("Failed to refresh campaign in case 3", request.get(0).getCount() == 1500);
        } catch (AMSException e) {
            Assert.fail("Failed to refresh campaign in case 3");
        }
    }

    @Test
    public void updateCampaign() throws AMSInvalidInputException {
        AudiencePo campaign = new AudiencePo();
        campaign.setAudienceType(FolderType.CAMPAIGN);
        campaign.setCount(1000L);
        campaign.setRuleJson(model.rule);
        campaign.setName("test_new");
        campaign.setTenantId(1L);
        campaign.setId(1L);
        campaign.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        campaign.setCreatedBy("amsdemo");
        campaign.setTaxonomyId("110111");
        campaign.setCreatedTime(new Date());
        campaign.setFolderPo(model.campaignFolder);

        Long audienceId = 1L;
        CampaignParam campaignParam = new CampaignParam();
        campaignParam.setName("campaign-1101");
        campaignParam.setRule(model.rule);
        campaignParam.setCreatedBy("test");
        campaignParam.setAudienceType(FolderType.SAVED_SEGMENT);
        campaignParam.setFolderId(3L);
        campaignParam.setUserId(model.userId);
        campaignParam.setUniverseIdList(Lists.newArrayList(2L));
       // campaignParam.setTempCampaignId(111L);
        campaignParam.setAudienceCount(100L);
        campaignParam.setCost("10");
        campaignParam.setDescription("test");
        campaignParam.setSegmentCode("11");
        List<AudienceCountByUniverseDTO> audienceCountByUniverseDTOList = new ArrayList<>();
        AudienceCountByUniverseDTO audienceCountByUniverseDTO = new AudienceCountByUniverseDTO(1L,1000L, null);
        audienceCountByUniverseDTOList.add(audienceCountByUniverseDTO);
        campaignParam.setAudienceCountByUniverseDTOList(audienceCountByUniverseDTOList);
        Mockito.when(universeService.getUniverseById(2L)).thenReturn(model.SUCCESS_UNIVERSE);
        // case 1: Audience does not exist
        try {
            audiencePoService.updateCampaign(tenantId, audienceId, campaignParam);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update campaign in case 1", StringUtils.equals("020221", e.getCode()));
        }
        // case 2: No permission updates
        try {
            Mockito.when(audiencePoJPA.findOne(audienceId)).thenReturn(model.campaign_distributed);
            Mockito.when(folderService.getFolderById(campaignParam.getFolderId())).thenReturn(model.campaignFolder);
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            audiencePoService.updateCampaign(tenantId, audienceId, campaignParam);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update campaign in case 2", StringUtils.equals("020206", e.getCode()));
        }
        // case 3: This status is not allowed to be updated
        try {
            campaignParam.setCreatedBy(model.createdBy);
            audiencePoService.updateCampaign(tenantId, audienceId, campaignParam);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update campaign in case 3", StringUtils.equals("020217", e.getCode()));
        }
        // case 4: audience count under limit
        try {
            Mockito.when(audiencePoJPA.findOne(audienceId)).thenReturn(campaign);
            audiencePoService.updateCampaign(tenantId, audienceId, campaignParam);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update campaign in case 4", StringUtils.equals("020268", e.getCode()));
        }
        // case 5:Audience already exists
        try {
            campaignParam.setAudienceCount(800L);
            Mockito.when(audiencePoJPA
                    .findAudiencePoByAudienceTypeAndTenantIdAndName(campaignParam.getAudienceType(),
                            tenantId, campaignParam.getName())).thenReturn(model.campaign_new);
            audiencePoService.updateCampaign(tenantId, audienceId, campaignParam);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update campaign in case 5", StringUtils.equals("020213", e.getCode()));
        }
        // case 6:no exception
        try {
            String rule = "{\"clientID\":\"mvpda\",\"campaignId\":1,\"tenantID\":1,\"rule\":\"{\\\"cap\\\":0," +
                    "\\\"defaultrule\\\":[],\\\"destination\\\":\\\"\\\"," +
                    "\\\"id\\\":\\\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\\\",\\\"name\\\":\\\"vivian0530\\\"," +
                    "\\\"rm-duplicates\\\":false,\\\"segments\\\":[{\\\"count\\\":1322720,\\\"exclude\\\":[]," +
                    "\\\"frozenCount\\\":1322720,\\\"id\\\":\\\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\\\"," +
                    "\\\"include\\\":[{\\\"id\\\":\\\"7b8d706e-6124-7df0-5e0d-c1f025962b30\\\",\\\"items\\\":[]," +
                    "\\\"logic\\\":\\\"and\\\",\\\"name\\\":\\\"Contact Channel\\\"," +
                    "\\\"objid\\\":\\\"5b07b74bc9e77c00062e02fe\\\",\\\"origin\\\":\\\"tree\\\"," +
                    "\\\"path\\\":\\\"Carrefour Demo / Contact Channel\\\",\\\"sourceType\\\":\\\"TAXONOMY\\\"," +
                    "\\\"style\\\":\\\"checkbox\\\",\\\"values\\\":[{\\\"name\\\":\\\"Email\\\"," +
                    "\\\"node\\\":\\\"99011\\\",\\\"type\\\":\\\"node\\\"}]}]}],\\\"test-control\\\":0}\"," +
                    "\"destinationID\":2,\"userID\":\"\",\"delete\":false,\"tempCampaignId\":111}";
            String resp = "{\"error\":\"\"}";
            Mockito.when(audiencePoJPA
                    .findAudiencePoByAudienceTypeAndTenantIdAndName(campaignParam.getAudienceType(),
                            tenantId, campaignParam.getName())).thenReturn(null);
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            Mockito.when(bitmapAPI.createBitmapForTV(rule)).thenReturn(resp);
            audiencePoService.updateCampaign(tenantId, audienceId, campaignParam);
        } catch (Exception e) {
            Assert.fail("Failed to update campaign in case 6");
        }
    }

    @Test
    public void copyCampaign() {
        AudienceParam audienceParam = new AudienceParam("test", "amsdemo");
        Long audienceId = 1L;
        // case 1:Audience does not exist
        try {
            audiencePoService.copyCampaign(audienceId, audienceParam);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to copy campaign in case 1", StringUtils.equals("020221", e.getCode()));
        }
        //case 2:no exception
        try {
            Mockito.when(audiencePoJPA.findOne(audienceId)).thenReturn(model.campaign_distributed);
            String reqParams = "{\"universeSysNames\":[\"test123\"],\"campaign\":{\"cap\":0,\"rm-duplicates\":false," +
                    "\"campaignId\":0,\"test-control\":0,\"destination\":\"\",\"name\":\"vivian0530\"," +
                    "\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                    "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                    "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\"," +
                    "\"name\":\"Email\",\"type\":\"node\"}],\"name\":\"Contact Channel\"," +
                    "\"objid\":\"5b07b74bc9e77c00062e02fe\",\"style\":\"checkbox\"," +
                    "\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\",\"items\":[]}]," +
                    "\"frozenCount\":1322720,\"count\":1322720,\"exclude\":[]," +
                    "\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]},\"tenantPath\":\"mvpda\"}";
            String resp = "{\"data\":{\"universe_tv50011\":{\"total\":1500,\"counts\":{\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\":1000}," +
                    "\"nativeCounts\":{\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\":1200}}}}";
            Mockito.when(bitmapAPI.calculateV2(reqParams)).thenReturn(resp);
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.tenantPo);
            Mockito.when(universePoJPA.findAll(Lists.newArrayList(destinationId))).thenReturn(Lists.newArrayList(model.universePo));
            audiencePoService.copyCampaign(audienceId, audienceParam);
        } catch (AMSException e) {
            Assert.fail("Fialed to copy campaign in case 2");
        }
    }

    @Test
    public void getCampaignStatusByIds() {
        Long[] campaignIds = new Long[]{1L, 2L};
        Mockito.when(audiencePoJPA
                .findAudiencePoByIdInAndAudienceType(Arrays.asList(campaignIds), FolderType.CAMPAIGN)).thenReturn
                (Lists.newArrayList(model.campaign_new));
        List<AudiencePo> audiencePoList = audiencePoService.getCampaignStatusByIds(campaignIds);
        Assert.assertTrue("Failed to get campaign status", audiencePoList.size() == 1);
    }

    @Test
    public void exportCampaign() {
        FolderAndAudience folderAndAudience = new FolderAndAudience(Lists.newArrayList(1L), Lists.newArrayList(1L),
                "amsdemo");
        // case 1: audience is not exist
        try {
            audiencePoService.exportCampaign(folderAndAudience, tenantId);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to export campaign", StringUtils.equals(e.getCode(), "020228"));
        }
        Mockito.when(audiencePoJPA
                .findAudiencePoByIdInAndAudienceType(folderAndAudience.getAudienceIdList(),
                        FolderType.CAMPAIGN)).thenReturn(Lists.newArrayList(model.campaign));
        try {
            ReflectionTestUtils.setField(audiencePoService, "exportTemplateFile", "./init/tmp/CampaignTemplate.xlsx");
            ReflectionTestUtils.setField(audiencePoService, "tempFile", "/tmp");
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
            Mockito.when(universePoJPA.findAll(Lists.newArrayList(destinationId))).thenReturn(Lists.newArrayList(model.universePo));
            Mockito.when(tenantService.getTenantById(23L)).thenReturn(model.tenantPo);
            audiencePoService.exportCampaign(folderAndAudience, tenantId);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to export campaign", StringUtils.equals(e.getCode(), "020228"));
        }
    }

    @Test
    public void getDataStoreNodeList() {
        try {
            Mockito.when(audienceDistributeJobPoJPA
                    .findByAudienceTypeAndStatus(FolderType.CAMPAIGN, SegmentStatusType.CAMPAIGN_DISTRIBUTED))
                    .thenReturn(Lists.newArrayList(model.audienceDistributeJobPo));
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            List<DataStoreVo> dataStoreVoList = audiencePoService.getDataStoreNodeList();
            Assert.assertTrue("Failed to get data store node list", dataStoreVoList.size() == 1);
        } catch (AMSException e) {
            Assert.fail("Failed to get data store node list");
        }

    }

    @Test
    public void getAttributeByNodeId() {
        try {
            List<String> nodeIds = Lists.newArrayList("99011", "99012");
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.tenantPo);
            TenantPo[] tenantPos = new TenantPo[]{model.tenantPo};
            Mockito.when(versionPoJPA.findAllByTenantPoInAndOperationFlag(tenantPos, TemplateStatusType.ACTIVE))
                    .thenReturn(Lists.newArrayList(model.versionPo));
            Mockito.when(taxonomyAPI.getTaxonomyListByNodeIdAndName(String.valueOf(model.versionPo.getId()), nodeIds))
                    .thenReturn(Lists.newArrayList(model.taxonomy));
            Mockito.when(taxonomyAPI.getTaxonomyListByNodeIdAndName(String.valueOf(model.versionPoInfoBase.getId()),
                    nodeIds))
                    .thenReturn(Lists.newArrayList(model.taxonomy1));
            Mockito.when(taxonomyMapper.map(Lists.newArrayList(model.taxonomy)))
                    .thenReturn(Lists.newArrayList(model.parseTaxonomy(model.taxonomy)));
            Mockito.when(taxonomyMapper.map(Lists.newArrayList(model.taxonomy1)))
                    .thenReturn(Lists.newArrayList(model.parseTaxonomy(model.taxonomy1)));
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            List<TaxonomyItemVo> taxonomyItemVoList = audiencePoService.getAttributeByNodeIdAndNameV2(nodeIds,
                    destinationId);
            Assert.assertTrue("Failed to get attribute by node id", taxonomyItemVoList.size() == 1);
        } catch (AMSException e) {
            Assert.fail("Failed to get attribute by node id");
        }

    }

    @Test
    public void getAllOwnerType() {
        try {
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.tenantPo);
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.infoBaseTenant);
            Mockito.when(versionPoJPA.findAllByTenantPoInAndOperationFlag(
                    new TenantPo[]{model.tenantPo, model.infoBaseTenant}, TemplateStatusType.ACTIVE))
                    .thenReturn(Lists.newArrayList(model.versionPo, model.versionPoInfoBase));
            List<String> ownerList = Lists.newArrayList("amsdemo");
            Mockito.when(tenantPoJPA.findByNameIn(ownerList)).thenReturn(Lists.newArrayList(model.tenantPo));
            Mockito.when(userCenterAPI.getTenantExtByKey(model.tenantPo.getTenantId(), "data_type"))
                    .thenReturn(new TenantExtVo("", "", ""));
            List<NodeDTO> nodeDTOList = Lists.newArrayList(new NodeDTO(Lists.newArrayList("99011")));
            List<OwnerTypeVo> ownerTypeVoList = audiencePoService.getOwnerTypeV2(ownerList, nodeDTOList,
                    null, 1L);
            Assert.assertTrue("Failed to get all owner type", ownerTypeVoList.size() == 1);
        } catch (AMSException e) {
            Assert.fail("Failed to get all owner type");
        }
    }

    @Test
    public void getAllOwnerAndDataType() {
        try {
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.tenantPo);
            Mockito.when(versionPoJPA.findAllByTenantPoInAndOperationFlag(
                    new TenantPo[]{model.tenantPo}, TemplateStatusType.ACTIVE))
                    .thenReturn(Lists.newArrayList(model.versionPo));
            Mockito.when(audiencePoJPA.findOne(4L)).thenReturn(model.campaign);
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            List<String> ownerList = Lists.newArrayList("amsdemo");
            List<NodeDTO> nodeDTOList = Lists.newArrayList(new NodeDTO(Lists.newArrayList("99011")));
            List<SegmentDTO> segmentDTOList = Lists.newArrayList(new SegmentDTO(
                    Lists.newArrayList("d62d3185-7722-3fa5-59c5-037e9a3bf7a2",
                            "4b2fb0c9-480e-cc61-a016-4bb2d3368370"), 4L));
            Mockito.when(taxonomyAPI.getAttributeOwnerAndDataTypeByNodeIdAndName(
                    String.valueOf(model.versionPo.getId()), nodeDTOList)).thenReturn(Lists.newArrayList(new
                    OwnerAndDataType("amsdemo", "3p")));
            List<OwnerAndDataType> ownerAndDataTypeList = audiencePoService.getOwnerTypeV3(ownerList, nodeDTOList,
                    segmentDTOList, 1L);
            Assert.assertTrue("Failed to get all owner and data type", ownerAndDataTypeList.size() == 3);
        } catch (AMSException e) {
            Assert.fail("Failed to get all owner and data type");
        }
    }

    @Test
    public void listPriceAndOwnerByTaxonomyId() {
        try {
            List<String> taxonomyIdList = Lists.newArrayList("99011", "99012");
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.tenantPo);
            Mockito.when(versionPoJPA.findAllByTenantPoInAndOperationFlag(
                    new TenantPo[]{model.tenantPo}, TemplateStatusType.ACTIVE))
                    .thenReturn(Lists.newArrayList(model.versionPo));
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            Mockito.when(taxonomyAPI
                    .listPriceAndOwnerByTaxonomyId(String.valueOf(model.versionPo.getId()), taxonomyIdList))
                    .thenReturn(Lists.newArrayList(new PriceAndOwnerVO("99011", 15.0, "amsdemo")));
            List<PriceAndOwnerVO> priceAndOwnerVOList = audiencePoService.listPriceAndOwnerByTaxonomyId(
                    tenantId, destinationId, taxonomyIdList);
            Assert.assertTrue("Failed to get price and owner by taxonomyId", priceAndOwnerVOList.size() == 1);
        } catch (AMSException e) {
            Assert.fail("Failed to get price and owner by taxonomyId");
        }
    }

    @Test
    public void getSegmentAndAttributeByKey() {
        try {
            String key = "Test";
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.tenantPo);
            Mockito.when(versionPoJPA
                    .findAllByTenantPoInAndOperationFlag(new TenantPo[]{model.tenantPo},
                            TemplateStatusType.ACTIVE))
                    .thenReturn(Lists.newArrayList(model.versionPo));
            Mockito.when(taxonomyAPI.getTaxonomyAttributeByKey(String.valueOf(model.versionPo.getId()), key))
                    .thenReturn(Lists.newArrayList(model.taxonomy));
            Map<String, Long> map = new HashMap<>();
            map.put("1_197_300217799", 100L);
            List<String> list = new ArrayList<>();
            list.add("1_197_300217790");
            Mockito.when(bitmapAPI.listNodeCountByTaxonomyIds(model.tenantPo, list)).thenReturn(map);
            Map<String, Long> map1 = new HashMap<>();
            map1.put("1_197_300217790", 120L);
            Mockito.when(bitmapAPI.listNodeCountByTaxonomyIds(model.tenantPo, list)).thenReturn(map);
            Mockito.when(taxonomyMapper.map(model.taxonomy.getTaxonomyIncludes())).thenReturn(Lists.newArrayList
                    (model.parseTaxonomy(model.taxonomy.getTaxonomyIncludes().get(0))));
            Mockito.when(taxonomyMapper.map(model.taxonomy1.getTaxonomyIncludes())).thenReturn(Lists.newArrayList
                    (model.parseTaxonomy(model.taxonomy1.getTaxonomyIncludes().get(0))));
            Mockito.when(audiencePoJPA
                    .findByAudienceTypeAndTenantId(FolderType.CAMPAIGN, tenantId))
                    .thenReturn(Lists.newArrayList(model.campaign));
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            CampaignAndAttributeVO campaignAndAttributeVO = audiencePoService.getSegmentAndAttributeByKeyV2
                    (Lists.newArrayList(destinationId), key, tenantId);
            Assert.assertTrue("Failed to get segment and attribute by key", campaignAndAttributeVO
                    .getSegmentVOList()
                    .size() == 1
                    && campaignAndAttributeVO.getTreeItemVoList().size() == 1);
        } catch (AMSException e) {
            Assert.fail("Failed to get segment and attribute by key");
        }
    }

    @Test
    public void listDataTypeAndPriceAndOwnerByTaxonomyIdList() {
        try {
            List<String> taxonomyIdList = Lists.newArrayList("99011", "99012");
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.tenantPo);
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            Mockito.when(versionPoJPA.findAllByTenantPoInAndOperationFlag(
                    new TenantPo[]{model.tenantPo}, TemplateStatusType.ACTIVE))
                    .thenReturn(Lists.newArrayList(model.versionPo));
            Mockito.when(taxonomyAPI
                    .listDataTypeAndPriceAndOwnerByTaxonomyIdList(String.valueOf(model.versionPo.getId()),
                            taxonomyIdList))
                    .thenReturn(Lists.newArrayList(new DataTypeAndPriceAndOwnerVO("99011", 15.0, "amsdemo", "3p")));
            List<DataTypeAndPriceAndOwnerVO> dataTypeAndPriceAndOwnerVOList = audiencePoService
                    .listDataTypeAndPriceAndOwnerByTaxonomyIdList(destinationId, taxonomyIdList);
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            Assert.assertTrue("Failed to get price and owner by taxonomyId", dataTypeAndPriceAndOwnerVOList.size
                    () ==
                    1);
        } catch (AMSException e) {
            Assert.fail("Failed to get price and owner by taxonomyId");
        }
    }

    @Test
    public void getSourceList() {
        try {
            List<Taxonomy> taxonomyList = Lists.newArrayList(model.taxonomy);
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model.tenantPo);
            Mockito.when(tenantService.getTenantById(1L)).thenReturn(model
                    .infoBaseTenant);
            Mockito.when(versionPoJPA
                    .findAllByTenantPoInAndOperationFlag(new TenantPo[]{model.infoBaseTenant, model.tenantPo},
                            TemplateStatusType.ACTIVE)).thenReturn(Lists.newArrayList(model.versionPo, model
                    .versionPoInfoBase));
            Mockito.when(taxonomyAPI.getTaxonomyList(String.valueOf(model.versionPo.getId()), model.versionPo
                    .getTreeId())).thenReturn(taxonomyList);
            Mockito.when(taxonomyAPI.getTaxonomyList(String.valueOf(model.versionPoInfoBase.getId()), model
                    .versionPoInfoBase
                    .getTreeId())).thenReturn(taxonomyList);
            Mockito.when(folderService.getParentFolderV2()).thenReturn(Lists.newArrayList(model.campaignFolder));
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            Mockito.when(dataSourceAPI.listNodeInfoByNodeIds(model.tenantPo.getTenantId(), Lists.newArrayList
                    ("8_186_99011")))
                    .thenReturn(Lists.newArrayList(new DataStoreNode("8_186_99011", "attr_14XIv23Wwz",
                            "value_PDvJ4IDCTl", 2L)));
            List<SourceItem> sourceItemList = audiencePoService.getSourceListV2(destinationId, tenantId);
            Assert.assertTrue("Failed to get source list", sourceItemList.size() == 1);
        } catch (Exception e) {
            Assert.fail("Failed to get source list");
        }
    }

    @Test
    public void listDataStoreNodesByDate() throws ParseException {
        String startDate = "2018-09-20";
        String endDate = "2018-09-21";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Mockito.when(audiencePoJPA.findAllByAudienceTypeAndCreatedTimeBetween(FolderType.CAMPAIGN, sdf.parse
                    (startDate), sdf.parse(endDate)))
                    .thenReturn(Lists.newArrayList(model.campaign));
            HashMap paramMap = new HashMap<String, Object>();
            List<String> rules = Lists.newArrayList(model.campaign.getRuleJson());
            paramMap.put("rules", rules);
            Mockito.when(tenantService.getTenantById(23L)).thenReturn(model.tenantPo);
            Mockito.when(universeService.getUniverseById(destinationId)).thenReturn(model.universePo);
            List<DataStoreNodeSortByTenantVO> resp = audiencePoService.listDataStoreNodesByDate(sdf.parse(startDate),
                    sdf.parse(endDate));
            Assert.assertTrue("Failed to get data store node list by date",
                    StringUtils.equals(resp.get(0).getTenantName(), model.tenantPo.getName()));
        } catch (AMSException e) {
            Assert.fail("Failed to get data store node list by date");
        }
    }
}