package com.acxiom.ams.model.po;


import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * Universe Activity Log
 * @author michaelzhang
 */
@Entity
@Table(name = "universe_activity_log")
@SQLDelete(sql = "update universe_activity_log set IS_DELETED = 1 where ID = ?")
@Where(clause = "IS_DELETED = 0")
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class UniverseActivityLogPo extends BaseEntity {
    @Column(name = "AUDIENCE_ID")
    private Long audienceId;
    @Column(name = "AUDIENCE_NAME")
    private String audienceName;
    @Column(name = "AUDIENCE_STATUS")
    @Enumerated(EnumType.STRING)
    private SegmentStatusType audienceStatus;
    @Column(name = "AUDIENCE_TYPE")
    @Enumerated(EnumType.STRING)
    private FolderType audienceType;
    @Column(name = "AUDIENCE_COUNT")
    private Long audienceCount;
    @Column(name = "TENANT_ID")
    private Long tenantId;
    @Column(name = "TENANT_NAME")
    private String tenantName;
    @Column(name = "DESTINATION_ID")
    private Long destinationId;
    @Column(name = "UNIVERSE_NAME")
    private String universeName;
    @Column(name = "FOLDER_ID")
    private Long folderId;
    @Column(name = "AUDIENCE_RULE_JSON")
    private String audienceRuleJson;
    @Column(name = "LEGAL_FLAG")
    private Boolean legalFlag;
    @Column(name = "REQUEST_BY")
    private String requestBy;
    @Column(name = "REQUEST_EMAIL")
    private String requestEmail;
    @Column(name = "OWNER_TENANT_ID")
    private Long ownerTenantId;
    @Column(name = "APPROVAL_BY")
    private String approvalBy;
}
