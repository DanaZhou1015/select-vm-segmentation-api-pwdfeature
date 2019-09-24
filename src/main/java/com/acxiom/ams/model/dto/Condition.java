package com.acxiom.ams.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 4:27 PM 10/9/2018
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Condition {
    private String[] tenantName;
    private String[] role;
    private String[] userName;
}
