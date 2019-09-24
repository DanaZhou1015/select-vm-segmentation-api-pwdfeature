package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI.DataSourceAPI;
import com.acxiom.ams.common.ErrorMessageSourceHandler;
import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.model.vo.DataSourceVo;
import com.acxiom.ams.model.vo.OverlapVo;
import com.acxiom.ams.service.DataSourceService;
import com.acxiom.ams.util.Constant;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 17:25 3/14/2018
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {

    @Autowired
    DataSourceAPI dataSourceAPI;

    @Autowired
    ErrorMessageSourceHandler errorMessageSourceHandler;

    @Override
    public List<DataSourceVo> getDataSourceList(String tenantId) throws AMSException {
        String resp = dataSourceAPI.getDataSourceList(tenantId);
        JSONArray respJson = JSONObject.parseArray(resp);
        List<DataSourceVo> dataSourceVoList = new ArrayList<>();
        for (int i = 0; i < respJson.size(); i++) {
            JSONObject obj = respJson.getJSONObject(i);
            DataSourceVo dataSourceVo = new DataSourceVo();
            dataSourceVo.setId(obj.getInteger("id"));
            dataSourceVo.setName(obj.getString("name"));
            dataSourceVoList.add(dataSourceVo);
        }
        return dataSourceVoList;
    }

    @Override
    public OverlapVo getOverlapById(Integer primaryId, Integer secondaryId)
            throws AMSException {
        String resp = dataSourceAPI.getOverlap(primaryId, secondaryId);
        JSONObject respJson;
        if (null == resp) {
            LogUtils.error("get overlap api result is null");
            throw new AMSInvalidInputException(Constant.ERROR_CODE_0240,
                    errorMessageSourceHandler.getMessage(Constant.ERROR_CODE_0240));
        }
        respJson = JSONObject.parseObject(resp);
        OverlapVo overlapVo = new OverlapVo();
        overlapVo.setPrimaryTotalRecords(respJson.getLong("primaryTotalRecords"));
        overlapVo.setSecondaryTotalRecords(respJson.getLong("secondaryTotalRecords"));
        overlapVo.setUniqueRecords(respJson.getLong("uniqueRecords"));
        return overlapVo;
    }
}
