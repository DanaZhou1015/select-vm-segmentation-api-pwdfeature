package com.acxiom.ams.mapper;

import com.acxiom.ams.model.po.TenantAndChannelPo;
import com.acxiom.ams.model.vo.TenantAndChannelVo;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 10:03 1/4/2018
 */
@Mapper(componentModel = "spring")
public interface TenantAndChannelPoMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "channelName", target = "channelName"),
            @Mapping(source = "tenantPo.id", target = "tenantId"),
            @Mapping(source = "tenantPo.name", target = "tenantName"),
            @Mapping(source = "createdBy", target = "createBy"),
            @Mapping(source = "host", target = "sftpHost"),
            @Mapping(source = "keyFile", target = "sftpKeyFile"),
            @Mapping(source = "passPhrase", target = "sftpPassphrase"),
            @Mapping(source = "password", target = "sftpPassword"),
            @Mapping(source = "path", target = "sftpPath"),
            @Mapping(source = "port", target = "sftpPort"),
            @Mapping(source = "username", target = "sftpUsername"),
            @Mapping(source = "lrAudienceId", target = "lrAudienceId"),
            @Mapping(source = "onBoardDestinationId", target = "onBoardDestinationId"),
            @Mapping(source = "onBoardIntegrationId", target = "onBoardIntegrationId"),
            @Mapping(source = "dataStoreDestinationId", target = "dataStoreDestinationId"),
            @Mapping(source = "dataStoreIntegrationId", target = "dataStoreIntegrationId"),
            @Mapping(source = "icon", target = "icon")
    })
    TenantAndChannelVo map(TenantAndChannelPo entity);

    List<TenantAndChannelVo> map(List<TenantAndChannelPo> entity);
}
