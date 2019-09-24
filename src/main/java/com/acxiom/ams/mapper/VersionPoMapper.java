package com.acxiom.ams.mapper;

import com.acxiom.ams.mapper.util.Date2String;
import com.acxiom.ams.model.po.VersionPo;
import com.acxiom.ams.model.vo.VersionVo;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Created by cldong on 12/5/2017.
 */
@Mapper(uses = {Date2String.class}, componentModel = "spring")
public interface VersionPoMapper {
    @Mappings({
            @Mapping(source = "id", target = "versionId"),
            @Mapping(source = "name", target = "versionName"),
            @Mapping(source = "treeId", target = "versionTreeId"),
            @Mapping(source = "maxDepth", target = "maxDepth"),
            @Mapping(source = "nodeNumber", target = "nodeNumber"),
            @Mapping(source = "createdTime", target = "createdTime"),
            @Mapping(source = "updateTime", target = "updateTime"),
            @Mapping(source = "updatedBy", target = "updatedBy"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "operationFlag", target = "versionOperationFlag"),
            @Mapping(source = "syncFlag", target = "syncFlag"),
            @Mapping(source = "datasourceId", target = "datasourceId"),
            @Mapping(source = "tenantPo.id", target = "tenantId")
    })
    VersionVo map(VersionPo entity);

    List<VersionVo> map(List<VersionPo> entity);
}
