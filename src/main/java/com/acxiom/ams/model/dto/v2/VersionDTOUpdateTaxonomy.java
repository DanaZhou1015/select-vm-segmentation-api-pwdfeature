package com.acxiom.ams.model.dto.v2;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * Created by cldong on 3/21/2018.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class VersionDTOUpdateTaxonomy {
    @NotNull(message = "{message.error.version.versionId.null}")
    private Long versionId;
    @Length(message = "{message.error.version.name.length}")
    private String name;
    private Integer maxDepth;
    private Integer nodeNumber;
    @Length(min = 24, max = 24, message = "{message.error.version.treeId.length}")
    private String treeId;
    @Length(min = 24, max = 24, message = "{message.error.version.treeId.length}")
    private String draftTreeId;
    @Range(min = 0, max = 2, message = "{message.error.version.activeFlag.range}")
    private Integer activeFlag;
    private Integer syncFlag;
    private String datasourceId;
}
