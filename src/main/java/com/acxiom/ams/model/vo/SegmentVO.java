package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 5:10 PM 5/8/2018
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class SegmentVO {
    private Long campaignId;
    private String campaignName;
    private String id;
    private Double testControl;
    private Long count;
    private String name;
    private String include;
    private String exclude;
}
