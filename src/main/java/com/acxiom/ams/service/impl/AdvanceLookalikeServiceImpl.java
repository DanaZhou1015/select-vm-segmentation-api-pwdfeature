package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.mapper.AudienceVoMapper;
import com.acxiom.ams.model.dto.Condition;
import com.acxiom.ams.model.dto.ReceiveMessageDTO;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.LookalikeType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.po.FolderPo;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.vo.TenantExtVo;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.repository.FolderPoJPA;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.service.AdvanceLookalikeService;
import com.acxiom.ams.service.FolderService;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.util.Constant;
import com.acxiom.ams.util.UUID;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.*;

/**
 * @Author: Michael Zhang
 * @Date: 2019-01-10 14:45
 **/
@Service
public class AdvanceLookalikeServiceImpl implements AdvanceLookalikeService {

    @Autowired
    TenantPoJPA tenantPoJPA;

    @Autowired
    TenantService tenantService;

    @Autowired
    FolderPoJPA folderPoJPA;

    @Autowired
    FolderService folderService;

    @Autowired
    AudiencePoJPA audiencePoJPA;

    @Autowired
    ServiceAPI.BitmapAPI bitmapAPI;

    @Autowired
    ServiceAPI.UserCenterAPI userCenterAPI;

    @Autowired
    ServiceAPI.AdvanceLookalikeAPI advanceLookalikeAPI;

    @Autowired
    ServiceAPI.MessageCenterAPI messageCenterAPI;

    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;

