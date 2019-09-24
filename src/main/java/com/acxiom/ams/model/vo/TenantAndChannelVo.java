package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 09:29 1/4/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class TenantAndChannelVo {
    private long id;
    private String channelName;
    private String tenantName;
    private String createBy;
    private String sftpHost;
    private String sftpKeyFile;
    private String sftpPassphrase;
    
    
    private String sftpPassword;
    
    
    private String sftpPath;
    private Integer sftpPort;
    private String sftpUsername;
    private Long tenantId;
    private String icon;
    private String lrAudienceId;
    private String onBoardDestinationId;
    private String onBoardIntegrationId;
    private String dataStoreDestinationId;
    private String dataStoreIntegrationId;
}
