package io.security.base.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class JwtTokenService {

    private static final Duration JWT_TOKEN_VALIDITY = Duration.ofMinutes(60);
    private static final Duration MAX_TOKEN_VALIDITY = Duration.ofMinutes(60);

    private final Algorithm rsa256;
    private final JWTVerifier verifier;

    public JwtTokenService(@Value("classpath:certs/public.pem") final RSAPublicKey publicKey,
            @Value("classpath:certs/private.pem") final RSAPrivateKey privateKey) {
        this.rsa256 = Algorithm.RSA256(publicKey, privateKey);
        this.verifier = JWT.require(this.rsa256).build();
    }

    public String generateToken(final UserDetails userDetails, final String loginType,
            final Duration validity) {
        final Instant now = Instant.now();
        final Duration tokenValidity = validity == null ? JWT_TOKEN_VALIDITY : (validity.compareTo(MAX_TOKEN_VALIDITY) > 0 ? MAX_TOKEN_VALIDITY : validity);
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("login_type", loginType)
                // only for client information
                .withArrayClaim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new))
                .withIssuer("app")
                .withIssuedAt(now)
                .withExpiresAt(now.plus(tokenValidity))
                .sign(this.rsa256);
    }

    public DecodedJWT validateToken(final String token) {
        try {
            return verifier.verify(token);
        } catch (final JWTVerificationException verificationEx) {
            log.warn("token invalid: {}", verificationEx.getMessage());
            return null;
        }
    }

}
