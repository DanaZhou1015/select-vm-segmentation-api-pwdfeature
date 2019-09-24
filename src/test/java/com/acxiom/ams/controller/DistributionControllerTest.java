package com.acxiom.ams.controller;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.compomemt.RetryRestTemplate;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.dto.DistributeParam;
import com.acxiom.ams.model.dto.DistributeReturnParam;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.AudienceDistributeJobPo;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.po.TenantAndChannelPo;
import com.acxiom.ams.repository.AudienceDistributeJobPoJPA;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.repository.TenantAndChannelPoJPA;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.service.FolderService;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.impl.DistributionServiceImpl;
import com.acxiom.ams.util.Constant;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 4:06 PM 10/19/2018
 */
public class DistributionControllerTest {
    @InjectMocks
    DistributionServiceImpl distributionService;
    @Mock
    TenantAndChannelPoJPA tenantAndChannelPoJPA;
    @Mock
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Mock
    AudiencePoJPA audiencePoJPA;
    @Mock
    TenantPoJPA tenantPoJPA;
    @Mock
    FolderService folderService;
    @InjectMocks
    @Spy
    ServiceAPI.BitmapAPI bitmapAPI = new ServiceAPI.BitmapAPI();
    @Mock
    RetryRestTemplate retryRestTemplate;
    @Mock
    AudienceDistributeJobPoJPA audienceDistributeJobPoJPA;
    @Mock
    TenantService tenantService;


    Model model = Model.getInstance();
    TenantAndChannelPo tenantAndChannelPo = new TenantAndChannelPo();
    List<Long> ids = new ArrayList<>();
    List<Long> jobIds = new ArrayList<>();
    List<AudiencePo> segmentList = Lists.newArrayList(model.segment_new);
    @Before
    public void init() throws AMSInvalidInputException {
        MockitoAnnotations.initMocks(this);
        ids.add(1L);
        jobIds.add(1L);
        Mockito.when(audiencePoJPA.findAudiencePoByIdIn(ids)).thenReturn(segmentList);
        Mockito.when(audienceDistributeJobPoJPA.findAll(jobIds)).thenReturn(model.segmentDistributeJobPoList);
        Mockito.when(tenantService.getTenantById(model.tenantId)).thenReturn(model.tenantPo);
        Mockito.when(errorMessageSourceHandler.getMessage("")).thenReturn("");
    }

