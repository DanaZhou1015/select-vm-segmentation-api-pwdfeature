package com.acxiom.ams.controller.v2;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.dto.v2.UniverseDTO;
import com.acxiom.ams.model.dto.v2.UniverseForUpdateDTO;
import com.acxiom.ams.model.po.UniversePo;
import com.acxiom.ams.model.rmi.Taxonomy;
import com.acxiom.ams.model.vo.SourceItem;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.service.UniverseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/v2/universe")
public class UniverseController {

    @Autowired
    UniverseService universeService;

    @PostMapping(value = "/{tenantId}")
    public Long createUniverse(@PathVariable(value = "tenantId") Long tenantId,
                               @RequestBody @Valid UniverseDTO universeDTO)
            throws AMSException {
        return universeService.createUniverse(tenantId, universeDTO);
    }

    @PutMapping(value = "/{tenantId}/{universeId}")
    public void updateUniverse(@PathVariable(value = "tenantId") Long tenantId,
                               @PathVariable(value = "universeId") Long universeId,
                               @RequestBody @Valid UniverseForUpdateDTO universeForUpdateDTO)
            throws AMSException {
        universeService.updateUniverse(tenantId, universeId, universeForUpdateDTO);
    }

    @PutMapping(value = "/callback/{tenantPath}/{universeSystemName}/{universeJobId}/{status}")
    public void callbackUniverseStatus(@PathVariable(value = "tenantPath") String tenantPath,
                                       @PathVariable(value = "universeSystemName") String universeSystemName,
                                       @PathVariable(value = "universeJobId") String universeJobId,
                                       @PathVariable(value = "status") Boolean status)
            throws AMSInvalidInputException {
        universeService.callbackUniverseStatus(tenantPath, universeSystemName, universeJobId, status);
    }

    @PostMapping(value = "/calculate/{tenantId}")
    public String calculate(@PathVariable(value = "tenantId") Long tenantId,
                            @RequestBody String rules)
            throws AMSException {
        return universeService.calculateUniverse(tenantId, rules);
    }

    @GetMapping(value = "/source/data/{tenantId}")
    public List<SourceItem> getMyDataByTenantId(@PathVariable(value = "tenantId") Long tenantId)
            throws AMSException {
        return universeService.getMyDataByTenantId(tenantId);
    }

    @GetMapping(value = "/{tenantId}/{username}")
    public List<UniversePo> listUniverseByTenantId(@PathVariable(value = "tenantId") Long tenantId,
                                                   @PathVariable(value = "username") String username)
            throws AMSException {
        return universeService.listUniverseByTenantId(tenantId,username);
    }

    @GetMapping(value = "/{tenantId}/{key}/{rootId}")
    public List<Taxonomy> listAllUniverseAttributeByRootIdAndKey(@PathVariable(value = "tenantId") Long tenantId,
                                                                 @PathVariable(value = "key") String key,
                                                                 @PathVariable(value = "rootId") String rootId)
            throws AMSException {
        return universeService.listAllUniverseAttributeByRootIdAndKey(tenantId, key, rootId);
    }

    @GetMapping(value = "/icon/{tenantId}")
    public String getIconByTenantId(@PathVariable(value = "tenantId") Long tenantId)
            throws AMSException {
        return universeService.getIconByTenantId(tenantId);
    }

    @PutMapping(value = "/callback/{tenantPath}/{universeSystemName}/{universeJobId}")
    public void callbackSharedUniverseStatus(@PathVariable(value = "tenantPath") String tenantPath,
                                             @PathVariable(value = "universeSystemName") String universeSystemName,
                                             @PathVariable(value = "universeJobId") String universeJobId)
            throws AMSInvalidInputException {
        universeService.callbackSharedUniverseStatus(tenantPath, universeSystemName, universeJobId);
    }

    @GetMapping(value = "/share/{tenantId}")
    public List<TenantVo> getShareUniverseTenantInfoByTenantId(@PathVariable(value = "tenantId") Long tenantId)
            throws AMSException {
        return universeService.getShareUniverseTenantInfoByTenantId(tenantId);
    }

    @GetMapping(value = "/icon/{universeSysName}/{tenantId}")
    public UniversePo getIconByUniverseSysNameAndTenantId(@PathVariable(value = "universeSysName") String universeSysName,@PathVariable(value = "tenantId") String tenantId)
            throws AMSException {
        return universeService.getIconByUniverseSysNameAndTenantId(universeSysName,tenantId);
    }
}
