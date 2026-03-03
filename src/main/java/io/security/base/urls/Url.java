package io.security.base.urls;

import io.security.base.core.BaseEntity;
import io.security.base.privilege.Privilege;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Url extends BaseEntity {

    @Column(nullable = false)
    private String endpoint;

    @Column(nullable = false)
    private String method;

    @ManyToMany(mappedBy = "urls")
    private Set<Privilege> privileges = new HashSet<>();

}
