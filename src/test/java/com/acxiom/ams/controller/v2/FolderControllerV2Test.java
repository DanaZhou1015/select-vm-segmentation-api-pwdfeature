package com.acxiom.ams.controller.v2;

import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.dto.FolderAndCampaign;
import com.acxiom.ams.model.dto.PermissionDTO;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.vo.AllowFlagVo;
import com.acxiom.ams.model.vo.PermissionVo;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.repository.FolderPoJPA;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.repository.VersionPoJPA;
import com.acxiom.ams.service.impl.BitmapServiceImpl;
import com.acxiom.ams.service.impl.FolderServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 5:33 PM 10/23/2018
 */
public class FolderControllerV2Test {
    @InjectMocks
    FolderServiceImpl folderService;
    @Mock
    BitmapServiceImpl bitmapService = new BitmapServiceImpl();
    @Mock
    TenantPoJPA tenantPoJPA;
    @Mock
    VersionPoJPA versionPoJPA;
    @Mock
    FolderPoJPA folderJPA;
    @Mock
    AudiencePoJPA audiencePoJPA;
    @Mock
    ErrorMessageSourceHandler errorMessageSourceHandler;
    Model model = Model.getInstance();
    List<String> folderIdList = new ArrayList<>();
    List<Long> folderIdList1 = new ArrayList<>();
    List<Long> audienceIdList = new ArrayList<>();


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        folderIdList.add("1");
        audienceIdList.add(1L);
    }

    @Test
    public void getFoldersByTenantIdV2() {
        // case 1: folder id is 3 and folder is not exist
        try {
            folderService.getFoldersByTenantIdV2(3, 1, FolderType.CAMPAIGN);
            Assert.fail("Failed to get campaign folder by tenant id in case 1");
        } catch (AMSInvalidInputException e) {
            Assert.assertTrue("Failed to get campaign folder by tenant id in case 1", StringUtils.equals(e.getCode(),
                    "020205"));
        }

        // case 2: folder id is 3
        try {
            Mockito.when(folderJPA.getFolderPoByIdAndFolderType(3L, FolderType.CAMPAIGN)).thenReturn(model
                    .campaignFolder);
            folderService.getFoldersByTenantIdV2(3, 1, FolderType.CAMPAIGN);
            verify(folderJPA).getFolderPoByParentFolderIdAndTenantIdAndFolderType(
                    3L, 1L, FolderType.CAMPAIGN);
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to get campaign folder by tenant id in case 2");
        }

        // case 3: folder id is 4 and folder is not exist
        try {
            Mockito.when(folderJPA.getFolderPoByParentFolderIdAndTenantIdAndFolderType(4L, 1L,
                    FolderType.CAMPAIGN)).thenReturn(null);
            folderService.getFoldersByTenantIdV2(4, 1, FolderType.CAMPAIGN);
            Assert.fail("Failed to get campaign folder by tenant id in case 3");
        } catch (AMSInvalidInputException e) {
            Assert.assertTrue("Failed to get campaign folder by tenant id in case 3", StringUtils.equals(e.getCode(),
                    "020205"));
        }

        // case 4: folder id is 4
        try {
            Mockito.when(folderJPA.getFolderPoByIdAndFolderType(4L, FolderType.CAMPAIGN)).thenReturn(model
                    .campaignFolder);
            Mockito.when(folderJPA.getFolderPoByParentFolderIdAndTenantIdAndFolderType(4L, 1L,
                    FolderType.CAMPAIGN)).thenReturn(Lists.newArrayList(model.campaignFolder));
            folderService.getFoldersByTenantIdV2(4, 1, FolderType.CAMPAIGN);
            verify(folderJPA, times(2)).getFolderPoByParentFolderIdAndTenantIdAndFolderType(
                    4L, 1L, FolderType.CAMPAIGN);
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to get campaign folder by tenant id in case 4");
        }
    }

    @Test
    public void getParentFolder() {
        // case 1:parent folder is not exist
        try {
            Mockito.when(folderJPA
                    .getFolderPoByParentFolderIdAndFolderType((long) 0, FolderType.CAMPAIGN)).thenReturn(null);
            folderService.getParentFolderV2();
            Assert.fail("Failed to get campaign parent folder in case 1");
        } catch (AMSInvalidInputException e) {
            Assert.assertTrue("", StringUtils.equals(e.getCode(), "020224"));
        }
        // case 2:non exception
        try {
            Mockito.when(folderJPA
                    .getFolderPoByParentFolderIdAndFolderType((long) 0, FolderType.CAMPAIGN)).thenReturn(Lists
                    .newArrayList(model.campaignFolder));
            folderService.getParentFolderV2();
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to get campaign parent folder in case 2");
        }
    }

    @Test
    public void isAllowDistribution() {
        FolderAndCampaign folderAndCampaign = new FolderAndCampaign(folderIdList1, audienceIdList, "test");
        // case 1: allow distribute
        Mockito.when(audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(folderAndCampaign.getFolderIdList(), ","))).thenReturn(new
                ArrayList<>());
        Mockito.when(audiencePoJPA.findAll(folderAndCampaign.getCampaignIdList())).thenReturn(Lists.newArrayList(model
                .campaign_new));
        Boolean resp = folderService.isAllowDistributeV2(folderAndCampaign);
        Assert.assertTrue("Failed to get is allow distribute in case 1", resp);
        // case 2: not allow distribute
        Mockito.when(audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(folderAndCampaign.getFolderIdList(), ","))).thenReturn(new
                ArrayList<>());
        Mockito.when(audiencePoJPA.findAll(folderAndCampaign.getCampaignIdList())).thenReturn(Lists.newArrayList
                (model.campaign_distributed, model
                        .campaign_new));
        resp = folderService.isAllowDistributeV2(folderAndCampaign);
        Assert.assertTrue("Failed to get is allow distribute in case 2", !resp);
    }

    @Test
    public void isAllowDistributeAndDelete() {
        List<Long> folderIdList = Lists.newArrayList(1L, 2L);
        List<Long> campaignIdList = Lists.newArrayList(1L, 2L);
        FolderAndCampaign folderAndCampaign = new FolderAndCampaign(folderIdList, campaignIdList, "amsdemo");
        // case 1: Not allowed distribution and deletion
        Mockito.when(audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(folderAndCampaign.getFolderIdList(), ","))).thenReturn
                (Lists.newArrayList(model.campaign_new));
        Mockito.when(audiencePoJPA.findAll(folderAndCampaign.getCampaignIdList())).thenReturn(Lists.newArrayList
                (model.campaign_distributed));
        AllowFlagVo allowFlagVo = folderService.isAllowDistributeAndDelete(folderAndCampaign);
        Assert.assertTrue("Failed to get is allow distribute and delete in case 1", !allowFlagVo.getDeleteFlag() &&
                !allowFlagVo.getDistributeFlag());

        // case 2: Allowed distribution and deletion
        Mockito.when(audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(folderAndCampaign.getFolderIdList(), ","))).thenReturn
                (Lists.newArrayList(model.campaign_new));
        Mockito.when(audiencePoJPA.findAll(folderAndCampaign.getCampaignIdList())).thenReturn(Collections.emptyList());
        AllowFlagVo allowFlagVo1 = folderService.isAllowDistributeAndDelete(folderAndCampaign);
        Assert.assertTrue("Failed to get is allow distribute and delete in case 2", allowFlagVo1.getDeleteFlag() &&
                allowFlagVo1.getDistributeFlag());
    }

    @Test
    public void checkPermission() {
        AudiencePo campaign = new AudiencePo();
        campaign.setAudienceType(FolderType.CAMPAIGN);
        campaign.setCount(1000L);
        campaign.setRuleJson(model.campaignRule);
        campaign.setName("test_distributing");
        campaign.setTenantId(1L);
        campaign.setId(4L);
        campaign.setUniverseIds("1");
        campaign.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        campaign.setCreatedBy("amsdemo");
        campaign.setTaxonomyId("110114");
        campaign.setCreatedTime(new Date());
        campaign.setUpdateTime(new Date());
        campaign.setFrozenCount(1L);
        String username = "test";
        PermissionDTO permissionDTO = new PermissionDTO(username, folderIdList, audienceIdList);
        // case 1: audience size is 1
        Mockito.when(audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(permissionDTO.getFolderIdList(), ","))).thenReturn(new
                ArrayList<>());
        Mockito.when(audiencePoJPA.findAll(permissionDTO.getAudienceIdList())).thenReturn(Lists.newArrayList(campaign));
        PermissionVo permissionVo = folderService.checkPermissionV2(permissionDTO);
        Assert.assertTrue("Failed to get permission in case 1", permissionVo.getCopy() && !permissionVo.getEdit()
                && permissionVo.getDistribute() && !permissionVo.getDelete() && !permissionVo.getRefresh());
        // case 2: audience size more than 1
        Mockito.when(audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(permissionDTO.getFolderIdList(), ","))).thenReturn(new
                ArrayList<>());
        Mockito.when(audiencePoJPA.findAll(permissionDTO.getAudienceIdList())).thenReturn(Lists.newArrayList(campaign, model.campaign_distributed));
        permissionVo = folderService.checkPermissionV2(permissionDTO);
        Assert.assertTrue("Failed to get permission in case 2", !permissionVo.getCopy() && !permissionVo.getEdit()
                && !permissionVo.getDistribute() && !permissionVo.getDelete() && !permissionVo.getRefresh());
    }
}