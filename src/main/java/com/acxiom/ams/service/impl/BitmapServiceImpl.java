package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.po.UniversePo;
import com.acxiom.ams.model.vo.CreateNonTVBitmapVO;
import com.acxiom.ams.repository.TenantPoJPA;
import com.acxiom.ams.service.BitmapService;
import com.acxiom.ams.service.TenantService;
import com.acxiom.ams.service.UniverseService;
import com.acxiom.ams.util.Constant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/8/2017 5:33 PM
 */
@Service
public class BitmapServiceImpl implements BitmapService {

    @Autowired
    TenantPoJPA tenantPoJPA;
    @Autowired
    UniverseService universeService;
    @Autowired
    ServiceAPI.BitmapAPI bitmapAPI;
    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;
    @Autowired
    TenantService tenantService;

    @Override
    public Long calculate(Long tenantId, String userId, String rules) throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("clientID", tenantPo.getPath());
        reqParams.put("userID", userId);
        reqParams.put("rules", JSONObject.parse(rules));
        String resp = bitmapAPI.calculate(JSON.toJSONString(reqParams));
        JSONObject respJson = JSONObject.parseObject(resp);
        return respJson.getJSONObject("data").getLong("count");
    }

    @Override
    public JSONObject calculateV2(List<UniversePo> universePoList, String rule)
            throws AMSException {
        if (universePoList.isEmpty()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0257,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0257));
        }
        List<String> universeSysNameList = new ArrayList<>();
        for (UniversePo universePo : universePoList) {
            universeSysNameList.add(universePo.getUniverseSystemName());
        }
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("tenantPath", universePoList.get(0).getTenantPath());
        reqParams.put("campaign", JSONObject.parse(rule));
        reqParams.put("universeSysNames", universeSysNameList);
        String resp = bitmapAPI.calculateV2(JSON.toJSONString(reqParams));
        JSONObject respJson = JSONObject.parseObject(resp);
        return respJson.getJSONObject("data");
    }

    @Override
    public JSONArray getCampaignInfoV2(List<UniversePo> universePoList, String rule)
            throws AMSException {
        if (universePoList.isEmpty()) {
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0257,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0257));
        }
        List<String> universeSysNameList = new ArrayList<>();
        for (UniversePo universePo : universePoList) {
            universeSysNameList.add(universePo.getUniverseSystemName());
        }
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("tenantPath", universePoList.get(0).getTenantPath());
        reqParams.put("campaign", JSONObject.parse(rule));
        reqParams.put("universeSysNames", universeSysNameList);
        String resp = bitmapAPI.getCampaignInfoV2(JSON.toJSONString(reqParams));
        JSONObject respJson = JSONObject.parseObject(resp);
        return respJson.getJSONObject("data").getJSONArray("campaignInfo");
    }

    @Override
    public Long calculateForNonTv(Long tenantId, String userId, String rules)
            throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        String path = tenantPo.getPath();
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("clientID", path);
        reqParams.put("userID", userId);
        reqParams.put("rules", JSONObject.parse(rules));
        String resp = bitmapAPI.calculateForNonTV(JSON.toJSONString(reqParams));
        JSONObject respJson = JSONObject.parseObject(resp);
        Long count = respJson.getJSONObject("data").getLong("count");
        return count;
    }

    @Override
    public CreateNonTVBitmapVO createBitmapForNonTV(Long tenantId, String taxonomyId, String userId, String rules,
                                                    boolean frozenFlag)
            throws AMSException {
        TenantPo tenantPo = tenantService.getTenantById(tenantId);
        CreateNonTVBitmapVO createNonTVBitmapVO = new CreateNonTVBitmapVO();
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("clientID", tenantPo.getPath());
        reqParams.put("userID", userId);
        reqParams.put("rules", JSONObject.parse(rules));
        reqParams.put("taxonomyid", taxonomyId);
        reqParams.put("tenantID", tenantId);
        reqParams.put("frozenFlag", frozenFlag);
        String resp = bitmapAPI.createBitmapV7(JSON.toJSONString(reqParams));
        JSONObject respJson = JSONObject.parseObject(resp);
        JSONObject data = respJson.getJSONObject("data");
        createNonTVBitmapVO.setCount(data.getLong("count"));
        createNonTVBitmapVO.setTaxonomyId(data.getString("taxonomyid"));
        createNonTVBitmapVO.setFinishFlag(data.getBoolean("finishFlag"));
        createNonTVBitmapVO.setRefreshFlag(data.getBoolean("refreshFlag"));
        return createNonTVBitmapVO;
    }

    @Override
    public Map<String, Long> listNodeCountByTaxonomyIds(TenantPo tenantPo, List<String> taxonomyIdList, List<String>
            universeSysNameList)
            throws AMSRMIException {
        JSONObject reqParams = new JSONObject();
        reqParams.put("taxonomyIdList", taxonomyIdList);
        reqParams.put("tenantPath", tenantPo.getPath());
        if (Optional.ofNullable(universeSysNameList).isPresent()
                && !universeSysNameList.isEmpty()) {
            reqParams.put("universeSysNames", universeSysNameList);
        }
        return bitmapAPI.listNodeCountByTaxonomyIds(tenantPo, taxonomyIdList);
    }
}
