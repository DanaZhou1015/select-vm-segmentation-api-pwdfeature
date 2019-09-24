package com.acxiom.ams.mapper;

import com.acxiom.ams.mapper.util.Date2String;
import com.acxiom.ams.model.po.ProfilingPo;
import com.acxiom.ams.model.vo.ProfilingVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import org.mapstruct.Mappings;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 3:07 PM
 */
@Mapper(uses = {Date2String.class}, componentModel = "spring")
public interface ProfilingPoMapper {
    @Mappings({
            @Mapping(source = "id", target = "profilingId"),
            @Mapping(source = "createdTime", target = "createdTime"),
            @Mapping(source = "updateTime", target = "updateTime"),
            @Mapping(source = "name", target = "profilingName"),
            @Mapping(source = "json", target = "profilingJson"),
            @Mapping(source = "description", target = "profilingDescription"),
            @Mapping(source = "createdBy", target = "createBy"),
            @Mapping(source = "active", target = "active")
    })
    ProfilingVo map(ProfilingPo entity);

    List<ProfilingVo> map(List<ProfilingPo> entity);
}
