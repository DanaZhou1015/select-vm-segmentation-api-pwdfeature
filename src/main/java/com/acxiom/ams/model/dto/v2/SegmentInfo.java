package com.acxiom.ams.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * @Author: Fermi.Tang
 * @Date: Created in 4:29 PM 9/10/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class SegmentInfo {
    private String segmentName;
    private String segmentCount;
}
