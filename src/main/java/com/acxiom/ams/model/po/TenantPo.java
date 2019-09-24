package com.acxiom.ams.model.po;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author: Gavin
 * @Description: 
 * @Date: 12/5/2017 3:23 PM
 * @Params:  * @param null
 */
@Entity
@Table(name = "tenant")
@SQLDelete(sql = "Update tenant set IS_DELETED = 1 where ID = ?")
@Where(clause = "IS_DELETED = 0")
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
@ToString(exclude = {"visionPoList","tenantAndChannelPoList"})
public class TenantPo extends BaseEntity{
    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "TENANT_NAME")
    private String name;

    @Column(name = "TENANT_PATH")
    private String path;

    @Column(name = "AI_TENANT")
    private String aiTenant;

    @Column(name = "TENANT_COUNT_LIMIT", columnDefinition="BIGINT default 250")
    private long countLimit;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "tenantPo")
    @Where(clause = "IS_DELETED = 0")
    private List<TenantAndChannelPo> tenantAndChannelPoList;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, mappedBy = "tenantPo")
    private List<VersionPo> visionPoList;
}
