package com.acxiom.ams.model.vo;

import lombok.*;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
@ToString
public class WhiteTenant {
    private Long requestId;
    private String tenantId;
    private String shareTenantId;
    private String shareTenantSysname;
    private String shareTenantDisplayName;
    private Integer activeFlag;
    private Integer requestType;
}
