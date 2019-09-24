package com.acxiom.ams.task;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 3:54 PM 10/9/2018
 */

import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.model.po.SystemParamPo;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.repository.SystemParamPoJPA;
import com.acxiom.ams.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
public class MetricsJobTask {

    @Value("${scheduled.enable}")
    private String scheduledEnable;
    @Value("${task.exec.time.rate}")
    private String taskExecTimeRate;
    @Autowired
    SystemParamPoJPA systemParamJPA;
    @Autowired
    AudiencePoJPA audiencePoJPA;
    @Autowired
    MetricsService metricsService;

    public static final String METRICS_SCAN_JOB_KEY = "Metrics_Scan_Job";

    public static final Long DAYS_OF_TWO_WEEKS = 14L;

    @Scheduled(cron = "${task.exec.time.rate}")
    public void metricsTask() {
        if (!Boolean.parseBoolean(scheduledEnable)) {
            return;
        }
        LocalDate localDate = LocalDate.now();
        LocalDate lastRunDate;
        SystemParamPo systemParam = systemParamJPA.findByJobKey(METRICS_SCAN_JOB_KEY);
        LogUtils.info("Metrics task run at " + localDate);
        if (Optional.ofNullable(systemParam).isPresent()) {
            lastRunDate = systemParam.getLastTaskRunTime();
            LogUtils.info("Last run time in db is:" + lastRunDate);
            Long days = ChronoUnit.DAYS.between(lastRunDate, localDate);
            if (days < DAYS_OF_TWO_WEEKS) {
                return;
            }
        }
        lastRunDate = localDate.minusDays(DAYS_OF_TWO_WEEKS);
        LogUtils.info("Count last run time is:" + lastRunDate);
        systemParamJPA.save(new SystemParamPo(METRICS_SCAN_JOB_KEY, localDate));
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDate = lastRunDate.format(formatter);
            String endDate = localDate.format(formatter);
            metricsService.listMetricsEveryFifteenDaysForTV(startDate, endDate);
            metricsService.listMetricsEveryFifteenDaysFor2P(startDate, endDate);
        } catch (Exception e) {
            LogUtils.error(e.getMessage());
            LogUtils.error("Failed to execute metrics task!");
        }
    }
}
