package com.acxiom.ams.model.po;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import javax.persistence.*;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "IS_DELETED", columnDefinition="bit default 0")
    protected boolean isDeleted = false;

    @Column(name = "CREATED_BY", nullable = false)
    private String createdBy;

    @Column(name = "CREATED_TIME", nullable = false)
    @CreatedDate
    private Date createdTime;

    @Column(name = "UPDATE_TIME")
    @LastModifiedDate
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @PrePersist
    protected void prePersist() {
        if (this.createdTime == null) createdTime = new Date();
        if (this.updateTime == null) updateTime = new Date();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updateTime = new Date();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this).omitNullValues().add("id", id)
            .add("createdBy",createdBy)
            .add("createdTime", createdTime)
            .add("updateTime", updateTime);
    }

    public String toString(){
        return toStringHelper().toString();
    }
}
