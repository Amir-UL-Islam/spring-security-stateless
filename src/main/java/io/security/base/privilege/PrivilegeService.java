package io.security.base.privilege;

import io.security.base.events.BeforeDeletePrivilege;
import io.security.base.util.CustomCollectors;
import io.security.base.util.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class PrivilegeService {

    private final PrivilegeMapper privilegeMapper;
    private final PrivilegeRepository privilegeRepository;
    private final ApplicationEventPublisher publisher;




    public List<PrivilegeDTO> findAll() {
        final List<Privilege> privileges = privilegeRepository.findAll(Sort.by("id"));
        return privileges.stream()
                .map(privilege -> privilegeMapper.mapToDTO(privilege, new PrivilegeDTO()))
                .toList();
    }

    public PrivilegeDTO get(final Long id) {
        return privilegeRepository.findById(id)
                .map(privilege -> privilegeMapper.mapToDTO(privilege, new PrivilegeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PrivilegeDTO privilegeDTO) {
        final Privilege privilege = new Privilege();
        privilegeMapper.mapToEntity(privilegeDTO, privilege);
        return privilegeRepository.save(privilege).getId();
    }

    public void update(final Long id, final PrivilegeDTO privilegeDTO) {
        final Privilege privilege = privilegeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        privilegeMapper.mapToEntity(privilegeDTO, privilege);
        privilegeRepository.save(privilege);
    }

    public void delete(final Long id) {
        final Privilege privilege = privilegeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePrivilege(id));
        privilegeRepository.delete(privilege);
    }


    public boolean nameExists(final String name) {
        return privilegeRepository.existsByNameIgnoreCase(name);
    }

    public Map<Long, String> getPrivilegeValues() {
        return privilegeRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Privilege::getId, Privilege::getName));
    }

    public boolean hasPermission(Authentication authentication, HttpServletRequest request) {
        // Extract the current URL pattern and HTTP method
        String urlPattern = request.getRequestURI();


        String httpMethod = request.getMethod();

        // Find permissions for this URL and method
        List<Privilege> permissions = privilegeRepository.findByUrlPatternAndHttpMethod(urlPattern, httpMethod);

        // If no specific permissions, deny access
        if (permissions.isEmpty()) {
            return false;
        }

        // Get user authorities
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Check if a user has a required role
        return permissions.stream()
                .anyMatch(permission ->
                        authorities.stream()
                                .anyMatch(authority ->
                                        Objects.equals(authority.getAuthority(), permission.getName())
                                )
                );
    }
}
