package io.security.base;

import io.security.base.events.BeforeDeletePrivilege;
import io.security.base.events.BeforeDeleteRole;
import io.security.base.privilege.Privilege;
import io.security.base.privilege.PrivilegeRepository;
import io.security.base.role.Role;
import io.security.base.role.RoleDTO;
import io.security.base.role.RoleRepository;
import io.security.base.util.CustomCollectors;
import io.security.base.util.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class RoleService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final ApplicationEventPublisher publisher;

    public RoleService(final RoleRepository roleRepository,
            final PrivilegeRepository privilegeRepository,
            final ApplicationEventPublisher publisher) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.publisher = publisher;
    }

    public Page<RoleDTO> findAll(final String filter, final Pageable pageable) {
        Page<Role> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = roleRepository.findAllById(longFilter, pageable);
        } else {
            page = roleRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(role -> mapToDTO(role, new RoleDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public RoleDTO get(final Long id) {
        return roleRepository.findById(id)
                .map(role -> mapToDTO(role, new RoleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final RoleDTO roleDTO) {
        final Role role = new Role();
        mapToEntity(roleDTO, role);
        return roleRepository.save(role).getId();
    }

    public void update(final Long id, final RoleDTO roleDTO) {
        final Role role = roleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(roleDTO, role);
        roleRepository.save(role);
    }

    public void delete(final Long id) {
        final Role role = roleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteRole(id));
        roleRepository.delete(role);
    }

    private RoleDTO mapToDTO(final Role role, final RoleDTO roleDTO) {
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());
        roleDTO.setPrivilege(role.getPrivilege().stream()
                .map(privilege -> privilege.getId())
                .toList());
        return roleDTO;
    }

    private Role mapToEntity(final RoleDTO roleDTO, final Role role) {
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

    public boolean nameExists(final String name) {
        return roleRepository.existsByNameIgnoreCase(name);
    }

    public boolean descriptionExists(final String description) {
        return roleRepository.existsByDescriptionIgnoreCase(description);
    }

    public Map<Long, String> getRoleValues() {
        return roleRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Role::getId, Role::getName));
    }

    @EventListener(BeforeDeletePrivilege.class)
    public void on(final BeforeDeletePrivilege event) {
        // remove many-to-many relations at owning side
        roleRepository.findAllByPrivilegeId(event.getId()).forEach(role ->
                role.getPrivilege().removeIf(privilege -> privilege.getId().equals(event.getId())));
    }

}
