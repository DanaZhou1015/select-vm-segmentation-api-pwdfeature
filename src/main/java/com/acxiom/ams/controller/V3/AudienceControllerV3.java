package com.acxiom.ams.controller.V3;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.service.AudiencePoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 5:59 PM 5/9/2018
 */
@RestController
@RequestMapping(value = "/v3/audience")
public class AudienceControllerV3 {
    @Autowired
    AudiencePoService audiencePoService;
    @PostMapping(value = "/calculate/{tenantId}/{userId}")
    public Long calculateV3(@PathVariable(value = "tenantId") Long tenantId,
                           @PathVariable(value = "userId") String userId,
                           @RequestBody String rule)
            throws AMSException {
        return audiencePoService.calculateForNonTv(tenantId, userId, rule);
    }
}
