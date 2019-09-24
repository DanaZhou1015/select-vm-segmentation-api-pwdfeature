package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.model.dto.TenantDTO;
import com.acxiom.ams.model.vo.TenantExtVo;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cldong on 12/12/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/v1/tenant")
public class TenantController {

    @Autowired
    TenantService tenantService;

    @PostMapping(value = "")
    public Long createTenant(@RequestBody TenantDTO tenantDTO) {
        return tenantService.createTenant(tenantDTO);
    }

    @GetMapping(value = "")
    public TenantVo getTenant(@RequestParam(name = "tenantId") String tenantId)
        throws AMSInvalidInputException {
        return tenantService.getTenantByTenantId(tenantId);
    }

    @GetMapping(value = "/ext/{tenantId}")
    public TenantExtVo getTenantExtById(@PathVariable(name = "tenantId") Long tenantId, @RequestParam("key") String key)
        throws AMSException {
        return tenantService.getTenantExtById(tenantId, key);
    }

    @GetMapping(value = "/{tenantId}/whitelist")
    public List<TenantVo> getTaxonomyNodeByKey(@PathVariable(value = "tenantId") String tenantId) throws AMSRMIException {
        return tenantService.getWhiteListRemoveBlackListByTenantId(tenantId);
    }
}
