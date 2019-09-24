package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:16 3/26/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Data
public class TenantExtVo {
    private String tenantId;
    private String key;
    private String value;
}
