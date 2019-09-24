package com.acxiom.ams.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class AudienceShareDTO {
    private Long audienceId;
    private String name;
    private String taxonomyId;
}
