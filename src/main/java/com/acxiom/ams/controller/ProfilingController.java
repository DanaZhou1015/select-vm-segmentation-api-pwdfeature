package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.dto.ProfilingDTO;
import com.acxiom.ams.model.vo.ProfilingVo;
import com.acxiom.ams.service.ProfilingService;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 2:33 PM
 */
@RestController
@Validated
@RequestMapping(value = "/v1/profiling")
public class ProfilingController {

    @Autowired
    ProfilingService profilingService;

    @GetMapping(value = "/{tenantId}")
    public List<ProfilingVo> getProfilingByTenantId(
            @PathVariable(value = "tenantId") Long tenantId, @RequestParam(name = "profilingId") Long profilingId)
            throws AMSInvalidInputException {
        return profilingService.getProfilingByTenantId(tenantId, profilingId);
    }

    @GetMapping(value = "/active/{tenantId}")
    public ProfilingVo getProfilingByTenantId(
            @PathVariable(value = "tenantId") Long tenantId)
            throws AMSInvalidInputException {
        return profilingService.getActiveProfilingByTenantId(tenantId);
    }

    @PostMapping(value = "/active/{tenantId}/{profilingId}")
    public void setActiveProfiling(
            @PathVariable(value = "tenantId") Long tenantId,
            @PathVariable(value = "profilingId") Long profilingId) throws AMSInvalidInputException {
        profilingService.setActiveProfiling(tenantId, profilingId);
    }

    @DeleteMapping(value = "/{tenantId}/{profilingId}")
    public boolean deleteProfilingById(
            @PathVariable(value = "tenantId") Long tenantId, @PathVariable(value = "profilingId") Long profilingId)
            throws AMSInvalidInputException {
        return profilingService.deleteProfilingById(tenantId, profilingId);
    }

    @PostMapping(value = "/{tenantId}")
    public long saveProfiling(
            @PathVariable(value = "tenantId") Long tenantId,
            @RequestBody @Valid ProfilingDTO profilingDTO) throws AMSInvalidInputException {
        return profilingService.saveProfiling(tenantId, profilingDTO);
    }

    @PostMapping(value = "/map/{tenantId}")
    public String profilingJson(
            @PathVariable(value = "tenantId") Long tenantId,
            @RequestBody String req) throws AMSException {
        return profilingService.profilingV7(tenantId, req);
    }
}
