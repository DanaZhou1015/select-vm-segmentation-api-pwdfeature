package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.dto.ProfilingDTO;
import com.acxiom.ams.model.dto.v2.ExportInsightDTO;
import com.acxiom.ams.model.vo.ProfilingVo;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import java.util.List;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 3:47 PM
 */
public interface ProfilingService {
    List<ProfilingVo> getProfilingByTenantId(Long tenantId, Long profilingId) throws AMSInvalidInputException;

    ProfilingVo getActiveProfilingByTenantId(Long tenantId) throws AMSInvalidInputException;

    void setActiveProfiling(Long tenantId, Long profilingId) throws AMSInvalidInputException;

    boolean deleteProfilingById(Long tenantId, Long profilingId) throws AMSInvalidInputException;

    ProfilingVo getInsightByDestinationId(Long destinationId);

    long saveProfiling(Long tenantId, ProfilingDTO profilingDTO) throws AMSInvalidInputException;

    String profiling(Long tenantId, String req) throws AMSException;

    String fillInsight(Long tenantId, String req) throws AMSException;

    String profilingV7(Long tenantId, String req) throws AMSException;

    ResponseEntity<Resource> exportInsight(ExportInsightDTO exportInsightDTO) throws
            AMSException;

    void setActiveInsight(Long destinationId, Long profilingId) throws AMSInvalidInputException;

    long saveInsight(Long tenantId, Long destinationId, ProfilingDTO profilingDTO) throws AMSInvalidInputException;

    List<ProfilingVo> listInsightsByDestinationId(Long destinationId);
}
