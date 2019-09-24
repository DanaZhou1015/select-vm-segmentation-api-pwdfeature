package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 09:22 3/13/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class PermissionVo {
    private int count;
    private Long id;
    private Boolean edit;
    private Boolean distribute;
    private Boolean copy;
    private Boolean refresh;
    private Boolean delete;
}
