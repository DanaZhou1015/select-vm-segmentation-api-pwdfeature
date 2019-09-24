package com.acxiom.ams.controller.v2;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.dto.PageParam;
import com.acxiom.ams.model.dto.v2.CampaignDistributeParamDTO;
import com.acxiom.ams.model.dto.v2.UniverseActivityLogParamForReview;
import com.acxiom.ams.model.dto.v2.DistributeReturnParamV2;
import com.acxiom.ams.model.dto.v2.UniverseActivityLogParamForSearch;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.model.vo.UniverseActivityLogVo;
import com.acxiom.ams.service.DistributionService;

import javax.validation.Valid;

import com.acxiom.ams.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:12 12/14/2017
 */
@RestController
@RequestMapping(value = "/v2/distribution")
public class DistributionControllerV2 {

    @Autowired
    DistributionService distributionService;

    @Autowired
    ExportService exportService;

    @PostMapping(value = "/callback")
    public void callbackSegmentStatus(@RequestBody DistributeReturnParamV2 distributeReturnParam)
            throws AMSInvalidInputException {
        distributionService.callbackSegmentStatusV2(distributeReturnParam);
    }

    @PostMapping
    public void distributeCampaigns(@RequestBody @Valid CampaignDistributeParamDTO campaignDistributeParamDTO)
            throws AMSException {
        distributionService.distributeCampaigns(campaignDistributeParamDTO);
    }

    @PostMapping(value = "/activity/{tenantId}")
    public Page<UniverseActivityLogVo> SearchActivityList(@PathVariable(value = "tenantId") Long tenantId,
                                                          @RequestBody @Valid UniverseActivityLogParamForSearch universeActivityLogParamForSearch,
                                                          PageParam pageParam) throws AMSInvalidInputException {
        return distributionService.getActivityList(tenantId, universeActivityLogParamForSearch.getKeywords(),
                universeActivityLogParamForSearch.getClientList(), universeActivityLogParamForSearch.getStartDate(),
                universeActivityLogParamForSearch.getEndDate(), pageParam.getPageNo(), pageParam.getPageSize());
    }

    @PostMapping(value = "/review")
    public void updateActivity(@RequestBody UniverseActivityLogParamForReview universeActivityLogParam)
            throws AMSException {
        distributionService.reviewActivity(universeActivityLogParam);
    }

    @PostMapping(value = "/export/{tenantId}")
    public ResponseEntity<Resource> exportActivity(@PathVariable(value = "tenantId") Long tenantId,
                                                   @RequestBody @Valid UniverseActivityLogParamForReview universeActivityLogParam)
            throws AMSException {
        return exportService.exportUniverseActivityLog(universeActivityLogParam.getIdList(), tenantId);
    }

    @GetMapping(value = "/share/{tenantId}")
    public List<TenantVo> getShareUniverseTenantInfoByTenantId(@PathVariable(value = "tenantId") Long tenantId)
            throws AMSException {
        return distributionService.getShareUniverseTenantInfoByTenantId(tenantId);
    }
}
