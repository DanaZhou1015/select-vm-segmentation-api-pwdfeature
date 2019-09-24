package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSResouceRequestException;
import com.acxiom.ams.model.dto.FolderAndAudience;
import com.acxiom.ams.model.dto.FolderAndCampaign;
import com.acxiom.ams.model.dto.FolderParam;
import com.acxiom.ams.model.dto.PermissionDTO;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.io.FolderIo;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.po.FolderPo;
import com.acxiom.ams.model.vo.AllowFlagVo;
import com.acxiom.ams.model.vo.AudienceAndFolderVo;
import com.acxiom.ams.model.vo.FolderVo;
import com.acxiom.ams.model.vo.PermissionVo;

import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 15:02 12/5/2017
 */
public interface FolderService {

    List<AudienceAndFolderVo> getFolderListByTenantId(long parentFolderId, long tenantId,
        Boolean isSort, FolderType folderType)
        throws AMSInvalidInputException;

    List<FolderPo> getParentFolder() throws AMSResouceRequestException;

    void deleteFolderAndAudience(FolderAndAudience folderAndAudience) throws AMSInvalidInputException;

    FolderVo createFolder(FolderIo folderIo) throws AMSInvalidInputException;

    void updateFolder(long folderId, FolderParam folderParam) throws AMSInvalidInputException;

    int getSegmentCountByFolderIds(String folderIds);

    PermissionVo getSegmentPermissionByFolderIds(PermissionDTO permissionDTO);

    List<AudiencePo> getSegmentListByFolderIds(String folderIds);

    List<AudienceAndFolderVo> getFolderListByTenantIdV2(long parentFolderId, long tenantId, FolderType folderType)
            throws AMSException;

    List<FolderPo> getFoldersByTenantIdV2(long parentFolderId, long tenantId, FolderType folderType)
        throws AMSInvalidInputException;

    List<FolderPo> getParentFolderV2() throws AMSInvalidInputException;

    List<AudienceAndFolderVo> listCampaignByTenantId(long folderId, long tenantId, FolderType folderType)
        throws AMSInvalidInputException;

    PermissionVo checkPermission(PermissionDTO permissionDTO);

    PermissionVo checkPermissionV2(PermissionDTO permissionDTO);

    Boolean isAllowDistributeV2(FolderAndCampaign folderAndCampaign);

    AllowFlagVo isAllowDistributeAndDelete(FolderAndCampaign folderAndCampaign);

    String getParentFolderNameByFolderId(String folderId) throws AMSInvalidInputException;

    FolderPo getFolderById(Long id) throws AMSInvalidInputException;
}
