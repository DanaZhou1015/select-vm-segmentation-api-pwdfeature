package com.acxiom.ams.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "tenant_universe")
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class TenantAndUniversePo{

    @EmbeddedId
    private TenantAndUniverseKey id;
    @Column(name = "UNIVERSE_IDS", nullable = false)
    private String universeIds;
}
