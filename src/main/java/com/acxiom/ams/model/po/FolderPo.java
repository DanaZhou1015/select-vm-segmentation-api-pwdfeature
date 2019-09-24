package com.acxiom.ams.model.po;

import com.acxiom.ams.model.em.FolderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Created by cldong on 12/5/2017.
 */
@Entity
@Table(name = "folder")
@SQLDelete(sql = "update folder set IS_DELETED = 1 where ID = ?")
@Where(clause = "IS_DELETED = 0")
@NamedNativeQueries(value = {@NamedNativeQuery(name ="get_all_child" , query = "select * from folder where FIND_IN_SET(id, getChildList(:rootId))", resultClass = FolderPo.class),
    @NamedNativeQuery(name ="get_all_parent" , query = "select getParentList(:rootId)")
})
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
@ToString(exclude = {"audiencePoList", "folderPoList"})
public class FolderPo extends BaseEntity {
    @Column(name = "FOLDER_PARENT_ID")
    private long parentFolderId;
    @Column(name = "FOLDER_NAME")
    private String folderName;
    @Column(name = "FOLDER_TYPE")
    @Enumerated(EnumType.STRING)
    private FolderType folderType;
    @Column(name = "TENANT_ID")
    private Long tenantId;
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, mappedBy = "folderPo")
    @Where(clause = "IS_DELETED = 0")
    private List<AudiencePo> audiencePoList;
    @Transient
    private List<FolderPo> folderPoList;
}
