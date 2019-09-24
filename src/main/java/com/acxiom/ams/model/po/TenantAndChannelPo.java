package com.acxiom.ams.model.po;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 10:54 12/19/2017
 */
@Entity
@Table(name = "tenant_channel")
@SQLDelete(sql = "Update tenant_channel set IS_DELETED = 1 where ID = ?")
@Where(clause = "IS_DELETED = 0")
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class TenantAndChannelPo extends BaseEntity {
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "TENANT_ID", nullable = false)
    private TenantPo tenantPo;
    @JoinColumn(name = "CHANNEL_NAME", nullable = false)
    private String channelName;
    @Column(name = "CHANNEL_SFTP_HOST", nullable = false)
    private String host;
    @Column(name = "CHANNEL_SFTP_PORT", nullable = false)
    private Integer port;
    @Column(name = "CHANNEL_SFTP_PATH", nullable = false)
    private String path;
    @Column(name = "CHANNEL_SFTP_USERNAME")
    private String username;
    
    
    @Column(name = "CHANNEL_SFTP_PASSWORD")
    private String password;
    
    
    
    @Column(name = "CHANNEL_SFTP_KEY_FILE")
    private String keyFile;
    @Column(name = "CHANNEL_SFTP_PASSPHRASE")
    private String passPhrase;
    @Column(name = "CHANNEL_ICON")
    private String icon;
    @Column(name = "LR_AUDIENCE_ID")
    private String lrAudienceId;
    @Column(name = "ONBOARD_DESTINATION_ID")
    private String onBoardDestinationId;
    @Column(name = "ONBOARD_INTEGRATION_ID")
    private String onBoardIntegrationId;
    @Column(name = "DATASTORE_DESTINATION_ID")
    private String dataStoreDestinationId;
    @Column(name = "DATASTORE_INTEGRATION_ID")
    private String dataStoreIntegrationId;
}
