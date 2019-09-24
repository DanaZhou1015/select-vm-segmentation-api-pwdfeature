package com.acxiom.ams.model.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 15:01 3/15/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class SegmentAndAttributeVo {
    private List<TreeItemVo> treeItemVoList;
    private List<AudienceVo> audienceVoList;
}
