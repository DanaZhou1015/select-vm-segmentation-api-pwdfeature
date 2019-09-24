package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSResouceRequestException;
import com.acxiom.ams.mapper.AudiencePoMapper;
import com.acxiom.ams.mapper.FolderPoMapper;
import com.acxiom.ams.mapper.FolderPoToVoMapper;
import com.acxiom.ams.model.dto.FolderAndAudience;
import com.acxiom.ams.model.dto.FolderAndCampaign;
import com.acxiom.ams.model.dto.FolderParam;
import com.acxiom.ams.model.dto.PermissionDTO;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.io.FolderIo;
import com.acxiom.ams.model.po.*;
import com.acxiom.ams.model.vo.AllowFlagVo;
import com.acxiom.ams.model.vo.AudienceAndFolderVo;
import com.acxiom.ams.model.vo.FolderVo;
import com.acxiom.ams.model.vo.PermissionVo;
import com.acxiom.ams.repository.*;
import com.acxiom.ams.service.*;
import com.acxiom.ams.util.Constant;
import com.acxiom.ams.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 15:04 12/5/2017
 */
@Service
public class FolderServiceImpl implements FolderService {

    @Autowired
    FolderPoJPA folderJPA;
    @Autowired
    AudiencePoJPA audiencePoJPA;
    @Autowired
    AudiencePoMapper audiencePoMapper;
    @Autowired
    FolderPoMapper folderPoMapper;
    @Autowired
    FolderPoToVoMapper folderPoToVoMapper;
    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Autowired
    AudiencePoService audiencePoService;
    @Autowired
    TenantPoJPA tenantPoJPA;
    @Autowired
    UniversePoJPA universePoJPA;
    @Autowired
    TenantService tenantService;
    @Autowired
    UniverseService universeService;
    @Autowired
    ServiceAPI.BitmapAPI bitmapAPI;
    @Autowired
    AudienceDistributeJobPoJPA audienceDistributeJobPoJPA;
    @Autowired
    UniverseActivityLogPoJPA universeActivityLogPoJPA;

    @Override
    public List<AudienceAndFolderVo> getFolderListByTenantId(long folderId, long tenantId,
                                                             Boolean isSort, FolderType folderType)
            throws AMSInvalidInputException {
        List<AudienceAndFolderVo> audienceAndFolderVoList = new ArrayList<>();
        List<AudiencePo> audiencePoList;
        List<FolderPo> folderPoList;
        if (folderId == 1 || folderId == 2 || folderId == 3) {
            FolderPo folderPo = folderJPA.getFolderPoByIdAndFolderType(folderId, folderType);
            if (folderPo == null) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205));
            }
            audiencePoList = folderPo.getAudiencePoList();
            if (audiencePoList != null && !audiencePoList.isEmpty()) {
                audiencePoList = audiencePoList.stream()
                        .filter(audiencePo -> (tenantId == audiencePo.getTenantId())).collect(
                                Collectors.toList());
            }
            folderPoList = folderJPA.getFolderPoByParentFolderIdAndTenantIdAndFolderType(
                    folderPo.getId(), tenantId, folderType);
            audienceAndFolderVoList.addAll(audiencePoMapper.map(audiencePoList));
            audienceAndFolderVoList.addAll(folderPoMapper.map(folderPoList));
            if (isSort) {
                Collections.sort(audienceAndFolderVoList);
            }
            return audienceAndFolderVoList;
        }
        FolderPo folderPo = folderJPA
                .getFolderPoByIdAndTenantIdAndFolderType(folderId, tenantId, folderType);
        if (folderPo == null) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205));
        }
        audiencePoList = folderPo.getAudiencePoList();
        folderPoList = folderJPA
                .getFolderPoByParentFolderIdAndTenantIdAndFolderType(folderId, tenantId, folderType);
        audienceAndFolderVoList.addAll(audiencePoMapper.map(audiencePoList));
        audienceAndFolderVoList.addAll(folderPoMapper.map(folderPoList));
        if (isSort) {
            Collections.sort(audienceAndFolderVoList);
        }
        //     List<UniversePo> universePoList = universePoJPA.findAllByTenantId(tenantId);
        //      Map<Long, String> universeMap = new HashMap<>();
        //      universePoList.forEach(universe -> universeMap.put(universe.getId(), universe.getUniverseName()));
