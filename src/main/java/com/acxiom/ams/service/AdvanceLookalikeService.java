package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.model.po.AudiencePo;

import java.util.concurrent.ScheduledExecutorService;

public interface AdvanceLookalikeService {

    void createLookalike(String reqParams) throws AMSRMIException, AMSInvalidInputException;

    void createBitmapCallback(String reqParams) throws AMSRMIException, AMSInvalidInputException;

    void getModel(AudiencePo audiencePo, ScheduledExecutorService scheduled, Integer initialDelay) throws AMSRMIException, AMSInvalidInputException;

    String getLookalikeResultById(Long id) throws AMSRMIException, AMSInvalidInputException;

    void deployLookalike(Long id, int value, Long size) throws AMSRMIException, AMSInvalidInputException;

    void deployModel(AudiencePo audiencePo, ScheduledExecutorService scheduled, Integer initialDelay) throws AMSRMIException, AMSInvalidInputException;
}
