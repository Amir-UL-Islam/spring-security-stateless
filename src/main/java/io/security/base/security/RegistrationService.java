package io.security.base.security;

import io.security.base.role.RoleRepository;
import io.security.base.users.Users;
import io.security.base.users.UsersRepository;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RegistrationService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public RegistrationService(final UsersRepository usersRepository,
            final PasswordEncoder passwordEncoder, final RoleRepository roleRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public void register(final RegistrationRequest registrationRequest) {
        log.info("registering new user: {}", registrationRequest.getUsername());

        final Users users = new Users();
        users.setName(registrationRequest.getName());
        users.setUsername(registrationRequest.getUsername());
        users.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        // assign default role
        users.setRole(Set.of(roleRepository.findByName(UserRoles.ADMIN)));
        usersRepository.save(users);
    }

}
