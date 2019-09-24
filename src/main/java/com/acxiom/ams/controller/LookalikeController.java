package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.service.LookalikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/12/2017 2:27 PM
 */
@RestController
@Validated
@RequestMapping(value = "/v1/lookalike")
public class LookalikeController {

    @Autowired
    LookalikeService lookalikeService;

    @PostMapping(value = "/job/by-seed")
    public String createJobBySeed(
        @RequestBody String reqParams)
        throws AMSException {
        return lookalikeService.createJobBySeed(reqParams);
    }

    @GetMapping(value = "/updateStatus")
    public void updateStatus(@RequestParam(value = "taxid") String taxid,
                             @RequestParam(value = "email") String email,
                             @RequestParam(value = "status") boolean status) throws AMSRMIException {
        lookalikeService.updateLookalikeStatus(taxid,email,status);
    }


    @GetMapping(value = "/confidence/by-level/{tenantPath}/{taxonomyId}/{level}")
    public String getConfidenceByLevel(
        @PathVariable(value = "tenantPath") String tenantPath,
        @PathVariable(value = "taxonomyId") String taxonomyId,
        @PathVariable(value = "level") String level)
        throws AMSException {
        return lookalikeService.getConfidenceByLevel(tenantPath, taxonomyId, level);
    }

    @GetMapping(value = "/confidence/by-size/{tenantPath}/{taxonomyId}/{size}")
    public String getConfidenceBySize(
        @PathVariable(value = "tenantPath") String tenantPath,
        @PathVariable(value = "taxonomyId") String taxonomyId,
        @PathVariable(value = "size") Long size)
        throws AMSException {
        return lookalikeService.getConfidenceBySize(tenantPath, taxonomyId, size);
    }

    @GetMapping(value = "/confidence/{taxonomyId}")
    public String getConfidenceByTaxonomyId(
        @PathVariable(value = "taxonomyId") String taxonomyId)
        throws AMSException {
        return lookalikeService.getConfidenceByTaxonomyId(taxonomyId);
    }

    @GetMapping(value = "/confidence/lift/{taxonomyId}")
    public String getConfidenceLiftByTaxonomyId(@PathVariable(value = "taxonomyId") String taxonomyId)throws AMSException {
        return lookalikeService.getConfidenceLiftByTaxonomyId(taxonomyId);
    }

    @RequestMapping(value = "/job/getJobs", method = RequestMethod.GET)
    public String getJobs() throws AMSRMIException {
        return lookalikeService.getJobs();
    }

}
