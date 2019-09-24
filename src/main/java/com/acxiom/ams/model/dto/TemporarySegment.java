package com.acxiom.ams.model.dto;

import com.acxiom.ams.model.em.FolderType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by cldong on 12/8/2017.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class TemporarySegment {
    @Size(min = 1, max = 240, message = "{temporarySegment.name.size}")
    private String name;
    @NotNull(message = "{temporarySegment.rule.notNull}")
    private String rule;
    @Size(min = 1, max = 255, message = "{temporarySegment.createdBy.size}")
    private String createdBy;
    @NotNull(message = "{temporarySegment.audienceType.notNull}")
    private FolderType audienceType;
    @NotNull(message = "{temporarySegment.folderId.notNull}")
    private Long folderId;
    @NotNull(message = "{temporarySegment.userId.notNull}")
    private String userId;
    // Additional optional attributes
    private String cost;
    private String segmentCode;
    private String description;
    private Long count;
}
