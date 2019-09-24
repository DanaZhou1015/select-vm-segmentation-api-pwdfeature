package com.acxiom.ams.model.vo;

import com.acxiom.ams.model.em.SourceType;
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
public class SourceItem {
    private String id;
    private String name;
    private SourceType sourceType;
    private Long tenantId;
}
