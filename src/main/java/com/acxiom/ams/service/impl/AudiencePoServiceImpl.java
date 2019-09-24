package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.api.ServiceAPI.AiAPI;
import com.acxiom.ams.api.ServiceAPI.BitmapAPI;
import com.acxiom.ams.api.ServiceAPI.TaxonomyAPI;
import com.acxiom.ams.api.ServiceAPI.UserCenterAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.*;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.mapper.AudiencePoMapper;
import com.acxiom.ams.mapper.AudienceVoMapper;
import com.acxiom.ams.mapper.CampaignStatusVoMapper;
import com.acxiom.ams.mapper.TaxonomyMapper;
import com.acxiom.ams.model.dto.*;
import com.acxiom.ams.model.dto.v2.AudienceCountByUniverseDTO;
import com.acxiom.ams.model.dto.v2.CampaignParam;
import com.acxiom.ams.model.em.*;
import com.acxiom.ams.model.po.*;
import com.acxiom.ams.model.rmi.Taxonomy;
import com.acxiom.ams.model.vo.*;
import com.acxiom.ams.repository.*;
import com.acxiom.ams.service.*;
import com.acxiom.ams.util.Constant;
import com.acxiom.ams.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.acxiom.ams.util.Constant.BLANK_STR;
import static com.acxiom.ams.util.Constant.LINE_HEIGHT;
import static com.acxiom.ams.util.StringUtil.*;

/**
 * Created by cldong on 12/5/2017.
 */
@Service
public class AudiencePoServiceImpl implements AudiencePoService {

    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Autowired
    VersionPoJPA versionPoJPA;
    @Autowired
    TenantPoJPA tenantPoJPA;
    @Autowired
    FolderPoJPA folderPoJPA;
    @Autowired
    AudiencePoJPA audiencePoJPA;
    @Autowired
    TenantAndChannelPoJPA tenantAndChannelPoJPA;
    @Autowired
    BitmapAPI bitmapAPI;
    @Autowired
    AudienceDistributeJobPoJPA audienceDistributeJobPoJPA;
    @Autowired
    TaxonomyAPI taxonomyAPI;
    @Autowired
    AiAPI aiAPI;
    @Autowired
    UserCenterAPI userCenterAPI;
    @Autowired
    FolderService folderService;
    @Autowired
    BitmapService bitmapService;
    @Autowired
    TaxonomyMapper taxonomyMapper;
    @Autowired
    AudienceVoMapper audienceVoMapper;
    @Autowired
    AudiencePoMapper audiencePoMapper;
    @Autowired
    CampaignStatusVoMapper campaignStatusVoMapper;
    @Autowired
    DistributionService distributionService;
    @Autowired
    ServiceAPI.DataSourceAPI dataSourceAPI;
    @Autowired
    TenantService tenantService;
    @Autowired
    VersionPoService versionService;
    @Autowired
    ChannelService channelService;
    @Autowired
    UniversePoJPA universePoJPA;
    @Autowired
    UniverseService universeService;
    @Autowired
    UniverseActivityLogPoJPA universeActivityLogPoJPA;
    @Autowired
    TenantAndUniversePoJPA tenantAndUniversePoJPA;

    @Value("${temp.file.path}")
    private String tempFile;
    @Value("${export.template.file}")
    private String exportTemplateFile;

    private static final String SEGMENTS = "segments";
    private static final String COUNT = "count";
    private static final String FROZEN_COUNT = "frozenCount";
    private static final String FROZEN_NATIVE_COUNT = "frozenNativeCount";
    private static final String CAMPAIGN_ID = "campaignId";
    private static final String USER_ID = "userID";
    private static final String CLIENT_ID = "clientID";
    private static final String TENANT_PATH = "tenantPath";
    private static final String DELETE = "delete";
    private static final String DESTINATION_ID = "destinationID";
    private static final String UNIVERSE_IDS = "universeIDs";
    private static final String TENANT_ID = "tenantID";
    private static final String UNIVERSE_SYS_NAME = "universeSysName";
    private static final String UNIVERSE_SYS_NAMES = "universeSysNames";
    private static final String RULE = "rule";
    private static final String TEMP_CAMPAIGN_ID = "tempCampaignId";
    private static final String INCLUDE = "include";
    private static final String EXCLUDE = "exclude";
    private static final String ITEMS = "items";
    private static final String OWNER = "owner";
    private static final String VALUES = "values";
    private static final String INFO_BASE_FLAG = "infobaseFlag";
    private static final String SHARED_TAXONOMY = "Shared Taxonomy";
    private static final String SHARED_AUDIENCE = "Shared Audience";

