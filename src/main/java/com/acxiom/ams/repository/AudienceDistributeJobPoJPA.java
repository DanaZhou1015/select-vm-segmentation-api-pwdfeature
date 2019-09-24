package com.acxiom.ams.repository;

import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.AudienceDistributeJobPo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 14:08 12/22/2017
 */
@Component
public interface AudienceDistributeJobPoJPA extends JpaRepository<AudienceDistributeJobPo, Long> {

    List<AudienceDistributeJobPo> findByAudienceTypeAndStatus(FolderType folderType, SegmentStatusType statusType);

    AudienceDistributeJobPo findFirstByAudienceIdOrderByUpdateTimeDesc(Long audienceId);

    List<AudienceDistributeJobPo> findByAudienceIdAndDestinationIdInOrderByUpdateTimeDesc(Long audienceId, List<Long> universeIdList);

    List<AudienceDistributeJobPo> findByAudienceIdOrderByUpdateTimeDesc(Long audienceId);

    List<AudienceDistributeJobPo> findByAudienceIdAndStatusInOrderByUpdateTimeDesc(Long audienceId, List<SegmentStatusType> segmentStatusTypeList);
}
