package io.security.base;

import io.security.base.events.BeforeDeletePrivilege;
import io.security.base.privilege.Privilege;
import io.security.base.privilege.PrivilegeDTO;
import io.security.base.privilege.PrivilegeRepository;
import io.security.base.util.CustomCollectors;
import io.security.base.util.NotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;
    private final ApplicationEventPublisher publisher;

    public PrivilegeService(final PrivilegeRepository privilegeRepository,
                            final ApplicationEventPublisher publisher) {
        this.privilegeRepository = privilegeRepository;
        this.publisher = publisher;
    }

    public List<PrivilegeDTO> findAll() {
        final List<Privilege> privileges = privilegeRepository.findAll(Sort.by("id"));
        return privileges.stream()
                .map(privilege -> mapToDTO(privilege, new PrivilegeDTO()))
                .toList();
    }

    public PrivilegeDTO get(final Long id) {
        return privilegeRepository.findById(id)
                .map(privilege -> mapToDTO(privilege, new PrivilegeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PrivilegeDTO privilegeDTO) {
        final Privilege privilege = new Privilege();
        mapToEntity(privilegeDTO, privilege);
        return privilegeRepository.save(privilege).getId();
    }

    public void update(final Long id, final PrivilegeDTO privilegeDTO) {
        final Privilege privilege = privilegeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(privilegeDTO, privilege);
        privilegeRepository.save(privilege);
    }

    public void delete(final Long id) {
        final Privilege privilege = privilegeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePrivilege(id));
        privilegeRepository.delete(privilege);
    }

    private PrivilegeDTO mapToDTO(final Privilege privilege, final PrivilegeDTO privilegeDTO) {
        privilegeDTO.setId(privilege.getId());
        privilegeDTO.setName(privilege.getName());
        return privilegeDTO;
    }

    private Privilege mapToEntity(final PrivilegeDTO privilegeDTO, final Privilege privilege) {
        privilege.setName(privilegeDTO.getName());
        return privilege;
    }

    public boolean nameExists(final String name) {
        return privilegeRepository.existsByNameIgnoreCase(name);
    }

    public Map<Long, String> getPrivilegeValues() {
        return privilegeRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Privilege::getId, Privilege::getName));
    }

}
