package com.acxiom.ams.mapper;

import com.acxiom.ams.model.po.FolderPo;
import com.acxiom.ams.model.vo.FolderVo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:42 12/7/2017
 */
@Mapper(componentModel = "spring")
public interface FolderPoToVoMapper {
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "folderName", target = "name"),
        @Mapping(source = "folderType", target = "type"),
        @Mapping(source = "updateTime", target = "updateTime"),
        @Mapping(source = "createdBy", target = "createdBy")
    })
    FolderVo map(FolderPo entity);
    List<FolderVo> map(List<FolderPo> entity);
}
