package com.acxiom.ams.model.dto.v2;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 10:15 1/3/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class ChannelParam {

    @Size(max = 255, message = "{message.format.channel.rules}")
    private String rules;

    @Size(min = 1, max = 255, message = "{message.error.channelName}")
    private String channelName;

    @NotBlank(message = "{message.error.createBy}")
    private String createBy;

    private Long tenantId;

    @Size(max = 255, message = "{message.error.channelPath}")
    private String path;

    private Long infoBaseId;
}
