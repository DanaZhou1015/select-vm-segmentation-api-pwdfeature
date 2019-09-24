package com.acxiom.ams.mapper;

import com.acxiom.ams.model.po.FolderPo;
import com.acxiom.ams.model.vo.AudienceAndFolderVo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 10:26 12/6/2017
 */
@Mapper(componentModel = "spring")
public interface FolderPoMapper {
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "folderName", target = "name"),
        @Mapping(source = "folderType", target = "type"),
        @Mapping(source = "updateTime", target = "updateTime"),
        @Mapping(source = "createdBy", target = "createdBy")
    })
    AudienceAndFolderVo map(FolderPo entity);
    List<AudienceAndFolderVo> map(List<FolderPo> entity);
}
