package jcn.yourorder.authgateway.auth.repositories;

import jcn.yourorder.authgateway.auth.enitites.models.RefreshToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, UUID> {
    Mono<RefreshToken> findByToken(String token);
}
