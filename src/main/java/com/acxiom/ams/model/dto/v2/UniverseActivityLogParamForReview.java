package com.acxiom.ams.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class UniverseActivityLogParamForReview {
    private List<Long> idList;
    private String status;
    private String username;
}
