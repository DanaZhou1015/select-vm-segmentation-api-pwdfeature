package com.acxiom.ams.model.vo;

import com.acxiom.ams.model.em.FolderType;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:05 12/7/2017
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class FolderVo {

    private long id;
    private String name;
    private FolderType type;
    private String createdBy;
    private Date updateTime;
}
