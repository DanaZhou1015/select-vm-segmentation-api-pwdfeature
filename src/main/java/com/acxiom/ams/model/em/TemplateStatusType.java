package com.acxiom.ams.model.em;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by cldong on 3/20/2018.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public enum TemplateStatusType {
    READY(0, "Ready"),ACTIVE(1, "Active"),DRAFT(2, "Draft");

    private Integer code;
    private String value;
}
