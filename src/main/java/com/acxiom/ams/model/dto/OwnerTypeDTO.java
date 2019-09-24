package com.acxiom.ams.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 17:26 3/26/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class OwnerTypeDTO {
    private List<String> ownerList;
    private List<NodeDTO> nodeDTOList;
    private List<SegmentDTO> segmentDTOList;
}
