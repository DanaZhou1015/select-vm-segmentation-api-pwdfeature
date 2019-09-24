package com.acxiom.ams.model.dto;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 17:29 12/20/2017
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class FolderParam {
    @Size(min = 1, max = 30, message = "{message.error.folderName.format}")
    private String newFolderName;
    @NotBlank(message = "{message.error.owner}")
    private String owner;
}
