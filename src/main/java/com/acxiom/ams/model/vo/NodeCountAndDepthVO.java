package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 17:34 4/26/2018
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NodeCountAndDepthVO {
   private int nodeCount;
   private int maxDepth;
}
