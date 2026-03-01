package io.security.base.role;

import io.security.base.privilege.Privilege;
import io.security.base.privilege.PrivilegeRepository;
import io.security.base.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleMapper {
    private final PrivilegeRepository privilegeRepository;

    public RoleDTO mapToDTO(final Role role, final RoleDTO roleDTO) {
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());
        roleDTO.setPrivilege(role.getPrivilege().stream()
                .map(privilege -> privilege.getId())
                .toList());
        return roleDTO;
    }

    public Role mapToEntity(final RoleDTO roleDTO, final Role role) {
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        final List<Privilege> privilege = privilegeRepository.findAllById(
                roleDTO.getPrivilege() == null ? List.of() : roleDTO.getPrivilege());
        if (privilege.size() != (roleDTO.getPrivilege() == null ? 0 : roleDTO.getPrivilege().size())) {
            throw new NotFoundException("one of privilege not found");
        }
        role.setPrivilege(new HashSet<>(privilege));
        return role;
    }
}
