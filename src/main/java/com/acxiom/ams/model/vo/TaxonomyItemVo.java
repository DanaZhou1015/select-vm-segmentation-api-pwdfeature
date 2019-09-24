package com.acxiom.ams.model.vo;

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
public class TaxonomyItemVo {
    private String objectId;
    private String taxonomyId;
    private String name;
    private String audienceCount;
    private String type;
    private String style;
    private String description;
    private Double price;
    private String owner;
}
