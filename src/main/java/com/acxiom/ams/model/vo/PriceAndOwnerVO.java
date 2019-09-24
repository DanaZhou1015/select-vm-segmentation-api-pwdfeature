package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 5:05 PM 5/4/2018
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PriceAndOwnerVO {
    private String taxonomyId;
    private Double price;
    private String owner;
}
