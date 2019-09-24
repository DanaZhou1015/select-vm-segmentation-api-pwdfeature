package com.acxiom.ams.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 18:25 12/21/2017
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class DistributeReturnParamV2 {
    private Boolean status;
    @NotNull(message = "{distribute.campaignId.notNull}")
    private Long campaignId;
    private String campaignName;
    private String message;
    @NotNull(message = "{distribute.jobId.notNull}")
    private Long jobId;
    @NotNull(message = "{distribute.universeId.notNull}")
    private Long universeId;
}
