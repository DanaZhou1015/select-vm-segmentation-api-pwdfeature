package com.acxiom.ams.controller.v2;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.model.vo.WhiteTenant;
import com.acxiom.ams.service.UniverseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/v2/universe/shared")
public class UniverseSharedController {
    @Autowired
    UniverseService universeService;

    @GetMapping(value = "/white/list/{tenantId}")
    public List<WhiteTenant> getWhiteListByTenantId(@PathVariable(value = "tenantId") Long tenantId)
            throws AMSException {
        return universeService.getWhiteListByTenantId(tenantId);
    }

    @PostMapping(value = "/{universeId}/{targetTenantId}")
    public Long createSharedUniverse(@PathVariable(value = "universeId") Long universeId,
                                     @PathVariable(value = "targetTenantId") String targetTenantId)
            throws AMSException {
          return universeService.createSharedUniverse(universeId,targetTenantId);
    }
}
