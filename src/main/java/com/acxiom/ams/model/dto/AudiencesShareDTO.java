package com.acxiom.ams.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class AudiencesShareDTO {
    private Date startDate;
    private Date endDate;
    private List<Long> folderIdList;
    private List<Long> audienceIdList;
    private String shareOwnerTenantId;
    private List<String> recipientTenantIdList;
    private List<AudienceShareDTO> shareAudienceDTOList;
    private Boolean pushFileFlag;
}
