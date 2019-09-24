package com.acxiom.ams.service;

import com.acxiom.ams.common.exception.AMSException;
import com.acxiom.ams.common.exception.AMSInvalidInputException;
import com.acxiom.ams.model.dto.DistributeParam;
import com.acxiom.ams.model.dto.DistributeReturnParam;
import com.acxiom.ams.model.dto.v2.CampaignDistributeParamDTO;
import com.acxiom.ams.model.dto.v2.DistributeReturnParamV2;
import com.acxiom.ams.model.dto.v2.UniverseActivityLogParamForReview;
import com.acxiom.ams.model.vo.TenantVo;
import com.acxiom.ams.model.vo.UniverseActivityLogVo;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 14:47 12/14/2017
 */
public interface DistributionService {

    String distributeSegments(DistributeParam distributeParam)
            throws AMSException;

    void callbackSegmentStatus(DistributeReturnParam distributeReturnParam);

    void distributeCampaigns(CampaignDistributeParamDTO distributeParamV2)
            throws AMSException;

    void callbackSegmentStatusV2(DistributeReturnParamV2 distributeReturnParam)
            throws AMSInvalidInputException;

    void distributeSegmentsByTenantPath(String tenantPath) throws AMSException;

    /**
     * search activity by page
     *
     * @param tenantId
     * @param keywords
     * @param clientList
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<UniverseActivityLogVo> getActivityList(Long tenantId, String keywords, List<Long> clientList, Date startDate,
                                                Date endDate, Integer pageNo, Integer pageSize) throws AMSInvalidInputException;

    /**
     * review activity
     *
     * @param distributeRequestParam
     * @throws AMSException
     */
    void reviewActivity(UniverseActivityLogParamForReview distributeRequestParam) throws AMSException;

    /**
     * get share universe tenant info
     *
     * @param tenantId
     * @return
     * @throws AMSException
     */
    List<TenantVo> getShareUniverseTenantInfoByTenantId(Long tenantId) throws AMSException;
}
