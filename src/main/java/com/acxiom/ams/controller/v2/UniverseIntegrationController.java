package com.acxiom.ams.controller.v2;

import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.dto.v2.UniverseIntegrationDTO;
import com.acxiom.ams.model.po.UniverseIntegrationPo;
import com.acxiom.ams.service.UniverseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/universe/integration")
public class UniverseIntegrationController {
    @Autowired
    UniverseService universeService;
    @PostMapping
    public void createUniverseIntegration(@RequestBody UniverseIntegrationDTO universeIntegrationDTO) throws AMSInvalidInputException {
        universeService.createUniverseIntegration(universeIntegrationDTO);
    }

    @GetMapping("/{universeId}")
    public UniverseIntegrationPo getUniverseIntegrationByUniverseId(@PathVariable("universeId") Long universeId){
       return universeService.getUniverseIntegrationByUniverseId(universeId);
    }
}
