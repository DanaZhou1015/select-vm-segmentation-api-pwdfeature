package com.acxiom.ams.service.impl;

import com.acxiom.ams.api.ServiceAPI;
import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.model.dto.Condition;
import com.acxiom.ams.model.dto.ReceiveMessageDTO;
import com.acxiom.ams.model.vo.MetricsVO;
import com.acxiom.ams.service.MetricsService;
import com.acxiom.ams.util.Constant;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 4:13 PM 10/9/2018
 */
@Service
public class MetricsServiceImpl implements MetricsService {

    public static final String MESSAGE_SEND_TYPE = "Email";
    public static final String MESSAGE_TYPE = "actions and information";
    public static final String METRICS_MAIL_TITLE_TV = "Product metrics for tv in ";
    public static final String METRICS_MAIL_TITLE_2P = "Product metrics for 2p in ";
    public static final String MESSAGE_TOTAL = "Total:";
    public static final String LINE_BREAK = "<br>";
    public static final String BUILT_COUNT = "\"builtCount\":";
    public static final String DISTRIBUTED_COUNT = "\"distributedCount\":";
    public static final String OVERLAP_COUNT = "\"overlapCount\":";
    public static final String COLON = ":";

    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    @Value("${test.tenant.list}")
    private String testTenant;

    @Autowired
    ServiceAPI.MessageCenterAPI messageCenterAPI;

