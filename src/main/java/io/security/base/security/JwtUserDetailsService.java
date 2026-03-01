package io.security.base.security;

import io.security.base.users.Users;
import io.security.base.users.UsersRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    public JwtUserDetailsService(final UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public JwtUserDetails loadUserByUsername(final String username) {
        final Users users = usersRepository.findByUsernameIgnoreCase(username);
        if (users == null) {
            log.warn("user not found: {}", username);
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        final List<SimpleGrantedAuthority> authorities = users.getRole()
                .stream()
                .map(roleRef -> new SimpleGrantedAuthority(roleRef.getName()))
                .toList();
        return new JwtUserDetails(users.getId(), username, users.getPassword(), authorities);
    }

}
