package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.*;
import com.acxiom.ams.model.dto.*;
import com.acxiom.ams.model.dto.v2.CampaignParam;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.em.SourceType;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.vo.*;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by cldong on 12/5/2017.
 */
public interface AudiencePoService {
    List<SourceItem> getSourceList(Long tenantId)
            throws AMSException;

    List<TreeItemVo> getTreeItem(Long tenantId, SourceType sourceType, String id)
            throws AMSException;

    Map<Long, SegmentStatusType> getAudienceStatus(AudienceStatus audienceStatus);

    Page<AudiencePo> getAudienceItemByKey(Long tenantId, String key, FolderType folderType, Integer pageNumber
            , Integer pageSize)
            throws AMSInvalidInputException;

    List<TreeItemVo> searchTreeItem(Long tenantId, String key)
            throws AMSException;

    Map<String, String> getParentPath(Long treeTenantId, String nodeId)
            throws AMSException;

    Long calculate(Long tenantId, String userId, String rule)
            throws AMSException;

    void refreshSegment(FolderAndAudience folderAndAudience, Long tenantId)
            throws AMSException;

    void deleteAudienceAndFolder(FolderAndAudience folderAndAudience)
            throws AMSInvalidInputException;

    AudiencePo getSegment(Long audienceId) throws AMSInvalidInputException;

    List<TreeItemVo> getSegmentOption(Long audienceId) throws AMSException;

    List<TaxonomyItemVo> getTaxonomyTreeItemByTenant(Long tenantId, String id)
            throws AMSException;

    void updateSegment(Long tenantId, Long audienceId, TemporarySegment temporarySegment)
            throws AMSException;

    AudiencePo saveSegment(Long tenantId, String taxonomyId, TemporarySegment temporarySegment)
            throws AMSException;

    List<AudiencePo> getAudiencePoByIds(List<Long> ids);

    void copySegment(Long audienceId, String taxonomyId, SegmentForCopyDTO audienceParam)
            throws AMSException;

    SegmentAndAttributeVo getSegmentAndAttributeByKey(Long tenantId, String key)
            throws AMSException;

    // get attribute by node id
    List<TaxonomyItemVo> getAttributeByNodeIdAndName(Long tenantId, List<String> nodeIds)
            throws AMSException;

    // v2 service
    void copyCampaign(Long audienceId, AudienceParam audienceParam)
            throws AMSException;

    List<SourceItem> getSourceListV2(Long universeId, Long tenantId)
            throws AMSException;

    List<TreeItemVo> getAllTreeItem(Long treeTenantId, SourceType sourceType, List<Long> universeIdList, String id)
            throws AMSException;

    List<TaxonomyItemVo> getTaxonomyTreeItem(Long tenantId, List<Long> universeIdList, String id)
            throws AMSException;

    List<DestinationVo> getDestination(Long tenantId, String username) throws AMSInvalidInputException, AMSRMIException;

    Map<String, List<TreeItemVo>> searchTreeItemV2(Long tenantId, List<Long> universeIdList, String key)
            throws AMSException;

    Map calculateV2(List<Long> universeIdList, String rule)
            throws AMSException;

    AudiencePo createCampaign(Long tenantId, CampaignParam campaignParam)
            throws AMSException;

    List<AudiencePo> getCampaignStatusByIds(Long[] campaignIds);

    ResponseEntity<Resource> exportCampaign(FolderAndAudience folderAndAudience, Long tenantId)
            throws AMSException;

    void refreshCampaign(FolderAndAudience folderAndAudience)
            throws AMSException;

    void updateCampaign(Long tenantId, Long audienceId, CampaignParam campaignParam)
            throws AMSException;

    List<DataStoreVo> getDataStoreNodeList() throws AMSInvalidInputException;

    Boolean checkSecurity(SecurityParam securityParam);

    // get attribute by node id
    List<TaxonomyItemVo> getAttributeByNodeIdAndNameV2(List<String> nodeIds, Long tenantId)
            throws AMSException;

    List<OwnerTypeVo> getOwnerTypeV2(List<String> ownerList, List<NodeDTO> nodeIdList,
                                     List<SegmentDTO> segmentDTOList, Long destinationId) throws AMSException;

    List<OwnerAndDataType> getOwnerTypeV3(List<String> ownerList, List<NodeDTO> nodeIdList,
                                          List<SegmentDTO> segmentDTOList, Long tenantId) throws AMSException;

    String getTaxonomyAllAttribute(String taxonomyName) throws AMSInvalidInputException, AMSRMIException;

    List<PriceAndOwnerVO> listPriceAndOwnerByTaxonomyId(Long tenantId, Long destinationId,
                                                        List<String> taxonomyIdList) throws AMSException;

    CampaignAndAttributeVO getSegmentAndAttributeByKeyV2(List<Long> universeIdList, String key, Long tenantId) throws AMSException;

    Long calculateForNonTv(Long tenantId, String userId, String rule)
            throws AMSException;

    List<DataTypeAndPriceAndOwnerVO> listDataTypeAndPriceAndOwnerByTaxonomyIdList(Long destinationId,
                                                                                  List<String> taxonomyIdList) throws AMSException;

    MetricsVO countDistributedAndBuiltAudiencesByMonth(String yearMonth, SegmentStatusType audienceStatus,
                                                       FolderType audienceType) throws AMSException;

    List<TaxonomyItemVo> getTaxonomyEndTypeItemByTenantWithinLimit(Long tenantId, Integer limit) throws AMSException;

    List<TaxonomyItemVo> getTaxonomyTreeEndTypeItemDestinationIdWithinLimit(Long tenantId, Integer limit) throws AMSException;

    List<DataTypeAndPriceAndOwnerVO> listNodeInfoByTenantIdAndTaxonomyIdList(Long tenantId,
                                                                             List<String> taxonomyIdList) throws AMSException;

    List<DataStoreNodeSortByTenantVO> listDataStoreNodesByDate(Date startDate, Date endDate) throws AMSException;

    void updateSegmentDistributionFlag(Long tenantId, Long audienceId, Boolean distributionFlag) throws AMSInvalidInputException;

    void updateSegmentStatus(String audienceId, String status, AudienceCallbackParam param);

    void audienceShare(AudiencesShareDTO audienceSharesDTO) throws AMSException;
}
