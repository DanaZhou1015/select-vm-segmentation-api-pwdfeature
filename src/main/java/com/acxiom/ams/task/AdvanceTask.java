package com.acxiom.ams.task;

import com.acxiom.ams.common.utils.LogUtils;
import com.acxiom.ams.model.em.LookalikeType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.repository.AudiencePoJPA;
import com.acxiom.ams.service.AdvanceLookalikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * @Author: Michael Zhang
 * @Date: 2019-01-22 18:06
 **/
@Component
public class AdvanceTask implements CommandLineRunner {

    @Autowired
    AudiencePoJPA audiencePoJPA;

    @Autowired
    AdvanceLookalikeService advanceLookalikeService;

    @Override
    public void run(String... strings) throws Exception {
        List<AudiencePo> audiencePoRunningList = audiencePoJPA.findAllBySegmentStatusTypeAndLookalikeType(SegmentStatusType.LOOKALIKE_RUNNING, LookalikeType.ADVANCE);
        LogUtils.info(audiencePoRunningList.size());
        for (int i = 0; i < audiencePoRunningList.size(); i++) {
            AudiencePo audiencePo = audiencePoRunningList.get(i);
            if (audiencePo.getLookalikeJobId() != null) {
                ScheduledExecutorService scheduled = newScheduledThreadPool(1);
                advanceLookalikeService.getModel(audiencePo, scheduled, 0);
            }
        }
        List<AudiencePo> audiencePoList = audiencePoJPA.findAllBySegmentStatusTypeAndLookalikeType(SegmentStatusType.LOOKALIKE_PENDING, LookalikeType.ADVANCE);
        for (int i = 0; i < audiencePoList.size(); i++) {
            AudiencePo audiencePo = audiencePoList.get(i);
            ScheduledExecutorService scheduled = newScheduledThreadPool(1);
            advanceLookalikeService.deployModel(audiencePo, scheduled, 0);
        }
    }
}
