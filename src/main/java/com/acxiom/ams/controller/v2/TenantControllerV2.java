	package com.acxiom.ams.controller.v2;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.dto.TenantDTO;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.service.TenantService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by cldong on 12/12/2017.
 */
@RestController
@Validated
@RequestMapping(value = "/v2/tenant")
public class TenantControllerV2 {

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

    @GetMapping(value = "/all")
    public List<TenantVo> getAllTenantList() {
        return tenantService.getAllTenantList();
    }

    @GetMapping(value = "/principal")
    public String getTenant(@RequestParam("username") String username, @RequestParam("appUrl") String appUrl)
        throws AMSException {
        return tenantService.getPrincipal(username, appUrl);
    }
}
