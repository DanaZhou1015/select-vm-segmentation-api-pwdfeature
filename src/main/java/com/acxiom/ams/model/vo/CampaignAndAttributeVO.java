package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 5:09 PM 5/8/2018
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class CampaignAndAttributeVO {

    private List<TreeItemVo> treeItemVoList;

    private List<SegmentVO> segmentVOList;
}
