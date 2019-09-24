package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.model.vo.DataSourceVo;
import com.acxiom.ams.model.vo.OverlapVo;
import com.acxiom.ams.service.DataSourceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 17:22 3/14/2018
 */
@RestController
@RequestMapping("/v1/datasource")
public class DataSourceController {

    @Autowired
    DataSourceService dataSourceService;

    @GetMapping(value = "/overlap")
    public OverlapVo getOverlapById(@RequestParam("primaryId") Integer primaryId,
        @RequestParam("secondaryId") Integer secondaryId) throws AMSException {
        return dataSourceService.getOverlapById(primaryId, secondaryId);
    }

    @GetMapping(value = "/{tenantId}")
    public List<DataSourceVo> getDataSourceListByTenantId(
        @PathVariable(value = "tenantId") String tenantId) throws AMSException {
        return dataSourceService.getDataSourceList(tenantId);
    }
}
