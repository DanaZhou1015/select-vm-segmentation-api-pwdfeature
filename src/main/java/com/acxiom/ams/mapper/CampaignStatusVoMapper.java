package com.acxiom.ams.mapper;

import com.acxiom.ams.model.po.AudiencePo;
import com.acxiom.ams.model.vo.CampaignStatusVo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 14:35 1/4/2018
 */
@Mapper(componentModel = "spring")
public interface CampaignStatusVoMapper {
    @Mappings({
        @Mapping(source = "id", target = "campaignId"),
        @Mapping(source = "segmentStatusType", target = "segmentStatusType")
    })
    CampaignStatusVo map(AudiencePo entity);
    List<CampaignStatusVo> map(List<AudiencePo> entity);
}
