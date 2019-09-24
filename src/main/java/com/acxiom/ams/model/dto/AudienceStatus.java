package com.acxiom.ams.model.dto;

import com.acxiom.ams.model.em.FolderType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by cldong on 12/22/2017.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class AudienceStatus {
    private FolderType folderType;
    private List<Long> list;
}
