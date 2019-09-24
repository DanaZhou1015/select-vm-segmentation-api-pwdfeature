package com.acxiom.ams.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantAndUniverseKey implements Serializable {
    @Column(name = "TENANT_ID")
    private Long tenantId;
    @Column(name = "USERNAME")
    private String username;
}
