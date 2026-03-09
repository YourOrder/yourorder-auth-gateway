package jcn.yourorder.authgateway.auth.services;

import jcn.yourorder.authgateway.auth.enitites.dtos.response.TokenResponseDTO;
import jcn.yourorder.authgateway.auth.enitites.models.RefreshToken;
import jcn.yourorder.authgateway.auth.enitites.models.User;
import jcn.yourorder.authgateway.auth.repositories.RefreshTokenRepository;
import jcn.yourorder.authgateway.auth.repositories.UserRepository;
import jcn.yourorder.authgateway.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final long REFRESH_VALIDITY_DAYS = 7;

    public Mono<TokenResponseDTO> generateTokens(User user) {

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = UUID.randomUUID().toString();

        RefreshToken entity = RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .token(refreshToken)
                .expiresAt(Instant.now().plus(REFRESH_VALIDITY_DAYS, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(entity)
                .thenReturn(new TokenResponseDTO(accessToken, refreshToken));
    }

    @Transactional
    public Mono<TokenResponseDTO> refreshAccessToken(String refreshTokenValue) {

        return refreshTokenRepository.findByToken(refreshTokenValue)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid refresh token")))

                .filter(token -> !token.isExpired() && !token.isRevoked())
                .switchIfEmpty(Mono.error(new RuntimeException("Refresh token expired or revoked")))

                .flatMap(token -> {
                    token.setRevoked(true);
                    return refreshTokenRepository.save(token);
                })

                .flatMap(oldToken ->
                        userRepository.findById(oldToken.getUserId())
                                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                )

                .flatMap(this::generateTokens);
    }

    @Transactional
    public Mono<Void> logout(String refreshTokenValue) {

        return refreshTokenRepository.findByToken(refreshTokenValue)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid refresh token")))
                .flatMap(token -> {
                    token.setRevoked(true);
                    return refreshTokenRepository.save(token);
                })
                .then();
    }

    @Transactional
    public Mono<Void> logoutAll(UUID userId) {

        return refreshTokenRepository.findAll()
                .filter(token -> token.getUserId().equals(userId))
                .flatMap(token -> {
                    token.setRevoked(true);
                    return refreshTokenRepository.save(token);
                })
                .then();
    }
}
