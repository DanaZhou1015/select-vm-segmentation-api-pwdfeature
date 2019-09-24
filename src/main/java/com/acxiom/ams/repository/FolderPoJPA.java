package com.acxiom.ams.repository;

import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.po.FolderPo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 15:54 12/5/2017
 */
@Component
public interface FolderPoJPA extends JpaRepository<FolderPo, Long> {

    List<FolderPo> getFolderPoByParentFolderIdAndTenantIdAndFolderType(Long parentFolderId,
        Long tenantId, FolderType folderType);

    List<FolderPo> getFolderPoByParentFolderIdAndFolderType(Long parentFolderId, FolderType folderType);

    List<FolderPo> getFolderPoByIdInAndFolderType(Long[] ids, FolderType folderType);

    List<FolderPo> getFolderPoByIdIn(List<Long> ids);

    FolderPo getFolderPoByIdAndTenantIdAndFolderType(Long id, Long tenantId, FolderType folderType);

    List<FolderPo> getFolderPoByParentFolderId(Long parentFolderId);

    void deleteFolderPoByIdIn(Long[] ids);

    @Query(name = "get_all_child")
    List<FolderPo> getChildList(@Param("rootId") String ids);

    @Query(name = "get_all_parent")
    String getParentList(@Param("rootId") String ids);

    FolderPo getFolderPoById(Long id);

    FolderPo getFolderPoByIdAndFolderType(Long id, FolderType folderType);

    List<FolderPo> getByParentFolderIdAndTenantId(Long folderId, Long tenantId);

}
