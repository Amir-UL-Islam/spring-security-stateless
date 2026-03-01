package io.security.base.privilege;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    boolean existsByNameIgnoreCase(String name);

}
