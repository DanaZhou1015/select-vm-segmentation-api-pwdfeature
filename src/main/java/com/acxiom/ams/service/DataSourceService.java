package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.model.vo.DataSourceVo;
import com.acxiom.ams.model.vo.OverlapVo;
import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 17:24 3/14/2018
 */
public interface DataSourceService {

    List<DataSourceVo> getDataSourceList(String tenantId) throws AMSException;

    OverlapVo getOverlapById(Integer primaryId, Integer secondaryId)
        throws AMSException;
}
