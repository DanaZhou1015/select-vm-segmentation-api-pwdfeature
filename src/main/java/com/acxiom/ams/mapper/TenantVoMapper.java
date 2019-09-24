package com.acxiom.ams.mapper;

import com.acxiom.ams.model.po.TenantPo;
import com.acxiom.ams.model.vo.TenantVo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Created by cldong on 12/12/2017.
 */
@Mapper(componentModel = "spring")
public interface TenantVoMapper {
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "path", target = "path"),
        @Mapping(source = "tenantId", target = "tenantId")
    })
    TenantVo map(TenantPo entity);

    List<TenantVo> map(List<TenantPo> entity);
}
