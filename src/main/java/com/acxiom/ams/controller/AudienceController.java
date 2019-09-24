package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.mapper.AudiencePoMapper;
import com.acxiom.ams.mapper.AudienceVoMapper;
import com.acxiom.ams.model.dto.*;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.em.SourceType;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.vo.*;
import com.acxiom.ams.service.AudiencePoService;
import com.acxiom.ams.util.UUID;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by cldong on 12/5/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/v1/audience")
public class AudienceController {

    @Autowired
    AudiencePoService audiencePoService;
    @Autowired
    AudienceVoMapper audienceVoMapper;
    @Autowired
    AudiencePoMapper audiencePoMapper;

    @GetMapping(value = "/source/{tenantId}")
    public List<SourceItem> getSourceList(@PathVariable(value = "tenantId") Long tenantId)
            throws AMSException {
        return audiencePoService.getSourceList(tenantId);
    }

    @GetMapping(value = "/tree/{tenantId}")
    public List<TreeItemVo> getTreeItem(@PathVariable(value = "tenantId") Long tenantId,
                                        @RequestParam(name = "sourceType") @NotNull(message = "{sourceType.notNull}") SourceType sourceType,
                                        @RequestParam(name = "id") String id)
            throws AMSException {
        return audiencePoService.getTreeItem(tenantId, sourceType, id);
    }

    @PostMapping(value = "/tree/status/{tenantId}")
    public Map<Long, SegmentStatusType> getTreeItemStatus(
            @PathVariable(value = "tenantId") Long tenantId,
            @RequestBody @Valid AudienceStatus audienceStatus) {
        return audiencePoService.getAudienceStatus(audienceStatus);
    }

    @GetMapping(value = "/search/{tenantId}")
    public Page<AudiencePo> searchAudienceItem(
            @PathVariable(value = "tenantId") Long tenantId,
            @RequestParam(name = "key") String key,
            @RequestParam(name = "folderType")
            @NotNull(message = "{message.error.folderType}") FolderType folderType,
            @RequestParam(name = "pageNumber") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer pageSize)
            throws AMSInvalidInputException {
        return audiencePoService.getAudienceItemByKey(tenantId, key, folderType, pageNumber, pageSize);
    }

    @GetMapping(value = "/tree/search/{tenantId}")
    public List<TreeItemVo> searchTreeItem(@PathVariable(value = "tenantId") Long tenantId,
                                           @RequestParam(name = "key") String key)
            throws AMSException {
        return audiencePoService.searchTreeItem(tenantId, key);
    }

    @GetMapping(value = "/tree/item/{tenantId}")
    public List<TaxonomyItemVo> getTaxonomyItem(@PathVariable(value = "tenantId") Long tenantId,
                                                @RequestParam(name = "id") String id) throws AMSException {
        return audiencePoService.getTaxonomyTreeItemByTenant(tenantId, id);
    }

    /**
     * @param tenantId
     * @param limit    should >=0
     * @return java.util.List<com.acxiom.ams.model.vo.TaxonomyItemVo>
     * @methodName getTaxonomyEndTypeItem
     * @author Owen.Que
     * @description get the Taxonomy which type is 'end' and 'limit' the data number of result
     * @date 8/30/2018 17:57
     */
    @GetMapping(value = "/endItem/{tenantId}/{limit}")
    public List<TaxonomyItemVo> getTaxonomyEndTypeItem(@PathVariable(value = "tenantId") Long tenantId,
                                                       @PathVariable(value = "limit") Integer limit) throws AMSException {
        return audiencePoService.getTaxonomyEndTypeItemByTenantWithinLimit(tenantId, limit);
    }

    @GetMapping(value = "/parent/path/{tenantId}")
    public Map<String, String> getParentPath(@PathVariable(value = "tenantId") Long tenantId,
                                             @RequestParam(name = "nodeId") String nodeId)
            throws AMSException {
        return audiencePoService.getParentPath(tenantId, nodeId);
    }

    @PostMapping(value = "/calculate/{tenantId}/{userId}")
    public Long calculate(@PathVariable(value = "tenantId") Long tenantId,
                          @PathVariable(value = "userId") String userId,
                          @RequestBody String rule)
            throws AMSException {
        return audiencePoService.calculate(tenantId, userId, rule);
    }

    @PostMapping(value = "/refresh/{tenantId}")
    public void refreshSegment(@RequestBody @Valid FolderAndAudience folderAndAudience,
                               @PathVariable("tenantId") Long tenantId)
            throws AMSException {
        audiencePoService.refreshSegment(folderAndAudience, tenantId);
    }

    @PostMapping(value = "/refresh")
    public void refreshSegment(@RequestBody @Valid FolderAndAudience folderAndAudience)
            throws AMSException {
        audiencePoService.refreshSegment(folderAndAudience, (long) 0);
    }

    @DeleteMapping(value = "/delete")
    public void deleteAudienceAndFolder(@RequestBody @Valid FolderAndAudience folderAndAudience)
            throws AMSInvalidInputException {
        audiencePoService.deleteAudienceAndFolder(folderAndAudience);
    }

