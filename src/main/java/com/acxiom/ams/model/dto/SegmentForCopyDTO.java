package com.acxiom.ams.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 1:55 PM 5/10/2018
 */

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class SegmentForCopyDTO {
    @Size(min = 1, max = 240, message = "{temporarySegment.name.size}")
    private String newName;
    @NotBlank(message = "{message.error.owner}")
    private String newOwner;
    @NotNull(message = "{temporarySegment.userId.notNull}")
    private String userId;
}
