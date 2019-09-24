package com.acxiom.ams.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class DistributeParamForCampaign {
    String universeSysName;
    String universeName;
    Long universeID;
    String filePath;
    String audienceID;
    Long jobID;
}
