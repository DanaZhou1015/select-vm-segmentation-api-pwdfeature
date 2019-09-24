package com.acxiom.ams.controller;

import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSResouceRequestException;
import com.acxiom.ams.mapper.AudiencePoMapper;
import com.acxiom.ams.mapper.FolderPoMapper;
import com.acxiom.ams.mapper.FolderPoToVoMapper;
import com.acxiom.ams.model.Model;
import com.acxiom.ams.model.dto.FolderParam;
import com.acxiom.ams.model.dto.PermissionDTO;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.io.FolderIo;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.po.FolderPo;
import com.acxiom.ams.model.vo.AudienceAndFolderVo;
import com.acxiom.ams.model.vo.PermissionVo;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.repository.FolderPoJPA;
import com.acxiom.ams.service.impl.FolderServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 5:05 PM 9/20/2018
 */
public class FolderControllerTest {
    @Mock
    FolderPoJPA folderJPA;
    @Mock
    AudiencePoJPA audiencePoJPA;
    @InjectMocks
    FolderServiceImpl folderService;
    @Mock
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Mock
    AudiencePoMapper audiencePoMapper;
    @Mock
    FolderPoMapper folderPoMapper;
    @Mock
    FolderPoToVoMapper folderPoToVoMapper;
    Model model = Model.getInstance();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        List<AudiencePo> audiencePoList = new ArrayList<>();
        Mockito.when(audiencePoJPA.getSegmentListByFolderId("")).thenReturn(audiencePoList);
    }

    @Test
    public void getAudienceListByFolderIdAndTenantId() {
        long folderId = 1;
        long tenantId = 1;
        Boolean isSort = false;
        FolderType folderType = FolderType.SAVED_SEGMENT;
        List<AudienceAndFolderVo> audienceAndFolderVoList = new ArrayList<>();
        AudienceAndFolderVo audienceAndFolderVo = new AudienceAndFolderVo();
        audienceAndFolderVo.setUpdateTime(new Date());
        audienceAndFolderVo.setName("test1");
        audienceAndFolderVoList.add(audienceAndFolderVo);
        AudienceAndFolderVo audienceAndFolderVo1 = new AudienceAndFolderVo();
        audienceAndFolderVo.setUpdateTime(new Date());
        audienceAndFolderVo.setName("test2");
        audienceAndFolderVoList.add(audienceAndFolderVo1);
        Mockito.when(audiencePoMapper.map(model.segmentFolder.getAudiencePoList())).thenReturn(audienceAndFolderVoList);
        Mockito.when(folderPoMapper.map(Lists.newArrayList(model.segmentFolder1))).thenReturn(audienceAndFolderVoList);
        // case 1: folder id is 1 and folder is not exist
        try {
            Mockito.when(folderJPA.getFolderPoByIdAndFolderType(folderId, folderType)).thenReturn(null);
            folderService.getFolderListByTenantId(folderId, tenantId, isSort, folderType);
            Assert.fail("Failed to get audience list by folder id and tenant id case 1");
        } catch (AMSInvalidInputException e) {
            Assert.assertTrue("Failed to get audience list by folder id and tenant id case 1", StringUtils.equals(e
                    .getCode(), "020205"));
        }
        // case 2: folder id is 1 and no exception
        try {
            Mockito.when(folderJPA.getFolderPoByIdAndFolderType(folderId, folderType)).thenReturn(model.segmentFolder);
            Mockito.when(folderJPA.getFolderPoByParentFolderIdAndTenantIdAndFolderType(model.segmentFolder.getId(),
                    tenantId, folderType)).thenReturn(Lists.newArrayList(model.segmentFolder1));
            folderService.getFolderListByTenantId(folderId, tenantId, isSort, folderType);
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to get audience list by folder id and tenant id case 2");
        }
        // case 3: folder id is 4 and folder is not exist
        try {
            Mockito.when(folderJPA.getFolderPoByIdAndTenantIdAndFolderType(4L, tenantId, folderType)).thenReturn(null);
            folderService.getFolderListByTenantId(4, tenantId, isSort, folderType);
            Assert.fail("Failed to get audience list by folder id and tenant id case 3");
        } catch (AMSInvalidInputException e) {
            Assert.assertTrue("Failed to get audience list by folder id and tenant id case 3", StringUtils.equals(e
                    .getCode(), "020205"));
        }
        // case 4: folder id is 4 and no exception
        try {
            Mockito.when(folderJPA.getFolderPoByIdAndFolderType(folderId, folderType)).thenReturn(model.segmentFolder);
            Mockito.when(folderJPA.getFolderPoByParentFolderIdAndTenantIdAndFolderType(model.segmentFolder.getId(),
                    tenantId, folderType)).thenReturn(Lists.newArrayList(model.segmentFolder1));
            folderService.getFolderListByTenantId(folderId, tenantId, isSort, folderType);
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to get audience list by folder id and tenant id case 4");
        }
    }

    @Test
    public void createFolder() {
        FolderIo folderIo = new FolderIo("segment1", "amsdemo", 1L, 1L, FolderType.SAVED_SEGMENT);
        // case 1: parent folder is exist and folder name is duplicate
        Mockito.when(folderJPA
                .getByParentFolderIdAndTenantId(folderIo.getParentId(),
                        folderIo.getTenantId())).thenReturn(Lists.newArrayList(model.segmentFolder, model
                .segmentFolder1));
        try {
            folderService.createFolder(folderIo);
            Assert.fail("Failed to create folder in case 1");
        } catch (AMSInvalidInputException e) {
            Assert.assertTrue("Failed to create folder in case 1", StringUtils.equals(e.getCode(), "020207"));
        }
        // case 2: parent folder is exist and folder name is not duplicate
        try {
            folderIo.setName("segment2");
            folderService.createFolder(folderIo);
            ArgumentCaptor<FolderPo> personCaptor = ArgumentCaptor.forClass(FolderPo.class);
            verify(folderJPA).save(personCaptor.capture());
            FolderPo request = personCaptor.getValue();
            Assert.assertTrue("Failed to create folder in case 2", StringUtils.equals(request.getFolderName(),
                    folderIo.getName()));
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to create folder in case 2");
        }
        // case 3: parent folder is not exist and type is segment
        try {
            folderIo.setParentId(null);
            folderService.createFolder(folderIo);
            ArgumentCaptor<FolderPo> personCaptor = ArgumentCaptor.forClass(FolderPo.class);
            verify(folderJPA, times(2)).save(personCaptor.capture());
            FolderPo request = personCaptor.getValue();
            Assert.assertTrue("Failed to create folder in case 3", request.getParentFolderId() == 1);
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to create folder in case 3");
        }
        // case 4: parent folder is not exist and type is looklike
        try {
            folderIo.setFolderType(FolderType.LOOKALIKE_GROUP);
            folderIo.setParentId(null);
            folderService.createFolder(folderIo);
            ArgumentCaptor<FolderPo> personCaptor = ArgumentCaptor.forClass(FolderPo.class);
            verify(folderJPA, times(3)).save(personCaptor.capture());
            FolderPo request = personCaptor.getValue();
            Assert.assertTrue("Failed to create folder in case 4", request.getParentFolderId() == 2);
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to create folder in case 4");
        }
    }

    @Test
    public void getParentFolder() throws AMSResouceRequestException {
        List<FolderPo> folderPoList = Lists.newArrayList(model.segmentFolder, model.lookAlikeFolder, model
                .campaignFolder);
        Mockito.when(folderJPA.getFolderPoByParentFolderId(0L)).thenReturn(folderPoList);
        folderPoList = folderService.getParentFolder();
        verify(folderJPA).getFolderPoByParentFolderId(0L);
        Assert.assertTrue("Failed to get parent folder", folderPoList.size() == 2);
    }

    @Test
    public void updateFolder() {
        long folderId = 1L;
        FolderPo folderPo = new FolderPo();
        folderPo.setParentFolderId(0L);
        folderPo.setTenantId(1L);
        folderPo.setCreatedBy("test");
        FolderParam folderParam = new FolderParam("segment1", "amsdemo");
        // case 1: folder is not exist
        try {
            Mockito.when(folderJPA.getFolderPoById(folderId)).thenReturn(null);
            folderService.updateFolder(folderId, folderParam);
            Assert.fail("Failed to update folder in case 1");
        } catch (AMSInvalidInputException e) {
            Assert.assertTrue("Failed to update folder in case 1", StringUtils.equals(e.getCode(), "020205"));
        }

        //case 2: folder name is duplicate
        Mockito.when(folderJPA.getFolderPoById(folderId)).thenReturn(folderPo);
        try {
            Mockito.when(folderJPA
                    .getByParentFolderIdAndTenantId(folderPo.getParentFolderId(),
                            folderPo.getTenantId())).thenReturn(Lists.newArrayList(model.segmentFolder, model
                    .segmentFolder1));
            folderService.updateFolder(folderId, folderParam);
            Assert.fail("Failed to update folder in case 2");
        } catch (AMSInvalidInputException e) {
            Assert.assertTrue("Failed to update folder in case 2", StringUtils.equals(e.getCode(), "020207"));
        }

        // case 3: has no permission to update
        Mockito.when(folderJPA
                .getByParentFolderIdAndTenantId(folderPo.getParentFolderId(),
                        folderPo.getTenantId())).thenReturn(Lists.newArrayList(model.segmentFolder));
        try {
            Mockito.when(folderJPA.getFolderPoById(folderId)).thenReturn(folderPo);
            Mockito.when(folderJPA
                    .getByParentFolderIdAndTenantId(folderPo.getParentFolderId(),
                            folderPo.getTenantId())).thenReturn(null);
            folderService.updateFolder(folderId, folderParam);
            Assert.fail("Failed to update folder in case 3");
        } catch (AMSInvalidInputException e) {
            Assert.assertTrue("Failed to update folder in case 3", StringUtils.equals(e.getCode(), "020206"));
        }
        // case 4: non exception
        folderPo.setCreatedBy("amsdemo");
        try {
            folderService.updateFolder(folderId, folderParam);
            ArgumentCaptor<FolderPo> personCaptor = ArgumentCaptor.forClass(FolderPo.class);
            verify(folderJPA).save(personCaptor.capture());
            FolderPo request = personCaptor.getValue();
            Assert.assertTrue("Failed to update folder in case 4", StringUtils.equals(request.getFolderName(),
                    folderParam.getNewFolderName()));
        } catch (AMSInvalidInputException e) {
            Assert.fail("Failed to update folder in case 4");
        }
    }

    @Test
    public void getSegmentPermissionByFolderIds() {
        List<String> folderIdList = Lists.newArrayList("1");
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setFolderIdList(folderIdList);
        permissionDTO.setUsername("amsdemo");
        Mockito.when(audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(permissionDTO.getFolderIdList(), ","))).thenReturn(Lists
                .newArrayList(model.segment_new));
        PermissionVo permissionVo = folderService.getSegmentPermissionByFolderIds(permissionDTO);
        Assert.assertTrue("Failed to get segment permission by folder ids", permissionVo.getEdit() && permissionVo
                .getCopy());

    }

    @Test
    public void checkPermission() {
        List<String> folderIdList = new ArrayList<>();
        folderIdList.add("1");
        List<Long> audienceIdList = new ArrayList<>();
        audienceIdList.add(1L);
        String username = "test";
        PermissionDTO permissionDTO = new PermissionDTO(username, folderIdList, audienceIdList);
        // case 1: audience size is 1
        Mockito.when(audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(permissionDTO.getFolderIdList(), ","))).thenReturn(new
                ArrayList<>());
        Mockito.when(audiencePoJPA.findAll(permissionDTO.getAudienceIdList())).thenReturn(Lists.newArrayList(model
                .segment_new));
        PermissionVo permissionVo = folderService.checkPermission(permissionDTO);
        Assert.assertTrue("Failed to get permission in case 1", permissionVo.getCopy() && !permissionVo.getEdit()
                && permissionVo.getDistribute());
        // case 2: audience size more than 1
        Mockito.when(audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(permissionDTO.getFolderIdList(), ","))).thenReturn(new
                ArrayList<>());
        Mockito.when(audiencePoJPA.findAll(permissionDTO.getAudienceIdList())).thenReturn(Lists.newArrayList(model
                .segment_new, model.segment_distributing));
        permissionVo = folderService.checkPermission(permissionDTO);
        Assert.assertTrue("Failed to get permission in case 2", !permissionVo.getCopy() && !permissionVo.getEdit()
                && !permissionVo.getDistribute());
    }

    @Test
    public void testPoJo() throws Exception {
        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageName = "com.acxiom.ams.model";
        String packagePath = packageName.replace(".", "/");
        Enumeration<URL> urls = FolderControllerTest.class.getClassLoader().getResources(packagePath);
        while (urls.hasMoreElements()) {
            URL url = (URL) urls.nextElement();
            if ("file".equals(url.getProtocol())) {
                String realPath = url.getFile();
                findAndAddClassesInPackageByFile(packageName, realPath,
                        recursive, classes);
            }
        }
        for (Class classObj : classes) {
            if(classObj.getName().endsWith("BaseEntity")){
                continue;
            }
            Object obj = classObj.newInstance();
            Object other = classObj.newInstance();
            obj.hashCode();
            obj.equals(other);
        }
    }

    private void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean
            recursive, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirFiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(),
                        recursive, classes);
            } else {
                if (file.getAbsolutePath().contains("em") || file.getAbsolutePath().contains("test-classes")) {
                    continue;
                }
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' +
                            className));
                } catch (ClassNotFoundException e) {
                    continue;
                }
            }
        }
    }
}