package com.acxiom.ams.model.dto.v2;

import com.acxiom.ams.model.em.FolderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:37 AM 11/1/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class CampaignParam {
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
    @NotNull(message = "{temporarySegment.audienceCount.notNull}")
    private Long audienceCount;
    private String cost;
    @NotNull(message = "{campaign.universe.id.notNull}")
    private List<Long> universeIdList;
    @NotNull(message = "{audience.count.by.universe.notNull}")
    private List<AudienceCountByUniverseDTO> audienceCountByUniverseDTOList;
    private String segmentCode;
    private String description;
}
