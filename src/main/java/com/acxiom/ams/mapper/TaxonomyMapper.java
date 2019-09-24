package com.acxiom.ams.mapper;

import com.acxiom.ams.model.rmi.Taxonomy;
import com.acxiom.ams.model.vo.TaxonomyItemVo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * Created by cldong on 12/7/2017.
 */
@Mapper(componentModel = "spring")
public interface TaxonomyMapper {
    @Mappings({
        @Mapping(source = "objectId", target = "objectId"),
        @Mapping(source = "id", target = "taxonomyId"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "style", target = "style"),
        @Mapping(source = "type", target = "type"),
        @Mapping(source = "description", target = "description"),
        @Mapping(source = "owner", target = "owner")
    })
    TaxonomyItemVo map(Taxonomy entity);
    List<TaxonomyItemVo> map(List<Taxonomy> entity);
}
