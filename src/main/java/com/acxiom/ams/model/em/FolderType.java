package com.acxiom.ams.model.em;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 14:10 12/5/2017
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public enum FolderType {
    LOOKALIKE_GROUP(1,"Lookalike Group"),SAVED_SEGMENT(2, "Saved Segments"), CAMPAIGN(3, "Campaign");
    private Integer code;
    private String value;
}
