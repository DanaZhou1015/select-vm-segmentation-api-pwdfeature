package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.model.dto.DistributeParam;
import com.acxiom.ams.model.dto.DistributeReturnParam;
import com.acxiom.ams.service.DistributionService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:12 12/14/2017
 */
@RestController
@RequestMapping(value = "/v1/distribution")
public class DistributionController {

    @Autowired
    DistributionService distributionService;

    @PostMapping(value = "")
    public void distributeSegments(@RequestBody @Valid DistributeParam distributeParam)
        throws AMSException {
        distributionService.distributeSegments(distributeParam);
    }

    @PostMapping(value = "/callback")
    public void callbackSegmentStatus(@RequestBody DistributeReturnParam distributeReturnParam) {
        distributionService.callbackSegmentStatus(distributeReturnParam);
    }

    @PutMapping(value = "/auto/{tenantPath}")
    public void distributeSegmentsByTenantPath(@PathVariable("tenantPath") String tenantPath)
            throws AMSException {
        distributionService.distributeSegmentsByTenantPath(tenantPath);
    }
}
