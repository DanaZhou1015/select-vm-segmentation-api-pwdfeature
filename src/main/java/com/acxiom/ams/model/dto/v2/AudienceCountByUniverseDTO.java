package com.acxiom.ams.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class AudienceCountByUniverseDTO {
    private Long universeId;
    private Long audienceCountByUniverse;
    private List<Long> segmentCounts;
}
