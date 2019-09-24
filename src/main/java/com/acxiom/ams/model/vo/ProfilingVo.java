package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/5/2017 3:49 PM
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class ProfilingVo {
    private long profilingId;
    private String profilingDescription;
    private String profilingName;
    private String profilingJson;
    private String createBy;
    private String createdTime;
    private String updateTime;
    private boolean active;
    private Long destinationId;
    private String channelName;
}
