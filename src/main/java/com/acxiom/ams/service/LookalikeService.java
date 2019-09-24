package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSRMIException;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 3:47 PM
 */
public interface LookalikeService {


    String getConfidenceByLevel(String tenantPath, String taxonomyId, String level) throws AMSException;

    String getConfidenceBySize(String tenantPath, String taxonomyId, Long size) throws AMSException;

    String getConfidenceByTaxonomyId(String taxonomyId) throws AMSException;

    String getConfidenceLiftByTaxonomyId(String taxonomyId) throws AMSException;

    String createJobBySeed(String reqParams) throws AMSException;

    String getJobs() throws AMSRMIException;

    void updateLookalikeStatus(String taxonomyId, String email, boolean status) throws AMSRMIException;
}
