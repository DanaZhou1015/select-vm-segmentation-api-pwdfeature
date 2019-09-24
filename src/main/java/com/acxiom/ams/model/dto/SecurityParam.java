package com.acxiom.ams.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:44 2/7/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class SecurityParam {
    private List<Long> folderIdList;
    private List<Long> campaignIdList;
    private Long tenantId;
}
