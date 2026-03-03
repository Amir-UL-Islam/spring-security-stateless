package io.security.base.core;

import io.security.base.users.Users;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity implements Serializable {

    @Id
    @SequenceGenerator(name = "primary_sequence", sequenceName = "primary_sequence", allocationSize = 1, initialValue = 10000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_sequence")
    @EqualsAndHashCode.Include
    private Long id;

    @CreatedDate
    @Column(name = "create_date", nullable = false, updatable = false)
    private OffsetDateTime createDate;

    @LastModifiedDate
    @Column(name = "update_date", nullable = false)
    private OffsetDateTime updateDate;

    @CreatedBy
    @JoinColumn(name = "created_by_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Users createdBy;

    @LastModifiedBy
    @JoinColumn(name = "updated_by_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Users updatedBy;

    @Column(name = "uuid", unique = true)
    private String uuid;


    @PrePersist
    private void onBasePersist() {
        if (this.uuid == null || this.uuid.isEmpty())
            this.uuid = UUID.randomUUID().toString();
    }

    @PreUpdate
    private void onBaseUpdate() {
        if (this.uuid == null || this.uuid.isEmpty())
            this.uuid = UUID.randomUUID().toString();
    }

}

