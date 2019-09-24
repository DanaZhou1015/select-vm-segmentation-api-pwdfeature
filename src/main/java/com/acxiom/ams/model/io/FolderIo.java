package com.acxiom.ams.model.io;

import com.acxiom.ams.model.em.FolderType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 17:42 12/7/2017
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class FolderIo {

    @Size(min = 1, max = 30, message = "{message.error.folderName.format}")
    private String name;
    @NotBlank(message = "{message.error.createBy}")
    private String createdBy;
    @NotNull(message = "{message.error.parentId}")
    private Long parentId;
    @NotNull(message = "{message.error.tenantId}")
    private Long tenantId;
    @NotNull(message = "{message.error.folderType}")
    private FolderType folderType;
}
