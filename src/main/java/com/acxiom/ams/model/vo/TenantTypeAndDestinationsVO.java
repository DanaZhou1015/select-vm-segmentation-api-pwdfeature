package com.acxiom.ams.model.vo;

import lombok.*;

import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 2:18 PM 9/12/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class TenantTypeAndDestinationsVO {
    private String platformType;
    private List<ChannelVo> channelVoList;
}
