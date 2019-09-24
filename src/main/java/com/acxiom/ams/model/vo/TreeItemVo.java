package com.acxiom.ams.model.vo;

import com.acxiom.ams.model.em.LookalikeType;
import com.acxiom.ams.model.em.SegmentStatusType;
import com.acxiom.ams.model.em.SourceType;
import com.acxiom.ams.model.em.TreeItemType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by cldong on 12/6/2017.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class TreeItemVo {
    private String id;
    private String name;
    private Long value;
    private String taxonomyId;
    private Long tenantId;
    private String description;
    private Double price;
    private TreeItemType type;
    private SourceType sourceType;
    private SegmentStatusType statusType;
    private List<TaxonomyItemVo> taxonomyItemVoList;
    private String owner;
    private String dataType;
    private LookalikeType lookalikeType;
    private String ruleJson;
    private Long testCount;
    private Long controlCount;
    private String audienceCount;
    private String objectId;
    private String errorCode;
}
