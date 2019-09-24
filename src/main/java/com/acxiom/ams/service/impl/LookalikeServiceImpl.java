package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.mapper.ProfilingPoMapper;
import com.acxiom.ams.mapper.VersionPoMapper;
import com.acxiom.ams.model.dto.Condition;
import com.acxiom.ams.model.dto.ReceiveMessageDTO;
import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.po.FolderPo;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.repository.FolderPoJPA;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.repository.VersionPoJPA;
import com.acxiom.ams.service.LookalikeService;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.util.Constant;
import com.acxiom.ams.util.UUID;
import com.alibaba.fastjson.JSONObject;

import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 3:47 PM
 */
@Service
@Transactional
public class LookalikeServiceImpl implements LookalikeService {
    @Autowired
    VersionPoJPA versionPoJPA;
    @Autowired
    TenantPoJPA tenantPoJPA;
    @Autowired
    VersionPoMapper versionPoMapper;
    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Autowired
    ProfilingPoMapper profilingPoMapper;
    @Autowired
    ServiceAPI.LookalikeAPI lookalikeAPI;
    @Autowired
    ServiceAPI.BitmapAPI bitmapAPI;
    @Autowired
    AudiencePoJPA audiencePoJPA;
    @Autowired
    FolderPoJPA folderPoJPA;
    @Autowired
    ServiceAPI.MessageCenterAPI messageCenterAPI;
    @Autowired
    TenantService tenantService;


    public static final String MESSAGE_SEND_TYPE = "Email";
    public static final String MESSAGE_TYPE = "actions and information";

    @Override
    public String getConfidenceByLevel(String tenantPath, String taxonomyId, String level) throws AMSRMIException {
        String resp = lookalikeAPI.getConfidenceByLevel(tenantPath, taxonomyId, level);
        if (StringUtils.isNotBlank(resp)) {
            Long count = Long.valueOf(resp);
            if (count >= 0) {
                audiencePoJPA.updateSegmentStatusTypeByTaxonomyId(SegmentStatusType.LOOKALIKE_DONE, taxonomyId);
                audiencePoJPA.updateCountByTaxonomyId(count, taxonomyId);
            }
        }
        return resp;
    }

    @Override
    public String getConfidenceBySize(String tenantPath, String taxonomyId, Long size) throws AMSRMIException {
        String resp = lookalikeAPI.getConfidenceBySize(tenantPath, taxonomyId, size);
        if (null != resp && !resp.equals("")) {
            Long count = Long.valueOf(resp);
            if (count >= 0) {
                audiencePoJPA.updateSegmentStatusTypeByTaxonomyId(SegmentStatusType.LOOKALIKE_DONE, taxonomyId);
                audiencePoJPA.updateCountByTaxonomyId(count, taxonomyId);
            }
        }
        return resp;
    }

    @Override
    public String getConfidenceByTaxonomyId(String taxonomyId) throws AMSRMIException {
        return lookalikeAPI.getConfidenceByTaxonomyId(taxonomyId);
    }

    @Override
    public String getConfidenceLiftByTaxonomyId(String taxonomyId) throws AMSRMIException {
        return lookalikeAPI.getConfidenceLiftByTaxonomyId(taxonomyId);
    }

    @Override
    public String getJobs() throws AMSRMIException {
        return lookalikeAPI.getJobs();
    }

    @Override
    public String createJobBySeed(String reqParams) throws AMSException {

        JSONObject reqCreateBitmapJson = JSONObject.parseObject(reqParams);
        String name = reqCreateBitmapJson.getString("name");
        String email = reqCreateBitmapJson.getString("email");
        Long tenantId = reqCreateBitmapJson.getLong("tenantID");
        Long folderId = reqCreateBitmapJson.getLong("folderId");
        tenantService.getTenantById(tenantId);
        int count = 0;
        AudiencePo audienceSeed = audiencePoJPA.findFirstByNameAndTenantIdAndAudienceType(name + "_seed", tenantId,
                FolderType.LOOKALIKE_GROUP);
        if (null != audienceSeed) count++;
        AudiencePo audienceLookalike = audiencePoJPA.findFirstByNameAndTenantIdAndAudienceType(name +
                "_seed_lookalike", tenantId, FolderType.LOOKALIKE_GROUP);
        if (null != audienceLookalike) count++;
        if (count > 0) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0209,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0209));
        }
        FolderPo folderPo = folderPoJPA.getFolderPoById(folderId);
        if (folderPo == null) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0205,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0205));
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
            audiencePoJPA.save(lookalike);
            // commit lookalike task
            JSONObject reqLookalikeJson = new JSONObject();
            reqLookalikeJson.put("id", lookalikeTaxonomyId.toString());
            reqLookalikeJson.put("cid", tenantId.toString());
            reqLookalikeJson.put("cpath", reqCreateBitmapJson.getString("clientID"));
            reqLookalikeJson.put("seedid", seedTaxonomyId);
            reqLookalikeJson.put("taxid", lookalikeTaxonomyId.toString());
            reqLookalikeJson.put("email", email);
            String url = URLEncoder.encode(Constant.CALLBACK_URL + Constant.GET_UPDATE_STATUS);
            reqLookalikeJson.put("callback", url);
            if (lookalikeAPI.createJobBySeed(reqLookalikeJson)) {
                return reqLookalikeJson.toString();
            }
        }
        return "";
    }

    @Override
    public void updateLookalikeStatus(String taxonomyId, String email, boolean status) throws AMSRMIException {
        AudiencePo audiencePo = audiencePoJPA.findAudiencePoByTaxonomyId(taxonomyId);
        String createdBy = audiencePo.getCreatedBy();
        ReceiveMessageDTO receiveMessageDTO = new ReceiveMessageDTO();
        receiveMessageDTO.setSendType(MESSAGE_SEND_TYPE);
        receiveMessageDTO.setMessageType(MESSAGE_TYPE);
        Condition condition = new Condition();
        condition.setUserName(new String[]{createdBy});
        receiveMessageDTO.setCondition(condition);
        String emailTitle;
        String emailContent;
        if (status) {
            audiencePoJPA.updateSegmentStatusTypeByTaxonomyId(SegmentStatusType.LOOKALIKE_READY, taxonomyId);
            emailTitle = "Lookalike Model for [" + audiencePo.getName() + "] is Ready";
            emailContent = "So glad to tell you that " + emailTitle.toLowerCase() + ".<br>Thanks for using Select.";
        } else {
            audiencePoJPA.updateSegmentStatusTypeByTaxonomyId(SegmentStatusType.LOOKALIKE_FAILED, taxonomyId);
            emailTitle = "Lookalike Model Generation for [" + audiencePo.getName() + "] is Failed";
            emailContent = "Sorry to tell you that " + emailTitle.toLowerCase() + ".<br>Thanks for using Select.";
        }
        receiveMessageDTO.setMessageTitle(emailTitle);
        receiveMessageDTO.setMessageContent(emailContent);
        messageCenterAPI.sendEmail(receiveMessageDTO);
    }
}
