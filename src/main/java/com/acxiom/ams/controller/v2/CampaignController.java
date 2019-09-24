package com.acxiom.ams.controller.v2;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.exception.*;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.mapper.AudiencePoMapper;
import com.acxiom.ams.mapper.AudienceVoMapper;
import com.acxiom.ams.mapper.CampaignStatusVoMapper;
import com.acxiom.ams.model.dto.*;
import com.acxiom.ams.model.dto.v2.CampaignParam;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.em.SourceType;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.po.UniversePo;
import com.acxiom.ams.model.vo.*;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.service.AudiencePoService;
import com.acxiom.ams.service.BitmapService;
import com.acxiom.ams.service.UniverseService;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created by cldong on 12/14/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/v2/campaign")
public class CampaignController {

    @Autowired
    AudiencePoService audiencePoService;
    @Autowired
    BitmapService bitmapService;
    @Autowired
    AudiencePoJPA audiencePoJPA;
    @Autowired
    AudienceVoMapper audienceVoMapper;
    @Autowired
    CampaignStatusVoMapper campaignStatusVoMapper;
    @Autowired
    AudiencePoMapper audiencePoMapper;
    @Autowired
    UniverseService universeService;

    @GetMapping(value = "/destination/{workingTenantId}/{username}")
    public List<DestinationVo> getDestination(@PathVariable(value = "workingTenantId") Long tenantId,
                                              @PathVariable(value = "username") String username)
            throws AMSException {
        return audiencePoService.getDestination(tenantId, username);
    }

    @GetMapping(value = "/source/{universeId}")
    public List<SourceItem> getSourceList(@PathVariable(value = "universeId") Long universeId,
                                          @RequestParam(name = "workingTenantId") Long tenantId)
            throws AMSException {
        return audiencePoService.getSourceListV2(universeId, tenantId);
    }

    @GetMapping(value = "/tree/{treeTenantId}")
    public List<TreeItemVo> getTreeItem(@PathVariable(value = "treeTenantId") Long treeTenantId,
                                        @RequestParam(name = "sourceType") SourceType sourceType,
                                        @RequestParam(name = "universeIdList") List<Long> universeIdList,
                                        @RequestParam(name = "id") String id)
            throws AMSException {
        return audiencePoService.getAllTreeItem(treeTenantId, sourceType, universeIdList, id);
    }

    @GetMapping(value = "/tree/item/{tenantId}")
    public List<TaxonomyItemVo> getTaxonomyItem(@PathVariable(value = "tenantId") Long tenantId,
                                                @RequestParam(name = "universeIdList") List<Long> universeIdList,
                                                @RequestParam(name = "id") String id)
            throws AMSException {
        return audiencePoService.getTaxonomyTreeItem(tenantId, universeIdList, id);
    }

    /**
     * @param tenantId
     * @param limit
     * @return java.util.List<com.acxiom.ams.model.vo.TaxonomyItemVo>
     * @methodName getTaxonomyEndTypeItem
     * @author Owen.Que
     * @description get destinationId's Taxonomy which type is 'end' and 'limit' the data number of result
     * @date 9/13/2018 10:11
     */
    @GetMapping(value = "/tree/endItem/{tenantId}/{limit}")
    public List<TaxonomyItemVo> getTaxonomyEndTypeItem(@PathVariable(value = "tenantId") Long tenantId,
                                                       @PathVariable(value = "limit") Integer limit) throws AMSException {
        return audiencePoService.getTaxonomyTreeEndTypeItemDestinationIdWithinLimit(tenantId, limit);
    }

    @GetMapping(value = "/parent/path/{treeTenantId}")
    public Map<String, String> getParentPath(@PathVariable(value = "treeTenantId") Long treeTenantId,
                                             @RequestParam(name = "nodeId") String nodeId)
            throws AMSException {
        return audiencePoService.getParentPath(treeTenantId, nodeId);
    }

    @GetMapping(value = "/tree/search/{tenantId}")
    public Map<String, List<TreeItemVo>> searchTreeItem(
            @PathVariable(value = "tenantId") Long tenantId,
            @RequestParam(name = "universeIdList") List<Long> universeIdList,
            @RequestParam(name = "key") String key)
            throws AMSException {
        return audiencePoService.searchTreeItemV2(tenantId, universeIdList, key);
    }

    @PostMapping(value = "/{tenantId}")
    public AudiencePo createCampaign(@PathVariable(value = "tenantId") Long tenantId,
                                     @RequestBody @Valid CampaignParam campaignParam)
            throws AMSException {
        return audiencePoService.createCampaign(tenantId, campaignParam);
    }