    @GetMapping(value = "/{audienceId}")
    public AudienceVo getSegment(@PathVariable(value = "audienceId") Long audienceId)
            throws AMSInvalidInputException {
        return audienceVoMapper.map(audiencePoService.getSegment(audienceId));
    }

    @GetMapping(value = "/option/{audienceId}")
    public List<TreeItemVo> getSegmentChoice(@PathVariable(value = "audienceId") Long audienceId)
            throws AMSException {
        return audiencePoService.getSegmentOption(audienceId);
    }

    @PutMapping(value = "/{tenantId}/{audienceId}")
    public void updateSegment(@PathVariable(value = "tenantId") Long tenantId,
                              @PathVariable(value = "audienceId") Long audienceId,
                              @RequestBody @Valid TemporarySegment temporarySegment)
            throws AMSException {
        audiencePoService.updateSegment(tenantId, audienceId, temporarySegment);
    }

    @PostMapping(value = "/{tenantId}")
    public AudiencePo saveSegment(@PathVariable(value = "tenantId") Long tenantId,
                                  @RequestBody @Valid TemporarySegment temporarySegment)
            throws AMSException {
        String taxonomyId = UUID.GetTaxonomyID();
        return audiencePoService.saveSegment(tenantId, taxonomyId, temporarySegment);
    }

    @PostMapping(value = "/copy/{audienceId}")
    public void copySegment(@PathVariable(value = "audienceId") Long audienceId,
                            @RequestBody @Valid SegmentForCopyDTO audienceParam)
            throws AMSException {
        String taxonomyId = UUID.GetTaxonomyID();
        audiencePoService.copySegment(audienceId, taxonomyId, audienceParam);
    }

    /**
     * @Author: Fermi.Tang
     * @Description: get segment and attribute by Key
     * @Date: 5/8/2018 2:16 PM
     * @Param: [tenantId, key]
     * @Return: com.acxiom.ams.model.vo.SegmentAndAttributeVo
     */
    @GetMapping(value = "/list/attribute/search/{tenantId}")
    public SegmentAndAttributeVo getSegmentAndAttributeByKey(@PathVariable(value = "tenantId") Long tenantId,
                                                             @RequestParam @NotBlank(message = "{message.error.key}") String key)
            throws AMSException {
        return audiencePoService.getSegmentAndAttributeByKey(tenantId, key);
    }

    /**
     * @Author: Fermi.Tang
     * @Description: back push attribute by node id
     * @Date: 5/8/2018 2:04 PM
     * @Param: [tenantId, nodeIds]
     * @Return: java.util.List<com.acxiom.ams.model.vo.TaxonomyItemVo>
     */
    @GetMapping(value = "/node/attribute/{tenantId}")
    public List<TaxonomyItemVo> getAttributeByNodeId(@PathVariable(value = "tenantId") Long tenantId,
                                                     @RequestParam(name = "nodeIds") @NotNull(message = "{message" +
                                                             ".error.nodeIds}") List<String> nodeIds)
            throws AMSException {
        return audiencePoService.getAttributeByNodeIdAndName(tenantId, nodeIds);
    }

    @GetMapping(value = "/taxonomy/all/attribute/{taxonomyName}")
    public String getTaxonomyAllAttribute(@PathVariable(value = "taxonomyName") String taxonomyName) throws AMSException {
        return audiencePoService.getTaxonomyAllAttribute(taxonomyName);
    }

    @GetMapping(value = "/metrics")
    public MetricsVO countDistributedAndBuiltAudiencesByMonth(@RequestParam(value = "yearMonth") String yearMonth) throws AMSException {
        return audiencePoService.countDistributedAndBuiltAudiencesByMonth(yearMonth,
                SegmentStatusType.SEGMENT_DISTRIBUTED, FolderType.SAVED_SEGMENT);
    }

    @PostMapping(value = "/data/price/owner/{tenantId}")
    public List<DataTypeAndPriceAndOwnerVO> listNodeInfoByTenantIdAndTaxonomyIdList(
            @PathVariable("tenantId") Long tenantId,
            @RequestBody List<String> taxonomyIdList)
            throws AMSException {
        return audiencePoService.listNodeInfoByTenantIdAndTaxonomyIdList(tenantId, taxonomyIdList);
    }

    @PutMapping(value = "/{audienceId}/{tenantId}/{distributionFlag}")
    public void updateSegmentDistributionFlag(@PathVariable(value = "audienceId") Long audienceId,
                                              @PathVariable(value = "tenantId") Long tenantId,
                                              @PathVariable(value = "distributionFlag") Boolean distributionFlag)
            throws AMSInvalidInputException {
        audiencePoService.updateSegmentDistributionFlag(tenantId, audienceId, distributionFlag);
    }

    @PutMapping(value = "/{taxId}/status/{status}")
    public void callback(@PathVariable(value = "taxId") String taxId, @PathVariable(value = "status") String status,
                         @RequestBody AudienceCallbackParam param) {
        audiencePoService.updateSegmentStatus(taxId, status, param);
    }

    @PostMapping(value = "/share")
    public void audienceShare(@RequestBody AudiencesShareDTO audienceSharesDTO) throws AMSException {
        audiencePoService.audienceShare(audienceSharesDTO);
    }
}
