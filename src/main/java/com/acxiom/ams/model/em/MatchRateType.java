package com.acxiom.ams.model.em;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 14:03 3/19/2018
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public enum MatchRateType {
    MATCH_SUCCESS(1,"Match Success"),MATCHING(2, "Matching"), MATCH_FAILED(3, "Match Failed");
    private Integer code;
    private String value;
}