    @Override
    public List<SourceItem> getSourceList(Long tenantId)
            throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionService.getActiveVersionByTenant(tenantPo);
        List<SourceItem> sourceItemList = new ArrayList<>();
        if (Optional.ofNullable(versionPo.getTreeId()).isPresent() && !versionPo.getTreeId()
                .isEmpty()) {
            List<Taxonomy> taxonomyList = taxonomyAPI
                    .getTaxonomyList(String.valueOf(versionPo.getId()), versionPo.getTreeId());
            for (Taxonomy taxonomy : taxonomyList) {
                SourceItem sourceItem = new SourceItem();
                sourceItem.setName(taxonomy.getName());
                sourceItem.setId(taxonomy.getObjectId());
                sourceItem.setSourceType(SourceType.TAXONOMY);
                sourceItemList.add(sourceItem);
            }
        }
        // get shared taxonomy folder
        try {
            List<Taxonomy> taxonomyList =
                    dataSourceAPI.listChildSharedTaxonomyByObjectId(tenantPo.getTenantId(), "");
            if (!taxonomyList.isEmpty()) {
                SourceItem sourceItem = new SourceItem();
                sourceItem.setName(SHARED_TAXONOMY);
                sourceItem.setSourceType(SourceType.SHARED_TAXONOMY);
                sourceItem.setId(SHARED_TAXONOMY);
                sourceItemList.add(sourceItem);
            }
        } catch (AMSException e) {
            LogUtils.error(e);
        }
        try {
            List<Taxonomy> taxonomyList = dataSourceAPI.getSharedAudienceList(tenantPo.getTenantId(), "");
            if (!taxonomyList.isEmpty()) {
                SourceItem sourceItem = new SourceItem();
                sourceItem.setName(SHARED_AUDIENCE);
                sourceItem.setSourceType(SourceType.SHARED_AUDIENCE);
                sourceItem.setId(SHARED_AUDIENCE);
                sourceItemList.add(sourceItem);
            }
        } catch (AMSException e) {
            LogUtils.error(e);
        }
        List<FolderPo> folderPoList = folderService.getParentFolder();
        for (FolderPo folderPo : folderPoList) {
            SourceItem sourceItem = new SourceItem();
            sourceItem.setName(folderPo.getFolderName());
            sourceItem.setId(String.valueOf(folderPo.getId()));
            sourceItem.setSourceType(parseFolderType(folderPo.getFolderType()));
            sourceItemList.add(sourceItem);
        }
        return sourceItemList;
    }

    @Override
    public List<TreeItemVo> getTreeItem(Long tenantId, SourceType sourceType, String id)
            throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionService.getActiveVersionByTenant(tenantPo);
        List<TreeItemVo> treeItemVoList = new ArrayList<>();
        switch (sourceType) {
            case TAXONOMY:
                isObjectId(id);
                List<Taxonomy> taxonomyList = taxonomyAPI
                        .getTaxonomyList(String.valueOf(versionPo.getId()), id);
                treeItemVoList.addAll(parseTaxonomyToTreeItemVoV2(taxonomyList, false, Collections.emptyList(),
                        tenantPo));
                break;
            case SHARED_TAXONOMY:
                if (!StringUtils.equals(id, SHARED_TAXONOMY)) {
                    isObjectId(id);
                } else {
                    id = "";
                }
                List<Taxonomy> sharedTaxonomyList = dataSourceAPI.listChildSharedTaxonomyByObjectId(tenantPo.getTenantId(), id);
                treeItemVoList.addAll(parseTaxonomyToTreeItemVoV2(sharedTaxonomyList, true, Collections.emptyList(),
                        tenantPo));
                break;
            case SHARED_AUDIENCE:
                if (!StringUtils.equals(id, SHARED_AUDIENCE)) {
                    isObjectId(id);
                } else {
                    id = "";
                }
                List<Taxonomy> sharedAudienceList = dataSourceAPI.getSharedAudienceList(tenantPo.getTenantId(), id);
                treeItemVoList.addAll(parseTaxonomyToTreeItemVoV2(sharedAudienceList, true, Collections.emptyList(),
                        tenantPo));
                treeItemVoList.forEach(treeItemVo -> {
                    if (treeItemVo.getAudienceCount() != null) {
                        treeItemVo.setValue(Long.valueOf(treeItemVo.getAudienceCount()));
                    }
                    treeItemVo.setSourceType(SourceType.SHARED_AUDIENCE);
                });
                break;
            case SEGMENT:
                Long parentFolderId;
                try {
                    parentFolderId = Long.parseLong(id);
                } catch (Exception e) {
                    LogUtils.error(e);
                    throw new AMSInvalidInputException(Constant.ERROR_CODE_0204,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0204));
                }
                List<AudienceAndFolderVo> audienceAndFolderVoList = folderService
                        .getFolderListByTenantId(parentFolderId, tenantId, true,
                                FolderType.SAVED_SEGMENT);
                audienceAndFolderVoList = audienceAndFolderVoList.stream().filter(audienceAndFolderVo ->
                        (!SegmentStatusType.SEGMENT_RUNNING.equals(audienceAndFolderVo.getSegmentStatusType())
                                && !SegmentStatusType.SEGMENT_FAILED.equals(audienceAndFolderVo.getSegmentStatusType()))
                ).collect(Collectors.toList());
                treeItemVoList.addAll(parseAudienceAndFolderVoToTreeItemVo(audienceAndFolderVoList));
                break;
            case LOOKALIKE:
                try {
                    parentFolderId = Long.parseLong(id);
                } catch (Exception e) {
                    LogUtils.error(e);
                    throw new AMSInvalidInputException(Constant.ERROR_CODE_0204,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0204));
                }
                audienceAndFolderVoList = folderService
                        .getFolderListByTenantId(parentFolderId, tenantId, true,
                                FolderType.LOOKALIKE_GROUP);
                treeItemVoList
                        .addAll(parseAudienceAndFolderVoToTreeItemVo(audienceAndFolderVoList));
                break;
            default:
                break;
        }
        return treeItemVoList;
    }

    @Override
    public Map<Long, SegmentStatusType> getAudienceStatus(AudienceStatus audienceStatus) {
        List<AudiencePo> audiencePoList = audiencePoJPA
                .findAudiencePoByIdInAndAudienceType(audienceStatus.getList(),
                        audienceStatus.getFolderType());
        Map<Long, SegmentStatusType> map = new HashMap<>();
        audiencePoList.forEach(audiencePo -> map.put(audiencePo.getId(), audiencePo.getSegmentStatusType()));
        return map;
    }

    @Override
    public Page<AudiencePo> getAudienceItemByKey(Long tenantId, String key, FolderType folderType, Integer pageNumber
            , Integer pageSize)
            throws AMSInvalidInputException {
        tenantService.getTenantById(tenantId);
        Sort sort = new Sort(Sort.Direction.DESC, "updateTime");
        Pageable pageable = new PageRequest(pageNumber, pageSize, sort);
        Page<AudiencePo> audiencePoPage = audiencePoJPA
                .findByNameLikeAndAudienceTypeInAndTenantId(parseRegexLike(key),
                        new FolderType[]{folderType}, tenantId, pageable);
        audiencePoPage.getContent().forEach(audiencePo -> audiencePo.setFolderPo(null));
        return audiencePoPage;
    }

    @Override
    public List<TreeItemVo> searchTreeItem(Long tenantId, String key)
            throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionService.getActiveVersionByTenant(tenantPo);
        List<TreeItemVo> treeItemVoList = new ArrayList<>();
        String[] keys = new String[]{key};
        if (Constant.AI_USE) {
            keys = aiAPI.query(key);
            if (keys.length == 0) {
                keys = new String[]{key};
            }
        }
        List<Taxonomy> taxonomyList = taxonomyAPI
                .getTaxonomyList(String.valueOf(versionPo.getId()), keys);
        treeItemVoList.addAll(parseTaxonomyToTreeItemVoV2(taxonomyList, false, Collections.emptyList(), tenantPo));
        // get shared taxonomy
        List<Taxonomy> sharedTaxonomyList = dataSourceAPI.searchSharedTaxonomyByKeys(tenantPo.getTenantId(), keys);
        treeItemVoList.addAll(parseTaxonomyToTreeItemVoV2(sharedTaxonomyList, true, Collections.emptyList(), tenantPo));
        List<AudiencePo> audiencePoList = audiencePoJPA
                .findByNameLikeAndAudienceTypeInAndTenantIdOrderByUpdateTime(parseRegexLike(key),
                        new FolderType[]{FolderType.SAVED_SEGMENT, FolderType.LOOKALIKE_GROUP}, tenantId);
        audiencePoList = audiencePoList.stream().filter(audiencePo ->
                (!SegmentStatusType.SEGMENT_RUNNING.equals(audiencePo.getSegmentStatusType())
                        && !SegmentStatusType.SEGMENT_FAILED.equals(audiencePo.getSegmentStatusType()))
        ).collect(Collectors.toList());
        treeItemVoList.addAll(parseAudienceToTreeItemVo(audiencePoList));
        return treeItemVoList;
    }

    @Override
    public Map<String, String> getParentPath(Long treeTenantId, String nodeId)
            throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(treeTenantId);
        VersionPo versionPo = versionService.getActiveVersionByTenant(tenantPo);
        Map<String, String> map = taxonomyAPI.getParentPath(String.valueOf(versionPo.getId()), new String[]{nodeId});
        if (map.isEmpty()) {
            map = dataSourceAPI.getParentPathByNodeId(tenantPo.getTenantId(), nodeId);
        }
        return map;
    }

    @Override
    public Long calculate(Long tenantId, String userId, String rule)
            throws AMSException {
        return bitmapService.calculate(tenantId, userId, rule);
    }

    @Override
    public void refreshSegment(FolderAndAudience folderAndAudience, Long tenantId)
            throws AMSException {
        String owner = folderAndAudience.getOwner();
        List<Long> folderIdList = folderAndAudience.getFolderIdList();
        String folderIdStr = StringUtils.join(folderIdList, ",");
        //folder and audience permission to update
        List<AudiencePo> audiencePoList = audiencePoJPA.getSegmentListByFolderId(folderIdStr);
        Long size = Long.valueOf(audiencePoList.size());
        Long filterSize = audiencePoList.stream().filter(
                audienceAndFolderVo -> StringUtils.equals(audienceAndFolderVo.getCreatedBy(), owner))
                .count();
        Long filterStatusSize = audiencePoList.stream().filter(
                audienceAndFolderVo -> audienceAndFolderVo.getSegmentStatusType()
                        .equals(SegmentStatusType.SEGMENT_NEW))
                .count();
        if (size != filterSize) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0206,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0206));
        }
        if (size != filterStatusSize) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0215,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0215));
        }
        //folder and audience permission to update
        List<Long> audienceIdList = folderAndAudience.getAudienceIdList();
        List<AudiencePo> audiencePoSingleList = audiencePoJPA.findAudiencePoByIdIn(audienceIdList);
        size = Long.valueOf(audiencePoSingleList.size());
        filterSize = audiencePoSingleList.stream()
                .filter(audiencePo -> StringUtils.equals(audiencePo.getCreatedBy(), owner)).count();
        filterStatusSize = audiencePoSingleList.stream()
                .filter(audiencePo -> audiencePo.getSegmentStatusType().equals(SegmentStatusType.SEGMENT_NEW)
                        || SegmentStatusType.SEGMENT_FAILED.equals(audiencePo.getSegmentStatusType()))
                .count();
        if (size != filterSize) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0206,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0206));
        }
        if (size != filterStatusSize) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0215,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0215));
        }
        // update count
        Set<AudiencePo> audiencePoSet = new HashSet<>();
        audiencePoSet.addAll(audiencePoList);
        audiencePoSet.addAll(audiencePoSingleList);
        // List<AudiencePo> newAudiencePoList = new ArrayList<>();
        for (AudiencePo audiencePo : audiencePoSet) {
            Long count = bitmapService
                    .calculateForNonTv(audiencePo.getTenantId(), "", audiencePo.getRuleJson());
            JSONObject ruleObj = JSONObject.parseObject(audiencePo.getRuleJson());
            JSONArray segments = ruleObj.getJSONArray(SEGMENTS);
            for (int i = 0; i < segments.size(); i++) {
                JSONObject sObj = segments.getJSONObject(i);
                sObj.put(COUNT, count);
                sObj.put(FROZEN_COUNT, count);
            }
            ruleObj.put(SEGMENTS, segments);
            audiencePo.setRuleJson(ruleObj.toJSONString());
            audiencePo.setCount(count);
            audiencePo.setFrozenCount(count);
            audiencePo.setUpdateTime(new Date());
            audiencePo.setTaxonomyId(audiencePo.getTaxonomyId());
            audiencePoJPA.save(audiencePo);
            boolean flag = true;
            if (SegmentStatusType.SEGMENT_DISTRIBUTING.equals(audiencePo.getSegmentStatusType())) {
                flag = false;
            }
            CreateNonTVBitmapVO createNonTVBitmapVO = bitmapService.createBitmapForNonTV(tenantId,
                    audiencePo.getTaxonomyId(), "", audiencePo.getRuleJson(), flag);
            if (ruleObj.getDouble("test-control") > 0 && !createNonTVBitmapVO.isFinishFlag()) {
                audiencePo.setTestCount(0L);
                audiencePo.setControlCount(0L);
                audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_RUNNING);
                audiencePoJPA.save(audiencePo);
            }
        }
    }

    @Override
    public void deleteAudienceAndFolder(FolderAndAudience folderAndAudience)
            throws AMSInvalidInputException {
        folderService.deleteFolderAndAudience(folderAndAudience);
    }

    @Override
    public AudiencePo getSegment(Long audienceId) throws AMSInvalidInputException {
        return Optional.ofNullable(audiencePoJPA.findOne(audienceId))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0221,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0221)));
    }

    @Override
    public List<TreeItemVo> getSegmentOption(Long audienceId)
            throws AMSException {
        AudiencePo audiencePo = Optional.ofNullable(audiencePoJPA.findOne(audienceId))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0221,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0221)));
        String rule = audiencePo.getRuleJson();
        Map<String, String> map;
        map = getNodeMap(rule);
        Long tenantId = audiencePo.getTenantId();
        TenantPo tenantPo = tenantPoJPA.findTenantPoById(tenantId);
        VersionPo versionPo = versionPoJPA
                .findFirstByTenantPoAndOperationFlag(tenantPo, TemplateStatusType.ACTIVE);
        Set<String> keySet = map.keySet();
        String[] objectIdArray = keySet.toArray(new String[keySet.size()]);
        List<Taxonomy> taxonomyList = taxonomyAPI
                .getTaxonomyListByChildrenId(String.valueOf(versionPo.getId()), objectIdArray);
        return parseTaxonomyToTreeItemVo(taxonomyList, Collections.emptyList(), tenantPo);
    }

    @Override
    public List<TaxonomyItemVo> getTaxonomyTreeItemByTenant(Long tenantId, String id)
            throws AMSException {
        TenantPo treeTenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionService.getActiveVersionByTenant(treeTenantPo);
        isObjectId(id);
        List<Taxonomy> taxonomyList = taxonomyAPI.getTaxonomyListByNode(String.valueOf(versionPo.getId()), id);
        // if not exist in taxonomy tree, query in the shared taxonomy
        if (taxonomyList.isEmpty()) {
            taxonomyList = dataSourceAPI.listChildSharedTaxonomyByObjectId(treeTenantPo.getTenantId(), id);
        }
        List<String> taxonomyIdList = new ArrayList<>();
        // lamda get perId
        taxonomyList.forEach(t -> {
            if (t.getId() != null) {
                taxonomyIdList.add(t.getId());
            }
        });
        JSONObject reqParams = new JSONObject();
        reqParams.put("tenantPath", treeTenantPo.getPath());
        reqParams.put("taxonomyIdList", taxonomyIdList);
        Map<String, Long> map = bitmapAPI.listNodeCountByTaxonomyIds(treeTenantPo, taxonomyIdList);
        List<TaxonomyItemVo> taxonomyItemVoList = taxonomyMapper.map(taxonomyList);
        taxonomyItemVoList.forEach(t -> {
            Long count = Optional.ofNullable(map.get(t.getTaxonomyId()))
                    .orElseGet(() -> 0L);
            t.setAudienceCount(count.toString());
        });
        taxonomyItemVoList.sort((t1, t2) -> t1.getTaxonomyId().compareTo(t2.getTaxonomyId()));
        return taxonomyItemVoList;
    }

    @Override
    public void updateSegment(Long tenantId, Long audienceId, TemporarySegment temporarySegment)
            throws AMSException {
        AudiencePo audiencePo = getAudiencePoByIdAndTenantId(audienceId, tenantId);
        FolderPo folderPo = folderService.getFolderById(temporarySegment.getFolderId());
        Long targetId = temporarySegment.getFolderId();
        if (!StringUtils.equals(audiencePo.getCreatedBy(), temporarySegment.getCreatedBy())) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0206,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0206));
        }
        if (!(audiencePo.getSegmentStatusType().equals(SegmentStatusType.SEGMENT_NEW)
                || audiencePo.getSegmentStatusType().equals(SegmentStatusType.SEGMENT_FAILED))) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0217,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0217));
        }
        audiencePo.setTaxonomyId(audiencePo.getTaxonomyId());
        audiencePo.setCount(temporarySegment.getCount());
        audiencePo.setFrozenCount(temporarySegment.getCount());
        audiencePo.setName(temporarySegment.getName());
        audiencePo.setRuleJson(temporarySegment.getRule());
        audiencePo.setAudienceType(temporarySegment.getAudienceType());
        if (Optional.ofNullable(temporarySegment.getCost()).isPresent()) {
            audiencePo.setCost(temporarySegment.getCost());
        }
        if (Optional.ofNullable(temporarySegment.getSegmentCode()).isPresent()) {
            audiencePo.setCode(temporarySegment.getSegmentCode());
        }
        audiencePo.setFolderPo(folderPo);
        if (Optional.ofNullable(temporarySegment.getDescription()).isPresent()) {
            audiencePo.setDescription(temporarySegment.getDescription());
        }
        Optional<AudiencePo> audiencePoOptional = Optional.ofNullable(audiencePoJPA
                .findAudiencePoByAudienceTypeAndTenantIdAndName(temporarySegment.getAudienceType(),
                        tenantId, temporarySegment.getName()));
        if (audiencePoOptional.isPresent() && audiencePoOptional.get().getId() == audiencePo.getId()
                && !StringUtils.equals(audiencePoOptional.get().getName(), audiencePo.getName())) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0213,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0213));
        }
        boolean testControlFlag = false;
        if (SegmentStatusType.SEGMENT_FAILED.equals(audiencePo.getSegmentStatusType())) {
            testControlFlag = true;
        }
        audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_NEW);
        JSONObject ruleObj = JSONObject.parseObject(audiencePo.getRuleJson());
        audiencePoJPA.save(audiencePo);
        CreateNonTVBitmapVO createNonTVBitmapVO = bitmapService.createBitmapForNonTV(tenantId,
                audiencePo.getTaxonomyId(), temporarySegment.getUserId(), temporarySegment.getRule(), true);
        if (ruleObj.getDouble("test-control") > 0 && (!createNonTVBitmapVO.isFinishFlag() || testControlFlag)) {
            audiencePo.setTestCount(0L);
            audiencePo.setControlCount(0L);
            audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_RUNNING);
            audiencePoJPA.save(audiencePo);
        }
        // modify folder update time
        String targetIdPaths = folderPoJPA.getParentList(String.valueOf(targetId));
        List<Long> targetIdList = Arrays.asList(targetIdPaths.substring(2).split(",")).stream()
                .map(s1 -> Long.parseLong(s1.trim())).collect(Collectors.toList());
        List<FolderPo> targetFolderPoList = folderPoJPA
                .getFolderPoByIdIn(targetIdList);
        targetFolderPoList.forEach(folderPo2 -> folderPo2.setUpdateTime(new Date()));
        folderPoJPA.save(targetFolderPoList);
        Boolean refreshFlag = createNonTVBitmapVO.isRefreshFlag();
        List<Long> audienceIdList = new ArrayList<>();
        audienceIdList.add(audiencePo.getId());
        TenantPo tenantPo = tenantPoJPA.findTenantPoById(audiencePo.getTenantId());
        refreshShareAudience(audienceIdList, tenantPo.getTenantId(), refreshFlag);
    }

    private void refreshShareAudience(List<Long> audienceIdList, String tenantId, boolean refreshFlag) {
        AudiencesShareDTO audiencesShareDTO = new AudiencesShareDTO();
        List<AudiencePo> audiencePoList = audiencePoJPA.findAll(audienceIdList);
        List<AudienceShareDTO> audienceShareDTOList = new ArrayList<>();
        for (AudiencePo audience : audiencePoList) {
            AudienceShareDTO audienceShareDTO = new AudienceShareDTO();
            audienceShareDTO.setAudienceId(audience.getId());
            audienceShareDTO.setName(audience.getName());
            audienceShareDTO.setTaxonomyId(audience.getTaxonomyId());
            audienceShareDTOList.add(audienceShareDTO);
        }
        audiencesShareDTO.setShareOwnerTenantId(tenantId);
        audiencesShareDTO.setShareAudienceDTOList(audienceShareDTOList);
        audiencesShareDTO.setPushFileFlag(refreshFlag);
        try {
            dataSourceAPI.refreshSharedDataSource(audiencesShareDTO);
        } catch (AMSRMIException e) {
            LogUtils.error(e);
        }
    }

    @Override
    public AudiencePo saveSegment(Long tenantId, String taxonomyId, TemporarySegment temporarySegment)
            throws AMSException {
        Long folderId = temporarySegment.getFolderId();
        FolderPo folderPo = folderService.getFolderById(folderId);
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        Optional<AudiencePo> audiencePoOptional = Optional
                .ofNullable(audiencePoJPA
                        .findAudiencePoByAudienceTypeAndTenantIdAndName(temporarySegment.getAudienceType(),
                                tenantId, temporarySegment.getName()));
        if (audiencePoOptional.isPresent()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0213,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0213));
        }
        AudiencePo audiencePo = new AudiencePo();
        audiencePo.setTaxonomyId(taxonomyId);
        long limit = tenantPo.getCountLimit();
        if (temporarySegment.getCount().longValue() < limit) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0216,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0216));
        }
        audiencePo.setCount(temporarySegment.getCount());
        audiencePo.setFrozenCount(temporarySegment.getCount());
        audiencePo.setFolderPo(folderPo);
        audiencePo.setTenantId(tenantId);
        audiencePo.setName(temporarySegment.getName());
        audiencePo.setAudienceType(temporarySegment.getAudienceType());
        audiencePo.setSegmentStatusType(temporarySegment.getAudienceType().equals(FolderType.SAVED_SEGMENT)
                ? SegmentStatusType.SEGMENT_NEW : SegmentStatusType.LOOKALIKE_READY);
        audiencePo.setCreatedBy(temporarySegment.getCreatedBy());
        if (Optional.ofNullable(temporarySegment.getCost()).isPresent()) {
            audiencePo.setCost(temporarySegment.getCost());
        } else {
            audiencePo.setCost("0");
        }
        if (!Optional.ofNullable(temporarySegment.getSegmentCode()).isPresent()) {
            audiencePo.setCode(String.valueOf(System.currentTimeMillis()));
        } else {
            audiencePo.setCode(temporarySegment.getSegmentCode());
        }
        audiencePo.setRuleJson(temporarySegment.getRule());
        audiencePo.setDescription(temporarySegment.getDescription());
        JSONObject ruleObj = JSONObject.parseObject(audiencePo.getRuleJson());
        if (ruleObj.getDouble("test-control") > 0) {
            audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_RUNNING);
        }
        audiencePoJPA.save(audiencePo);
        try {
            bitmapService.createBitmapForNonTV(tenantId, taxonomyId, temporarySegment.getUserId(),
                    temporarySegment.getRule(), true);
        } catch (Exception e) {
            audiencePo.setDeleted(true);
            audiencePoJPA.save(audiencePo);
        }
        return audiencePo;
    }

    private void isObjectId(String str) throws AMSInvalidInputException {
        try {
            new ObjectId(str);
        } catch (Exception e) {
            throw new AMSInvalidInputException();
        }
    }

    private List<TreeItemVo> parseTaxonomyToTreeItemVoV2(List<Taxonomy> taxonomyList, Boolean isShared,
                                                         List<Long> universeIdList,
                                                         TenantPo tenantPo, TenantPo... dataTenantPo)
            throws AMSException {
        Boolean endNodeFlag = false;
        List<TreeItemVo> treeItemVoList = new ArrayList<>();
        List<String> universeSysNameList = new ArrayList<>();
        if (Optional.ofNullable(universeIdList).isPresent() && !universeIdList.isEmpty()) {
            List<UniversePo> universePoList = universePoJPA.findAll(universeIdList);
            universePoList.forEach(universePo -> universeSysNameList.add(universePo.getUniverseSystemName()));
        }
        for (Taxonomy taxonomy : taxonomyList) {
            if (Constant.TAXONOMY_END_NODE_TYPE.equals(taxonomy.getType())) {
                endNodeFlag = true;
                break;
            }
            TreeItemVo treeItemVo = new TreeItemVo();
            treeItemVo.setId(taxonomy.getObjectId());
            treeItemVo.setName(taxonomy.getName());
            if (isShared) {
                treeItemVo.setSourceType(SourceType.SHARED_TAXONOMY);
            } else {
                treeItemVo.setSourceType(SourceType.TAXONOMY);
            }
            treeItemVo.setTenantId(tenantPo.getId());
            treeItemVo.setPrice(taxonomy.getPrice());
            treeItemVo.setOwner(taxonomy.getOwner());
            treeItemVo.setTaxonomyId(taxonomy.getId());
            treeItemVo.setDataType(taxonomy.getDataType());
            if (Constant.ATTRIBUTE_NODE_TYPE.equals(taxonomy.getType())) {
                treeItemVo.setDescription(taxonomy.getDescription());
                treeItemVo.setType(TreeItemType.CHECKBOX);
            }
            treeItemVoList.add(treeItemVo);
        }
        if (endNodeFlag) {
            List<String> taxonomyIdList = new ArrayList<>();
            taxonomyList.forEach(t -> taxonomyIdList.add(t.getId()));
            Map<String, Long> resultMap;
            if (dataTenantPo.length == 0) {
                resultMap = bitmapService.listNodeCountByTaxonomyIds(tenantPo, taxonomyIdList, universeSysNameList);
            } else {
                resultMap = bitmapService.listNodeCountByTaxonomyIds(dataTenantPo[0], taxonomyIdList,
                        universeSysNameList);
            }
            for (Taxonomy taxonomy : taxonomyList) {
                TreeItemVo treeItemVo = new TreeItemVo();
                treeItemVo.setObjectId(taxonomy.getObjectId());
                treeItemVo.setName(taxonomy.getName());
                treeItemVo.setType(TreeItemType.END);
                treeItemVo.setPrice(taxonomy.getPrice());
                treeItemVo.setOwner(taxonomy.getOwner());
                treeItemVo.setTaxonomyId(taxonomy.getId());
                treeItemVo.setAudienceCount(Optional.ofNullable(resultMap.get(taxonomy.getTaxonomyId())).isPresent() ?
                        String.valueOf(resultMap.get(taxonomy.getTaxonomyId())) : "0");
                treeItemVoList.add(treeItemVo);
            }
            treeItemVoList.sort((t1, t2) -> t1.getTaxonomyId().compareTo(t2.getTaxonomyId()));
        }

        return treeItemVoList;
    }


    //dataTenantPo(this value is to get audienceCount according to tv)
    private List<TreeItemVo> parseTaxonomyToTreeItemVo(List<Taxonomy> taxonomyList, List<Long> universeIdList,
                                                       TenantPo tenantPo, TenantPo... dataTenantPo)
            throws AMSException {
        List<TreeItemVo> treeItemVoList = new ArrayList<>();
        List<String> universeSysNameList = new ArrayList<>();
        if (Optional.ofNullable(universeIdList).isPresent() && !universeIdList.isEmpty()) {
            List<UniversePo> universePoList = universePoJPA.findAll(universeIdList);
            universePoList.forEach(universePo -> universeSysNameList.add(universePo.getUniverseSystemName()));
        }
        for (Taxonomy taxonomy : taxonomyList) {
            TreeItemVo treeItemVo = new TreeItemVo();
            treeItemVo.setId(taxonomy.getObjectId());
            treeItemVo.setName(taxonomy.getName());
            treeItemVo.setSourceType(SourceType.TAXONOMY);
            treeItemVo.setTenantId(tenantPo.getId());
            treeItemVo.setPrice(taxonomy.getPrice());
            treeItemVo.setOwner(taxonomy.getOwner());
            treeItemVo.setTaxonomyId(taxonomy.getId());
            treeItemVo.setDataType(taxonomy.getDataType());
            List<Taxonomy> taxonomys = taxonomy.getTaxonomyIncludes();
            if (taxonomys != null && !taxonomys.isEmpty() && StringUtils
                    .equals(taxonomys.get(0).getType(), Constant.TAXONOMY_END_NODE_TYPE)) {
                List<String> taxonomyIdList = new ArrayList<>();
                taxonomys.forEach(t -> {
                    if (t.getId() != null && !StringUtils.equals(t.getChecked(), Constant.NONE)) {
                        taxonomyIdList.add(t.getId());
                    }
                });
                taxonomys = taxonomys.stream().filter(taxonomy1 -> {
                    return taxonomy1.getId() != null && !StringUtils.equals(taxonomy1.getChecked(), Constant.NONE);
                }).collect(Collectors.toList());
                Map<String, Long> resultMap;
                if (dataTenantPo.length == 0) {
                    resultMap = bitmapService.listNodeCountByTaxonomyIds(tenantPo, taxonomyIdList, universeSysNameList);
                } else {
                    resultMap = bitmapService.listNodeCountByTaxonomyIds(dataTenantPo[0], taxonomyIdList,
                            universeSysNameList);
                }
                List<TaxonomyItemVo> taxonomyItemVoList = taxonomyMapper.map(taxonomys);
                taxonomyItemVoList.forEach(t -> {
                    Long count = Optional.ofNullable(resultMap.get(t.getTaxonomyId()))
                            .orElseGet(() -> 0L);
                    t.setAudienceCount(count.toString());
                });
                taxonomyItemVoList.sort((t1, t2) -> t1.getTaxonomyId().compareTo(t2.getTaxonomyId()));
                treeItemVo.setDescription(taxonomy.getDescription());
                treeItemVo.setType(TreeItemType.CHECKBOX);
                treeItemVo.setTaxonomyItemVoList(taxonomyItemVoList);
            }
            treeItemVoList.add(treeItemVo);
        }
        return treeItemVoList;
    }

    private List<TreeItemVo> parseAudienceAndFolderVoToTreeItemVo(
            List<AudienceAndFolderVo> audienceAndFolderVoList) {
        List<TreeItemVo> treeItemVoList = new ArrayList<>();
        for (AudienceAndFolderVo audienceAndFolderVo : audienceAndFolderVoList) {
            TreeItemVo treeItemVo = new TreeItemVo();
            treeItemVo.setName(audienceAndFolderVo.getName());
            treeItemVo.setValue(audienceAndFolderVo.getCount());
            treeItemVo.setTaxonomyId(audienceAndFolderVo.getTaxonomyId());
            treeItemVo.setId(String.valueOf(audienceAndFolderVo.getId()));
            treeItemVo.setType(
                    audienceAndFolderVo.getType() == null ? TreeItemType.FILE : TreeItemType.FOLDER);
            if (audienceAndFolderVo.getType() == null) {
                treeItemVo.setSourceType(parseFolderType(audienceAndFolderVo.getAudienceType()));
            } else {
                treeItemVo.setSourceType(parseFolderType(audienceAndFolderVo.getType()));
            }
            treeItemVo.setStatusType(audienceAndFolderVo.getSegmentStatusType());
            treeItemVo.setLookalikeType(audienceAndFolderVo.getLookalikeType());
            if (audienceAndFolderVo.getCount() == 0 && LookalikeType.ADVANCE.equals(audienceAndFolderVo.getLookalikeType())
                    && SegmentStatusType.LOOKALIKE_READY.equals(audienceAndFolderVo.getSegmentStatusType())) {
                JSONObject jsonObject = JSONObject.parseObject(audienceAndFolderVo.getLookalikeResult());
                int recommended = jsonObject.getInteger("recommended-percentile");
                List reach = jsonObject.getJSONArray("reach-values");
                int index = recommended - 1;
                if (index >= 0) {
                    treeItemVo.setValue(((Integer) reach.get(index)).longValue());
                }
            }
            treeItemVo.setRuleJson(audienceAndFolderVo.getRuleJsonDisplay());
            treeItemVo.setTestCount(audienceAndFolderVo.getTestCount());
            treeItemVo.setControlCount(audienceAndFolderVo.getControlCount());
            treeItemVo.setErrorCode(audienceAndFolderVo.getErrorCode());
            treeItemVoList.add(treeItemVo);
        }
        return treeItemVoList;
    }

    private List<TreeItemVo> parseAudienceToTreeItemVo(List<AudiencePo> audiencePoList) {
        List<TreeItemVo> treeItemVoList = new ArrayList<>();
        for (AudiencePo audiencePo : audiencePoList) {
            TreeItemVo treeItemVo = new TreeItemVo();
            treeItemVo.setName(audiencePo.getName());
            treeItemVo.setTaxonomyId(audiencePo.getTaxonomyId());
            treeItemVo.setId(String.valueOf(audiencePo.getId()));
            treeItemVo.setValue(audiencePo.getCount());
            treeItemVo.setType(TreeItemType.FILE);
            treeItemVo.setSourceType(parseFolderType(audiencePo.getAudienceType()));
            treeItemVo.setStatusType(audiencePo.getSegmentStatusType());
            treeItemVo.setLookalikeType(audiencePo.getLookalikeType());
            if ((audiencePo.getCount() != null && audiencePo.getCount() == 0) && LookalikeType.ADVANCE.equals(audiencePo.getLookalikeType())
                    && SegmentStatusType.LOOKALIKE_READY.equals(audiencePo.getSegmentStatusType())) {
                JSONObject jsonObject = JSONObject.parseObject(audiencePo.getLookalikeResult());
                int recommended = jsonObject.getInteger("recommended-percentile");
                List reach = jsonObject.getJSONArray("reach-values");
                int index = recommended - 1;
                if (index >= 0) {
                    treeItemVo.setValue(((Integer) reach.get(index)).longValue());
                }
            }
            treeItemVo.setRuleJson(audiencePo.getRuleJson());
            treeItemVo.setTestCount(audiencePo.getTestCount());
            treeItemVo.setControlCount(audiencePo.getControlCount());
            treeItemVoList.add(treeItemVo);
        }
        return treeItemVoList;
    }

    private SourceType parseFolderType(FolderType folderType) {
        switch (folderType) {
            case LOOKALIKE_GROUP:
                return SourceType.LOOKALIKE;
            case SAVED_SEGMENT:
                return SourceType.SEGMENT;
            case CAMPAIGN:
                return SourceType.CAMPAIGN;
            default:
                return SourceType.SEGMENT;
        }
    }

    @Override
    public List<AudiencePo> getAudiencePoByIds(List<Long> ids) {
        return audiencePoJPA.findAudiencePoByIdIn(ids);
    }

    @Override
    public void copySegment(Long audienceId, String taxonomyId, SegmentForCopyDTO audienceParam)
            throws AMSException {
        String owner = audienceParam.getNewOwner();
        AudiencePo audiencePo = getSegment(audienceId);
        // copy name
        String audienceName = generateNewAudienceNameForDuplicateAudience(audiencePo);
        Long count = bitmapService
                .calculateForNonTv(audiencePo.getTenantId(), "", audiencePo.getRuleJson());
        AudiencePo audience = new AudiencePo();
        audience.setName(audienceName);
        audience.setTaxonomyId(taxonomyId);
        audience.setFrozenCount(count);
        audience.setCreatedBy(owner);
        audience.setCreatedTime(new Date());
        audience.setUpdateTime(new Date());
        audience.setSegmentStatusType(SegmentStatusType.SEGMENT_NEW);
        if (audiencePo.getFolderPo().getFolderType().equals(FolderType.CAMPAIGN)) {
            audience.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        }
        audience.setAudienceType(audiencePo.getAudienceType());
        audience.setCount(count);
        audience.setCode("");
        audience.setCost(audiencePo.getCost());
        audience.setDescription(audiencePo.getDescription());
        audience.setFolderPo(audiencePo.getFolderPo());
        JSONObject ruleObj = JSONObject.parseObject(audiencePo.getRuleJson());
        JSONArray segments = ruleObj.getJSONArray(SEGMENTS);
        for (int i = 0; i < segments.size(); i++) {
            JSONObject sObj = segments.getJSONObject(i);
            sObj.put(COUNT, count);
            sObj.put(FROZEN_COUNT, count);
        }
        ruleObj.put(SEGMENTS, segments);
        audience.setRuleJson(ruleObj.toJSONString());
        audience.setTenantId(audiencePo.getTenantId());
        audience.setDeleted(audiencePo.isDeleted());
        audience.setUniverseIds(audiencePo.getUniverseIds());
        if (ruleObj.getDouble("test-control") > 0) {
            audience.setSegmentStatusType(SegmentStatusType.SEGMENT_RUNNING);
        }
        audiencePoJPA.save(audience);
        bitmapService.createBitmapForNonTV(audiencePo.getTenantId(), taxonomyId, audienceParam.getUserId(),
                audiencePo.getRuleJson(), true);
    }

    @Override
    public SegmentAndAttributeVo getSegmentAndAttributeByKey(Long tenantId, String key)
            throws AMSException {
        //get attributes
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionService.getActiveVersionByTenant(tenantPo);
        List<Taxonomy> taxonomyList = taxonomyAPI.getTaxonomyAttributeByKey(
                String.valueOf(versionPo.getId()), key);
        List<TreeItemVo> treeItemVoList = parseTaxonomyToTreeItemVoV2(taxonomyList, false, Collections.emptyList(),
                tenantPo);
        // get shared taxonomy list
        List<Taxonomy> sharedTaxonomyList = dataSourceAPI.searchSharedTaxonomyByKeys(tenantPo.getTenantId(),
                new String[]{key});
        treeItemVoList.addAll(parseTaxonomyToTreeItemVoV2(sharedTaxonomyList, true, Collections.emptyList(), tenantPo));
        SegmentAndAttributeVo segmentAndAttributeVo = new SegmentAndAttributeVo();
        segmentAndAttributeVo.setTreeItemVoList(treeItemVoList);
        // get segments and lookalike
        List<AudienceVo> audienceVoList = new ArrayList<>();
        List<AudiencePo> segmentList = audiencePoJPA
                .findByAudienceTypeAndTenantIdAndNameLike(FolderType.SAVED_SEGMENT, tenantId,
                        key.concat(Constant.PER_CENT));
        segmentList = segmentList.stream().filter(audiencePo ->
                (!SegmentStatusType.SEGMENT_RUNNING.equals(audiencePo.getSegmentStatusType())
                        && !SegmentStatusType.SEGMENT_FAILED.equals(audiencePo.getSegmentStatusType()))
        ).collect(Collectors.toList());
        audienceVoList.addAll(audienceVoMapper.map(segmentList));
        List<AudiencePo> lookLikeList = audiencePoJPA
                .findByAudienceTypeAndTenantIdAndNameLike(FolderType.LOOKALIKE_GROUP, tenantId,
                        key.concat(Constant.PER_CENT));
        if (Optional.ofNullable(lookLikeList).isPresent()) {
            lookLikeList = lookLikeList.stream().filter(audiencePo ->
                    !Optional.ofNullable(audiencePo.getSegmentStatusType()).isPresent()
            ).collect(Collectors.toList());
            audienceVoList.addAll(audienceVoMapper.map(lookLikeList));
        }
        segmentAndAttributeVo.setAudienceVoList(audienceVoList);
        return segmentAndAttributeVo;
    }


    // get attribute by node id
    @Override
    public List<TaxonomyItemVo> getAttributeByNodeIdAndName(Long tenantId, List<String> nodeIds)
            throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionService.getActiveVersionByTenant(tenantPo);
        List<Taxonomy> taxonomyList = taxonomyAPI
                .getTaxonomyListByNodeIdAndName(String.valueOf(versionPo.getId()), nodeIds);
        // not exist in taxonomy , query in shared taxonomy
        if (taxonomyList.isEmpty()) {
            taxonomyList = dataSourceAPI.listAttributeSharedTaxonomyByNodeIds(tenantPo.getTenantId(), nodeIds);
        }
        // get shared taxonomy
        parseTaxonomyToTreeItemVo(taxonomyList, Collections.emptyList(), tenantPo);
        List<String> taxonomyIdList = new ArrayList<>();
        // lamda get perId
        taxonomyList.forEach(t -> {
            if (t.getId() != null) {
                taxonomyIdList.add(t.getId());
            }
        });
        Map<String, Long> map = bitmapService.listNodeCountByTaxonomyIds(tenantPo, taxonomyIdList,
                Collections.emptyList());
        List<TaxonomyItemVo> taxonomyItemVoList = taxonomyMapper.map(taxonomyList);
        taxonomyItemVoList.forEach(t -> {
            Long count = Optional.ofNullable(map.get(t.getTaxonomyId()))
                    .orElseGet(() -> 0L);
            t.setAudienceCount(count.toString());
        });
        taxonomyItemVoList.sort((t1, t2) -> t1.getTaxonomyId().compareTo(t2.getTaxonomyId()));
        return taxonomyItemVoList;
    }

    @Override
    public void copyCampaign(Long audienceId, AudienceParam audienceParam)
            throws AMSException {
        String owner = audienceParam.getNewOwner();
        AudiencePo audiencePo = Optional.ofNullable(audiencePoJPA.findOne(audienceId))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0221,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0221)));
        List<UniversePo> universePoList =
                universePoJPA.findAll(StringUtil.parseUniverseIdsToList(audiencePo.getUniverseIds()));
        // copy name
        String audienceName = generateNewAudienceNameForDuplicateAudience(audiencePo);
        // duplicated audience
        AudiencePo audience = new AudiencePo();
        audience.setName(audienceName);
        audience.setCreatedBy(owner);
        audience.setSegmentStatusType(SegmentStatusType.SEGMENT_NEW);
        if (audiencePo.getFolderPo().getFolderType().equals(FolderType.CAMPAIGN)) {
            audience.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        }
        audience.setAudienceType(audiencePo.getAudienceType());
        audience.setCount(audiencePo.getCount());
        audience.setCode("");
        audience.setCost(audiencePo.getCost());
        audience.setDescription(audiencePo.getDescription());
        audience.setFolderPo(audiencePo.getFolderPo());
        audience.setCreatedTime(new Date());
        audience.setUpdateTime(new Date());
        audience.setRuleJson(audiencePo.getRuleJson());
        audience.setTaxonomyId(audiencePo.getTaxonomyId());
        audience.setTenantId(audiencePo.getTenantId());
        audience.setDeleted(audiencePo.isDeleted());
        audience.setUniverseIds(audiencePo.getUniverseIds());
        audience.setLegalFlag(audiencePo.getLegalFlag());
        audiencePoJPA.save(audience);
        JSONObject ruleObj = JSONObject.parseObject(audience.getRuleJson());
        long cap = ruleObj.getLong("cap");
        ruleObj.put(CAMPAIGN_ID, audience.getId());
        JSONObject calculateResult = bitmapService.calculateV2(universePoList, ruleObj.toJSONString());
        JSONArray segments = ruleObj.getJSONArray(SEGMENTS);
        long count = 0;
        Map<String, Long> frozenCountMap = new HashMap<>();
        Map<String, Long> frozenNativeCountMap = new HashMap<>();
        List<AudienceCountByUniverseDTO> audienceCountByUniverseDTOList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : calculateResult.entrySet()) {
            AudienceCountByUniverseDTO audienceCountByUniverseDTO = new AudienceCountByUniverseDTO();
            for (UniversePo universePo : universePoList) {
                if (universePo.getUniverseSystemName().equals(entry.getKey())) {
                    audienceCountByUniverseDTO.setUniverseId(universePo.getId());
                }
            }
            List<Long> segmentCountList = new ArrayList<>();
            JSONObject universeObj = JSONObject.parseObject(entry.getValue().toString());
            JSONObject obj = JSONObject.parseObject(universeObj.get("counts").toString());
            JSONObject nativeObj = JSONObject.parseObject(universeObj.get("nativeCounts").toString());
            count += universeObj.getLong("total");
            long totalCount = 0;
            for (int i = 0; i < segments.size(); i++) {
                JSONObject sObj = segments.getJSONObject(i);
                String segmentId = sObj.getString("id");
                totalCount += obj.getLong(segmentId);
            }
            long tempTotalCount = 0;
            for (int i = 0; i < segments.size(); i++) {
                JSONObject sObj = segments.getJSONObject(i);
                String segmentId = sObj.getString("id");
                if (cap == 0 || (cap > 0 && totalCount <= cap)) {
                    frozenCountMap.put(segmentId, (Optional.ofNullable(frozenCountMap.get(segmentId)).isPresent() ?
                            frozenCountMap.get(segmentId) : 0) + obj.getLong(segmentId));
                } else {
                    float percent = (float) cap / totalCount;
                    long segmentCount = Math.round(obj.getLong(segmentId) * percent);
                    if (i == segments.size() - 1) {
                        frozenCountMap.put(segmentId, (Optional.ofNullable(frozenCountMap.get(segmentId)).isPresent() ?
                                frozenCountMap.get(segmentId) : 0) + cap - tempTotalCount);
                        segmentCountList.add(cap - tempTotalCount);
                    } else {
                        frozenCountMap.put(segmentId, (Optional.ofNullable(frozenCountMap.get(segmentId)).isPresent() ?
                                frozenCountMap.get(segmentId) : 0) + segmentCount);
                        segmentCountList.add(segmentCount);
                    }
                    tempTotalCount += segmentCount;
                }
                frozenNativeCountMap.put(segmentId,
                        (Optional.ofNullable(frozenNativeCountMap.get(segmentId)).isPresent() ?
                                frozenNativeCountMap.get(segmentId) : 0) + nativeObj.getLong(segmentId));
            }
            audienceCountByUniverseDTO.setAudienceCountByUniverse(universeObj.getLong("total"));
            audienceCountByUniverseDTO.setSegmentCounts(segmentCountList);
            audienceCountByUniverseDTOList.add(audienceCountByUniverseDTO);
        }
        for (int i = 0; i < segments.size(); i++) {
            JSONObject sObj = segments.getJSONObject(i);
            String segmentId = sObj.getString("id");
            sObj.put(COUNT, frozenCountMap.get(segmentId));
            sObj.put(FROZEN_COUNT, frozenCountMap.get(segmentId));
            sObj.put(FROZEN_NATIVE_COUNT, frozenNativeCountMap.get(segmentId));
        }
        ruleObj.put(SEGMENTS, segments);
        audience.setRuleJson(ruleObj.toJSONString());
        audience.setCount(count);
        audience.setFrozenCount(count);
        audience.setUniverseSegmentCountJson(JSONObject.toJSONString(audienceCountByUniverseDTOList));
        audience.setUpdateTime(new Date());
        audiencePoJPA.save(audience);
        for (UniversePo universePo : universePoList) {
            if (!universePo.getTenantPath().equals(universePo.getOwnerTenantPath())) {
                addUniverseActivityLog(audience, universePo);
            }
        }
        createBitmapForTV(audience, universePoList);
    }

    private String generateNewAudienceNameForDuplicateAudience(AudiencePo audiencePo) {
        String audienceNameTemp = audiencePo.getName() + Constant.COPY_FIX;
        List<AudiencePo> audiencePoList = audiencePoJPA.findAudiencePoByNameLikeAndTenantId(audienceNameTemp +
                        Constant.PER_CENT,
                audiencePo.getTenantId());
        String pattern = audienceNameTemp + "[\\d]*";
        // the audience duplicated list
        audiencePoList = audiencePoList.stream().filter(item -> (Pattern.matches(pattern, item.getName()))).collect
                (Collectors.toList());
        // get new name
        boolean flag = true;
        String audienceName = audienceNameTemp;
        int audiencePoListIndex = 0;
        int audiencePoListSize = audiencePoList.size();
        for (; audiencePoListIndex < audiencePoListSize; audiencePoListIndex++) {
            if (flag && audiencePoListIndex != 0) {
                break;
            }
            flag = true;
            if (audiencePoListIndex != 0) {
                audienceName = audienceNameTemp + (audiencePoListIndex + 1);
            }
            for (int i = 0; i < audiencePoListSize; i++) {
                if (audienceName.equals(audiencePoList.get(i).getName())) {
                    flag = false;
                    break;
                }
            }
        }
        if (!flag) {
            audienceName = audienceNameTemp + (audiencePoListIndex + 1);
        }
        return audienceName;
    }

    private Map<String, String> getNodeMap(String ruleJson) {
        Map<String, String> map = new HashMap<>();
        String pattern = "\"objectId\":[ ]?\"(.*?)\",\"name\":[ ]?\"(.*?)\"";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(ruleJson);
        while (m.find()) {
            String node = m.group(1);
            String name = m.group(2);
            map.put(node, name);
        }
        return map;
    }

    @Override
    public String getTaxonomyAllAttribute(String taxonomyName) throws AMSRMIException {
        return taxonomyAPI.getTaxonomyAllAttributeByName(taxonomyName);
    }

    @Override
    public List<PriceAndOwnerVO> listPriceAndOwnerByTaxonomyId(Long tenantId, Long destinationId, List<String>
            taxonomyIdList) throws AMSException {
        // TODO for get Shared taxonomy nodes
        tenantService.getTenantById(tenantId);
        UniversePo universePo = universeService.getUniverseById(destinationId);
        TenantPo treeTenantPo = tenantService.getTenantById(universePo.getTenantId());
        TenantPo[] tenantPos = new TenantPo[]{treeTenantPo};
        List<VersionPo> versionPoList = versionPoJPA
                .findAllByTenantPoInAndOperationFlag(tenantPos, TemplateStatusType.ACTIVE);
        List<PriceAndOwnerVO> priceAndOwnerVOList = new ArrayList<>();
        for (VersionPo versionPo : versionPoList) {
            priceAndOwnerVOList.addAll(taxonomyAPI
                    .listPriceAndOwnerByTaxonomyId(String.valueOf(versionPo.getId()), taxonomyIdList));
        }
        return priceAndOwnerVOList;
    }

    @Override
    public CampaignAndAttributeVO getSegmentAndAttributeByKeyV2(List<Long> universeIdList, String key, Long tenantId) throws
            AMSException {
        //get attributes
        TenantPo treeTenantPo = tenantService.getTenantById(tenantId);
        TenantPo[] tenantPos = new TenantPo[]{treeTenantPo};
        List<VersionPo> versionPoList = versionPoJPA
                .findAllByTenantPoInAndOperationFlag(tenantPos, TemplateStatusType.ACTIVE);
        List<TreeItemVo> treeItemVoList = new ArrayList<>();
        for (VersionPo versionPo : versionPoList) {
            treeItemVoList.addAll(parseTaxonomyToTreeItemVo(
                    taxonomyAPI.getTaxonomyAttributeByKey(
                            String.valueOf(versionPo.getId()), key), universeIdList, versionPo.getTenantPo()));
        }
        CampaignAndAttributeVO campaignAndAttributeVO = new CampaignAndAttributeVO();
        campaignAndAttributeVO.setTreeItemVoList(treeItemVoList);
        // get segments
        List<AudiencePo> segmentList = audiencePoJPA
                .findByAudienceTypeAndTenantId(FolderType.CAMPAIGN, tenantId);
        List<SegmentVO> segmentVOList = new ArrayList<>();
        if (Optional.ofNullable(segmentList).isPresent()) {
            segmentList.forEach(audiencePo -> {
                JSONObject rule = JSON.parseObject(audiencePo.getRuleJson());
                Double testControl = rule.getDouble("test-control");
                JSONArray segments = rule.getJSONArray(SEGMENTS);
                for (int i = 0; i < segments.size(); i++) {
                    JSONObject segment = segments.getJSONObject(i);
                    if (segment.getString("name").toLowerCase().startsWith(key.toLowerCase())) {
                        SegmentVO segmentVO = new SegmentVO();
                        segmentVO.setCampaignId(audiencePo.getId());
                        segmentVO.setCampaignName(audiencePo.getName());
                        segmentVO.setCount(segment.getLong(COUNT));
                        segmentVO.setId(segment.getString("id"));
                        segmentVO.setName(segment.getString("name"));
                        segmentVO.setInclude(segment.getJSONArray(INCLUDE).toJSONString());
                        segmentVO.setExclude(segment.getJSONArray(EXCLUDE).toJSONString());
                        segmentVO.setTestControl(testControl);
                        segmentVOList.add(segmentVO);
                    }
                }
            });
        }
        campaignAndAttributeVO.setSegmentVOList(segmentVOList);
        return campaignAndAttributeVO;
    }

    @Override
    public Long calculateForNonTv(Long tenantId, String userId, String rule) throws AMSException {
        return bitmapService.calculateForNonTv(tenantId, userId, rule);
    }

    @Override
    public List<DataTypeAndPriceAndOwnerVO> listDataTypeAndPriceAndOwnerByTaxonomyIdList(Long destinationId,
                                                                                         List<String> taxonomyIdList)
            throws AMSException {
        UniversePo universePo = universeService.getUniverseById(destinationId);
        List<VersionPo> versionPoList = getActiveVersionList(universePo.getTenantId());
        List<DataTypeAndPriceAndOwnerVO> dataTypeAndPriceAndOwnerVOList = new ArrayList<>();
        for (VersionPo versionPo : versionPoList) {
            dataTypeAndPriceAndOwnerVOList.addAll(taxonomyAPI
                    .listDataTypeAndPriceAndOwnerByTaxonomyIdList(String.valueOf(versionPo.getId()), taxonomyIdList));
        }
        return dataTypeAndPriceAndOwnerVOList;
    }

    @Override
    public MetricsVO countDistributedAndBuiltAudiencesByMonth(String yearMonth, SegmentStatusType audienceStatus,
                                                              FolderType audienceType) throws
            AMSException {
        List<String> tenantNameList = new ArrayList<>();
        tenantNameList.add("MVPD Demo");
        tenantNameList.add("Mercury Demo");
        tenantNameList.add("Brands");
        List<TenantPo> tenantPoList = tenantPoJPA.findByNameIn(tenantNameList);
        List<Long> tenantIdList = new ArrayList<>();
        List<String> tenantIdStringList = new ArrayList<>();
        tenantIdList.add(-1L);
        tenantIdStringList.add("-1");
        if (Optional.ofNullable(tenantPoList).isPresent() && !tenantPoList.isEmpty()) {
            tenantPoList.forEach(tenantPo -> {
                tenantIdList.add(tenantPo.getId());
                tenantIdStringList.add(tenantPo.getTenantId());
            });
        }
        yearMonth = Constant.formatDate(yearMonth);
        if (!Optional.ofNullable(yearMonth).isPresent()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0255,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0255));
        }
        MetricsVO metricsVO = new MetricsVO();
        Long builtCount = audiencePoJPA.countBuiltAudiencesByMonth(audienceType.name(), yearMonth, tenantIdList);
        Long distributedCount = audiencePoJPA.countDistributedAudiencesByMonth(audienceStatus.name(), yearMonth,
                tenantIdList);
        metricsVO.setBuiltCount(BigDecimal.valueOf(builtCount));
        metricsVO.setDistributedCount(BigDecimal.valueOf(distributedCount));
        if (FolderType.SAVED_SEGMENT.equals(audienceType)) {
            Long overlapCount = dataSourceAPI.countOverlapsByMonth(yearMonth, StringUtils.join(tenantIdStringList,
                    Constant.COMMA));
            metricsVO.setOverlapCount(BigInteger.valueOf(overlapCount));
        }
        return metricsVO;
    }

    @Override
    public List<TaxonomyItemVo> getTaxonomyEndTypeItemByTenantWithinLimit(Long tenantId, Integer limit) throws
            AMSException {
        TenantPo treeTenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionService.getActiveVersionByTenant(treeTenantPo);
        List<Taxonomy> taxonomyList = taxonomyAPI.getTaxonomyEndTypeItemWithinLimit(String.valueOf(versionPo.getId())
                , 1, limit);
        List<String> taxonomyIdList = new ArrayList<>();
        taxonomyList.forEach(t -> {
            if (t.getId() != null) {
                taxonomyIdList.add(t.getId());
            }
        });
        Map<String, Long> map = bitmapService.listNodeCountByTaxonomyIds(treeTenantPo, taxonomyIdList, null);
        List<TaxonomyItemVo> taxonomyItemVoList = taxonomyMapper.map(taxonomyList);
        taxonomyItemVoList.forEach(t -> {
            Long count = Optional.ofNullable(map.get(t.getTaxonomyId()))
                    .orElseGet(() -> 0L);
            t.setAudienceCount(count.toString());
        });
        taxonomyItemVoList.sort((t1, t2) -> t1.getTaxonomyId().compareTo(t2.getTaxonomyId()));
        return taxonomyItemVoList;
    }


    // v2 service
    @Override
    public List<TreeItemVo> getAllTreeItem(Long treeTenantId, SourceType sourceType,
                                           List<Long> universeIdList,
                                           String id)
            throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(treeTenantId);
        List<TreeItemVo> treeItemVoList = new ArrayList<>();
        switch (sourceType) {
            case TAXONOMY:
                VersionPo versionPo = versionService.getActiveVersionByTenant(tenantPo);
                isObjectId(id);
                List<Taxonomy> taxonomyList = taxonomyAPI.getTaxonomyList(String.valueOf(versionPo.getId()), id);
                treeItemVoList.addAll(parseTaxonomyToTreeItemVoV2(taxonomyList, false, universeIdList, tenantPo));
                break;
            case CAMPAIGN:
                Long parentFolderId;
                try {
                    parentFolderId = Long.parseLong(id);
                } catch (Exception e) {
                    LogUtils.error(e);
                    throw new AMSInvalidInputException(Constant.ERROR_CODE_0204,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0204));
                }
                List<AudienceAndFolderVo> audienceAndFolderVoList = folderService
                        .listCampaignByTenantId(parentFolderId, treeTenantId, FolderType.CAMPAIGN);
                treeItemVoList
                        .addAll(parseAudienceAndFolderVoToTreeItemVo(audienceAndFolderVoList));
                break;
            default:
                break;
        }
        return treeItemVoList;
    }

    @Override
    public List<TaxonomyItemVo> getTaxonomyTreeItem(Long tenantId, List<Long> universeIdList, String id)
            throws AMSException {
        List<VersionPo> versionPoList = getActiveVersionList(tenantId);
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        List<TaxonomyItemVo> taxonomyItemVoList = new ArrayList<>();
        for (VersionPo versionPo : versionPoList) {
            isObjectId(id);
            List<Taxonomy> taxonomyList = taxonomyAPI
                    .getTaxonomyListByNode(String.valueOf(versionPo.getId()), id);
            List<String> taxonomyIdList = new ArrayList<>();
            taxonomyList = taxonomyList.stream().filter(taxonomy -> {
                if (Optional.ofNullable(taxonomy.getId()).isPresent()) {
                    taxonomyIdList.add(taxonomy.getId());
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            Map<String, Long> resultMap;
            List<String> universeSysNameList = new ArrayList<>();
            if (Optional.ofNullable(universeIdList).isPresent() && !universeIdList.isEmpty()) {
                List<UniversePo> universePoList = universePoJPA.findAll(universeIdList);
                universePoList.forEach(universePo -> universeSysNameList.add(universePo.getUniverseSystemName()));
            }
            resultMap = bitmapService.listNodeCountByTaxonomyIds(tenantPo, taxonomyIdList, universeSysNameList);
            taxonomyItemVoList = taxonomyMapper.map(taxonomyList);
            taxonomyItemVoList.forEach(t -> {
                if (!Optional.ofNullable(t.getTaxonomyId()).isPresent()) {
                    t.setTaxonomyId("");
                }
                Long count = Optional.ofNullable(resultMap.get(t.getTaxonomyId()))
                        .orElseGet(() -> 0L);
                t.setAudienceCount(count.toString());
            });
            taxonomyItemVoList.sort(Comparator.comparing(taxonomyItemVo -> taxonomyItemVo.getTaxonomyId()));
            if (!taxonomyItemVoList.isEmpty()) {
                return taxonomyItemVoList;
            }
        }
        return taxonomyItemVoList;
    }

    // get Taxonomy EndType Item list for tv
    @Override
    public List<TaxonomyItemVo> getTaxonomyTreeEndTypeItemDestinationIdWithinLimit(Long tenantId, Integer limit)
            throws AMSException {
        List<VersionPo> versionPoList = getActiveVersionList(tenantId);
        List<TaxonomyItemVo> totalItemVoList = new ArrayList<>();
        for (VersionPo versionPo : versionPoList) {
            List<Taxonomy> taxonomyList = taxonomyAPI
                    .getTaxonomyEndTypeItemWithinLimit(String.valueOf(versionPo.getId()), 1, limit);
            List<TaxonomyItemVo> taxonomyItemVoList = taxonomyMapper.map(taxonomyList);
            taxonomyItemVoList.sort((t1, t2) -> (Optional.ofNullable(t1.getTaxonomyId()).isPresent() ?
                    t1.getTaxonomyId() : "").compareTo(t2.getTaxonomyId()));
            if (!taxonomyItemVoList.isEmpty()) {
                totalItemVoList.addAll(taxonomyItemVoList);
            }
        }
        return totalItemVoList;
    }

    // get source list for tv
    @Override
    public List<SourceItem> getSourceListV2(Long universeId, Long tenantId)
            throws AMSException {
        universeService.getUniverseByIdAndTenantId(universeId, tenantId);
        List<VersionPo> versionPoList = getActiveVersionList(tenantId);
        List<SourceItem> sourceItemList = new ArrayList<>();
        for (VersionPo versionPo : versionPoList) {
            if (!Optional.ofNullable(versionPo.getTreeId()).isPresent() || versionPo.getTreeId().isEmpty()) {
                continue;
            }
            List<Taxonomy> taxonomyList = taxonomyAPI
                    .getTaxonomyList(String.valueOf(versionPo.getId()), versionPo.getTreeId());
            for (Taxonomy taxonomy : taxonomyList) {
                SourceItem sourceItem = new SourceItem();
                sourceItem.setName(taxonomy.getName());
                sourceItem.setId(taxonomy.getObjectId());
                sourceItem.setSourceType(SourceType.TAXONOMY);
                sourceItem.setTenantId(versionPo.getTenantPo().getId());
                sourceItemList.add(sourceItem);
            }
        }
        List<FolderPo> folderVoList = folderService.getParentFolderV2();
        for (FolderPo folderpo : folderVoList) {
            SourceItem sourceItem = new SourceItem();
            sourceItem.setName(folderpo.getFolderName());
            sourceItem.setId(String.valueOf(folderpo.getId()));
            sourceItem.setSourceType(SourceType.CAMPAIGN);
            sourceItem.setTenantId(tenantId);
            sourceItemList.add(sourceItem);
        }
        return sourceItemList;
    }

    // get destination list by tenantId from universe for tv
    @Override
    public List<DestinationVo> getDestination(Long tenantId, String username) throws AMSRMIException {
        List<UniversePo> universePoList = universePoJPA.findAllByTenantId(tenantId);
        List<DestinationVo> destinationVoList = new ArrayList<>();
        List<TenantVo> ucTenantList = userCenterAPI.listUcTenant();
        Map<String, String> tenantNameMap = new HashMap<>();
        ucTenantList.forEach(tenantVo -> tenantNameMap.put(tenantVo.getTenantSysName(), tenantVo.getDisplayName()));
        for (UniversePo universePo : universePoList) {
            if (SegmentStatusType.UNIVERSE_FAILED.equals(universePo.getUniverseStatus())
                    || SegmentStatusType.UNIVERSE_PROCESSING.equals(universePo.getUniverseStatus())) {
                continue;
            }
            DestinationVo destinationVo = new DestinationVo();
            destinationVo.setDestinationId(universePo.getId());
            destinationVo.setDestinationName(universePo.getUniverseName());
            destinationVo.setTenantPath(universePo.getTenantPath());
            destinationVo.setTenantId(tenantId);
            destinationVo.setUniversePath(universePo.getUniverseSystemName());
            destinationVo.setUniverseThreshold(universePo.getUniverseThreshold());
            destinationVo.setUniverseCount(universePo.getUniverseCount());
            destinationVo.setOwnerTenantName(tenantNameMap.get(universePo.getOwnerTenantPath()));
            destinationVoList.add(destinationVo);
        }
        return destinationVoList;
    }

    // search tree item for tv
    @Override
    public Map<String, List<TreeItemVo>> searchTreeItemV2(Long tenantId, List<Long> universeIdList,
                                                          String key)
            throws AMSException {
        tenantService.getTenantById(tenantId);
        TenantPo treeTenantPo = tenantService.getTenantById(tenantId);
        TenantPo[] tenantPos = new TenantPo[]{treeTenantPo};
        List<VersionPo> versionPoList = versionPoJPA
                .findAllByTenantPoInAndOperationFlag(tenantPos, TemplateStatusType.ACTIVE);
        Map<String, List<TreeItemVo>> stringListMap = new HashMap<>();
        String[] keys = new String[]{key};
        if (Constant.AI_USE) {
            keys = aiAPI.query(key);
            if (keys.length == 0) {
                keys = new String[]{key};
            }
        }
        List<TreeItemVo> treeTaxonomyItemVoList = new ArrayList<>();
        for (VersionPo versionPo : versionPoList) {
            List<Taxonomy> taxonomyList = taxonomyAPI.getTaxonomyList(String.valueOf(versionPo.getId()), keys);
            treeTaxonomyItemVoList.addAll(parseTaxonomyToTreeItemVoV2(taxonomyList, false, universeIdList,
                    versionPo.getTenantPo(),
                    treeTenantPo));
        }
        stringListMap.put(Constant.TAXONOMY_NAME, treeTaxonomyItemVoList);
        List<AudiencePo> audiencePoList = audiencePoJPA
                .findByNameLikeAndAudienceTypeInAndTenantIdOrderByUpdateTime(
                        parseRegexLike(key), new FolderType[]{FolderType.CAMPAIGN}, tenantId);
        List<TreeItemVo> treeItemVoList = new ArrayList<>();
        treeItemVoList.addAll(parseAudienceToTreeItemVo(audiencePoList));
        stringListMap.put(String.valueOf(3), treeItemVoList);
        return stringListMap;
    }

    @Override
    public Map calculateV2(List<Long> universeIdList, String rule)
            throws AMSException {
        List<UniversePo> universePoList = universePoJPA.findAll(universeIdList);
        return bitmapService.calculateV2(universePoList, rule);
    }

    @Override
    public AudiencePo createCampaign(Long tenantId, CampaignParam campaignParam)
            throws AMSException {
        boolean isDuplicate = isDuplicateAudience(campaignParam.getAudienceType(), tenantId, campaignParam.getName());
        if (isDuplicate) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0213,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0213));
        }
        Long folderId = campaignParam.getFolderId();
        Long campaignCount = campaignParam.getAudienceCount();
        List<Long> universeIdList = campaignParam.getUniverseIdList();
        FolderPo folderPo = folderService.getFolderById(folderId);
        List<UniversePo> universePoList = universePoJPA.findAll(universeIdList);
        if (universePoList.size() != universeIdList.size()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0257,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0257));
        }
        // match the threshold
        matchedUniverseThreshold(universePoList, campaignParam.getAudienceCountByUniverseDTOList());
        AudiencePo audiencePo = new AudiencePo();
        audiencePo.setCount(campaignCount);
        audiencePo.setFolderPo(folderPo);
        audiencePo.setTenantId(tenantId);

        audiencePo.setName(campaignParam.getName());
        audiencePo.setAudienceType(campaignParam.getAudienceType());
        audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_SAVED);
        audiencePo.setCreatedBy(campaignParam.getCreatedBy());
        audiencePo.setCode(campaignParam.getSegmentCode() != null ? campaignParam.getSegmentCode()
                : String.valueOf(System.currentTimeMillis()));
        audiencePo.setRuleJson(campaignParam.getRule());
        audiencePo.setDescription(campaignParam.getDescription());
        audiencePo.setUniverseIds(universeIdList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        audiencePo.setFrozenCount(campaignCount);
        audiencePo.setLegalFlag(true);
        audiencePo.setUniverseSegmentCountJson(JSONObject.toJSONString(campaignParam.getAudienceCountByUniverseDTOList()));
        audiencePoJPA.save(audiencePo);
        List<String> universeSysNameList = new ArrayList<>();
        for (UniversePo universePo : universePoList) {
            universeSysNameList.add(universePo.getUniverseSystemName());
            if (!universePo.getTenantPath().equals(universePo.getOwnerTenantPath())) {
                addUniverseActivityLog(audiencePo, universePo);
            }
        }
        // Record the last universe used by the user
        createOrUpdateTenantAndUniverseBindings(tenantId, StringUtils.join(campaignParam.getUniverseIdList(), ","),
                campaignParam.getCreatedBy());
        createBitmapForTV(audiencePo, universePoList);
        return audiencePo;
    }

    private void matchedUniverseThreshold(List<UniversePo> universePoList,
                                          List<AudienceCountByUniverseDTO> audienceCountByUniverseDTOList) throws AMSInvalidInputException {
        Map<Long, Long> audienceCountMap = new HashMap<>();
        audienceCountByUniverseDTOList.forEach(audienceCountByUniverseDTO ->
                audienceCountMap.put(audienceCountByUniverseDTO.getUniverseId(),
                        audienceCountByUniverseDTO.getAudienceCountByUniverse()));
        for (UniversePo universePo : universePoList) {
            long threshold = Math.round(universePo.getUniverseCount() * universePo.getUniverseThreshold());
            if (!Optional.ofNullable(audienceCountMap.get(universePo.getId())).isPresent() || audienceCountMap.get(universePo.getId()) < threshold) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0268,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0268));
            }
        }
    }

    private void createBitmapForTV(AudiencePo audiencePo, List<UniversePo> universePoList) throws AMSRMIException,
            AMSInvalidInputException {
        List<Object> universes = new ArrayList<>();
        for (UniversePo universePo : universePoList) {
            JSONObject object = new JSONObject();
            object.put("universeSysName", universePo.getUniverseSystemName());
            object.put("universeName", universePo.getUniverseName());
            object.put("universeID", universePo.getId());
            universes.add(object);
        }
        TenantPo tenantPo = tenantService.getTenantById(audiencePo.getTenantId());
        HashMap paramMap = new HashMap<String, Object>();
        paramMap.put(CAMPAIGN_ID, audiencePo.getId());
        paramMap.put(TENANT_PATH, tenantPo.getPath());
        paramMap.put(TENANT_ID, audiencePo.getTenantId());
        paramMap.put(RULE, audiencePo.getRuleJson());
        paramMap.put("universes", universes);
        bitmapAPI.createBitmapForTV(JSONObject.toJSONString(paramMap));
    }

    private void addUniverseActivityLog(AudiencePo audiencePo, UniversePo universePo) {
        TenantPo tenantPo = tenantPoJPA.findTenantPoById(audiencePo.getTenantId());
        UniverseActivityLogPo universeActivityLogPo = new UniverseActivityLogPo();
        universeActivityLogPo.setAudienceId(audiencePo.getId());
        universeActivityLogPo.setAudienceName(audiencePo.getName());
        universeActivityLogPo.setAudienceStatus(audiencePo.getSegmentStatusType());
        universeActivityLogPo.setAudienceType(audiencePo.getAudienceType());
        universeActivityLogPo.setAudienceCount(audiencePo.getCount());
        universeActivityLogPo.setTenantId(audiencePo.getTenantId());
        universeActivityLogPo.setTenantName(tenantPo.getName());
        universeActivityLogPo.setDestinationId(universePo.getId());
        universeActivityLogPo.setUniverseName(universePo.getUniverseName());
        universeActivityLogPo.setFolderId(audiencePo.getFolderPo().getId());
        universeActivityLogPo.setAudienceRuleJson(audiencePo.getRuleJson());
        universeActivityLogPo.setCreatedBy(audiencePo.getCreatedBy());
        tenantPo = tenantPoJPA.findFirstByPath(universePo.getOwnerTenantPath());
        universeActivityLogPo.setOwnerTenantId(tenantPo.getId());
        universeActivityLogPoJPA.save(universeActivityLogPo);
    }

    @Override
    public void updateCampaign(Long tenantId, Long audienceId, CampaignParam campaignParam)
            throws AMSException {
        AudiencePo audiencePo = Optional
                .ofNullable(audiencePoJPA.findOne(audienceId))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0221,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0221)));
        Long campaignCount = campaignParam.getAudienceCount();
        FolderPo folderPo = folderService.getFolderById(campaignParam.getFolderId());
        if (!StringUtils.equals(audiencePo.getCreatedBy(), campaignParam.getCreatedBy())) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0206,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0206));
        }
        if (!audiencePo.getSegmentStatusType().equals(SegmentStatusType.CAMPAIGN_SAVED)
                && !audiencePo.getSegmentStatusType().equals(SegmentStatusType.CAMPAIGN_PREPARING)) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0217,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0217));
        }
        List<UniversePo> universePoList = universePoJPA.findAll(campaignParam.getUniverseIdList());
        matchedUniverseThreshold(universePoList, campaignParam.getAudienceCountByUniverseDTOList());
        audiencePo.setCount(campaignCount);
        audiencePo.setFrozenCount(campaignCount);
        audiencePo.setRuleJson(campaignParam.getRule());
        audiencePo.setAudienceType(campaignParam.getAudienceType());
        if (Optional.ofNullable(campaignParam.getCost()).isPresent()) {
            audiencePo.setCost(campaignParam.getCost());
        }
        if (Optional.ofNullable(campaignParam.getSegmentCode()).isPresent()) {
            audiencePo.setCode(campaignParam.getSegmentCode());
        }
        audiencePo.setFolderPo(folderPo);
        if (Optional.ofNullable(campaignParam.getDescription()).isPresent()) {
            audiencePo.setDescription(campaignParam.getDescription());
        }
        audiencePo.setUniverseIds(campaignParam.getUniverseIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        Optional<AudiencePo> audiencePoOptional = Optional.ofNullable(audiencePoJPA
                .findAudiencePoByAudienceTypeAndTenantIdAndName(campaignParam.getAudienceType(),
                        tenantId, campaignParam.getName()));
        if (audiencePoOptional.isPresent() && !StringUtils
                .equals(campaignParam.getName(), audiencePo.getName())) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0213,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0213));
        }
        audiencePo.setName(campaignParam.getName());
        audiencePo.setLegalFlag(true);
        audiencePo.setUniverseSegmentCountJson(JSONObject.toJSONString(campaignParam.getAudienceCountByUniverseDTOList()));
        audiencePoJPA.save(audiencePo);
        for (UniversePo universePo : universePoList) {
            if (!universePo.getTenantPath().equals(universePo.getOwnerTenantPath())) {
                addUniverseActivityLog(audiencePo, universePo);
            }
        }
        createOrUpdateTenantAndUniverseBindings(tenantId, StringUtils.join(campaignParam.getUniverseIdList(), ","),
                campaignParam.getCreatedBy());
        createBitmapForTV(audiencePo, universePoList);
    }

    @Override
    public List<AudiencePo> getCampaignStatusByIds(Long[] campaignIds) {
        return audiencePoJPA
                .findAudiencePoByIdInAndAudienceType(Arrays.asList(campaignIds), FolderType.CAMPAIGN);
    }

    @Override
    public ResponseEntity<Resource> exportCampaign(FolderAndAudience folderAndAudience,
                                                   Long tenantId)
            throws AMSException {
        List<AudiencePo> audiencePoList = audiencePoJPA
                .findAudiencePoByIdInAndAudienceType(folderAndAudience.getAudienceIdList(),
                        FolderType.CAMPAIGN);
        Integer size = folderAndAudience.getFolderIdList().size();
        Long[] audiencePoArray = new Long[size];
        folderAndAudience.getFolderIdList().toArray(audiencePoArray);
        List<AudiencePo> audiencePoFolderList = audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(audiencePoArray, ","));
        audiencePoList.addAll(audiencePoFolderList);
        if (audiencePoList.isEmpty()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0228,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0228));
        }
        List<String> nameList = new ArrayList<>();
        for (AudiencePo audiencePo : audiencePoList) {
            String name = getCampaignName(audiencePo);
            nameList.add(name);
        }
        String fileName = "";
        File file;
        if (nameList.size() == 1) {
            file = new File(nameList.get(0));
            fileName = getFileNameByPath(nameList.get(0));
        } else {
            String zipPath = getZip(nameList);
            file = new File(zipPath);
            fileName = Constant.CAMPAIGN_EXPORT_NAME + getCurrentDate() + ".zip";
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HttpHeaders headers = new HttpHeaders();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int ch;
            while ((ch = fileInputStream.read()) != -1) {
                bos.write(ch);
            }
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("charset", "utf-8");
            headers.add("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        } catch (IOException e) {
            throw new AMSFileIOException(Constant.ERROR_CODE_0107,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0107));
        }
        Resource resource = new InputStreamResource(
                new ByteArrayInputStream(bos.toByteArray()));
        FileSystemUtils.deleteRecursively(file);
        return ResponseEntity.ok().headers(headers)
                .contentType(MediaType.parseMediaType("application/x-msdownload")).body(resource);
    }

    @Override
    public void refreshCampaign(FolderAndAudience folderAndAudience)
            throws AMSException {
        String owner = folderAndAudience.getOwner();
        List<Long> folderIdList = folderAndAudience.getFolderIdList();
        String folderIdStr = StringUtils.join(folderIdList, ",");
        //folder and audience permission to update
        List<AudiencePo> audiencePoList = audiencePoJPA.getSegmentListByFolderId(folderIdStr);
        //folder and audience permission to update
        List<Long> audienceIdList = folderAndAudience.getAudienceIdList();
        audiencePoList.addAll(audiencePoJPA.findAudiencePoByIdIn(audienceIdList));
        Long size = Long.valueOf(audiencePoList.size());
        Long filterSize = audiencePoList.stream().filter(
                audienceAndFolderVo -> StringUtils.equals(audienceAndFolderVo.getCreatedBy(), owner))
                .count();
        Long filterStatusSize = audiencePoList.stream().filter(
                audienceAndFolderVo -> audienceAndFolderVo.getSegmentStatusType()
                        .equals(SegmentStatusType.CAMPAIGN_SAVED))
                .count();
        if (size != filterSize) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0206,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0206));
        }
        if (size != filterStatusSize) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0215,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0215));
        }
        // update count
        Set<AudiencePo> audiencePoSet = new HashSet<>();
        audiencePoSet.addAll(audiencePoList);
        List<AudiencePo> newAudiencePoList = new ArrayList<>();
        for (AudiencePo audiencePo : audiencePoSet) {
            List<UniversePo> universePoList =
                    universePoJPA.findAll(StringUtil.parseUniverseIdsToList(audiencePo.getUniverseIds()));
            JSONObject calculateResult = bitmapService.calculateV2(universePoList, audiencePo.getRuleJson());
            JSONObject ruleObj = JSONObject.parseObject(audiencePo.getRuleJson());
            long cap = ruleObj.getLong("cap");
            JSONArray segments = ruleObj.getJSONArray(SEGMENTS);
            long count = 0;
            Map<String, Long> frozenCountMap = new HashMap<>();
            Map<String, Long> frozenNativeCountMap = new HashMap<>();
            List<AudienceCountByUniverseDTO> audienceCountByUniverseDTOList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : calculateResult.entrySet()) {
                AudienceCountByUniverseDTO audienceCountByUniverseDTO = new AudienceCountByUniverseDTO();
                for (UniversePo universePo : universePoList) {
                    if (universePo.getUniverseSystemName().equals(entry.getKey())) {
                        audienceCountByUniverseDTO.setUniverseId(universePo.getId());
                    }
                }
                List<Long> segmentCountList = new ArrayList<>();
                JSONObject universeObj = JSONObject.parseObject(entry.getValue().toString());
                JSONObject obj = JSONObject.parseObject(universeObj.get("counts").toString());
                JSONObject nativeObj = JSONObject.parseObject(universeObj.get("nativeCounts").toString());
                count += universeObj.getLong("total");
                long totalCount = 0;
                for (int i = 0; i < segments.size(); i++) {
                    JSONObject sObj = segments.getJSONObject(i);
                    String segmentId = sObj.getString("id");
                    totalCount += obj.getLong(segmentId);
                }
                long tempTotalCount = 0;
                for (int i = 0; i < segments.size(); i++) {
                    JSONObject sObj = segments.getJSONObject(i);
                    String segmentId = sObj.getString("id");
                    if (cap == 0 || (cap > 0 && totalCount <= cap)) {
                        frozenCountMap.put(segmentId, (Optional.ofNullable(frozenCountMap.get(segmentId)).isPresent() ?
                                frozenCountMap.get(segmentId) : 0) + obj.getLong(segmentId));
                    } else {
                        float percent = (float) cap / totalCount;
                        long segmentCount = Math.round(obj.getLong(segmentId) * percent);
                        if (i == segments.size() - 1) {
                            frozenCountMap.put(segmentId, (Optional.ofNullable(frozenCountMap.get(segmentId)).isPresent() ?
                                    frozenCountMap.get(segmentId) : 0) + cap - tempTotalCount);
                            segmentCountList.add(cap - tempTotalCount);
                        } else {
                            frozenCountMap.put(segmentId, (Optional.ofNullable(frozenCountMap.get(segmentId)).isPresent() ?
                                    frozenCountMap.get(segmentId) : 0) + segmentCount);
                            tempTotalCount += segmentCount;
                        }
                        tempTotalCount += segmentCount;
                    }
                    frozenNativeCountMap.put(segmentId,
                            (Optional.ofNullable(frozenNativeCountMap.get(segmentId)).isPresent() ?
                                    frozenNativeCountMap.get(segmentId) : 0) + nativeObj.getLong(segmentId));
                }
                audienceCountByUniverseDTO.setAudienceCountByUniverse(universeObj.getLong("total"));
                audienceCountByUniverseDTO.setSegmentCounts(segmentCountList);
                audienceCountByUniverseDTOList.add(audienceCountByUniverseDTO);
            }
            for (int i = 0; i < segments.size(); i++) {
                JSONObject sObj = segments.getJSONObject(i);
                String segmentId = sObj.getString("id");
                sObj.put(COUNT, frozenCountMap.get(segmentId));
                sObj.put(FROZEN_COUNT, frozenCountMap.get(segmentId));
                sObj.put(FROZEN_NATIVE_COUNT, frozenNativeCountMap.get(segmentId));
            }
            ruleObj.put(SEGMENTS, segments);
            audiencePo.setRuleJson(ruleObj.toJSONString());
            audiencePo.setCount(count);
            audiencePo.setFrozenCount(count);
            audiencePo.setUniverseSegmentCountJson(JSONObject.toJSONString(audienceCountByUniverseDTOList));
            audiencePo.setUpdateTime(new Date());
            newAudiencePoList.add(audiencePo);
        }
        audiencePoJPA.save(newAudiencePoList);
    }

    @Override
    public List<DataStoreVo> getDataStoreNodeList() throws AMSInvalidInputException {
        List<AudienceDistributeJobPo> audienceDistributeJobPoList = audienceDistributeJobPoJPA
                .findByAudienceTypeAndStatus(FolderType.CAMPAIGN,
                        SegmentStatusType.CAMPAIGN_DISTRIBUTED);
        List<DataStoreVo> dataStoreVoList = new ArrayList<>();
        for (AudienceDistributeJobPo audienceDistributeJobPo : audienceDistributeJobPoList) {
            JSONArray values = new JSONArray();
            String rules = audienceDistributeJobPo.getRules();
            JSONObject obj = JSONObject.parseObject(rules);
            JSONArray segments = obj.getJSONArray(SEGMENTS);
            for (int i = 0; i < segments.size(); i++) {
                JSONObject segmentObj = segments.getJSONObject(i);
                values.addAll(getAllDataStoreNode(segmentObj));
            }
            if (!values.isEmpty()) {
                DataStoreVo dataStoreVo = parseNodeToDataStoreVo(values,
                        audienceDistributeJobPo.getAudienceId(),
                        audienceDistributeJobPo.getTenantId(),
                        audienceDistributeJobPo.getDestinationId());
                dataStoreVoList.add(dataStoreVo);
            }
        }
        return dataStoreVoList;
    }

    @Override
    public Boolean checkSecurity(SecurityParam securityParam) {
        List<AudiencePo> audiencePoList = audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(securityParam.getFolderIdList(), ","));
        audiencePoList.addAll(audiencePoJPA.findAll(securityParam.getCampaignIdList()));
        Set<Long> tenantSet = new HashSet<>();
        audiencePoList.forEach(audiencePo -> tenantSet.add(audiencePo.getTenantId()));
        if (tenantSet.size() == 1) {
            return (tenantSet.iterator().next() == securityParam.getTenantId());
        }
        return false;
    }

    @Override
    public List<OwnerTypeVo> getOwnerTypeV2(List<String> ownerList, List<NodeDTO> nodeDTOList,
                                            List<SegmentDTO> segmentDTOList, Long destinationId)
            throws AMSException {
        List<OwnerTypeVo> ownerTypeVoList = new ArrayList<>();
        if (!Optional.ofNullable(ownerList).isPresent()) {
            return ownerTypeVoList;
        }
        UniversePo universePo = universeService.getUniverseById(destinationId);
        TenantPo treeTenantPo = tenantService.getTenantById(universePo.getTenantId());
        TenantPo[] tenantPos = new TenantPo[]{treeTenantPo};
        List<VersionPo> versionPoList = versionPoJPA
                .findAllByTenantPoInAndOperationFlag(tenantPos, TemplateStatusType.ACTIVE);
        List<OwnerAndNodeDTO> ownerAndNodeDTOList = new ArrayList<>();
        if (Optional.ofNullable(segmentDTOList).isPresent()) {
            ownerAndNodeDTOList = parseSegment(segmentDTOList, ownerAndNodeDTOList);
        }
        for (OwnerAndNodeDTO ownerAndNodeDTO : ownerAndNodeDTOList) {
            nodeDTOList.addAll(ownerAndNodeDTO.getNodeDTOList());
            ownerList.addAll(ownerAndNodeDTO.getOwnerList());
        }
        if (Optional.ofNullable(nodeDTOList).isPresent()) {
            for (VersionPo versionPo : versionPoList) {
                List<String> ownerList1 = taxonomyAPI
                        .getAttributeOwnerByNodeIdAndName(String.valueOf(versionPo.getId()),
                                nodeDTOList);
                ownerList.addAll(ownerList1);
            }
        }
        ownerList =
                ownerList.stream().filter(string -> Optional.ofNullable(string).isPresent()).collect(Collectors.toList());
        List<TenantPo> tenantPoList = Optional.ofNullable(tenantPoJPA.findByNameIn(ownerList))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0227,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0227)));
        for (TenantPo tenantPo1 : tenantPoList) {
            String tenantId1 = tenantPo1.getTenantId();
            TenantExtVo tenantExtVo = userCenterAPI.getTenantExtByKey(tenantId1, "data_type");
            if (Optional.ofNullable(tenantExtVo).isPresent()) {
                OwnerTypeVo ownerTypeVo = new OwnerTypeVo();
                ownerTypeVo.setOwnerType(tenantExtVo.getValue());
                ownerTypeVo.setOwner(tenantPo1.getName());
                ownerTypeVoList.add(ownerTypeVo);
            } else {
                OwnerTypeVo ownerTypeVo = new OwnerTypeVo();
                ownerTypeVo.setOwnerType("");
                ownerTypeVo.setOwner(tenantPo1.getName());
                ownerTypeVoList.add(ownerTypeVo);
            }
        }
        return ownerTypeVoList;
    }

    public List<OwnerAndNodeDTO> parseSegment(List<SegmentDTO> segmentDTOList,
                                              List<OwnerAndNodeDTO> ownerAndNodeDTOList) {
        if (!Optional.ofNullable(segmentDTOList).isPresent() || segmentDTOList.isEmpty()) {
            return ownerAndNodeDTOList;
        }
        for (SegmentDTO segmentDTO : segmentDTOList) {
            Long campaignId = segmentDTO.getCampaignId();
            AudiencePo audiencePo = audiencePoJPA.findOne(campaignId);
            if (Optional.ofNullable(audiencePo).isPresent()) {
                JSONObject obj = JSON.parseObject(audiencePo.getRuleJson());
                JSONArray segments = obj.getJSONArray(SEGMENTS);
                if (Optional.ofNullable(segments).isPresent()) {
                    for (String segmentId : segmentDTO.getSegmentIdList()) {
                        for (int i = 0; i < segments.size(); i++) {
                            JSONObject segment = segments.getJSONObject(i);
                            JSONArray condition = segment.getJSONArray(INCLUDE)
                                    .fluentAddAll(segment.getJSONArray(EXCLUDE));
                            if (StringUtils.equals(segmentId, segment.getString("id"))) {
                                parseCondition(condition, ownerAndNodeDTOList);
                            }
                        }
                    }
                }
            }
        }
        return ownerAndNodeDTOList;
    }

    private void parseCondition(JSONArray jsonArray, List<OwnerAndNodeDTO> ownerAndNodeDTOList) {
        List<SegmentDTO> segmentDTOList = new ArrayList<>();
        List<ConditionDTO> conditionDTOList = new ArrayList<>();
        if (!Optional.ofNullable(jsonArray).isPresent() || jsonArray.isEmpty()) {
            return;
        }
        OwnerAndNodeDTO ownerAndNodeDTO = new OwnerAndNodeDTO();
        List<String> ownerList = new ArrayList<>();
        List<String> dataTypeList = new ArrayList<>();
        List<NodeDTO> nodeDTOList = new ArrayList<>();
        List<OwnerAndDataType> ownerAndDataTypeList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            conditionDTOList.add(parseIncludeRuleForFirstParty(obj));
            JSONArray items = obj.getJSONArray(ITEMS);
            if (Optional.ofNullable(items).isPresent() && items.size() > 0) {
                for (int m = 0; m < items.size(); m++) {
                    JSONObject item = items.getJSONObject(m);
                    conditionDTOList.add(parseIncludeRuleForFirstParty(item));
                    JSONArray childItems = item.getJSONArray(ITEMS);
                    while (childItems.size() > 0) {
                        JSONArray newChildItems = new JSONArray();
                        for (int r = 0; r < childItems.size(); r++) {
                            JSONObject childItem = childItems.getJSONObject(r);
                            conditionDTOList.add(parseIncludeRuleForFirstParty(childItem));
                            if (Optional.ofNullable(childItem.getJSONArray(ITEMS)).isPresent()) {
                                newChildItems.fluentAddAll(childItem.getJSONArray(ITEMS));
                            }
                        }
                        childItems = newChildItems;
                    }
                }
            }
        }
        conditionDTOList.forEach(conditionDTO -> {
            nodeDTOList.addAll(conditionDTO.getNodeDTOList());
            ownerList.addAll(conditionDTO.getOwnerList());
            segmentDTOList.addAll(conditionDTO.getSegmentDTOList());
            dataTypeList.addAll(conditionDTO.getDataTypeList());
            ownerAndDataTypeList.addAll(conditionDTO.getOwnerAndDataTypeList());
        });
        ownerAndNodeDTO.setNodeDTOList(nodeDTOList);
        ownerAndNodeDTO.setOwnerList(ownerList);
        ownerAndNodeDTO.setDataTypeList(dataTypeList);
        ownerAndNodeDTO.setOwnerAndDataTypeList(ownerAndDataTypeList);
        ownerAndNodeDTOList.add(ownerAndNodeDTO);
        parseSegment(segmentDTOList, ownerAndNodeDTOList);
    }

    private ConditionDTO parseIncludeRuleForFirstParty(JSONObject obj) {
        ConditionDTO conditionDTO = new ConditionDTO();
        List<String> ownerList = new ArrayList<>();
        List<String> dataTypeList = new ArrayList<>();
        List<NodeDTO> nodeDTOList = new ArrayList<>();
        List<SegmentDTO> segmentDTOList = new ArrayList<>();
        List<OwnerAndDataType> ownerAndDataTypeList = new ArrayList<>();
        if (StringUtils.equals(obj.getString("origin"), "tree")) {
            OwnerAndDataType ownerAndDataType = new OwnerAndDataType();
            String dataType = obj.getString("dataType");
            String owner = obj.getString(OWNER);
            if (StringUtils.isNotBlank(dataType)) {
                dataTypeList.add(dataType);
                ownerAndDataType.setDataType(dataType);
            }
            if (Optional.ofNullable(owner).isPresent()) {
                ownerList.add(owner);
                ownerAndDataType.setOwner(owner);
            }
            if (StringUtils.isBlank(dataType) || StringUtils.isBlank(owner)) {
                JSONArray valueArray = obj.getJSONArray(VALUES);
                if (Optional.ofNullable(valueArray).isPresent()) {
                    NodeDTO nodeDTO = new NodeDTO();
                    List<String> nodeIdList = new ArrayList<>();
                    for (int j = 0; j < valueArray.size(); j++) {
                        nodeIdList.add(valueArray.getJSONObject(j).getString("node"));
                    }
                    nodeDTO.setNodeIdList(nodeIdList);
                    nodeDTOList.add(nodeDTO);
                }
            }
            ownerAndDataTypeList.add(ownerAndDataType);
        } else if (StringUtils.equals(obj.getString("origin"), "segment")) {
            SegmentDTO segmentDTO = new SegmentDTO();
            segmentDTO.setCampaignId(obj.getLong(CAMPAIGN_ID));
            JSONArray values = obj.getJSONArray(VALUES);
            List<String> segmentIdList = new ArrayList<>();
            for (int j = 0; j < values.size(); j++) {
                String node = values.getJSONObject(j).getString("node");
                if (Optional.ofNullable(node).isPresent()) {
                    node = node.replace("_test", "").replace("_control", "");
                    segmentIdList.add(node);
                }
            }
            segmentDTO.setSegmentIdList(segmentIdList);
            segmentDTOList.add(segmentDTO);
        }
        conditionDTO.setNodeDTOList(nodeDTOList);
        conditionDTO.setOwnerList(ownerList);
        conditionDTO.setSegmentDTOList(segmentDTOList);
        conditionDTO.setDataTypeList(dataTypeList);
        conditionDTO.setOwnerAndDataTypeList(ownerAndDataTypeList);
        return conditionDTO;
    }

    private List<OwnerAndNodeDTO> parseRule(String rule, List<String> segmentIdList) {
        JSONObject obj = JSON.parseObject(rule);
        JSONArray segments = obj.getJSONArray(SEGMENTS);
        List<OwnerAndNodeDTO> ownerAndNodeDTOList = new ArrayList<>();
        if (!Optional.ofNullable(segments).isPresent()) {
            return ownerAndNodeDTOList;
        }
        for (String segmentId : segmentIdList) {
            for (int i = 0; i < segments.size(); i++) {
                JSONObject segment = segments.getJSONObject(i);
                if (StringUtils.equals(segmentId, segment.getString("id"))) {
                    OwnerAndNodeDTO include = parseInclude(segment.getJSONArray(INCLUDE));
                    OwnerAndNodeDTO exclude = parseInclude(segment.getJSONArray(EXCLUDE));
                    if (Optional.ofNullable(include).isPresent()) {
                        ownerAndNodeDTOList.add(include);
                    }
                    if (Optional.ofNullable(exclude).isPresent()) {
                        ownerAndNodeDTOList.add(exclude);
                    }
                }
            }
        }
        return ownerAndNodeDTOList;
    }

    private OwnerAndNodeDTO parseInclude(JSONArray jsonArray) {
        if (!Optional.ofNullable(jsonArray).isPresent() || jsonArray.isEmpty()) {
            return null;
        }
        OwnerAndNodeDTO ownerAndNodeDTO = new OwnerAndNodeDTO();
        List<String> ownerList = new ArrayList<>();
        List<NodeDTO> nodeDTOList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (Optional.ofNullable(obj.getString(OWNER)).isPresent()) {
                ownerList.add(obj.getString(OWNER));
            } else {
                JSONArray valueArray = obj.getJSONArray(VALUES);
                if (Optional.ofNullable(valueArray).isPresent()) {
                    NodeDTO nodeDTO = new NodeDTO();
                    List<String> nodeIdList = new ArrayList<>();
                    for (int j = 0; j < valueArray.size(); j++) {
                        nodeIdList.add(valueArray.getJSONObject(j).getString("node"));
                    }
                    nodeDTO.setNodeIdList(nodeIdList);
                    nodeDTOList.add(nodeDTO);
                }
            }
        }
        ownerAndNodeDTO.setNodeDTOList(nodeDTOList);
        ownerAndNodeDTO.setOwnerList(ownerList);
        return ownerAndNodeDTO;
    }


    // get attribute by node id v2
    @Override
    public List<TaxonomyItemVo> getAttributeByNodeIdAndNameV2(List<String> nodeIds, Long tenantId)
            throws AMSException {
        TenantPo treeTenantPo = tenantService.getTenantById(tenantId);
        TenantPo[] tenantPos = new TenantPo[]{treeTenantPo};
        List<VersionPo> versionPoList = versionPoJPA
                .findAllByTenantPoInAndOperationFlag(tenantPos, TemplateStatusType.ACTIVE);
        List<TaxonomyItemVo> taxonomyItemVoList = new ArrayList<>();
        for (VersionPo versionPo : versionPoList) {
            List<Taxonomy> taxonomyList = taxonomyAPI
                    .getTaxonomyListByNodeIdAndName(String.valueOf(versionPo.getId()), nodeIds);
            taxonomyItemVoList = taxonomyMapper.map(taxonomyList);
            taxonomyItemVoList.sort((t1, t2) -> t1.getTaxonomyId().compareTo(t2.getTaxonomyId()));
            if (!taxonomyItemVoList.isEmpty()) {
                return taxonomyItemVoList;
            }
        }
        return taxonomyItemVoList;
    }

    private String getCampaignName(AudiencePo audiencePo) throws AMSException {
        List<Long> universeIdList = StringUtil.parseUniverseIdsToList(audiencePo.getUniverseIds());
        List<UniversePo> universePoList = universePoJPA.findAll(universeIdList);
        String exportName =
                tempFile + "/audience" + "_" + audiencePo.getId() + ".xlsx";
        File exportFile = new File(exportName);
        File templateFile = new File(exportTemplateFile);
        if (!templateFile.exists()) {
            templateFile = new File(Constant.EXPORT_TEMPLATE_FILE);
        }
        try (FileInputStream fileInputStream = new FileInputStream(templateFile)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int ch;
            while ((ch = fileInputStream.read()) != -1) {
                bos.write(ch);
            }
            FileCopyUtils.copy(bos.toByteArray(), exportFile);
        } catch (IOException e) {
            throw new AMSFileIOException();
        }
        XSSFWorkbook wk = null;
        try {
            wk = new XSSFWorkbook(new FileInputStream(exportFile));
        } catch (IOException e) {
            LogUtils.error(e);
        }
        XSSFSheet sheet = wk.getSheetAt(0);
        CellStyle leftCellStyle = wk.createCellStyle();
        leftCellStyle.setAlignment(HorizontalAlignment.LEFT);
        leftCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        leftCellStyle.setWrapText(true);
        leftCellStyle.setBorderBottom(BorderStyle.THIN);
        leftCellStyle.setBorderLeft(BorderStyle.THIN);
        leftCellStyle.setBorderTop(BorderStyle.THIN);
        leftCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle midCellStyle = wk.createCellStyle();
        midCellStyle.setAlignment(HorizontalAlignment.CENTER);
        midCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        midCellStyle.setWrapText(true);
        midCellStyle.setBorderBottom(BorderStyle.THIN);
        midCellStyle.setBorderLeft(BorderStyle.THIN);
        midCellStyle.setBorderTop(BorderStyle.THIN);
        midCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle rightCellStyle = wk.createCellStyle();
        rightCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        rightCellStyle.setWrapText(true);
        rightCellStyle.setBorderBottom(BorderStyle.THIN);
        rightCellStyle.setBorderLeft(BorderStyle.THIN);
        rightCellStyle.setBorderTop(BorderStyle.THIN);
        rightCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle noThinRightCellStyle = wk.createCellStyle();
        noThinRightCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        noThinRightCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        noThinRightCellStyle.setWrapText(true);
        CellStyle lastMidCellStyle = wk.createCellStyle();
        lastMidCellStyle.setAlignment(HorizontalAlignment.CENTER);
        lastMidCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        lastMidCellStyle.setWrapText(true);
        lastMidCellStyle.setBorderBottom(BorderStyle.THIN);
        lastMidCellStyle.setBorderLeft(BorderStyle.THIN);
        lastMidCellStyle.setBorderTop(BorderStyle.THIN);
        lastMidCellStyle.setBorderRight(BorderStyle.MEDIUM);
        CellStyle endLeftCellStyle = wk.createCellStyle();
        endLeftCellStyle.setAlignment(HorizontalAlignment.LEFT);
        endLeftCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        endLeftCellStyle.setWrapText(true);
        endLeftCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        endLeftCellStyle.setBorderLeft(BorderStyle.THIN);
        endLeftCellStyle.setBorderTop(BorderStyle.THIN);
        endLeftCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle endMidCellStyle = wk.createCellStyle();
        endMidCellStyle.setAlignment(HorizontalAlignment.CENTER);
        endMidCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        endMidCellStyle.setWrapText(true);
        endMidCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        endMidCellStyle.setBorderLeft(BorderStyle.THIN);
        endMidCellStyle.setBorderTop(BorderStyle.THIN);
        endMidCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle endRightCellStyle = wk.createCellStyle();
        endRightCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        endRightCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        endRightCellStyle.setWrapText(true);
        endRightCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        endRightCellStyle.setBorderLeft(BorderStyle.THIN);
        endRightCellStyle.setBorderTop(BorderStyle.THIN);
        endRightCellStyle.setBorderRight(BorderStyle.THIN);
        CellStyle endLastMidCellStyle = wk.createCellStyle();
        endLastMidCellStyle.setAlignment(HorizontalAlignment.CENTER);
        endLastMidCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        endLastMidCellStyle.setWrapText(true);
        endLastMidCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        endLastMidCellStyle.setBorderLeft(BorderStyle.THIN);
        endLastMidCellStyle.setBorderTop(BorderStyle.THIN);
        endLastMidCellStyle.setBorderRight(BorderStyle.MEDIUM);
        CellStyle totalMidCellStyle = wk.createCellStyle();
        totalMidCellStyle.setAlignment(HorizontalAlignment.CENTER);
        totalMidCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        totalMidCellStyle.setWrapText(true);
        totalMidCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        totalMidCellStyle.setBorderLeft(BorderStyle.THIN);
        totalMidCellStyle.setBorderTop(BorderStyle.THIN);
        totalMidCellStyle.setBorderRight(BorderStyle.MEDIUM);
        CellStyle totalRightCellStyle = wk.createCellStyle();
        totalRightCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        totalRightCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        totalRightCellStyle.setWrapText(true);
        totalRightCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        totalRightCellStyle.setBorderLeft(BorderStyle.THIN);
        totalRightCellStyle.setBorderTop(BorderStyle.THIN);
        totalRightCellStyle.setBorderRight(BorderStyle.THIN);
        setCampaignName(sheet.getRow(0), audiencePo.getName());
        Integer startRow = sheet.getLastRowNum();
        JSONObject ruleJsonObject = JSONObject.parseObject(audiencePo.getRuleJson());
        Double testController = ruleJsonObject.getDouble("test-control");
        JSONArray segmentCampaignArray = bitmapService
                .getCampaignInfoV2(universePoList, audiencePo.getRuleJson());
        List<JSONObject> campaignList = new ArrayList<>();
        for (Object object : segmentCampaignArray) {
            JSONObject segmentCampaignObject = JSONObject.parseObject(object.toString());
            campaignList.add(segmentCampaignObject);
        }
        JSONArray segmentArray = ruleJsonObject.getJSONArray(SEGMENTS);
        Integer segmentOrder = 0;
        Integer segmentSize = segmentArray.size();
        for (Object object : segmentArray) {
            JSONObject segmentObject = JSONObject.parseObject(object.toString());
            segmentOrder++;
            Row row = sheet.createRow(++startRow);
            Cell firstRowFirstCell = row.createCell(0);
            firstRowFirstCell.setCellValue(segmentOrder);
            Cell firstRowSecondCell = row.createCell(1);
            firstRowSecondCell.setCellValue(segmentObject.getString("name"));
            Cell ruleCell = row.createCell(2);
            if (segmentOrder == segmentSize) {
                setRule(ruleCell, segmentObject, endLeftCellStyle);
            } else {
                setRule(ruleCell, segmentObject, leftCellStyle);
            }
            JSONObject campaignInfo = campaignList.get(segmentOrder - 1);
            int rwsTemp = ruleCell.getStringCellValue().split("\n").length;
            row.setHeight((short) (rwsTemp * LINE_HEIGHT * 2));
            Cell allTestCountCell = row.createCell(3);
            allTestCountCell.setCellValue(
                    campaignInfo.getLong("allTestCount") > -1 ? getFormat(
                            campaignInfo.getLong("allTestCount")) : "\\");
            Cell testCountCell = row.createCell(4);
            testCountCell.setCellValue(campaignInfo.getLong("testCount") > -1 ? getFormat(
                    campaignInfo.getLong("testCount")) : "\\");
            Cell controlCountCell = row.createCell(5);
            controlCountCell.setCellValue(
                    campaignInfo.getLong("controlCount") > -1 ? getFormat(
                            campaignInfo.getLong("controlCount")) : "\\");
            Cell percentCell = row.createCell(6);
            percentCell.setCellValue(testController != 0 ? formatPercent(1 - testController) : "\\");
            if (segmentOrder == segmentSize) {
                firstRowFirstCell.setCellStyle(endMidCellStyle);
                firstRowSecondCell.setCellStyle(endMidCellStyle);
                testCountCell.setCellStyle(endRightCellStyle);
                allTestCountCell.setCellStyle(endRightCellStyle);
                controlCountCell.setCellStyle(endRightCellStyle);
                percentCell.setCellStyle(endLastMidCellStyle);
            } else {
                firstRowFirstCell.setCellStyle(midCellStyle);
                firstRowSecondCell.setCellStyle(midCellStyle);
                testCountCell.setCellStyle(rightCellStyle);
                allTestCountCell.setCellStyle(rightCellStyle);
                controlCountCell.setCellStyle(rightCellStyle);
                percentCell.setCellStyle(lastMidCellStyle);
            }
        }
        Row totalRow = sheet.createRow(++startRow);
        //set total count row
        setTotalInfo(totalRow, segmentCampaignArray, testController, totalMidCellStyle,
                totalRightCellStyle);
        //set extra info row
        setExtraInfo(sheet, startRow + 2, ruleJsonObject, audiencePo.getUpdateTime(),
                noThinRightCellStyle);
        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(exportFile);
            wk.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            LogUtils.error(e);
            throw new AMSFileIOException();
        }
        return exportName;
    }

    private String getZip(List<String> nameList) throws AMSFileIOException {
        byte[] buffer = new byte[1024];
        String zipPath = tempFile + "/audience_zip_" + System.currentTimeMillis() + ".zip";
        File tmpZip = new File(zipPath);
        try {
            if (!tmpZip.createNewFile()) {
                throw new AMSFileIOException(Constant.ERROR_CODE_0108,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0108));
            }
        } catch (IOException e) {
            throw new AMSFileIOException(Constant.ERROR_CODE_0108,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0108));
        }
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipPath))) {
            for (String name : nameList) {
                File nameFile = new File(name);
                try (FileInputStream fis = new FileInputStream(nameFile)) {
                    out.putNextEntry(new ZipEntry(nameFile.getName()));
                    out.setEncoding("UTF-8");
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    throw new AMSFileIOException(Constant.ERROR_CODE_0108,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0108));
                } finally {
                    out.closeEntry();
                    FileSystemUtils.deleteRecursively(nameFile);
                }

            }
        } catch (IOException e) {
            throw new AMSFileIOException(Constant.ERROR_CODE_0108,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0108));
        }
        return zipPath;
    }

    private void setRule(Cell cell, JSONObject segmentObject, CellStyle cellStyle) {
        Boolean isFirst = true;
        JSONArray includeArray = segmentObject.getJSONArray(INCLUDE);
        JSONArray excludeArray = segmentObject.getJSONArray(EXCLUDE);
        StringBuilder newRule = new StringBuilder();
        if (includeArray.size() != 0) {
            newRule.append("INCLUDE \n");
            String rule = getRuleDisplay1(segmentObject.getJSONArray(INCLUDE), 1);
            newRule.append(rule);
        }
        if (excludeArray.size() != 0) {
            newRule.append("EXCLUDE \n");
            String rule = getRuleDisplay1(segmentObject.getJSONArray(EXCLUDE), 1);
            newRule.append(rule);
        }
        cell.setCellValue(new XSSFRichTextString(newRule.toString()));
        cell.setCellStyle(cellStyle);
    }

    private String getRuleDisplay1(JSONArray itemArray, Integer size) {
        StringBuilder rule = new StringBuilder();
        Boolean isFirstItem = true;
        for (Object itemObject : itemArray) {
            JSONObject itemJSONObject = JSONObject.parseObject(itemObject.toString());
            String value = getSelectDisplay(itemJSONObject);
            if (value != "") {
                if (!isFirstItem) {
                    rule.append(formatRule(itemJSONObject.getString("logic"), size, true));
                }
                isFirstItem = false;
                rule.append(formatRule(getSelectDisplay(itemJSONObject), size, false));
            }

            JSONArray childItemArray = itemJSONObject.getJSONArray("items");
            if (childItemArray != null && childItemArray.size() > 0) {
                String groupString = itemJSONObject.getString("origin");
                String logic = itemJSONObject.getString("logic");
                String newStr = "";
                for (int i = 0; i < logic.length(); i++) {
                    newStr += " ";
                }
                if ("group".equals(groupString) && !isFirstItem) {
                    rule.append(formatRule(logic, size, true));
                    rule.append("[");
                }
                rule.append(getRuleDisplay1(childItemArray, size));
                if ("group".equals(groupString) && !isFirstItem) {
                    rule.append(newStr + formatRule("]", size, true) + "\n");
                }
                isFirstItem = false;
            }
        }
        return rule.toString();
    }

    private String formatRule(String str, Integer size, Boolean isLogic) {
        if (StringUtils.equals(str, "")) {
            return "";
        }
        StringBuilder newStr = new StringBuilder();
        for (int i = 0; i < size; i++) {
            newStr.append(BLANK_STR);
        }
        if (isLogic) {
            newStr.append(StringUtils.upperCase(str));
        } else {
            newStr.append(str.substring(0, str.length() - 1));
            newStr.append("\n");
        }
        return newStr.toString();
    }

    private String getSelectDisplay(JSONObject jsonObject) {
        String path = jsonObject.getString("name");
        String taxonomyId = "";
        JSONArray itemArray = jsonObject.getJSONArray(VALUES);
        String groupString = jsonObject.getString("origin");
        if ("group".equals(groupString)) {
            return "";
        }
        if (itemArray.size() == 0) {
            return "";
        }
        int flag = 0;
        for (Object item : itemArray) {
            JSONObject itemObject = JSONObject.parseObject(item.toString());
            if (Optional.ofNullable(itemObject.getString("node")).isPresent()) {
                taxonomyId = itemObject.getString("node");
            }
            if (Optional.ofNullable(itemObject.getString("name")).isPresent()) {
                if (flag == 0) {
                    path += " = ";
                }
                path += itemObject.getString("name") + ",";
                flag++;
            } else {
                path += " ";
            }
        }
        if ("3p".equals(jsonObject.getString("dataType")) && !taxonomyId.isEmpty() && taxonomyId.split("_").length == 3) {
            String[] taxonomyIds = taxonomyId.split("_");
            try {
                String tenantId = tenantService.getTenantById(Long.valueOf(taxonomyIds[0])).getTenantId();
                Integer datasourceId = Integer.valueOf(taxonomyIds[1]);
                path = dataSourceAPI.getParentPathByTaxonomyId(taxonomyId, tenantId, datasourceId) + " / " + path;
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }
        return path;
    }

    private void setTotalInfo(Row totalRow, JSONArray segmentArray, Double testController,
                              CellStyle midCellStyle, CellStyle rightCellStyle) {
        Integer testCount = 0;
        Integer allTestCount = 0;
        Integer controlCount = 0;
        for (Object object : segmentArray) {
            JSONObject segmentObject = JSONObject.parseObject(object.toString());
            testCount += segmentObject.getInteger("testCount");
            allTestCount += segmentObject.getInteger("allTestCount");
            controlCount += segmentObject.getInteger("controlCount");
        }

        Cell fourthCell = totalRow.createCell(3);
        fourthCell.setCellValue(allTestCount > -1 ? getFormat(allTestCount) : "\\");
        fourthCell.setCellStyle(rightCellStyle);

        Cell fifthCell = totalRow.createCell(4);
        fifthCell.setCellValue(testCount > -1 ? getFormat(testCount) : "\\");
        fifthCell.setCellStyle(rightCellStyle);

        Cell sixCell = totalRow.createCell(5);
        sixCell.setCellValue(controlCount > -1 ? getFormat(controlCount) : "\\");
        sixCell.setCellStyle(rightCellStyle);

        Cell sevenCell = totalRow.createCell(6);
        sevenCell.setCellValue(testController != 0 ? formatPercent(1 - testController) : "\\");
        sevenCell.setCellStyle(midCellStyle);
    }

    private void setExtraInfo(XSSFSheet sheet, Integer startRow, JSONObject ruleJsonObject,
                              Date updateDate, CellStyle rightCellStyle) {
        Row firstRow = sheet.createRow(startRow++);
        firstRow.createCell(1).setCellValue("Addressable, Linear, or Both?");
        firstRow.createCell(2).setCellValue("Addressable");
        Row secondRow = sheet.createRow(startRow++);
        secondRow.createCell(1).setCellValue("De-duped?");
        Boolean isGross = ruleJsonObject.getBoolean("rm-duplicates");
        if (isGross) {
            secondRow.createCell(2).setCellValue("Yes");
        } else {
            secondRow.createCell(2).setCellValue("No");
        }
        Row thirdRow = sheet.createRow(startRow++);
        thirdRow.createCell(1).setCellValue("Cap:");
        Integer capCount = ruleJsonObject.getInteger("cap");
        if (capCount == 0) {
            thirdRow.createCell(2).setCellValue("No");
        } else {
            Cell thirdRowCell = thirdRow.createCell(2);
            thirdRowCell.setCellValue(getFormat(capCount));
            thirdRowCell.setCellStyle(rightCellStyle);
        }
        Row fourthRow = sheet.createRow(startRow++);
        fourthRow.createCell(1).setCellValue("Audience Update Date");
        fourthRow.createCell(2).setCellValue(getDateString(updateDate));
    }

    private void setCampaignName(Row row, String campaignName) {
        Cell cell = row.getCell(1);
        String value = cell.getStringCellValue();
        value = value.replace("?", campaignName);
        cell.setCellValue(value);
    }

    private JSONArray getAllDataStoreNode(JSONObject segmentObj) {
        JSONArray array = new JSONArray();
        Optional<JSONArray> includeArray = Optional
                .ofNullable(segmentObj.getJSONArray(INCLUDE));
        if (includeArray.isPresent()) {
            for (int i = 0; i < includeArray.get().size(); i++) {
                if (Optional
                        .ofNullable(includeArray.get().getJSONObject(i).getBoolean(INFO_BASE_FLAG))
                        .isPresent() ?
                        includeArray.get().getJSONObject(i).getBoolean(INFO_BASE_FLAG) : false) {
                    array.addAll(includeArray.get().getJSONObject(i).getJSONArray(VALUES));
                }
            }
        }
        Optional<JSONArray> excludeArray = Optional
                .ofNullable(segmentObj.getJSONArray(EXCLUDE));
        if (excludeArray.isPresent()) {
            for (int i = 0; i < excludeArray.get().size(); i++) {
                if (Optional
                        .ofNullable(excludeArray.get().getJSONObject(i).getBoolean(INFO_BASE_FLAG))
                        .isPresent() ?
                        excludeArray.get().getJSONObject(i).getBoolean(INFO_BASE_FLAG) : false) {
                    array.addAll(excludeArray.get().getJSONObject(i).getJSONArray(VALUES));
                }
            }
        }
        return array;
    }

    private DataStoreVo parseNodeToDataStoreVo(JSONArray jsonArray, Long campaignId, Long tenantId,
                                               Long destinationId)
            throws AMSInvalidInputException {
        DataStoreVo dataStoreVo = new DataStoreVo();
        String tenantName = tenantService.getTenantById(tenantId).getName();
        String destinationName = universeService.getUniverseById(destinationId).getUniverseName();
        long[] segments = new long[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            segments[i] = jsonArray.getJSONObject(i).getLong("node");
        }
        dataStoreVo.setPackageId(campaignId);
        dataStoreVo.setSegments(segments);
        dataStoreVo.setTenantName(tenantName);
        dataStoreVo.setDestinationName(destinationName);
        return dataStoreVo;
    }

    @Override
    public List<OwnerAndDataType> getOwnerTypeV3(List<String> ownerList, List<NodeDTO> nodeDTOList,
                                                 List<SegmentDTO> segmentDTOList, Long tenantId)
            throws AMSException {
        List<String> dataTypeList = new ArrayList<>();
        List<OwnerAndDataType> ownerAndDataTypeList = new ArrayList<>();
        if (!Optional.ofNullable(ownerList).isPresent()) {
            return ownerAndDataTypeList;
        }
        TenantPo treeTenantPo = tenantService.getTenantById(tenantId);
        TenantPo[] tenantPos = new TenantPo[]{treeTenantPo};
        List<VersionPo> versionPoList = versionPoJPA
                .findAllByTenantPoInAndOperationFlag(tenantPos, TemplateStatusType.ACTIVE);
        List<OwnerAndNodeDTO> ownerAndNodeDTOList = new ArrayList<>();
        if (Optional.ofNullable(segmentDTOList).isPresent()) {
            ownerAndNodeDTOList = parseSegment(segmentDTOList, ownerAndNodeDTOList);
        }
        for (OwnerAndNodeDTO ownerAndNodeDTO : ownerAndNodeDTOList) {
            nodeDTOList.addAll(ownerAndNodeDTO.getNodeDTOList());
            ownerList.addAll(ownerAndNodeDTO.getOwnerList());
            dataTypeList.addAll(ownerAndNodeDTO.getDataTypeList());
            ownerAndDataTypeList.addAll(ownerAndNodeDTO.getOwnerAndDataTypeList());
        }
        if (Optional.ofNullable(nodeDTOList).isPresent()) {
            for (VersionPo versionPo : versionPoList) {
                List<OwnerAndDataType> rtnMap = taxonomyAPI
                        .getAttributeOwnerAndDataTypeByNodeIdAndName(String.valueOf(versionPo.getId()),
                                nodeDTOList);
                ownerAndDataTypeList.addAll(rtnMap);
            }
        }
        return ownerAndDataTypeList;
    }

    @Override
    public List<DataTypeAndPriceAndOwnerVO> listNodeInfoByTenantIdAndTaxonomyIdList(Long tenantId, List<String>
            taxonomyIdList)
            throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        VersionPo versionPo = versionService.getActiveVersionByTenant(tenantPo);
        List<DataTypeAndPriceAndOwnerVO> dataTypeAndPriceAndOwnerVOList = new ArrayList<>();
        dataTypeAndPriceAndOwnerVOList.addAll(taxonomyAPI
                .listDataTypeAndPriceAndOwnerByTaxonomyIdList(String.valueOf(versionPo.getId()), taxonomyIdList));
        return dataTypeAndPriceAndOwnerVOList;
    }

    @Override
    public List<DataStoreNodeSortByTenantVO> listDataStoreNodesByDate(Date startDate, Date endDate) throws
            AMSException {
        List<AudiencePo> audiencePoList = audiencePoJPA.findAllByAudienceTypeAndCreatedTimeBetween(FolderType
                .CAMPAIGN, startDate, endDate);
        List<String> taxonomyIdList = new ArrayList<>();
        for (AudiencePo campaign : audiencePoList) {
            taxonomyIdList.addAll(parseCampaignRuleJson(campaign));
        }
        Map<Long, List<String>> tenantMap = new HashMap<>();
        for (String taxonomyId : taxonomyIdList) {
            String[] taxonomyIdSplit = taxonomyId.split(Constant.UNDER_LINE);
            if (taxonomyIdSplit.length != 3) {
                continue;
            }
            try {
                Long tenantId = Long.valueOf(taxonomyIdSplit[0]);
                if (Optional.ofNullable(tenantMap.get(tenantId)).isPresent()) {
                    List<String> taxonomyIds = tenantMap.get(tenantId);
                    taxonomyIds.add(taxonomyId);
                    tenantMap.put(tenantId, taxonomyIds);
                } else {
                    tenantMap.put(tenantId, Lists.newArrayList(taxonomyId));
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        List<DataStoreNodeSortByTenantVO> dataStoreNodeSortByTenantVOList = new ArrayList<>();
        for (Long tenantId : tenantMap.keySet()) {
            DataStoreNodeSortByTenantVO dataStoreNodeSortByTenantVO = new DataStoreNodeSortByTenantVO();
            TenantPo tenantPo = tenantService.getTenantById(tenantId);
            dataStoreNodeSortByTenantVO.setTenantName(tenantPo.getName());
            dataStoreNodeSortByTenantVO.setDataStoreNodeList(dataSourceAPI.listNodeInfoByNodeIds(tenantPo.getTenantId
                    (), tenantMap.get(tenantId)));
            dataStoreNodeSortByTenantVOList.add(dataStoreNodeSortByTenantVO);
        }
        return dataStoreNodeSortByTenantVOList;
    }

    @Override
    public void updateSegmentDistributionFlag(Long tenantId, Long audienceId, Boolean distributionFlag) throws
            AMSInvalidInputException {
        AudiencePo audiencePo = getAudiencePoByIdAndTenantId(audienceId, tenantId);
        audiencePo.setUpdateTime(new Date());
        audiencePo.setDistributionFlag(distributionFlag);
        audiencePoJPA.save(audiencePo);
        String folderId = String.valueOf(audiencePo.getFolderPo().getId());
        String folderIdPaths = folderPoJPA.getParentList(folderId);
        List<Long> folderIdList = Arrays.asList(folderIdPaths.substring(2).split(",")).stream()
                .map(s1 -> Long.parseLong(s1.trim())).collect(Collectors.toList());
        List<FolderPo> targetFolderPoList = folderPoJPA.getFolderPoByIdIn(folderIdList);
        targetFolderPoList.forEach(folderPo2 -> folderPo2.setUpdateTime(new Date()));
        folderPoJPA.save(targetFolderPoList);
    }

    @Override
    public void updateSegmentStatus(String texId, String status, AudienceCallbackParam param) {
        AudiencePo audiencePo = audiencePoJPA.findAudiencePoByTaxonomyId(texId);
        if (Constant.TEST_CONTROL_SUCCESS.equals(status)) {
            audiencePo.setTestCount(param.getTestCount());
            audiencePo.setControlCount(param.getControlCount());
            if (SegmentStatusType.SEGMENT_RUNNING.equals(audiencePo.getSegmentStatusType())) {
                AudienceDistributeJobPo audienceDistributeJobPo =
                        audienceDistributeJobPoJPA.findFirstByAudienceIdOrderByUpdateTimeDesc(audiencePo.getId());
                if (audienceDistributeJobPo == null) {
                    audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_NEW);
                } else {
                    audiencePo.setSegmentStatusType(audienceDistributeJobPo.getStatus());
                }
            }
        } else if (Constant.TEST_CONTROL_FAILED.equals(status)) {
            audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_FAILED);
        } else if (Constant.TEST_CONTROL_RUNNING.equals(status)) {
            audiencePo.setTestCount(0L);
            audiencePo.setControlCount(0L);
            audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_RUNNING);
        }
        audiencePoJPA.save(audiencePo);
    }

    private AudiencePo getAudiencePoByIdAndTenantId(Long audienceId, Long tenantId) throws AMSInvalidInputException {
        return Optional.ofNullable(audiencePoJPA.findByIdAndTenantId(audienceId, tenantId))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0221,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0221)));
    }


    private List<String> parseCampaignRuleJson(AudiencePo audience) {
        List<String> taxonomyIdList = new ArrayList<>();
        JSONObject rule = JSON.parseObject(audience.getRuleJson());
        JSONArray segments = rule.getJSONArray(SEGMENTS);
        JSONArray conditionArray = new JSONArray();
        List<SegmentDTO> segmentDTOList = new ArrayList<>();
        for (int i = 0; i < segments.size(); i++) {
            JSONObject segment = segments.getJSONObject(i);
            conditionArray.addAll(segment.getJSONArray(INCLUDE)
                    .fluentAddAll(segment.getJSONArray(EXCLUDE)));
        }
        segmentDTOList = parseRuleForThirdPartyNode(conditionArray, segmentDTOList, taxonomyIdList);
        while (!segmentDTOList.isEmpty()) {
            JSONArray childConditionArray = parseSegmentDTOListToCludeJson(segmentDTOList);
            segmentDTOList = parseRuleForThirdPartyNode(childConditionArray, new ArrayList<>(), taxonomyIdList);
        }
        return taxonomyIdList;
    }

    private JSONArray parseSegmentDTOListToCludeJson(List<SegmentDTO> segmentDTOList) {
        JSONArray conditionArray = new JSONArray();
        for (SegmentDTO segmentDTO : segmentDTOList) {
            AudiencePo campaign = audiencePoJPA.findOne(segmentDTO.getCampaignId());
            if (null == campaign) {
                continue;
            }
            JSONObject rule = JSON.parseObject(campaign.getRuleJson());
            JSONArray segments = rule.getJSONArray(SEGMENTS);
            for (String segmentId : segmentDTO.getSegmentIdList()) {
                for (int i = 0; i < segments.size(); i++) {
                    JSONObject segment = segments.getJSONObject(i);
                    if (StringUtils.equals(segmentId, segment.getString("id"))) {
                        JSONArray condition = segment.getJSONArray(INCLUDE)
                                .fluentAddAll(segment.getJSONArray(EXCLUDE));
                        conditionArray.addAll(condition);
                    }
                }
            }
        }
        return conditionArray;
    }

    private List<SegmentDTO> parseRuleForThirdPartyNode(JSONArray array, List<SegmentDTO> segmentDTOList,
                                                        List<String> taxonomyIdList) {
        if (array.isEmpty()) {
            return Collections.emptyList();
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (StringUtils.equals(obj.getString("origin"), "tree")) {
                String dataType = obj.getString("dataType");
                if (StringUtils.isNotBlank(dataType) && StringUtils.equals(dataType, "3p")) {
                    JSONArray valueArray = obj.getJSONArray(VALUES);
                    if (Optional.ofNullable(valueArray).isPresent()) {
                        for (int j = 0; j < valueArray.size(); j++) {
                            taxonomyIdList.add(valueArray.getJSONObject(j).getString("node"));
                        }
                    }
                }
            } else if (StringUtils.equals(obj.getString("origin"), "segment")) {
                SegmentDTO segmentDTO = new SegmentDTO();
                JSONArray values = obj.getJSONArray(VALUES);
                List<String> segmentIdList = new ArrayList<>();
                for (int j = 0; j < values.size(); j++) {
                    String node = values.getJSONObject(j).getString("node");
                    if (Optional.ofNullable(node).isPresent()) {
                        node = node.replace("_test", "").replace("_control", "");
                        segmentIdList.add(node);
                    }
                }
                segmentDTO.setCampaignId(obj.getLong(CAMPAIGN_ID));
                segmentDTO.setSegmentIdList(segmentIdList);
                segmentDTOList.add(segmentDTO);
            } else if (StringUtils.equals(obj.getString("origin"), "group")) {
                JSONArray items = obj.getJSONArray("items");
                parseRuleForThirdPartyNode(items, segmentDTOList, taxonomyIdList);
            }
        }
        return segmentDTOList;
    }

    private void createOrUpdateTenantAndUniverseBindings(Long tenantId, String universeIds, String createdBy) {
        TenantAndUniverseKey tenantAndUniverseKey = new TenantAndUniverseKey();
        tenantAndUniverseKey.setUsername(createdBy);
        tenantAndUniverseKey.setTenantId(tenantId);
        TenantAndUniversePo tenantAndUniversePo = new TenantAndUniversePo();
        tenantAndUniversePo.setUniverseIds(universeIds);
        tenantAndUniversePo.setId(tenantAndUniverseKey);
        tenantAndUniversePoJPA.save(tenantAndUniversePo);
    }


    private Boolean isDuplicateAudience(FolderType folderType, Long tenantId, String audienceName) {
        AudiencePo audiencePo = audiencePoJPA.findAudiencePoByAudienceTypeAndTenantIdAndName(folderType,
                tenantId, audienceName);
        if (Optional.ofNullable(audiencePo).isPresent()) {
            return true;
        }
        return false;
    }


    private List<VersionPo> getActiveVersionList(Long tenantId) throws AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        TenantPo[] tenantPos = new TenantPo[]{tenantPo};
        List<VersionPo> versionPoList = versionPoJPA
                .findAllByTenantPoInAndOperationFlag(tenantPos, TemplateStatusType.ACTIVE);
        return versionPoList;
    }

    @Override
    public void audienceShare(AudiencesShareDTO audienceSharesDTO) throws AMSException {
        List<Long> audienceIdList = audienceSharesDTO.getAudienceIdList();
        List<AudiencePo> audiencePoList = audiencePoJPA.findAll(audienceIdList);
        List<AudienceShareDTO> audienceShareDTOList = new ArrayList<>();
        for (AudiencePo audiencePo : audiencePoList) {
            AudienceShareDTO audienceShareDTO = new AudienceShareDTO();
            audienceShareDTO.setAudienceId(audiencePo.getId());
            audienceShareDTO.setName(audiencePo.getName());
            audienceShareDTO.setTaxonomyId(audiencePo.getTaxonomyId());
            audienceShareDTOList.add(audienceShareDTO);
        }
        List<FolderPo> folderPoList = folderPoJPA.getFolderPoByIdIn(audienceSharesDTO.getFolderIdList());
        if (!folderPoList.isEmpty()) {
            audiencePoList = audiencePoJPA.findByFolderPoIn(folderPoList);
            for (AudiencePo audiencePo : audiencePoList) {
                AudienceShareDTO audienceShareDTO = new AudienceShareDTO();
                audienceShareDTO.setAudienceId(audiencePo.getId());
                audienceShareDTO.setName(audiencePo.getName());
                audienceShareDTO.setTaxonomyId(audiencePo.getTaxonomyId());
                audienceShareDTOList.add(audienceShareDTO);
            }
        }
        audienceSharesDTO.setShareAudienceDTOList(audienceShareDTOList);
        audienceSharesDTO.setPushFileFlag(true);
        dataSourceAPI.createSharedDataSource(audienceSharesDTO);
    }
}