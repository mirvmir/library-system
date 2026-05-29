package io.github.mirvmir.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.github.mirvmir.frameworks.Config;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final KeyLoader keyLoader;
    private final Config config;

    public JwtService(KeyLoader keyLoader, Config config) {
        this.keyLoader = keyLoader;
        this.config = config;
    }

    public String generateToken(Long userId, Collection<? extends GrantedAuthority> authorities) {
        try {
            Instant now = Instant.now();
            Date issuedAt = Date.from(now);
            Date expiresAt = Date.from(now.plus(config.getExpiration()));

            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(String.valueOf(userId))
                    .claim("role", roles)
                    .issueTime(issuedAt)
                    .expirationTime(expiresAt)
                    .build();

            RSAPrivateKey privateKey = keyLoader.loadPrivateKey();
            RSAPublicKey publicKey = keyLoader.loadPublicKey();

            RSAKey rsaJwk = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID("kid-1") // не используется, но пускай будет
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .keyID(rsaJwk.getKeyID())
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claims);

            JWSSigner signer = new RSASSASigner(rsaJwk);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("JWT generation failed", e);
        }
    }
}
