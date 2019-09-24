package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 1:55 PM 7/2/2018
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DataTypeAndPriceAndOwnerVO {
    private String taxonomyId;
    private Double price;
    private String owner;
    private String dataType;
}
