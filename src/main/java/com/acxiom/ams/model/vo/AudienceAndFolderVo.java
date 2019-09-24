package com.acxiom.ams.model.vo;

import com.acxiom.ams.model.em.FolderType;
import com.acxiom.ams.model.em.LookalikeType;
import com.acxiom.ams.model.em.SegmentStatusType;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 15:19 12/5/2017
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class AudienceAndFolderVo implements Comparable {

    private long id;
    private String name;
    private String ruleJsonDisplay;
    private long count;
    private SegmentStatusType segmentStatusType;
    private FolderType type;
    private String createdBy;
    private String cost;
    private Date updateTime;
    private String distribution;
    private String taxonomyId;
    private String universeIds;
    private String universeName;
    private String description;
    private FolderType audienceType;
    private List<AudienceAndFolderVo> audienceAndFolderVoList;
    private long frozenCount;
    private Boolean distributionFlag;
    private Boolean legalFlag;
    private LookalikeType lookalikeType;
    private String lookalikeResult;
    private Long testCount;
    private Long controlCount;
    private Long frozenTestCount;
    private Long frozenControlCount;
    private String errorCode;
    private Date createdTime;

    @Override
    public int compareTo(Object o) {
        AudienceAndFolderVo vo = (AudienceAndFolderVo) o;
        if (this.type == vo.type) {
            return vo.getUpdateTime().compareTo(this.getUpdateTime());
        } else {
            if (this.type == null) {
                return 1;
            }
            return -1;
        }
    }
}
