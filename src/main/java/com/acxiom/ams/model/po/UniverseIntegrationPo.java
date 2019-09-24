package com.acxiom.ams.model.po;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "universe_integration")
@SQLDelete(sql = "update universe_integration set IS_DELETED = 1 where ID = ?")
@Where(clause = "IS_DELETED = 0")
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class UniverseIntegrationPo extends BaseEntity{
    @Column(name = "UNIVERSE_ID")
    private Long universeId;
    @Column(name = "DROP_OFF_POINT")
    private String dropOffPoint;
    @Column(name = "LR_AUDIENCE_ID")
    private String lrAudienceId;
    @Column(name = "ONBOARD_DESTINATION_ID")
    private String onboardDestinationId;
    @Column(name = "ONBOARD_INTEGRATION_ID")
    private String onboardIntegrationId;
    @Column(name = "DATASTORE_DESTINATION_ID")
    private String dataStoreDestinationId;
    @Column(name = "DATASTORE_INTEGRATION_ID")
    private String dataStoreIntegrationId;
}
