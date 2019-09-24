package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by cldong on 12/12/2017.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class TenantVo {
    private Long id;
    private String name;
    private String path;
    private String tenantId;
    private String platformType;
    private Integer iconId;
    private String tenantSysName;
    private String displayName;
    private Boolean usePpid;
    private String spaceId;
}
