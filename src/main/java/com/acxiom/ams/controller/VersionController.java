package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.mapper.VersionPoMapper;
import com.acxiom.ams.model.dto.VersionDTO;
import com.acxiom.ams.model.po.VersionPo;
import com.acxiom.ams.model.vo.VersionPageVo;
import com.acxiom.ams.model.vo.VersionVo;
import com.acxiom.ams.service.VersionPoService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/7/2017 11:24 AM
 */
@RestController
@Validated
@RequestMapping(value = "/v1/version")
public class VersionController {

    @Autowired
    VersionPoService versionPoService;
    @Autowired
    VersionPoMapper versionPoMapper;

    @GetMapping(value = "/page/{tenantId}")
    public VersionPageVo findVersionByTenantIdAndPage(
        @PathVariable(value = "tenantId") Long tenantId,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "pageSize") int pageSize) throws AMSInvalidInputException {
        return versionPoService.findVersionByTenantIdAndPage(tenantId, page, pageSize);
    }


    @GetMapping(value = "/{tenantId}/{versionId}")
    public VersionVo findVersionById(@PathVariable(value = "tenantId") Long tenantId,
        @PathVariable(value = "versionId") Long versionId) throws AMSInvalidInputException {
        return versionPoMapper.map(versionPoService.findVersionById(tenantId, versionId));
    }

    @PostMapping(value = "/{tenantId}")
    public long saveVersion(@PathVariable(value = "tenantId") Long tenantId,
        @RequestBody @Valid VersionDTO versionDTO) throws AMSInvalidInputException {
        return versionPoService.saveVersion(tenantId, versionDTO);
    }

    @PutMapping(value = "/active/{tenantId}")
    public void setVersionFlagActive(@PathVariable(value = "tenantId") Long tenantId,
        @RequestBody VersionDTO versionDTO) throws AMSInvalidInputException {
        versionPoService.setVersionFlagActive(tenantId, versionDTO.getVersionId());
    }

    @PutMapping(value = "/{tenantId}")
    public void updateVersionTreeId(@PathVariable(value = "tenantId") Long tenantId,
        @RequestBody VersionDTO versionDTO) throws AMSInvalidInputException {
        versionPoService
            .updateVersionTreeId(tenantId, versionDTO.getVersionId(), versionDTO.getTreeId());
    }

    @DeleteMapping(value = "/{tenantId}")
    public void deleteVersionById(@PathVariable(value = "tenantId") Long tenantId,
        @RequestBody VersionDTO versionDTO) throws AMSInvalidInputException {
        versionPoService.deleteVersionById(tenantId, versionDTO.getVersionId());
    }

    @GetMapping(value = "/{versionId}")
    public VersionVo getVersionById(@PathVariable(value = "versionId") Long versionId) throws AMSInvalidInputException {
        VersionPo versionPo = versionPoService.getVersionById(versionId);
        return versionPoMapper.map(versionPo);
    }

    @GetMapping(value = "/migration/data")
    public void migrationTaxonomyTree() throws AMSRMIException {
        versionPoService.migrationTaxonomyTree();
    }
}
