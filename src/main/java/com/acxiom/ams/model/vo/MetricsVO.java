package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 3:20 PM 8/7/2018
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MetricsVO {
    private BigDecimal builtCount;
    private BigDecimal distributedCount;
    private BigInteger overlapCount;
    private String tenantName;
    private String tenantPath;
}
