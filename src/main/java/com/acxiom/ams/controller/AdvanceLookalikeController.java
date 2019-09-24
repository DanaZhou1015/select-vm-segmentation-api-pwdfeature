package com.acxiom.ams.controller;

import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.service.AdvanceLookalikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Michael Zhang
 * @Date: 2019-01-10 14:45
 **/
@RestController
@Validated
@RequestMapping(value = "/advance/lookalike")
public class AdvanceLookalikeController {

    @Autowired
    AdvanceLookalikeService advanceLookalikeService;

    @PostMapping(value = "/create")
    public void createLookalike(@RequestBody String reqParams) throws AMSInvalidInputException, AMSRMIException {
        advanceLookalikeService.createLookalike(reqParams);
    }

    @PostMapping(value = "/create/callback")
    public void createLookalikeBitmapCallback(@RequestBody String reqParams) throws AMSInvalidInputException, AMSRMIException {
        advanceLookalikeService.createBitmapCallback(reqParams);
    }

    @GetMapping(value = "/{id}")
    public String getLookalikeResult(@PathVariable(value = "id") Long id) throws AMSInvalidInputException, AMSRMIException {
        return advanceLookalikeService.getLookalikeResultById(id);
    }

    @PutMapping(value = "/{id}/{value}/{size}")
    public void deployLookalike(@PathVariable(value = "id") Long id, @PathVariable(value = "value") int value, @PathVariable(value = "size") Long size)
            throws AMSInvalidInputException, AMSRMIException {
        advanceLookalikeService.deployLookalike(id, value, size);
    }
}
