package com.acxiom.ams.model.dto;

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
public class VersionDTO {
    private Long versionId;
    private String name;
    private String userName;
    private String treeId;
    private String userId;
}
