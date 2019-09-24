package com.acxiom.ams.model.dto.v2;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 16:10 1/3/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class TenantAndChannelDTO {

    @NotBlank(message = "{message.error.createBy}")
    private String createBy;
    @Size(max = 255, message = "{message.error.sftpHost}")
    private String sftpHost;
    @Size(max = 255, message = "{message.error.sftpKeyFile}")
    private String sftpKeyFile;
    private String sftpPassphrase;
    @Size(max = 255, message = "{message.error.sftpPassword}")
    private String sftpPassword;
    @Size(max = 255, message = "{message.error.sftpPath}")
    private String sftpPath;
    @Range(max = 65535, message = "{message.error.sftpPort}")
    private Integer sftpPort;
    @Size(max = 255, message = "{message.error.sftpUsername}")
    private String sftpUsername;
    @Size(max = 255, message = "{message.error.channelName}")
    private String channelName;
    private long tenantId;
    private String icon;
    @Size(max = 255, message = "{message.error.lrAudienceId}")
    private String lrAudienceId;
    @Size(max = 255, message = "{message.error.onBoardDestinationId}")
    private String onBoardDestinationId;
    @Size(max = 255, message = "{message.error.onBoardIntegrationId}")
    private String onBoardIntegrationId;
    @Size(max = 255, message = "{message.error.dataStoreDestinationId}")
    private String dataStoreDestinationId;
    @Size(max = 255, message = "{message.error.dataStoreIntegrationId}")
    private String dataStoreIntegrationId;
}
