package com.acxiom.ams.model.vo;

import com.acxiom.ams.model.em.SegmentStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 14:24 1/4/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class CampaignStatusVo {
    private long campaignId;
    private SegmentStatusType segmentStatusType;
}