    @PostMapping(value = "/calculate/{universeIdList}")
    public Map calculate(@PathVariable(value = "universeIdList") List<Long> universeIdList,
                         @RequestBody String rule)
            throws AMSException {
        return audiencePoService.calculateV2(universeIdList, rule);
    }

    @GetMapping(value = "/search/{tenantId}")
    public List<AudienceAndFolderVo> searchAudienceItem(
            @PathVariable(value = "tenantId") Long tenantId,
            @RequestParam(name = "key") String key,
            @RequestParam(name = "folderType")
            @NotNull(message = "{message.error.folderType}") FolderType folderType)
            throws AMSException {
        Page<AudiencePo> audiencePoList = audiencePoService.getAudienceItemByKey(tenantId, key, folderType, 1, 10000);
        List<UniversePo> universePoList = universeService.listUniverseByTenantId(tenantId, "");
        Map<Long, String> universeMap = new HashMap<>();
        universePoList.forEach(universePo -> universeMap.put(universePo.getId(), universePo.getUniverseName()));
        List<AudienceAndFolderVo> audienceAndFolderVoList = audiencePoMapper.map(audiencePoList.getContent());
        return audienceAndFolderVoList;
    }

    @PostMapping(value = "/refresh")
    public void refreshCampaign(@RequestBody @Valid FolderAndAudience folderAndAudience)
            throws AMSException {
        audiencePoService.refreshCampaign(folderAndAudience);
    }

    @DeleteMapping(value = "/delete")
    public void deleteCampaignAndFolder(@RequestBody @Valid FolderAndAudience folderAndAudience)
            throws AMSException {
        audiencePoService.deleteAudienceAndFolder(folderAndAudience);
    }

    @GetMapping(value = "/{campaignId}")
    public AudienceVo getCampaign(@PathVariable(value = "campaignId") Long campaignId)
            throws AMSException {
        return audienceVoMapper.map(audiencePoService.getSegment(campaignId));
    }

    @PutMapping(value = "/{tenantId}/{campaignId}")
    public void updateCampaign(@PathVariable(value = "tenantId") Long tenantId,
                               @PathVariable(value = "campaignId") Long campaignId,
                               @RequestBody @Valid CampaignParam campaignParam)
            throws AMSException {
        audiencePoService.updateCampaign(tenantId, campaignId, campaignParam);
    }

    @PostMapping(value = "/copy/{campaignId}")
    public void copyCampaign(@PathVariable(value = "campaignId") Long campaignId,
                             @RequestBody @Valid AudienceParam audienceParam)
            throws AMSException {
        audiencePoService.copyCampaign(campaignId, audienceParam);
    }

    @GetMapping(value = "/status")
    public List<CampaignStatusVo> getCampaignStatusByIds(@RequestParam("campaignIds") Long[] campaignIds) {
        return campaignStatusVoMapper.map(audiencePoService.getCampaignStatusByIds(campaignIds));
    }

    @RequestMapping(value = "/heartbeat", method = RequestMethod.GET)
    public String heartBeat() {
        return "heartbeat";
    }

    @PostMapping(value = "/export/{tenantId}")
    public ResponseEntity<Resource> exportCampaign(@PathVariable(value = "tenantId") Long tenantId,
                                                   @RequestBody @Valid FolderAndAudience folderAndAudience)
            throws AMSException {
        return audiencePoService.exportCampaign(folderAndAudience, tenantId);
    }

    @GetMapping(value = "/data/store")
    public List<DataStoreVo> getDataStoreNodeList() throws AMSInvalidInputException {
        return audiencePoService.getDataStoreNodeList();
    }

    @PostMapping(value = "/check/security")
    public Boolean checkSecurity(@RequestBody SecurityParam securityParam) {
        return audiencePoService.checkSecurity(securityParam);
    }

    /**
     * @Author: Fermi.Tang
     * @Description: back push attribute by node id
     * @Date: 5/8/2018 1:54 PM
     * @Param: [destinationId, nodeIds]
     * @Return: java.util.List<com.acxiom.ams.model.vo.TaxonomyItemVo>
     */
    @GetMapping(value = "/node/attribute/{tenantId}")
    public List<TaxonomyItemVo> getAttributeByNodeId(@PathVariable(value = "tenantId") Long tenantId,
                                                     @RequestParam(name = "nodeIds") @NotNull(message = "{message" +
                                                             ".error.nodeIds}") List<String> nodeIds)
            throws AMSException {
        return audiencePoService.getAttributeByNodeIdAndNameV2(nodeIds, tenantId);
    }

