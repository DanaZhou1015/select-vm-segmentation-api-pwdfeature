package com.acxiom.ams.model.dto.v2;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 17:37 12/19/2017
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class CampaignDistributeParamDTO {
    @NotNull(message = "{message.error.tenantId}")
    private Long tenantId;
    @NotBlank(message = "{message.error.email}")
    private String noticeEmail;
    private List<Long> campaignIdList;
    @NotBlank(message = "{message.error.username}")
    private String username;
}
