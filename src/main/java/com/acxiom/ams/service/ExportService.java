package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * export
 *
 * @author michaelzhang
 */
public interface ExportService {

    /**
     * export universe activity log
     *
     * @param idList
     * @param tenantId
     * @return
     * @throws AMSException
     */
    ResponseEntity<Resource> exportUniverseActivityLog(List<Long> idList, Long tenantId) throws AMSException;
}
