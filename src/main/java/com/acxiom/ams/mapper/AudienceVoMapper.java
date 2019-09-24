package com.acxiom.ams.mapper;

import com.acxiom.ams.mapper.util.FolderPoToFolderId;
import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.vo.AudienceVo;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Created by cldong on 12/11/2017.
 */
@Mapper(uses = {FolderPoToFolderId.class}, componentModel = "spring")
public interface AudienceVoMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "ruleJson", target = "ruleJson"),
            @Mapping(source = "count", target = "count"),
            @Mapping(source = "cost", target = "cost"),
            @Mapping(source = "universeIds", target = "universeIds"),
            @Mapping(source = "taxonomyId", target = "taxonomyId"),
            @Mapping(source = "segmentStatusType", target = "statusType"),
            @Mapping(source = "folderPo", target = "folderId"),
            @Mapping(source = "audienceType", target = "audienceType"),
            @Mapping(source = "code", target = "segmentCode"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "distributionFlag", target = "distributionFlag")
    })
    AudienceVo map(AudiencePo entity);

    List<AudienceVo> map(List<AudiencePo> entity);
}
