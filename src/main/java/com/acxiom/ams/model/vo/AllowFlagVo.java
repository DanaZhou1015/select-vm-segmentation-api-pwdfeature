package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 10:47 3/29/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class AllowFlagVo {
  private Boolean distributeFlag;
  private Boolean deleteFlag;
}
