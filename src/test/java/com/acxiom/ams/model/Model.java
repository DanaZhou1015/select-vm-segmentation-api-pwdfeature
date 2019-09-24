package com.acxiom.ams.model;

import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.em.TemplateStatusType;
import com.acxiom.ams.model.em.UniverseType;
import com.acxiom.ams.model.po.*;
import com.acxiom.ams.model.rmi.Taxonomy;
import com.acxiom.ams.model.vo.AudienceAndFolderVo;
import com.acxiom.ams.model.vo.TaxonomyItemVo;
import com.google.common.collect.Lists;
import lombok.Data;

import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 3:18 PM 10/17/2018
 */
@Data
public class Model {
    public static AudiencePo segment_new = new AudiencePo();
    public static AudiencePo segment_distributing = new AudiencePo();
    public static AudiencePo segment_distributed = new AudiencePo();
    public static AudiencePo segment_distributed_failed = new AudiencePo();
    public static AudiencePo segment_count_under_limit = new AudiencePo();
    public static AudiencePo campaign_new = new AudiencePo();
    public static AudiencePo campaign = new AudiencePo();
    public static AudiencePo campaign_distributed = new AudiencePo();
    public static AudiencePo campaign_distributed_failed = new AudiencePo();
    public static AudiencePo campaign_count_under_limit = new AudiencePo();
    public static FolderPo segmentFolder = new FolderPo();
    public static FolderPo segmentFolder1 = new FolderPo();
    public static FolderPo lookAlikeFolder = new FolderPo();
    public static FolderPo campaignFolder = new FolderPo();
    public static TenantPo tenantPo = new TenantPo();
    public static TenantPo infoBaseTenant = new TenantPo();
    public static VersionPo versionPo = new VersionPo();
    public static VersionPo versionPoInfoBase = new VersionPo();
    public static VersionPo draftVersion = new VersionPo();
    public static List<TenantAndChannelPo> tenantAndChannelPoList = new ArrayList<>();
    public static TenantAndChannelPo tenantAndChannelPo = new TenantAndChannelPo();
    public static List<VersionPo> visionPoList = new ArrayList<>();
    public static List<Taxonomy> taxonomyList = new ArrayList<>();
    public static AudienceDistributeJobPo audienceDistributeJobPo = new AudienceDistributeJobPo();
    public static List<AudienceDistributeJobPo> campaignDistributeJobPoList = new ArrayList<>();
    public static List<AudienceDistributeJobPo> segmentDistributeJobPoList = new ArrayList<>();
    public static Taxonomy taxonomy = new Taxonomy();
    public static Taxonomy taxonomy1 = new Taxonomy();
    public static AudienceAndFolderVo segmentVo = new AudienceAndFolderVo();
    public static AudienceAndFolderVo lookLikeVo = new AudienceAndFolderVo();
    public static AudienceAndFolderVo folderVo = new AudienceAndFolderVo();
    public static UniversePo universePo = new UniversePo();
    public static UniversePo SUCCESS_UNIVERSE = new UniversePo();
    public static UniversePo TEST_UNIVERSE = new UniversePo();
    public static final String username = "amsdemo";
    public static final Long tenantId = 1L;
    public static final Long campaignJobId = 10L;
    public static final String createdBy = "amsdemo";
    public static final String userId = "cd59e860-543a-4b2f-8920-9412e590afb2";
    public static final String rule = "{\"cap\":0,\"defaultrule\":[],\"destination\":\"\"," +
            "\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\"," +
            "\"name\":\"vivian0530\",\"rm-duplicates\":false,\"segments\":[{\"count\":1322720,\"exclude\":[]," +
            "\"frozenCount\":1322720,\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"," +
            "\"include\":[{\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"items\":[],\"logic\":\"and\"," +
            "\"name\":\"Contact Channel\",\"objid\":\"5b07b74bc9e77c00062e02fe\",\"origin\":\"tree\"," +
            "\"path\":\"Carrefour Demo / Contact Channel\",\"sourceType\":\"TAXONOMY\",\"style\":\"checkbox\"," +
            "\"values\":[{\"name\":\"Email\",\"node\":\"99011\",\"type\":\"node\"}]}]}],\"test-control\":0}";
    public static final String campaignRule = "{\"campaignId\":4590370230252204000,\"cap\":5,\"defaultrule\":[]," +
            "\"destination\":13,\"id\":\"bf8b44c3-07b8-8d4d-78ba-6643d68168ac\",\"name\":\"Audience7\"," +
            "\"rm-duplicates\":false,\"segments\":[{\"count\":4,\"exclude\":[],\"frozenCount\":4," +
            "\"id\":\"d62d3185-7722-3fa5-59c5-037e9a3bf7a2\",\"include\":[{\"campaignId\":1667,\"dataType\":\"1p\"," +
            "\"id\":\"955b3658-b3b0-5674-3945-abd7a851d1b2\",\"items\":[],\"logic\":\"and\",\"name\":\"SegmentH" +
            "(derived 1p)\",\"objid\":\"\",\"origin\":\"segment\",\"owner\":\"PartnerA\",\"path\":\"AudienceH / " +
            "SegmentH(derived 1p)\",\"values\":[{\"node\":\"cae9861c-7672-08a7-e1cc-6440cc767076\"}]}," +
            "{\"dataType\":\"1p\",\"id\":\"e146998f-ac19-0d37-7f82-bc3461e2fae2\",\"infobaseFlag\":false," +
            "\"items\":[],\"logic\":\"or\",\"name\":\"CustomerSegmentation\",\"objid\":\"5b86452446e0fb000500a727\"," +
            "\"origin\":\"tree\",\"owner\":\"PartnerA\",\"path\":\"Client Data / PartnerA / 1P_2P_PartnerA_557320 / " +
            "CustomerSegmentation\",\"sourceType\":\"TAXONOMY\",\"style\":\"checkbox\"," +
            "\"values\":[{\"name\":\"Tourist\",\"node\":\"23_410_S404\",\"type\":\"node\"}]}],\"name\":\"test1\"}," +
            "{\"cap\":0,\"count\":2,\"exclude\":[],\"frozenCount\":2,\"id\":\"4b2fb0c9-480e-cc61-a016-4bb2d3368370\"," +
            "\"include\":[{\"campaignId\":1667,\"dataType\":\"1p\",\"id\":\"11a9b8ba-1394-b734-1d11-dd1323e3048f\"," +
            "\"items\":[],\"logic\":\"and\",\"name\":\"SegmentH(derived 1p)\",\"objid\":\"\",\"origin\":\"segment\"," +
            "\"owner\":\"PartnerA\",\"path\":\"AudienceH / SegmentH(derived 1p)\"," +
            "\"values\":[{\"node\":\"cae9861c-7672-08a7-e1cc-6440cc767076\"}]},{\"dataType\":\"3p\"," +
            "\"id\":\"0f918698-6028-36ad-ed8b-66ba672ecb14\",\"infobaseFlag\":false,\"items\":[],\"logic\":\"or\"," +
            "\"name\":\"AgeGroup\",\"objid\":\"5b86452446e0fb000500a729\",\"origin\":\"tree\",\"owner\":\"PartnerB\"," +
            "\"path\":\"Data Store / ProviderA / data_store_PartnerA_557321 / AgeGroup\",\"sourceType\":\"TAXONOMY\"," +
            "\"style\":\"checkbox\",\"values\":[{\"name\":\"36-40\",\"node\":\"23_412_S301\",\"type\":\"node\"}]}]," +
            "\"name\":\"segment1\"}],\"test-control\":0}";
    public static final String infoBaseRule = "{\"cap\":950,\"defaultrule\":[],\"destination\":1," +
            "\"id\":\"40ad90fd-6c41-2bb2-2cfd-01cc709a8af8\",\"name\":\"Campaign + cap 950 + dedup\"," +
            "\"rm-duplicates\":true,\"segments\":[{\"count\":0,\"exclude\":[],\"frozenCount\":0," +
            "\"frozenNativeCount\":0,\"id\":\"2fca1fd6-a703-fa48-72eb-d079458341e5\"," +
            "\"include\":[{\"id\":\"43f2a84a-976d-ecc9-b97e-8711bdb9789f\",\"infobaseFlag\":true,\"items\":[]," +
            "\"logic\":\"and\",\"name\":\"Marital Status\",\"objid\":\"5a71349446e0fb0006cebc97\"," +
            "\"origin\":\"tree\",\"path\":\"Carrefour Demo/Demographic/Marital Status\",\"style\":\"checkbox\"," +
            "\"values\":[{\"name\":\"Married\",\"node\":\"990153\",\"type\":\"node\"},{\"name\":\"Other\"," +
            "\"node\":\"990154\",\"type\":\"node\"}]}],\"exclude\":[{\"id\":\"3e3535d0-f9cd-b025-1abc-2ec4dad6bf25\"," +
            "\"infobaseFlag\":true,\"items\":[],\"logic\":\"and\",\"name\":\"Food Entertainment\"," +
            "\"objid\":\"5a71362346e0fb0006cedd11\",\"origin\":\"tree\",\"path\":\"Acxiom InfoBase/Behavioural and " +
            "Lifestyle/Leisure/Food Entertainment\",\"style\":\"checkbox\",\"values\":[{\"name\":\"Yes\"," +
            "\"node\":\"50210000001\",\"type\":\"node\"},{\"name\":\"Unknown\",\"node\":\"50210000002\"," +
            "\"type\":\"node\"}]}],\"name\":\"Marital Status\"}],\"test-control\":0}";

