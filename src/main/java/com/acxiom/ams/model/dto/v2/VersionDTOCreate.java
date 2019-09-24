package com.acxiom.ams.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by cldong on 3/21/2018.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class VersionDTOCreate {
    @NotBlank(message = "{message.error.version.name.null}")
    @Length(max = 255,message = "{message.error.version.name.length}")
    private String name;
    @NotBlank(message = "{message.error.version.createdBy.null}")
    @Length(max = 255,message = "{message.error.version.createdBy.length}")
    private String userName;
}
