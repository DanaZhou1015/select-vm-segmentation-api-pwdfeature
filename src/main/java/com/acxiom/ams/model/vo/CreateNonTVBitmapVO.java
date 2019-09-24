package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 2:02 PM 9/6/2018
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CreateNonTVBitmapVO {
    private String taxonomyId;
    private Long count;
    private boolean finishFlag;
    private boolean refreshFlag;
}
