package io.security.base.privilege;

import org.springframework.stereotype.Component;

@Component
public class PrivilegeMapper {

    public PrivilegeDTO mapToDTO(final Privilege privilege, final PrivilegeDTO privilegeDTO) {
        privilegeDTO.setId(privilege.getId());
        privilegeDTO.setName(privilege.getName());
        return privilegeDTO;
    }

    public Privilege mapToEntity(final PrivilegeDTO privilegeDTO, final Privilege privilege) {
        privilege.setName(privilegeDTO.getName());
        return privilege;
    }
}
