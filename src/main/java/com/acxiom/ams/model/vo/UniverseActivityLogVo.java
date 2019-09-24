package com.acxiom.ams.model.vo;

import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class UniverseActivityLogVo {
    private Long id;
    private Long audienceId;
    private String audienceName;
    private SegmentStatusType audienceStatus;
    private FolderType audienceType;
    private Long audienceCount;
    private Long tenantId;
    private String tenantName;
    private Long destinationId;
    private String universeName;
    private Long folderId;
    private String audienceRuleJson;
    private Date createdTime;
    private Date updateTime;
    private String createdBy;
    private Boolean legalFlag;
    private String requestBy;
    private String requestEmail;
    private Long ownerTenantId;
    private String approvalBy;
}
