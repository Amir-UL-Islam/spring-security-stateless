package io.security.base.config;

import static org.springframework.security.config.Customizer.withDefaults;

import io.security.base.security.JwtSocialUserDetailsService;
import io.security.base.security.JwtTokenService;
import io.security.base.security.JwtUserDetailsService;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class JwtSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // creates hashes with {bcrypt} prefix
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(final PasswordEncoder passwordEncoder,
            final JwtUserDetailsService jwtUserDetailsService) {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(jwtUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    public JwtRequestFilter jwtRequestFilter(final JwtUserDetailsService jwtUserDetailsService,
            final JwtSocialUserDetailsService jwtSocialUserDetailsService,
            final JwtTokenService jwtTokenService) {
        return new JwtRequestFilter(jwtUserDetailsService, jwtSocialUserDetailsService, jwtTokenService);
    }

    @Bean
    public SecurityFilterChain jwtFilterChain(final HttpSecurity http,
            final JwtUserDetailsService jwtUserDetailsService,
            final JwtSocialUserDetailsService jwtSocialUserDetailsService,
            final JwtTokenService jwtTokenService) {
        return http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter(jwtUserDetailsService, jwtSocialUserDetailsService, jwtTokenService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
