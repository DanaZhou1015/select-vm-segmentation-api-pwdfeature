package com.acxiom.ams.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * @Author: Gavin
 * @Description: 
 * @Date: 12/5/2017 3:49 PM
 */
@Entity
@Table(name = "profiling")
@SQLDelete(sql = "update profiling set IS_DELETED = 1 where ID = ?")
@Where(clause = "IS_DELETED = 0")
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
@ToString(exclude = "tenantPo")
public class ProfilingPo extends BaseEntity{

    @Column(name = "profiling_description")
    private String description;

    @Column(name = "profiling_name")
    private String name;

    @Column(name = "profiling_json",columnDefinition="TEXT")
    private String json;

    @Column(name = "destination_id")
    private Long destinationId;

    @Column(name = "active")
    private boolean active;

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "TENANT_ID", nullable = false)
    private TenantPo tenantPo;
}
