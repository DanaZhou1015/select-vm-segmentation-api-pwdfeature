package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSResouceRequestException;
import com.acxiom.ams.mapper.AudiencePoMapper;
import com.acxiom.ams.mapper.FolderPoToVoMapper;
import com.acxiom.ams.model.dto.FolderParam;
import com.acxiom.ams.model.dto.PermissionDTO;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.io.FolderIo;
import com.acxiom.ams.model.vo.AudienceAndFolderVo;
import com.acxiom.ams.model.vo.FolderVo;
import com.acxiom.ams.model.vo.PermissionVo;
import com.acxiom.ams.service.FolderService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:32 12/6/2017
 */
@RestController
@Validated
@RequestMapping(value = "/v1/folder")
public class FolderController {

    @Autowired
    FolderService folderService;
    @Autowired
    FolderPoToVoMapper folderPoToVoMapper;
    @Autowired
    AudiencePoMapper audiencePoMapper;

    @GetMapping(value = "/list/{folderId}/{tenantId}/{isSort}")
    public List<AudienceAndFolderVo> getAudienceListByFolderIdAndTenantId(
        @PathVariable(value = "folderId") long folderId,
        @PathVariable(value = "tenantId") long tenantId,
        @PathVariable(value = "isSort") Boolean isSort,
        @RequestParam(value = "folderType")
        @NotNull(message = "{message.error.folderType}") FolderType folderType)
        throws AMSInvalidInputException {
        return folderService.getFolderListByTenantId(folderId, tenantId, isSort, folderType);
    }

    @PostMapping(value = "")
    public FolderVo createFolder(@RequestBody @Valid FolderIo folderIo)
        throws AMSInvalidInputException {
        return folderService.createFolder(folderIo);
    }

    @GetMapping(value = "/parent")
    public List<FolderVo> getParentFolder() throws AMSResouceRequestException {
        List<FolderVo> folderVoList = folderPoToVoMapper.map(folderService.getParentFolder());
        return folderVoList;
    }

    @PutMapping(value = "/{folderId}")
    public void updateFolder(@PathVariable(value = "folderId") long folderId,
        @RequestBody @Valid FolderParam folderParam)
        throws AMSInvalidInputException {
        folderService.updateFolder(folderId, folderParam);
    }

    @PostMapping(value = "/segment/permission")
    public PermissionVo getSegmentPermissionByFolderIds(@RequestBody @Valid PermissionDTO permissionDTO) {
        return folderService.getSegmentPermissionByFolderIds(permissionDTO);
    }

    @GetMapping(value = "/segment/count/{folderIds}")
    public int getSegmentCountByFolderIds(@PathVariable(value = "folderIds") String folderIds) {
        return folderService.getSegmentCountByFolderIds(folderIds);
    }

    @GetMapping(value = "/segment/list/{folderIds}")
    public List<AudienceAndFolderVo> getSegmentListByFolderIds(
        @PathVariable(value = "folderIds") String folderIds) {
        return audiencePoMapper.map(folderService.getSegmentListByFolderIds(folderIds));
    }

    @PostMapping(value = "/check/permission")
    public PermissionVo checkPermission(@RequestBody
        PermissionDTO permissionDTO) {
        return folderService.checkPermission(permissionDTO);
    }
}
