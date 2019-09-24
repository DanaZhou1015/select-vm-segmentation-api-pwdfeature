package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.api.ServiceAPI.BitmapAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.mapper.TenantAndChannelPoMapper;
import com.acxiom.ams.mapper.TenantVoMapper;
import com.acxiom.ams.mapper.UniverseActivityLogPoMapper;
import com.acxiom.ams.model.dto.ChannelDTO;
import com.acxiom.ams.model.dto.DistributeParam;
import com.acxiom.ams.model.dto.DistributeReturnParam;
import com.acxiom.ams.model.dto.Segment;
import com.acxiom.ams.model.dto.v2.CampaignDistributeParamDTO;
import com.acxiom.ams.model.dto.v2.DistributeParamForCampaign;
import com.acxiom.ams.model.dto.v2.DistributeReturnParamV2;
import com.acxiom.ams.model.dto.v2.UniverseActivityLogParamForReview;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.*;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.model.vo.UniverseActivityLogVo;
import com.acxiom.ams.repository.*;
import com.acxiom.ams.service.*;
import com.acxiom.ams.util.Constant;
import com.acxiom.ams.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.io.*;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 14:47 12/14/2017
 */
@Service
@Transactional
public class DistributionServiceImpl implements DistributionService {

    private static final Logger logger = LoggerFactory.getLogger(DistributionServiceImpl.class);

    private static final String SEGMENT = "SEGMENT";
    @Autowired
    BitmapAPI bitmapAPI;
    @Autowired
    ServiceAPI.UserCenterAPI userCenterAPI;
    @Autowired
    AudiencePoJPA audiencePoJPA;
    @Autowired
    TenantAndChannelPoJPA tenantAndChannelPoJPA;
    @Autowired
    TenantPoJPA tenantPoJPA;
    @Autowired
    TenantVoMapper tenantVoMapper;
    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Autowired
    FolderService folderService;
    @Autowired
    AudienceDistributeJobPoJPA audienceDistributeJobPoJPA;
    @Autowired
    UniverseService universeService;
    @Autowired
    TenantService tenantService;
    @Autowired
    UniversePoJPA universePoJPA;
    @Autowired
    UniverseIntegrationPoJPA universeIntegrationPoJPA;
    @Autowired
    UniverseActivityLogPoJPA universeActivityLogPoJPA;
    @Autowired
    ChannelService channelService;
    @Autowired
    TenantAndChannelPoMapper tenantAndChannelPoMapper;
    @Autowired
    UniverseActivityLogPoMapper universeActivityLogPoMapper;

