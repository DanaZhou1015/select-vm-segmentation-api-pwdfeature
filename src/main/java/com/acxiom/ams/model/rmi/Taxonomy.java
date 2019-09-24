package com.acxiom.ams.model.rmi;

import com.acxiom.ams.util.Constant;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Optional;

import lombok.*;
import org.apache.commons.lang.StringUtils;

/**
 * Created by cldong on 12/6/2017.
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
@ToString(exclude = "taxonomyIncludes")
public class Taxonomy {
    private String objectId;
    private String objectPid;
    private String id;
    private String name;
    private String type;
    private String style;
    private String description;
    private Double price;
    private String owner;
    private String checked;
    private String dataType;
    private String taxonomyId;
    @JSONField(name = "taxonomyIncludes")
    private List<Taxonomy> taxonomyIncludes;

    public String getId() {
        if (StringUtils.equals(this.type, Constant.TAXONOMY_END_NODE_TYPE)
                && Optional.ofNullable(taxonomyId).isPresent()) {
            return taxonomyId;
        }
        return id;
    }
}