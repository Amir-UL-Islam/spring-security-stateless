package io.security.base.config;

import static org.springframework.security.config.Customizer.withDefaults;

import io.security.base.security.jwt.JwtUserDetailsService;
import io.security.base.security.filters.ACLFilter;
import io.security.base.security.interceptor.CustomAccessDeniedHandler;
import java.security.interfaces.RSAPublicKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class JwtSecurityConfig {

    private final ACLFilter aclPermissionFilter;

    public JwtSecurityConfig(final ACLFilter aclPermissionFilter) {
        this.aclPermissionFilter = aclPermissionFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(final PasswordEncoder passwordEncoder,
                                                         final JwtUserDetailsService jwtUserDetailsService) {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(jwtUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("classpath:certs/public.pem") final RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        final JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();
        delegate.setAuthorityPrefix(""); // keep roles as-is
        delegate.setAuthoritiesClaimName("roles");

        final JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(delegate);
        return converter;
    }

    @Bean
    public SecurityFilterChain jwtFilterChain(final HttpSecurity http,
                                              final JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        return http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/authenticate",
                                "/authenticateGoogle",
                                "/oauth/token",
                                "/register",
                                "/",
                                "/index.html",
                                "/static/**",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(aclPermissionFilter, BearerTokenAuthenticationFilter.class)
                .exceptionHandling(ex -> ex.accessDeniedHandler(new CustomAccessDeniedHandler()))
                .build();
    }
}
