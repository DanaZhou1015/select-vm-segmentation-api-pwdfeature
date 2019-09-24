package com.acxiom.ams.model.dto;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 17:20 12/20/2017
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class AudienceParam {
    @Size(min = 1, max = 240, message = "{temporarySegment.name.size}")
    private String newName;
    @NotBlank(message = "{message.error.owner}")
    private String newOwner;
}