//        audienceAndFolderVoList.forEach(audienceAndFolderVo ->
//                audienceAndFolderVo.setUniverseName(universeMap.get(audienceAndFolderVo.getDestinationId())));
        return audienceAndFolderVoList;
    }

    @Override
    public List<FolderPo> getParentFolder() throws AMSResouceRequestException {
        List<FolderPo> folderPoList = folderJPA.getFolderPoByParentFolderId((long) 0);
        if (folderPoList == null || folderPoList.isEmpty()) {
            throw new AMSResouceRequestException();
        }
        folderPoList = folderPoList.stream().filter(folderPo -> !folderPo.getFolderType().equals(FolderType.CAMPAIGN))
                .collect(Collectors.toList());
        return folderPoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFolderAndAudience(FolderAndAudience folderAndAudience)
            throws AMSInvalidInputException {
        String owner = folderAndAudience.getOwner();
        List<AudiencePo> audiencePoList = new ArrayList<>();
        if (folderAndAudience.getAudienceIdList() != null) {
            audiencePoList = audiencePoService
                    .getAudiencePoByIds(folderAndAudience.getAudienceIdList());
        }
        List<FolderPo> folderPoList = new ArrayList<>();
        if (folderAndAudience.getFolderIdList() != null) {
            folderPoList = folderJPA.getFolderPoByIdIn(folderAndAudience.getFolderIdList());
        }
        if (folderPoList.isEmpty() && audiencePoList.isEmpty()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205));
        }
        String strIds = StringUtils.join(folderAndAudience.getFolderIdList(), ",");
        List<AudienceAndFolderVo> audienceAndFolderVoList = new ArrayList<>();
        List<AudiencePo> audiencePos = getSegmentListByFolderIds(strIds);
        audiencePoList.addAll(audiencePos);
        audienceAndFolderVoList.addAll(folderPoMapper.map(folderPoList));
        audienceAndFolderVoList.addAll(audiencePoMapper.map(audiencePoList));
        if (!isPermission(audienceAndFolderVoList, owner)) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0214,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0214));
        }
        List<FolderPo> folderPoChildList = folderJPA.getChildList(strIds);
        Long[] ids = new Long[folderPoChildList.size()];
        for (int i = 0; i < folderPoChildList.size(); i++) {
            ids[i] = folderPoChildList.get(i).getId();
        }
        folderJPA.deleteFolderPoByIdIn(ids);
        audiencePoJPA.deleteAudiencePoByIdIn(folderAndAudience.getAudienceIdList());
    }

    @Override
    public FolderVo createFolder(FolderIo folderIo) throws AMSInvalidInputException {
        String folderName = folderIo.getName();
        long tenantId = folderIo.getTenantId();
        List<FolderPo> folderPoList = folderJPA
                .getByParentFolderIdAndTenantId(folderIo.getParentId(), tenantId);
        if (Optional.ofNullable(folderPoList).isPresent()) {
            long size = folderPoList.stream().filter(folderPo -> {
                if (StringUtils.equals(folderPo.getFolderName(), folderName)) {
                    return true;
                }
                return false;
            }).count();
            if (size > 0) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0207,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0207));
            }
        }
        FolderPo folderPo = new FolderPo();
        if (folderIo.getParentId() == null) {
            folderIo.setParentId((long) 1);
            if (folderIo.getFolderType().equals(FolderType.LOOKALIKE_GROUP)) {
                folderIo.setParentId((long) 2);
            }
        }
        folderPo.setFolderName(folderName);
        folderPo.setFolderType(folderIo.getFolderType());
        folderPo.setParentFolderId(folderIo.getParentId());
        folderPo.setTenantId(folderIo.getTenantId());
        folderPo.setCreatedBy(folderIo.getCreatedBy());
        folderJPA.save(folderPo);
        return folderPoToVoMapper.map(folderPo);
    }

    @Override
    public void updateFolder(long folderId, FolderParam folderParam)
            throws AMSInvalidInputException {
        FolderPo folderPo = Optional.ofNullable(folderJPA.getFolderPoById(folderId))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205)));
        String folderName = folderParam.getNewFolderName();
        if (!StringUtils.equals(folderName, folderPo.getFolderName())) {
            List<FolderPo> folderPoList = folderJPA
                    .getByParentFolderIdAndTenantId(folderPo.getParentFolderId(),
                            folderPo.getTenantId());
            if (Optional.ofNullable(folderPoList).isPresent()) {
                long size = folderPoList.stream().filter(folderPo1 -> {
                    if (StringUtils.equals(folderPo1.getFolderName(), folderName)) {
                        return true;
                    }
                    return false;
                }).count();
                if (size > 0) {
                    throw new AMSInvalidInputException(Constant.ERROR_CODE_0207,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0207));
                }
            }
            String owner = folderParam.getOwner();
            if (!StringUtils.equals(owner, folderPo.getCreatedBy())) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0206,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0206));
            }
            folderPo.setFolderName(folderName);
            folderJPA.save(folderPo);
        }
    }

    @Override
    public int getSegmentCountByFolderIds(String folderIds) {
        return audiencePoJPA.getSegmentCountByFolderId(folderIds);
    }

    @Override
    public PermissionVo getSegmentPermissionByFolderIds(PermissionDTO permissionDTO) {
        List<AudiencePo> audiencePoList = audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(permissionDTO.getFolderIdList(), ","));
        PermissionVo permissionVo = new PermissionVo(audiencePoList.size(), (long) -1, false,
                false, false, false, false);
        if (audiencePoList.size() == 1) {
            permissionVo.setCopy(true);
            permissionVo.setId(audiencePoList.get(0).getId());
            if (StringUtils.equals(audiencePoList.get(0).getCreatedBy(), permissionDTO.getUsername())) {
                permissionVo.setEdit(true);
            }
        }
        return permissionVo;
    }

    @Override
    public List<AudiencePo> getSegmentListByFolderIds(String folderIds) {
        List<AudiencePo> audiencePoList = audiencePoJPA.getSegmentListByFolderId(folderIds);
        return audiencePoList;
    }


    private Boolean isPermission(List<AudienceAndFolderVo> audienceAndFolderVoList,
                                 String owner) {
        long size = 0;
        if (audienceAndFolderVoList != null && !audienceAndFolderVoList.isEmpty()) {
            long folderSize = audienceAndFolderVoList.size();
            size = audienceAndFolderVoList.stream()
                    .filter(folderPo -> StringUtils.equals(folderPo.getCreatedBy(), owner)).count();
            if (folderSize != size) {
                return false;
            }
        }
        return true;
    }


    // v2
    @Override
    public List<AudienceAndFolderVo> getFolderListByTenantIdV2(long folderId, long tenantId,
                                                               FolderType folderType)
            throws AMSException {
        List<AudienceAndFolderVo> audienceAndFolderVoList = new ArrayList<>();
        List<AudiencePo> audiencePoList;
        List<FolderPo> folderPoList;
        List<UniversePo> universePoList = universeService.listUniverseByTenantId(tenantId, "");
        Map<Long, UniversePo> universeMap = new HashMap<>();
        universePoList.forEach(universePo -> universeMap.put(universePo.getId(), universePo));
        if (folderId == 3) {
            FolderPo folderPo = Optional
                    .ofNullable(folderJPA.getFolderPoByIdAndFolderType(folderId, folderType))
                    .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205)));
            audiencePoList = folderPo.getAudiencePoList();
            if (null != audiencePoList && !audiencePoList.isEmpty()) {
                audiencePoList = audiencePoList.stream()
                        .filter(audiencePo -> (tenantId == audiencePo.getTenantId())).collect(
                                Collectors.toList());
            }
            folderPoList = folderJPA.getFolderPoByParentFolderIdAndTenantIdAndFolderType(
                    folderPo.getId(), tenantId, folderType);
        } else {
            FolderPo folderPo = Optional.ofNullable(
                    folderJPA.getFolderPoByIdAndTenantIdAndFolderType(folderId, tenantId, folderType))
                    .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205)));
            audiencePoList = folderPo.getAudiencePoList();
            folderPoList = folderJPA
                    .getFolderPoByParentFolderIdAndTenantIdAndFolderType(folderId, tenantId, folderType);
        }
        //refreshAllAudienceWhenThresholdChanged(audiencePoList, universeMap);
        audienceAndFolderVoList.addAll(audiencePoMapper.map(audiencePoList));
        audienceAndFolderVoList.addAll(folderPoMapper.map(folderPoList));
        Collections.sort(audienceAndFolderVoList);
        return audienceAndFolderVoList;
    }

    @Override
    public List<FolderPo> getFoldersByTenantIdV2(long folderId, long tenantId,
                                                 FolderType folderType) throws AMSInvalidInputException {
        List<FolderPo> folderPoList;
        if (folderId == 3) {
            FolderPo folderPo = Optional
                    .ofNullable(folderJPA.getFolderPoByIdAndFolderType(folderId, folderType))
                    .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205)));
            folderPoList = folderJPA.getFolderPoByParentFolderIdAndTenantIdAndFolderType(
                    folderPo.getId(), tenantId, folderType);
            return folderPoList;
        }
        folderPoList = Optional
                .ofNullable(
                        folderJPA.getFolderPoByParentFolderIdAndTenantIdAndFolderType(folderId, tenantId,
                                folderType))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205)));
        return folderPoList;
    }

    @Override
    public List<FolderPo> getParentFolderV2() throws AMSInvalidInputException {
        List<FolderPo> folderPoList = Optional.ofNullable(folderJPA
                .getFolderPoByParentFolderIdAndFolderType((long) 0, FolderType.CAMPAIGN))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0224,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0224)));
        return folderPoList;
    }

    @Override
    public List<AudienceAndFolderVo> listCampaignByTenantId(long folderId, long tenantId, FolderType folderType)
            throws AMSInvalidInputException {
        List<AudienceAndFolderVo> audienceAndFolderVoList = new ArrayList<>();
        List<AudiencePo> audiencePoList;
        List<FolderPo> folderPoList;
        if (folderId == 3) {
            FolderPo folderPo = Optional
                    .ofNullable(folderJPA.getFolderPoByIdAndFolderType(folderId, folderType))
                    .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                            errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205)));
            audiencePoList = folderPo.getAudiencePoList();
            if (Optional.ofNullable(audiencePoList).isPresent()) {
                audiencePoList = audiencePoList.stream()
                        .filter(audiencePo -> (tenantId == audiencePo.getTenantId()))
                        .collect(Collectors.toList());
            }
            folderPoList = folderJPA.getFolderPoByParentFolderIdAndTenantIdAndFolderType(
                    folderPo.getId(), tenantId, folderType);
            audienceAndFolderVoList.addAll(audiencePoMapper.map(audiencePoList));
            audienceAndFolderVoList.addAll(folderPoMapper.map(folderPoList));
            Collections.sort(audienceAndFolderVoList);
            return audienceAndFolderVoList;
        }
        FolderPo folderPo = Optional.ofNullable(
                folderJPA.getFolderPoByIdAndTenantIdAndFolderType(folderId, tenantId, folderType))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205)));
        audiencePoList = folderPo.getAudiencePoList();
        folderPoList = folderJPA
                .getFolderPoByParentFolderIdAndTenantIdAndFolderType(folderId, tenantId, folderType);
        audienceAndFolderVoList.addAll(audiencePoMapper.map(audiencePoList));
        audienceAndFolderVoList.addAll(folderPoMapper.map(folderPoList));
        Collections.sort(audienceAndFolderVoList);
        return audienceAndFolderVoList;
    }

    @Override
    public PermissionVo checkPermission(PermissionDTO permissionDTO) {
        List<AudiencePo> audiencePoList = audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(permissionDTO.getFolderIdList(), ","));
        audiencePoList.addAll(audiencePoJPA.findAll(permissionDTO.getAudienceIdList()));
        PermissionVo permissionVo = new PermissionVo(audiencePoList.size(), (long) -1, false,
                false, false, false, false);
        if (Optional.ofNullable(audiencePoList).isPresent()) {
            if (audiencePoList.size() == 1) {
                permissionVo.setCopy(true);
                permissionVo.setId(audiencePoList.get(0).getId());
                if (StringUtils
                        .equals(audiencePoList.get(0).getCreatedBy(), permissionDTO.getUsername())
                        && (SegmentStatusType.SEGMENT_NEW.equals(audiencePoList.get(0).getSegmentStatusType())
                        || SegmentStatusType.SEGMENT_FAILED.equals(audiencePoList.get(0).getSegmentStatusType()))) {
                    permissionVo.setEdit(true);
                }
            }
            long size = audiencePoList.stream().filter(audiencePo -> (
                    audiencePo.getSegmentStatusType().equals(SegmentStatusType.SEGMENT_DISTRIBUTING)
                            || audiencePo.getSegmentStatusType().equals(SegmentStatusType.SEGMENT_RUNNING)
                            || audiencePo.getSegmentStatusType().equals(SegmentStatusType.SEGMENT_FAILED)))
                    .collect(Collectors.toList()).size();
            if (size == 0) {
                permissionVo.setDistribute(true);
            }
            size = audiencePoList.stream().filter(audiencePo -> (audiencePo.getSegmentStatusType().equals(SegmentStatusType.SEGMENT_RUNNING)))
                    .collect(Collectors.toList()).size();
            if (size == 0) {
                permissionVo.setRefresh(true);
            }
            size = audiencePoList.stream().filter(audiencePo -> (audiencePo.getSegmentStatusType().equals(SegmentStatusType.SEGMENT_RUNNING)))
                    .collect(Collectors.toList()).size();
            if (size == 0) {
                permissionVo.setDelete(true);
            }
        }
        return permissionVo;
    }

    @Override
    public PermissionVo checkPermissionV2(PermissionDTO permissionDTO) {
        List<AudiencePo> audiencePoList = audiencePoJPA.getSegmentListByFolderId(StringUtils.join(permissionDTO
                .getFolderIdList(), ","));
        audiencePoList.addAll(audiencePoJPA.findAll(permissionDTO.getAudienceIdList()));
        PermissionVo permissionVo = new PermissionVo(audiencePoList.size(), (long) -1, false, false, false, false,
                false);
        if (Optional.ofNullable(audiencePoList).isPresent()) {
            if (audiencePoList.size() == 1) {
                permissionVo.setCopy(true);
                permissionVo.setId(audiencePoList.get(0).getId());
                permissionVo.setEdit(StringUtils.equals(audiencePoList.get(0).getCreatedBy(), permissionDTO
                        .getUsername()) && SegmentStatusType.CAMPAIGN_SAVED.equals(audiencePoList.get(0)
                        .getSegmentStatusType()));
            }
            long size = audiencePoList.stream().filter(audiencePo -> {
                if (SegmentStatusType.CAMPAIGN_PREPARING.equals(audiencePo.getSegmentStatusType()) ||
                        SegmentStatusType.CAMPAIGN_DISTRIBUTING.equals(audiencePo.getSegmentStatusType()) ||
                        SegmentStatusType.CAMPAIGN_DISTRIBUTED.equals(audiencePo.getSegmentStatusType()) ||
                        SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED.equals(audiencePo.getSegmentStatusType()) ||
                        SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUEST_REJECTED.equals(audiencePo.getSegmentStatusType()) ||
                        audiencePo.getFrozenCount() == 0) {
                    return true;
                } else {
                    if (!SegmentStatusType.CAMPAIGN_SAVED.equals(audiencePo.getSegmentStatusType())) {
                        List<UniverseActivityLogPo> universeActivityLogPoList = universeActivityLogPoJPA
                                .findByAudienceId(audiencePo.getId());
                        long size1 =
                                universeActivityLogPoList.stream().filter(universeActivityLogPo ->
                                        SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED.equals(universeActivityLogPo.getAudienceStatus())
                                        || SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUEST_REJECTED.equals(universeActivityLogPo.getAudienceStatus())).count();
                        if (size1 > 0) {
                            return true;
                        }
                        List<Long> universeIdList = StringUtil.parseUniverseIdsToList(audiencePo.getUniverseIds());
                        List<AudienceDistributeJobPo> audienceDistributeJobPoList = audienceDistributeJobPoJPA
                                .findByAudienceIdAndDestinationIdInOrderByUpdateTimeDesc(audiencePo.getId(),
                                        universeIdList);
                        long size2 = audienceDistributeJobPoList.stream().filter(audienceDistributeJobPo ->
                                !SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED.equals(audienceDistributeJobPo.getStatus())).count();
                        if (size2 > 0) {
                            return true;
                        }
                    }
                    JSONObject obj = JSON.parseObject(audiencePo.getRuleJson());
                    if (Optional.ofNullable(obj).isPresent()) {
                        JSONArray segments = obj.getJSONArray("segments");
                        Long capTotal = obj.getLong("cap");
                        List<Long> countList = new ArrayList<>();
                        long countTotal = 0;
                        Float testControl = obj.getFloat("test-control");
                        if (Optional.ofNullable(segments).isPresent()) {
                            for (int i = 0; i < segments.size(); i++) {
                                JSONObject segment = segments.getJSONObject(i);
                                if (Optional.ofNullable(segment).isPresent()) {
                                    Long frozenCount = segment.getLong("frozenCount");
                                    if (Optional.ofNullable(frozenCount).isPresent() && frozenCount == 0) {
                                        return true;
                                    }
                                    countList.add(frozenCount);
                                    countTotal += frozenCount;
                                    if (testControl > 0) {
                                        if (Math.round(frozenCount * testControl) == 0 || frozenCount - Math.round(frozenCount * testControl) == 0) {
                                            return true;
                                        }
                                        Long cap = segment.getLong("cap");
                                        if (capTotal == 0 && Optional.ofNullable(cap).isPresent() && cap > 0) {
                                            if (Math.round(testControl * cap) == 0 || cap - Math.round(testControl * cap) == 0) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                            if (capTotal > 0) {
                                long capTotalCount = 0;
                                for (int i = 0; i < countList.size(); i++) {
                                    Long count = countList.get(i);
                                    Long capCount = count * capTotal / countTotal;
                                    if (capCount == 0) {
                                        return true;
                                    }
                                    if (countList.size() - 1 == i) {
                                        capCount = capTotal - capTotalCount;
                                        if (capCount == 0) {
                                            return true;
                                        }
                                    }
                                    capTotalCount += capCount;
                                    if (testControl > 0) {
                                        if (Math.round(capCount * testControl) == 0 || capCount - Math.round(capCount * testControl) == 0) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return false;
            }).collect(Collectors.toList()).size();
            permissionVo.setDistribute(size == 0);
            size = audiencePoList.stream().filter(audiencePo -> (audiencePo.getCreatedBy().equals(permissionDTO
                    .getUsername()) && SegmentStatusType.CAMPAIGN_SAVED.equals(audiencePo.getSegmentStatusType())))
                    .collect(Collectors.toList()).size();
            permissionVo.setRefresh(size == audiencePoList.size());
            size = audiencePoList.stream().filter(audiencePo -> (audiencePo.getCreatedBy().equals(permissionDTO
                    .getUsername()) && SegmentStatusType.CAMPAIGN_SAVED.equals(audiencePo.getSegmentStatusType())))
                    .collect(Collectors.toList()).size();
            permissionVo.setDelete(size == audiencePoList.size());
        }
        return permissionVo;
    }

    @Override
    public Boolean isAllowDistributeV2(FolderAndCampaign folderAndCampaign) {
        List<AudiencePo> audiencePoList = audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(folderAndCampaign.getFolderIdList(), ","));
        audiencePoList.addAll(audiencePoJPA.findAll(folderAndCampaign.getCampaignIdList()));
        if (Optional.ofNullable(audiencePoList).isPresent()) {
            long size = audiencePoList.stream().filter(audiencePo -> (audiencePo.getSegmentStatusType()
                    .equals(SegmentStatusType.CAMPAIGN_DISTRIBUTING)
                    || audiencePo.getSegmentStatusType()
                    .equals(SegmentStatusType.CAMPAIGN_DISTRIBUTED))).collect(
                    Collectors.toList()).size();
            if (size > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public AllowFlagVo isAllowDistributeAndDelete(FolderAndCampaign folderAndCampaign) {
        AllowFlagVo allowFlagVo = new AllowFlagVo(false, false);
        List<AudiencePo> audiencePoList = audiencePoJPA
                .getSegmentListByFolderId(StringUtils.join(folderAndCampaign.getFolderIdList(), ","));
        audiencePoList.addAll(audiencePoJPA.findAll(folderAndCampaign.getCampaignIdList()));
        if (Optional.ofNullable(audiencePoList).isPresent()) {
            long distributeSize = audiencePoList.stream().filter(audiencePo -> (audiencePo.getSegmentStatusType()
                    .equals(SegmentStatusType.CAMPAIGN_DISTRIBUTING)
                    || audiencePo.getSegmentStatusType()
                    .equals(SegmentStatusType.CAMPAIGN_DISTRIBUTED))).collect(
                    Collectors.toList()).size();
            if (distributeSize == 0) {
                allowFlagVo.setDistributeFlag(true);
            }
            long deleteSize = audiencePoList.stream().filter(
                    audiencePo -> (!audiencePo.getSegmentStatusType()
                            .equals(SegmentStatusType.CAMPAIGN_SAVED))).collect(
                    Collectors.toList()).size();
            if (deleteSize == 0) {
                deleteSize = audiencePoList.stream().filter(audiencePo -> {
                    if (StringUtils
                            .equals(audiencePo.getCreatedBy(), folderAndCampaign.getOwner())) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList()).size();
                if (deleteSize == audiencePoList.size()) {
                    allowFlagVo.setDeleteFlag(true);
                }
            }
        }
        return allowFlagVo;
    }

    @Override
    public String getParentFolderNameByFolderId(String folderId) throws AMSInvalidInputException {
        List<String> folderNameList = new ArrayList<>();
        String parentFolderIds = folderJPA.getParentList(folderId);
        try {
            List<Long> folderIds = ConvertStringToLong(parentFolderIds);
            List<FolderPo> folderPoList = folderJPA.getFolderPoByIdIn(folderIds);
            for (int i = 0; i < folderPoList.size(); i++) {
                folderNameList.add(folderPoList.get(i).getFolderName());
            }
        } catch (Exception e) {
            throw new AMSInvalidInputException();
        }
        return StringUtils.join(folderNameList, "/");
    }

    @Override
    public FolderPo getFolderById(Long id) throws AMSInvalidInputException {
        FolderPo folderPo = Optional.ofNullable(folderJPA.getFolderPoById(id))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0203,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0203)));
        return folderPo;
    }

    private List<Long> ConvertStringToLong(String ids) {
        return Arrays.asList(ids.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
    }

    public void refreshAllAudienceWhenThresholdChanged(List<AudiencePo> audiencePoList,
                                                       Map<Long, UniversePo> universeMap) {
        audiencePoList.forEach(audiencePo -> {
            List<Long> universeIdList = StringUtil.parseUniverseIdsToList(audiencePo.getUniverseIds());
            for (Long universeId : universeIdList) {
                UniversePo universePo = universeMap.get(universeId);
                audiencePo.setLegalFlag(true);
                if (Optional.ofNullable(universePo).isPresent()) {
                    long thresholdCount = Math.round(universePo.getUniverseCount() * universePo.getUniverseThreshold());
                    boolean illegal = (SegmentStatusType.CAMPAIGN_SAVED.equals(audiencePo.getSegmentStatusType())
                            || SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED.equals(audiencePo.getSegmentStatusType()))
                            && audiencePo.getCount().compareTo(thresholdCount) < 0;
                    audiencePo.setLegalFlag(!illegal);
                    if (illegal) {
                        break;
                    }
                }
            }
        });
        audiencePoJPA.save(audiencePoList);
    }
}
