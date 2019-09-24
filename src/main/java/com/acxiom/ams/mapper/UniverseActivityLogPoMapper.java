package com.acxiom.ams.mapper;

import com.acxiom.ams.model.po.UniverseActivityLogPo;
import com.acxiom.ams.model.vo.UniverseActivityLogVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UniverseActivityLogPoMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "audienceId", target = "audienceId"),
            @Mapping(source = "audienceName", target = "audienceName"),
            @Mapping(source = "audienceStatus", target = "audienceStatus"),
            @Mapping(source = "audienceType", target = "audienceType"),
            @Mapping(source = "audienceCount", target = "audienceCount"),
            @Mapping(source = "tenantId", target = "tenantId"),
            @Mapping(source = "tenantName", target = "tenantName"),
            @Mapping(source = "destinationId", target = "destinationId"),
            @Mapping(source = "universeName", target = "universeName"),
            @Mapping(source = "folderId", target = "folderId"),
            @Mapping(source = "audienceRuleJson", target = "audienceRuleJson"),
            @Mapping(source = "createdTime", target = "createdTime"),
            @Mapping(source = "updateTime", target = "updateTime"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "legalFlag", target = "legalFlag"),
            @Mapping(source = "requestBy", target = "requestBy"),
            @Mapping(source = "requestEmail", target = "requestEmail"),
            @Mapping(source = "ownerTenantId", target = "ownerTenantId"),
            @Mapping(source = "approvalBy", target = "approvalBy")
    })
    UniverseActivityLogVo map(UniverseActivityLogPo entity);

    List<UniverseActivityLogVo> map(List<UniverseActivityLogPo> entity);
}
