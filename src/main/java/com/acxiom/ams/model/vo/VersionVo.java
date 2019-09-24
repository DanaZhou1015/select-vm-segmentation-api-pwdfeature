package com.acxiom.ams.model.vo;

import com.acxiom.ams.model.em.TemplateStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by cldong on 12/5/2017.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class VersionVo {
    private String versionId;
    private String versionName;
    private String versionTreeId;
    private Integer maxDepth;
    private Integer nodeNumber;
    private String updatedBy;
    private String createdBy;
    private String createdTime;
    private String updateTime;
    private TemplateStatusType versionOperationFlag;
    private Integer syncFlag;
    private String datasourceId;
    private Long tenantId;
}
