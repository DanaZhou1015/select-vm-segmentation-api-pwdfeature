package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by cldong on 1/3/2018.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class DestinationVo {
    private Long destinationId;
    private String destinationName;
    private Long tenantId;
    private String tenantPath;
    private String universePath;
    private Boolean lastUsedFlag;
    private Float universeThreshold;
    private Long universeCount;
    private String ownerTenantName;
}
