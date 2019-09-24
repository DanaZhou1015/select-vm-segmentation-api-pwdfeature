package com.acxiom.ams.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UniverseIntegrationDTO {
    private Long universeId;
    private String dropOffPoint;
    private String lrAudienceId;
    private String onboardDestinationId;
    private String onboardIntegrationId;
    private String dataStoreDestinationId;
    private String dataStoreIntegrationId;
}