    @Autowired
    AudienceVoMapper audienceVoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createLookalike(String reqParams) throws AMSRMIException, AMSInvalidInputException {

        JSONObject reqCreateBitmapJson = JSONObject.parseObject(reqParams);
        String name = reqCreateBitmapJson.getString("name");
        String email = reqCreateBitmapJson.getString("email");
        Long tenantId = reqCreateBitmapJson.getLong("tenantID");
        Long folderId = reqCreateBitmapJson.getLong("folderId");

        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        FolderPo folderPo = folderService.getFolderById(folderId);
        AudiencePo audienceSeed = audiencePoJPA.findFirstByNameAndTenantIdAndAudienceType(name + "_seed", tenantId, FolderType.LOOKALIKE_GROUP);
        AudiencePo audienceLookalike = audiencePoJPA.findFirstByNameAndTenantIdAndAudienceType(name + "_seed_lookalike", tenantId, FolderType.LOOKALIKE_GROUP);
        if (audienceSeed != null || audienceLookalike != null) {
            throw new AMSInvalidInputException("020209", errorMessageSourceHandler.getMessage("020209"));
        }

        String resp = bitmapAPI.createBitmapV7(reqParams);
        JSONObject respCreateBitmapJson = JSONObject.parseObject(resp);

        if (respCreateBitmapJson.getBoolean("success")) {
            String seedTaxonomyId = respCreateBitmapJson.getJSONObject("data").getString("taxonomyid");
            AudiencePo seed = new AudiencePo();
            seed.setRuleJson(reqCreateBitmapJson.getString("rules"));
            seed.setTenantId(tenantId);
            seed.setName(name + "_seed");
            seed.setCreatedBy(reqCreateBitmapJson.getString("userID"));
            seed.setAudienceType(FolderType.LOOKALIKE_GROUP);
            seed.setCount(respCreateBitmapJson.getJSONObject("data").getLong("count"));
            seed.setTaxonomyId(seedTaxonomyId);
            seed.setFolderPo(folderPo);
            seed.setLookalikeType(LookalikeType.ADVANCE);
            seed.setLookalikeInclude(true);
            audiencePoJPA.save(seed);
            String lookalikeTaxonomyId = UUID.GetTaxonomyID();
            AudiencePo lookalike = new AudiencePo();
            lookalike.setRuleJson(reqCreateBitmapJson.getString("rules"));
            lookalike.setTenantId(tenantId);
            lookalike.setName(name + "_seed_lookalike");
            lookalike.setCreatedBy(reqCreateBitmapJson.getString("userID"));
            lookalike.setAudienceType(FolderType.LOOKALIKE_GROUP);
            lookalike.setCount(respCreateBitmapJson.getLong("count"));
            lookalike.setTaxonomyId(lookalikeTaxonomyId);
            lookalike.setSegmentStatusType(SegmentStatusType.LOOKALIKE_RUNNING);
            lookalike.setFolderPo(folderPo);
            lookalike.setLookalikeType(LookalikeType.ADVANCE);
            lookalike.setLookalikeInclude(true);
            audiencePoJPA.save(lookalike);
            JSONObject reqLookalikeJson = new JSONObject();
            reqLookalikeJson.put("id", lookalikeTaxonomyId);
            reqLookalikeJson.put("tenantPath", reqCreateBitmapJson.getString("clientID"));
            reqLookalikeJson.put("seedID", seedTaxonomyId);
            reqLookalikeJson.put("email", email);
            String universeName = Constant.ADVANCE_UNIVERSE_NAME;
            String tenantExtUniverseName = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_USER_TENANT_EXT_KEY, null);
            if (tenantExtUniverseName != null) {
                universeName = tenantExtUniverseName;
            }
            boolean isOnlySeed = true;
            if ("".equals(universeName)) {
                isOnlySeed = false;
            }
            reqLookalikeJson.put("isOnlySeed", isOnlySeed);
            String user = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_USER_TENANT_EXT_KEY, "020303");
            reqLookalikeJson.put("url", String.format(Constant.ADVANCE_S3_PATH, user));
            String accessKey = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_ACCESS_KEY_TENANT_EXT_KEY, "020301");
            reqLookalikeJson.put("accessKey", accessKey);
            String secretKey = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_SECRET_KEY_TENANT_EXT_KEY, "020302");
            reqLookalikeJson.put("secretKey", secretKey);
            reqLookalikeJson.put("region", Constant.ADVANCE_URL_REGION);
            bitmapAPI.createAdvanceLookalike(reqLookalikeJson.toJSONString());
        }
    }

    private String getTenantExtValue(String tenantId, String key, String errorCode) throws AMSRMIException, AMSInvalidInputException {
        TenantExtVo tenantExt = userCenterAPI.getTenantExtByKey(tenantId, key);
        if (tenantExt == null) {
            if (errorCode == null) {
                return null;
            } else {
                throw new AMSInvalidInputException(errorCode, errorMessageSourceHandler.getMessage(errorCode));
            }
        }
        return tenantExt.getValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBitmapCallback(String reqParams) throws AMSRMIException, AMSInvalidInputException {

        JSONObject reqCreateBitmapJson = JSONObject.parseObject(reqParams);
        String type = reqCreateBitmapJson.getString("status");
        String taxonomyId = reqCreateBitmapJson.getString("id");
        AudiencePo audiencePo = audiencePoJPA.findAudiencePoByTaxonomyId(taxonomyId);

        SegmentStatusType segmentStatusType = audiencePo.getSegmentStatusType();
        if (SegmentStatusType.LOOKALIKE_RUNNING.name().equals(type)) {
            String fileName = reqCreateBitmapJson.getString("fileName");

            audiencePo.setLookalikeFilePath(fileName);
            audiencePoJPA.save(audiencePo);

            TenantPo tenantPo = tenantService.getTenantById(audiencePo.getTenantId());

            String baseData = Constant.ADVANCE_BASE_DATA;
            String tenantExtBaseData = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_BASE_DATA_TENANT_EXT_KEY, null);
            if (tenantExtBaseData != null) {
                baseData = tenantExtBaseData;
            }
            String universeName = Constant.ADVANCE_UNIVERSE_NAME;
            String tenantExtUniverseName = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_UNIVERSE_NAME_TENANT_EXT_KEY, null);
            if (tenantExtUniverseName != null) {
                universeName = tenantExtUniverseName;
            }

            String accessKey = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_ACCESS_KEY_TENANT_EXT_KEY, "020301");
            String secretKey = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_SECRET_KEY_TENANT_EXT_KEY, "020302");

            JSONObject reqLookalikeJson = new JSONObject();
            reqLookalikeJson.put("segment-name", audiencePo.getName());
            reqLookalikeJson.put("model-class", Constant.ADVANCE_MODEL_CLASS_BINARY);
            reqLookalikeJson.put("base-data", baseData);
            reqLookalikeJson.put("model-type", Constant.ADVANCE_MODEL_TYPE_LASSO);
            reqLookalikeJson.put("universe-name", universeName);
            reqLookalikeJson.put("file-name", fileName);
            reqLookalikeJson.put("include-seed", "True");
            reqLookalikeJson.put("comments", "");

            String jobId = "";

            try {
                String jsonString = advanceLookalikeAPI.buildModel(accessKey, secretKey, reqLookalikeJson);
                JSONObject jsonObject = JSONObject.parseObject(jsonString);
                jobId = jsonObject.getString(Constant.ADVANCE_JOB_ID);
                audiencePo.setLookalikeJobId(jobId);
                audiencePoJPA.save(audiencePo);
            } catch (Exception e) {
                audiencePo.setSegmentStatusType(SegmentStatusType.LOOKALIKE_FAILED);
                audiencePoJPA.save(audiencePo);
                LogUtils.error(e);
                sendEmail(audiencePo, segmentStatusType);
            }
            if (!"".equals(jobId)) {
                ScheduledExecutorService scheduled = newScheduledThreadPool(1);
                getModel(audiencePo, scheduled, Constant.ADVANCE_GET_API_DELAY_TIME);
            }
        } else if (SegmentStatusType.LOOKALIKE_DONE.name().equals(type)) {
            Long size = reqCreateBitmapJson.getLong("size");
            if (size != null && size > 0L) {
                audiencePo.setCount(size);
            }
            audiencePo.setSegmentStatusType(SegmentStatusType.LOOKALIKE_DONE);
            audiencePoJPA.save(audiencePo);
            sendEmail(audiencePo, SegmentStatusType.LOOKALIKE_DONE);
        } else {
            audiencePo.setSegmentStatusType(SegmentStatusType.LOOKALIKE_FAILED);
            audiencePoJPA.save(audiencePo);
            sendEmail(audiencePo, segmentStatusType);
        }
    }

    public void getModel(AudiencePo audiencePo, ScheduledExecutorService scheduled, Integer initialDelay) throws AMSRMIException, AMSInvalidInputException {
        SegmentStatusType segmentStatusType = audiencePo.getSegmentStatusType();
        TenantPo tenantPo = tenantService.getTenantById(audiencePo.getTenantId());
        String accessKey = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_ACCESS_KEY_TENANT_EXT_KEY, "020301");
        String secretKey = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_SECRET_KEY_TENANT_EXT_KEY, "020302");
        scheduled.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String jsonString = advanceLookalikeAPI.getModel(accessKey, secretKey, audiencePo.getLookalikeJobId());
                    JSONObject jsonObject = JSONObject.parseObject(jsonString);
                    LogUtils.info(jsonObject);
                    if (jsonObject.getString(Constant.ADVANCE_RESULT_UPLIFT) != null && jsonObject.getString(Constant.ADVANCE_RESULT_REACH_VALUES) != null) {
                        audiencePo.setLookalikeResult(jsonString);
                        audiencePo.setSegmentStatusType(SegmentStatusType.LOOKALIKE_READY);
                        audiencePoJPA.save(audiencePo);
                        sendEmail(audiencePo, SegmentStatusType.LOOKALIKE_READY);
                        scheduled.shutdown();
                    }
                    if (jsonObject.getJSONArray("errors") != null) {
                        JSONObject error = (JSONObject) jsonObject.getJSONArray("errors").get(0);
                        String code = error.getString("code");
                        audiencePo.setErrorCode(code);
                        audiencePo.setSegmentStatusType(SegmentStatusType.LOOKALIKE_FAILED);
                        audiencePoJPA.save(audiencePo);
                        scheduled.shutdown();
                        try {
                            sendEmail(audiencePo, segmentStatusType);
                        } catch (AMSRMIException e) {
                            LogUtils.error(e);
                        }
                    }
                } catch (Exception e) {
                    audiencePo.setSegmentStatusType(SegmentStatusType.LOOKALIKE_FAILED);
                    audiencePoJPA.save(audiencePo);
                    scheduled.shutdown();
                    try {
                        sendEmail(audiencePo, segmentStatusType);
                    } catch (AMSRMIException e1) {
                        LogUtils.error(e1);
                    }
                    LogUtils.error(e);
                }
            }
        }, initialDelay, Constant.ADVANCE_GET_API_CYCLE_TIME, TimeUnit.MINUTES);
    }

    @Override
    public String getLookalikeResultById(Long id) {
        return audiencePoJPA.findOne(id).getLookalikeResult();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deployLookalike(Long id, int value, Long size) throws AMSRMIException, AMSInvalidInputException {
        AudiencePo audiencePo = audiencePoJPA.findOne(id);
        audiencePo.setCount(size);
        audiencePo.setFrozenCount(size);
        audiencePo.setLookalikeReachValue(value);
        audiencePo.setSegmentStatusType(SegmentStatusType.LOOKALIKE_PENDING);
        audiencePoJPA.save(audiencePo);

        TenantPo tenantPo = tenantService.getTenantById(audiencePo.getTenantId());
        String accessKey = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_ACCESS_KEY_TENANT_EXT_KEY, "020301");
        String secretKey = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_SECRET_KEY_TENANT_EXT_KEY, "020302");

        JSONObject lookalikeJson = new JSONObject();
        lookalikeJson.put(Constant.ADVANCE_JOB_ID, audiencePo.getLookalikeJobId());
        lookalikeJson.put(Constant.ADVANCE_REACH_VALUE, value);
        try {
            advanceLookalikeAPI.deployModel(accessKey, secretKey, lookalikeJson);
        } catch (Exception e) {
            LogUtils.error(e);
        }
        ScheduledExecutorService scheduled = newScheduledThreadPool(1);
        deployModel(audiencePo, scheduled, Constant.ADVANCE_DEPLOY_API_DELAY_TIME);
    }

    public void deployModel(AudiencePo audiencePo, ScheduledExecutorService scheduled, Integer initialDelay) throws AMSRMIException, AMSInvalidInputException {
        TenantPo tenantPo = tenantService.getTenantById(audiencePo.getTenantId());
        String accessKey = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_ACCESS_KEY_TENANT_EXT_KEY, "020301");
        String secretKey = getTenantExtValue(tenantPo.getTenantId(), Constant.ADVANCE_SECRET_KEY_TENANT_EXT_KEY, "020302");
        scheduled.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                JSONObject deployModelJson = new JSONObject();
                deployModelJson.put(Constant.ADVANCE_JOB_ID, audiencePo.getLookalikeJobId());
                deployModelJson.put(Constant.ADVANCE_REACH_VALUE, audiencePo.getLookalikeReachValue());
                LogUtils.info(deployModelJson);
                try {
                    String jsonString = advanceLookalikeAPI.deployModel(accessKey, secretKey, deployModelJson);
                    JSONObject jsonObject = JSONObject.parseObject(jsonString);
                    if (jsonObject.getString("file-name") != null) {
                        JSONObject bitmapJson = new JSONObject();
                        bitmapJson.put("accessKey", accessKey);
                        bitmapJson.put("secretKey", secretKey);
                        bitmapJson.put("region", Constant.ADVANCE_URL_REGION);
                        bitmapJson.put("tenantPath", tenantPo.getPath());
                        bitmapJson.put("size", audiencePo.getCount());
                        bitmapJson.put("taxID", audiencePo.getTaxonomyId());
                        bitmapJson.put("url", jsonObject.getString("file-name"));
                        bitmapAPI.advanceLookalikeToBitmap(bitmapJson.toJSONString());
                        scheduled.shutdown();
                    }
                } catch (Exception e) {
                    audiencePo.setSegmentStatusType(SegmentStatusType.LOOKALIKE_FAILED);
                    audiencePoJPA.save(audiencePo);
                    scheduled.shutdownNow();
                    LogUtils.error(e);
                    try {
                        sendEmail(audiencePo, SegmentStatusType.LOOKALIKE_PENDING);
                    } catch (AMSRMIException e1) {
                        LogUtils.error(e1);
                    }
                }
            }
        }, initialDelay, Constant.ADVANCE_DEPLOY_API_CYCLE_TIME, TimeUnit.MINUTES);
    }

    private void sendEmail(AudiencePo audiencePo, SegmentStatusType type) throws AMSRMIException {
        String createdBy = audiencePo.getCreatedBy();
        ReceiveMessageDTO receiveMessageDTO = new ReceiveMessageDTO();
        receiveMessageDTO.setSendType(Constant.MESSAGE_SEND_TYPE);
        receiveMessageDTO.setMessageType(Constant.MESSAGE_TYPE);
        Condition condition = new Condition();
        condition.setUserName(new String[]{createdBy});
        receiveMessageDTO.setCondition(condition);
        String emailTitle;
        String emailContent;
        if (type.equals(SegmentStatusType.LOOKALIKE_RUNNING)) {
            emailTitle = "Lookalike Model Generation for [" + audiencePo.getName() + "] is Failed";
            emailContent = "Sorry to tell you that " + emailTitle.toLowerCase() + ".<br>Thanks for using Select.";
        } else if (type.equals(SegmentStatusType.LOOKALIKE_READY)) {
            emailTitle = "Lookalike Model for [" + audiencePo.getName() + "] is Ready";
            emailContent = "So glad to tell you that " + emailTitle.toLowerCase() + ".<br>Thanks for using Select.";
        } else if (type.equals(SegmentStatusType.LOOKALIKE_PENDING)) {
            emailTitle = "Lookalike segment generation for [" + audiencePo.getName() + "] is Failed";
            emailContent = "Sorry to tell you that " + emailTitle.toLowerCase() + ".<br>Thanks for using Select.";
        } else if (type.equals(SegmentStatusType.LOOKALIKE_DONE)) {
            emailTitle = "Lookalike segment [" + audiencePo.getName() + "] is Created";
            emailContent = "So glad to tell you that " + emailTitle.toLowerCase() + ".<br>Thanks for using Select.";
        } else {
            emailTitle = "";
            emailContent = "";
        }
        receiveMessageDTO.setMessageTitle(emailTitle);
        receiveMessageDTO.setMessageContent(emailContent);
        messageCenterAPI.sendEmail(receiveMessageDTO);
    }
}
