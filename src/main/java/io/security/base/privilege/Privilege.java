package io.security.base.privilege;

import io.security.base.core.BaseEntity;
import io.security.base.role.Role;
import io.security.base.urls.Url;
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
public class Privilege extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "privilege")
    private Set<Role> role = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "PrivilegeUrl",
            joinColumns = @JoinColumn(name = "privilegeId"),
            inverseJoinColumns = @JoinColumn(name = "urlId")
    )
    private Set<Url> urls = new HashSet<>();

    public Privilege() {
        super();
    }

    public Privilege(String name) {
        super();
        this.name = name;
    }
}
