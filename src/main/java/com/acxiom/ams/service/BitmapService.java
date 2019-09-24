package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.po.UniversePo;
import com.acxiom.ams.model.vo.CreateNonTVBitmapVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by cldong on 12/5/2017.
 */
public interface BitmapService {

    Long calculate(Long tenantId, String userId, String rules) throws AMSException;

    JSONObject calculateV2(List<UniversePo> universePoList, String rule) throws AMSException;

    JSONArray getCampaignInfoV2(List<UniversePo> universePoList, String rule)
            throws AMSException;

    Long calculateForNonTv(Long tenantId, String userId, String rules) throws AMSException;

    CreateNonTVBitmapVO createBitmapForNonTV(Long tenantId, String taxonomyId, String userId, String rules, boolean frozenFlag) throws AMSException;

    Map<String, Long> listNodeCountByTaxonomyIds(TenantPo tenantPo, List<String> taxonomyIdList, List<String>
            universeSysNameList) throws AMSRMIException;
}
