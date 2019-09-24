package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Allen.Qu
 * @Date: Created in 17:32 13/12/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class ShareVO {
    private Long shareId;
    private String shareName;
    private Integer type;
    private Date startDate;
    private Date endDate;
    private Integer status;
    private String shareRule;
    private String shareOwnerTenantId;
    private String recipientTenantId;
    private Integer versionId;
    private String stepInfo;
}
