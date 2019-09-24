package com.acxiom.ams.model.dto.v2;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by cldong on 3/28/2018.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class VersionDtoDelete {
    private List<Long> idList;
    private String createdBy;
}
