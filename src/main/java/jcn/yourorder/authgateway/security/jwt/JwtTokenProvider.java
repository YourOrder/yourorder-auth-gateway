package jcn.yourorder.authgateway.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jcn.yourorder.authgateway.auth.enitites.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RsaKeyProvider rsaKeyProvider;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(Date.from(Instant.now()))
                .expiration(new Date(Instant.now().toEpochMilli() + 15 * 60 * 1000))
                .signWith(rsaKeyProvider.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(rsaKeyProvider.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        String role = claims.get("role", String.class);
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}