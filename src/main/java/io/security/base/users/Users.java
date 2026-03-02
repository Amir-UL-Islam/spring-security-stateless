package io.security.base.users;

import io.security.base.core.BaseEntity;
import io.security.base.role.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
public class Users extends BaseEntity {
    @Column
    private String name;

    @Column
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

//    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "UsersRoles",
            joinColumns = @JoinColumn(name = "usersId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> role = new HashSet<>();

    @Column
    private Boolean twoFactorEnabled = false;

    @Column
    private String totpSecret;

}
