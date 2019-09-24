package com.acxiom.ams.controller.v2;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.mapper.VersionPoMapper;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.dto.v2.VersionDTOCreate;
import com.acxiom.ams.model.dto.v2.VersionDTOUpdateTaxonomy;
import com.acxiom.ams.model.dto.v2.VersionDtoDelete;
import com.acxiom.ams.model.em.TemplateStatusType;
import com.acxiom.ams.model.po.VersionPo;
import com.acxiom.ams.model.vo.NodeCountAndDepthVO;
import com.acxiom.ams.repository.VersionPoJPA;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.impl.VersionPoServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 10:02 AM 11/6/2018
 */
public class VersionControllerV2Test {
    @InjectMocks
    VersionPoServiceImpl versionPoService;
    @Mock
    VersionPoJPA versionPoJPA;
    @Mock
    ServiceAPI.TaxonomyAPI taxonomyAPI;
    @Mock
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Mock
    VersionPoMapper versionPoMapper;
    @Mock
    TenantService tenantService;
    Long tenantId;
    Long versionId;
    String username;
    Model model = Model.getInstance();

    @Before
    public void init() throws AMSInvalidInputException {
        MockitoAnnotations.initMocks(this);
        tenantId = 1L;
        versionId = 1L;
        username = "test";
        Mockito.when(tenantService.getTenantById(tenantId)).thenReturn(model.tenantPo);
    }

    @Test
    public void findVersionByTenantId() {
        try {
            Mockito.when(versionPoJPA
                    .findAllByTenantPoOrderByUpdateTimeDesc(model.tenantPo)).thenReturn(Lists.newArrayList(model
                    .versionPo, model.versionPoInfoBase));
            versionPoService.findByTenant(tenantId);
            verify(versionPoMapper).map(Lists.newArrayList(model.versionPo, model.versionPoInfoBase));
        } catch (AMSException e) {
            Assert.fail("Failed to get version list by tenant id");
        }
    }

    @Test
    public void findVersionByTenantIdAndVersionId() {
        try {
            Mockito.when(versionPoJPA.findById(versionId)).thenReturn(model.versionPo);
            versionPoService.findByTenantAndId(tenantId, versionId);
            verify(versionPoMapper).map(model.versionPo);
        } catch (AMSException e) {
            Assert.fail("Failed to get version by tenant id and version id");
        }

    }

    @Test
    public void createVersion() {
        VersionDTOCreate versionDTOCreate = new VersionDTOCreate("test1106", "amsdemo");
        // case 1: version does not exist, return new version
        try {
            Mockito.when(versionPoJPA
                    .findByTenantPoAndName(model.tenantPo, versionDTOCreate.getName())).thenReturn(Collections
                    .EMPTY_LIST);
            versionPoService.createVersion(versionDTOCreate, tenantId);
            ArgumentCaptor<VersionPo> personCaptor = ArgumentCaptor.forClass(VersionPo.class);
            verify(versionPoJPA).save(personCaptor.capture());
            VersionPo request = personCaptor.getValue();
            Assert.assertTrue("Failed to update version in case 4", StringUtils.equals(request.getName(),
                    versionDTOCreate.getName()));
        } catch (AMSException e) {
            Assert.fail("Failed to create version in case 1");
        }
        // case 2: version does exist, tree id is not empty
        try {
            Mockito.when(versionPoJPA
                    .findByTenantPoAndName(model.tenantPo, versionDTOCreate.getName())).thenReturn(Lists.newArrayList
                    (model.versionPo));
            Long id = versionPoService.createVersion(versionDTOCreate, tenantId);
            Assert.fail("Failed to create version in case 2");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to create version in case 2", StringUtils.equals(e.getCode(), "020243"));
        }
    }

    @Test
    public void updateVersionAndTaxonomy() {
        VersionDTOUpdateTaxonomy versionDTOUpdateTaxonomy = new VersionDTOUpdateTaxonomy(463L, "test1", 3, 100,
                "5b4d5d7e99ebfa3530fbc3cf", "", 1, 0, "187");
        // case 1: version does not exist
        try {
            versionPoService.updateVersion(versionDTOUpdateTaxonomy, tenantId);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update version in case 1", StringUtils.equals(e.getCode(), "020239"));
        }
        VersionPo versionPo = new VersionPo();
        versionPo.setDatasourceId("187");
        versionPo.setTenantPo(model.tenantPo);
        versionPo.setTreeId("5b56ee2c99ebfa33780b6d86");
        versionPo.setName("test");
        versionPo.setOperationFlag(TemplateStatusType.ACTIVE);
        versionPo.setId(1L);
        // case 2: No permission to update version
        Mockito.when(versionPoJPA.findById(versionDTOUpdateTaxonomy.getVersionId())).thenReturn(versionPo);
        try {
            versionPoService.updateVersion(versionDTOUpdateTaxonomy, 2L);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update version in case 2", StringUtils.equals(e.getCode(), "020242"));
        }
        // case 3: The version has exist
        Mockito.when(versionPoJPA
                .findByTenantPoAndName(versionPo.getTenantPo(), versionDTOUpdateTaxonomy.getName())).thenReturn(Lists
                .newArrayList(model.versionPo));
        try {
            versionPoService.updateVersion(versionDTOUpdateTaxonomy, tenantId);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to update version in case 3", StringUtils.equals(e.getCode(), "020243"));
        }
        // case 4: No exception
        try {
            Mockito.when(versionPoJPA
                    .findByTenantPoAndName(versionPo.getTenantPo(), versionDTOUpdateTaxonomy.getName())).thenReturn
                    (Collections.emptyList());
            NodeCountAndDepthVO nodeCountAndDepthVO = new NodeCountAndDepthVO(5, 100);
            Mockito.when(taxonomyAPI.countMaxDepthAndNodeCount(
                    String.valueOf(versionPo.getId()))).thenReturn(nodeCountAndDepthVO);
            versionPoService.updateVersion(versionDTOUpdateTaxonomy, tenantId);
            ArgumentCaptor<VersionPo> personCaptor = ArgumentCaptor.forClass(VersionPo.class);
            verify(versionPoJPA).save(personCaptor.capture());
            VersionPo request = personCaptor.getValue();
            Assert.assertTrue("Failed to update version in case 4", StringUtils.equals(request.getName(),
                    versionDTOUpdateTaxonomy.getName()));
        } catch (AMSException e) {
            Assert.fail("Failed to update version in case 4");
        }
    }

