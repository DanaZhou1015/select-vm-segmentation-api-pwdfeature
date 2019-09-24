package com.acxiom.ams.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class DuplicateVersionDTO {
    private Long versionId;
    private String username;
}
