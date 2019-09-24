package com.acxiom.ams.model.po;

import com.acxiom.ams.model.em.TemplateStatusType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 3:11 PM
 */
@Entity
@Table(name = "version")
@SQLDelete(sql = "Update version set IS_DELETED = 1 where ID = ?")
@Where(clause = "IS_DELETED = 0")
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
@ToString(exclude = "tenantPo")
public class VersionPo extends BaseEntity {
    @Column(name = "VERSION_NAME")
    private String name;
    @Column(name = "VERSION_TREE_ID")
    private String treeId;
    @Column(name = "VERSION_OPERATION_FLAG")
    @Enumerated(EnumType.ORDINAL)
    private TemplateStatusType operationFlag = TemplateStatusType.DRAFT;
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "TENANT_ID", nullable = false)
    private TenantPo tenantPo;
    @Column(name = "UPDATED_BY")
    private String updatedBy;
    @Column(name = "MAX_DEPTH")
    private int maxDepth;
    @Column(name = "NODE_NUMBER")
    private int nodeNumber;
    @Column(name = "SYNC_FLAG")
    private int syncFlag;
    @Column(name = "DATASOURCE_ID")
    private String datasourceId;
}
