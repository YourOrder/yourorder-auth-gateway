package jcn.yourorder.authgateway.security.jwt;

import jcn.yourorder.authgateway.auth.enitites.enums.UserRole;
import jcn.yourorder.authgateway.auth.enitites.models.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(new RsaKeyProvider());

    @Test
    void generatedTokenContainsUserClaimsAndRoleAuthority() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("admin")
                .email("admin@mail.local")
                .role(UserRole.ADMIN)
                .build();

        String token = jwtTokenProvider.generateAccessToken(user);
        var claims = jwtTokenProvider.getClaims(token);

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("username", String.class)).isEqualTo("admin");
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(jwtTokenProvider.getAuthorities(claims))
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }
}
