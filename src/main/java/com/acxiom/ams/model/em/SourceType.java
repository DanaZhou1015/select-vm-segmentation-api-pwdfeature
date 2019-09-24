package com.acxiom.ams.model.em;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by cldong on 12/6/2017.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public enum SourceType {
    TAXONOMY(0, "Taxonomy Item"),
    LOOKALIKE(1,"Lookalike Group"),
    SEGMENT(2, "Saved Segments"),
    CAMPAIGN(3, "Campaign"),
    SHARED_TAXONOMY(4, "Shared Taxonomy"),
    SHARED_AUDIENCE(5, "Shared Audience");

    private Integer code;
    private String value;
}
