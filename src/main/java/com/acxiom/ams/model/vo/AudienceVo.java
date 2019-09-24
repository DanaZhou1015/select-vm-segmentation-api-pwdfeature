package com.acxiom.ams.model.vo;

import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.SegmentStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by cldong on 12/11/2017.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class AudienceVo {
    private Integer id;
    private String name;
    private String ruleJson;
    private Long count;
    private String cost;
    private SegmentStatusType statusType;
    private FolderType audienceType;
    private String taxonomyId;
    private String universeIds;
    private Long folderId;
    private String segmentCode;
    private String description;
    private String createdBy;
    private Boolean distributionFlag;
}
