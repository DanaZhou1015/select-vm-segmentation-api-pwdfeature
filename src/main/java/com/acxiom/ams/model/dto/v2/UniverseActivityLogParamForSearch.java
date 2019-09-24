package com.acxiom.ams.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class UniverseActivityLogParamForSearch {
    private String keywords;
    private List<Long> clientList;
    private Date startDate;
    private Date endDate;
}
