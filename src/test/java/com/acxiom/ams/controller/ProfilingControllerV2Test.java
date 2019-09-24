package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.mapper.ProfilingPoMapper;
import com.acxiom.ams.model.dto.ProfilingDTO;
import com.acxiom.ams.model.po.ProfilingPo;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.vo.ProfilingVo;
import com.acxiom.ams.repository.ProfilingJPA;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.impl.ProfilingServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:30 AM 9/12/2018
 */

public class ProfilingControllerV2Test {

    // @Autowired
    @InjectMocks
    ProfilingServiceImpl profilingService;

    @Mock
    ProfilingJPA profilingJPA;

    @Mock
    ProfilingPoMapper profilingPoMapper;

    @Mock
    TenantPoJPA tenantPoJPA;
    @Mock
    TenantService tenantService;

    ProfilingPo profilingPo;

    Long destinationId;
    Long tenantId;
    Long profilingId;
    String profilingName;

    @Before
    public void init() throws AMSInvalidInputException {
        MockitoAnnotations.initMocks(this);
        profilingName = "mock_test";
        profilingId = 1L;
        destinationId = 2L;
        tenantId = 1L;
        profilingPo = new ProfilingPo();
        profilingPo.setId(profilingId);
        profilingPo.setName(profilingName);
        profilingPo.setDescription("mock_test");
        profilingPo.setJson("{\"chart\":[{\"id\":\"4e2fcdc9" +
                "-1e5a-00e0-bb68-626c3daa7e4b\",\"type\":\"pie\",\"title\":\"111111\",\"subtitle\":\"\"," +
                "\"items\":[{\"id\":\"990110\",\"name\":\"[60,80]\",\"value\":0},{\"id\":\"990111\"," +
                "\"name\":\"Other\",\"value\":0},{\"id\":\"99012\",\"name\":\"[20,24]\",\"value\":0}]}]}");
        profilingPo.setTenantPo(new TenantPo());
        profilingPo.setCreatedBy("fermi");
        profilingPo.setCreatedTime(new Date());
        profilingPo.setUpdateTime(new Date());
        Mockito.when(profilingJPA.findByDestinationIdAndActive(destinationId, true)).thenReturn(profilingPo);
        Mockito.when(profilingJPA.findByDestinationId(destinationId)).thenReturn(Lists.newArrayList(profilingPo));
        Mockito.when(profilingJPA.findPoByDestinationIdAndId(destinationId, profilingId)).thenReturn(profilingPo);
        Mockito.when(profilingJPA.updateActiveByDestinationId(false, destinationId)).thenReturn(1);
        Mockito.when(profilingJPA.save(profilingPo)).thenReturn(profilingPo);
        TenantPo tenantPo = new TenantPo();
        tenantPo.setName("test");
        Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(tenantPo);
    }

    @Test
    public void getInsightByDestinationId() {
        try {
            ProfilingVo profilingVo = new ProfilingVo();
            profilingVo.setProfilingId(profilingId);
            Mockito.when(profilingPoMapper.map(profilingPo)).thenReturn(profilingVo);
            profilingVo = profilingService.getInsightByDestinationId(destinationId);
            verify(profilingJPA).findByDestinationIdAndActive(destinationId, true);
            Assert.assertTrue("Failed to get active profiling by destination id", profilingVo.getProfilingId() ==
                    profilingPo.getId());
        } catch (MockitoAssertionError error) {
            Assert.fail("Failed to get active profiling by destination id");
        }
    }

    @Test
    public void listInsightsByDestinationId() {
        try {
            ProfilingVo profilingVo = new ProfilingVo();
            Mockito.when(profilingPoMapper.map(Lists.newArrayList(profilingPo))).thenReturn(Lists.newArrayList
                    (profilingVo));
            List<ProfilingVo> profilingVoList = profilingService.listInsightsByDestinationId(destinationId);
            verify(profilingJPA).findByDestinationId(destinationId);
            Assert.assertTrue("Failed to get insights list by destination id", profilingVoList.size() > 0);
        } catch (MockitoAssertionError error) {
            Assert.fail("Failed to get insights list by destination id");
        }
    }

    @Test
    public void setActiveInsight() throws AMSInvalidInputException {
        try {
            profilingService.setActiveInsight(destinationId, profilingId);
            verify(profilingJPA, times(1)).findPoByDestinationIdAndId(destinationId, profilingId);
            verify(profilingJPA, times(1)).updateActiveByDestinationId(false, destinationId);
            ArgumentCaptor<ProfilingPo> personCaptor = ArgumentCaptor.forClass(ProfilingPo.class);
            verify(profilingJPA, times(1)).save(personCaptor.capture());
            ProfilingPo profilingPo = personCaptor.getValue();
            Assert.assertTrue("Failed to update insight active status", profilingId == profilingPo.getId());
        } catch (MockitoAssertionError error) {
            Assert.fail("Failed to update insight active status");
        }
    }

    @Test
    public void saveInsight() throws AMSInvalidInputException {
        try {
            // insert
            ProfilingPo profilingPo;
            ProfilingDTO profilingDTOForAdd = new ProfilingDTO(-1, "", "junit_test_insert", "", "fermi");
            Mockito.when(profilingJPA.findPoByDestinationIdAndName(destinationId, profilingDTOForAdd.getProfilingName
                    ())).thenReturn(null);
            profilingService.saveInsight(tenantId, destinationId, profilingDTOForAdd);
            verify(profilingJPA).findPoByDestinationIdAndName(destinationId, profilingDTOForAdd.getProfilingName());
            ArgumentCaptor<ProfilingPo> personCaptor = ArgumentCaptor.forClass(ProfilingPo.class);
            verify(profilingJPA).save(personCaptor.capture());
            profilingPo = personCaptor.getValue();
            Assert.assertTrue("Failed to insert insight", StringUtils.equals(profilingDTOForAdd.getProfilingName(),
                    profilingPo.getName()));
            // update
            ProfilingDTO profilingForUpdate = new ProfilingDTO(2, "", "junit_test_update", "", "fermi");
            Mockito.when(profilingJPA.findOne(profilingForUpdate.getId())).thenReturn(profilingPo);
            long updateInsightId = profilingService.saveInsight(tenantId,
                    destinationId, profilingForUpdate);
            verify(profilingJPA).findOne(profilingForUpdate.getId());
            verify(profilingJPA, times(2)).save(personCaptor.capture());
            profilingPo = personCaptor.getValue();
            Assert.assertTrue("Failed to update insight", updateInsightId == profilingPo.getId());
        } catch (MockitoAssertionError error) {
            Assert.fail("Failed to insert or update insight");
        }
    }
}