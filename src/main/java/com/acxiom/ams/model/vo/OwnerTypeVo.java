package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 08:52 3/22/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class OwnerTypeVo {
    private String owner;
    private String ownerType;
}