    @Override
    @Transactional(readOnly = true)
    public void listMetricsEveryFifteenDaysForTV(String startDate, String endDate) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createSQLQuery("select\n" +
                "sum(case when au.audience_status = 'CAMPAIGN_DISTRIBUTED' and DATE_FORMAT(au.update_time, " +
                "'%Y-%m-%d') >= '" + startDate +
                "' and DATE_FORMAT(au.update_time, '%Y-%m-%d') < '" + endDate + "' then 1 else 0 end)  as " +
                "distributedCount,\n" +
                "sum(case when au.audience_type = 'CAMPAIGN' and DATE_FORMAT(au.created_time,'%Y-%m-%d') >= '" +
                startDate +
                "' and DATE_FORMAT(au.created_time,'%Y-%m-%d') < '" + endDate + "' then 1 else 0 end)  as builtCount," +
                "\n" +
                "t.tenant_name as tenantName, \n" +
                "t.tenant_path as tenantPath \n" +
                "from audience au\n" +
                "LEFT JOIN tenant t on \n" +
                "t.id = au.tenant_id\n" +
                "LEFT JOIN sso.tenant te on te.tenant_id = t.tenant_id\n" +
                "WHERE t.tenant_name is not NULL\n" +
                "and te.platform_type = 'MVPD'\n" +
                "GROUP BY au.tenant_id");
        query.setResultTransformer(Transformers.aliasToBean(MetricsVO.class));
        List<MetricsVO> list = query.list();
        BigDecimal totalBuiltCount = new BigDecimal(0);
        BigDecimal totalDistributedCount = new BigDecimal(0);
        if (StringUtils.isNotBlank(testTenant)) {
            List<String> testTenantList = Arrays.asList(testTenant.split(Constant.COMMA));
            list = list.stream().filter(metricsVO -> !testTenantList.contains(metricsVO.getTenantPath()))
                    .collect(Collectors.toList());
        }
        for (MetricsVO metricsVO : list) {
            totalBuiltCount = totalBuiltCount.add(metricsVO.getBuiltCount());
            totalDistributedCount = totalDistributedCount.add(metricsVO.getDistributedCount());
        }
        sendEmail(list, startDate, endDate, totalBuiltCount, totalDistributedCount, null, false);
    }

    @Override
    @Transactional(readOnly = true)
    public void listMetricsEveryFifteenDaysFor2P(String startDate, String endDate) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query query = currentSession.createSQLQuery("select\n" +
                "sum(case when au.audience_status = 'SEGMENT_DISTRIBUTED' and DATE_FORMAT(au.update_time,'%Y-%m-%d')" +
                " >= '" + startDate + "' and DATE_FORMAT(au.update_time,'%Y-%m-%d') < '" + endDate +
                "' then 1 else 0 end)  as distributedCount,\n" +
                "sum(case when au.audience_type = 'SAVED_SEGMENT' and DATE_FORMAT(au.created_time,'%Y-%m-%d')" +
                " >= '" + startDate + "' and DATE_FORMAT(au.created_time,'%Y-%m-%d') < '" + endDate +
                "' then 1 else 0 end)  as builtCount,\n" +
                "IFNULL(oc.count,0) as overlapCount,\n" +
                "t.tenant_name as tenantName,\n" +
                "t.tenant_path as tenantPath \n" +
                "from audience au \n" +
                "LEFT JOIN tenant t on \n" +
                "t.id = au.tenant_id\n" +
                "LEFT JOIN (select count(*) count ,st.tenant_sysname, st.display_name, st.tenant_id\n" +
                "   from data_source.overlap ol\n" +
                "LEFT JOIN data_source.data_source_content ds on ds.id = ol.primary_datasource_id\n" +
                "LEFT JOIN sso.tenant st on st.tenant_id = ds.tenant_id \n" +
                "   where DATE_FORMAT(ol.update_time,'%Y-%m-%d') >= '" + startDate +
                "' and DATE_FORMAT(ol.update_time,'%Y-%m-%d') < '" + endDate +
                "' and st.tenant_sysname is not null GROUP BY  st.tenant_sysname) oc on oc.tenant_id = t.tenant_id\n" +
                "LEFT JOIN sso.tenant te on te.tenant_id = t.tenant_id\n" +
                "WHERE t.tenant_name is not NULL\n" +
                "and te.platform_type = 'BRAND'\n" +
                "GROUP BY au.tenant_id");
        query.setResultTransformer(Transformers.aliasToBean(MetricsVO.class));
        List<MetricsVO> list = query.list();
        BigDecimal totalBuiltCount = new BigDecimal(0);
        BigDecimal totalDistributedCount = new BigDecimal(0);
        BigInteger totalOverlapCount = new BigInteger("0");
        if (StringUtils.isNotBlank(testTenant)) {
            List<String> testTenantList = Arrays.asList(testTenant.split(Constant.COMMA));
            list = list.stream().filter(metricsVO -> !testTenantList.contains(metricsVO.getTenantPath()))
                    .collect(Collectors.toList());
        }
        for (MetricsVO metricsVO : list) {
            totalBuiltCount = totalBuiltCount.add(metricsVO.getBuiltCount());
            totalDistributedCount = totalDistributedCount.add(metricsVO.getDistributedCount());
            totalOverlapCount = totalOverlapCount.add(metricsVO.getOverlapCount());
        }
       sendEmail(list, startDate, endDate, totalBuiltCount, totalDistributedCount, totalOverlapCount, true);
    }

    private void sendEmail(List<MetricsVO> metricsVOList, String startDate, String endDate, BigDecimal
            totalBuiltCount, BigDecimal totalDistributedCount, BigInteger totalOverlapCount, Boolean overlapFlag) {
        StringBuffer emailContent = new StringBuffer();
        String emailTitle = METRICS_MAIL_TITLE_TV.concat(startDate).concat(" ~ ").concat(endDate);
        if (overlapFlag) {
            emailTitle = METRICS_MAIL_TITLE_2P.concat(startDate).concat(" ~ ").concat(endDate);
        }
        emailContent.append(MESSAGE_TOTAL);
        emailContent.append(LINE_BREAK);
        emailContent.append(BUILT_COUNT).append(totalBuiltCount).append(Constant.COMMA).append(LINE_BREAK);
        if (overlapFlag) {
            emailContent.append(DISTRIBUTED_COUNT).append(totalDistributedCount).append(Constant.COMMA).append
                    (LINE_BREAK);
            emailContent.append(OVERLAP_COUNT).append(totalOverlapCount).append(LINE_BREAK);
        } else {
            emailContent.append(DISTRIBUTED_COUNT).append(totalDistributedCount).append(LINE_BREAK);
        }
        emailContent.append(LINE_BREAK);
        for (MetricsVO metricsVO : metricsVOList) {
            emailContent.append(metricsVO.getTenantName()).append(COLON).append(LINE_BREAK);
            emailContent.append(BUILT_COUNT).append(metricsVO.getBuiltCount()).append(Constant.COMMA).append
                    (LINE_BREAK);
            if (overlapFlag) {
                emailContent.append(DISTRIBUTED_COUNT).append(metricsVO.getDistributedCount()).append(Constant.COMMA)
                        .append(LINE_BREAK);
                emailContent.append(OVERLAP_COUNT).append(metricsVO.getOverlapCount()).append(LINE_BREAK);
            } else {
                emailContent.append(DISTRIBUTED_COUNT).append(metricsVO.getDistributedCount()).append(LINE_BREAK);
            }
            emailContent.append(LINE_BREAK);
        }
        ReceiveMessageDTO receiveMessageDTO = new ReceiveMessageDTO();
        receiveMessageDTO.setMessageTitle(emailTitle);
        receiveMessageDTO.setMessageContent(emailContent.toString());
        receiveMessageDTO.setSendType(MESSAGE_SEND_TYPE);
        receiveMessageDTO.setMessageType(MESSAGE_TYPE);
        Condition condition = new Condition();
        condition.setRole(new String[]{Constant.SEND_EMAIL_ROLE});
        receiveMessageDTO.setCondition(condition);
        if (StringUtils.isNotBlank(emailContent.toString())) {
            try {
                messageCenterAPI.sendEmail(receiveMessageDTO);
            } catch (AMSRMIException e) {
                LogUtils.error(e);
                LogUtils.error("Failed to send email with product metrics!");
            }
        }
    }
}
