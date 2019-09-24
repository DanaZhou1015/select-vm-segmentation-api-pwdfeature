package com.acxiom.ams.controller;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.common.exception.AMSResouceRequestException;
import com.acxiom.ams.mapper.TaxonomyMapper;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.dto.AudienceStatus;
import com.acxiom.ams.model.dto.FolderAndAudience;
import com.acxiom.ams.model.dto.SegmentForCopyDTO;
import com.acxiom.ams.model.dto.TemporarySegment;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.em.SourceType;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.po.FolderPo;
import com.acxiom.ams.model.rmi.Taxonomy;
import com.acxiom.ams.model.vo.*;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.repository.FolderPoJPA;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.repository.VersionPoJPA;
import com.acxiom.ams.service.*;
import com.acxiom.ams.service.impl.AudiencePoServiceImpl;
import com.acxiom.ams.service.impl.BitmapServiceImpl;
import com.acxiom.ams.util.Constant;
import com.acxiom.ams.util.UUID;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static com.acxiom.ams.util.StringUtil.parseRegexLike;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * @Author: Fermi.Tang
 * @Date: Created in 5:49 PM 9/14/2018
 */
public class AudienceControllerTest {

    @InjectMocks
    AudiencePoServiceImpl audiencePoService;
    @InjectMocks
    @Spy
    BitmapServiceImpl bitmapService = new BitmapServiceImpl();
    @Mock
    TenantPoJPA tenantPoJPA;
    @Mock
    VersionPoJPA versionPoJPA;
    @Mock
    AudiencePoJPA audiencePoJPA;
    @Mock
    ServiceAPI.BitmapAPI bitmapAPI;
    @Mock
    ServiceAPI.TaxonomyAPI taxonomyAPI;
    @Mock
    FolderService folderService;
    @Mock
    FolderPoJPA folderPoJPA;
    @Mock
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Mock
    TenantService tenantService;
    @Mock
    VersionPoService versionPoService;
    @Mock
    TaxonomyMapper taxonomyMapper;
    @Mock
    ServiceAPI.DataSourceAPI dataSourceAPI;
    Long tenantId;
    String userId;
    Model model = Model.getInstance();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        tenantId = 1L;
    }

    @Test
    public void getSourceList() throws AMSException {
        Mockito.when(folderService.getParentFolder()).thenReturn(Lists.newArrayList(model.segmentFolder, model
                .lookAlikeFolder, model.campaignFolder));
        Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
        Mockito.when(versionPoService.getActiveVersionByTenant(model.tenantPo)).thenReturn(model
                .versionPo);
        Mockito.when(taxonomyAPI
                .getTaxonomyList(String.valueOf(model.versionPo.getId()), model.versionPo.getTreeId())).thenReturn
                (model.taxonomyList);
        List<SourceItem> sourceItemList = audiencePoService.getSourceList(tenantId);
        List<Taxonomy> taxonomyList = new ArrayList<>();
        Mockito.when(dataSourceAPI.listChildSharedTaxonomyByObjectId(model.tenantPo.getTenantId(), "")).thenReturn(taxonomyList);
        verify(folderService).getParentFolder();
        verify(taxonomyAPI).getTaxonomyList(String.valueOf(model.versionPo.getId()), model.versionPo.getTreeId());
        Assert.assertTrue("Failed to get source list", sourceItemList.size() > 3);
    }

    @Test
    public void calculate() throws AMSException {
        String reqParams = "{\"clientID\":\"test\",\"rules\":{\"cap\":0,\"rm-duplicates\":false,\"test-control\":0," +
                "\"destination\":\"\",\"name\":\"vivian0530\",\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\"," +
                "\"defaultrule\":[],\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\",\"name\":\"Email\"," +
                "\"type\":\"node\"}],\"name\":\"Contact Channel\",\"objid\":\"5b07b74bc9e77c00062e02fe\"," +
                "\"style\":\"checkbox\",\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\"," +
                "\"items\":[]}],\"frozenCount\":1322720,\"count\":1322720,\"exclude\":[]," +
                "\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]}}";
        String resp = "{\"data\":{\"taxonomyid\":\"110111\",\"count\":1000}}";
        Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
        Mockito.when(bitmapAPI.calculate(reqParams)).thenReturn(resp);
        Long count = audiencePoService.calculate(tenantId, userId, model.rule);
        Assert.assertTrue("Failed to calculate segment", count == 1000);
    }

    @Test
    public void getTreeItem() {
        try {
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            Mockito.when(versionPoService.getActiveVersionByTenant(model.tenantPo)).thenReturn(model.versionPo);
            Map<String, Long> map = new HashMap<>();
            map.put("99011", 120L);
            map.put("99012", 150L);
            Mockito.when(bitmapAPI.listNodeCountByTaxonomyIds(null, null)).thenReturn(map);
            List<SourceType> sourceTypeList = Lists.newArrayList(SourceType.TAXONOMY, SourceType.SEGMENT, SourceType.LOOKALIKE);
            List<TreeItemVo> treeItemVoList;
            List<AudienceAndFolderVo> audienceAndFolderVoList;
            String id;
            for (SourceType sourceType : sourceTypeList) {
                switch (sourceType) {
                    case TAXONOMY:
                        id = "5bc459f107f3f17a156eb63e";
                        List<Taxonomy> taxonomyList = Lists.newArrayList(model.taxonomy, model.taxonomy1);
                        Mockito.when(taxonomyAPI.getTaxonomyList(String.valueOf(model.versionPo.getId()), id))
                                .thenReturn(taxonomyList);
                        Mockito.when(taxonomyMapper.map(taxonomyList))
                                .thenReturn(Lists.newArrayList(model.parseTaxonomy(taxonomyList.get(0)), model.parseTaxonomy
                                        (taxonomyList.get(1))));
                        treeItemVoList = audiencePoService.getTreeItem(tenantId, sourceType, id);
                        Assert.assertTrue("Failed to get child taxonomy node", treeItemVoList.size() == 2);
                        break;
                    case SEGMENT:
                        id = "1s";
                        try {
                            audiencePoService.getTreeItem(tenantId, sourceType, id);
                            Assert.fail("Failed to get all segments under folder");
                        } catch (AMSException e) {
                            Assert.assertTrue("Failed to get all child segments and folders under parent folder",
                                    StringUtils.equals(e.getCode(), "020204"));
                        }
                        id = "1";
                        audienceAndFolderVoList = Lists.newArrayList(model.segmentVo, model.folderVo);
                        Mockito.when(folderService
                                .getFolderListByTenantId(Long.valueOf(id), tenantId, true,
                                        FolderType.SAVED_SEGMENT)).thenReturn(audienceAndFolderVoList);
                        treeItemVoList = audiencePoService.getTreeItem(tenantId, sourceType, id);
                        Assert.assertTrue("Failed to get all child segments and folders under parent folder",
                                treeItemVoList.size() == 2);
                        break;
                    case LOOKALIKE:
                        id = "3s";
                        try {
                            audiencePoService.getTreeItem(tenantId, sourceType, id);
                            Assert.fail("Failed to get all lookalikes under parent folder");
                        } catch (AMSException e) {
                            Assert.assertTrue("Failed to get all child lookalikes under parent folder",
                                    StringUtils.equals(e.getCode(), "020204"));
                        }
                        id = "3";
                        audienceAndFolderVoList = Lists.newArrayList(model.lookLikeVo);
                        Mockito.when(folderService
                                .getFolderListByTenantId(Long.valueOf(id), tenantId, true,
                                        FolderType.LOOKALIKE_GROUP)).thenReturn(audienceAndFolderVoList);
                        treeItemVoList = audiencePoService.getTreeItem(tenantId, sourceType, id);
                        Assert.assertTrue("Failed to get all child lookalikes under parent folder",
                                treeItemVoList.size() == 1);
                        break;
                    default:
                        break;
                }
            }
        } catch (AMSException e) {
            Assert.fail("Failed to get tree item");
        }
    }

    @Test
    public void getTreeItemStatus() {
        AudienceStatus audienceStatus = new AudienceStatus(FolderType.SAVED_SEGMENT, Lists.newArrayList(1L));
        Mockito.when(audiencePoJPA
                .findAudiencePoByIdInAndAudienceType(audienceStatus.getList(),
                        audienceStatus.getFolderType())).thenReturn(Lists.newArrayList(model.segment_new));
        Map<Long, SegmentStatusType> map = audiencePoService.getAudienceStatus(audienceStatus);
        Assert.assertTrue(SegmentStatusType.SEGMENT_NEW.equals(map.get(1L)));
    }

    @Test
    public void searchAudienceItem() {
//        try {
//            FolderType folderType = FolderType.SAVED_SEGMENT;
//            String key = "test";
//            Mockito.when(audiencePoJPA
//                    .findByNameLikeAndAudienceTypeInAndTenantIdOrderByUpdateTime("%" + key + "%",
//                            new FolderType[]{folderType}, tenantId)).thenReturn(Lists.newArrayList(model.segment_new,
//                    model.segment_distributed));
//            List<AudiencePo> audiencePoList = audiencePoService.getAudienceItemByKey(tenantId, key, folderType);
//            Assert.assertTrue("Failed to search audience by key", StringUtils.equals(audiencePoList.get(0).getName(),
//                    "test_distributed"));
//        } catch (AMSException e) {
//            Assert.fail("Failed to search audience by key");
//        }

    }

    @Test
    public void searchTreeItem() {
        try {
            ReflectionTestUtils.setField(Constant.class, "AI_USE", false);
            String key = "test";
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            Mockito.when(versionPoService.getActiveVersionByTenant(model.tenantPo)).thenReturn(model.versionPo);
            Mockito.when(audiencePoJPA
                    .findByNameLikeAndAudienceTypeInAndTenantIdOrderByUpdateTime(parseRegexLike(key),
                            new FolderType[]{FolderType.SAVED_SEGMENT, FolderType.LOOKALIKE_GROUP}, tenantId))
                    .thenReturn(Lists.newArrayList(model.segment_new));
            List<Taxonomy> taxonomyList = new ArrayList<>();
            Mockito.when(dataSourceAPI.listChildSharedTaxonomyByObjectId(model.tenantPo.getTenantId(), "")).thenReturn(taxonomyList);
            List<TreeItemVo> treeItemVoList = audiencePoService.searchTreeItem(tenantId, key);
            Assert.assertTrue("Failed to search tree and audience", treeItemVoList.size() == 1);
        } catch (AMSException e) {
            Assert.fail("Failed to search tree and audience");
        }
    }

    @Test
    public void getTaxonomyItem() {
        try {
            String id = "5bc459f107f3f17a156eb63e";
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            Mockito.when(versionPoService.getActiveVersionByTenant(model.tenantPo)).thenReturn(model.versionPo);
            List<Taxonomy> taxonomyList = Lists.newArrayList(model.taxonomy1, model.taxonomy);
            Mockito.when(taxonomyAPI.getTaxonomyListByNode(String.valueOf(model.versionPo.getId()), id))
                    .thenReturn(taxonomyList);
            Map<String, Long> map = new HashMap<>();
            map.put("99012", 1000L);
            map.put("99011", 1000L);
            List<String> taxonomyIdList = Lists.newArrayList("99012", "99011");
            Mockito.when(bitmapAPI.listNodeCountByTaxonomyIds(null, null)).thenReturn(map);
            Mockito.when(taxonomyMapper.map(taxonomyList))
                    .thenReturn(Lists.newArrayList(model.parseTaxonomy(taxonomyList.get(0)), model.parseTaxonomy
                            (taxonomyList.get(1))));
            List<TaxonomyItemVo> taxonomyItemVoList = audiencePoService.getTaxonomyTreeItemByTenant(tenantId, id);
            Assert.assertTrue("Failed to get taxonomy value node", StringUtils.equals(taxonomyItemVoList.get(0).getTaxonomyId(), "99011"));
        } catch (AMSException e) {
            Assert.fail("Failed to get taxonomy value node");
        }
    }

    @Test
    public void refreshSegment() {
        AudiencePo segment = new AudiencePo();
        segment.setAudienceType(FolderType.SAVED_SEGMENT);
        segment.setCount(1000L);
        segment.setRuleJson(model.rule);
        segment.setName("test_new");
        segment.setTenantId(1L);
        segment.setId(1L);
        segment.setSegmentStatusType(SegmentStatusType.SEGMENT_NEW);
        segment.setCreatedBy("amsdemo");
        segment.setTaxonomyId("110111");
        segment.setCreatedTime(new Date());
        AudiencePo distributedSegment = new AudiencePo();
        distributedSegment.setAudienceType(FolderType.SAVED_SEGMENT);
        distributedSegment.setCount(1000L);
        distributedSegment.setRuleJson(model.rule);
        distributedSegment.setName("test_distributed");
        distributedSegment.setTenantId(1L);
        distributedSegment.setId(2L);
        distributedSegment.setSegmentStatusType(SegmentStatusType.SEGMENT_DISTRIBUTED);
        distributedSegment.setCreatedBy("amsdemo");
        distributedSegment.setTaxonomyId("110112");
        distributedSegment.setCreatedTime(new Date());
        FolderAndAudience folderAndAudience = new FolderAndAudience(Lists.newArrayList(1L), Lists.newArrayList(2L),
                "test");
        String folderIdStr = StringUtils.join(folderAndAudience.getFolderIdList(), ",");
        Mockito.when(audiencePoJPA.getSegmentListByFolderId(folderIdStr)).thenReturn(Lists.newArrayList(segment,
                distributedSegment));
        // case 1: No permission to refresh
        try {
            audiencePoService.refreshSegment(folderAndAudience, 1L);
            Assert.fail("Failed to refresh segment in case 1");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to refresh segment in case 1", StringUtils.equals(e.getCode(), "020206"));
        }

        // case 2: Status has no permission to refresh
        folderAndAudience.setOwner("amsdemo");
        try {
            audiencePoService.refreshSegment(folderAndAudience, 1L);
            Assert.fail("Failed to refresh segment in case 2");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to refresh segment in case 2", StringUtils.equals(e.getCode(), "020215"));
        }

        // case 3:No exception
        try {
            Mockito.when(audiencePoJPA.getSegmentListByFolderId(folderIdStr)).thenReturn(Collections.emptyList());
            List<AudiencePo> audiencePoList = Lists.newArrayList(segment);
            Mockito.when(audiencePoJPA.getSegmentListByFolderId(folderIdStr)).thenReturn(audiencePoList);
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            String calculateReqParams = "{\"clientID\":\"test\",\"rules\":{\"cap\":0,\"rm-duplicates\":false," +
                    "\"test-control\":0,\"destination\":\"\",\"name\":\"vivian0530\"," +
                    "\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                    "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                    "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\"," +
                    "\"name\":\"Email\",\"type\":\"node\"}],\"name\":\"Contact Channel\"," +
                    "\"objid\":\"5b07b74bc9e77c00062e02fe\",\"style\":\"checkbox\"," +
                    "\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\",\"items\":[]}]," +
                    "\"frozenCount\":1322720,\"count\":1322720,\"exclude\":[]," +
                    "\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]},\"userID\":\"\"}";
            String createReqParams = "{\"clientID\":\"test\",\"taxonomyid\":\"110111\",\"tenantID\":1," +
                    "\"rules\":{\"cap\":0,\"rm-duplicates\":false,\"test-control\":0,\"destination\":\"\"," +
                    "\"name\":\"vivian0530\",\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                    "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                    "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\"," +
                    "\"name\":\"Email\",\"type\":\"node\"}],\"name\":\"Contact Channel\",\"objid\":\"5b07b74bc9e77c00062e02fe\"," +
                    "\"style\":\"checkbox\",\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\",\"items\":[]}]," +
                    "\"frozenCount\":1000,\"count\":1000,\"exclude\":[],\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]}," +
                    "\"frozenFlag\":true,\"userID\":\"\"}";
            String resp = "{\"data\":{\"taxonomyid\":\"110111\",\"count\":1000,\"finishFlag\":true}}";
            Mockito.when(bitmapAPI.calculateForNonTV(calculateReqParams)).thenReturn(resp);
            Mockito.when(bitmapAPI.createBitmapV7(createReqParams)).thenReturn(resp);
            audiencePoService.refreshSegment(folderAndAudience, 1L);
//            verify(audiencePoJPA).save(audiencePoList);
        } catch (AMSException e) {
            Assert.fail("Failed to refresh segment in case 3");
        }

    }

    @Test
    public void getSegment() throws AMSInvalidInputException {
        Long audienceId = 1L;
        Mockito.when(audiencePoJPA.findOne(audienceId)).thenReturn(model.segment_new);
        AudiencePo audiencePo = audiencePoService.getSegment(audienceId);
        Assert.assertTrue("Failed to get segment bu audience id", audiencePo.getId() == 1L);
    }

    @Test
    public void updateSegment() {
        Long tenantId = 1L;
        Long audienceId = 1L;
        TemporarySegment temporarySegment = new TemporarySegment();
        temporarySegment.setName("segment-1023");
        temporarySegment.setRule(model.rule);
        temporarySegment.setCreatedBy("test");
        temporarySegment.setAudienceType(FolderType.SAVED_SEGMENT);
        temporarySegment.setFolderId(1L);
        temporarySegment.setUserId(model.userId);
        temporarySegment.setCount(1L);
        String taxonomyId = UUID.GetTaxonomyID();
        // case 1: Audience is not exist
        try {
            audiencePoService.updateSegment(tenantId, audienceId, temporarySegment);
            Assert.fail("Failed to update segment in case 1");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update segment in case 1", StringUtils.equals(e.getCode(), "020221"));
        }
        // case 2: owner is different
        try {
            Mockito.when(audiencePoJPA.findByIdAndTenantId(audienceId, tenantId)).thenReturn(model.segment_distributed);
            Mockito.when(folderService.getFolderById(temporarySegment.getFolderId())).thenReturn(model.segmentFolder);
            audiencePoService.updateSegment(tenantId, audienceId, temporarySegment);
            Assert.fail("Failed to update segment in case 2");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update segment in case 2", StringUtils.equals(e.getCode(), "020206"));
        }
        // case 3: Audience is not allowed to update
        try {
            temporarySegment.setCreatedBy(model.createdBy);
            audiencePoService.updateSegment(tenantId, audienceId, temporarySegment);
            Assert.fail("Failed to update segment in case 3");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update segment in case 3", StringUtils.equals(e.getCode(), "020217"));
        }
        // case 4:
        String reqParams = "{\"clientID\":\"test\",\"taxonomyid\":\"110111\",\"tenantID\":1,\"rules\":{\"cap\":0," +
                "\"rm-duplicates\":false,\"test-control\":0,\"destination\":\"\",\"name\":\"vivian0530\"," +
                "\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\",\"name\":\"Email\"," +
                "\"type\":\"node\"}],\"name\":\"Contact Channel\",\"objid\":\"5b07b74bc9e77c00062e02fe\"," +
                "\"style\":\"checkbox\",\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\"," +
                "\"items\":[]}],\"frozenCount\":1322720,\"count\":1322720,\"exclude\":[]," +
                "\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]},\"frozenFlag\":true," +
                "\"userID\":\"cd59e860-543a-4b2f-8920-9412e590afb2\"}";
        try {
            Mockito.when(audiencePoJPA.findByIdAndTenantId(audienceId, tenantId)).thenReturn(model.segment_new);
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            CreateNonTVBitmapVO createNonTVBitmapVO = new CreateNonTVBitmapVO();
            createNonTVBitmapVO.setTaxonomyId(model.segment_new.getTaxonomyId());
            createNonTVBitmapVO.setCount(1000L);
            String resp = "{\"data\":{\"taxonomyid\":\"110111\",\"count\":1000,\"finishFlag\":true}}";
            Mockito.when(bitmapAPI.createBitmapV7(reqParams)).thenReturn(resp);
            String targetId = "0,1";
            List<Long> targetIdList = Lists.newArrayList(1L);
            Mockito.when(folderPoJPA.getParentList(String.valueOf(temporarySegment.getFolderId()))).thenReturn
                    (targetId);
            List<FolderPo> targetFolderPoList = Lists.newArrayList(model.segmentFolder);
            Mockito.when(folderPoJPA.getFolderPoByIdIn(targetIdList)).thenReturn(targetFolderPoList);
            audiencePoService.updateSegment(tenantId, audienceId, temporarySegment);
            verify(folderPoJPA).save(targetFolderPoList);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update segment in case 4", StringUtils.equals(e.getCode(), "020213"));
        }
    }

    @Test
    public void saveSegment() throws AMSRMIException {
        TemporarySegment temporarySegment = new TemporarySegment();
        temporarySegment.setName("segment-1023");
        temporarySegment.setRule(model.rule);
        temporarySegment.setCreatedBy(model.createdBy);
        temporarySegment.setAudienceType(FolderType.SAVED_SEGMENT);
        temporarySegment.setFolderId(1L);
        temporarySegment.setUserId(model.userId);
        temporarySegment.setCount(1L);
        String taxonomyId = UUID.GetTaxonomyID();
        // case 3 : calculate count under tenant min count limit
        try {
            Mockito.when(folderService.getFolderById(temporarySegment.getFolderId())).thenReturn(model.segmentFolder);
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            Mockito.when(tenantPoJPA.findTenantPoById(tenantId)).thenReturn(model.tenantPo);
            String reqParams = "{\"clientID\":\"test\",\"taxonomyid\":\"" + taxonomyId + "\"," +
                    "\"tenantID\":1,\"rules\":{\"cap\":0,\"rm-duplicates\":false,\"test-control\":0,\"destination\":\"\"," +
                    "\"name\":\"vivian0530\",\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                    "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                    "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\",\"name\":\"Email\",\"type\":\"node\"}]," +
                    "\"name\":\"Contact Channel\",\"objid\":\"5b07b74bc9e77c00062e02fe\",\"style\":\"checkbox\"," +
                    "\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\",\"items\":[]}],\"frozenCount\":1322720," +
                    "\"count\":1322720,\"exclude\":[],\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]},\"frozenFlag\":true," +
                    "\"userID\":\"cd59e860-543a-4b2f-8920-9412e590afb2\"}";
            String resp = "{\"data\":{\"taxonomyid\":\"110111\",\"count\":180}}";
            Mockito.when(bitmapAPI.createBitmapV7(reqParams)).thenReturn(resp);
            audiencePoService.saveSegment(tenantId, taxonomyId, temporarySegment);
            Assert.fail("Failed to save segment in case 3");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to save segment", StringUtils.equals(e.getCode(), "020216"));
        }
        // case 4: audience is already exist
        try {
            String reqParams = "{\"clientID\":\"test\",\"taxonomyid\":\"" + taxonomyId + "\",\"tenantID\":1," +
                    "\"rules\":{\"cap\":0,\"rm-duplicates\":false,\"test-control\":0,\"destination\":\"\"," +
                    "\"name\":\"vivian0530\",\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                    "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\",\"sourceType\":\"TAXONOMY\"," +
                    "\"origin\":\"tree\",\"values\":[{\"node\":\"99011\",\"name\":\"Email\",\"type\":\"node\"}]," +
                    "\"name\":\"Contact Channel\",\"objid\":\"5b07b74bc9e77c00062e02fe\",\"style\":\"checkbox\"," +
                    "\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\",\"items\":[]}],\"frozenCount\":1322720," +
                    "\"count\":1322720,\"exclude\":[],\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]},\"frozenFlag\":true," +
                    "\"userID\":\"cd59e860-543a-4b2f-8920-9412e590afb2\"}";
            String resp = "{\"data\":{\"taxonomyid\":\"110111\",\"count\":280,\"finishFlag\":true}}";
            Mockito.when(bitmapAPI.createBitmapV7(reqParams)).thenReturn(resp);
            Mockito.when(audiencePoJPA
                    .findAudiencePoByAudienceTypeAndTenantIdAndName(temporarySegment.getAudienceType(),
                            tenantId, temporarySegment.getName())).thenReturn(model.segment_new);
            audiencePoService.saveSegment(tenantId, taxonomyId, temporarySegment);
            Assert.fail("Failed to save segment in case 4");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to save segment: audience is already exist", StringUtils.equals(e.getCode(),
                    "020213"));
        }
        // case 5 : no exception
        temporarySegment.setCount(500L);
        try {
            Mockito.when(audiencePoJPA
                    .findAudiencePoByAudienceTypeAndTenantIdAndName(temporarySegment.getAudienceType(),
                            tenantId, temporarySegment.getName())).thenReturn(null);
            audiencePoService.saveSegment(tenantId, taxonomyId, temporarySegment);
            ArgumentCaptor<AudiencePo> personCaptor = ArgumentCaptor.forClass(AudiencePo.class);
            verify(audiencePoJPA, times(1)).save(personCaptor.capture());
            AudiencePo audiencePo = personCaptor.getValue();
            Assert.assertTrue("Failed to save segment", SegmentStatusType.SEGMENT_NEW.equals
                    (audiencePo.getSegmentStatusType()));
        } catch (Exception e) {
            Assert.fail("Failed to save segment in case 5");
        }
    }

    @Test
    public void copySegment() {
        SegmentForCopyDTO segmentForCopyDTO = new SegmentForCopyDTO("test", "amsdemo", model.userId);
        String taxonomyId = UUID.GetTaxonomyID();
        AudiencePo audiencePo = model.segment_distributed;
        AudiencePo copyAudiencePo = new AudiencePo();
        copyAudiencePo.setName(audiencePo.getName() + Constant.COPY_FIX);
        Mockito.when(audiencePoJPA.findOne(2L)).thenReturn(audiencePo);
        Mockito.when(audiencePoJPA.findAudiencePoByNameLikeAndTenantId(audiencePo.getName() + Constant.COPY_FIX +
                "%", audiencePo.getTenantId())).thenReturn(Lists.newArrayList(copyAudiencePo));
        try {
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            String calculateReqParams = "{\"clientID\":\"test\",\"rules\":{\"cap\":0,\"rm-duplicates\":false,\"test-control\":0," +
                    "\"destination\":\"\",\"name\":\"vivian0530\",\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                    "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\",\"sourceType\":\"TAXONOMY\"," +
                    "\"origin\":\"tree\",\"values\":[{\"node\":\"99011\",\"name\":\"Email\",\"type\":\"node\"}]," +
                    "\"name\":\"Contact Channel\",\"objid\":\"5b07b74bc9e77c00062e02fe\",\"style\":\"checkbox\"," +
                    "\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\",\"items\":[]}],\"frozenCount\":1322720," +
                    "\"count\":1322720,\"exclude\":[],\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]},\"userID\":\"\"}";
            String reqParams = "{\"clientID\":\"test\",\"taxonomyid\":\"" + taxonomyId + "\",\"tenantID\":1," +
                    "\"rules\":{\"cap\":0,\"rm-duplicates\":false,\"test-control\":0,\"destination\":\"\"," +
                    "\"name\":\"vivian0530\",\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                    "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                    "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\",\"name\":\"Email\",\"type\":\"node\"}]," +
                    "\"name\":\"Contact Channel\",\"objid\":\"5b07b74bc9e77c00062e02fe\",\"style\":\"checkbox\"," +
                    "\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\",\"items\":[]}]," +
                    "\"frozenCount\":1322720,\"count\":1322720,\"exclude\":[],\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]}," +
                    "\"frozenFlag\":true,\"userID\":\"cd59e860-543a-4b2f-8920-9412e590afb2\"}";
            String resp = "{\"data\":{\"taxonomyid\":\"110111\",\"count\":280,\"finishFlag\":true}}";
            Mockito.when(bitmapAPI.calculateForNonTV(calculateReqParams)).thenReturn(resp);
            Mockito.when(bitmapAPI.createBitmapV7(reqParams)).thenReturn(resp);
            audiencePoService.copySegment(audiencePo.getId(), taxonomyId, segmentForCopyDTO);
            ArgumentCaptor<AudiencePo> personCaptor = ArgumentCaptor.forClass(AudiencePo.class);
            verify(audiencePoJPA, times(1)).save(personCaptor.capture());
            AudiencePo requestParam = personCaptor.getValue();
            Assert.assertTrue("Failed to copy segment!", StringUtils.equals(requestParam.getName(), audiencePo
                    .getName() + "_copy2")
                    && SegmentStatusType.SEGMENT_NEW.equals(requestParam.getSegmentStatusType()));
        } catch (Exception e) {
            Assert.fail("Failed to copy segment!");
        }
    }

    @Test
    public void getAttributeByNodeId() {
        try {
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            Mockito.when(versionPoService.getActiveVersionByTenant(model.tenantPo)).thenReturn(model.versionPo);
            List<Taxonomy> taxonomyList = Lists.newArrayList(model.taxonomy1, model.taxonomy);
            List<String> nodeIds = Lists.newArrayList("99011", "99012");
            Mockito.when(taxonomyAPI.getTaxonomyListByNodeIdAndName(String.valueOf(model.versionPo.getId()), nodeIds))
                    .thenReturn(taxonomyList);
            Map<String, Long> map = new HashMap<>();
            map.put("99012", 1000L);
            map.put("99011", 1000L);
            List<String> list = new ArrayList<>();
            list.add("1_197_300217790");
            Mockito.when(bitmapAPI.listNodeCountByTaxonomyIds(model.tenantPo, list)).thenReturn(map);
            Mockito.when(taxonomyMapper.map(taxonomyList))
                    .thenReturn(Lists.newArrayList(model.parseTaxonomy(taxonomyList.get(0)), model.parseTaxonomy
                            (taxonomyList.get(1))));
            List<TaxonomyItemVo> taxonomyItemVoList = audiencePoService.getAttributeByNodeIdAndName(tenantId, nodeIds);
            Assert.assertTrue("Failed to get attribute by node id",
                    StringUtils.equals(taxonomyItemVoList.get(0).getTaxonomyId(), "99011"));
        } catch (AMSException e) {
            Assert.fail("Failed to get attribute by node id");
        }
    }

    @Test
    public void listNodeInfoByTenantIdAndTaxonomyIdList() {
        List<String> taxonomyIdList = Lists.newArrayList("99011", "99012");
        try {
            Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
            Mockito.when(versionPoService.getActiveVersionByTenant(model.tenantPo)).thenReturn(model.versionPo);
            audiencePoService.listNodeInfoByTenantIdAndTaxonomyIdList(tenantId, taxonomyIdList);
            verify(taxonomyAPI).listDataTypeAndPriceAndOwnerByTaxonomyIdList(String.valueOf(model.versionPo.getId()),
                    taxonomyIdList);
        } catch (Exception e) {
            Assert.fail("Failed to get node info");
        }
    }
}