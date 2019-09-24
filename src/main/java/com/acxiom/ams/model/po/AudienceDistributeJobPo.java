package com.acxiom.ams.model.po;

import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 16:08 12/21/2017
 */
@Entity
@Table(name = "audience_distribute_job")
@SQLDelete(sql = "update audience_distribute_job set IS_DELETED = 1 where ID = ?")
@Where(clause = "IS_DELETED = 0")
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class AudienceDistributeJobPo extends BaseEntity {

    @Column(name = "distribute_job_tenant_id", nullable = false)
    private long tenantId;
    @Column(name = "distribute_job_audience_id", nullable = false)
    private long audienceId;
    @Column(name = "distribute_job_notice_email", nullable = false)
    private String noticeEmail;
    @Column(name = "distribute_job_update_by")
    private String updateBy;
    @Column(name = "distribute_job_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SegmentStatusType status;
    @Lob
    @Column(name = "distribute_job_rules")
    private String rules;
    @Column(name = "distribute_job_destination_id")
    private Long destinationId;
    @Column(name = "distribute_job_audience_type")
    @Enumerated(EnumType.STRING)
    private FolderType audienceType;
}