    @Override
    public String distributeSegments(DistributeParam distributeParam)
            throws AMSException {
        List<TenantAndChannelPo> tenantAndChannelList =
                tenantAndChannelPoJPA.findByIdIn(distributeParam.getChannelIdList());
        if (tenantAndChannelList.isEmpty()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0210,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0210));
        }
        List<AudiencePo> audiencePoList = new ArrayList<>();
        if (distributeParam.getFolderIdList() != null && !distributeParam.getFolderIdList()
                .isEmpty()) {
            String folderIds = StringUtils.join(distributeParam.getFolderIdList(), ",");
            audiencePoList.addAll(audiencePoJPA.getSegmentListByFolderId(folderIds));
        }
        audiencePoList.addAll(audiencePoJPA.findAll(distributeParam.getAudienceIdList()));
        String metadata = createMetadata(audiencePoList);
        return distributeSegment(audiencePoList, distributeParam.getTenantId(),
                distributeParam.getNoticeEmail(), tenantAndChannelList,
                distributeParam.getUsername(), metadata);
    }

    private String distributeSegment(List<AudiencePo> audiencePoList, Long tenantId,
                                     String noticeEmail, List<TenantAndChannelPo> tenantAndChannelPoList,
                                     String username, String
                                             metadata)
            throws AMSException {
        if (audiencePoList.isEmpty()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0212,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0212));
        }
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        long limit = tenantPo.getCountLimit();
        long lessLimit = audiencePoList.stream()
                .filter(audiencePo -> audiencePo.getCount().longValue() < limit).count();
        if (lessLimit > 0) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0216,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0216));
        }
        List<Long> destinations = new ArrayList<>();
        tenantAndChannelPoList.forEach(TenantAndChannelPo -> {
            destinations.add(TenantAndChannelPo.getId());
        });
        audiencePoList.forEach(audiencePo -> {
            audiencePo.setDestinationIds(StringUtils.join(destinations, ","));
            audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_DISTRIBUTING);
        });
        audiencePoJPA.save(audiencePoList);
        String tenantPath = tenantPo.getPath();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clientID", tenantPath);
        jsonObject.put("tenantId", String.valueOf(tenantId));
        jsonObject.put("tenantName", tenantPo.getName());
        jsonObject.put("email", noticeEmail);
        try {
            jsonObject.put("okContent", URLEncoder.encode(metadata, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        List<String> segmentNames = new ArrayList<>();
        List<Segment> segments = new ArrayList<>();
        for (int i = 0; i < audiencePoList.size(); i++) {
            Segment segment = new Segment();
            try {
                segment.setSegmentName(URLEncoder.encode(audiencePoList.get(i).getName(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
            }
            segment.setRules(
                    (JSON.parseObject(audiencePoList.get(i).getRuleJson())));
            segment.setTaxonomyID(audiencePoList.get(i).getTaxonomyId());
            List<Long> distributeJobIds = new ArrayList<>();
            for (TenantAndChannelPo tenantAndChannelPo : tenantAndChannelPoList) {
                AudienceDistributeJobPo audienceDistributeJobPo = new AudienceDistributeJobPo();
                audienceDistributeJobPo.setAudienceId(audiencePoList.get(i).getId());
                audienceDistributeJobPo.setNoticeEmail(noticeEmail);
                audienceDistributeJobPo.setStatus(SegmentStatusType.SEGMENT_DISTRIBUTING);
                audienceDistributeJobPo.setTenantId(tenantId);
                audienceDistributeJobPo.setUpdateBy(username);
                audienceDistributeJobPo.setDestinationId(tenantAndChannelPo.getId());
                audienceDistributeJobPo.setCreatedBy(audiencePoList.get(i).getCreatedBy());
                audienceDistributeJobPo.setCreatedTime(audiencePoList.get(i).getCreatedTime());
                audienceDistributeJobPo.setUpdateTime(new Date());
                audienceDistributeJobPoJPA.save(audienceDistributeJobPo);
                distributeJobIds.add(audienceDistributeJobPo.getId());
            }
            segment.setSegmentId(audiencePoList.get(i).getId());
            segment.setDistributeJobIds(distributeJobIds);
            segment.setSegmentPath(folderService.getParentFolderNameByFolderId(String.valueOf(audiencePoList.get(i)
                    .getFolderPo().getId())));
            try {
                segment.setSegmentName(URLEncoder.encode(audiencePoList.get(i).getName(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                LogUtils.error(e.getMessage());
                segment.setSegmentName("");
            }
            segmentNames.add(segment.getSegmentName());
            segments.add(segment);
        }
        jsonObject.put("audiences", segments);
        jsonObject.put("segmentNames", StringUtils.join(segmentNames, ","));
        List<ChannelDTO> channelDTOList = new ArrayList<>();
        for (TenantAndChannelPo tenantAndChannelPo : tenantAndChannelPoList) {
            ChannelDTO channelDTO = new ChannelDTO();
            channelDTO.setId(tenantAndChannelPo.getId());
            channelDTO.setLrAudienceId(tenantAndChannelPo.getLrAudienceId());
            channelDTO.setDataLakeFlag(false);
            channelDTO.setSftpHost(tenantAndChannelPo.getHost());
            channelDTO.setSftpName(tenantAndChannelPo.getUsername());
            channelDTO.setSftpPassword(tenantAndChannelPo.getPassword());
            channelDTO.setSftpPath(tenantAndChannelPo.getPath());
            channelDTO.setSftpPem(tenantAndChannelPo.getKeyFile());
            channelDTO.setSftpPort(tenantAndChannelPo.getPort());
            channelDTO.setSftpPassPhrase(tenantAndChannelPo.getPassPhrase());
            channelDTOList.add(channelDTO);
        }
        jsonObject.put("channels", channelDTOList);
        jsonObject.put("username", username);
        TenantVo tenantVo = userCenterAPI.getTenantById(tenantPo.getTenantId());
        jsonObject.put("usePPID", tenantVo.getUsePpid());
        jsonObject.put("spaceId", tenantVo.getSpaceId());
        jsonObject.put("tenantUUID", tenantVo.getTenantId());
        String resp = bitmapAPI.distributeV7(jsonObject.toJSONString());
        JSONObject respJson = JSONObject.parseObject(resp);
        JSONObject data = respJson.getJSONObject("data");
        return data.getString("fileName");
    }

    @Override
    public void callbackSegmentStatus(DistributeReturnParam distributeReturnParam) {
        List<Long> audienceJobIds = distributeReturnParam.getAudienceJobIds();
        List<Long> audienceIds = new ArrayList<>();
        List<AudienceDistributeJobPo> audienceDistributeJobPoList = audienceDistributeJobPoJPA
                .findAll(audienceJobIds);
        audienceDistributeJobPoList.forEach(audienceDistributeJobPo -> audienceIds.add(audienceDistributeJobPo.getAudienceId()));
        List<AudiencePo> audiencePoList = audiencePoJPA.findAudiencePoByIdIn(audienceIds);
        if (StringUtils.equals(distributeReturnParam.getStatus(), "success")) {
//            audiencePoList.forEach(audiencePo -> {
//                if (!SegmentStatusType.SEGMENT_FAILED.equals(audiencePo.getSegmentStatusType())
//                        || !SegmentStatusType.SEGMENT_RUNNING.equals(audiencePo.getSegmentStatusType())) {
//                    audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_DISTRIBUTED);
//                }
//            });
//            audienceDistributeJobPoList
//                    .forEach(audienceDistributeJobPo -> audienceDistributeJobPo
//                            .setStatus(SegmentStatusType.SEGMENT_DISTRIBUTED));
            audiencePoList.forEach(audiencePo -> {
                String[] destinationIds = audiencePo.getDestinationIds().split(",");
                List<AudienceDistributeJobPo> audienceDistributeJobPoListNew = audienceDistributeJobPoJPA.findByAudienceIdOrderByUpdateTimeDesc(audiencePo.getId());
                audienceDistributeJobPoListNew = audienceDistributeJobPoListNew.subList(0, destinationIds.length);
                long distributedSize = audienceDistributeJobPoListNew.stream()
                        .filter(audienceDistributeJobPo -> !SegmentStatusType.SEGMENT_DISTRIBUTED.equals(audienceDistributeJobPo.getStatus())).count();
                long updateSize = audienceDistributeJobPoList.stream()
                        .filter(audienceDistributeJobPo -> audienceDistributeJobPo.getAudienceId() == audiencePo.getId()).count();
                if (distributedSize == updateSize) {
                    if (!SegmentStatusType.SEGMENT_FAILED.equals(audiencePo.getSegmentStatusType())
                            || !SegmentStatusType.SEGMENT_RUNNING.equals(audiencePo.getSegmentStatusType())) {
                        audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_DISTRIBUTED);
                    }
                }
            });
            audienceDistributeJobPoList
                    .forEach(audienceDistributeJobPo -> audienceDistributeJobPo
                            .setStatus(SegmentStatusType.SEGMENT_DISTRIBUTED));
        } else {
            logger.info(distributeReturnParam.getMessage());
            audiencePoList.forEach(audiencePo -> {
                if (!SegmentStatusType.SEGMENT_FAILED.equals(audiencePo.getSegmentStatusType())
                        || !SegmentStatusType.SEGMENT_RUNNING.equals(audiencePo.getSegmentStatusType())) {
                    audiencePo.setSegmentStatusType(SegmentStatusType.SEGMENT_DISTRIBUTE_FAILED);
                }
            });
            audienceDistributeJobPoList
                    .forEach(audienceDistributeJobPo -> audienceDistributeJobPo
                            .setStatus(SegmentStatusType.SEGMENT_DISTRIBUTE_FAILED));
        }
        audiencePoJPA.save(audiencePoList);
        audienceDistributeJobPoJPA.save(audienceDistributeJobPoList);
    }

    @Override
    public void distributeCampaigns(CampaignDistributeParamDTO campaignDistributeParamDTO)
            throws AMSException {
        if (!Optional.ofNullable(campaignDistributeParamDTO.getCampaignIdList()).isPresent()
                || campaignDistributeParamDTO.getCampaignIdList().isEmpty()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0211,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0211));
        }
        TenantPo tenantPo = tenantService.getTenantById(campaignDistributeParamDTO.getTenantId());
        List<AudiencePo> audiencePoList = audiencePoJPA.findAll(campaignDistributeParamDTO.getCampaignIdList());
        if (audiencePoList.isEmpty()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0212,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0212));
        }
        Set<Long> universeIdSet = new HashSet<>();
        Map<Long, List<UniversePo>> universeMap = new HashMap<>();
        Map<Long, UniverseIntegrationPo> universeIntegrationPoMap = new HashMap<>();
        long size = audiencePoList.stream().filter(audiencePo -> (
                !SegmentStatusType.CAMPAIGN_SAVED.equals(audiencePo.getSegmentStatusType())
                        && !SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED.equals(audiencePo.getSegmentStatusType())))
                .collect(Collectors.toList()).size();
        if (size > 0) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0233,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0233));
        }
        for (AudiencePo audiencePo : audiencePoList) {
            List<Long> universeIdList = StringUtil.parseUniverseIdsToList(audiencePo.getUniverseIds());
            List<UniversePo> universePoList = universePoJPA.findAll(universeIdList);
            if (universePoList.isEmpty()) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0257,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0257));
            }
            universePoList.forEach(universePo -> universeIdSet.add(universePo.getId()));
            universeMap.put(audiencePo.getId(), universePoList);
        }
        List<UniverseIntegrationPo> universeIntegrationPoList =
                universeIntegrationPoJPA.findAllByUniverseIdIn(new ArrayList<>(universeIdSet));
        if (universeIntegrationPoList.size() != universeIdSet.size()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0259,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0259));
        }
        universeIntegrationPoList.forEach(universeIntegrationPo ->
                universeIntegrationPoMap.put(universeIntegrationPo.getUniverseId(), universeIntegrationPo));
        distributeCampaign(audiencePoList, tenantPo, campaignDistributeParamDTO.getUsername(),
                campaignDistributeParamDTO.getNoticeEmail(), universeIntegrationPoMap, universeMap);
    }

    @Override
    public void callbackSegmentStatusV2(DistributeReturnParamV2 distributeReturnParam)
            throws AMSInvalidInputException {
        Long id = distributeReturnParam.getCampaignId();
        Long jobId = distributeReturnParam.getJobId();
        Long universeId = distributeReturnParam.getUniverseId();
        logger.info("Campaign callback jobId : " + jobId);
        AudiencePo audiencePo = Optional.ofNullable(audiencePoJPA.findOne(id)).orElseThrow(
                () -> new AMSInvalidInputException(Constant.ERROR_CODE_0227,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0227)));
        AudienceDistributeJobPo audienceDistributeJobPo = Optional
                .ofNullable(audienceDistributeJobPoJPA.findOne(jobId)).orElseThrow(
                        () -> new AMSInvalidInputException(Constant.ERROR_CODE_0227,
                                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0227)));
        audiencePo.setUpdateTime(new Date());
        audienceDistributeJobPo.setUpdateTime(new Date());
        UniverseActivityLogPo universeActivityLogPo = universeActivityLogPoJPA.
                findFirstByAudienceIdAndDestinationIdOrderByCreatedTimeDesc(id, universeId);
        if (distributeReturnParam.getStatus()) {
            if (Optional.ofNullable(universeActivityLogPo).isPresent()) {
                universeActivityLogPo.setAudienceStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTED);
            }
            audienceDistributeJobPo.setStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTED);
            audienceDistributeJobPoJPA.save(audienceDistributeJobPo);
            if (SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED.equals(audiencePo.getSegmentStatusType())
                    || SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED.equals(audiencePo.getSegmentStatusType())
                    || SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUEST_REJECTED.equals(audiencePo.getSegmentStatusType())) {
                return;
            } else {
                List<Long> universeIdList = StringUtil.parseUniverseIdsToList(audiencePo.getUniverseIds());
                List<AudienceDistributeJobPo> audienceDistributeJobPoList = audienceDistributeJobPoJPA
                        .findByAudienceIdAndDestinationIdInOrderByUpdateTimeDesc(audiencePo.getId(), universeIdList);
                List<SegmentStatusType> statusList = new ArrayList<>();
                if (audienceDistributeJobPoList.size() < universeIdList.size()) {
                    LogUtils.error("Failed to callback audience status, audienceDistributeJobPoList's size is less than universeIdList's size, audience id is : " + audiencePo.getId());
                    return;
                }
                for (int i = 0; i < universeIdList.size(); i++) {
                    statusList.add(audienceDistributeJobPoList.get(i).getStatus());
                }
                long size = statusList.stream().filter(segmentStatusType ->
                        SegmentStatusType.CAMPAIGN_DISTRIBUTED.equals(segmentStatusType)).count();
                if (size == audienceDistributeJobPoList.size()) {
                    audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTED);
                }
            }
        } else {
            audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED);
            audienceDistributeJobPo.setStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED);
            audienceDistributeJobPoJPA.save(audienceDistributeJobPo);
            if (Optional.ofNullable(universeActivityLogPo).isPresent()) {
                universeActivityLogPo.setAudienceStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED);
                universeActivityLogPoJPA.save(universeActivityLogPo);
            }
        }
        audiencePoJPA.save(audiencePo);
    }

    @Override
    @Async
    public void distributeSegmentsByTenantPath(String tenantPath) throws AMSException {
        TenantPo tenantPo = Optional.ofNullable(tenantPoJPA.findFirstByPath(tenantPath))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0201,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0201)));
        Long tenantId = tenantPo.getId();
        List<AudiencePo> audiencePoList = audiencePoJPA.findBySegmentStatusTypeInAndTenantIdAndDistributionFlag(
                new SegmentStatusType[]{SegmentStatusType.SEGMENT_DISTRIBUTE_FAILED,
                        SegmentStatusType.SEGMENT_DISTRIBUTED}, tenantId, true);
        if (audiencePoList.isEmpty()) {
            LogUtils.info("No Audience distribute.");
        }
        for (int i = 0; i < audiencePoList.size(); i++) {
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String[] destinationIds = StringUtils.split(audiencePoList.get(i).getDestinationIds(), ",");
            List<Long> destinations = new ArrayList<>();
            for (int j = 0; j < destinationIds.length; j++) {
                destinations.add(Long.valueOf(destinationIds[j]));
            }
            List<TenantAndChannelPo> tenantAndChannelPoList = tenantAndChannelPoJPA.findByIdIn(destinations);
            tenantAndChannelPoList.forEach(TenantAndChannelPo -> {
                destinations.add(TenantAndChannelPo.getId());
            });

            List<AudiencePo> audiencePoListTemp = new ArrayList<>();
            audiencePoListTemp.add(audiencePoList.get(i));
            String metadata = createMetadata(audiencePoList);
            distributeSegment(audiencePoListTemp, tenantPo.getId(), "", tenantAndChannelPoList, "", metadata);
        }
    }

    private void distributeCampaign(List<AudiencePo> audiencePoList, TenantPo tenantPo, String username,
                                    String noticeEmail,
                                    Map<Long, UniverseIntegrationPo> universeIntegrationPoMap, Map<Long,
            List<UniversePo>> universeMap) throws AMSException {
        for (AudiencePo audiencePo : audiencePoList) {
            List<DistributeParamForCampaign> distributeParamForCampaignList = new ArrayList<>();
            List<UniversePo> universePoList = universeMap.get(audiencePo.getId());
            for (UniversePo universePo : universePoList) {
                if (!universePo.getTenantPath().equals(universePo.getOwnerTenantPath())) {
                    UniverseActivityLogPo universeActivityLogPo = universeActivityLogPoJPA
                            .findFirstByAudienceIdAndDestinationIdOrderByCreatedTimeDesc(audiencePo.getId(),
                                    universePo.getId());
                    if (SegmentStatusType.CAMPAIGN_SAVED.equals(universeActivityLogPo.getAudienceStatus())) {
                        universeActivityLogPo.setAudienceStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED);
                        universeActivityLogPo.setRequestBy(username);
                        universeActivityLogPo.setRequestEmail(noticeEmail);
                        universeActivityLogPoJPA.save(universeActivityLogPo);
                        audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED);
                        audiencePoJPA.save(audiencePo);
                        continue;
                    } else if (SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED.equals(universeActivityLogPo.getAudienceStatus())) {
                        universeActivityLogPo.setAudienceStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
                        universeActivityLogPoJPA.save(universeActivityLogPo);
                    } else {
                        LogUtils.info("universe activity log distribute: " + universeActivityLogPo.getId());
                        continue;
                    }
                }
                AudienceDistributeJobPo audienceDistributeJobPo = new AudienceDistributeJobPo();
                audienceDistributeJobPo.setAudienceId(audiencePo.getId());
                audienceDistributeJobPo.setNoticeEmail(noticeEmail);
                audienceDistributeJobPo.setStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
                audienceDistributeJobPo.setTenantId(tenantPo.getId());
                audienceDistributeJobPo.setUpdateBy(username);
                audienceDistributeJobPo.setCreatedBy(audiencePo.getCreatedBy());
                audienceDistributeJobPo.setCreatedTime(audiencePo.getCreatedTime());
                audienceDistributeJobPo.setUpdateTime(new Date());
                audienceDistributeJobPo.setRules(audiencePo.getRuleJson());
                audienceDistributeJobPo.setDestinationId(universePo.getId());
                audienceDistributeJobPo.setAudienceType(FolderType.CAMPAIGN);
                audienceDistributeJobPoJPA.save(audienceDistributeJobPo);
                DistributeParamForCampaign distributeParamForCampaign = new DistributeParamForCampaign();
                distributeParamForCampaign.setAudienceID(universeIntegrationPoMap.get(universePo.getId()).getLrAudienceId());
                distributeParamForCampaign.setFilePath(universeIntegrationPoMap.get(universePo.getId()).getDropOffPoint());
                distributeParamForCampaign.setJobID(audienceDistributeJobPo.getId());
                distributeParamForCampaign.setUniverseID(universePo.getId());
                distributeParamForCampaign.setUniverseName(universePo.getUniverseName());
                distributeParamForCampaign.setUniverseSysName(universePo.getUniverseSystemName());
                distributeParamForCampaignList.add(distributeParamForCampaign);
            }
            JSONObject obj = JSON.parseObject(audiencePo.getRuleJson());
            if (!Optional.ofNullable(obj).isPresent()) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0269,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0269));
            }
            JSONArray segments = obj.getJSONArray("segments");
            if (!Optional.ofNullable(segments).isPresent() || segments.isEmpty()) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0269,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0269));
            }
            // segment is 0, can't distribute
            parseSegmentForDistributeCampaign(obj, segments);
            JSONObject campaign = new JSONObject();
            campaign.put("campaign", obj);
            campaign.put("campaignID", audiencePo.getId());
            campaign.put("universes", distributeParamForCampaignList);
            campaign.put("campaignName", audiencePo.getName());
            List<Object> campaigns = new ArrayList<>();
            campaigns.add(campaign);
            callBitmapToDistributeCampaign(campaigns, tenantPo, noticeEmail, username);
            UniverseActivityLogPo requestedActivityLogPo =
                    universeActivityLogPoJPA.findFirstByAudienceIdAndAudienceStatus(audiencePo.getId(),
                            SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED);
            if (Optional.ofNullable(requestedActivityLogPo).isPresent()) {
                audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED);
            } else if (SegmentStatusType.CAMPAIGN_SAVED.equals(audiencePo.getSegmentStatusType())
                    || SegmentStatusType.CAMPAIGN_DISTRIBUTE_FAILED.equals(audiencePo.getSegmentStatusType())) {
                audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
            }
            audiencePoJPA.save(audiencePo);
        }
    }

    private void callBitmapToDistributeCampaign(List<Object> campaigns, TenantPo tenantPo, String noticeEmail,
                                                String username) throws AMSRMIException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tenantPath", tenantPo.getPath());
        jsonObject.put("tenantName", tenantPo.getName());
        jsonObject.put("tenantID", tenantPo.getId());
        jsonObject.put("email", noticeEmail);
        jsonObject.put("emailURL", Constant.TV_FE_APP_URL);
        jsonObject.put("userName", username);
        jsonObject.put("campaigns", campaigns);
        bitmapAPI.distributeCampaign(jsonObject.toJSONString());
    }

    private void parseSegmentForDistributeCampaign(JSONObject obj, JSONArray segments) throws AMSInvalidInputException {
        Long capTotal = obj.getLong("cap");
        List<Long> countList = new ArrayList<>();
        long countTotal = 0;
        Float testControl = obj.getFloat("test-control");
        for (int i = 0; i < segments.size(); i++) {
            JSONObject segment = segments.getJSONObject(i);
            if (!Optional.ofNullable(segment).isPresent() || segment.isEmpty()) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0281,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0281
                        ));
            }
            Long frozenCount = segment.getLong("frozenCount");
            if (Optional.ofNullable(frozenCount).isPresent() && frozenCount == 0) {
                throw new AMSInvalidInputException(Constant.ERROR_CODE_0216,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0216
                        ));
            }
            countList.add(frozenCount);
            countTotal += frozenCount;
            if (testControl > 0) {
                if (Math.round(frozenCount * testControl) == 0 || frozenCount - Math.round(frozenCount * testControl) == 0) {
                    throw new AMSInvalidInputException(Constant.ERROR_CODE_0216,
                            errorMessageSourceHandler.getMessage(
                                    Constant.ERROR_CODE_0216));
                }
                Long cap = segment.getLong("cap");
                if (capTotal == 0 && Optional.ofNullable(cap).isPresent() && cap > 0) {
                    if (Math.round(testControl * cap) == 0 || cap - Math.round(testControl * cap) == 0) {
                        throw new AMSInvalidInputException(Constant.ERROR_CODE_0216,
                                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0216));
                    }
                }
            }
        }
        if (capTotal > 0) {
            long capTotalCount = 0;
            for (int i = 0; i < countList.size(); i++) {
                Long count = countList.get(i);
                if (countTotal == 0) {
                    throw new AMSInvalidInputException(Constant.ERROR_CODE_0216,
                            errorMessageSourceHandler.getMessage(
                                    Constant.ERROR_CODE_0216));
                }
                Long capCount = count * capTotal / countTotal;
                if (capCount == 0) {
                    throw new AMSInvalidInputException(Constant.ERROR_CODE_0216,
                            errorMessageSourceHandler.getMessage(
                                    Constant.ERROR_CODE_0216));
                }
                if (countList.size() - 1 == i) {
                    capCount = capTotal - capTotalCount;
                    if (capCount == 0) {
                        throw new AMSInvalidInputException(Constant.ERROR_CODE_0216,
                                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0216));
                    }
                }
                capTotalCount += capCount;
                if (testControl > 0) {
                    if (Math.round(capCount * testControl) == 0 || capCount - Math.round(capCount * testControl) == 0) {
                        throw new AMSInvalidInputException(Constant.ERROR_CODE_0216,
                                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0216));
                    }
                }
            }
        }
    }

    private String createMetadata(List<AudiencePo> audiencePoList) {
        Document document = DocumentHelper.createDocument();
        Element root = DocumentHelper.createElement("Custom");
        document.setRootElement(root);
        int segmentFlag = 1;
        for (AudiencePo audiencePo : audiencePoList) {
            Element column = root.addElement("Column");
            column.addAttribute("display",
                    audiencePo.getName() == null ? "" : audiencePo.getName().trim());
            column.addAttribute("name",
                    SEGMENT.concat(String.valueOf(segmentFlag)));
            column.addAttribute("segmentCode",
                    audiencePo.getCode() == null ? "" : audiencePo.getCode().trim());
            column.addAttribute("value", "1");
            column.addAttribute("description",
                    audiencePo.getDescription() == null ? "" : audiencePo.getDescription().trim());
            segmentFlag++;
        }
        return document.asXML();
    }

    @Override
    public Page<UniverseActivityLogVo> getActivityList(Long tenantId, String keywords, List<Long> clientList,
                                                       Date startDate, Date endDate, Integer pageNo,
                                                       Integer pageSize) throws AMSInvalidInputException {
        if (!Optional.ofNullable(pageNo).isPresent()) {
            pageNo = 0;
        }
        if (!Optional.ofNullable(pageSize).isPresent()) {
            pageSize = Constant.DEFAULT_PAGE_SIZE;
        }
        Specification<UniverseActivityLogPo> specification = (Root<UniverseActivityLogPo> root,
                                                              CriteriaQuery<?> criteriaQuery,
                                                              CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("ownerTenantId").as(Long.class), tenantId));
            if (Optional.ofNullable(keywords).isPresent()) {
                predicates.add(criteriaBuilder.like(root.get("audienceName").as(String.class), "%" + keywords + "%"));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (Optional.ofNullable(startDate).isPresent()) {
                String startDateStr = sdf.format(startDate);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updateTime").as(String.class),
                        startDateStr));
            }
            if (Optional.ofNullable(endDate).isPresent()) {
                Calendar c = Calendar.getInstance();
                c.setTime(endDate);
                c.add(Calendar.DAY_OF_MONTH, 1);
                String endDateStr = sdf.format(c.getTime());
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updateTime").as(String.class), endDateStr));
            }
            if (Optional.ofNullable(clientList).isPresent() && !clientList.isEmpty()) {
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("tenantId"));
                for (Long client : clientList) {
                    in.value(client);
                }
                predicates.add(criteriaBuilder.and(in));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<UniverseActivityLogPo> page = universeActivityLogPoJPA.findAll(specification, new PageRequest(pageNo,
                pageSize,
                new Sort(Sort.Direction.DESC, "updateTime")));
        return page.map(universeActivityLogPo -> {
            UniverseActivityLogVo universeActivityLogVo = universeActivityLogPoMapper.map(universeActivityLogPo);
            try {
                universeActivityLogVo.setUniverseName(universeService.getUniverseById(universeActivityLogVo.getDestinationId()).getUniverseName());
            } catch (AMSInvalidInputException e) {
                LogUtils.error(e);
            }
            return universeActivityLogVo;
        });
    }

    @Override
    public void reviewActivity(UniverseActivityLogParamForReview universeActivityLogParam) throws AMSException {
        List<UniverseActivityLogPo> universeActivityLogPoList =
                universeActivityLogPoJPA.findAll(universeActivityLogParam.getIdList());
        long size = universeActivityLogPoList.stream().filter(
                universeActivityLogPo -> SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED.equals(universeActivityLogPo.getAudienceStatus()))
                .count();
        if (universeActivityLogPoList.size() != size) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0261,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0261));
        }
        String status = universeActivityLogParam.getStatus();
        if (Constant.UNIVERSE_ACTIVITY_APPROVE.equals(status)) {
            for (int i = 0; i < universeActivityLogPoList.size(); i++) {
                UniverseActivityLogPo universeActivityLogPo = universeActivityLogPoList.get(i);
                universeActivityLogPo.setApprovalBy(universeActivityLogParam.getUsername());
                universeActivityLogPo.setAudienceStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
                universeActivityLogPoJPA.save(universeActivityLogPo);
                AudiencePo audiencePo = audiencePoJPA.findOne(universeActivityLogPo.getAudienceId());
                distributeCampaignJob(audiencePo, universeActivityLogPo.getDestinationId(),
                        universeActivityLogPo.getRequestBy(), universeActivityLogPo.getRequestEmail());
            }
        } else if (Constant.UNIVERSE_ACTIVITY_REJECT.equals(status)) {
            for (int i = 0; i < universeActivityLogPoList.size(); i++) {
                UniverseActivityLogPo universeActivityLogPo = universeActivityLogPoList.get(i);
                universeActivityLogPo.setApprovalBy(universeActivityLogParam.getUsername());
                universeActivityLogPo.setAudienceStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUEST_REJECTED);
                universeActivityLogPoJPA.save(universeActivityLogPo);
                AudiencePo audiencePo = audiencePoJPA.getOne(universeActivityLogPo.getAudienceId());
                audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUEST_REJECTED);
                audiencePoJPA.save(audiencePo);
            }
        }
    }

    private void distributeCampaignJob(AudiencePo audiencePo, Long universeId, String username, String noticeEmail) throws AMSException {
        JSONObject obj = JSON.parseObject(audiencePo.getRuleJson());
        if (!Optional.ofNullable(obj).isPresent()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0269,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0269));
        }
        JSONArray segments = obj.getJSONArray("segments");
        if (!Optional.ofNullable(segments).isPresent() || segments.isEmpty()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0269,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0269));
        }
        parseSegmentForDistributeCampaign(obj, segments);
        TenantPo tenantPo = tenantService.getTenantById(audiencePo.getTenantId());
        UniversePo universePo = universeService.getUniverseById(universeId);
        UniverseIntegrationPo universeIntegrationPo = universeService.getUniverseIntegrationByUniverseId(universeId);
        if (!Optional.ofNullable(universeIntegrationPo).isPresent() || !Optional.ofNullable(universePo).isPresent()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0257,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0257));
        }
        AudienceDistributeJobPo audienceDistributeJobPo = new AudienceDistributeJobPo();
        audienceDistributeJobPo.setAudienceId(audiencePo.getId());
        audienceDistributeJobPo.setNoticeEmail(noticeEmail);
        audienceDistributeJobPo.setStatus(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
        audienceDistributeJobPo.setTenantId(audiencePo.getTenantId());
        audienceDistributeJobPo.setUpdateBy(username);
        audienceDistributeJobPo.setCreatedBy(audiencePo.getCreatedBy());
        audienceDistributeJobPo.setCreatedTime(audiencePo.getCreatedTime());
        audienceDistributeJobPo.setUpdateTime(new Date());
        audienceDistributeJobPo.setRules(audiencePo.getRuleJson());
        audienceDistributeJobPo.setDestinationId(universeId);
        audienceDistributeJobPo.setAudienceType(FolderType.CAMPAIGN);
        audienceDistributeJobPoJPA.save(audienceDistributeJobPo);

        DistributeParamForCampaign distributeParamForCampaign = new DistributeParamForCampaign();
        distributeParamForCampaign.setAudienceID(universeIntegrationPo.getLrAudienceId());
        distributeParamForCampaign.setFilePath(universeIntegrationPo.getDropOffPoint());
        distributeParamForCampaign.setJobID(audienceDistributeJobPo.getId());
        distributeParamForCampaign.setUniverseID(universePo.getId());
        distributeParamForCampaign.setUniverseName(universePo.getUniverseName());
        distributeParamForCampaign.setUniverseSysName(universePo.getUniverseSystemName());
        JSONObject campaign = new JSONObject();
        campaign.put("campaign", obj);
        campaign.put("campaignID", audiencePo.getId());
        campaign.put("universes", Lists.newArrayList(distributeParamForCampaign));
        campaign.put("campaignName", audiencePo.getName());
        List<Object> campaigns = new ArrayList<>();
        campaigns.add(campaign);
        callBitmapToDistributeCampaign(campaigns, tenantPo, noticeEmail, username);
        if (SegmentStatusType.CAMPAIGN_SAVED.equals(audiencePo.getSegmentStatusType())) {
            audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
        } else if (SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED.equals(audiencePo.getSegmentStatusType())) {
            List<UniverseActivityLogPo> universeActivityLogPoList =
                    universeActivityLogPoJPA.findByAudienceId(audiencePo.getId());
            long size =
                    universeActivityLogPoList.stream().filter(universeActivityLogPo -> SegmentStatusType.CAMPAIGN_DISTRIBUTION_REQUESTED.equals(universeActivityLogPo.getAudienceStatus())).count();
            if (size == 1) {
                audiencePo.setSegmentStatusType(SegmentStatusType.CAMPAIGN_DISTRIBUTING);
            }
        }
        audiencePoJPA.save(audiencePo);
    }

    @Override
    public List<TenantVo> getShareUniverseTenantInfoByTenantId(Long tenantId) {
        List<BigInteger> tenantList = universeActivityLogPoJPA.findDistinctTenantIdByOwnerTenantId(tenantId);
        List<Long> ids = new ArrayList<>();
        for (BigInteger t : tenantList) {
            ids.add(t.longValue());
        }
        return tenantVoMapper.map(tenantPoJPA.findAll(ids));
    }
}

