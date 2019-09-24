package com.acxiom.ams.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class ChannelDTO {
    Long id;
    String sftpHost;
    String sftpName;
    String sftpPassword;
    Integer sftpPort;
    String sftpPath;
    String sftpPem;
    String lrAudienceId;
    String sftpPassPhrase;
    Boolean dataLakeFlag;
    String dataLakeInboundPath;
}