    private Model() {
        initFolderList();
        initTenantPo();
        initVersionPo();
        initTaxonomyList();
        initVersionPoList();
        initCampaign();
        initCampaignDistributeJobPo();
        initSegment();
        initCampaignDistributeJobPoList();
        initSegmentDistributeJobPoList();
        initTenantAndChannelPo();
        initTaxonomy();
        initAudienceAndFolderVo();
        initUniversePo();
    }

    private static class SingletonInstance {
        private static final Model model = new Model();
    }

    public static Model getInstance() {
        return SingletonInstance.model;
    }

    private void initFolderList() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        segmentFolder = new FolderPo(0, "Saved Segments", FolderType.SAVED_SEGMENT, null, null, null);
        segmentFolder.setId(1);
        segmentFolder.setCreatedBy(createdBy);
        segmentFolder.setUpdateTime(calendar.getTime());
        segmentFolder.setFolderName("segment");
        segmentFolder.setAudiencePoList(Lists.newArrayList(segment_new));

        segmentFolder1 = new FolderPo(0, "Saved Segments", FolderType.SAVED_SEGMENT, null, null, null);
        segmentFolder1.setId(1);
        segmentFolder1.setCreatedBy(createdBy);
        segmentFolder1.setUpdateTime(calendar.getTime());
        segmentFolder1.setFolderName("segment1");

