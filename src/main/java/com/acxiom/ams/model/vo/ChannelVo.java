package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 09:29 1/4/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class ChannelVo {
    private long id;
    private String defaultRule;
    private String channelName;
    private String tenantId;
    private String path;
    private String infoBaseId;
}
