package com.acxiom.ams.controller.v2;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.dto.ProfilingDTO;
import com.acxiom.ams.model.dto.v2.ExportInsightDTO;
import com.acxiom.ams.model.vo.ProfilingVo;
import com.acxiom.ams.service.ProfilingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 2:33 PM
 */
@RestController
@Validated
@RequestMapping(value = "/v2/profiling")
public class ProfilingControllerV2 {

    @Autowired
    ProfilingService profilingService;

    @GetMapping(value = "/list/{destinationId}")
    public List<ProfilingVo> listInsightsByDestinationId(
            @PathVariable(value = "destinationId") Long destinationId) {
        return profilingService.listInsightsByDestinationId(destinationId);
    }

    @GetMapping(value = "/{destinationId}")
    public ProfilingVo getInsightByDestinationId(
            @PathVariable(value = "destinationId") Long destinationId) {
        return profilingService.getInsightByDestinationId(destinationId);
    }

    @PostMapping(value = "/json/{tenantId}")
    public String fillInsight(
            @PathVariable(value = "tenantId") Long tenantId,
            @RequestBody String req) throws AMSException {
        return profilingService.fillInsight(tenantId, req);
    }

    @PostMapping(value = "/export/insight")
    public ResponseEntity<Resource> exportInsight(@RequestBody @Valid ExportInsightDTO exportInsightDTO)
            throws AMSException {
        return profilingService.exportInsight(exportInsightDTO);
    }

    @PutMapping(value = "/active/{destinationId}/{profilingId}")
    public void setActiveInsight(
            @PathVariable(value = "destinationId") Long destinationId,
            @PathVariable(value = "profilingId") Long profilingId) throws AMSInvalidInputException {
        profilingService.setActiveInsight(destinationId, profilingId);
    }

    @PostMapping(value = "/{tenantId}/{destinationId}")
    public long saveInsight(
            @PathVariable(value = "tenantId") Long tenantId,
            @PathVariable(value = "destinationId") Long destinationId,
            @RequestBody @Valid ProfilingDTO profilingDTO) throws AMSInvalidInputException {
        return profilingService.saveInsight(tenantId, destinationId, profilingDTO);
    }
}
