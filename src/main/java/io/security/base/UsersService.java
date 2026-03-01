package io.security.base;

import io.security.base.events.BeforeDeleteRole;
import io.security.base.role.Role;
import io.security.base.role.RoleRepository;
import io.security.base.users.Users;
import io.security.base.users.UsersDTO;
import io.security.base.users.UsersRepository;
import io.security.base.util.NotFoundException;
import java.util.HashSet;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor = Exception.class)
public class UsersService {

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(final UsersRepository usersRepository, final RoleRepository roleRepository,
            final PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UsersDTO> findAll(final String filter, final Pageable pageable) {
        Page<Users> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = usersRepository.findAllById(longFilter, pageable);
        } else {
            page = usersRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(users -> mapToDTO(users, new UsersDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public UsersDTO get(final Long id) {
        return usersRepository.findById(id)
                .map(users -> mapToDTO(users, new UsersDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UsersDTO usersDTO) {
        final Users users = new Users();
        mapToEntity(usersDTO, users);
        return usersRepository.save(users).getId();
    }

    public void update(final Long id, final UsersDTO usersDTO) {
        final Users users = usersRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(usersDTO, users);
        usersRepository.save(users);
    }

    public void delete(final Long id) {
        final Users users = usersRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        usersRepository.delete(users);
    }

    private UsersDTO mapToDTO(final Users users, final UsersDTO usersDTO) {
        usersDTO.setId(users.getId());
        usersDTO.setName(users.getName());
        usersDTO.setEmail(users.getEmail());
        usersDTO.setUsername(users.getUsername());
        usersDTO.setRole(users.getRole().stream()
                .map(role -> role.getId())
                .toList());
        return usersDTO;
    }

    private Users mapToEntity(final UsersDTO usersDTO, final Users users) {
        users.setName(usersDTO.getName());
        users.setEmail(usersDTO.getEmail());
        users.setUsername(usersDTO.getUsername());
        users.setPassword(passwordEncoder.encode(usersDTO.getPassword()));
        final List<Role> role = roleRepository.findAllById(
                usersDTO.getRole() == null ? List.of() : usersDTO.getRole());
        if (role.size() != (usersDTO.getRole() == null ? 0 : usersDTO.getRole().size())) {
            throw new NotFoundException("one of role not found");
        }
        users.setRole(new HashSet<>(role));
        return users;
    }

    public boolean usernameExists(final String username) {
        return usersRepository.existsByUsernameIgnoreCase(username);
    }

    @EventListener(BeforeDeleteRole.class)
    public void on(final BeforeDeleteRole event) {
        // remove many-to-many relations at owning side
        usersRepository.findAllByRoleId(event.getId()).forEach(users ->
                users.getRole().removeIf(role -> role.getId().equals(event.getId())));
    }

}
