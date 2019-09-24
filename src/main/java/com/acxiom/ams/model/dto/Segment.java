package com.acxiom.ams.model.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 16:52 12/21/2017
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class Segment {
    private String segmentName;
    private String taxonomyID;
    private JSONObject rules;
    private Long segmentId;
    private List<Long> distributeJobIds;
    private String segmentPath;

}