        lookAlikeFolder = new FolderPo(0, "Lookalike Group", FolderType.LOOKALIKE_GROUP, null, null, null);
        lookAlikeFolder.setId(2);
        lookAlikeFolder.setCreatedBy(createdBy);
        lookAlikeFolder.setUpdateTime(calendar.getTime());
        lookAlikeFolder.setFolderName("looklike");

        campaignFolder = new FolderPo(0, "Custom Audiences", FolderType.CAMPAIGN, null, null, null);
        campaignFolder.setId(3);
        campaignFolder.setCreatedBy(createdBy);
        campaignFolder.setUpdateTime(calendar.getTime());
        campaignFolder.setFolderName("campaign");
    }

    private void initTaxonomyList() {
        Taxonomy taxonomy = new Taxonomy();
        taxonomy.setName("MY_DATA");
        taxonomyList.add(taxonomy);
    }

    private void initVersionPoList() {
        visionPoList.add(versionPo);
        tenantPo.setVisionPoList(visionPoList);
    }

    private void initTenantPo() {
        tenantPo.setId(1);
        tenantPo.setName("test");
        tenantPo.setPath("test");
        tenantPo.setTenantId("c2f89e4b-7311-4368-8421-dd170d35da18");
        tenantPo.setTenantAndChannelPoList(tenantAndChannelPoList);
        tenantPo.setCountLimit(250);
        tenantPo.setCreatedBy("amsdemo");

        infoBaseTenant.setId(2);
        infoBaseTenant.setName("InfoBase");
        infoBaseTenant.setPath("infobase");
        infoBaseTenant.setTenantId("dff46975-927b-4a98-abe8-55d0b4e638f0");
        infoBaseTenant.setTenantAndChannelPoList(tenantAndChannelPoList);
        infoBaseTenant.setCountLimit(250);
        infoBaseTenant.setCreatedBy("amsdemo");
    }

    private void initVersionPo() {
        versionPo.setDatasourceId("187");
        versionPo.setTenantPo(tenantPo);
        versionPo.setTreeId("5b56ee2c99ebfa33780b6d86");
        versionPo.setName("test");
        versionPo.setOperationFlag(TemplateStatusType.ACTIVE);
        versionPo.setId(1L);

        versionPoInfoBase.setTenantPo(tenantPo);
        versionPoInfoBase.setTreeId("5b2262ef0250ae639c4c9727");
        versionPoInfoBase.setName("InfoBase");
        versionPoInfoBase.setOperationFlag(TemplateStatusType.ACTIVE);
        versionPoInfoBase.setId(2L);
        versionPoInfoBase.setDatasourceId("");

        draftVersion.setDatasourceId("187");
        draftVersion.setTenantPo(tenantPo);
        draftVersion.setTreeId("5b56ee2c99ebfa33780b6d86");
        draftVersion.setName("test");
        draftVersion.setOperationFlag(TemplateStatusType.DRAFT);
        draftVersion.setId(1L);
    }

    private void initCampaignDistributeJobPoList() {
        AudienceDistributeJobPo audienceDistributeJobPo = new AudienceDistributeJobPo();
        audienceDistributeJobPo.setAudienceId(campaign.getId());
        audienceDistributeJobPo.setNoticeEmail("test@liveramp.com");
        audienceDistributeJobPo.setStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
        audienceDistributeJobPo.setTenantId(tenantId);
        audienceDistributeJobPo.setUpdateBy(username);
        audienceDistributeJobPo.setCreatedBy(campaign.getCreatedBy());
        audienceDistributeJobPo.setCreatedTime(campaign.getCreatedTime());
        audienceDistributeJobPo.setUpdateTime(new Date());
        audienceDistributeJobPo.setRules(campaign.getRuleJson());
        audienceDistributeJobPo.setDestinationId(1L);
        audienceDistributeJobPo.setAudienceType(FolderType.CAMPAIGN);
        campaignDistributeJobPoList.add(audienceDistributeJobPo);
    }

    private void initSegmentDistributeJobPoList() {
        AudiencePo audiencePo = new AudiencePo();
        audiencePo.setCount(1000L);
        audiencePo.setRuleJson(rule);
        audiencePo.setName("test");
        audiencePo.setTenantId(1L);
        audiencePo.setId(100L);
        audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_NEW);
        AudienceDistributeJobPo audienceDistributeJobPo = new AudienceDistributeJobPo();
        audienceDistributeJobPo.setAudienceId(audiencePo.getId());
        audienceDistributeJobPo.setNoticeEmail("test@liveramp.com");
        audienceDistributeJobPo.setStatus(SegmentStatusType.SEGMENT_DISTRIBUTING);
        audienceDistributeJobPo.setTenantId(tenantId);
        audienceDistributeJobPo.setUpdateBy(username);
        audienceDistributeJobPo.setCreatedBy(audiencePo.getCreatedBy());
        audienceDistributeJobPo.setCreatedTime(audiencePo.getCreatedTime());
        audienceDistributeJobPo.setUpdateTime(new Date());
        audienceDistributeJobPo.setRules(audiencePo.getRuleJson());
       // audienceDistributeJobPo.setDestinationId(audiencePo.getUniverseIds());
        audienceDistributeJobPo.setAudienceType(FolderType.SAVED_SEGMENT);
        campaignDistributeJobPoList.add(audienceDistributeJobPo);
    }

    private void initCampaignDistributeJobPo() {
        audienceDistributeJobPo.setAudienceId(campaign_new.getId());
        audienceDistributeJobPo.setNoticeEmail("test@liveramp.com");
        audienceDistributeJobPo.setStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
        audienceDistributeJobPo.setTenantId(tenantId);
        audienceDistributeJobPo.setUpdateBy(username);
        audienceDistributeJobPo.setCreatedBy(campaign_new.getCreatedBy());
        audienceDistributeJobPo.setCreatedTime(campaign_new.getCreatedTime());
        audienceDistributeJobPo.setUpdateTime(new Date());
        audienceDistributeJobPo.setDestinationId(1L);
        audienceDistributeJobPo.setAudienceType(FolderType.CAMPAIGN);
        audienceDistributeJobPo.setId(campaignJobId);
        audienceDistributeJobPo.setRules(infoBaseRule);

    }

    private void initSegment() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        segment_new.setAudienceType(FolderType.SAVED_SEGMENT);
        segment_new.setCount(1000L);
        segment_new.setRuleJson(rule);
        segment_new.setName("test_new");
        segment_new.setTenantId(1L);
        segment_new.setId(1L);
        segment_new.setSegmentStatusType(SegmentStatusType.SEGMENT_NEW);
        segment_new.setCreatedBy("amsdemo");
        segment_new.setTaxonomyId("110111");
        calendar.add(Calendar.DATE, -2);
        segment_new.setCreatedTime(calendar.getTime());
        segment_new.setUpdateTime(calendar.getTime());
        segment_new.setFolderPo(segmentFolder);

        segment_distributed.setAudienceType(FolderType.SAVED_SEGMENT);
        segment_distributed.setCount(1000L);
        segment_distributed.setRuleJson(rule);
        segment_distributed.setName("test_distributed");
        segment_distributed.setTenantId(1L);
        segment_distributed.setId(2L);
        segment_distributed.setSegmentStatusType(SegmentStatusType.SEGMENT_DISTRIBUTED);
        segment_distributed.setCreatedBy("amsdemo");
        segment_distributed.setTaxonomyId("110112");
        calendar.add(Calendar.DATE, 1);
        segment_distributed.setCreatedTime(calendar.getTime());
        segment_distributed.setFolderPo(segmentFolder);
        segment_distributed.setUpdateTime(calendar.getTime());

        segment_distributed_failed.setAudienceType(FolderType.SAVED_SEGMENT);
        segment_distributed_failed.setCount(1000L);
        segment_distributed_failed.setRuleJson(rule);
        segment_distributed_failed.setName("test_distributed_failed");
        segment_distributed_failed.setTenantId(1L);
        segment_distributed_failed.setId(3L);
        segment_distributed_failed.setSegmentStatusType(SegmentStatusType.SEGMENT_DISTRIBUTE_FAILED);
        segment_distributed_failed.setCreatedBy("amsdemo");
        segment_distributed_failed.setTaxonomyId("110113");
        segment_distributed_failed.setCreatedTime(new Date());
        segment_distributed_failed.setUpdateTime(new Date());

        segment_distributing.setAudienceType(FolderType.SAVED_SEGMENT);
        segment_distributing.setCount(1000L);
        segment_distributing.setRuleJson(rule);
        segment_distributing.setName("test_distributing");
        segment_distributing.setTenantId(1L);
        segment_distributing.setId(4L);
        segment_distributing.setSegmentStatusType(SegmentStatusType.SEGMENT_DISTRIBUTING);
        segment_distributing.setCreatedBy("amsdemo");
        segment_distributing.setTaxonomyId("110114");
        segment_distributing.setCreatedTime(new Date());
        segment_distributing.setUpdateTime(new Date());

        segment_count_under_limit.setAudienceType(FolderType.SAVED_SEGMENT);
        segment_count_under_limit.setCount(100L);
        segment_count_under_limit.setRuleJson(rule);
        segment_count_under_limit.setName("test_count_under_limit");
        segment_count_under_limit.setTenantId(1L);
        segment_count_under_limit.setId(5L);
        segment_count_under_limit.setSegmentStatusType(SegmentStatusType.SEGMENT_NEW);
        segment_count_under_limit.setCreatedBy("amsdemo");
        segment_count_under_limit.setTaxonomyId("110115");
        segment_count_under_limit.setCreatedTime(new Date());
        segment_count_under_limit.setUpdateTime(new Date());
    }

    private void initCampaign() {
        campaign_new.setAudienceType(FolderType.CAMPAIGN);
        campaign_new.setCount(1000L);
        campaign_new.setRuleJson(rule);
        campaign_new.setName("test_new");
        campaign_new.setTenantId(1L);
        campaign_new.setId(1L);
        campaign_new.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        campaign_new.setCreatedBy("amsdemo");
        campaign_new.setTaxonomyId("110111");
        campaign_new.setCreatedTime(new Date());
        campaign_new.setUniverseIds("1");
        campaign_new.setFolderPo(campaignFolder);
        campaign_new.setUpdateTime(new Date());

        campaign_distributed.setAudienceType(FolderType.CAMPAIGN);
        campaign_distributed.setCount(1000L);
        campaign_distributed.setRuleJson(rule);
        campaign_distributed.setName("test_distributed");
        campaign_distributed.setTenantId(1L);
        campaign_distributed.setId(2L);
        campaign_distributed.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTED);
        campaign_distributed.setCreatedBy("amsdemo");
        campaign_distributed.setTaxonomyId("110112");
        campaign_distributed.setCreatedTime(new Date());
        campaign_distributed.setUniverseIds("1");
        campaign_distributed.setFolderPo(campaignFolder);
        campaign_distributed.setUpdateTime(new Date());

        campaign_distributed_failed.setAudienceType(FolderType.CAMPAIGN);
        campaign_distributed_failed.setCount(1000L);
        campaign_distributed_failed.setRuleJson(rule);
        campaign_distributed_failed.setName("test_distributed_failed");
        campaign_distributed_failed.setTenantId(1L);
        campaign_distributed_failed.setId(3L);
        campaign_distributed_failed.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED);
        campaign_distributed_failed.setCreatedBy("amsdemo");
        campaign_distributed_failed.setTaxonomyId("110113");
        campaign_distributed_failed.setUniverseIds("1");
        campaign_distributed_failed.setCreatedTime(new Date());
        campaign_distributed_failed.setUpdateTime(new Date());

        campaign.setAudienceType(FolderType.CAMPAIGN);
        campaign.setCount(1000L);
        campaign.setRuleJson(campaignRule);
        campaign.setName("test_distributing");
        campaign.setTenantId(1L);
        campaign.setId(4L);
        campaign.setUniverseIds("1");
        campaign.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        campaign.setCreatedBy("amsdemo");
        campaign.setTaxonomyId("110114");
        campaign.setCreatedTime(new Date());
        campaign.setUpdateTime(new Date());

        campaign_count_under_limit.setAudienceType(FolderType.CAMPAIGN);
        campaign_count_under_limit.setCount(100L);
        campaign_count_under_limit.setRuleJson(rule);
        campaign_count_under_limit.setName("test_count_under_limit");
        campaign_count_under_limit.setTenantId(1L);
        campaign_count_under_limit.setId(5L);
        campaign_count_under_limit.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        campaign_count_under_limit.setCreatedBy("amsdemo");
        campaign_count_under_limit.setTaxonomyId("110115");
        campaign_count_under_limit.setCreatedTime(new Date());
        campaign_count_under_limit.setUniverseIds("1");
        campaign_count_under_limit.setUpdateTime(new Date());
    }

    private void initTenantAndChannelPo() {
        tenantAndChannelPo.setTenantPo(tenantPo);
    }

    private void initTaxonomy() {
        Taxonomy childTaxonomy = new Taxonomy("5b30571ae6f63729dc8627ed", "5b30571ae6f63729dc8627ea", "00217799-2",
                "Likely Eye Shadow Type", "end", "", "", 30.0, "amsdemo",
                "ALL", "3p", "1_197_300217799", null);

        Taxonomy childTaxonomy1 = new Taxonomy("5b30571ae6f63729dc8627ee", "5b30571ae6f63729dc8627ef", "10217799-2",
                "ACXM Health and Fitness", "end", "", "", 30.0, "amsdemo",
                "ALL", "3p", "1_197_300217790", null);
        taxonomy.setName("Test Beauty Test");
        taxonomy.setChecked("ALL");
        taxonomy.setDataType("3p");
        taxonomy.setTaxonomyId("99011");
        taxonomy.setObjectId("5b30571ae6f63729dc8627ea");
        taxonomy.setObjectPid("5b30571ae6f63729dc8627e9");
        taxonomy.setOwner("mvpda");
        taxonomy.setType("attribute");
        taxonomy.setId("300217799-1");
        taxonomy.setPrice((double) 30);
        taxonomy.setTaxonomyIncludes(Lists.newArrayList(childTaxonomy));

        taxonomy1.setName("Test CPG Test");
        taxonomy1.setChecked("ALL");
        taxonomy1.setDataType("3p");
        taxonomy1.setTaxonomyId("99012");
        taxonomy1.setObjectId("5b30571ae6f63729dc8627ef");
        taxonomy1.setObjectPid("5b30571ae6f63729dc8627e9");
        taxonomy1.setOwner("mvpda");
        taxonomy1.setId("300217790-1");
        taxonomy1.setType("attribute");
        taxonomy1.setPrice((double) 40);
        taxonomy1.setTaxonomyIncludes(Lists.newArrayList(childTaxonomy1));
    }

    public TaxonomyItemVo parseTaxonomy(Taxonomy taxonomy) {
        TaxonomyItemVo taxonomyItemVo = new TaxonomyItemVo();
        taxonomyItemVo.setOwner(taxonomy.getOwner());
        taxonomyItemVo.setTaxonomyId(taxonomy.getTaxonomyId());
        taxonomyItemVo.setName(taxonomy.getName());
        taxonomyItemVo.setDescription(taxonomy.getDescription());
        taxonomyItemVo.setStyle(taxonomy.getStyle());
        taxonomyItemVo.setType(taxonomy.getType());
        taxonomyItemVo.setObjectId(taxonomy.getObjectId());
        taxonomyItemVo.setPrice(taxonomy.getPrice());
        return taxonomyItemVo;
    }

    public void initAudienceAndFolderVo() {
        segmentVo = new AudienceAndFolderVo(1L, "segmnet", "", 366L, null, FolderType.SAVED_SEGMENT, "",
                "", new Date(), "", "", null,"", "", FolderType.SAVED_SEGMENT, null, 366L, false, true, null, null, null, null, null, null, null,new Date());
        lookLikeVo = new AudienceAndFolderVo(1L, "looklike", "", 366L, SegmentStatusType.LOOKALIKE_DONE, FolderType
                .LOOKALIKE_GROUP, "",
                "", new Date(), "", "", null,"", "", null, null, 366L, false,true, null, null, null, null, null, null, null, new Date());
        folderVo = new AudienceAndFolderVo(1L, "folder", "", 366L, SegmentStatusType.SEGMENT_NEW, FolderType
                .SAVED_SEGMENT, "",
                "", new Date(), "", "", null,"", "", FolderType.SAVED_SEGMENT, null, 366L,false,true, null, null, null, null, null, null, null, new Date());
    }

    public void initUniversePo(){
        universePo.setId(1L);
        universePo.setUniverseStatus(SegmentStatusType.UNIVERSE_PROCESSING);
        universePo.setUniverseName("Test123");
        universePo.setUniverseRuleJson("");
        universePo.setUniverseSystemName("test123");
        universePo.setUniverseCount(1000L);
        universePo.setTenantPath("mvpda");
        universePo.setUniverseThreshold(0.36F);
        universePo.setTenantId(1L);
        universePo.setUniverseType(UniverseType.DEFAULT);
        universePo.setOwnerTenantPath("mvpda");


        SUCCESS_UNIVERSE.setId(2L);
        SUCCESS_UNIVERSE.setUniverseStatus(SegmentStatusType.UNIVERSE_SUCCESS);
        SUCCESS_UNIVERSE.setUniverseName("Test123456");
        SUCCESS_UNIVERSE.setUniverseRuleJson("");
        SUCCESS_UNIVERSE.setUniverseSystemName("test123456");
        SUCCESS_UNIVERSE.setUniverseCount(1500L);
        SUCCESS_UNIVERSE.setTenantPath("mvpda");
        SUCCESS_UNIVERSE.setTenantId(1L);
        SUCCESS_UNIVERSE.setUniverseType(UniverseType.DEFAULT);
        SUCCESS_UNIVERSE.setUniverseThreshold(0.33f);
        SUCCESS_UNIVERSE.setOwnerTenantPath("mvpda");

        TEST_UNIVERSE.setId(3L);
        TEST_UNIVERSE.setUniverseStatus(SegmentStatusType.UNIVERSE_SUCCESS);
        TEST_UNIVERSE.setUniverseName("Test");
        TEST_UNIVERSE.setUniverseRuleJson("");
        TEST_UNIVERSE.setUniverseSystemName("test");
        TEST_UNIVERSE.setUniverseCount(1500L);
        TEST_UNIVERSE.setTenantPath("test");
        TEST_UNIVERSE.setTenantId(1L);
        TEST_UNIVERSE.setOwnerTenantPath("test");
        TEST_UNIVERSE.setUniverseType(UniverseType.DEFAULT);
    }
}
