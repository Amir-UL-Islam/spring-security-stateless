package io.security.base.privilege;

import io.security.base.core.BaseEntity;
import io.security.base.role.Role;
import io.security.base.urls.Urls;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Privilege  extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "privilege")
    private Set<Role> role = new HashSet<>();

    @OneToMany(mappedBy = "privilege")
    private Set<Urls> urls = new HashSet<>();

}
