package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI.UserCenterAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.mapper.TenantVoMapper;
import com.acxiom.ams.model.dto.TenantDTO;
import com.acxiom.ams.model.em.TemplateStatusType;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.po.VersionPo;
import com.acxiom.ams.model.vo.TenantExtVo;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.repository.VersionPoJPA;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.util.Constant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Created by cldong on 12/12/2017.
 */
@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    TenantPoJPA tenantPoJPA;

    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;

    @Autowired
    TenantVoMapper tenantVoMapper;

    @Autowired
    UserCenterAPI userCenterAPI;

    @Autowired
    VersionPoJPA versionPoJPA;

    @Override
    public Long createTenant(TenantDTO tenantDTO) {
        TenantPo tenantPo = tenantPoJPA.findTenantPoByTenantId(tenantDTO.getTenantId());
        if (tenantPo == null) {
            tenantPo = new TenantPo();
            tenantPo.setName(tenantDTO.getName());
            tenantPo.setPath(tenantDTO.getTenantPath());
            tenantPo.setTenantId(tenantDTO.getTenantId());
            tenantPo.setCreatedBy(tenantDTO.getCreatedBy());
            tenantPoJPA.save(tenantPo);
        }
        return tenantPo.getId();
    }

    @Override
    public TenantVo getTenantByTenantId(String tenantId) throws AMSInvalidInputException {
        TenantPo tenantPo = Optional.ofNullable(tenantPoJPA.findTenantPoByTenantId(tenantId))
            .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0201,
                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0201)));
        return tenantVoMapper.map(tenantPo);
    }

    @Override
    public List<TenantVo> getAllTenantList() {
        return tenantVoMapper.map(tenantPoJPA.findAll());
    }

    @Override
    public String getPrincipal(String username, String appUrl)
        throws AMSException {
        String resp = userCenterAPI.getPrincipal(username, appUrl);
        JSONObject obj;
        try {
            obj = JSON.parseObject(resp);
        } catch (JSONException e) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0232,
                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0232));
        }
        TenantDTO tenantDTO = new TenantDTO();
        tenantDTO.setTenantId(obj.getString("workingTenantId"));
        tenantDTO.setName(obj.getString("workingTenantSysname"));
        tenantDTO.setTenantPath(obj.getString("workingTenantSysname").toLowerCase().replace(" +", ""));
        tenantDTO.setCreatedBy(obj.getString("loginName"));
        Long newTenantId = createTenant(tenantDTO);
        obj.put("newTenantId", newTenantId);
        TenantPo tenantPo = Optional
            .ofNullable(tenantPoJPA.findTenantPoByTenantId(obj.getString("workingTenantId")))
            .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0201,
                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0201)));
        VersionPo versionPo = versionPoJPA
            .findFirstByTenantPoAndOperationFlag(tenantPo, TemplateStatusType.ACTIVE);
        if (Optional.ofNullable(versionPo).isPresent()) {
            obj.put("taxonomyId", versionPo.getId());
            obj.put("taxonomyRootId", versionPo.getTreeId());
        }
        return JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue);
    }

    @Override
    public TenantExtVo getTenantExtById(Long tenantId, String key)
        throws AMSException {
        TenantPo tenantPo = Optional
            .ofNullable(tenantPoJPA.findTenantPoById(tenantId))
            .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0201,
                errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0201)));
        return userCenterAPI.getTenantExtByKey(tenantPo.getTenantId(), key);
    }

    @Override
    public TenantPo getTenantById(Long tenantId)
            throws AMSInvalidInputException {
       return Optional
                .ofNullable(tenantPoJPA.findTenantPoById(tenantId))
                .orElseThrow(() -> new AMSInvalidInputException(Constant.ERROR_CODE_0201,
                        errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0201)));
    }

    @Override
    public List<TenantVo> getWhiteListRemoveBlackListByTenantId(String tenantId) throws AMSRMIException {
        return userCenterAPI.getWhiteListRemoveBlackListByTenantId(tenantId);
    }
}