    /**
     * @Author: Fermi.Tang
     * @Description: get owner type
     * @Date: 5/8/2018 1:56 PM
     * @Param: [ownerTypeDTO, destinationId]
     * @Return: java.util.List<com.acxiom.ams.model.vo.OwnerTypeVo>
     */
    @PostMapping(value = "/owner/type/{destinationId}")
    public List<OwnerTypeVo> getAllOwnerType(@RequestBody OwnerTypeDTO ownerTypeDTO, @PathVariable("destinationId")
            Long destinationId)
            throws AMSException {
        return audiencePoService.getOwnerTypeV2(ownerTypeDTO.getOwnerList(), ownerTypeDTO.getNodeDTOList(),
                ownerTypeDTO.getSegmentDTOList(), destinationId);
    }

    /**
     * Get owner type and data type
     *
     * @param ownerTypeDTO
     * @param tenantId
     * @return
     * @throws AMSRMIException
     * @throws AMSInvalidInputException
     */
    @PostMapping(value = "/owner/typeAndDataType/{tenantId}")
    public List<OwnerAndDataType> getAllOwnerAndDataType(@RequestBody OwnerTypeDTO ownerTypeDTO, @PathVariable
            ("tenantId") Long tenantId)
            throws AMSException {
        return audiencePoService.getOwnerTypeV3(ownerTypeDTO.getOwnerList(), ownerTypeDTO.getNodeDTOList(),
                ownerTypeDTO.getSegmentDTOList(), tenantId);
    }

    /**
     * @Author: Fermi.Tang
     * @Description: get price list by taxonomy id list
     * @Date: 5/8/2018 1:56 PM
     * @Param: [tenantId, destinationId, taxonomyIdList]
     * @Return: java.util.List<com.acxiom.ams.model.vo.PriceAndOwnerVO>
     */
    @PostMapping(value = "/price/{tenantId}/{destinationId}")
    public List<PriceAndOwnerVO> listPriceAndOwnerByTaxonomyId(@PathVariable("tenantId") Long tenantId, @PathVariable
            ("destinationId") Long destinationId,
                                                               @RequestBody List<String> taxonomyIdList) throws
            AMSException {
        return audiencePoService.listPriceAndOwnerByTaxonomyId(tenantId, destinationId, taxonomyIdList);
    }

    /**
     * @Author: Fermi.Tang
     * @Description: get segment and attribute by Key
     * @Date: 5/8/2018 2:16 PM
     * @Param: [tenantId, key]
     * @Return: com.acxiom.ams.model.vo.SegmentAndAttributeVo
     */
    @GetMapping(value = "/list/attribute/search/{universeIdList}")
    public CampaignAndAttributeVO getSegmentAndAttributeByKey(@PathVariable(value = "universeIdList") List<Long> universeIdList,
                                                              @RequestParam @NotBlank(message = "{message.error.key}") String key,
                                                              @RequestParam @NotNull(message = "{message.error.tenantId}") Long tenantId)
            throws AMSException {
        return audiencePoService.getSegmentAndAttributeByKeyV2(universeIdList, key, tenantId);
    }

    @PostMapping(value = "/data/price/owner/{destinationId}")
    public List<DataTypeAndPriceAndOwnerVO> listDataTypeAndPriceAndOwnerByTaxonomyIdList(
            @PathVariable("destinationId") Long destinationId,
            @RequestBody List<String> taxonomyIdList)
            throws AMSException {
        return audiencePoService.listDataTypeAndPriceAndOwnerByTaxonomyIdList(destinationId, taxonomyIdList);
    }

    @GetMapping(value = "/metrics")
    public MetricsVO countDistributedAndBuiltAudiencesByMonth(@RequestParam(value = "yearMonth") String yearMonth)
            throws AMSException {
        return audiencePoService.countDistributedAndBuiltAudiencesByMonth(yearMonth, SegmentStatusType
                .CAMPAIGN_DISTRIBUTED, FolderType.CAMPAIGN);
    }

    @GetMapping(value = "/track/data/store/node")
    public List<DataStoreNodeSortByTenantVO> listDataStoreNodesByDate(@RequestParam(value = "startDate")
                                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                                      @RequestParam(value = "endDate")
                                                                      @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate)
            throws AMSException {
        return audiencePoService.listDataStoreNodesByDate(startDate, endDate);
    }
}
