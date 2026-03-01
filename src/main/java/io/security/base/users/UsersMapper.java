package io.security.base.users;

import io.security.base.role.Role;
import io.security.base.role.RoleRepository;
import io.security.base.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UsersMapper {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersDTO mapToDTO(final Users users, final UsersDTO usersDTO) {
        usersDTO.setId(users.getId());
        usersDTO.setName(users.getName());
        usersDTO.setEmail(users.getEmail());
        usersDTO.setUsername(users.getUsername());
        usersDTO.setRole(users.getRole().stream()
                .map(role -> role.getId())
                .toList());
        return usersDTO;
    }

    public Users mapToEntity(final UsersDTO usersDTO, final Users users) {
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
}
