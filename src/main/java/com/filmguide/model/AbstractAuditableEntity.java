package com.filmguide.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditableEntity<U, ID> extends AbstractPersistableEntity<ID> {

    /**
     *
     */
    private static final long serialVersionUID = -6959805731177130423L;

    @CreatedDate
    Date createdAt;

    @LastModifiedDate
    Date lastModifiedAt;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "created_by")
    U createdBy;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "last_modified_by")
    U lastModifiedBy;
}
