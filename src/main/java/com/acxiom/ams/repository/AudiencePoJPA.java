package com.acxiom.ams.repository;

import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.LookalikeType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.AudiencePo;

import java.util.Date;
import java.util.List;

import com.acxiom.ams.model.po.FolderPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

/**
 * Created by cldong on 12/5/2017.
 */
@Component
public interface AudiencePoJPA extends JpaRepository<AudiencePo, Long> {

    @Query(name = "get_all_segment_by_folderId")
    List<AudiencePo> getSegmentListByFolderId(@Param("rootId") String folderId);

    @Query(name = "get_segment_count_by_folderId")
    int getSegmentCountByFolderId(@Param("rootId") String folderId);

    List<AudiencePo> findAll(Specification<AudiencePo> spec);


    List<AudiencePo> findByNameLikeAndAudienceTypeInAndTenantIdOrderByUpdateTime(String name, FolderType[]
            folderTypes, Long tenantId);

    AudiencePo findFirstByNameAndTenantIdAndAudienceType(String name, Long tenantId, FolderType folderType);

    List<AudiencePo> findAudiencePoByIdIn(List<Long> ids);

    List<AudiencePo> findByFolderPoIn(List<FolderPo> folderPoList);

    AudiencePo findAudiencePoByTaxonomyId(String taxonomyId);

    AudiencePo findAudiencePoByAudienceTypeAndTenantIdAndName(FolderType folderType, Long tenantId, String name);

    List<AudiencePo> findAudiencePoByIdInAndAudienceType(List<Long> ids, FolderType folderType);

    @Modifying
    @Query("update AudiencePo v set v.segmentStatusType = ?1 where v.taxonomyId = ?2")
    void updateSegmentStatusTypeByTaxonomyId(SegmentStatusType segmentStatusType, String taxonomyId);

    @Modifying
    @Query("update AudiencePo v set v.count = ?1 where v.taxonomyId = ?2")
    void updateCountByTaxonomyId(Long count, String taxonomyId);

    void deleteAudiencePoByIdIn(List<Long> ids);

    List<AudiencePo> findAudiencePoByNameLikeAndTenantId(String name, Long tenantId);

    List<AudiencePo> findByAudienceTypeAndTenantIdAndNameLike(FolderType audienceType, Long tenantId, String key);

    List<AudiencePo> findByAudienceTypeAndTenantId(FolderType audienceType, Long tenantId);

    @Query(value = "select " +
            "sum(case when au.audience_status = ?1 and DATE_FORMAT(au.update_time,'%Y-%m') = " +
            "?2 and au.tenant_id not in (?3) then 1 else 0 end)  as distributed_count from audience au", nativeQuery = true)
    Long countDistributedAudiencesByMonth(String audienceStatus, String yearMonth, List<Long> tenantList);

    @Query(value = "select " +
            "sum(case when au.audience_type = ?1 and DATE_FORMAT(au.created_time,'%Y-%m') = ?2 and au.tenant_id not in (?3) then 1" +
            " else 0 end)  as built_count " +
            "from audience au", nativeQuery = true)
    Long countBuiltAudiencesByMonth(String audienceType, String yearMonth, List<Long> tenantList);

    List<AudiencePo> findBySegmentStatusTypeInAndTenantIdAndDistributionFlag(SegmentStatusType[] statusType, Long tenantId, Boolean distributionFlag);

    AudiencePo findByIdAndTenantId(Long id, Long tenantId);

    List<AudiencePo> findAllByAudienceTypeAndCreatedTimeBetween(FolderType folderType, Date startDate, Date endDate);

    List<AudiencePo> findAllBySegmentStatusTypeAndLookalikeType(SegmentStatusType segmentStatusType, LookalikeType lookalikeType);

    List<AudiencePo> findAllByUniverseIdsLike(String universeId);

    Page<AudiencePo> findByNameLikeAndAudienceTypeInAndTenantId(String name, FolderType[]
            folderTypes, Long tenantId, Pageable pageable);
}