    @Test
    public void distributeSegments() throws AMSException {
        Mockito.when(audiencePoJPA.getSegmentListByFolderId("")).thenReturn(Collections.emptyList());
        Mockito.when(audiencePoJPA.findAll(ids)).thenReturn(Lists.newArrayList(model.segment_count_under_limit));
        DistributeParam distributeParam = new DistributeParam(1L, "test@liveramp.com", Lists.newArrayList(1L), null, ids, "amsdemo");
        Mockito.when(tenantAndChannelPoJPA.findByIdIn(distributeParam.getChannelIdList())).thenReturn(Lists.newArrayList(model.tenantAndChannelPo));
        // case 1: count under tenant count limit
        try {
            distributionService.distributeSegments(distributeParam);
            Assert.fail("Failed to distribute segment in case 1");
        } catch (AMSInvalidInputException e) {
            Assert.assertTrue("Failed to distribute segment", StringUtils.equals(e.getCode(), "020216"));
        }
        // case 2 : no exception
        String reqParams = "{\"clientID\":\"test\",\"segmentNames\":\"test_new\",\"tenantName\":\"test\"," +
                "\"okContent\":\"%3C%3Fxml+version%3D%221" +
                ".0%22+encoding%3D%22UTF-8%22%3F%3E%0A%3CCustom%3E%3CColumn+display%3D%22test_new%22+name%3D" +
                "%22SEGMENT1%22+segmentCode%3D%22%22+value%3D%221%22+description%3D%22%22%2F%3E%3C%2FCustom%3E\"," +
                "\"channels\":[{\"id\":0}],\"tenantId\":\"1\",\"audiences\":[{\"rules\":{\"jobId\":0,\"cap\":0," +
                "\"rm-duplicates\":false,\"test-control\":0,\"destination\":\"\",\"name\":\"vivian0530\"," +
                "\"id\":\"9c603167-30f7-5f3f-458e-f72d00e1fcfe\",\"defaultrule\":[]," +
                "\"segments\":[{\"include\":[{\"path\":\"Carrefour Demo / Contact Channel\"," +
                "\"sourceType\":\"TAXONOMY\",\"origin\":\"tree\",\"values\":[{\"node\":\"99011\",\"name\":\"Email\"," +
                "\"type\":\"node\"}],\"name\":\"Contact Channel\",\"objid\":\"5b07b74bc9e77c00062e02fe\"," +
                "\"style\":\"checkbox\",\"id\":\"7b8d706e-6124-7df0-5e0d-c1f025962b30\",\"logic\":\"and\"," +
                "\"items\":[]}],\"frozenCount\":1322720,\"count\":1322720,\"exclude\":[]," +
                "\"id\":\"6458e7c6-f834-0af8-4119-fc0ff6967fc1\"}]},\"segmentId\":1,\"segmentJobId\":0," +
                "\"segmentName\":\"test_new\",\"taxonomyID\":\"110111\"}],\"email\":\"test@liveramp.com\"," +
                "\"username\":\"amsdemo\"}";
        try {
            AudiencePo audiencePo = new AudiencePo();
            audiencePo.setAudienceType(FolderType.SAVED_SEGMENT);
            audiencePo.setCount(1000L);
            audiencePo.setName("test_new");
            audiencePo.setTenantId(1L);
            audiencePo.setId(1L);
            audiencePo.setRuleJson(model.rule);
            audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_NEW);
            audiencePo.setCreatedBy("amsdemo");
            audiencePo.setTaxonomyId("110111");
            audiencePo.setCreatedTime(new Date());
            audiencePo.setFolderPo(model.segmentFolder);
            List<AudiencePo> audiencePoList = Lists.newArrayList(audiencePo);
            Mockito.when(audiencePoJPA.findAll(ids)).thenReturn(audiencePoList);
            Mockito.when(audiencePoJPA.save(audiencePoList)).thenReturn(audiencePoList);
            Mockito.when(retryRestTemplate.post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_DISTRIBUTE_V7, reqParams,
                    String.class)).thenReturn("{\"success\":true,\"data\":{\"fileName\":\"test.txt\"}}");
            String resp = distributionService.distributeSegments(distributeParam);
            audiencePoList.forEach(audiencePo1 -> {
                if (!SegmentStatusType.SEGMENT_DISTRIBUTING.equals(audiencePo1.getSegmentStatusType())) {
                    Assert.fail("Failed to distribute segment");
                }
            });
            verify(retryRestTemplate).post(Constant.BITMAP_SERVER_SERVICE, Constant.POST_DISTRIBUTE_V7, reqParams,
                    String.class);
            Assert.assertTrue("Failed to distribute segment", StringUtils.equals(resp, "test.txt"));
        } catch (Exception e) {
            Assert.fail("Failed to distribute segment in case 2");
        }
    }

    @Test
    public void callbackSegmentStatus() throws AMSInvalidInputException {
        Map<String, Object> callback = new HashMap<>();
        List<Long> list = new ArrayList<>();
        list.add(1L);
        callback.put("audienceJobIds", list);
        callback.put("audienceIds", list);
        DistributeReturnParam distributeSuccess = new DistributeReturnParam("success", "distribute success", list);
        distributionService.callbackSegmentStatus(distributeSuccess);
        ArgumentCaptor<List> personCaptor1 = ArgumentCaptor.forClass(List.class);
        verify(audiencePoJPA, times(1)).save(personCaptor1.capture());
        List<AudiencePo> audiencePoList = personCaptor1.getValue();
        ArgumentCaptor<List> personCaptor2 = ArgumentCaptor.forClass(List.class);
        verify(audienceDistributeJobPoJPA).save(personCaptor2.capture());
        List<AudienceDistributeJobPo> audienceDistributeJobPoList = personCaptor2.getValue();
        for (AudiencePo audiencePo : audiencePoList) {
            Assert.assertTrue("Failed to callback distribute status", SegmentStatusType.SEGMENT_DISTRIBUTED.equals
                    (audiencePo.getSegmentStatusType()));
        }
        for (AudienceDistributeJobPo audienceDistributeJobPo : audienceDistributeJobPoList) {
            Assert.assertTrue("Failed to callback distribute status", SegmentStatusType.SEGMENT_DISTRIBUTED.equals
                    (audienceDistributeJobPo.getStatus()));
        }
        // failed
        DistributeReturnParam distributeFailed = new DistributeReturnParam("failed", "distribute failed", list);
        distributionService.callbackSegmentStatus(distributeFailed);
        ArgumentCaptor<List> personCaptor3 = ArgumentCaptor.forClass(List.class);
        verify(audiencePoJPA, times(2)).save(personCaptor3.capture());
        List<AudiencePo> audiencePoList1 = personCaptor3.getValue();
        ArgumentCaptor<List> personCaptor4 = ArgumentCaptor.forClass(List.class);
        verify(audienceDistributeJobPoJPA, times(2)).save(personCaptor4.capture());
        List<AudienceDistributeJobPo> audienceDistributeJobPoList1 = personCaptor4.getValue();
        for (AudiencePo audiencePo : audiencePoList1) {
            Assert.assertTrue("Failed to callback distribute status", SegmentStatusType.SEGMENT_DISTRIBUTE_FAILED.equals
                    (audiencePo.getSegmentStatusType()));
        }
        for (AudienceDistributeJobPo audienceDistributeJobPo : audienceDistributeJobPoList1) {
            Assert.assertTrue("Failed to callback distribute status", SegmentStatusType.SEGMENT_DISTRIBUTE_FAILED.equals
                    (audienceDistributeJobPo.getStatus()));
        }
    }
}