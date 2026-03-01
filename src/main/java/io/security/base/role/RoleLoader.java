package io.security.base.role;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class RoleLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public RoleLoader(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(final ApplicationArguments args) {
        if (roleRepository.count() != 0) {
            return;
        }
        log.info("initializing roles");
        final Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Admin Description");
        roleRepository.save(adminRole);
        final Role userRole = new Role();
        userRole.setName("USER");
        userRole.setDescription("Demo Description");
        roleRepository.save(userRole);
    }

}
