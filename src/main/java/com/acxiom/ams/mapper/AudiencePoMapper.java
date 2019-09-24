package com.acxiom.ams.mapper;

import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.vo.AudienceAndFolderVo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 10:25 12/6/2017
 */
@Mapper(componentModel = "spring")
public interface AudiencePoMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "ruleJson", target = "ruleJsonDisplay"),
            @Mapping(source = "count", target = "count"),
            @Mapping(source = "taxonomyId", target = "taxonomyId"),
            @Mapping(source = "createdBy", target = "createdBy"),
            @Mapping(source = "cost", target = "cost"),
            @Mapping(source = "segmentStatusType", target = "segmentStatusType"),
            @Mapping(source = "updateTime", target = "updateTime"),
            @Mapping(source = "createdTime", target = "createdTime"),
            @Mapping(source = "distribution", target = "distribution", defaultValue = "0"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "audienceType", target = "audienceType"),
            @Mapping(source = "frozenCount", target = "frozenCount"),
            @Mapping(source = "distributionFlag", target = "distributionFlag"),
            @Mapping(source = "universeIds", target = "universeIds"),
            @Mapping(source = "legalFlag", target = "legalFlag"),
            @Mapping(source = "lookalikeType", target = "lookalikeType"),
            @Mapping(source = "lookalikeResult", target = "lookalikeResult"),
            @Mapping(source = "testCount", target = "testCount"),
            @Mapping(source = "controlCount", target = "controlCount"),
            @Mapping(source = "frozenTestCount", target = "frozenTestCount"),
            @Mapping(source = "frozenControlCount", target = "frozenControlCount"),
            @Mapping(source = "errorCode", target = "errorCode")
    })
    AudienceAndFolderVo map(AudiencePo entity);

    List<AudienceAndFolderVo> map(List<AudiencePo> entity);
}
