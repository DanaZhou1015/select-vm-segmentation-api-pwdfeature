package com.acxiom.ams.model.po;

import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.em.UniverseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "universe")
@SQLDelete(sql = "Update universe set IS_DELETED = 1 where ID = ?")
@Where(clause = "IS_DELETED = 0")
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class UniversePo extends BaseEntity {
    @Column(name = "UNIVERSE_NAME")
    private String universeName;
    @Lob
    @Column(name = "UNIVERSE_RULE_JSON")
    private String universeRuleJson;
    @Column(name = "UNIVERSE_COUNT")
    private Long universeCount;
    @Column(name = "UNIVERSE_STATUS")
    @Enumerated(EnumType.STRING)
    private SegmentStatusType universeStatus;
    @Column(name = "TENANT_PATH")
    private String tenantPath;
    @Column(name = "UNIVERSE_SYSTEM_NAME")
    private String universeSystemName;
    @Column(name = "TENANT_ID")
    private Long tenantId;
    @Column(name = "universe_threshold")
    private Float universeThreshold;
    @Column(name = "UNIVERSE_TYPE")
    @Enumerated(EnumType.STRING)
    private UniverseType universeType;
    @Column(name = "UNIVERSE_JOB_ID")
    private String universeJobId;
    @Column(name = "OWNER_TENANT_PATH")
    private String ownerTenantPath;
    @Column(name = "SEGMENT_THRESHOLD")
    private Long segmentThreshold;
    @Transient
    private String icon;
    @Transient
    private Boolean lastUsedFlag;
    @Transient
    private String ownerTenantName;
}
