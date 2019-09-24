package com.acxiom.ams.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class UniverseDTO {
    @Size(min = 1, max = 240, message = "{message.error.universe.name}")
    private String universeName;
    @NotNull(message = "{message.error.universe.rule.json}")
    private String universeRuleJson;
    @Size(min = 1, max = 255, message = "{message.error.universe.createdBy}")
    private String createdBy;
    @NotNull(message = "{message.error.universe.count}")
    private Long universeCount;
    private float universeThreshold;
    private Long segmentThreshold;
}
