package com.acxiom.ams.model.em;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public enum UniverseType {

    DEFAULT(0, "Default"), OTHER(1,"Other"),SHARED(2,"Shared");
    private Integer code;
    private String value;
}
