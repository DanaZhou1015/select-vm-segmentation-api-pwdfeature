package com.acxiom.ams.model.po;

import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.LookalikeType;
import com.acxiom.ams.model.em.MatchRateType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.util.Constant;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Created by cldong on 12/5/2017.
 */
@Entity
@Table(name = "audience")
@SQLDelete(sql = "Update audience set IS_DELETED = 1 where ID = ?")
@Where(clause = "IS_DELETED = 0")
@NamedNativeQueries(value = {@NamedNativeQuery(name = "get_segment_count_by_folderId", query = "select count(*) from audience where FIND_IN_SET(folder_id, getChildList(:rootId)) AND IS_DELETED = 0"),
        @NamedNativeQuery(name = "get_all_segment_by_folderId", query = "select * from audience where FIND_IN_SET(folder_id, getChildList(:rootId)) AND IS_DELETED = 0", resultClass = AudiencePo.class)
})
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
@ToString(exclude = "folderPo")
public class AudiencePo extends BaseEntity {
    @Column(name = "AUDIENCE_NAME")
    private String name;
    @Lob
    @Column(name = "AUDIENCE_RULE_JSON")
    private String ruleJson;
    @Column(name = "AUDIENCE_COST")
    private String cost;
    @Column(name = "AUDIENCE_COUNT")
    private Long count;
    @Column(name = "AUDIENCE_STATUS")
    @Enumerated(EnumType.STRING)
    private SegmentStatusType segmentStatusType;
    @Column(name = "AUDIENCE_TYPE")
    @Enumerated(EnumType.STRING)
    private FolderType audienceType;
    @Column(name = "AUDIENCE_DESCRIPTION")
    private String description;
    @Column(name = "AUDIENCE_CODE")
    private String code;
    @Column(name = "UNIVERSE_IDS")
    private String universeIds;
    // create new Item and have a new bitmap taxonomy ID
    @Column(name = "TAXONOMY_ID")
    private String taxonomyId;
    @Column(name = "TENANT_ID")
    private Long tenantId;
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLDER_ID", nullable = false)
    private FolderPo folderPo;
    @Column(name = "MATCH_RATE")
    private String matchRate;
    @Column(name = "MATCH_STATUS")
    @Enumerated(EnumType.STRING)
    private MatchRateType matchStatus;
    @Column(name = "CONNECT_FILENAME")
    private String filename;
    @Column(name = "FROZEN_AUDIENCE_COUNT")
    private Long frozenCount;
    @Column(name = "DISTRIBUTION_FLAG")
    private Boolean distributionFlag;
    @Column(name = "LEGAL_FLAG")
    private Boolean legalFlag;
    @Column(name = "LOOKALIKE_TYPE")
    @Enumerated(EnumType.STRING)
    private LookalikeType lookalikeType;
    @Column(name = "LOOKALIKE_INCLUDE")
    private boolean lookalikeInclude;
    @Column(name = "LOOKALIKE_FILE_PATH")
    private String lookalikeFilePath;
    @Column(name = "LOOKALIKE_JOB_ID")
    private String lookalikeJobId;
    @Column(name = "LOOKALIKE_RESULT")
    private String lookalikeResult;
    @Column(name = "LOOKALIKE_REACH_VALUE")
    private Integer lookalikeReachValue;
    @Column(name = "TEST_COUNT")
    private Long testCount;
    @Column(name = "CONTROL_COUNT")
    private Long controlCount;
    @Column(name = "FROZEN_TEST_COUNT")
    private Long frozenTestCount;
    @Column(name = "FROZEN_CONTROL_COUNT")
    private Long frozenControlCount;
    @Column(name = "ERROR_CODE")
    private String errorCode;
    @Column(name = "UNIVERSE_SEGMENT_COUNT_JSON")
    private String universeSegmentCountJson;
    @Column(name = "DESTINATION_IDS")
    private String destinationIds;
    @Transient
    private String distribution = Constant.DISTRIBUTION_PLATFORM_VALUE;
}
