package com.acxiom.ams.model.em;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by cldong on 12/7/2017.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public enum TreeItemType {
    LEAF(0, "leaf node"), CHECKBOX(1, "check box"), FOLDER(2, "folder"), FILE(3, "file"), END(4, "end");

    private Integer code;
    private String value;
}