    @Test
    public void deleteVersionAndTaxonomy() {
        VersionDtoDelete versionDtoDelete = new VersionDtoDelete(Lists.newArrayList(1L, 2L), "amsdemo");
        // case 1: Version does not exist
        try {
            versionPoService.deleteVersionByIdList(tenantId, versionDtoDelete);
            Assert.fail("Failed to delete version in case 1");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to delete version in case 1", StringUtils.equals(e.getCode(), "020246"));
        }

        // case 2: Active version can not delete
        try {
            Mockito.when(versionPoJPA.findAllByTenantPoAndAndIdIn(model.tenantPo, versionDtoDelete.getIdList()))
                    .thenReturn(Lists.newArrayList(model.versionPo, model.versionPoInfoBase));
            versionPoService.deleteVersionByIdList(tenantId, versionDtoDelete);
            Assert.fail("Failed to delete version in case 2");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to delete version in case 2", StringUtils.equals(e.getCode(), "020247"));
        }
        // case 3: No permission to delete
        try {
            Mockito.when(versionPoJPA.findAllByTenantPoAndAndIdIn(model.tenantPo, versionDtoDelete.getIdList()))
                    .thenReturn(Lists.newArrayList(model.draftVersion, model.draftVersion));
            versionPoService.deleteVersionByIdList(tenantId, versionDtoDelete);
            Assert.fail("Failed to delete version in case 3");
        } catch (AMSException e) {
            Assert.assertTrue("Failed to delete version in case 3", StringUtils.equals(e.getCode(), "020249"));
        }
    }

    @Test
    public void deleteVersionById() {
    }

    @Test
    public void updateSyncFlagById() {
    }

    @Test
    public void duplicateVersionById() {
        // case 1: Version is not exist
        try {
            versionPoService.duplicateVersionById(versionId, username);
        } catch (AMSException e) {
            Assert.assertTrue("Failed to duplicate version in case 1", StringUtils.equals(e.getCode(), "020239"));
        }
        // case 2: No exception
        try {
            Mockito.when(versionPoJPA.findById(versionId)).thenReturn(model.versionPo);
            versionPoService.duplicateVersionById(versionId, username);
            ArgumentCaptor<VersionPo> personCaptor = ArgumentCaptor.forClass(VersionPo.class);
            verify(versionPoJPA).save(personCaptor.capture());
            VersionPo request = personCaptor.getValue();
            Assert.assertTrue("Failed to duplicate version in case 2", TemplateStatusType.READY.equals(request
                    .getOperationFlag()));
        } catch (AMSException e) {
            Assert.fail("Failed to duplicate version in case 2");
        }
    }

    @Test
    public void listVersionByDatasourceId() {
        Mockito.when(versionPoJPA.findByDatasourceIdNotNull()).thenReturn(Lists.newArrayList(model.versionPoInfoBase,
                model.versionPo));
        versionPoService.listVersionByDatasourceId(187);
        ArgumentCaptor<List> personCaptor = ArgumentCaptor.forClass(List.class);
        verify(versionPoMapper).map(personCaptor.capture());
        List<VersionPo> request = personCaptor.getValue();
        Assert.assertTrue("Failed to get version list by datasource id", request.size() == 1);
    }

    @Test
    public void listTenantPathByIds() {
    }

//    @Test
//    public void generateMarkdownDocs() throws Exception {
//        //    输出Markdown格式
//        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
//                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
//                .withOutputLanguage(Language.EN)
//                .withPathsGroupedBy(GroupBy.TAGS)
//                .withGeneratedExamples()
//                .withoutInlineSchema()
//                .build();
//        Swagger2MarkupConverter.from(new URL("http://localhost:8081/v2/api-docs?group=ACP-API"))
//                .withConfig(config)
//                .build()
//                .toFile(Paths.get("/private/tmp/acp-api.md"));
//    }
}