package io.security.base.security;

import io.security.base.role.RoleRepository;
import io.security.base.users.Users;
import io.security.base.users.UsersRepository;
import jakarta.validation.Valid;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;


@RestController
@Slf4j
public class AuthenticationController {

    private final AuthenticationProvider authenticationProvider;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtSocialUserDetailsService jwtSocialUserDetailsService;
    private final JwtTokenService jwtTokenService;
    private final Environment environment;
    private final UsersRepository usersRepository;
    private final String baseHost;
    private final RoleRepository roleRepository;
    private final RestClient googleClient = RestClient.builder()
            .baseUrl("https://oauth2.googleapis.com/")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public AuthenticationController(final AuthenticationProvider authenticationProvider,
                                    final JwtUserDetailsService jwtUserDetailsService,
                                    final JwtSocialUserDetailsService jwtSocialUserDetailsService,
                                    final JwtTokenService jwtTokenService, final Environment environment,
                                    final UsersRepository usersRepository,
                                    @Value("${app.baseHost}") final String baseHost,
                                    final RoleRepository roleRepository) {
        this.authenticationProvider = authenticationProvider;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtSocialUserDetailsService = jwtSocialUserDetailsService;
        this.jwtTokenService = jwtTokenService;
        this.environment = environment;
        this.usersRepository = usersRepository;
        this.baseHost = baseHost;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(
            @RequestBody @Valid final AuthenticationRequest authenticationRequest) {
        try {
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (final BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        final JwtUserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(jwtTokenService.generateToken(userDetails, "direct", null));
        return authenticationResponse;
    }

    private AuthenticationResponse synchronizeUserAndGetToken(final String loginType,
                                                              final String subject, final Map<String, Object> tokeninfoResponse, final Instant expiresAt) {
        Users users = usersRepository.findByEmail(subject);
        if (users == null) {
            log.info("adding new user after successful authentication: {}", subject);
            users = new Users();
            users.setEmail(subject);
            users.setUsername(subject);
            users.setName(tokeninfoResponse.get("name").toString());
            // assign default role
            users.setRole(Set.of(roleRepository.findByName(UserRoles.ADMIN)));
        } else {
            log.info("updating existing user after successful authentication: {}", subject);
        }
        usersRepository.save(users);

        final JwtUserDetails userDetails = jwtSocialUserDetailsService.loadUserByUsername(subject);
        final Duration validity = Duration.between(Instant.now(), expiresAt);
        final AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(jwtTokenService.generateToken(userDetails, loginType, validity));
        return authenticationResponse;
    }

    @PostMapping("/authenticateGoogle")
    public AuthenticationResponse authenticateGoogle(
            @RequestBody @Valid final AuthenticationSocialRequest authenticationSocialRequest) {
        log.info("exchanging google code");
        final String providerId = "google";
        final String clientId = environment.getProperty("app." + providerId + ".client-id");
        final String clientSecret = environment.getProperty("app." + providerId + ".client-secret");
        final RestClient.ResponseSpec accessTokenSpec = googleClient.post()
                .uri("token")
                .body(Map.of("client_id", clientId, "client_secret", clientSecret,
                        "redirect_uri", baseHost + "/completeLogin?provider=google", "grant_type", "authorization_code",
                        "code", authenticationSocialRequest.getCode()))
                .retrieve();
        final Map<String, Object> accessTokenResponse = accessTokenSpec.body(new ParameterizedTypeReference<>() {
        });

        log.info("validating google access token");
        final RestClient.ResponseSpec tokeninfoSpec = googleClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("tokeninfo")
                        .queryParam("id_token", accessTokenResponse.get("id_token"))
                        .build())
                .retrieve();
        final Map<String, Object> tokeninfoResponse = tokeninfoSpec.body(new ParameterizedTypeReference<>() {
        });
        if (!clientId.equals(tokeninfoResponse.get("aud"))) {
            log.warn("google app id not matching");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        final String subject = tokeninfoResponse.get("sub").toString();
        final Instant expiresAt = Instant.ofEpochSecond(Long.parseLong(tokeninfoResponse.get("exp").toString()));
        if (expiresAt.isBefore(Instant.now())) {
            log.warn("google token has expired");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return synchronizeUserAndGetToken(providerId, subject, tokeninfoResponse, expiresAt);
    }

}
