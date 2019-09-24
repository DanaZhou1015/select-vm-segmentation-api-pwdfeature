package com.acxiom.ams.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 15:50 3/28/2018
 */

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class SegmentDTO {
    private List<String> segmentIdList;
    private Long campaignId;
}
