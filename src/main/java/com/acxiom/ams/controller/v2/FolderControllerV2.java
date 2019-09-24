package com.acxiom.ams.controller.v2;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.mapper.AudiencePoMapper;
import com.acxiom.ams.mapper.FolderPoMapper;
import com.acxiom.ams.mapper.FolderPoToVoMapper;
import com.acxiom.ams.model.dto.FolderAndCampaign;
import com.acxiom.ams.model.dto.FolderParam;
import com.acxiom.ams.model.dto.PermissionDTO;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.io.FolderIo;
import com.acxiom.ams.model.vo.AllowFlagVo;
import com.acxiom.ams.model.vo.AudienceAndFolderVo;
import com.acxiom.ams.model.vo.FolderVo;
import com.acxiom.ams.model.vo.PermissionVo;
import com.acxiom.ams.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:32 12/6/2017
 */
@RestController
@Validated
@RequestMapping(value = "/v2/folder")
public class FolderControllerV2 {
    @Autowired
    FolderService folderService;
    @Autowired
    FolderPoToVoMapper folderPoToVoMapper;
    @Autowired
    FolderPoMapper folderPoMapper;
    @Autowired
    AudiencePoMapper audiencePoMapper;

    @GetMapping(value = "/list/{tenantId}/{folderId}")
    public List<AudienceAndFolderVo> getAudienceListByFolderIdAndTenantId(
        @PathVariable(value = "folderId") long folderId,
        @PathVariable(value = "tenantId") long tenantId,
        @RequestParam(value = "folderType")
        @NotNull(message = "{message.error.folderType}") FolderType folderType)
            throws AMSException {
        return folderService.getFolderListByTenantIdV2(folderId, tenantId, folderType);
    }
    @GetMapping(value = "/{tenantId}/{folderId}")
    public List<AudienceAndFolderVo> getFoldersByTenantIdV2(@PathVariable(value = "folderId") long folderId,
        @PathVariable(value = "tenantId") long tenantId,
        @RequestParam(value = "folderType")
        @NotNull(message = "{message.error.folderType}") FolderType folderType) throws AMSInvalidInputException {
        return folderPoMapper.map(folderService.getFoldersByTenantIdV2(folderId, tenantId, folderType));
    }

    @PostMapping(value = "")
    public void createFolder(@RequestBody @Valid FolderIo folderIo)
        throws AMSInvalidInputException {
        folderService.createFolder(folderIo);
    }

    @GetMapping(value = "/parent")
    public List<FolderVo> getParentFolder()
        throws AMSInvalidInputException {
        return folderPoToVoMapper.map(folderService.getParentFolderV2());
    }

    @PutMapping(value = "/{folderId}")
    public void updateFolder(@PathVariable(value = "folderId") long folderId,
      @RequestBody @Valid FolderParam folderParam)
        throws AMSInvalidInputException {
        folderService.updateFolder(folderId, folderParam);
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

    @PostMapping(value = "/check/distribution")
    public Boolean isAllowDistribution(@RequestBody
        FolderAndCampaign folderAndCampaign) {
        return folderService.isAllowDistributeV2(folderAndCampaign);
    }

    @PostMapping(value = "/allow/distribute/delete")
    public AllowFlagVo isAllowDistributeAndDelete(@RequestBody
        FolderAndCampaign folderAndCampaign) {
        return folderService.isAllowDistributeAndDelete(folderAndCampaign);
    }

    @PostMapping(value = "/check/permission")
    public PermissionVo checkPermission(@RequestBody PermissionDTO permissionDTO) {
        return folderService.checkPermissionV2(permissionDTO);
    }
}
